package main.by.epam.admissionweb.command.impl.general;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.controller.Controller;
import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;

/**
 * Класс <code>UnknownCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по умолчанию.
 * <p>
 * Команда по умолчанию выполняется, когда диспетчеру запросов (контроллеру) не
 * удается получить команду в ответ на запрос клиента.
 * 
 * @author Daria Krupenko
 * @see Controller
 */
public class UnknownCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по умолчанию.
	 * <p>
	 * Поведением команды по умолчанию является перенаправление запроса на
	 * страницу ошибки
	 *
	 * @param request
	 *            контекст запроса (используется для получение доступа к
	 *            параметрам запроса и атрибутам запроса/сессии/приложения)
	 * @param response
	 *            контекст ответа на HTTP-запрос
	 * @throws CommandException
	 *             если при перенаправлении запроса произошла ошибка
	 * @see CommandHelper
	 */
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : UnknownCommand");
		CommandHelper.getInstance().redirectToPage(request, response, CommandHelper.PageName.DEFAULT_ERROR);
	}

}
