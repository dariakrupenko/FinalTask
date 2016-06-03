package main.by.epam.admissionweb.service;

import java.util.List;

import main.by.epam.admissionweb.entity.Faculty;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Интерфейс <code>FacultyService</code> предоставляет основные методы для
 * обработки данных о факультетах учебного заведения.
 * <p>
 * Реализации интерфейса <code>FacultyService</code> инкапсулируют логику
 * приложения по работе с данными о факультетах.
 * <p>
 * Все методы интерфейса <code>FacultyService</code> могут выбросить исключение
 * {@link ServiceException}, которое сигнализирует об ошибке обработки данных
 * либо служит классом-оберткой для исключений чтения/записи данных из
 * источника.
 * 
 * @author Daria Krupenko
 * @see Faculty
 * @see ServiceException
 *
 */
public interface FacultyService {

	/**
	 * Получение факультета по его id.
	 * 
	 * @param id
	 *            id факультета
	 * @return объект факультета
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public Faculty getFaculty(int id) throws ServiceException;

	/**
	 * Получение количества факультетов
	 * 
	 * @return количество факультетов
	 * 
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public int getFacultiesNumber() throws ServiceException;

	/**
	 * Получение списка факультетов. Количество элементов списка может быть
	 * ограничено при указании номера страницы <code>requiredPage</code> и
	 * количества элементов на странице <code>elementsCount</code>.
	 * 
	 * @param requiredPage
	 *            номер требуемой страницы
	 * @param elementsCount
	 *            количество элементов на одной странице
	 * @return список факультетов, привязанный к номеру страницы
	 *         <code>requiredPage</code>
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public List<Faculty> getFacultiesList(int requiredPage, int elementsCount) throws ServiceException;

	/**
	 * Добавление факультета в систему
	 * 
	 * @param a
	 *            объект факультета
	 * @return объект факультета, добавленного в систему
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public Faculty addFaculty(Faculty f) throws ServiceException;

	/**
	 * Обновление факультета.
	 * 
	 * @param a
	 *            объект факультета с обновляемыми данными
	 * @return объект факультета с обновленными данными
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public Faculty updateFaculty(Faculty f) throws ServiceException;

	/**
	 * Удаление факультета
	 * 
	 * @param id
	 *            id факультета, который необходимо удалить
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public void deleteFaculty(int id) throws ServiceException;

	/**
	 * Получение возможности удаления факультета
	 * 
	 * @return true - если удаление факультета возможно; false - в противном
	 *         случае
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public boolean isDeleteEnable() throws ServiceException;

}
