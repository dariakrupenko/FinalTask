package main.by.epam.admissionweb.dao;

import java.util.List;

import main.by.epam.admissionweb.dao.exception.DAOException;
import main.by.epam.admissionweb.dao.exception.TransactionException;
import main.by.epam.admissionweb.entity.Applicant;
import main.by.epam.admissionweb.entity.Enroll;
import main.by.epam.admissionweb.entity.Faculty;
import main.by.epam.admissionweb.entity.RegisterRecord;

/**
 * Интерфейс <code>RegisterDAO</code> предоставляет основные методы для чтения и
 * записи информации о ведомости учебного заведения.
 * <p>
 * Интерфейс <code>RegisterDAO</code> также предоставляет поддержку выполнения
 * бизнес-транзакций, так как каждый метод данного интерфейса сопровождается
 * кодом транзакции в качестве параметра. Работа с бизнес-транзакциями
 * осуществляется посредством методов <code>beginTransaction()</code>,
 * <code>rollbackTransaction()</code>, <code>commitTransaction()</code>, которые
 * позволяют начать, откатить или завершить транзакцию соотвественно.
 * <p>
 * Управление транзакцией осуществляется со стороны логики приложения. Интерфейс
 * <code>RegisterDAO</code> предоставляет лишь механизм поддержки транзакций.
 * <p>
 * Методы чтения/записи данных выбрасывают {@link DAOException}, сигнализирующее
 * об ошибке чтения/записи данных. Методы поддержки транзакций выбрасывают
 * исключение {@link TransactionException}, сигнализирующее об ошибке
 * организации транзакции.
 * 
 * @author Daria Krupenko
 * @see RegisterRecord
 * @see DAOException
 * @see TransactioException
 *
 */
public interface RegisterDAO {

	/**
	 * Сохранение записи ведомости в источнике данных. При указании кода
	 * транзакции в качестве параметра <code>trCode</code> данное действие будет
	 * выполнено как часть транзакции с указанным кодом.
	 * 
	 * @param r
	 *            объект записи ведомости
	 * @param trCode
	 *            код транзакции.
	 * @return id записи ведомости, сохраненной в источнике данных
	 * @throws DAOException
	 *             если произошла ошибка при сохранении объекта дисциплины
	 */
	public void createRecord(RegisterRecord r, int trCode) throws DAOException;

	/**
	 * Получение объекта записи ведомости абитуриента. При указании кода
	 * транзакции в качестве параметра <code>trCode</code> данное действие будет
	 * выполнено как часть транзакции с указанным кодом.
	 * 
	 * @param a
	 *            абитуриент, чью запись нужно прочесть из ведомости
	 * @param trCode
	 *            код транзакции.
	 * @return объект записи ведомости абитуриента
	 * @throws DAOException
	 *             если произошла ошибка при получении записи ведомости
	 *             абитуриента
	 */
	public RegisterRecord getRecord(Applicant a, int trCode) throws DAOException;

	/**
	 * Удаление записи ведомости абитуриента из системы. При указании кода
	 * транзакции в качестве параметра <code>trCode</code> данное действие будет
	 * выполнено как часть транзакции с указанным кодом.
	 * 
	 * @param a
	 *            абитуриент, чью запись нужно удалить из ведомости
	 * @param trCode
	 *            код транзакции.
	 * @throws DAOException
	 *             если произошла ошибка при удалении записи ведомости
	 *             абитуриента
	 */
	public void deleteRecord(Applicant a, int trCode) throws DAOException;

	/**
	 * Получение количества записей ведомости в рамках указанного набора. При
	 * указании кода транзакции в качестве параметра <code>trCode</code> данное
	 * действие будет выполнено как часть транзакции с указанным кодом.
	 * 
	 * @param e
	 *            набор, в рамках которого нужно получить ведомость
	 * @param trCode
	 *            код транзакции.
	 * @return количество записей ведомости в рамках набора <code>e</code>
	 * @throws DAOException
	 *             если произошла ошибка при получении количества записей
	 *             ведомости
	 */
	public int getRecordsNumber(Enroll e, int trCode) throws DAOException;

	/**
	 * Получение ведомости. Количество записей ведомости может быть ограничено и
	 * контролируется параметрами <code>beginIndex</code> и
	 * <code>elementsCount</code>. При указании кода транзакции в качестве
	 * параметра <code>trCode</code> данное действие будет выполнено как часть
	 * транзакции с указанным кодом.
	 * 
	 * @param beginIndex
	 *            индекс записи ведомости, с которой начинается построение
	 *            списка
	 * @param elementsCount
	 *            количество требуемых записей ведомости, начиная с
	 *            <code>beginIndex</code>
	 * @param trCode
	 *            код транзакции.
	 * @return ведомость, начиная с записи с индексом <code>beginIndex</code>;
	 *         количество элементов списка равно <code>elementsCount</code>
	 * @throws DAOException
	 *             если произошла ошибка при получении ведомости
	 */
	public List<RegisterRecord> getRegister(int beginIndex, int elementsCount, Enroll e, int trCode)
			throws DAOException;

