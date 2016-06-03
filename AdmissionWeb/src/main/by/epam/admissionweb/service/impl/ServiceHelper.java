package main.by.epam.admissionweb.service.impl;

/**
 * Класс <code>ServiceHelper</code> содержит константы, совместно использумые
 * всеми сервис-объектами.
 * 
 * @author Daria Krupenko
 *
 */
class ServiceHelper {

	/**
	 * Код транзакции по умолчанию
	 */
	static final int NO_TRANSACTION = -1;

	/**
	 * Номер требуемой страницы по умолчанию (при постраничном построении
	 * длинных списков)
	 */
	static final int REQUIRED_PAGE_DEFAULT = 0;

	/**
	 * Максимальное количество элемнтов на одной странице (при постраничном
	 * построении длинных списков)
	 */
	static final int ELEMENTS_MAX_VALUE = Integer.MAX_VALUE;

}
