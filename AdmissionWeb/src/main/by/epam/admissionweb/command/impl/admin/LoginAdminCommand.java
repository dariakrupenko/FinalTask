package main.by.epam.admissionweb.command.impl.admin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;

/**
 * Класс <code>LoginAdminCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * авторизацию администратора системы.
 * 
 * @author Daria Krupenko
 *
 */
public class LoginAdminCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Имя файла свойств с указанием логина и пароля администратора
	 */
	private static final String PROPERTIES_FILE = "admin.properties";

	/**
	 * Имя свойства, значением которого является логин администратора
	 */
	private static final String LOGIN_PROPERTY = "admin.login";

	/**
	 * Имя свойства, значением которого является пароль администратора
	 */
	private static final String PASSWORD_PROPERTY = "admin.password";

	/**
	 * Логин администратора
	 */
	private String adminLogin;

	/**
	 * Пароль администратора
	 */
	private String adminPassword;

	/**
	 * Метод описывает поведение команды по авторизации администратора системы.
	 * <p>
	 * Логин и пароль администратора системы хранятся в файле свойств.
	 * <p>
	 * В случае успешной авторизации запрос перенаправляется на страницу консоли
	 * администратора, иначе - на страницу авторизации администратора (в
	 * контекст запроса устанавливается атрибут, сигнализирующий о неудавшейся
	 * попытке авторизации). Если авторизация завершилась успешно, предыдущая сессия прерывается
	 * и открывается новая сессия, связанная с администратором системы.
	 * <p>
	 * Если в процессе обработки возникла ошибка, обусловленная чтением файла
	 * свойств, в контекст запроса устанавливается флаг ошибки.
	 * 
	 * @param request
	 *            контекст запроса (используется для получение доступа к
	 *            парамаетрам запроса и атрибутам запроса/сессии/приложения)
	 * @param response
	 *            контекст ответа на HTTP-запрос
	 * @throws CommandException
	 *             если при перенаправлении запроса произошла ошибка
	 * @see CommandHelper
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : LoginAdminCommand");
		CommandHelper helper = CommandHelper.getInstance();
		String path = null;
		try {
			initAdmin();
			String login = request.getParameter(CommandHelper.ParameterName.LOGIN);
			String password = request.getParameter(CommandHelper.ParameterName.PASSWORD);
			LOGGER.debug("COMMAND : LoginAdminCommand (login = {}, pw = {})", login, password);
			HttpSession session = request.getSession(true);
			if (adminLogin.equals(login) && adminPassword.equals(password)) {
				session.invalidate();
				session = request.getSession(true);
				session.setAttribute(CommandHelper.AttributeName.ADMIN, true);
				path = CommandHelper.PageName.CONSOLE;
			} else {
				request.setAttribute(CommandHelper.AttributeName.LOGIN_FAILED, true);
				path = CommandHelper.PageName.ADMIN_LOGIN;
			}
		} catch (IOException ex) {
			LOGGER.error(ex);
			request.setAttribute(CommandHelper.AttributeName.ERROR, true);
		}
		helper.redirectToPage(request, response, path);
	}

	/**
	 * Метод считывает логин и пароль администратора из файла свойств.
	 * <p>
	 * Работа с файлом свойств осуществляется через класс
	 * <code>Properties</code>.
	 * 
	 * @throws IOException
	 *             если произошла ошибка при чтении файла свойств или при
	 *             закрытии потока ввода, связанного с файлом свойств
	 * @see Properties
	 */
	private void initAdmin() throws IOException {
		Properties props = new Properties();
		InputStream is = this.getClass().getResourceAsStream(PROPERTIES_FILE);
		props.load(is);
		adminLogin = props.getProperty(LOGIN_PROPERTY);
		adminPassword = props.getProperty(PASSWORD_PROPERTY);
		is.close();
	}
}
