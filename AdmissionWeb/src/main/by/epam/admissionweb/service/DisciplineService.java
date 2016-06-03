package main.by.epam.admissionweb.service;

import java.util.List;

import main.by.epam.admissionweb.entity.Discipline;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Интерфейс <code>DisciplineService</code> предоставляет основные методы для
 * обработки данных о дисциплинах учебного заведения.
 * <p>
 * Реализации интерфейса <code>DisciplineService</code> инкапсулируют логику
 * приложения по работе с данными о дисциплинах.
 * <p>
 * Все методы интерфейса <code>DisciplineService</code> могут выбросить
 * исключение {@link ServiceException}, которое сигнализирует об ошибке
 * обработки данных либо служит классом-оберткой для исключений чтения/записи
 * данных из источника.
 * 
 * @author Daria Krupenko
 * @see Discipline
 * @see ServiceException
 *
 */
public interface DisciplineService {

	/**
	 * Получение дисциплины по ее id.
	 * 
	 * @param id
	 *            id дисциплины
	 * @return объект дисциплины
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public Discipline getDiscipline(int id) throws ServiceException;

	/**
	 * Получение количества дисциплин
	 * 
	 * @return количество дисциплин
	 * 
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public int getDisciplinesNumber() throws ServiceException;

	/**
	 * Получение списка дисциплин. Количество элементов списка может быть
	 * ограничено при указании номера страницы <code>requiredPage</code> и
	 * количества элементов на странице <code>elementsCount</code>.
	 * 
	 * @param requiredPage
	 *            номер требуемой страницы
	 * @param elementsCount
	 *            количество элементов на одной странице
	 * @return список дисциплин, привязанный к номеру страницы
	 *         <code>requiredPage</code>
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public List<Discipline> getDisciplinesList(int requiredPage, int elementsCount) throws ServiceException;

	/**
	 * Добавление дисциплины в систему
	 * 
	 * @param a
	 *            объект дисциплины
	 * @return объект дисциплины, добавленной в систему
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public Discipline addDiscipline(Discipline d) throws ServiceException;

	/**
	 * Обновление дисциплины.
	 * 
	 * @param a
	 *            объект дисциплины с обновляемыми данными
	 * @return объект абитуриента с обновленными данными
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public Discipline updateDiscipline(Discipline d) throws ServiceException;

	/**
	 * Удаление дисциплины
	 * 
	 * @param id
	 *            id дисциплины, которую необходимо удалить
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public void deleteDiscipline(int id) throws ServiceException;

	/**
	 * Получение возможности удаления дисциплины
	 * 
	 * @return true - если удаление дисциплины возможно; false - в противном
	 *         случае
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public boolean isDeleteEnable() throws ServiceException;
}
