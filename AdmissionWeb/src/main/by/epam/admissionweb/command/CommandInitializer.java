package main.by.epam.admissionweb.command;

import java.util.Map;

import main.by.epam.admissionweb.command.exception.CommandException;

/**
 * Интерфейс <code>CommandInitializer</code> реализуется классами,
 * предназначенными для инициализации хранилища команд, которое представляет
 * собой карту (<code>Map</code>), где ключ - название команды, а значение -
 * объект команды (объект класса, реализующего интерфейс {@link Command}).
 * <p>
 * Интерфейс <code>CommandInitializer</code> описывает единственный метод
 * <code>initCommands()</code>. Классы, реализуя интерфейс
 * <code>CommandInitializer</code>, описывают алгоритм инициализации хранилища
 * команд в методе <code>initCommands()</code>.
 * 
 * @author Daria Krupenko
 * @see Command
 *
 */
public interface CommandInitializer {

	/**
	 * Метод <code>initCommands()</code> описывает алгоритм инициализации
	 * хранилища команд - карты, где ключ - название команды, а значение -
	 * объект команды (объект класса, реализующего интерфейс {@link Command}).
	 * 
	 * @return объект класса, реализующий интерфейс <code>Map</code> - хранилище
	 *         команд.
	 * @throws CommandException
	 *             если в процессе инициализации хранилища команд произошла
	 *             ошибка.
	 */
	public Map<String, Command> initCommands() throws CommandException;

}
