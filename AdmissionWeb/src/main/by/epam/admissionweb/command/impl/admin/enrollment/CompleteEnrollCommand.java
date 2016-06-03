package main.by.epam.admissionweb.command.impl.admin.enrollment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;
import main.by.epam.admissionweb.service.EnrollmentService;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>CompleteEnrollCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * завершение набора абитуриентов в учебное заведение.
 * <p>
 * Данное действие доступно только для администратора системы.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see Enroll
 *
 */
public class CompleteEnrollCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на завершение
	 * набора абитуриентов в учебное заведение.
	 * <p>
	 * В процессе работы метод обращается к сервису {@link EnrollmentService}.
	 * Если завершение набора запрещено логикой приложения, в контекст запроса
	 * устаналивается соответсвующий атрибут, если завершение произошло успешно
	 * - атрибут успешного завершения. Далее управление передается команде
	 * {@link GetEnrollsListCommand}.
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
	 * @see EnrollmentService
	 * @see GetEnrollsListCommand
	 * @see CommandHelper
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : CompleteEnrollCommand");
		CommandHelper helper = CommandHelper.getInstance();
		Object adminObj = request.getSession(true).getAttribute(CommandHelper.AttributeName.ADMIN);
		if (adminObj == null) {
			helper.redirectToPage(request, response, CommandHelper.PageName.ADMIN_LOGIN);
		} else {
			try {
				EnrollmentService service = ServiceFactory.getInstance().getEnrollmentService();
				boolean isCurrentEnroll = service.isCurrentEnroll();
				if (!isCurrentEnroll) {
					request.setAttribute(CommandHelper.AttributeName.NO_CURRENT_ENROLL, true);
				} else {
					service.completeCurrentEnroll();
					request.setAttribute(CommandHelper.AttributeName.SUCCESS_COMPLETED, true);
				}
				Command command = new GetEnrollsListCommand();
				command.execute(request, response);
			} catch (ServiceException ex) {
				LOGGER.error(ex);
				request.setAttribute(CommandHelper.AttributeName.ERROR, true);
				helper.redirectToPage(request, response, CommandHelper.PageName.ENROLLS_LIST);
			}
		}
	}

}
