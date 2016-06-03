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
 * Класс <code>AcceptSettingsCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * установку новых настроек.
 * <p>
 * К настройкам относится язык интерфейса и количество элементов, выводимых на
 * одну страницу при просмотре длинных списков.
 * 
 * @author Daria Krupenko
 *
 */
public class AcceptSettingsCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на установку новых
	 * настроек.
	 * <p>
	 * Новые настройки устанавливаются в качестве атрибутов в контекст сессии
	 * 
	 * @param request
	 *            контекст запроса (используется для получение доступа к
	 *            парамаетрам запроса и атрибутам запроса/сессии/приложения)
	 * @param response
	 *            контекст ответа на HTTP-запрос
	 * @throws CommandException
	 *             если при перенаправлении запроса произошла ошибка
	 */
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : AcceptSettingsCommand");
		CommandHelper helper = CommandHelper.getInstance();
		String lang = request.getParameter(CommandHelper.ParameterName.LANG);
		String elementsPerPageStr = request.getParameter(CommandHelper.ParameterName.ELEMENTS_PER_PAGE);
		int elementsPerPage = helper.parseElementsPerPage(elementsPerPageStr);
		LOGGER.debug("COMMAND : AcceptSettingsCommand (lang = {}, elements = {})", lang, elementsPerPage);
		HttpSession session = request.getSession(true);
		session.setAttribute(CommandHelper.AttributeName.LOCALE, lang);
		session.setAttribute(CommandHelper.AttributeName.ELEMENTS_PER_PAGE, elementsPerPage);
		request.setAttribute(CommandHelper.AttributeName.SUCCESS_COMPLETED, true);
		helper.redirectToPage(request, response, CommandHelper.PageName.SETTINGS);

	}

}
