package main.by.epam.admissionweb.command.impl.applicant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;
import main.by.epam.admissionweb.entity.Applicant;

/**
 * Класс <code>EnterAccountCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * вход в личный кабинет абитуриента.
 * <p>
 * Данное действие доступно только для абитуриента.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see Applicant
 *
 */
public class EnterAccountCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса вход в личный
	 * кабинет абитуриента.
	 * <p>
	 * Метод проверяет наличие объекта абитуриента в сессии. Если объект не
	 * найден, запрос будет перенаправлен на странцу авторизации абитуриента
	 * <p>
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
		LOGGER.debug("COMMAND : EnterAccountCommand");
		CommandHelper helper = CommandHelper.getInstance();
		HttpSession session = request.getSession(true);
		Object applicantObj = session.getAttribute(CommandHelper.AttributeName.APPLICANT);
		String path = null;
		if (applicantObj == null) {
			path = CommandHelper.PageName.APPLICANT_LOGIN;
		} else {
			request.setAttribute(CommandHelper.AttributeName.APPLICANT_TO_REQUEST, applicantObj);
			path = CommandHelper.PageName.ACCOUNT;
		}
		helper.redirectToPage(request, response, path);
	}
}
