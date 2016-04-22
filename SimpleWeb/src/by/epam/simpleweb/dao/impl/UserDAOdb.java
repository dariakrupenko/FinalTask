package by.epam.simpleweb.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import by.epam.simpleweb.dao.UserDAO;
import by.epam.simpleweb.dao.exception.DAOException;
import by.epam.simpleweb.entity.User;
import by.epam.simpleweb.source.pool.ConnectionPool;
import by.epam.simpleweb.source.pool.exception.ConnectionPoolException;
import by.epam.simpleweb.source.pool.impl.ConnectionPoolImpl;

/**
 * Класс UserDAOdb управляет доступом к информацию о пользователях через базу
 * данных
 * 
 * @author User
 *
 */
public class UserDAOdb implements UserDAO {

	/**
	 * Логгер
	 */
	private final static Logger logger = LogManager.getRootLogger();

	/**
	 * Номер колонки таблицы результирующего набора, которая соответствует
	 * логину
	 */
	private static final int LOGIN_COLUMN = 1;

	/**
	 * Номер колонки таблицы результирующего набора, которая соответствует
	 * паролю
	 */
	private static final int PASSWORD_COLUMN = 2;

	/**
	 * Номер колонки таблицы результирующего набора, которая соответствует имени
	 */
	private static final int NAME_COLUMN = 3;

	/**
	 * Запрос к базе данных на языке SQL для вставки в таблицу нового
	 * пользователя
	 */
	private static final String INSERT_QUERY = "INSERT INTO users VALUES (?,?,?)";

	/**
	 * Запрос к базе данных на языке SQL для поиска пользователя с заданным
	 * логином и паролем
	 */
	private static final String FIND_USER_QUERY = "SELECT * FROM users WHERE login = ? AND password = ?";

	@Override
	public void createUser(User user) throws DAOException {
		logger.debug("UserDAO : create user");
		ConnectionPool pool = null;
		Connection connection = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			connection = pool.takeConnection();
			PreparedStatement stat = connection.prepareStatement(INSERT_QUERY);
			stat.setString(LOGIN_COLUMN, user.getLogin());
			stat.setString(PASSWORD_COLUMN, user.getPassword());
			stat.setString(NAME_COLUMN, user.getName());
			stat.executeUpdate();
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("Unable to create user", ex);
		} finally {
			try {
				if (connection != null && !connection.isClosed()) {
					pool.returnConnection(connection);
				}
			} catch (ConnectionPoolException | SQLException ex) {
				throw new DAOException("Unable to close connection", ex);
			}
		}
	}

	@Override
	public User findUser(String login, String password) throws DAOException {
		logger.debug("UserDAO : find user");
		User user = null;
		ConnectionPool pool = null;
		Connection connection = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			connection = pool.takeConnection();
			PreparedStatement stat = connection.prepareStatement(FIND_USER_QUERY);
			stat.setString(LOGIN_COLUMN, login);
			stat.setString(PASSWORD_COLUMN, password);
			ResultSet rs = stat.executeQuery();
			if (rs.next()) {
				user = new User();
				user.setLogin(rs.getString(LOGIN_COLUMN));
				user.setPassword(rs.getString(PASSWORD_COLUMN));
				user.setName(rs.getString(NAME_COLUMN));
			}
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("Unable to find user", ex);
		} finally {
			try {
				if (connection != null && !connection.isClosed()) {
					pool.returnConnection(connection);
				}
			} catch (ConnectionPoolException | SQLException ex) {
				throw new DAOException("Unable to close connection", ex);
			}
		}
		return user;
	}

}