	/**
	 * Получение количества записей ведомости в рамках указанного набора,
	 * факультета и статуса абитуриентов. При указании кода транзакции в
	 * качестве параметра <code>trCode</code> данное действие будет выполнено
	 * как часть транзакции с указанным кодом.
	 * 
	 * @param isAdmitted
	 *            статус абитуриента: true - (временно)зачислен, false -
	 *            (временно)незачислен
	 * @param f
	 *            факультет, в рамках которого необходимо получить ведомость
	 * @param e
	 *            набор, в рамках которого нужно получить ведомость
	 * @param trCode
	 *            код транзакции.
	 * @return количество записей ведомости в рамках набора, факультета и
	 *         статуса абитуриентов <code>e</code>
	 * @throws DAOException
	 *             если произошла ошибка при получении количества записей
	 *             ведомости
	 */
	public int getRecordsNumberByStatusAndFaculty(boolean isAdmitted, Faculty f, Enroll e, int trCode)
			throws DAOException;

	/**
	 * Получение ведомости. Количество записей ведомости может быть ограничено и
	 * контролируется параметрами <code>beginIndex</code> и
	 * <code>elementsCount</code>. При указании кода транзакции в качестве
	 * параметра <code>trCode</code> данное действие будет выполнено как часть
	 * транзакции с указанным кодом.
	 * 
	 * @param beginIndex
	 *            индекс записи ведомости, с которой начинается построение
	 *            списка
	 * @param elementsCount
	 *            количество требуемых записей ведомости, начиная с
	 *            <code>beginIndex</code>
	 * @param trCode
	 *            код транзакции.
	 * @return ведомость, начиная с записи с индексом <code>beginIndex</code>;
	 *         количество элементов списка равно <code>elementsCount</code>
	 * @throws DAOException
	 *             если произошла ошибка при получении ведомости
	 */
	public List<RegisterRecord> getRegisterByStatusAndFaculty(int beginIndex, int elementsCount, boolean isAdmitted,
			Faculty f, Enroll e, int trCode) throws DAOException;

	/**
	 * Обновление статусов абитуриентов в рамках факультета и набора. При
	 * указании кода транзакции в качестве параметра <code>trCode</code> данное
	 * действие будет выполнено как часть транзакции с указанным кодом.
	 * 
	 * @param isAdmitted
	 *            статус абитуриентов; true - обновление статусов
	 *            (временно)зачисленных абитуриентов; false - обновление
	 *            статусов (временно)незачисленных абитуриентов.
	 * @param f
	 *            факультет, в рамках которого необходимо обновить статусы
	 * @param e
	 *            набор, в рамках которого необходимо обновить статусы
	 * @param trCode
	 *            код транзакции
	 * @throws DAOException
	 *             если при обновлении статусов произошла ошибка
	 */
	public void updateStatuses(boolean isAdmitted, Faculty f, Enroll e, int trCode) throws DAOException;

	/**
	 * Получить изменившийся проходной балл указанного факультета. При указании
	 * кода транзакции в качестве параметра <code>trCode</code> данное действие
	 * будет выполнено как часть транзакции с указанным кодом.
	 * 
	 * @param f
	 *            факультет, проходной балл которого необходимо получить
	 * @param trCode
	 *            код транзакции
	 * @return изменившийся проходной балл указанного факультета
	 * @throws DAOException
	 *             если произошла ошибка при получении изменившегося проходного
	 *             балла факультета
	 */
	public int getChangedPassRate(Faculty f, int trCode) throws DAOException;

	/**
	 * Получение количества записей ведомости в рамках указанного факультета и
	 * набора. При указании кода транзакции в качестве параметра
	 * <code>trCode</code> данное действие будет выполнено как часть транзакции
	 * с указанным кодом.
	 * 
	 * @param f
	 *            факультет, в рамках которого нужно получить количество записей
	 *            ведомости
	 * @param e
	 *            набор, в рамках которого нужно получить количество записей
	 *            ведомости
	 * @param trCode
	 *            код транзакции.
	 * @return количество записей ведомости в рамках указанного набора и
	 *         факультета
	 * @throws DAOException
	 *             если произошла ошибка при получении количества записей
	 *             ведомости
	 */
	public int getRecordsNumberByFaculty(Faculty f, Enroll e, int trCode) throws DAOException;

	/**
	 * Установить новый проходной балл факультета. При указании кода транзакции
	 * в качестве параметра <code>trCode</code> данное действие будет выполнено
	 * как часть транзакции с указанным кодом.
	 * 
	 * @param f
	 *            факультет, проходной балл которого нужно изменить
	 * @param newPassRate
	 *            новый проходной балл
	 * @param trCode
	 *            код транзакции
	 * @throws DAOException
	 *             если произошла ошибка при изменении проходного балла
	 *             факультета
	 */
	public void setNewPassRate(Faculty f, int newPassRate, int trCode) throws DAOException;

	/**
	 * Создание новой транзакции.
	 * <p>
	 * Как правило, вызывается классами логики приложения.
	 * 
	 * @return код транзакции
	 * @throws TransactionException
	 *             при создании новой транзакции произошла ошибка
	 */
	public int beginTransaction() throws TransactionException;

	/**
	 * Откат транзакции с указанным в параметре <code>trCode</code> кодом.
	 * <p>
	 * Как правило, вызывается классами логики приложения.
	 * 
	 * @param trCode
	 *            код транзакции
	 * @throws TransactionException
	 *             если при откате транзакции произошла ошибка
	 */
	public void rollbackTransaction(int trCode) throws TransactionException;

	/**
	 * Завершение транзакции с указанным в параметре <code>trCode</code> кодом.
	 * <p>
	 * Как правило, вызывается классами логики приложения.
	 * 
	 * @param trCode
	 *            код транзакции
	 * @throws TransactionException
	 *             если при завершении транзакции произошла ошибка
	 */
	public void commitTransaction(int trCode) throws TransactionException;
}
