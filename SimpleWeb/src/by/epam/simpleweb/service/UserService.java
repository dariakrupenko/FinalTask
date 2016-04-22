package by.epam.simpleweb.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import by.epam.simpleweb.dao.DAOFactory;
import by.epam.simpleweb.dao.UserDAO;
import by.epam.simpleweb.dao.exception.DAOException;
import by.epam.simpleweb.entity.User;
import by.epam.simpleweb.service.exception.ServiceException;

/**
 * Класс UserService представляет собой службу, которая выполняет все операции,
 * связанные с пользователями
 * 
 * @author User
 *
 */
public class UserService {

	/**
	 * Логгер
	 */
	private static final Logger logger = LogManager.getRootLogger();

	/**
	 * Объект службы пользователей
	 */
	private static final UserService instance = new UserService();

	private UserService() {
	}

	/**
	 * Получение службы пользователей
	 * 
	 * @return служба пользователей
	 */
	public static UserService getInstance() {
		return instance;
	}

	/**
	 * Регистрация пользователя в системе
	 * 
	 * @param login
	 *            логин
	 * @param password
	 *            пароль
	 * @param name
	 *            имя
	 * @throws ServiceException
	 *             - ошибка при регистрации пользователя
	 */
	public void registrate(String login, String password, String name) throws ServiceException {
		logger.debug("UserService : registrate");
		try {
			User user = new User(login, password, name);
			DAOFactory factory = DAOFactory.getInstance();
			UserDAO userDAO = factory.getUserDAO();
			userDAO.createUser(user);
		} catch (DAOException ex) {
			throw new ServiceException("Unable to registrate user", ex);
		}
	}

	/**
	 * Вход пользователя в систему
	 * 
	 * @param login
	 *            логин
	 * @param password
	 *            пароль
	 * @return объект класса User при удачной авторизации, иначе null
	 * @throws ServiceException
	 *             - ошибка при авторизации пользователя
	 */
	public User login(String login, String password) throws ServiceException {
		logger.debug("UserService : login");
		try {
			DAOFactory factory = DAOFactory.getInstance();
			UserDAO userDAO = factory.getUserDAO();
			User user = userDAO.findUser(login, password);
			return user;
		} catch (DAOException ex) {
			throw new ServiceException("Unable to login user", ex);
		}
	}

}
