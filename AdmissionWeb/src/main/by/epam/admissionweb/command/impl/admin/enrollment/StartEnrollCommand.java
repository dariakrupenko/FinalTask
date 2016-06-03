package main.by.epam.admissionweb.command.impl.admin.enrollment;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandHelper;
import main.by.epam.admissionweb.entity.Enroll;
import main.by.epam.admissionweb.service.EnrollmentService;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>StartEnrollCommand</code> является реализацией интерфейса
 * {@link Command} и инкапсулирует поведение команды по обработке запроса на
 * добавление открытия нового набора абитуриентов в учебное заведение.
 * <p>
 * Данное действие доступно только для администратора системы.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see Enroll
 *
 */
public class StartEnrollCommand implements Command {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Метод описывает поведение команды по обработке запроса на открытие нового
	 * набора абитуриентов в учебное заведение.
	 * <p>
	 * Метод поддерживает защиту от обработки запросов с одинаковыми ключами. В
	 * процессе работы метод обращается к сервису {@link EnrollmentService}.
	 * Если открытие нового набора запрещено логикой приложения, в контекст
	 * запроса устаналивается соответсвующий атрибут, если открытие произошло
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
	 * @see EnrollmentService
	 * @see CommandHelper
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
		LOGGER.debug("COMMAND : StartEnrollCommand");
		CommandHelper helper = CommandHelper.getInstance();
		String path = null;
		Object adminObj = request.getSession(true).getAttribute(CommandHelper.AttributeName.ADMIN);
		if (adminObj == null) {
			path = CommandHelper.PageName.ADMIN_LOGIN;
		} else {
			if (helper.isAdditionEnabled(CommandHelper.AttributeName.ENROLL_KEY, request)) {
				try {
					EnrollmentService service = ServiceFactory.getInstance().getEnrollmentService();
					boolean isCurrentEnroll = service.isCurrentEnroll();
					if (isCurrentEnroll) {
						request.setAttribute(CommandHelper.AttributeName.IS_CURRENT_ENROLL, true);
					} else {
						String bDateStr = request.getParameter(CommandHelper.ParameterName.BEGIN_DATE);
						Date bDate = helper.parseDate(bDateStr);
						String eDateStr = request.getParameter(CommandHelper.ParameterName.END_DATE);
						Date eDate = helper.parseDate(eDateStr);
						Enroll e = helper.constructEnroll(0, bDate, eDate, true);
						LOGGER.debug("COMMAND : StartEnrollCommand (e = {})", e);
						Enroll eNew = service.startEnroll(e);
						if (eNew == null) {
							request.setAttribute(CommandHelper.AttributeName.NOT_VALID, true);
						} else {
							e = eNew;
							request.setAttribute(CommandHelper.AttributeName.SUCCESS_STARTED, true);
						}
						request.setAttribute(CommandHelper.AttributeName.BEGIN_DATE, bDate);
						request.setAttribute(CommandHelper.AttributeName.ENROLL, e);
					}
				} catch (ServiceException ex) {
					LOGGER.error(ex);
					request.setAttribute(CommandHelper.AttributeName.ERROR, true);
				} finally {
					request.getServletContext().removeAttribute(CommandHelper.AttributeName.ENROLL_KEY);
				}
			}
			path = CommandHelper.PageName.START_ENROLL;
		}
		helper.redirectToPage(request, response, path);
	}

	

}
