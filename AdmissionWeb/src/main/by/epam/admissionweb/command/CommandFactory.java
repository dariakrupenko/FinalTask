package main.by.epam.admissionweb.command;

import java.util.HashMap;
import java.util.Map;

import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.command.impl.general.UnknownCommand;

/**
 * Класс <code>CommandFactory</code> представляет собой фабрику для получения
 * объекта конкретной команды по обработке HTTP-запроса.
 * <p>
 * Для получения фабрики необходимо вызвать статический метод
 * <code>getInstance()</code> данного класса.
 * <p>
 * Объекты команд являются реализациями интерфейса {@link Command}.
 * <p>
 * Хранилищем команд является карта (<code>Map</code>), где ключ представлен
 * названием команды, а значение - соответствующим классом, реализующим
 * интерфейс {@link Command}. Для инициализации команд используется объект
 * класса, реализующего интерфейс {@link CommandInitializer}.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see CommandInitializer
 *
 */
public class CommandFactory {

	/**
	 * Хранилище команд - карта (ключ - название команды (объект класса
	 * <code>String</code>), значение - команда (объект класса, реализующего
	 * интерфейс {@link Command})). Изначально хранилище не содержит ни одной
	 * команды.
	 */
	private Map<String, Command> commandMap = new HashMap<String, Command>();

	/**
	 * Объект фабрики. Создается один раз при загрузке класса в память. Далее
	 * объект фабрики можно получить, вызвав статический метод
	 * <code>getInstance()</code> данного класса.
	 */
	private static final CommandFactory INSTANCE = new CommandFactory();

	/**
	 * Конструктор для создания объекта фабрики. Может быть вызван только из
	 * данного класса.
	 */
	private CommandFactory() {
	}

	/**
	 * Статический метод <code>getInstance()</code> предназначен для получения
	 * объекта фабрики с целью получения доступа к объектам команд.
	 * 
	 * @return объект фабрики для получения команд
	 */
	public static final CommandFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * Метод <code>setCommandInitializer()</code> вызывается при инициализации
	 * хранилища команд.
	 * <p>
	 * Параметр <code>initializer</code> является объектом класса, реализующего
	 * интерфейс <code>CommandInitializer</code>. Посредством данного объекта
	 * инициализируется хранилище команд.
	 * 
	 * @param initializer
	 *            инициализатор хранилища команд
	 * @throws CommandException
	 *             если в процессе инициализации хранилища команд возникла
	 *             ошибка; хранилище команд останется пустым.
	 * @throws NullPointerException
	 *             если параметр <code>initializer</code> равен null.
	 */
	public void setCommandInitializer(CommandInitializer initializer) throws CommandException {
		commandMap = initializer.initCommands();
	}

	/**
	 * Метод <code>getCommand()</code> возвращает объект класса, реализуюещего
	 * интерфейс {@link Command}.
	 * <p>
	 * Если хранилище команд не было проинициализировано вызовом метода
	 * <code>setCommandInitializer()</code> или параметр <code>key</code> равен
	 * null, метод вернет команду по умолчанию (объект класса
	 * {@link UnknownCommand}).
	 * 
	 * @param key
	 *            название команды (регистр не учитывается)
	 * @return объект класса, реализующего интерфейс {@link Command}.
	 */
	public Command getCommand(String key) {
		Command command = commandMap.get(key.toLowerCase());
		if (command == null) {
			command = new UnknownCommand();
		}
		return command;
	}

}
