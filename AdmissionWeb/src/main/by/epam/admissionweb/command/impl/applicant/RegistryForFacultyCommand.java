package main.by.epam.admissionweb.command.impl.applicant;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;
import main.by.epam.admissionweb.command.impl.general.ToRegistryForFacultyPageCommand;
import main.by.epam.admissionweb.entity.Applicant;
import main.by.epam.admissionweb.entity.Discipline;
import main.by.epam.admissionweb.entity.Faculty;
import main.by.epam.admissionweb.entity.RegisterRecord;
import main.by.epam.admissionweb.service.RegisterService;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>RegistryForFacultyCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * запись абитуриента на факультет.
 * <p>
 * Данное действие доступно только для абитуриента.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see Applicant
 * @see RegisterRecord
 *
 */
public class RegistryForFacultyCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на запись абитуриента на факультет.
	 * <p>
	 * В процессе работы метод обращается к сервисам {@link RegisterService},
	 * {@link FacultyService} и {@link ApplicantService}.
	 * Если запись на факультет запрещена логикой приложения, в контекст запроса
	 * устанавливаетя соответствующий атрибут, если запись произошла успешно -
	 * атрибут успешного завершения. Далее управление передается команде
	 * {@link ToRegistryForFacultyPageCommand}.
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
	 * @see FacultyService
	 * @see ApplicantService
	 * @see ToRegistryForFacultyPageCommand
	 * @see CommandHelper
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : RegistryForFacultyCommand");
		CommandHelper helper = CommandHelper.getInstance();
		try {
			if (helper.isAdditionEnabled(CommandHelper.AttributeName.RECORD_KEY, request)) {
				Object applicantObj = request.getSession(true).getAttribute(CommandHelper.AttributeName.APPLICANT);
				if (applicantObj != null) {
					Applicant a = null;
					if (applicantObj instanceof Applicant) {
						a = (Applicant) applicantObj;
					}
					int fId = helper.parseId(request.getParameter(CommandHelper.ParameterName.FACULTY_ID));
					Faculty f = ServiceFactory.getInstance().getFacultyService().getFaculty(fId);

					LOGGER.debug("f = {}", f);
					Map<Discipline, Integer> scores = helper.parseScores(request);
					int certScore = Integer.parseInt(request.getParameter(CommandHelper.ParameterName.CERTIFICATE));
					RegisterRecord r = helper.constructRecord(a, f, scores, certScore, 0, null);
					LOGGER.debug("COMMAND : RegistryForFacultyCommand (r = {})", r);
					RegisterService service = ServiceFactory.getInstance().getRegisterService();
					RegisterRecord rNew = service.registryApplicant(r);
					if (rNew == null) {
						request.setAttribute(CommandHelper.AttributeName.NOT_VALID, true);
					} else {
						r = rNew;
						request.setAttribute(CommandHelper.AttributeName.SUCCESS_REGISTERED, true);
						Applicant aNew = ServiceFactory.getInstance().getApplicantService().getApplicant(a.getId());
						request.getSession(true).setAttribute(CommandHelper.AttributeName.APPLICANT, aNew);
					}
					request.setAttribute(CommandHelper.AttributeName.SCORES, r.getScores());
					request.setAttribute(CommandHelper.AttributeName.CERTIFICATE, r.getCertificateScore());
				}
			}
			Command command = new ToRegistryForFacultyPageCommand();
			command.execute(request, response);
		} catch (ServiceException ex) {
			LOGGER.error(ex);
			request.setAttribute(CommandHelper.AttributeName.ERROR, true);
			helper.redirectToPage(request, response, CommandHelper.PageName.REGISTRY_FOR_FACULTY);
		} finally {
			request.getServletContext().removeAttribute(CommandHelper.AttributeName.RECORD_KEY);
		}
	}

	
}
