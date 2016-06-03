package main.by.epam.admissionweb.command.impl.admin.enrollment;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;
import main.by.epam.admissionweb.entity.Enroll;
import main.by.epam.admissionweb.service.EnrollmentService;
import main.by.epam.admissionweb.service.PageManagerService;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>GetEnrollsListCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * получение списка всех наборов учебного заведения.
 * <p>
 * Данный список доступен только для администратора системы.
 * <p>
 * Если не найдено ни одного набора, в ответ на запрос передается пустой список.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see Enroll
 *
 */
public class GetEnrollsListCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на получение
	 * списка всех наборов учебного заведения.
	 * <p>
	 * Метод поддерживает построение постраничного вывода длинных списков через
	 * обращение к сервису {@link PageManagerService}.
	 * <p>
	 * Для получения списка метод обращается к сервису {@link EnrollmentService}
	 * . В результате работы метод устанавливает в контекст запроса список
	 * дисциплин и атрибуты прокрутки страниц.
	 * <p>
	 * Если клиент не является администратором системы, запрос будет
	 * перенаправлен на страницу авторизации администратора.
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
	 * @see EnrollmentService
	 * @see CommandHelper
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : GetEnrollsListCommand");
		CommandHelper helper = CommandHelper.getInstance();
		String path = null;
		Object adminObj = request.getSession(true).getAttribute(CommandHelper.AttributeName.ADMIN);
		if (adminObj == null) {
			path = CommandHelper.PageName.ADMIN_LOGIN;
		} else {
			try {
				int currentPage = helper
						.parseCurrentPage(request.getParameter(CommandHelper.ParameterName.CURRENT_PAGE));
				boolean next = helper.parseDirection(request.getParameter(CommandHelper.ParameterName.DIRECTION));
				int elementsPerPage = helper.parseElementsPerPage(
						request.getSession(true).getAttribute(CommandHelper.AttributeName.ELEMENTS_PER_PAGE));
				EnrollmentService service = ServiceFactory.getInstance().getEnrollmentService();
				int enrollsNumber = service.getEnrollsNumber();
				PageManagerService pageService = ServiceFactory.getInstance().getPageManagerService();
				int pagesNumber = pageService.getPagesNumber(enrollsNumber, elementsPerPage);
				int requiredPage = pageService.getRequiredPage(currentPage, next, pagesNumber);
				LOGGER.debug("COMMAND : GetEnrollsListCommand (page to view = {})", requiredPage);
				List<Enroll> list = service.getEnrollsList(requiredPage, elementsPerPage);
				request.setAttribute(CommandHelper.AttributeName.LIST, list);
				request.setAttribute(CommandHelper.AttributeName.PAGE, requiredPage);
				request.setAttribute(CommandHelper.AttributeName.PAGES_NUMBER, pagesNumber);
			} catch (ServiceException ex) {
				LOGGER.error(ex);
				request.setAttribute(CommandHelper.AttributeName.ERROR, true);
			}
			path = CommandHelper.PageName.ENROLLS_LIST;
		}
		helper.redirectToPage(request, response, path);

	}

}
