package main.by.epam.admissionweb.dao;

import java.util.Date;
import java.util.List;

import main.by.epam.admissionweb.dao.exception.DAOException;
import main.by.epam.admissionweb.dao.exception.TransactionException;
import main.by.epam.admissionweb.entity.Enroll;

/**
 * Интерфейс <code>EnrollmentDAO</code> предоставляет основные методы для чтения
 * и записи информации о наборах учебного заведения.
 * <p>
 * Интерфейс <code>EnrollmentDAO</code> также предоставляет поддержку выполнения
 * бизнес-транзакций, так как каждый метод данного интерфейса сопровождается
 * кодом транзакции в качестве параметра. Работа с бизнес-транзакциями
 * осуществляется посредством методов <code>beginTransaction()</code>,
 * <code>rollbackTransaction()</code>, <code>commitTransaction()</code>, которые
 * позволяют начать, откатить или завершить транзакцию соотвественно.
 * <p>
 * Управление транзакцией осуществляется со стороны логики приложения. Интерфейс
 * <code>EnrollmentDAO</code> предоставляет лишь механизм поддержки транзакций.
 * <p>
 * Методы чтения/записи данных выбрасывают {@link DAOException}, сигнализирующее
 * об ошибке чтения/записи данных. Методы поддержки транзакций выбрасывают
 * исключение {@link TransactionException}, сигнализирующее об ошибке
 * организации транзакции.
 * 
 * @author Daria Krupenko
 * @see Enroll
 * @see DAOException
 * @see TransactioException
 *
 */
public interface EnrollmentDAO {

	/**
	 * Получение списка наборов. Количество элементов списка может быть
	 * ограничено и контролируется параметрами <code>beginIndex</code> и
	 * <code>elementsCount</code>. При указании кода транзакции в качестве
	 * параметра <code>trCode</code> данное действие будет выполнено как часть
	 * транзакции с указанным кодом.
	 * 
	 * @param beginIndex
	 *            индекс набора, с которого начинается построение списка
	 * @param elementsCount
	 *            количество требуемых наборов, начиная с
	 *            <code>beginIndex</code>
	 * @param trCode
	 *            код транзакции.
	 * @return список наборов, начиная с набора с индексом
	 *         <code>beginIndex</code>; количество элементов списка равно
	 *         <code>elementsCount</code>
	 * @throws DAOException
	 *             если произошла ошибка при получении списка наборов
	 */
	public List<Enroll> getEnrollsList(int beginIndex, int elementsCount, int trCode) throws DAOException;

	/**
	 * Получение количества наборов. При указании кода транзакции в качестве
	 * параметра <code>trCode</code> данное действие будет выполнено как часть
	 * транзакции с указанным кодом.
	 * 
	 * @param trCode
	 *            код транзакции.
	 * @return количество наборов
	 * @throws DAOException
	 *             если произошла ошибка при получении количества наборов
	 */
	public int getEnrollsNumber(int trCode) throws DAOException;

	/**
	 * Сохранение объекта набора в источнике данных. При указании кода
	 * транзакции в качестве параметра <code>trCode</code> данное действие будет
	 * выполнено как часть транзакции с указанным кодом.
	 * 
	 * @param e
	 *            объект набора
	 * @param trCode
	 *            код транзакции.
	 * @return id набора, сохраненного в источнике данных
	 * @throws DAOException
	 *             если произошла ошибка при сохранении объекта набора
	 */
	public int createEnroll(Enroll e, int trCode) throws DAOException;

	/**
	 * Удаление набора из системы. При указании кода транзакции в качестве
	 * параметра <code>trCode</code> данное действие будет выполнено как часть
	 * транзакции с указанным кодом.
	 * 
	 * @param id
	 *            id набора
	 * @param trCode
	 *            код транзакции.
	 * @throws DAOException
	 *             если произошла ошибка при удалении набора
	 */
	public void deleteEnroll(int id, int trCode) throws DAOException;

	/**
	 * Получение наборов с указанным статусом. При указании кода транзакции в
	 * качестве параметра <code>trCode</code> данное действие будет выполнено
	 * как часть транзакции с указанным кодом.
	 * 
	 * @param b
	 *            статус набора: true - активный, false - закрытый
	 * @param trCode
	 *            код транзакции.
	 * @return список наборов с указанным статусом
	 * @throws DAOException
	 *             если произошла ошибка при получении списка наборов с
	 *             указанным статусом
	 */
	public List<Enroll> getEnrollsByStatus(boolean b, int trCode) throws DAOException;

	/**
	 * Получение последнего набора учебного заведения. При указании кода
	 * транзакции в качестве параметра <code>trCode</code> данное действие будет
	 * выполнено как часть транзакции с указанным кодом.
	 * 
	 * @param trCode
	 *            код транзакции.
	 * @return объект последнего набора учебного заведения
	 * @throws DAOException
	 *             если произошла ошибка при получении последнего набора
	 *             учебного заведения
	 */
	public Enroll getLastEnroll(int trCode) throws DAOException;

	/**
	 * Завершение набора. При указании кода транзакции в качестве параметра
	 * <code>trCode</code> данное действие будет выполнено как часть транзакции
	 * с указанным кодом.
	 * 
	 * @param e
	 *            набор, который необоходимо завершить
	 * @param d
	 *            дата завершения набора
	 * @param trCode
	 *            код транзакции
	 * @throws DAOException
	 *             если при завершении набора произошла ошибка
	 */
	public void completeEnroll(Enroll e, Date d, int trCode) throws DAOException;

	/**
	 * Сброс (обнуление) проходных баллов всех факультетов. При указании кода
	 * транзакции в качестве параметра <code>trCode</code> данное действие будет
	 * выполнено как часть транзакции с указанным кодом.
	 * 
	 * @param trCode
	 *            код транзакции
	 * @throws DAOException
	 *             если произошла ошибка при сбросе проходных баллов всех
	 *             факультетов.
	 */
	public void resetPassRates(int trCode) throws DAOException;

	/**
	 * Поменять статусы абитуриентов в контексте завершения набора. При указании
	 * кода транзакции в качестве параметра <code>trCode</code> данное действие
	 * будет выполнено как часть транзакции с указанным кодом.
	 * 
	 * @param isAdmitted
	 *            статус абитуриентов; true - завершить статусы зачисленных
	 *            абитуриентов; false - завершить статусы незачисленных
	 *            абитуриентов.
	 * @param e
	 *            набор, в рамках которого меняются статусы абитуриентов
	 * @param trCode
	 *            код транзакции
	 * @throws DAOException
	 *             если произошла ошибка при смене статусов абитуриентов
	 */
	public void completeStatuses(boolean isAdmitted, Enroll e, int trCode) throws DAOException;

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
