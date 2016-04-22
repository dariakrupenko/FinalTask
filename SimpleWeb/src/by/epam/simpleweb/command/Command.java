package by.epam.simpleweb.command;

import javax.servlet.http.HttpServletRequest;

import by.epam.simpleweb.command.exception.CommandException;

/**
 * Интерфейс Command должен быть реализован любым классом, предназначенным для
 * выполнения какой-либо команды
 * 
 * @author User
 *
 */
public interface Command {

	/**
	 * Запуск команды на выполнение
	 * 
	 * @param request
	 *            - контекст запроса
	 * @return - строковое представление пути страницы, на которую должен быть
	 *         перенаправлен запрос
	 * @throws CommandException
	 *             ошибка при выполнении команды
	 */
	public String execute(HttpServletRequest request) throws CommandException;

}
