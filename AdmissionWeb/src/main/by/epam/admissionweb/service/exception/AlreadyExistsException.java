package main.by.epam.admissionweb.service.exception;

/**
 * Класс <code>AlreadyExistsException</code> является подклассом класса
 * {@link ServiceException}, который в свою очередь является подклассом класса
 * <code>Exception</code>. Таким образом,
 * <code>AlreadyExistsException</code> является проверяемым
 * исключением.
 * <p>
 * <code>AlreadyExistsException</code> сигнализирует об ошибке в
 * результате попытки сохранить в источнике данных дублированные данные.
 * 
 * @author Daria Krupenko
 * @see ServiceException
 *
 */
public class AlreadyExistsException extends ServiceException {

	private static final long serialVersionUID = 1L;

	/**
	 * Конструирует <code>AlreadyExistsException</code>, причиной которого
	 * является иное исключение, указанное параметром <code>ex</code>. Таким
	 * образом, создаваемый объект является оберткой для иного исключения.
	 * 
	 * @param ex
	 *            истинная причина исключения
	 */
	public AlreadyExistsException(Throwable ex) {
		super(ex);
	}

	/**
	 * Конструирует <code>AlreadyExistsException</code> с указанным сообщением
	 * об ошибке <code>message</code>, причиной которой является иное исключение
	 * <code>ex</code>.
	 * 
	 * @param message
	 *            сообщение об ошибке
	 * @param ex
	 *            истинная причина исключения
	 */
	public AlreadyExistsException(String message, Throwable ex) {
		super(message, ex);
	}
}
