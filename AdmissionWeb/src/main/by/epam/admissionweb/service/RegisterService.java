package main.by.epam.admissionweb.service;

import java.util.List;

import main.by.epam.admissionweb.entity.Applicant;
import main.by.epam.admissionweb.entity.Enroll;
import main.by.epam.admissionweb.entity.Faculty;
import main.by.epam.admissionweb.entity.RegisterRecord;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Интерфейс <code>RegisterService</code> предоставляет основные методы для
 * обработки данных ведомости учебного заведения.
 * <p>
 * Реализации интерфейса <code>RegisterService</code> инкапсулируют логику
 * приложения по работе с ведомостью.
 * <p>
 * Все методы интерфейса <code>RegisterService</code> могут выбросить исключение
 * {@link ServiceException}, которое сигнализирует об ошибке обработки данных
 * либо служит классом-оберткой для исключений чтения/записи данных из
 * источника.
 * 
 * @author Daria Krupenko
 * @see RegisterRecord
 * @see ServiceException
 *
 */
public interface RegisterService {

	/**
	 * Получение записи ведомости, ассоциированной с абитуриентом
	 * 
	 * @param a
	 *            объект абитуриента, для которого нужно получить запись
	 *            ведомости
	 * @return запись ведомости, ассоциированной с абитуриентом
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public RegisterRecord getRecord(Applicant a) throws ServiceException;

	/**
	 * Получение количества записей в ведомости
	 * 
	 * @return количество записей в ведомости
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public int getRecordsNumber() throws ServiceException;

	/**
	 * Получение ведомости абитуриентов, содержащей список записей. Количество
	 * элементов списка может быть ограничено при указании номера страницы
	 * <code>requiredPage</code> и количества элементов на странице
	 * <code>elementsCount</code>.
	 * 
	 * @param requiredPage
	 *            номер требуемой страницы
	 * @param elementsCount
	 *            количество элементов на одной странице
	 * @return список записей, привязанный к номеру страницы
	 *         <code>requiredPage</code>
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public List<RegisterRecord> getRegister(int requiredPage, int elementsCount) throws ServiceException;

	/**
	 * Получение количества записей в ведомости в рамках статуса абитуриентов и
	 * указанного факультета
	 * 
	 * @param isAdmitted
	 *            статус абитуриентов
	 * @param f
	 *            факультет
	 * @return количество записей в ведомости в рамках статуса абитуриентов и
	 *         указанного факультета
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public int getRecordsNumberByStatusAndFaculty(boolean isAdmitted, Faculty f) throws ServiceException;

	/**
	 * Получение ведомости абитуриентов, содержащей список записей, в рамках
	 * статуса и факультета. Количество элементов списка может быть ограничено
	 * при указании номера страницы <code>requiredPage</code> и количества
	 * элементов на странице <code>elementsCount</code>.
	 * 
	 * @param requiredPage
	 *            номер требуемой страницы
	 * @param elementsCount
	 *            количество элементов на одной странице
	 * @param isAdmitted
	 *            статус абитуриентов
	 * @param f
	 *            факультет
	 * @return список записей в рамках статуса и факультета, привязанный к
	 *         номеру страницы <code>requiredPage</code>
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public List<RegisterRecord> getRegisterByStatusAndFaculty(int requiredPage, int elementsCount, boolean isAdmitted,
			Faculty f) throws ServiceException;

	/**
	 * Запись абитуриента на факультет.
	 * 
	 * @param r
	 *            объект записи в ведомости, которая содержит всю необходимую
	 *            информацию для записи абитуриента
	 * @return запись ведомости, ассоциированная с абитуриентом, успешно
	 *         сохраненная в источнике данных
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public RegisterRecord registryApplicant(RegisterRecord r) throws ServiceException;

	/**
	 * Обновление записи ведомости, ассоциированной с абитуриентом
	 * 
	 * @param a
	 *            объект абитуриента
	 * @return обновленная запись в ведомости
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public RegisterRecord updateRecordInf(Applicant a) throws ServiceException;

	/**
	 * Отмена записи на факультет.
	 * 
	 * @param a
	 *            абитуриент, который отменяет запись на факультет
	 * @return объект абитуриента с отмененной записью на факультет (пустой
	 *         записью)
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public Applicant cancelRegistry(Applicant a) throws ServiceException;

	/**
	 * Пересчет ведомости для заданного факультета в рамках заданного набора.
	 * Данная операция может являться частью бизнес-транзакции с кодом
	 * <code>trCode</code>.
	 * 
	 * @param f
	 *            факультет
	 * @param e
	 *            набор
	 * @param trCode
	 *            код транзакции
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public void recalculateRegister(Faculty f, Enroll e, int trCode) throws ServiceException;

	/**
	 * Получение состояния о возможности записи на факультет.
	 * 
	 * @return true - запись на факультет возможна, false - в противном случае.
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public boolean isRegistryEnabled() throws ServiceException;

	/**
	 * Получение состояния записи на факультет для конкретного абитуриента.
	 * 
	 * @param applicant
	 *            абитуриент, состояние записи для которого проверяется
	 * @return состояние записи абитуриента
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public boolean isApplicantRegistered(Applicant applicant) throws ServiceException;

}
