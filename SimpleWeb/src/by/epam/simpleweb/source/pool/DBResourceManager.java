package by.epam.simpleweb.source.pool;

import java.util.ResourceBundle;

/**
 * Класс DBResourceManager предназначен для получения параметров соединения из
 * файла свойств
 * 
 * @author User
 *
 */
public class DBResourceManager {

	/**
	 * Файл свойств
	 */
	private static final String DB_PROPERTIES = "by.epam.simpleweb.source.pool.db";

	private final static DBResourceManager instance = new DBResourceManager();
	private ResourceBundle bundle = ResourceBundle.getBundle(DB_PROPERTIES);

	private DBResourceManager() {
	}

	public static DBResourceManager getInstance() {
		return instance;
	}

	/**
	 * Получение свойства
	 * 
	 * @param key
	 *            название свойства
	 * @return значение свойства
	 */
	public String getValue(String key) {
		return bundle.getString(key);
	}

}
