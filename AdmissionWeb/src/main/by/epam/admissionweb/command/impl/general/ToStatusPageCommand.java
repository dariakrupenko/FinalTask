package main.by.epam.admissionweb.command.impl.general;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;
import main.by.epam.admissionweb.entity.Applicant;
import main.by.epam.admissionweb.entity.RegisterRecord;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>ToStatusPageCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * перенаправление запроса на страницу статуса абитуриента.
 * 
 * @author Daria Krupenko
 *
 */
public class ToStatusPageCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на перенаправление
	 * запроса на страницу статуса абитуриента.
	 * <p>
	 * Метод обращается к сервису {@link RegisterService} для получения статуса
	 * абитуриента.
	 * <p>
	 * Если клиент не является абитуриентом, запрос будет перенаправлен на
	 * страницу авторизации абитуриента
	 * 
	 * @param request
	 *            контекст запроса (используется для получение доступа к
	 *            параметрам запроса и атрибутам запроса/сессии/приложения)
	 * @param response
	 *            контекст ответа на HTTP-запрос
	 * @throws CommandException
	 *             если при перенаправлении запроса произошла ошибка
	 * @see RegisterService
	 * @see CommandHelper
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : ToStatusPageCommand");
		String path = null;
		Object applicantObj = request.getSession(true).getAttribute(CommandHelper.AttributeName.APPLICANT);
		if (applicantObj == null) {
			path = CommandHelper.PageName.APPLICANT_LOGIN;
		} else {
			try {
				Applicant a = null;
				if (applicantObj instanceof Applicant) {
					a = (Applicant) applicantObj;
				}
				RegisterRecord r = ServiceFactory.getInstance().getRegisterService().updateRecordInf(a);
				if (r == null) {
					request.setAttribute(CommandHelper.AttributeName.NOT_REGISTERED, true);
				} else {
					a.setRecord(r);
				}
				request.getSession(true).setAttribute(CommandHelper.AttributeName.APPLICANT, a);
				path = CommandHelper.PageName.STATUS;
			} catch (ServiceException ex) {
				LOGGER.error(ex);
				request.setAttribute(CommandHelper.AttributeName.ERROR, true);
			}
		}
		CommandHelper.getInstance().redirectToPage(request, response, path);
	}

}
