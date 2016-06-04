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
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>GetDisciplineCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * получение информации о дисциплине.
 * <p>
 * Данная информация доступна только для администратора системы и содержит
 * основные сведения о выбранной дисциплине.
 * <p>
 * Если выбранная дисциплина не найдена, ответом на запрос будет пустой объект.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see Discipline
 *
 */
public class GetDisciplineCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на получение
	 * сведения о дисциплине.
	 * <p>
	 * В процессе своей работы метод обращается к сервису
	 * {@link DisciplineService}. Если клиент не является администратором
	 * системы, запрос будет перенаправлен на страницу авторизации
	 * администратора.
	 * <p>
	 * В результате работы метод устанавливает в контекст запроса объект
	 * дисциплины. Если в процессе обработки возникла ошибка, обусловленная
	 * работой сервисов приложения, в контекст запроса устанавливается флаг
	 * ошибки.
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
		LOGGER.debug("COMMAND : GetDisciplineCommand");
		CommandHelper helper = CommandHelper.getInstance();
		String path = null;
		Object adminObj = request.getSession(true).getAttribute(CommandHelper.AttributeName.ADMIN);
		if (adminObj == null) {
			path = CommandHelper.PageName.ADMIN_LOGIN;
		} else {
			String idStr = request.getParameter(CommandHelper.ParameterName.DISCIPLINE_ID);
			int id = helper.parseId(idStr);
			LOGGER.debug("COMMAND : GetDisciplineCommand (id = {})", id);
			DisciplineService service = ServiceFactory.getInstance().getDisciplineService();
			try {
				Discipline d = service.getDiscipline(id);
				if (d == null) {
					request.setAttribute(CommandHelper.AttributeName.NOT_FOUND, true);
				} else {
					request.setAttribute(CommandHelper.AttributeName.DISCIPLINE, d);
				}
			} catch (ServiceException ex) {
				LOGGER.error(ex);
				request.setAttribute(CommandHelper.AttributeName.ERROR, true);
			}
			path = CommandHelper.PageName.EDIT_DISCIPLINE;
		}
		helper.redirectToPage(request, response, path);
	}
}
