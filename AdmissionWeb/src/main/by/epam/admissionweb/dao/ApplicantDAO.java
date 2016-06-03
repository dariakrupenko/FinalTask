package main.by.epam.admissionweb.dao;

import java.util.List;

import main.by.epam.admissionweb.dao.exception.DAOException;
import main.by.epam.admissionweb.dao.exception.TransactionException;
import main.by.epam.admissionweb.entity.Applicant;

/**
 * Интерфейс <code>ApplicantDAO</code> предоставляет основные методы для чтения
 * и записи информации об абитуриентах учебного заведения.
 * <p>
 * Интерфейс <code>ApplicantDAO</code> также предоставляет поддержку выполнения
 * бизнес-транзакций, так как каждый метод данного интерфейса сопровождается
 * кодом транзакции в качестве параметра. Работа с бизнес-транзакциями
 * осуществляется посредством методов <code>beginTransaction()</code>,
 * <code>rollbackTransaction()</code>, <code>commitTransaction()</code>, которые
 * позволяют начать, откатить или завершить транзакцию соотвественно.
 * <p>
 * Управление транзакцией осуществляется со стороны логики приложения. Интерфейс
 * <code>ApplicantDAO</code> предоставляет лишь механизм поддержки транзакций.
 * <p>
 * Методы чтения/записи данных выбрасывают {@link DAOException}, сигнализирующее
 * об ошибке чтения/записи данных. Методы поддержки транзакций выбрасывают
 * исключение {@link TransactionException}, сигнализирующее об ошибке
 * организации транзакции.
 * 
 * @author Daria Krupenko
 * @see Applicant
 * @see DAOException
 * @see TransactioException
 *
 */
public interface ApplicantDAO {

	/**
	 * Сохранение объекта абитуриента в источнике данных. При указании кода
	 * транзакции в качестве параметра <code>trCode</code> данное действие будет
	 * выполнено как часть транзакции с указанным кодом.
	 * 
	 * @param a
	 *            объект абитуриента
	 * @param trCode
	 *            код транзакции.
	 * @return id абитуриента, сохраненного в источнике данных
	 * @throws DAOException
	 *             если произошла ошибка при сохранении объекта абитуриента
	 */
	public int createApplicant(Applicant a, int trCode) throws DAOException;

	/**
	 * Получение абитуриента с указанным логином и паролем. При указании кода
	 * транзакции в качестве параметра <code>trCode</code> данное действие будет
	 * выполнено как часть транзакции с указанным кодом.
	 * 
	 * @param login
	 *            логин абитуриента
	 * @param password
	 *            пароль абитуриента
	 * @param trCode
	 *            код транзакции.
	 * @return объект абитуриента с указанным логином и паролем
	 * @throws DAOException
	 *             если произошла ошибка при получении абитуриента с указанным
	 *             логином и паролем
	 */
	public Applicant getApplicantByLoginPassword(String login, String password, int trCode) throws DAOException;

	/**
	 * Обновление информации об абитуриенте. При указании кода транзакции в
	 * качестве параметра <code>trCode</code> данное действие будет выполнено
	 * как часть транзакции с указанным кодом.
	 * 
	 * @param a
	 *            объект абитуриента с обновленными данными
	 * @param trCode
	 *            код транзакции.
	 * @throws DAOException
	 *             если произошла ошибка при обновлении данных абитуриента
	 */
	public void updateApplicant(Applicant a, int trCode) throws DAOException;

	/**
	 * Получение объекта абитуриента по его id. При указании кода транзакции в
	 * качестве параметра <code>trCode</code> данное действие будет выполнено
	 * как часть транзакции с указанным кодом.
	 * 
	 * @param id
	 *            id абитуриента
	 * @param trCode
	 *            код транзакции.
	 * @return объект абитуриента с указанным id
	 * @throws DAOException
	 *             если произошла ошибка при получении абитуриента с указанным
	 *             id
	 */
	public Applicant getApplicant(int id, int trCode) throws DAOException;

	/**
	 * Получение количества абитуриентов, зарегистрированных в системе. При
	 * указании кода транзакции в качестве параметра <code>trCode</code> данное
	 * действие будет выполнено как часть транзакции с указанным кодом.
	 * 
	 * @param trCode
	 *            код транзакции.
	 * @return количество абитуриентов, зарегистрированных в системе
	 * @throws DAOException
	 *             если произошла ошибка при получении количества абитуриентов,
	 *             зарегистрированных в системе
	 */
	public int getApplicantsNumber(int trCode) throws DAOException;

	/**
	 * Получение списка абитуриентов, зарегистрированных в системе. Количество
	 * элементов списка может быть ограничено и контролируется параметрами
	 * <code>beginIndex</code> и <code>elementsCount</code>. При указании кода
	 * транзакции в качестве параметра <code>trCode</code> данное действие будет
	 * выполнено как часть транзакции с указанным кодом.
	 * 
	 * @param beginIndex
	 *            индекс абитуриента, с которого начинается построение списка
	 * @param elementsCount
	 *            количество требуемых абитуриентов, начиная с
	 *            <code>beginIndex</code>
	 * @param trCode
	 *            код транзакции.
	 * @return список абитуриентов, начиная с абитуриента с индексом
	 *         <code>beginIndex</code>; количество элементов списка равно
	 *         <code>elementsCount</code>
	 * @throws DAOException
	 *             если произошла ошибка при получении списка абитуриентов
	 */
	public List<Applicant> getApplicantsList(int beginIndex, int elementsCount, int trCode) throws DAOException;

	/**
	 * Получение объекта абитуриента по указанному логину. При указании кода
	 * транзакции в качестве параметра <code>trCode</code> данное действие будет
	 * выполнено как часть транзакции с указанным кодом.
	 * 
	 * @param login
	 *            логин абитуриента
	 * @param trCode
	 *            код транзакции
	 * @return объект абитуриента с указанным логином
	 * @throws DAOException
	 *             если произошла ошибка при получении абитуриента с указанным
	 *             логином.
	 */
	public Applicant getApplicantByLogin(String login, int trCode) throws DAOException;

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
