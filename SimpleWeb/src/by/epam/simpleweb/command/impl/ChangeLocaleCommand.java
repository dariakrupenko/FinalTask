package by.epam.simpleweb.command.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import by.epam.simpleweb.command.Command;
import by.epam.simpleweb.command.exception.CommandException;
import by.epam.simpleweb.controller.PageName;

/**
 * Класс ChangeLocaleCommand представляет собой команды, которая выполняет
 * операцию смены локали
 * 
 * @author User
 *
 */
public class ChangeLocaleCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger logger = LogManager.getRootLogger();

	/**
	 * Имя параметра запроса, значением которого является желаемая локаль
	 */
	private static final String LANG_PARAMETER = "lang";

	/**
	 * Строковое представление локали по умолчанию
	 */
	private static final String DEFAULT_LOCALE = "ru";

	/**
	 * Имя атрибута, значением которого является локаль, установленная в
	 * результате выполнения данной команды
	 */
	private static final String LOCALE_ATTR = "locale";

	@Override
	public String execute(HttpServletRequest request) throws CommandException {
		logger.debug("Command : change-locale");
		String lang = request.getParameter(LANG_PARAMETER);
		if (lang == null || lang.isEmpty()) {
			lang = DEFAULT_LOCALE;
		}
		HttpSession session = request.getSession(true);
		session.setAttribute(LOCALE_ATTR, lang);
		return PageName.INDEX_PAGE;
	}

}
