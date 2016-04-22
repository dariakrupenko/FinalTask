package by.epam.simpleweb.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import by.epam.simpleweb.source.pool.ConnectionPool;
import by.epam.simpleweb.source.pool.exception.ConnectionPoolException;
import by.epam.simpleweb.source.pool.impl.ConnectionPoolImpl;

public class ProjectServletContextListener implements ServletContextListener {

	private static final Logger logger = LogManager.getRootLogger();

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			ConnectionPool pool = ConnectionPoolImpl.getInstance();
			pool.destroyConnectionPool();
			logger.debug("ServletContextListener : Connection Pool has been destroyed");
		} catch (ConnectionPoolException ex) {
			logger.error(ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			ConnectionPool pool = ConnectionPoolImpl.getInstance();
			pool.initConnectionPool();
			logger.debug("ServletContextListener : Connection Pool has been initialized");
		} catch (ConnectionPoolException ex) {
			logger.error(ex);
			throw new RuntimeException(ex);
		}
	}

}
