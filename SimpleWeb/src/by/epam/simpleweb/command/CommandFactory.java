package by.epam.simpleweb.command;

import java.io.InputStream;
import java.util.Map;

import by.epam.simpleweb.command.exception.CommandException;
import by.epam.simpleweb.command.util.CommandUtil;

/**
 * Класс CommandFactory представляет собой фабрику для получения нужной команды
 * 
 * @author User
 *
 */
public class CommandFactory {

	/**
	 * Объект фабрики
	 */
	private static final CommandFactory instance = new CommandFactory();

	/**
	 * Хранилище команд (Map: key - строковое название команды, value -
	 * соответствующий объект класса, реализующего интерфейс Command)
	 */
	private Map<String, Command> commandMap;

	private CommandFactory() {
	}

	/**
	 * Возвращает объект фабрики
	 * 
	 * @return объект фабрики
	 */
	public static CommandFactory getInstance() {
		return instance;
	}

	/**
	 * Инициализация хранилища команд
	 * 
	 * @param stream
	 *            объект класса InputStream, связанный с xml-файлом команд
	 * @throws CommandException
	 *             - ошибка при инициализации команд
	 */
	public void initCommands(InputStream stream) throws CommandException {
		commandMap = CommandUtil.initCommands(stream);
	}

	/**
	 * Получение команды по ее строковому названию
	 * 
	 * @param key
	 *            строковое название команды
	 * @return объект Command (если команда неизвестна, возвращается объект
	 *         класса UnknownCommand)
	 */
	public Command getCommand(String key) {
		Command command = commandMap.get(key);
		if (command == null) {
			command = commandMap.get("unknown-command");
		}
		return command;
	}

}
