package main.by.epam.admissionweb.service.exception;

/**
 * Класс <code>ServiceException</code> является подклассом класса
 * <code>Exception</code> и описывает исключение, которое сигнализирует об
 * ошибке, связанной с обработкой данных в соотвествии с логикой приложения, или
 * служит классом-оберткой для исключений чтения/записи данных из источника.
 * 
 * @author Daria Krupenko
 *
 */
public class ServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Конструирует <code>ServiceException</code>, причиной которого является
	 * иное исключение, указанное параметром <code>ex</code>. Таким образом,
	 * создаваемый объект является оберткой для иного исключения.
	 * 
	 * @param ex
	 *            истинная причина исключения
	 */
	public ServiceException(Throwable ex) {
		super(ex);
	}

	/**
	 * Конструирует <code>ServiceException</code> с указанным сообщением об
	 * ошибке <code>message</code>, причиной которой является иное исключение
	 * <code>ex</code>.
	 * 
	 * @param message
	 *            сообщение об ошибке
	 * @param ex
	 *            истинная причина исключения
	 */
	public ServiceException(String message, Throwable ex) {
		super(message, ex);
	}
}
