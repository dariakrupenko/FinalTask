package by.epam.simpleweb.command.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import by.epam.simpleweb.command.Command;
import by.epam.simpleweb.command.exception.CommandException;
import by.epam.simpleweb.controller.PageName;
import by.epam.simpleweb.entity.User;
import by.epam.simpleweb.service.UserService;
import by.epam.simpleweb.service.exception.ServiceException;

/**
 * Класс LoginCommand представляет собой команду, которая выполняется при
 * попытке пользователя войти в систему
 * 
 * @author User
 *
 */
public class LoginCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger logger = LogManager.getRootLogger();

	/**
	 * Имя параметра запроса, значением которого является логин пользователя
	 */
	private static final String LOGIN_PARAMETER = "login";

	/**
	 * Имя параметра запроса, значением которого является пароль пользователя
	 */
	private static final String PASSWORD_PARAMETER = "password";

	/**
	 * Имя атрибута, значение которого true - если авторизация закончилась
	 * неудачей, false - в противном случае
	 */
	private static final String LOGIN_FAILED_ATTR = "loginFailed";

	/**
	 * Имя атрибута, значением которого является объект класса User - если
	 * авторизация прошла успешно, null - в противном случае
	 */
	private static final String USER_ATTR = "user";

	@Override
	public String execute(HttpServletRequest request) throws CommandException {
		logger.debug("Command : login");
		String pagePath = null;
		try {
			pagePath = null;
			String login = request.getParameter(LOGIN_PARAMETER);
			String password = request.getParameter(PASSWORD_PARAMETER);
			UserService service = UserService.getInstance();
			User user = service.login(login, password);
			if (user == null) {
				request.setAttribute(LOGIN_FAILED_ATTR, true);
				pagePath = PageName.LOGIN_PAGE;
			} else {
				/*объект класса User помещается в контекст запроса, т.к. в данном приложении
				 достаточно просто вывести страницу пользователя (в финальном проекте пользователя
				 нужно помещать в контекст сессии)*/
				request.setAttribute(USER_ATTR, user);
				pagePath = PageName.USER_PAGE;
			}
		} catch (ServiceException ex) {
			throw new CommandException("The problem occured while executing login command", ex);
		}
		return pagePath;
	}

}
