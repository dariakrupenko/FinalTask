package main.by.epam.admissionweb.dao.pool.exception;

/**
 * Класс <code>ConnectionPoolException</code> является подклассом класса
 * <code>Exception</code> и описывает исключение, которое сигнализирует
 * об ошибке при работе с пулом соединения с базой данных.
 * 
 * @author Daria Krupenko
 *
 */
public class ConnectionPoolException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Конструирует <code>ConnectionPoolException</code>, причиной которого является
	 * иное исключение, указанное параметром <code>ex</code>. Таким образом,
	 * создаваемый объект является оберткой для иного исключения.
	 * 
	 * @param ex
	 *            истинная причина исключения
	 */
	public ConnectionPoolException(Throwable ex) {
		super(ex);
	}

	/**
	 * Конструирует <code>ConnectionPoolException</code> с указанным сообщением об
	 * ошибке <code>message</code>, причиной которой является иное исключение
	 * <code>ex</code>.
	 * 
	 * @param message
	 *            сообщение об ошибке
	 * @param ex
	 *            истинная причина исключения
	 */
	public ConnectionPoolException(String message, Throwable ex) {
		super(message, ex);
	}

}
