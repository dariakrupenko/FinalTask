package main.by.epam.admissionweb.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.dao.pool.ConnectionPool;
import main.by.epam.admissionweb.dao.pool.exception.ConnectionPoolException;
import main.by.epam.admissionweb.dao.pool.impl.ConnectionPoolImpl;
import main.by.epam.admissionweb.generator.KeyGenerator;
import main.by.epam.admissionweb.generator.KeyGeneratorFactory;

/**
 * Класс <code>ProjectServletContextListener</code> реализует интерфейс
 * <code>ServletContextListener</code> и является слушателем инициализации и
 * уничтожения контекста сервлета.
 * <p>
 * Главное назначение класса <code>ProjectServletContextListener</code>
 * заключается в управление инициализацией и разрушением пула соединений
 * {@link ConnectionPool}.
 * <p>
 * При инициализации контекста сервлета также инициализируется пул соединений с
 * базой данных. Если в процессе инициализации произошла ошибка, слушатель
 * устанавливает в контекс приложения в качестве атрибута флаг ошибки
 * подключения к базе данных.
 * <p>
 * Также при инициализации контекста сервлета в контекст приложения в качестве
 * атрибута устанавливается объект {@link KeyGenerator}, представляющий
 * собой генератор уникальных ключей.
 * <p>
 * При уничтожении контекста сервлета также уничтожается пул соединений.
 * 
 * 
 * @author Daria Krupenko
 * @see ServletContextListener
 * @see ConnectionPool
 * @see KeyGenerator
 *
 */
public class ProjectServletContextListener implements ServletContextListener {

	private static final Logger LOGGER = LogManager.getRootLogger();

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			ConnectionPool pool = ConnectionPoolImpl.getInstance();
			pool.destroyConnectionPool();
			LOGGER.debug("ServletContextListener : Connection Pool has been destroyed");
		} catch (ConnectionPoolException ex) {
			LOGGER.error(ex);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			ConnectionPool pool = ConnectionPoolImpl.getInstance();
			pool.initConnectionPool();
			arg0.getServletContext().setAttribute("databaseError", false);
			LOGGER.debug("ServletContextListener : Connection Pool has been initialized");
			LOGGER.info("LISTENER : The connection to the database has been successfully established");
			KeyGenerator generator = KeyGeneratorFactory.getInstance().getKeyGenerator();
			arg0.getServletContext().setAttribute("generator", generator);
		} catch (ConnectionPoolException ex) {
			LOGGER.error(ex);
			arg0.getServletContext().setAttribute("databaseError", true);
		}
	}

}
