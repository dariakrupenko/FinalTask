package main.by.epam.admissionweb.command.impl.admin.applicant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 * Класс <code>GetApplicantCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * получение информации об абитуриенте.
 * <p>
 * Данная информация доступна только для администратора системы и содержит
 * основные сведения о выбранном абитуриенте, включая его персональные данные
 * (за исключение логина и пароля), а также статус в учебном заведении (если
 * абитуриент записан на какой-либо факультет).
 * <p>
 * Если выбранный абитуриент не найден, ответом на запрос будет пустой объект.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see Applicant
 *
 */
public class GetApplicantCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на получение
	 * сведения об абитуриенте.
	 * <p>
	 * В процессе своей работы метод обращается к сервису
	 * {@link ApplicantService}. Если клиент не является администратором
	 * системы, запрос будет перенаправлен на страницу авторизации
	 * администратора.
	 * <p>
	 * В результате работы метод устанавливает в контекст запроса объект
	 * абитуриента. Если в процессе обработки возникла ошибка, обусловленная
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
	 * @see ApplicantService
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : GetApplicantCommand");
		CommandHelper helper = CommandHelper.getInstance();
		String path = null;
		Object adminObj = request.getSession(true).getAttribute(CommandHelper.AttributeName.ADMIN);
		if (adminObj == null) {
			path = CommandHelper.PageName.ADMIN_LOGIN;
		} else {
			String idStr = request.getParameter(CommandHelper.ParameterName.APPLICANT_ID);
			int id = helper.parseId(idStr);
			ApplicantService service = ServiceFactory.getInstance().getApplicantService();
			try {
				LOGGER.debug("COMMAND : GetApplicantCommand (id = {})", id);
				Applicant a = service.getApplicant(id);
				if (a == null) {
					request.setAttribute(CommandHelper.AttributeName.NOT_FOUND, true);
				} else {
					request.setAttribute(CommandHelper.AttributeName.APPLICANT_TO_REQUEST, a);
				}
			} catch (ServiceException ex) {
				LOGGER.error(ex);
				request.setAttribute(CommandHelper.AttributeName.ERROR, true);
			}
			path = CommandHelper.PageName.APPLICANT;
		}
		helper.redirectToPage(request, response, path);
	}
}
