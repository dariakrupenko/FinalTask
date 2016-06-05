package main.by.epam.admissionweb.dao.pool.impl;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.dao.pool.ConnectionPool;
import main.by.epam.admissionweb.dao.pool.DBParameter;
import main.by.epam.admissionweb.dao.pool.DBResourceManager;
import main.by.epam.admissionweb.dao.pool.exception.ConnectionPoolException;

/**
 * Класс <code>ConnectionPoolImpl</code> реализует интерфейс
 * <code>ConnectionPool</code>, таким образом является конкретной реализацией
 * пула соединений с базой данных, позволяющего получать соединения для работы с
 * базой данных.
 * <p>
 * При инициализации пула свойства соединения с базой данных считываются из
 * файла свойств посредством объекта {@link DBResourceManager}.
 * <p>
 * Данная реализация пула соединений содержит две очереди: свободные и
 * использующиеся соединения. При попытке взять соединение, оно перемещается из
 * очереди свободных соединений в очередь использующихся.
 * <p>
 * Данный пул соединений использует вспомогательный класс {@link PoolConnection}
 * , который служит оберткой для <code>Connection</code>.
 * 
 * @author Daria Krupenko
 * @see DBResourceManager
 * @see Connection
 * @see PoolConnection
 *
 */
public final class ConnectionPoolImpl implements ConnectionPool {

	/**
	 * Логгер
	 */
	
	private static final Logger LOGGER = LogManager.getRootLogger();
	/**
	 * Имя свойства соединения с базой данных, ассоциированное с пользователем
	 * базы данных
	 */
	private static final String USER_PROP = "user";

	/**
	 * Имя свойства соединения с базой данных, ассоциированное с паролем для
	 * доступа к базе данных
	 */
	private static final String PASSWORD_PROP = "password";

	/**
	 * Имя свойства соединения с базой данных, ассоциированное с использованием
	 * кодировки UNICODE
	 */
	private static final String USE_UNICODE_PROP = "useUnicode";

	/**
	 * Значение свойства соединения с базой данных, ассоциированное с
	 * использованием кодировки UNICODE
	 */
	private static final String USE_UNICODE_VALUE = "true";

	/**
	 * Имя свойства соединения с базой данных, ассоциированное с кодировкой
	 * драйвера базы данных
	 */
	private static final String CHARACTER_ENCODING_PROP = "characterEncoding";

	/**
	 * Количество соединений в пуле по умолчанию
	 */
	private static final int DEFAULT_POOL_SIZE = 5;

	/**
	 * Объект пула соединений
	 */
	private static ConnectionPoolImpl INSTANCE;

	/**
	 * Хранилище соединений пула соединений. Представляет собой очередь.
	 */
	private BlockingQueue<Connection> poolConnections;

	/**
	 * Хранилище соединений пула соединений, находящихся в использовании.
	 * Представляет собой очередь.
	 */
	private BlockingQueue<Connection> usedPoolConnections;

	/**
	 * Значение свойства соединения с базой данных, ассоциированное с драйвером
	 * базы данных
	 */
	private String driverName;

	/**
	 * Значение свойства соединения с базой данных, ассоциированное с URL
	 * (расположение) базы данных
	 */
	private String url;

	/**
	 * Значение свойства соединения с базой данных, ассоциированное с
	 * пользователем базы данных
	 */
	private String user;

	/**
	 * Значение свойства соединения с базой данных, ассоциированное с паролем
	 * для доступа к базе данных
	 */
	private String password;

	/**
	 * Значение свойства, ассоциированное с количеством соединений в пуле
	 */
	private int poolsize;

	/**
	 * Значение свойства соединения с базой данных, ассоциированное с кодировкой
	 * драйвера базы данных
	 */
	private String encoding;

	/**
	 * Конструирует пул соединений, считывая свойства соединения из файла
	 * свойств, используя объект {@link DBResourceManager}.
	 * <p>
	 * Если произошла ошибка при считывании количества соединений, количество
	 * соединений приравнивается к значению по умолчанию (5).
	 */
	private ConnectionPoolImpl() {
		DBResourceManager resourceManager = DBResourceManager.getInstance();
		this.driverName = resourceManager.getValue(DBParameter.DB_DRIVER);
		this.url = resourceManager.getValue(DBParameter.DB_URL);
		this.user = resourceManager.getValue(DBParameter.DB_USER);
		this.password = resourceManager.getValue(DBParameter.DB_PASSWORD);
		this.encoding = resourceManager.getValue(DBParameter.DB_ENCODING);
		try {
			this.poolsize = Integer.parseInt(resourceManager.getValue(DBParameter.DB_POOL_SIZE));
		} catch (NumberFormatException ex) {
			this.poolsize = DEFAULT_POOL_SIZE;
		}
	}

