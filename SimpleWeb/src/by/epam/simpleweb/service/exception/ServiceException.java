package by.epam.simpleweb.service.exception;

/**
 * Класс ServiceException описывает исключение, которое возникает при работе
 * какого-либа сервиса
 * 
 * @author User
 *
 */
public class ServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public ServiceException(Throwable ex) {
		super(ex);
	}

	public ServiceException(String message, Throwable ex) {
		super(message, ex);
	}

}
