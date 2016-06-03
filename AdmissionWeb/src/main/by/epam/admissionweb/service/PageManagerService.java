package main.by.epam.admissionweb.service;

/**
 * Интерфейс <code>PageManagerService</code> предоставляет методы для поддержки
 * постраничного построения длинных списков.
 * 
 * @author Daria Kupenko
 *
 */
public interface PageManagerService {

	/**
	 * Получить количество страниц в соответствии с общим количеством элементов
	 * и количеством элементов, которые должны быть отображены на одной странице
	 * 
	 * @param elementsNumber
	 *            общее количество элементов
	 * @param elementsNumberPerPage
	 *            количество элементов на одной странице
	 * @return количество страниц
	 */
	public int getPagesNumber(int elementsNumber, int elementsNumberPerPage);

	/**
	 * Получить требуемую для просмотра страницу в соответствии с
	 * просматриваемой страницей и направлением прокрутки
	 * 
	 * @param currentPage
	 *            текущая просматриваемая страница
	 * @param next
	 *            направление прокрутки
	 * @param pagesNumber
	 *            общее количество страниц
	 * @return требуемая для просмотра страница
	 */
	public int getRequiredPage(int currentPage, boolean next, int pagesNumber);

}
