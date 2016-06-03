package main.by.epam.admissionweb.command.impl.admin.register;

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
import main.by.epam.admissionweb.entity.RegisterRecord;
import main.by.epam.admissionweb.service.FacultyService;
import main.by.epam.admissionweb.service.PageManagerService;
import main.by.epam.admissionweb.service.RegisterService;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>GetRegisterByStatusCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * получение ведомости абитуриентов с указанными статусом и факультетом.
 * <p>
 * Данная информация доступна только для администратора системы.
 * <p>
 * Если не найдено ни одной записи в ведомости, в ответ на запрос передается
 * пустой список.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see RegisterRecord
 *
 */
public class GetRegisterByStatusCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на получение
	 * ведомости абитуриентов с указанным статусом и для указанного факультета.
	 * <p>
	 * Метод поддерживает построение постраничного вывода длинных списков через
	 * обращение к сервису {@link PageManagerService}.
	 * <p>
	 * Для получения списка записей метод обращается к сервисам
	 * {@link FacultyService} (для получения только тех записей, которые
	 * относятся к определенному факультету) и {@link RegisterService}. В
	 * результате работы метод устанавливает в контекст запроса ведомость
	 * абитуриентов и атрибуты прокрутки страниц.
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
	 * @see FacultyService
	 * @see RegisterService
	 * @see CommandHelper
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : GetRegisterByStatusCommand");
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
				int fId = helper.parseId(request.getParameter(CommandHelper.ParameterName.FACULTY_ID));
				LOGGER.debug("COMMAND : GetRegisterByStatusCommand (fId = {})", fId);
				Faculty f = ServiceFactory.getInstance().getFacultyService().getFaculty(fId);
				request.setAttribute(CommandHelper.AttributeName.FACULTY, f);
				if (f != null) {
					boolean isAdmitted = helper.parseStatus(request.getParameter(CommandHelper.ParameterName.STATUS));
					LOGGER.debug("COMMAND : GetRegisterByStatusCommand (admitted = {})", isAdmitted);
					RegisterService service = ServiceFactory.getInstance().getRegisterService();
					int recordsNumber = service.getRecordsNumberByStatusAndFaculty(isAdmitted, f);
					PageManagerService pageService = ServiceFactory.getInstance().getPageManagerService();
					int pagesNumber = pageService.getPagesNumber(recordsNumber, elementsPerPage);
					int requiredPage = pageService.getRequiredPage(currentPage, next, pagesNumber);
					LOGGER.debug("COMMAND : GetRegisterByStatusCommand (page to view = {})", requiredPage);
					List<RegisterRecord> list = service.getRegisterByStatusAndFaculty(requiredPage, elementsPerPage,
							isAdmitted, f);
					request.setAttribute(CommandHelper.AttributeName.PAGE, requiredPage);
					request.setAttribute(CommandHelper.AttributeName.PAGES_NUMBER, pagesNumber);
					request.setAttribute(CommandHelper.AttributeName.LIST, list);
					request.setAttribute(CommandHelper.AttributeName.STATUS, isAdmitted);
				} else {
					request.setAttribute(CommandHelper.AttributeName.ERROR, true);
				}
			} catch (ServiceException ex) {
				LOGGER.error(ex);
				request.setAttribute(CommandHelper.AttributeName.ERROR, true);
			}
			path = PageName.APPLICANTS_RATE;
		}
		helper.redirectToPage(request, response, path);
	}
}
