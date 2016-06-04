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
import main.by.epam.admissionweb.entity.Faculty;
import main.by.epam.admissionweb.service.DisciplineService;
import main.by.epam.admissionweb.service.FacultyService;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>GetFacultyCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * получение информации о факультете.
 * <p>
 * Если выбранный факультет не найден, ответом на запрос будет пустой объект.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see Faculty
 *
 */
public class GetFacultyCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на получение
	 * информации о факультете.
	 * <p>
	 * Для получения списка метод обращается к сервису {@link FacultyService}. В
	 * результате работы метод устанавливает в контекст запроса объект
	 * факультета.
	 * <p>
	 * В зависимости от того, является ли клиент администратором, запрос
	 * перенаправляется на соответствующую страницу.
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
	 * @see CommandHelper
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : GetFacultyCommand");
		CommandHelper helper = CommandHelper.getInstance();
		Object adminObj = request.getSession(true).getAttribute(CommandHelper.AttributeName.ADMIN);
		String path = null;
		String idStr = request.getParameter(CommandHelper.ParameterName.FACULTY_ID);
		int id = helper.parseId(idStr);
		LOGGER.debug("COMMAND : GetFacultyCommand (id = {})", id);
		FacultyService service = ServiceFactory.getInstance().getFacultyService();
		try {
			Faculty f = service.getFaculty(id);
			if (f == null) {
				request.setAttribute(CommandHelper.AttributeName.NOT_FOUND, true);
			} else {
				request.setAttribute(CommandHelper.AttributeName.FACULTY, f);

				DisciplineService dService = ServiceFactory.getInstance().getDisciplineService();
				List<Discipline> list = dService.getDisciplinesList(CommandHelper.REQUIRED_PAGE_DEFAULT,
						CommandHelper.ELEMENTS_MAX_VALUE);
				request.setAttribute(CommandHelper.AttributeName.D_LIST, list);
			}
		} catch (ServiceException ex) {
			LOGGER.error(ex);
			request.setAttribute(CommandHelper.AttributeName.ERROR, true);
		}
		boolean forAdmin = helper.parseForAdmin(request.getParameter(CommandHelper.ParameterName.FOR_ADMIN));
		if (forAdmin && adminObj != null) {
			path = CommandHelper.PageName.EDIT_FACULTY;
		} else {
			path = CommandHelper.PageName.FACULTY;
		}
		helper.redirectToPage(request, response, path);

	}

}
