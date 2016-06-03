package main.by.epam.admissionweb.dao.exception;

/**
 * Класс <code>DAOException</code> является подклассом класса <code>Exception</code>
 * и описывает исключение, которое сигнализирует об ошибке чтения данных
 * из источника данных.
 * 
 * @author Daria Krupenko
 *
 */
public class DAOException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Конструирует <code>DAOException</code>, причиной которого является
	 * иное исключение, указанное параметром <code>ex</code>. Таким образом,
	 * создаваемый объект является оберткой для иного исключения.
	 * 
	 * @param ex
	 *            истинная причина исключения
	 */
	public DAOException(Throwable ex) {
		super(ex);
	}

	/**
	 * Конструирует <code>DAOException</code> с указанным сообщением об
	 * ошибке <code>message</code>, причиной которой является иное исключение
	 * <code>ex</code>.
	 * 
	 * @param message
	 *            сообщение об ошибке
	 * @param ex
	 *            истинная причина исключения
	 */
	public DAOException(String message, Throwable ex) {
		super(message, ex);
	}
}
