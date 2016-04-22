package by.epam.simpleweb.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import by.epam.simpleweb.command.Command;
import by.epam.simpleweb.command.CommandFactory;
import by.epam.simpleweb.command.exception.CommandException;

/**
 * Класс Controller представляет собой сервлет, который получает запрос клиента,
 * находит соответсвующую команду, запускает ее на выполнение и осуществляет
 * перенаправление на нужную страницу
 * 
 * @author User
 *
 */
public class Controller extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Логгер
	 */
	private static final Logger logger = LogManager.getRootLogger();

	/**
	 * Имя контекстного параметра приложения, значением которого является
	 * xml-файл с описанием команд
	 */
	private static final String COMMAND_FILE_PARAM = "commands-file";

	/**
	 * Имя параметра запроса, значением которого является имя команды
	 */
	private static final String COMMAND_NAME = "command";

	/**
	 * Имя атрибута, значением которого является сообщение об ошибке (если
	 * таковая имеется)
	 */
	private static final String ERROR_MESSAGE_ATTR = "errorMessage";

	/**
	 * Фабрика для получения команды
	 */
	private final CommandFactory commandFactory = CommandFactory.getInstance();

	public Controller() {
		super();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		logger.debug("Controller : initialize");
		try {
			super.init(config);
			String commandsFilePath = getServletContext().getInitParameter(COMMAND_FILE_PARAM);
			InputStream stream = getServletContext().getResourceAsStream(commandsFilePath);
			commandFactory.initCommands(stream);
		} catch (CommandException ex) {
			logger.error(ex);
			throw new ServletException("Unable to initialize controller servlet", ex);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Метод processRequest() обрабатывает запросы DO и POST клиента
	 * 
	 * @param request
	 *            - контекст запроса
	 * @param response
	 *            - контекст ответа
	 * @throws ServletException
	 *             - ошибка при перенаправлении запроса
	 * @throws IOException
	 *             - ошибка при перенаправлении запроса
	 */
	private void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.debug("Controller : process request");
		String pagePath = null;
		try {
			String commandName = null;
			Command command = null;
			commandName = request.getParameter(COMMAND_NAME);
			command = commandFactory.getCommand(commandName);
			pagePath = command.execute(request);
		} catch (CommandException ex) {
			logger.error(ex);
			request.setAttribute(ERROR_MESSAGE_ATTR, ex.getMessage());
			pagePath = PageName.ERROR_PAGE;
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher(pagePath);
		if (dispatcher != null) {
			dispatcher.forward(request, response);
		} else {
			throw new ServletException("Unable to get request dispatcher");
		}
	}

}
