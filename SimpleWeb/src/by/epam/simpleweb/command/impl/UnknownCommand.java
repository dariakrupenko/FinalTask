package by.epam.simpleweb.command.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import by.epam.simpleweb.command.Command;
import by.epam.simpleweb.controller.PageName;

/**
 * Класс UnknownCommand предназначен для описания неизвестной команды
 * @author User
 *
 */
public class UnknownCommand implements Command{
	
	/**
	 * Логгер
	 */
	private static final Logger logger = LogManager.getRootLogger();
	
	@Override
	public String execute(HttpServletRequest request) {
		logger.debug("Command : unknown-command");
		return PageName.ERROR_PAGE;
	}
}
