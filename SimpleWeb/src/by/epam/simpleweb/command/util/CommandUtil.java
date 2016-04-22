package by.epam.simpleweb.command.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import by.epam.simpleweb.command.Command;
import by.epam.simpleweb.command.exception.CommandException;

/**
 * Класс CommandUtil является утилитным классом для получения хранилища (карты)
 * команд из XML-файла, где ключом является строковое название команды, а
 * значением - объект класса, реализующего интерфейс Command
 * 
 * @author User
 *
 */
public class CommandUtil {

	/**
	 * Логгер
	 */
	private static final Logger logger = LogManager.getRootLogger();

	/**
	 * Название XML-элемента команды
	 */
	private static final String COMMAND_TAG = "command";

	/**
	 * Название XML-элемента имени команды
	 */
	private static final String COMMAND_NAME_TAG = "command-name";

	/**
	 * Название XML-элемента имени класса команды
	 */
	private static final String COMMAND_CLASS_TAG = "command-class";

	/**
	 * Получение хранилища (карты) команд из XML-файла
	 * 
	 * @param stream
	 *            объект класса InputStream, связанный с XML-файлом команд
	 * @return карта команд
	 * @throws CommandException
	 *             - ошибка при чтении, анализе XML-документа или при создании
	 *             объекта класса команды
	 */
	public static Map<String, Command> initCommands(InputStream stream) throws CommandException {
		Map<String, Command> commandMap = new HashMap<>();
		logger.debug("CommandUtil : initialize commands");
		try {
			DOMParser parser = new DOMParser();
			InputSource source = new InputSource(stream);
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
			throw new CommandException("Unable to initialize commands", ex);
		}
	}

	/**
	 * Получение дочернего элемента по его имени
	 * 
	 * @param element
	 *            родительский элемент
	 * @param childName
	 *            имя дочернего элемента
	 * @return объект класса Element, представляющий дочерний элемент
	 */
	private static Element getSingleChild(Element element, String childName) {
		NodeList nodes = element.getElementsByTagName(childName);
		return (Element) nodes.item(0);
	}

}
