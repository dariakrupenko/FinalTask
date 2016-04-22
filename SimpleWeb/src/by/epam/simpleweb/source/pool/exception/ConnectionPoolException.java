package by.epam.simpleweb.source.pool.exception;

/**
 * Класс ConnectionPoolException описывает исключение, которое может возникнуть
 * в процессе работы с пулом соединений
 * 
 * @author User
 *
 */
public class ConnectionPoolException extends Exception {

	private static final long serialVersionUID = 1L;

	public ConnectionPoolException(Throwable ex) {
		super(ex);
	}

	public ConnectionPoolException(String message, Throwable ex) {
		super(message, ex);
	}

}
