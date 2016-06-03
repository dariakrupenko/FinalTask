package main.by.epam.admissionweb.command.impl.general;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;
import main.by.epam.admissionweb.entity.Discipline;
import main.by.epam.admissionweb.service.DisciplineService;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>ToAddFacultyPageCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * перенаправление запроса на страницу добавления нового факультета.
 * 
 * @author Daria Krupenko
 *
 */
public class ToAddFacultyPageCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на перенаправление
	 * запроса на страницу добавления нового факультета.
	 * <p>
	 * Метод обращается к сервису {@link DisciplineService} для получения списка
	 * всех дисциплин с целью предоставить выбор администратору при добавлении
	 * нового факультета.
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
	 * @see DisciplineService
	 * @see CommandHelper
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : ToAddFacultyPageCommand");
		CommandHelper helper = CommandHelper.getInstance();
		Object adminObj = request.getSession(true).getAttribute(CommandHelper.AttributeName.ADMIN);
		String path = null;
		if (adminObj == null) {
			path = CommandHelper.PageName.ADMIN_LOGIN;
		} else {
			try {
				DisciplineService service = ServiceFactory.getInstance().getDisciplineService();
				List<Discipline> list = service.getDisciplinesList(CommandHelper.REQUIRED_PAGE_DEFAULT,
						CommandHelper.ELEMENTS_MAX_VALUE);
				request.setAttribute(CommandHelper.AttributeName.D_LIST, list);
			} catch (ServiceException ex) {
				LOGGER.error(ex);
				request.setAttribute(CommandHelper.AttributeName.ERROR, true);
			}
			path = CommandHelper.PageName.ADD_FACULTY;
		}
		helper.redirectToPage(request, response, path);
	}

}
