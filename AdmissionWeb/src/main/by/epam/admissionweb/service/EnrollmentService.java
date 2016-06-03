package main.by.epam.admissionweb.service;

import java.util.List;

import main.by.epam.admissionweb.entity.Enroll;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Интерфейс <code>EnrollmentService</code> предоставляет основные методы для
 * обработки данных о наборах учебного заведения.
 * <p>
 * Реализации интерфейса <code>EnrollmentService</code> инкапсулируют логику
 * приложения по работе с данными о наборах.
 * <p>
 * Все методы интерфейса <code>EnrollmentService</code> могут выбросить
 * исключение {@link ServiceException}, которое сигнализирует об ошибке
 * обработки данных либо служит классом-оберткой для исключений чтения/записи
 * данных из источника.
 * 
 * @author Daria Krupenko
 * @see Enroll
 * @see ServiceException
 *
 */
public interface EnrollmentService {

	/**
	 * Получение текущего набора
	 * 
	 * @return текущий набор
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public Enroll getCurrentEnroll() throws ServiceException;

	/**
	 * Получение последнего набора
	 * 
	 * @return последний набор
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public Enroll getLastEnroll() throws ServiceException;

	/**
	 * Получение количества наборов
	 * 
	 * @return количество наборов
	 * 
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public int getEnrollsNumber() throws ServiceException;

	/**
	 * Получение списка наборов. Количество элементов списка может быть
	 * ограничено при указании номера страницы <code>requiredPage</code> и
	 * количества элементов на странице <code>elementsCount</code>.
	 * 
	 * @param requiredPage
	 *            номер требуемой страницы
	 * @param elementsCount
	 *            количество элементов на одной странице
	 * @return список наборов, привязанный к номеру страницы
	 *         <code>requiredPage</code>
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public List<Enroll> getEnrollsList(int requiredPage, int elementsCount) throws ServiceException;

	/**
	 * Открыть набор
	 * 
	 * @param e
	 *            объект набора, который необходимо открыть
	 * @return объект набора, который успешно открыт и сохранен в источнике
	 *         данных
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public Enroll startEnroll(Enroll e) throws ServiceException;

	/**
	 * Удаление набора
	 * 
	 * @param id
	 *            id набора, который должен быть удален
	 * @return true - если набор успешно удален, false - удаление набора
	 *         запрещено логикой приложения
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public boolean deleteEnroll(int id) throws ServiceException;

	/**
	 * Подвести итоги текущего набора
	 * 
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public void completeCurrentEnroll() throws ServiceException;

	/**
	 * Получение состояния текущего набора
	 * 
	 * @return true - в данный момент существует открытый набор; false - набор
	 *         закрыт или не найдено ни одного набора
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public boolean isCurrentEnroll() throws ServiceException;

}
