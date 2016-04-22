package by.epam.simpleweb.command.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import by.epam.simpleweb.command.Command;
import by.epam.simpleweb.command.exception.CommandException;
import by.epam.simpleweb.controller.PageName;
import by.epam.simpleweb.service.UserService;
import by.epam.simpleweb.service.ValidateService;
import by.epam.simpleweb.service.exception.ServiceException;

/**
 * Класс RegistrationCommand представляет собой команду, которая выполняется при
 * попытке пользователя зарегистрироваться в системе
 * 
 * @author User
 *
 */
public class RegistrationCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger logger = LogManager.getRootLogger();

	// имена параметров запроса (логин, пароль, имя)
	private static final String LOGIN_PARAMETER = "login";
	private static final String PASSWORD_PARAMETER = "password";
	private static final String NAME_PARAMETER = "name";

	private static final String LOGIN_ERROR_ATTR = "loginError";
	private static final String LOGIN_ATTR = "login";
	private static final String PASSWORD_ERROR_ATTR = "passwordError";
	private static final String PASSWORD_ATTR = "password";
	private static final String NAME_ERROR_ATTR = "nameError";
	private static final String NAME_ATTR = "name";

	@Override
	public String execute(HttpServletRequest request) throws CommandException {
		logger.debug("Command : registration");
		String pagePath = null;
		try {
			boolean error = validateData(request);
			pagePath = null;
			if (error) {
				pagePath = PageName.REGISTRATION_PAGE;
			} else {
				pagePath = PageName.INDEX_PAGE;
			}
		} catch (ServiceException ex) {
			throw new CommandException("The problem occured while executing registration command", ex);
		}

		return pagePath;
	}

	/**
	 * Валидация данных и регистрация пользователя в системе (в случае удачной
	 * валидации)
	 * 
	 * @param request
	 *            контекст запроса
	 * @return true - валидация окончилась неудачей, иначе false
	 * @throws ServiceException
	 *             - ошибка при регистрации пользователя в системе
	 */
	private boolean validateData(HttpServletRequest request) throws ServiceException {
		boolean error = false;
		ValidateService validator = ValidateService.getInstance();
		String login = request.getParameter(LOGIN_PARAMETER);
		String password = request.getParameter(PASSWORD_PARAMETER);
		String name = request.getParameter(NAME_PARAMETER);
		boolean loginError = validator.validateLogin(login);
		request.setAttribute(LOGIN_ERROR_ATTR, loginError);
		request.setAttribute(LOGIN_ATTR, login);
		boolean passwordError = validator.validatePassword(password);
		request.setAttribute(PASSWORD_ERROR_ATTR, passwordError);
		request.setAttribute(PASSWORD_ATTR, password);
		boolean nameError = validator.validateName(name);
		request.setAttribute(NAME_ERROR_ATTR, nameError);
		request.setAttribute(NAME_ATTR, name);
		if (loginError || passwordError || nameError) {
			error = true;
		} else {
			UserService service = UserService.getInstance();
			service.registrate(login, password, name);
		}
		return error;
	}

}
