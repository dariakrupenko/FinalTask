package main.by.epam.admissionweb.command.impl.admin.applicant;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;
import main.by.epam.admissionweb.entity.Applicant;
import main.by.epam.admissionweb.service.ApplicantService;
import main.by.epam.admissionweb.service.PageManagerService;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>GetApplicantsListCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * получение списка всех зарегистрированных в системе абитуриентов.
 * <p>
 * Данный список доступен только для администратора системы и содержит сведения
 * обо всех абитуриентах (включая тех, кто еще не записан на какой-либо
 * факультет).
 * <p>
 * Если не найдено ни одного абитуриента, в ответ на запрос передается пустой
 * список.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see Applicant
 *
 */
public class GetApplicantsListCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на получение
	 * списка всех абитуриентов, зарегистрированных в системе.
	 * <p>
	 * Метод поддерживает построение постраничного вывода длинных списков через
	 * обращение к сервису {@link PageManagerService}.
	 * <p>
	 * Для получения списка метод обращается к сервису {@link ApplicantService}.
	 * В результате работы метод устанавливает в контекст запроса список
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
	 * @see ApplicantService
	 * @see CommandHelper
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : GetApplicantsListCommand");
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
				ApplicantService service = ServiceFactory.getInstance().getApplicantService();
				int aNumber = service.getApplicantsNumber();
				PageManagerService pageService = ServiceFactory.getInstance().getPageManagerService();
				int pagesNumber = pageService.getPagesNumber(aNumber, elementsPerPage);
				int requiredPage = pageService.getRequiredPage(currentPage, next, pagesNumber);
				LOGGER.debug("COMMAND : GetApplicantsListCommand (page to view = {})", requiredPage);
				List<Applicant> list = service.getApplicantsList(requiredPage, elementsPerPage);
				request.setAttribute(CommandHelper.AttributeName.LIST, list);
				request.setAttribute(CommandHelper.AttributeName.PAGE, requiredPage);
				request.setAttribute(CommandHelper.AttributeName.PAGES_NUMBER, pagesNumber);
			} catch (ServiceException ex) {
				LOGGER.error(ex);
				request.setAttribute(CommandHelper.AttributeName.ERROR, true);
			}
			path = CommandHelper.PageName.APPLICANTS_LIST;
		}
		helper.redirectToPage(request, response, path);
	}

}
