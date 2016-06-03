package main.by.epam.admissionweb.controller;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.CommandFactory;
import main.by.epam.admissionweb.command.CommandInitializer;
import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.CommandInitializerXML;

/**
 * Класс <code>Controller</code> является подклассом класса
 * <code>HttpServlet</code> и предназначен для получения запросов клиента
 * методами GET и POST, и перенаправления этих запросов объектам классов,
 * реализующих интерфейс {@link Command}, на дальнейшую обработку.
 * <p>
 * Для получения доступа к командам используется объект фабрики
 * {@link CommandFactory}, а для инициализации команд - объект класса
 * {@link CommandInitializerXML}, который инициализирует команды через
 * XML-документ и, в свою очередь, реализует интерфейс
 * {@link CommandInitializer}.
 * 
 * @author Daria Krupenko
 * @see CommandFactory
 * @see CommandInitializer
 * @see CommandInitializerXML
 * @see Command
 * @see HttpServlet
 *
 */
public class Controller extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Имя параметра HTTP-запроса, значением которого является название команды
	 */
	private static final String COMMAND_NAME = "command";

	/**
	 * Объект фабрики {@link CommandFactory} для доступа к объектам команд
	 */
	private final CommandFactory commandFactory = CommandFactory.getInstance();

	/**
	 * Создание объекта класса <code>Controller</code> (вызов конструктора
	 * суперкласса)
	 */
	public Controller() {
		super();
	}

	/**
	 * Инициализация контроллера и команд с использованием инициализатора на
	 * основе XML-документа
	 * 
	 * @param config
	 *            объект, содержащий необходимую информацию для инициализации
	 *            сервлета, далее передается методу <code>init</code>
	 *            суперкласса.
	 * @throws ServletException
	 *             ошибка при инициализации сервлета или команд
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		LOGGER.debug("CONTROLLER : Controller.init()");
		try {
			super.init(config);
			CommandInitializer initializer = new CommandInitializerXML(this.getServletContext());
			commandFactory.setCommandInitializer(initializer);
		} catch (CommandException ex) {
			LOGGER.error(ex);
			throw new ServletException("CONTROLLER : Unable to initialize controller servlet", ex);
		}
	}

	/**
	 * Обработка HTTP-запросов GET
	 * <p>
	 * Данный метод делегирует дальнейшую обработку методу
	 * <code>processRequest</code>
	 * 
	 * @param request
	 *            контекст запроса клиента
	 * @param response
	 *            контекст ответа на HTTP-запрос
	 * @throws ServletException
	 *             ошибка при обработке запроса
	 * @throws IOException
	 *             ошибка при обработке запроса
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Обработка HTTP-запросов POST
	 * <p>
	 * Данный метод делегирует дальнейшую обработку методу
	 * <code>processRequest</code>
	 * 
	 * @param request
	 *            контекст запроса клиента
	 * @param response
	 *            контекст ответа на HTTP-запрос
	 * @throws ServletException
	 *             ошибка при обработке запроса
	 * @throws IOException
	 *             ошибка при обработке запроса
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Обработка HTTP-запросов GET и POST
	 * <p>
	 * Данный метод извлекает название команды из запроса и получает объект
	 * команды через объект фабрики {@link CommandFactory}. Если команда не
	 * найдена, выполняется команда по умолчанию.
	 * 
	 * @param request
	 *            контекст запроса клиента
	 * @param response
	 *            контекст ответа на HTTP-запрос
	 * @throws ServletException
	 *             при выполнении команды произошла ошибка
	 */
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			String commandName = request.getParameter(COMMAND_NAME);
			LOGGER.info("CONTROLLER : {}", commandName);
			if (commandName == null || commandName.isEmpty()) {
				commandName = "unknown-command";
			}
			Command command = commandFactory.getCommand(commandName);
			command.execute(request, response);
		} catch (CommandException ex) {
			LOGGER.error(ex);
			throw new ServletException(ex);
		}
	}
}
