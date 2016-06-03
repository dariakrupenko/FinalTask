package main.by.epam.admissionweb.command.impl.general;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;
import main.by.epam.admissionweb.command.impl.CommandHelper.PageName;
import main.by.epam.admissionweb.entity.Faculty;
import main.by.epam.admissionweb.service.DisciplineService;
import main.by.epam.admissionweb.service.FacultyService;
import main.by.epam.admissionweb.service.PageManagerService;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>GetFacultiesListCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * получение списка всех факультетов.
 * <p>
 * Если не найдено ни одного факультета, в ответ на запрос передается пустой
 * список.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see Faculty
 *
 */
public class GetFacultiesListCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на получение
	 * списка всех факультетов.
	 * <p>
	 * Метод поддерживает построение постраничного вывода длинных списков через
	 * обращение к сервису {@link PageManagerService}.
	 * <p>
	 * Для получения списка метод обращается к сервису {@link FacultyService}. В
	 * результате работы метод устанавливает в контекст запроса список дисциплин
	 * и атрибуты прокрутки страниц.
	 * <p>
	 * В зависимости от того, является ли клиент администратором, запрос
	 * перенаправляется на соответствующую страницу
	 * <p>
	 * Если в процессе обработки возникла ошибка, обусловленная работой сервисов
	 * приложения, в контекст запроса устанавливается флаг ошибки.
	 * 
	 * @param request
	 *            контекст запроса (используется для получение доступа к
	 *            парамаетрам запроса и атрибутам запроса/сессии/приложения)
	 * @param response
	 *            контекст ответа на HTTP-запрос
	 * @throws CommandException
	 *             если при перенаправлении запроса произошла ошибка
	 * @see PageManagerService
	 * @see DisciplineService
	 * @see CommandHelper
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {

		LOGGER.debug("COMMAND : GetFacultiesListCommand");
		CommandHelper helper = CommandHelper.getInstance();
		Object adminObj = request.getSession(true).getAttribute(CommandHelper.AttributeName.ADMIN);
		String path = null;
		try {
			int currentPage = helper.parseCurrentPage(request.getParameter(CommandHelper.ParameterName.CURRENT_PAGE));
			boolean next = helper.parseDirection(request.getParameter(CommandHelper.ParameterName.DIRECTION));
			int elementsPerPage = helper.parseElementsPerPage(
					request.getSession(true).getAttribute(CommandHelper.AttributeName.ELEMENTS_PER_PAGE));
			FacultyService service = ServiceFactory.getInstance().getFacultyService();
			int facultiesNumber = service.getFacultiesNumber();
			PageManagerService pageService = ServiceFactory.getInstance().getPageManagerService();
			int pagesNumber = pageService.getPagesNumber(facultiesNumber, elementsPerPage);
			int requiredPage = pageService.getRequiredPage(currentPage, next, pagesNumber);
			LOGGER.debug("COMMAND : GetFacultiesListCommand (page to view = {})", requiredPage);
			List<Faculty> list = service.getFacultiesList(requiredPage, elementsPerPage);
			request.setAttribute(CommandHelper.AttributeName.LIST, list);
			request.setAttribute(CommandHelper.AttributeName.PAGE, requiredPage);
			request.setAttribute(CommandHelper.AttributeName.PAGES_NUMBER, pagesNumber);
		} catch (ServiceException ex) {
			LOGGER.error(ex);
			request.setAttribute(CommandHelper.AttributeName.ERROR, true);
		}
		boolean forAdmin = helper.parseForAdmin(request.getParameter(CommandHelper.ParameterName.FOR_ADMIN));
		if (forAdmin && adminObj != null) {
			path = PageName.FACULTIES_LIST_ADMIN;
		} else {
			path = PageName.FACULTIES_LIST;
		}
		helper.redirectToPage(request, response, path);
	}
}
