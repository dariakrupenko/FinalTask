package main.by.epam.admissionweb.command.impl.admin.faculty;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;
import main.by.epam.admissionweb.entity.Faculty;
import main.by.epam.admissionweb.service.FacultyService;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>DeleteFacultyCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * удаление факультета из системы.
 * <p>
 * Данное действие доступно только для администратора системы.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see Faculty
 *
 */
public class DeleteFacultyCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на удаление
	 * факультета из системы.
	 * <p>
	 * В процессе работы метод обращается к сервису {@link FacultyService}. Если
	 * удаление факультета запрещено логикой приложения, в контекст запроса
	 * устанавливаетя соответствующий атрибут, если удаление произошло успешно -
	 * атрибут успешного завершения.
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
	 * @see CommandHelper
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : DeleteFacultyCommand");
		CommandHelper helper = CommandHelper.getInstance();
		String path = null;
		Object adminObj = request.getSession(true).getAttribute(CommandHelper.AttributeName.ADMIN);
		if (adminObj == null) {
			path = CommandHelper.PageName.ADMIN_LOGIN;
		} else {
			String idStr = request.getParameter(CommandHelper.ParameterName.FACULTY_ID);
			int id = helper.parseId(idStr);
			LOGGER.debug("COMMAND : DeleteFacultyCommand (id = {})", id);
			if (id == 0) {
				request.setAttribute(CommandHelper.AttributeName.NOT_FOUND, true);
			} else {
				try {
					FacultyService service = ServiceFactory.getInstance().getFacultyService();
					boolean isDeleteEnable = service.isDeleteEnable();
					if (!isDeleteEnable) {
						request.setAttribute(CommandHelper.AttributeName.DELETE_UNABLE, true);
					} else {
						service.deleteFaculty(id);
						request.setAttribute(CommandHelper.AttributeName.SUCCESS_DELETED, true);
					}
				} catch (ServiceException ex) {
					LOGGER.error(ex);
					request.setAttribute(CommandHelper.AttributeName.ERROR, true);
				}
			}
			path = CommandHelper.PageName.EDIT_FACULTY;
		}
		helper.redirectToPage(request, response, path);
	}

}
