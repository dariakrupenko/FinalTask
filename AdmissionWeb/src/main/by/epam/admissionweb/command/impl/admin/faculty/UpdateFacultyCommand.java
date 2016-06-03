package main.by.epam.admissionweb.command.impl.admin.faculty;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;
import main.by.epam.admissionweb.entity.Discipline;
import main.by.epam.admissionweb.entity.Faculty;
import main.by.epam.admissionweb.service.DisciplineService;
import main.by.epam.admissionweb.service.FacultyService;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.AlreadyExistsException;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>UpdateFacultyCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * обновление информации о факультете.
 * <p>
 * Данное действие доступно только для администратора системы.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see Faculty
 *
 */
public class UpdateFacultyCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на обновление
	 * информации о факультете.
	 * <p>
	 * В процессе работы метод обращается к сервисам {@link DisciplineService}
	 * (для получение всего списка дисциплин) и {@link FacultyService}. Если
	 * обновление факультета запрещено логикой приложения, в контекст запроса
	 * устаналивается соответсвующий атрибут, если обновление произошло успешно
	 * - атрибут успешного завершения.
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
	 * @see DisciplineService
	 * @see FacultyService
	 * @see CommandHelper
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : UpdateFacultyCommand");
		CommandHelper helper = CommandHelper.getInstance();
		String path = null;
		Object adminObj = request.getSession(true).getAttribute(CommandHelper.AttributeName.ADMIN);
		if (adminObj == null) {
			path = CommandHelper.PageName.ADMIN_LOGIN;
		} else {
			int id = helper.parseId(request.getParameter(CommandHelper.ParameterName.FACULTY_ID));
			String title = request.getParameter(CommandHelper.ParameterName.TITLE);
			String description = request.getParameter(CommandHelper.ParameterName.DESCRIPTION);
			String logoname = request.getParameter(CommandHelper.ParameterName.LOGONAME);
			String phone = request.getParameter(CommandHelper.ParameterName.PHONE);
			String address = request.getParameter(CommandHelper.ParameterName.ADDRESS);
			String dean = request.getParameter(CommandHelper.ParameterName.DEAN);
			int plan = helper.parsePlan(request.getParameter(CommandHelper.ParameterName.PLAN));
			List<Discipline> disciplines = helper.parseDisciplines(request);
			try {
				Faculty f = helper.constructFaculty(id, title, description, logoname, phone, address, dean, plan,
						disciplines);
				LOGGER.debug("COMMAND : UpdateFacultyCommand (f = {})", f);
				DisciplineService dService = ServiceFactory.getInstance().getDisciplineService();
				List<Discipline> list = dService.getDisciplinesList(CommandHelper.REQUIRED_PAGE_DEFAULT,
						CommandHelper.ELEMENTS_MAX_VALUE);
				request.setAttribute(CommandHelper.AttributeName.D_LIST, list);
				FacultyService service = ServiceFactory.getInstance().getFacultyService();
				try {
					Faculty fNew = service.updateFaculty(f);
					if (fNew == null) {
						request.setAttribute(CommandHelper.AttributeName.NOT_VALID, true);
					} else {
						f = fNew;
						request.setAttribute(CommandHelper.AttributeName.SUCCESS_UPDATED, true);
					}
				} catch (AlreadyExistsException ex) {
					LOGGER.error(ex);
					request.setAttribute(CommandHelper.AttributeName.ALREADY_EXISTS, true);
				}
				request.setAttribute(CommandHelper.AttributeName.FACULTY, f);
			} catch (ServiceException ex) {
				LOGGER.error(ex);
				request.setAttribute(CommandHelper.AttributeName.ERROR, true);
			}
			path = CommandHelper.PageName.EDIT_FACULTY;
		}
		helper.redirectToPage(request, response, path);
	}
}
