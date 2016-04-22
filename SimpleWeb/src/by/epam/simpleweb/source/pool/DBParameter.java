package by.epam.simpleweb.source.pool;

/**
 * Класс DBParameter содержит константы, описывающие названия свойств из файла
 * свойств, в котором содержатся параметры для подключения к базе данных
 * 
 * @author User
 *
 */
public final class DBParameter {

	public static final String DB_DRIVER = "db.driver";
	public static final String DB_URL = "db.url";
	public static final String DB_USER = "db.user";
	public static final String DB_PASSWORD = "db.password";
	public static final String DB_POOL_SIZE = "db.poolsize";

	private DBParameter() {
	}

}
