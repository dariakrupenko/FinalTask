package main.by.epam.admissionweb.command.impl.admin.faculty;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;
import main.by.epam.admissionweb.command.impl.general.ToAddFacultyPageCommand;
import main.by.epam.admissionweb.entity.Discipline;
import main.by.epam.admissionweb.entity.Faculty;
import main.by.epam.admissionweb.service.FacultyService;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.AlreadyExistsException;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>AddFacultyCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * добавление нового факультета в систему.
 * <p>
 * Данное действие доступно только для администратора системы.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see Faculty
 *
 */
public class AddFacultyCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на добавление
	 * нового факультета в систему.
	 * <p>
	 * Метод поддерживает защиту от обработки запросов с одинаковыми ключами. В
	 * процессе работы метод обращается к сервису {@link FacultyService}.
	 * Если добавление факультета запрещено логикой приложения, в контекст
	 * запроса устаналивается соответсвующий атрибут, если добавление произошло
	 * успешно - атрибут успешного завершения. Далее управление передается
	 * команде {@link ToAddFacultyPageCommand}
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
	 * @see FacultyService
	 * @see ToAddFacultyPageCommand
	 * @see CommandHelper
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : AddFacultyCommand");
		CommandHelper helper = CommandHelper.getInstance();
		Object adminObj = request.getSession(true).getAttribute(CommandHelper.AttributeName.ADMIN);
		if (adminObj == null) {
			helper.redirectToPage(request, response, CommandHelper.PageName.ADMIN_LOGIN);
		} else {
			if (helper.isAdditionEnabled(CommandHelper.AttributeName.FACULTY_KEY, request)) {
				String title = request.getParameter(CommandHelper.ParameterName.TITLE);
				String description = request.getParameter(CommandHelper.ParameterName.DESCRIPTION);
				String logoname = request.getParameter(CommandHelper.ParameterName.LOGONAME);
				String phone = request.getParameter(CommandHelper.ParameterName.PHONE);
				String address = request.getParameter(CommandHelper.ParameterName.ADDRESS);
				String dean = request.getParameter(CommandHelper.ParameterName.DEAN);
				int plan = helper.parsePlan(request.getParameter(CommandHelper.ParameterName.PLAN));
				List<Discipline> disciplines = helper.parseDisciplines(request);
				try {
					Faculty f = helper.constructFaculty(0, title, description, logoname, phone, address, dean, plan,
							disciplines);
					LOGGER.debug("COMMAND : AddFacultyCommand (f = {})", f);
					FacultyService service = ServiceFactory.getInstance().getFacultyService();
					try {
						Faculty fNew = service.addFaculty(f);
						if (fNew == null) {
							request.setAttribute(CommandHelper.AttributeName.NOT_VALID, true);
						} else {
							f = fNew;
							request.setAttribute(CommandHelper.AttributeName.SUCCESS_ADDED, true);
						}
					} catch (AlreadyExistsException ex) {
						LOGGER.error(ex);
						request.setAttribute(CommandHelper.AttributeName.ALREADY_EXISTS, true);
					} finally {
						request.getServletContext().removeAttribute(CommandHelper.AttributeName.FACULTY_KEY);
					}
					request.setAttribute(CommandHelper.AttributeName.FACULTY, f);
					Command command = new ToAddFacultyPageCommand();
					command.execute(request, response);
				} catch (ServiceException ex) {
					LOGGER.error(ex);
					request.setAttribute(CommandHelper.AttributeName.ERROR, true);
					helper.redirectToPage(request, response, CommandHelper.PageName.ADD_FACULTY);
				}
			}
		}
	}

}
