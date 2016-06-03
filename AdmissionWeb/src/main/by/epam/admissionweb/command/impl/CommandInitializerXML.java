package main.by.epam.admissionweb.command.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import main.by.epam.admissionweb.command.Command;
import main.by.epam.admissionweb.command.CommandInitializer;
import main.by.epam.admissionweb.command.exception.CommandException;

/**
 * Класс <code>CommandInitializerXML</code> является конкретной реализацией
 * интерфейса {@link CommandInitializer} и инкапсулирует алгоритм инициализации
 * хранилища (карты) команд посредством анализа XML-документа.
 * <p>
 * Для доступа к XML-документу используется контекст приложения (объект класса,
 * реализующего интерфейс <code>ServletContext</code>).
 * <p>
 * При анализе XML-документа применяется DOMParser.
 * 
 * @author Daria Krupenko
 * @see CommandInitializer
 * @see Command
 * @see ServletContext
 * @see DOMParser
 * 
 */
public class CommandInitializerXML implements CommandInitializer {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Название атрибута контекста приложения, значением которого является путь
	 * к XML-документу с данными о командах
	 */
	private static final String COMMANDS_FILE_NAME = "commands-file";

	/**
	 * Элемент XML-документа, содержимым которого является название команды и
	 * название класса, который инкапсулирует поведение указанной команды
	 */
	private static final String COMMAND_TAG = "command";

	/**
	 * Элемент XML-документа, содержимым которого является название-команды
	 */
	private static final String COMMAND_NAME_TAG = "command-name";

	/**
	 * Элемент XML-документа, содержимым которого является название класса
	 * команды
	 */
	private static final String COMMAND_CLASS_TAG = "command-class";

	/**
	 * Поток ввода, связанный с XML-документом
	 */
	private InputStream commandsStream;

	/**
	 * Конструирует объект инициализатора хранилища команд на основе
	 * XML-документа.
	 * <p>
	 * Для получения потока ввода, связанного с XML-документом, используется
	 * контекст приложения (объект класса, реализующего интерфейс
	 * <code>ServletContext</code>) и его атрибут (значение - путь к
	 * XML-документу).
	 * 
	 * @param context
	 *            контекст приложения
	 * @throws CommandException
	 *             если в контексте приложения отсутствует искомый атрибут или
	 *             не удается получить доступ к XML-документу
	 * @throws NullPointerException
	 *             если параметр <code>context</code> равен null
	 */
	public CommandInitializerXML(ServletContext context) throws CommandException {
		String commandsString = context.getInitParameter(COMMANDS_FILE_NAME);
		if (commandsString == null || commandsString.isEmpty()) {
			throw new CommandException("Commands-file attribute is not found", null);
		}
		commandsStream = context.getResourceAsStream(commandsString);
		if (commandsStream == null) {
			throw new CommandException("Commands-file is not found", null);
		}
	}

	/**
	 * Инициализирует хранилище команд - карту, где ключ - название команды, а
	 * значение - объект класса, реализующего интерфейс {@link Command}.
	 * <p>
	 * Название команды и название класса команды извлекаются путем анализа
	 * XML-документа с использованием DOM-парсера.
	 * 
	 * @return хранилище команд
	 * @throws CommandException
	 *             если произошла ошибка в процессе парсинга XML-файла; если не
	 *             удается найти указанный класс, получить к нему доступ или
	 *             создать новый объект этого класса.
	 */
	public Map<String, Command> initCommands() throws CommandException {
		LOGGER.debug("COMMAND : CommandInitializerXML.initCommands()");
		Map<String, Command> commandMap = new HashMap<>();
		try {
			InputSource source = new InputSource(commandsStream);
			DOMParser parser = new DOMParser();
			parser.parse(source);
			Document document = parser.getDocument();
			Element root = document.getDocumentElement();
			NodeList commandNodes = root.getElementsByTagName(COMMAND_TAG);
			for (int i = 0; i < commandNodes.getLength(); i++) {
				Element commandNode = (Element) commandNodes.item(i);
				String commandName = getSingleChild(commandNode, COMMAND_NAME_TAG).getTextContent().trim();
				String commandClass = getSingleChild(commandNode, COMMAND_CLASS_TAG).getTextContent().trim();
				Command command = (Command) Class.forName(commandClass).newInstance();
				commandMap.put(commandName, command);
			}
			return commandMap;
		} catch (IOException | ClassNotFoundException | SAXException | IllegalAccessException
				| InstantiationException ex) {
			throw new CommandException("COMMAND : Unable to initialize commands", ex);
		}
	}

	/**
	 * Поиск дочернего элемента для элемента, указанного параметром
	 * <code>element</code>, с именем, указанном в параметре
	 * <code>childName</code>.
	 * <p>
	 * Используется для извлечения названия команды и названия класса команды
	 * 
	 * @param element
	 *            родительский элемент
	 * @param childName
	 *            название искомого дочернего элемента
	 * @return дочерний элемент
	 */
	private Element getSingleChild(Element element, String childName) {
		NodeList nodes = element.getElementsByTagName(childName);
		return (Element) nodes.item(0);
	}

}
