package main.by.epam.admissionweb.dao.exception;

/**
 * Класс <code>TransactionException</code> является подклассом класса
 * {@link DAOException}, который в свою очередь является подклассом класса
 * <code>Exception</code>. Таким образом, <code>TransactionException</code>
 * является проверяемым исключением.
 * <p>
 * <code>TransactionException</code> сигнализирует об ошибке в результате работы
 * с транзакциями базы данных, а именно ошибка создания транзакции, ее
 * завершения или отката.
 * <p>
 * Как правило, <code>TransactionException</code> не является результатом ошибки
 * взаимодействия непосредственно с данными базы данных.
 * 
 * @author Daria Krupenko
 * @see DAOException
 *
 */
public class TransactionException extends DAOException {

	private static final long serialVersionUID = 1L;

	/**
	 * Конструирует <code>TransactionException</code>, причиной которого
	 * является иное исключение, указанное параметром <code>ex</code>. Таким
	 * образом, создаваемый объект является оберткой для иного исключения.
	 * 
	 * @param ex
	 *            истинная причина исключения
	 */
	public TransactionException(Throwable ex) {
		super(ex);
	}

	/**
	 * Конструирует <code>TransactionException</code> с указанным сообщением об
	 * ошибке <code>message</code>, причиной которой является иное исключение
	 * <code>ex</code>.
	 * 
	 * @param message
	 *            сообщение об ошибке
	 * @param ex
	 *            истинная причина исключения
	 */
	public TransactionException(String message, Throwable ex) {
		super(message, ex);
	}
}
