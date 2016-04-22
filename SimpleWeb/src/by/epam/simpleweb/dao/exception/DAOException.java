package by.epam.simpleweb.dao.exception;

public class DAOException extends Exception {

	private static final long serialVersionUID = 1L;

	public DAOException(Throwable ex) {
		super(ex);
	}

	public DAOException(String message, Throwable ex) {
		super(message, ex);
	}

}