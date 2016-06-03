package main.by.epam.admissionweb.command.impl.applicant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;
import main.by.epam.admissionweb.entity.Applicant;
import main.by.epam.admissionweb.service.ApplicantService;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>LoginApplicantCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * авторизацию абитуриента.
 * 
 * @author Daria Krupenko
 * @see Applicant
 *
 */
public class LoginApplicantCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на авторизацию абитуриента.
	 * <p>
	 * В процессе работы метод обращается к сервису {@link ApplicantService}.
	 * В случае успешной авторизации запрос перенаправляется на страницу 
	 * личного кабинета абитуриента, иначе - на страницу авторизации абитуриента (в
	 * контекст запроса устанавливается атрибут, сигнализирующий о неудавшейся
	 * попытке авторизации). Если авторизация завершилась успешно, предыдущая сессия прерывается
	 * и открывается новая сессия, связанная с абитуриентом.
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
	 * @see ApplicantService
	 * @see CommandHelper
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {

		LOGGER.debug("COMMAND : LoginApplicantCommand");
		CommandHelper helper = CommandHelper.getInstance();
		String login = request.getParameter(CommandHelper.ParameterName.LOGIN);
		String password = request.getParameter(CommandHelper.ParameterName.PASSWORD);
		LOGGER.debug("COMMAND : LoginApplicantCommand (login = {}, password = {})", login, password);
		String path = null;
		try {
			ApplicantService service = ServiceFactory.getInstance().getApplicantService();
			Applicant a = service.loginApplicant(login, password);
			if (a != null) {
				HttpSession session = request.getSession(true);
				session.invalidate();
				session = request.getSession(true);
				session.setAttribute(CommandHelper.AttributeName.APPLICANT, a);
				request.setAttribute(CommandHelper.AttributeName.APPLICANT_TO_REQUEST, a);
				path = CommandHelper.PageName.ACCOUNT;
			} else {
				request.setAttribute(CommandHelper.AttributeName.LOGIN_FAILED, true);
				path = CommandHelper.PageName.APPLICANT_LOGIN;
			}
		} catch (ServiceException ex) {
			LOGGER.error(ex);
			request.setAttribute(CommandHelper.AttributeName.ERROR, true);
		}
		helper.redirectToPage(request, response, path);

	}

}