	/**
	 * Получение пула соединений <code>ConnectionPoolImpl</code>
	 * 
	 * @return пул соединений
	 */
	public static ConnectionPoolImpl getInstance() throws ConnectionPoolException {
		if (INSTANCE == null) {
			INSTANCE = new ConnectionPoolImpl();
		}
		return INSTANCE;
	}

	/**
	 * Инициализация пула соединений.
	 * <p>
	 * В обязанность данного метода входит создание объектов
	 * <code>Connection</code>. В данном методе инициализируются хранилища
	 * соединений.
	 * 
	 * @throws ConnectionPoolException
	 *             если не удается найти класс драйвера базы данных; если не
	 *             удается создать соединение
	 */
	@Override
	public void initConnectionPool() throws ConnectionPoolException {
		try {
			Class.forName(driverName);
			poolConnections = new ArrayBlockingQueue<Connection>(poolsize);
			usedPoolConnections = new ArrayBlockingQueue<Connection>(poolsize);
			Properties properties = new Properties();
			properties.setProperty(USER_PROP, user);
			properties.setProperty(PASSWORD_PROP, password);
			properties.setProperty(USE_UNICODE_PROP, USE_UNICODE_VALUE);
			properties.setProperty(CHARACTER_ENCODING_PROP, encoding);
			for (int i = 0; i < poolsize; i++) {
				Connection connection = DriverManager.getConnection(url, properties);
				PoolConnection poolConnection = new PoolConnection(connection);
				poolConnections.add(poolConnection);
			}
		} catch (ClassNotFoundException ex) {
			throw new ConnectionPoolException("Unable to find the driver for the database", ex);
		} catch (SQLException ex) {
			throw new ConnectionPoolException("Unable to get connection for the connection pool", ex);
		}
	}

	/**
	 * Уничтожение пула соединений.
	 * <p>
	 * В результате работы данного метода все соединения, инициализированные в
	 * пуле, закрываются.
	 * 
	 * @throws ConnectionPoolException
	 *             если при закрытии соединений произошла ошибка
	 */
	@Override
	public void destroyConnectionPool() throws ConnectionPoolException {
		try {
			clearPoolConnections();
		} catch (SQLException ex) {
			throw new ConnectionPoolException("Unable to destroy pool connection", ex);
		}
	}

	/**
	 * Получение соединения из пула.
	 * <p>
	 * Соединение перемещается из очереди свободных соединений пула в очередь
	 * соединений, находящихся в использовании.
	 * 
	 * @return объект <code>Connection</code> - соединение с базой данных
	 * @throws ConnectionPoolException
	 *             если при получении соединения из очереди свободных соединений
	 *             возникла ошибка
	 */
	@Override
	public Connection takeConnection() throws ConnectionPoolException {
		Connection connection = null;
		try {
			connection = poolConnections.take();
			usedPoolConnections.add(connection);
			LOGGER.debug("CONNECTION POOL : take connection (pool size = {})", poolConnections.size());
		} catch (InterruptedException ex) {
			throw new ConnectionPoolException("Unable to take connection from the pool", ex);
		}
		return connection;
	}

	/**
	 * Возвращение соединения, указанного в параметре <code>connection</code>
	 * обратно в пул соединений.
	 * <p>
	 * Так как в качестве соединения используется класс
	 * <code>PoolConnection</code>, который переопределяет метод
	 * <code>close()</code>, закрытие соединения <code>connection</code>
	 * подразумевает перемещение данного соединения из очереди использующихся в
	 * очередь свободных соединений.
	 * 
	 * @param connection
	 *            соединение с базой данных, которое должно быть возвращено в
	 *            пул
	 * @throws ConnectionPoolException
	 *             если при возвращении соединения в пул возникла ошибка
	 */
	@Override
	public void returnConnection(Connection connection) throws ConnectionPoolException {
		try {
			connection.close();
			LOGGER.debug("CONNECTION POOL : return connection (pool size = {})", poolConnections.size());
		} catch (SQLException ex) {
			throw new ConnectionPoolException("Unable to return connection to the connection pool", ex);
		}
	}

