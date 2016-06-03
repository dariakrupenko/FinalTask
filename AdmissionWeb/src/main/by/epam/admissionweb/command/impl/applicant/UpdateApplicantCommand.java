package main.by.epam.admissionweb.command.impl.applicant;

import java.util.Date;

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
import main.by.epam.admissionweb.service.exception.AlreadyExistsException;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>UpdateApplicantCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * обновление личной информации абитуриента.
 * <p>
 * Данное действие доступно только для абитуриента.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see Applicant
 *
 */
public class UpdateApplicantCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на обновление
	 * личной информации абитуриента.
	 * <p>
	 * В процессе работы метод обращается к сервису {@link ApplicantService}.
	 * Если обновление информации запрещено логикой приложения, в контекст
	 * запроса устаналивается соответсвующий атрибут, если обновление произошло
	 * успешно - атрибут успешного завершения.
	 * <p>
	 * Если клиент не является абитуриентом, запрос будет
	 * перенаправлен на страницу авторизации абитуриента.
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
		LOGGER.debug("COMMAND : UpdateApplicantCommand");
		CommandHelper helper = CommandHelper.getInstance();
		HttpSession session = request.getSession(true);
		Object applicantObj = session.getAttribute(CommandHelper.AttributeName.APPLICANT);
		String path = null;
		if (applicantObj == null) {
			path = CommandHelper.PageName.APPLICANT_LOGIN;
		} else {
			int id = helper.parseId(request.getParameter(CommandHelper.ParameterName.APPLICANT_ID));
			String login = request.getParameter(CommandHelper.ParameterName.LOGIN);
			String password = request.getParameter(CommandHelper.ParameterName.PASSWORD);
			String name = request.getParameter(CommandHelper.ParameterName.NAME);
			String email = request.getParameter(CommandHelper.ParameterName.EMAIL);
			String phone = request.getParameter(CommandHelper.ParameterName.PHONE);
			String address = request.getParameter(CommandHelper.ParameterName.ADDRESS);
			String bDateStr = request.getParameter(CommandHelper.ParameterName.BIRTHDATE);
			String school = request.getParameter(CommandHelper.ParameterName.SCHOOL);
			String yearStr = request.getParameter(CommandHelper.ParameterName.GRAD_YEAR);
			Date bDate = helper.parseDate(bDateStr);
			int year = helper.parseYear(yearStr);
			try {
				Applicant a = helper.constructApplicant(id, login, password, name, email, phone, address, bDate, school,
						year, null);
				LOGGER.debug("COMMAND : UpdateApplicantCommand (a = {})", a);
				ApplicantService service = ServiceFactory.getInstance().getApplicantService();
				try {
					Applicant aNew = service.updateApplicant(a);
					if (aNew == null) {
						request.setAttribute(CommandHelper.AttributeName.NOT_VALID, true);
					} else {
						a = aNew;
						request.setAttribute(CommandHelper.AttributeName.SUCCESS_UPDATED, true);
						session.setAttribute(CommandHelper.AttributeName.APPLICANT, a);
					}
				} catch (AlreadyExistsException ex) {
					LOGGER.debug(ex);
					request.setAttribute(CommandHelper.AttributeName.ALREADY_EXISTS, true);
				}
				request.setAttribute(CommandHelper.AttributeName.APPLICANT_TO_REQUEST, a);
			} catch (ServiceException ex) {
				LOGGER.error(ex);
				request.setAttribute(CommandHelper.AttributeName.ERROR, true);
			}
			path = CommandHelper.PageName.ACCOUNT;
		}
		helper.redirectToPage(request, response, path);
	}

}
