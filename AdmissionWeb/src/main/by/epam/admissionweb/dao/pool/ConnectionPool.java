package main.by.epam.admissionweb.dao.pool;

import java.sql.Connection;

import main.by.epam.admissionweb.dao.pool.exception.ConnectionPoolException;

/**
 * Интерфейс <code>ConnectionPool</code> предоставляет методы для работы с пулом
 * соединений с базой данных.
 * <p>
 * Пул соединений представляет собой совокупность объектов
 * <code>Connection</code>. Вместо того чтобы создавать новое соединение с базой
 * данных, что очень затратно и негативно влияет на производительность,
 * приложение берет соединение из пула, а после работы с ним возвращает обратно
 * в пул.
 * <p>
 * Таким образом, создание объектов <code>Connection</code> и их закрытие
 * полностью возлагается на пул соединений.
 * 
 * @author Daria Krupenko
 * @see Connection
 */
public interface ConnectionPool {

	/**
	 * Инициализация пула соединений.
	 * <p>
	 * В обязанность данного метода входит создание объектов
	 * <code>Connection</code>.
	 * 
	 * @throws ConnectionPoolException
	 *             если при инициализации пула соединений произошла ошибка
	 */
	public void initConnectionPool() throws ConnectionPoolException;

	/**
	 * Уничтожение пула соединений.
	 * <p>
	 * В результате работы данного метода все соединения, инициализированные в
	 * пуле, должны быть закрыты.
	 * 
	 * @throws ConnectionPoolException
	 *             если при уничтожении пула соединений возникла ошибка
	 */
	public void destroyConnectionPool() throws ConnectionPoolException;

	/**
	 * Получение соединения из пула.
	 * 
	 * @return объект <code>Connection</code> - соединение с базой данных
	 * @throws ConnectionPoolException
	 *             если при получении соединения из пула возникла ошибка
	 */
	public Connection takeConnection() throws ConnectionPoolException;

	/**
	 * Возвращение соединения, указанного в параметре <code>connection</code>
	 * обратно в пул соединений.
	 * <p>
	 * При возвращении соединения оно не должно быть закрыто, а лишь
	 * подготовлено для последующего использования. Таким образом, приложение не
	 * может напрямую закрыть соединение.
	 * 
	 * @param connection
	 *            соединение с базой данных, которое должно быть возвращено в
	 *            пул
	 * @throws ConnectionPoolException
	 *             если при возвращение соединения в пул возникла ошибка
	 */
	public void returnConnection(Connection connection) throws ConnectionPoolException;

}
