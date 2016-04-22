package by.epam.simpleweb.source.pool;

import java.sql.Connection;

import by.epam.simpleweb.source.pool.exception.ConnectionPoolException;

/**
 * Интерфейс ConnectionPool описывает пул соединений с базой данных
 * 
 * @author User
 *
 */
public interface ConnectionPool {

	/**
	 * Инициализация пула соединений
	 * 
	 * @throws ConnectionPoolException
	 */
	public void initConnectionPool() throws ConnectionPoolException;

	/**
	 * Уничтожение пула соединений
	 * 
	 * @throws ConnectionPoolException
	 */
	public void destroyConnectionPool() throws ConnectionPoolException;

	/**
	 * Получение соединения с базой данных из пула
	 * 
	 * @return соединение с базой данных
	 * @throws ConnectionPoolException
	 */
	public Connection takeConnection() throws ConnectionPoolException;

	/**
	 * Возврат соединения с базой данных обратно в пул
	 * 
	 * @param connection
	 *            соединение с базой данных
	 * @throws ConnectionPoolException
	 */
	public void returnConnection(Connection connection) throws ConnectionPoolException;

}