	/**
	 * Закрытие соединений из обеих очередей: свободных и используюущихся
	 * соединений
	 * 
	 * @throws SQLException
	 *             если возникла ошибка при закрытии соединений
	 */
	private void clearPoolConnections() throws SQLException {
		closePoolConnections(poolConnections);
		closePoolConnections(usedPoolConnections);
	}

	/**
	 * Закрытие всех соединений из указанной очереди соединений.
	 * <p>
	 * Прежде чем закрыть соединение, все изменения в базе данных, внесеннные
	 * посредством данного соединения, завершаются (commit).
	 * 
	 * @param queue
	 *            очередь соединений
	 * @throws SQLException
	 *             если при закрытии соединения произошла ошибка
	 */
	private void closePoolConnections(BlockingQueue<Connection> queue) throws SQLException {
		Connection connection;
		while ((connection = queue.poll()) != null) {
			if (!connection.getAutoCommit()) {
				connection.commit();
			}
			if (connection instanceof PoolConnection) {
				((PoolConnection) connection).closePoolConnection();
			}
		}
	}

	/**
	 * Внутренний класс <code>PoolConnection</code> реализует интерфейс
	 * <code>Connection</code> и является классом-оберткой для соединений
	 * <code>Connection</code>.
	 * <p>
	 * Основное назначение данного класса заключается в предотвращении
	 * физического закрытия соединения со стороны приложения, поэтому данный
	 * класс переопределяет метод <code>close()</code> и добавляет новый метод -
	 * <code>closePoolConnection()</code>, который физически закрывает
	 * соединение с базой данных, но может быть вызван только пулом соединений.
	 * <p>
	 * 
	 * @author Daria Krupenko
	 *
	 */
	private class PoolConnection implements Connection {

		/**
		 * Соединение с базой данных
		 */
		private Connection connection;

		/**
		 * Конструирует <code>PoolConnection</code> со значением autoCommit
		 * равным true.
		 * 
		 * @param connection
		 * @throws SQLException
		 */
		public PoolConnection(Connection connection) throws SQLException {
			this.connection = connection;
			this.connection.setAutoCommit(true);
		}

		/**
		 * Физическое закрытие соединения
		 * 
		 * @throws SQLException
		 *             если произошла ошибка при закрытии соединения
		 */
		public void closePoolConnection() throws SQLException {
			connection.close();
		}

		@Override
		public <T> T unwrap(Class<T> iface) throws SQLException {
			return connection.unwrap(iface);
		}

		@Override
		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return connection.isWrapperFor(iface);
		}

		@Override
		public Statement createStatement() throws SQLException {
			return connection.createStatement();
		}

		@Override
		public PreparedStatement prepareStatement(String sql) throws SQLException {
			return connection.prepareStatement(sql);
		}

		@Override
		public CallableStatement prepareCall(String sql) throws SQLException {
			return connection.prepareCall(sql);
		}

		@Override
		public String nativeSQL(String sql) throws SQLException {
			return connection.nativeSQL(sql);
		}

		@Override
		public void setAutoCommit(boolean autoCommit) throws SQLException {
			connection.setAutoCommit(autoCommit);
		}

		@Override
		public boolean getAutoCommit() throws SQLException {
			return connection.getAutoCommit();
		}

		@Override
		public void commit() throws SQLException {
			connection.commit();

		}

		@Override
		public void rollback() throws SQLException {
			connection.rollback();
		}

		/**
		 * Предотвращение физического закрытия соединения; перемещение
		 * соединения из очереди использующихся соединений в очередь свободных
		 * соединений.
		 * 
		 * @throws SQLException
		 *             если произошла ошибка при перемещении соединения из
		 *             очереди использующихся соединений в очередь свободных
		 *             соединений
		 */
		@Override
		public void close() throws SQLException {
			if (connection.isClosed()) {
				throw new SQLException("The connection is already closed");
			}
			if (connection.isReadOnly()) {
				connection.setReadOnly(false);
			}
			if (!usedPoolConnections.remove(this)) {
				throw new SQLException("Unable to remove connection from the used connections in the connection pool");
			}
			if (!poolConnections.offer(this)) {
				throw new SQLException("Unable to return connection to the connection pool");
			}

		}

