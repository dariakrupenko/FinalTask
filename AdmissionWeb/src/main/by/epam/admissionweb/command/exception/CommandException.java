package main.by.epam.admissionweb.command.exception;

/**
 * Класс <code>CommandException</code> является подклассом класса
 * <code>Exception</code> и описывает исключение, которое может быть выброшено
 * на слое команд.
 * <p>
 * Как правило, данное исключение сигнализирует об ошибке, связанной с
 * инициализацией команд или с перенаправлением HTTP-запроса клиента. Данное
 * исключение не может быть результатом ошибки, обусловленной логикой
 * приложения.
 * 
 * @author Daria Krupenko
 * @see Command
 * @see Exception
 *
 */
public class CommandException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Конструирует <code>CommandException</code>, причиной которого является
	 * иное исключение, указанное параметром <code>ex</code>. Таким образом,
	 * создаваемый объект является оберткой для иного исключения.
	 * 
	 * @param ex
	 *            истинная причина исключения
	 */
	public CommandException(Throwable ex) {
		super(ex);
	}

	/**
	 * Конструирует <code>CommandException</code> с указанным сообщением об
	 * ошибке <code>message</code>, причиной которой является иное исключение
	 * <code>ex</code>.
	 * 
	 * @param message
	 *            сообщение об ошибке
	 * @param ex
	 *            истинная причина исключения
	 */
	public CommandException(String message, Throwable ex) {
		super(message, ex);
	}
}
