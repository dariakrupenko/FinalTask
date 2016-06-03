package main.by.epam.admissionweb.command.impl.applicant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;
import main.by.epam.admissionweb.command.impl.CommandHelper.PageName;
import main.by.epam.admissionweb.entity.Applicant;
import main.by.epam.admissionweb.service.RegisterService;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>CancelRegistryForFacultyCommand</code> является реализацией
 * интерфейса {@link Command} и инкапсулирует поведение команды по обработке
 * запроса на отмену записи на факультет.
 * <p>
 * Данное действие доступно только для абитуриента.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see Applicant
 *
 */
public class CancelRegistryForFacultyCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на отмену
	 * абитуриентом записи на факультет.
	 * <p>
	 * В процессе работы метод обращается к сервису {@link RegisterService}.
	 * Если отмена записи запрещена логикой приложения, в контекст запроса
	 * устанавливаетя соответствующий атрибут, если отмена произошла успешно -
	 * атрибут успешного завершения.
	 * <p>
	 * Если клиент не является абитуриентом, запрос будет перенаправлен на
	 * страницу авторизации абитуриента.
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
	 * @see RegisterService
	 * @see CommandHelper
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {

		LOGGER.debug("COMMAND : CancelRegistryForFacultyCommand");
		CommandHelper helper = CommandHelper.getInstance();
		Object applicantObj = request.getSession(true).getAttribute(CommandHelper.AttributeName.APPLICANT);
		Applicant a = null;
		if (applicantObj instanceof Applicant) {
			a = (Applicant) applicantObj;
		}
		String path = null;
		if (a == null) {
			path = CommandHelper.PageName.APPLICANT_LOGIN;
		} else {
			try {
				RegisterService service = ServiceFactory.getInstance().getRegisterService();
				boolean isRegistered = service.isApplicantRegistered(a);
				if (!isRegistered) {
					request.setAttribute(CommandHelper.AttributeName.NOT_REGISTERED, true);
				} else {
					Applicant newA = service.cancelRegistry(a);
					if (newA == null) {
						request.setAttribute(CommandHelper.AttributeName.DELETE_UNABLE, true);
					} else {
						request.setAttribute(CommandHelper.AttributeName.SUCCESS_DELETED, true);
						request.getSession(true).setAttribute(CommandHelper.AttributeName.APPLICANT, newA);
					}
				}
			} catch (ServiceException ex) {
				LOGGER.error(ex);
				request.setAttribute(CommandHelper.AttributeName.ERROR, true);
			}
			path = PageName.STATUS;
		}
		helper.redirectToPage(request, response, path);
	}

}
