package by.epam.simpleweb.source.pool.impl;

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

import by.epam.simpleweb.source.pool.exception.ConnectionPoolException;
import by.epam.simpleweb.source.pool.ConnectionPool;
import by.epam.simpleweb.source.pool.DBParameter;
import by.epam.simpleweb.source.pool.DBResourceManager;

/**
 * Класс ConnectionPoolImpl представляет собой реализацию пула соединений
 * 
 * @author User
 *
 */
public final class ConnectionPoolImpl implements ConnectionPool {

	/**
	 * Логгер
	 */
	private static final Logger logger = LogManager.getRootLogger();

	/**
	 * Объект, представляющий пул соединений (существует в единственном
	 * экзампляре)
	 */
	private static ConnectionPoolImpl instance;

	/**
	 * Свободные соединения
	 */
	private BlockingQueue<Connection> poolConnections;

	/**
	 * Использующиеся соединения
	 */
	private BlockingQueue<Connection> usedPoolConnections;

	/**
	 * Название драйвера
	 */
	private String driverName;

	/**
	 * URL базы данных
	 */
	private String url;

	/**
	 * Имя пользователя
	 */
	private String user;

	/**
	 * Пароль
	 */
	private String password;

	/**
	 * Размер пула соединений
	 */
	private int poolsize;

	private ConnectionPoolImpl() throws ConnectionPoolException {
		DBResourceManager resourceManager = DBResourceManager.getInstance();
		this.driverName = resourceManager.getValue(DBParameter.DB_DRIVER);
		this.url = resourceManager.getValue(DBParameter.DB_URL);
		this.user = resourceManager.getValue(DBParameter.DB_USER);
		this.password = resourceManager.getValue(DBParameter.DB_PASSWORD);
		try {
			this.poolsize = Integer.parseInt(resourceManager.getValue(DBParameter.DB_POOL_SIZE));
		} catch (NumberFormatException ex) {
			this.poolsize = 5;
		}
		logger.debug("All the necessary properties have been setted to the connection pool");
	}

	/**
	 * Получение пула соединений
	 * 
	 * @return пул соединений
	 */
	public static ConnectionPoolImpl getInstance() throws ConnectionPoolException {
		if (instance == null) {
			instance = new ConnectionPoolImpl();
		}
		return instance;
	}

	@Override
	public void initConnectionPool() throws ConnectionPoolException {
		try {
			Class.forName(driverName);
			logger.debug("The database driver has been found");
			poolConnections = new ArrayBlockingQueue<Connection>(poolsize);
			usedPoolConnections = new ArrayBlockingQueue<Connection>(poolsize);
			for (int i = 0; i < poolsize; i++) {
				Connection connection = DriverManager.getConnection(url, user, password);
				PoolConnection poolConnection = new PoolConnection(connection);
				poolConnections.add(poolConnection);
			}
			logger.debug("The connections have been added to the connection pool");
		} catch (ClassNotFoundException ex) {
			throw new ConnectionPoolException("Unable to find the driver for the database", ex);
		} catch (SQLException ex) {
			throw new ConnectionPoolException("Unable to get connection for the connection pool", ex);
		}
	}

	@Override
	public void destroyConnectionPool() throws ConnectionPoolException {
		try {
			clearPoolConnections();
			logger.debug("The connection pool has been destroyed");
		} catch (SQLException ex) {
			throw new ConnectionPoolException("Unable to destroy pool connection", ex);
		}
	}

	@Override
	public Connection takeConnection() throws ConnectionPoolException {
		Connection connection = null;
		try {
			connection = poolConnections.take();
			usedPoolConnections.add(connection);
			logger.debug("The connection has been taken from the connection pool");
		} catch (InterruptedException ex) {
			throw new ConnectionPoolException("Unable to take connection from the pool", ex);
		}
		return connection;
	}

	@Override
	public void returnConnection(Connection connection) throws ConnectionPoolException {
		try {
			connection.close();
			logger.debug("The connection has been returned to the connection pool");
		} catch (SQLException ex) {
			throw new ConnectionPoolException("Unable to return connection to the connection pool", ex);
		}
	}

	/**
	 * Закрыть все соединения
	 * 
	 * @throws SQLException
	 */
	private void clearPoolConnections() throws SQLException {
		closePoolConnections(poolConnections);
		closePoolConnections(usedPoolConnections);
	}

	/**
	 * Закрыть соединения из очереди
	 * 
	 * @param queue
	 *            очередь соединений
	 * @throws SQLException
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
	 * Класс PoolConnection инкапсулирует объект класса Connection с целью
	 * переопределения метода close(), т.к. соединение из пула должно быть
	 * закрыто только при уничтожении пула, иначе оно просто возвращается в пул
	 * 
	 * @author User
	 *
	 */
	private class PoolConnection implements Connection {
		private Connection connection;

		public PoolConnection(Connection connection) throws SQLException {
			this.connection = connection;
			this.connection.setAutoCommit(true);
		}

		/**
		 * Закрытие соединения из пула
		 * 
		 * @throws SQLException
		 */
		public void closePoolConnection() throws SQLException {
			connection.close();
			logger.debug("The connection from the connection pool has been successfully closed");
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
