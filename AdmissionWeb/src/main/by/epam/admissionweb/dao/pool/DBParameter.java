package main.by.epam.admissionweb.dao.pool;

/**
 * Класс <code>DBParameter</code> содержит строковые константы, представляющие
 * собой названия свойств соединения с базой данных.
 * <p>
 * Данные свойства сохраняются в файле свойств.
 * <p>
 * Класс <code>DBParameter</code> не может быть наследован
 * 
 * @author Daria Krupenko
 *
 */
public final class DBParameter {

	/**
	 * Имя свойства драйвера базы данных
	 */
	public static final String DB_DRIVER = "db.driver";
	
	/**
	 * Имя свойства URL (расположения) базы данных
	 */
	public static final String DB_URL = "db.url";
	
	/**
	 * Имя свойства пользователя базы данных
	 */
	public static final String DB_USER = "db.user";
	
	/**
	 * Имя свойства пароля для доступа к базе данных
	 */
	public static final String DB_PASSWORD = "db.password";
	
	/**
	 * Имя свойства количество соединений в пуле соединений
	 */
	public static final String DB_POOL_SIZE = "db.poolsize";
	
	/**
	 * Имя свойства кодировки для драйвера базы данных
	 */
	public static final String DB_ENCODING = "db.encoding";

	private DBParameter() {
	}

}
