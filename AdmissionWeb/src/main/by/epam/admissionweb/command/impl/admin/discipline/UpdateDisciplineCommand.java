package main.by.epam.admissionweb.command.impl.admin.discipline;

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
import main.by.epam.admissionweb.service.exception.AlreadyExistsException;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>UpdateDisciplineCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * обновление информации о дисциплине.
 * <p>
 * Данное действие доступно только для администратора системы.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see Discipline
 *
 */
public class UpdateDisciplineCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на обновлении
	 * информации о дисциплине.
	 * <p>
	 * В процессе работы метод обращается к сервису {@link DisciplineService}.
	 * Если обновление дисицплины запрещено логикой приложения, в контекст
	 * запроса устаналивается соответсвующий атрибут, если обновление произошло
	 * успешно - атрибут успешного завершения.
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
	 * @see CommandHelper
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : UpdateDisciplineCommand");
		CommandHelper helper = CommandHelper.getInstance();
		String path = null;
		Object adminObj = request.getSession(true).getAttribute(CommandHelper.AttributeName.ADMIN);
		if (adminObj == null) {
			path = CommandHelper.PageName.ADMIN_LOGIN;
		} else {
			String idStr = request.getParameter(CommandHelper.ParameterName.DISCIPLINE_ID);
			int id = helper.parseId(idStr);
			String title = request.getParameter(CommandHelper.ParameterName.TITLE);
			try {
				DisciplineService service = ServiceFactory.getInstance().getDisciplineService();
				Discipline d = helper.constructDiscipline(id, title, null);
				LOGGER.debug("COMMAND : UpdateDisciplineCommand (d = {})", d);
				try {
					Discipline dNew = service.updateDiscipline(d);
					if (dNew == null) {
						request.setAttribute(CommandHelper.AttributeName.NOT_VALID, true);
					} else {
						d = dNew;
						request.setAttribute(CommandHelper.AttributeName.SUCCESS_UPDATED, true);
					}
				} catch (AlreadyExistsException ex) {
					LOGGER.error(ex);
					request.setAttribute(CommandHelper.AttributeName.ALREADY_EXISTS, true);
				}
				request.setAttribute(CommandHelper.AttributeName.DISCIPLINE, d);
			} catch (ServiceException ex) {
				LOGGER.error(ex);
				request.setAttribute(CommandHelper.AttributeName.ERROR, true);
			}
			path = CommandHelper.PageName.EDIT_DISCIPLINE;
		}
		helper.redirectToPage(request, response, path);
	}

}
