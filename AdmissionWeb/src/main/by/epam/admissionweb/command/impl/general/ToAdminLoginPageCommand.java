package main.by.epam.admissionweb.command.impl.general;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;

/**
 * Класс <code>ToAdminLoginPageCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * перенаправление запроса на страницу консоли администратора.
 * 
 * @author Daria Krupenko
 *
 */
public class ToAdminLoginPageCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на перенаправление
	 * запроса на страницу консоли администратора.
	 * <p>
	 * Если клиент не является администратором, запрос будет перенаправлен на
	 * страницу авторизации администатора
	 * 
	 * @param request
	 *            контекст запроса (используется для получение доступа к
	 *            параметрам запроса и атрибутам запроса/сессии/приложения)
	 * @param response
	 *            контекст ответа на HTTP-запрос
	 * @throws CommandException
	 *             если при перенаправлении запроса произошла ошибка
	 */
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : ToAdminLoginPage");
		HttpSession session = request.getSession(true);
		Object adminObj = session.getAttribute(CommandHelper.AttributeName.ADMIN);
		String path = null;
		if (adminObj == null) {
			path = CommandHelper.PageName.ADMIN_LOGIN;
		} else {
			path = CommandHelper.PageName.CONSOLE;
		}
		CommandHelper.getInstance().redirectToPage(request, response, path);
	}
}