		@Override
		public boolean isClosed() throws SQLException {
			return connection.isClosed();
		}

		@Override
		public DatabaseMetaData getMetaData() throws SQLException {
			return connection.getMetaData();
		}

		@Override
		public void setReadOnly(boolean readOnly) throws SQLException {
			connection.setReadOnly(readOnly);
		}

		@Override
		public boolean isReadOnly() throws SQLException {
			return connection.isReadOnly();
		}

		@Override
		public void setCatalog(String catalog) throws SQLException {
			connection.setCatalog(catalog);
		}

		@Override
		public String getCatalog() throws SQLException {
			return connection.getCatalog();
		}

		@Override
		public void setTransactionIsolation(int level) throws SQLException {
			connection.setTransactionIsolation(level);
		}

		@Override
		public int getTransactionIsolation() throws SQLException {
			return connection.getTransactionIsolation();
		}

		@Override
		public SQLWarning getWarnings() throws SQLException {
			return connection.getWarnings();
		}

		@Override
		public void clearWarnings() throws SQLException {
			connection.clearWarnings();
		}

		@Override
		public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
			return connection.createStatement(resultSetType, resultSetConcurrency);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
				throws SQLException {
			return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
		}

		@Override
		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
				throws SQLException {
			return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
		}

		@Override
		public Map<String, Class<?>> getTypeMap() throws SQLException {
			return connection.getTypeMap();
		}

		@Override
		public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
			connection.setTypeMap(map);
		}

		@Override
		public void setHoldability(int holdability) throws SQLException {
			connection.setHoldability(holdability);
		}

		@Override
		public int getHoldability() throws SQLException {
			return connection.getHoldability();
		}

		@Override
		public Savepoint setSavepoint() throws SQLException {
			return connection.setSavepoint();
		}

		@Override
		public Savepoint setSavepoint(String name) throws SQLException {
			return connection.setSavepoint(name);
		}

		@Override
		public void rollback(Savepoint savepoint) throws SQLException {
			connection.rollback(savepoint);
		}

		@Override
		public void releaseSavepoint(Savepoint savepoint) throws SQLException {
			connection.releaseSavepoint(savepoint);
		}

		@Override
		public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
				throws SQLException {
			return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
				int resultSetHoldability) throws SQLException {
			return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		}

		@Override
		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
				int resultSetHoldability) throws SQLException {
			return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
			return connection.prepareStatement(sql, autoGeneratedKeys);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
			return connection.prepareStatement(sql, columnIndexes);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
			return connection.prepareStatement(sql, columnNames);
		}

		@Override
		public Clob createClob() throws SQLException {
			return connection.createClob();
		}

		@Override
		public Blob createBlob() throws SQLException {
			return connection.createBlob();
		}

		@Override
		public NClob createNClob() throws SQLException {
			return connection.createNClob();
		}

		@Override
		public SQLXML createSQLXML() throws SQLException {
			return connection.createSQLXML();
		}

		@Override
		public boolean isValid(int timeout) throws SQLException {
			return connection.isValid(timeout);
		}

		@Override
		public void setClientInfo(String name, String value) throws SQLClientInfoException {
			connection.setClientInfo(name, value);
		}

		@Override
		public void setClientInfo(Properties properties) throws SQLClientInfoException {
			connection.setClientInfo(properties);
		}

		@Override
		public String getClientInfo(String name) throws SQLException {
			return connection.getClientInfo(name);
		}

		@Override
		public Properties getClientInfo() throws SQLException {
			return connection.getClientInfo();
		}

		@Override
		public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
			return connection.createArrayOf(typeName, elements);
		}

		@Override
		public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
			return connection.createStruct(typeName, attributes);
		}

		@Override
		public void setSchema(String schema) throws SQLException {
			connection.setSchema(schema);
		}

		@Override
		public String getSchema() throws SQLException {
			return connection.getSchema();
		}

		@Override
		public void abort(Executor executor) throws SQLException {
			connection.abort(executor);
		}

		@Override
		public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
			connection.setNetworkTimeout(executor, milliseconds);
		}

		@Override
		public int getNetworkTimeout() throws SQLException {
			return connection.getNetworkTimeout();
		}
	}

}
