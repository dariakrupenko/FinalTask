package main.by.epam.admissionweb.command.impl.general;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;

/**
 * Класс <code>ToStartEnrollPageCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * перенаправление запроса на страницу открытия нового набора.
 * 
 * @author Daria Krupenko
 *
 */
public class ToStartEnrollPageCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();
	
	/**
	 * Метод описывает поведение команды по обработке запроса на перенаправление
	 * запроса на страницу открытия нового набора.
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
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : ToStartEnrollPageCommand");
		Object adminObj = request.getSession(true).getAttribute(CommandHelper.AttributeName.ADMIN);
		String path = null;
		if (adminObj == null) {
			path = CommandHelper.PageName.ADMIN_LOGIN;
		} else {
			Date date = new Date();
			request.setAttribute(CommandHelper.AttributeName.BEGIN_DATE, date);
			path = CommandHelper.PageName.START_ENROLL;
		}
		CommandHelper.getInstance().redirectToPage(request, response, path);
	}

}
