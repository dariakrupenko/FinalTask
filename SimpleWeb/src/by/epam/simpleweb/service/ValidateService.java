package by.epam.simpleweb.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Класс ValidateService представляет собой службу по валидации данных
 * пользователя
 * 
 * @author User
 *
 */
public class ValidateService {

	/**
	 * Логгер
	 */
	private static final Logger logger = LogManager.getRootLogger();

	private static final String WHITESPACE = " ";

	/**
	 * Максимальная длина логина
	 */
	private static final int LOGIN_MAX_LENGTH = 20;

	/**
	 * Максимальная длина пароля
	 */
	private static final int PASSWORD_MAX_LENGTH = 20;

	/**
	 * Максимальная длина имени
	 */
	private static final int NAME_MAX_LENGTH = 20;

	/**
	 * Объект службы валидации данных
	 */
	private static final ValidateService instance = new ValidateService();

	private ValidateService() {
	}

	/**
	 * Получение службы валидации данных
	 * 
	 * @return служба валидации данных
	 */
	public static ValidateService getInstance() {
		return instance;
	}

	/**
	 * Валидация логина
	 * 
	 * @param login
	 *            логин
	 * @return true - неудачная валидация, иначе false
	 */
	public boolean validateLogin(String login) {
		logger.debug("ValidateService : validate login");
		if (login == null || login.isEmpty() || login.contains(WHITESPACE) || login.length() > LOGIN_MAX_LENGTH) {
			return true;
		} else
			return false;
	}

	/**
	 * Валидация пароля
	 * 
	 * @param password
	 *            пароль
	 * @return true - неудачная валидация, иначе false
	 */
	public boolean validatePassword(String password) {
		logger.debug("ValidateService : validate password");
		if (password == null || password.isEmpty() || password.contains(WHITESPACE)
				|| password.length() > PASSWORD_MAX_LENGTH) {
			return true;
		} else
			return false;
	}

	/**
	 * Валидация имени
	 * 
	 * @param name
	 *            имя
	 * @return true - неудачная валидация, иначе false
	 */
	public boolean validateName(String name) {
		logger.debug("ValidateService : validate name");
		if (name == null || name.isEmpty() || name.length() > NAME_MAX_LENGTH) {
			return true;
		} else
			return false;
	}

}
