package main.by.epam.admissionweb.command.impl.general;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;
import main.by.epam.admissionweb.entity.Applicant;
import main.by.epam.admissionweb.entity.Faculty;
import main.by.epam.admissionweb.service.FacultyService;
import main.by.epam.admissionweb.service.RegisterService;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>ToRegistryForFacultyPageCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * перенаправление запроса абитуриента на страницу записи на факультет.
 * 
 * @author Daria Krupenko
 *
 */
public class ToRegistryForFacultyPageCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на перенаправление
	 * запроса абитуриента на страницу записи на факультет.
	 * <p>
	 * Метод обращается к сервису {@link RegisterService} для получения сведений
	 * о статусе абитуриента в учебном заведении, а также о состоянии набора в целом,
	 * чтобы установить соответствующие атрибуты в контекст запроса.
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
		LOGGER.debug("COMMAND : ToRegistryForFacultyCommand");
		CommandHelper helper = CommandHelper.getInstance();
		Object applicantObj = request.getSession(true).getAttribute(CommandHelper.AttributeName.APPLICANT);
		Object successAttr = request.getAttribute(CommandHelper.AttributeName.SUCCESS_REGISTERED);
		if (applicantObj != null) {
			try {
				if (successAttr == null) {
					RegisterService service = ServiceFactory.getInstance().getRegisterService();
					boolean isRegistryEnabled = service.isRegistryEnabled();
					if (!isRegistryEnabled) {
						request.setAttribute(CommandHelper.AttributeName.NO_CURRENT_ENROLL, true);
					} else {
						Applicant a = null;
						if (applicantObj instanceof Applicant) {
							a = (Applicant) applicantObj;
						}
						boolean isApplicantRegistered = service.isApplicantRegistered(a);
						if (isApplicantRegistered) {
							request.setAttribute(CommandHelper.AttributeName.REGISTERED, true);
						} else {
							int id = helper.parseId(request.getParameter(CommandHelper.ParameterName.FACULTY_ID));
							FacultyService fService = ServiceFactory.getInstance().getFacultyService();
							Faculty f = fService.getFaculty(id);
							if (f != null) {
								request.setAttribute(CommandHelper.AttributeName.FACULTY, f);
							} else {
								request.setAttribute(CommandHelper.AttributeName.ERROR, true);
							}
						}
					}
				}
			} catch (ServiceException ex) {
				LOGGER.error(ex);
				request.setAttribute(CommandHelper.AttributeName.ERROR, true);
			}
		} else {
			request.setAttribute(CommandHelper.AttributeName.UNREGISTRATED, true);
		}
		helper.redirectToPage(request, response, CommandHelper.PageName.REGISTRY_FOR_FACULTY);
	}

}
