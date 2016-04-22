package by.epam.simpleweb.command.exception;

/**
 * Класс CommandException представляет собой исключение, которое возникает в
 * процессе выполнения команды
 * 
 * @author User
 *
 */
public class CommandException extends Exception {

	private static final long serialVersionUID = 1L;

	public CommandException(Throwable ex) {
		super(ex);
	}

	public CommandException(String message, Throwable ex) {
		super(message, ex);
	}
}
