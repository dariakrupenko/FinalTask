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
 * Класс <code>AddDisciplineCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * добавление новой дисциплины в систему.
 * <p>
 * Данное действие доступно только для администратора системы.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see Discipline
 *
 */
public class AddDisciplineCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на добавление
	 * новой дисциплины в систему.
	 * <p>
	 * Метод поддерживает защиту от обработки запросов с одинаковыми ключами. В
	 * процессе работы метод обращается к сервису {@link DisciplineService}.
	 * Если добавление дисицплины запрещено логикой приложения, в контекст
	 * запроса устаналивается соответсвующий атрибут, если добавление произошло
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
		LOGGER.debug("COMMAND : AddDisciplineCommand");
		CommandHelper helper = CommandHelper.getInstance();
		String path = null;
		Object adminObj = request.getSession(true).getAttribute(CommandHelper.AttributeName.ADMIN);
		if (adminObj == null) {
			path = CommandHelper.PageName.ADMIN_LOGIN;
		} else {
			if (helper.isAdditionEnabled(CommandHelper.AttributeName.DISCIPLINE_KEY, request)) {
				String title = request.getParameter(CommandHelper.ParameterName.TITLE);
				try {
					DisciplineService service = ServiceFactory.getInstance().getDisciplineService();
					Discipline d = helper.constructDiscipline(0, title, null);
					LOGGER.debug("COMMAND : AddDisciplineCommand (d = {})", d);
					try {
						Discipline dNew = service.addDiscipline(d);
						if (dNew == null) {
							request.setAttribute(CommandHelper.AttributeName.NOT_VALID, true);
						} else {
							d = dNew;
							request.setAttribute(CommandHelper.AttributeName.SUCCESS_ADDED, true);
						}
					} catch (AlreadyExistsException ex) {
						LOGGER.error(ex);
						request.setAttribute(CommandHelper.AttributeName.ALREADY_EXISTS, true);
					} finally {
						request.getServletContext().removeAttribute(CommandHelper.AttributeName.DISCIPLINE_KEY);
					}
					request.setAttribute(CommandHelper.AttributeName.DISCIPLINE, d);
				} catch (ServiceException ex) {
					LOGGER.error(ex);
					request.setAttribute(CommandHelper.AttributeName.ERROR, true);
				}
			}
			path = CommandHelper.PageName.ADD_DISCIPLINE;
		}
		helper.redirectToPage(request, response, path);
	}

}
