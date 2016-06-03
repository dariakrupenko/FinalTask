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
 * Класс <code>RegistrateApplicantCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * регистрацию абитуриента в системе.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see Applicant
 *
 */
public class RegistrateApplicantCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на регистрацию
	 * абитуриента в системе.
	 * <p>
	 * В процессе работы метод обращается к сервису {@link ApplicantService}.
	 * Если регистрация абитуриента запрещена логикой приложения, в контекст
	 * запроса устаналивается соответсвующий атрибут, если регистрация произошла
	 * успешно - атрибут успешного завершения, причем предыдущая сессия
	 * прерывается, и в контекст новой сессии устанавливается атрибут
	 * абитуриента.
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

		LOGGER.debug("COMMAND : RegistrateApplicantCommand");
		CommandHelper helper = CommandHelper.getInstance();
		if (helper.isAdditionEnabled(CommandHelper.AttributeName.APPLICANT_KEY, request)) {
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
				Applicant a = helper.constructApplicant(0, login, password, name, email, phone, address, bDate, school,
						year, null);
				LOGGER.debug("COMMAND : RegistrateApplicantCommand (a = {})", a);
				ApplicantService service = ServiceFactory.getInstance().getApplicantService();
				try {
					Applicant aNew = service.registrateApplicant(a);
					if (aNew == null) {
						request.setAttribute(CommandHelper.AttributeName.NOT_VALID, true);
					} else {
						request.setAttribute(CommandHelper.AttributeName.SUCCESS_REGISTRATED, true);
						HttpSession session = request.getSession(true);
						session.invalidate();
						a = aNew;
						request.getSession(true).setAttribute(CommandHelper.AttributeName.APPLICANT, a);
					}
				} catch (AlreadyExistsException ex) {
					LOGGER.debug(ex);
					request.setAttribute(CommandHelper.AttributeName.ALREADY_EXISTS, true);
				} finally {
					request.getServletContext().removeAttribute(CommandHelper.AttributeName.APPLICANT_KEY);
				}
				request.setAttribute(CommandHelper.AttributeName.APPLICANT_TO_REQUEST, a);
			} catch (ServiceException ex) {
				LOGGER.error(ex);
				request.setAttribute(CommandHelper.AttributeName.ERROR, true);
			}
		}
		helper.redirectToPage(request, response, CommandHelper.PageName.APPLICANT_REGISTRATION);
	}
}
