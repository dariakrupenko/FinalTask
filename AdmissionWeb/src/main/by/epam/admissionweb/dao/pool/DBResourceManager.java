package main.by.epam.admissionweb.dao.pool;

import java.util.ResourceBundle;

/**
 * Класс <code>DBReaourceManager</code> предназначен для извлечения свойств
 * соединения с базой данных из файла свойств.
 * <p>
 * Получение объекта класса <code>DBResourceManager</code> осуществляется путем
 * вызова статического метода <code>getInstance()</code> данного класса.
 * <p>
 * При работе с файлом свойств используется объект класса
 * <code>ResourceBundle</code>. Класс <code>DBResourceManager</code>
 * предоставляет метод для получения свойства из файла свойств.
 * <p>
 * Данный класс упрощает работу пула соединений {@link ConnectionPoolImpl} при
 * извлечении необходимы свойств соединения с базой данных.
 * 
 * @author Daria Krupenko
 * @see ConnectionPoolImpl
 * @see ResourceBundle
 *
 */
public class DBResourceManager {

	/**
	 * Название файла свойств соединения с базой данных
	 */
	private static final String DB_PROPERTIES = "main.by.epam.admissionweb.dao.pool.db";

	/**
	 * Объект класса <code>DBResourceManager</code>, создается один раз при
	 * загрузке класса в память
	 */
	private final static DBResourceManager INSTANCE = new DBResourceManager();

	/**
	 * Объект <code>ResourceBundle</code> для работы с файлом свойств
	 */
	private ResourceBundle bundle = ResourceBundle.getBundle(DB_PROPERTIES);

	private DBResourceManager() {
	}

	/**
	 * Получение объекта класса <code>DBResourceManager</code>
	 * 
	 * @return объект класса <code>DBResourceManager</code>
	 */
	public static DBResourceManager getInstance() {
		return INSTANCE;
	}

	/**
	 * Получение значения свойства по указанному в параметре <code>key</code>
	 * ключу.
	 * 
	 * @param key
	 *            имя свойства
	 * @return значение свойства
	 */
	public String getValue(String key) {
		return bundle.getString(key);
	}

}
