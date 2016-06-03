package main.by.epam.admissionweb.command.impl.general;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;

/**
 * Класс <code>LogoutCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * выход из системы.
 * <p>
 * Данная команда выполняет как при выходе администратора из системы, так и при
 * выходе абитуриента.
 * 
 * @author Daria Krupenko
 *
 */
public class LogoutCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на выход из
	 * системы.
	 * <p>
	 * Выход из системы производится через закрытие сессии. Далее запрос
	 * перенаправляется на главную страницу
	 * 
	 * @param request
	 *            контекст запроса (используется для получение доступа к
	 *            парамаетрам запроса и атрибутам запроса/сессии/приложения)
	 * @param response
	 *            контекст ответа на HTTP-запрос
	 * @throws CommandException
	 *             если при перенаправлении запроса произошла ошибка
	 * @see HttpSession
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : LogoutCommand");
		request.getSession(true).invalidate();
		CommandHelper.getInstance().redirectToPage(request, response, CommandHelper.PageName.INDEX);
	}

}
