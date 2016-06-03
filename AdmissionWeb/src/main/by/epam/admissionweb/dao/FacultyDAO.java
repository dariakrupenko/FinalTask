package main.by.epam.admissionweb.dao;

import java.util.List;

import main.by.epam.admissionweb.dao.exception.DAOException;
import main.by.epam.admissionweb.dao.exception.TransactionException;
import main.by.epam.admissionweb.entity.Enroll;
import main.by.epam.admissionweb.entity.Faculty;

/**
 * Интерфейс <code>FacultyDAO</code> предоставляет основные методы для чтения и
 * записи информации о факультетах учебного заведения.
 * <p>
 * Интерфейс <code>FacultyDAO</code> также предоставляет поддержку выполнения
 * бизнес-транзакций, так как каждый метод данного интерфейса сопровождается
 * кодом транзакции в качестве параметра. Работа с бизнес-транзакциями
 * осуществляется посредством методов <code>beginTransaction()</code>,
 * <code>rollbackTransaction()</code>, <code>commitTransaction()</code>, которые
 * позволяют начать, откатить или завершить транзакцию соотвественно.
 * <p>
 * Управление транзакцией осуществляется со стороны логики приложения. Интерфейс
 * <code>FacultyDAO</code> предоставляет лишь механизм поддержки транзакций.
 * <p>
 * Методы чтения/записи данных выбрасывают {@link DAOException}, сигнализирующее
 * об ошибке чтения/записи данных. Методы поддержки транзакций выбрасывают
 * исключение {@link TransactionException}, сигнализирующее об ошибке
 * организации транзакции.
 * 
 * @author Daria Krupenko
 * @see Faculty
 * @see DAOException
 * @see TransactioException
 *
 */
public interface FacultyDAO {

	/**
	 * Получение списка факультетов. Количество элементов списка может быть
	 * ограничено и контролируется параметрами <code>beginIndex</code> и
	 * <code>elementsCount</code>. При указании кода транзакции в качестве
	 * параметра <code>trCode</code> данное действие будет выполнено как часть
	 * транзакции с указанным кодом.
	 * 
	 * @param beginIndex
	 *            индекс факультета, с которого начинается построение списка
	 * @param elementsCount
	 *            количество требуемых факультетов, начиная с
	 *            <code>beginIndex</code>
	 * @param e
	 *            набор, в контексте которого подсчитывается количество
	 *            записавшихся абитуриентов
	 * @param trCode
	 *            код транзакции.
	 * @return список факультетов, начиная с факультета с индексом
	 *         <code>beginIndex</code>; количество элементов списка равно
	 *         <code>elementsCount</code>
	 * @throws DAOException
	 *             если произошла ошибка при получении списка факультетов
	 */
	public List<Faculty> getFacultiesList(int beginIndex, int elementsCount, Enroll e, int trCode) throws DAOException;

	/**
	 * Получение количества факультетов. При указании кода транзакции в качестве
	 * параметра <code>trCode</code> данное действие будет выполнено как часть
	 * транзакции с указанным кодом.
	 * 
	 * @param trCode
	 *            код транзакции.
	 * @return количество факультетов
	 * @throws DAOException
	 *             если произошла ошибка при получении количества факультетов
	 */
	public int getFacultiesNumber(int trCode) throws DAOException;

	/**
	 * Сохранение объекта факультета в источнике данных. При указании кода
	 * транзакции в качестве параметра <code>trCode</code> данное действие будет
	 * выполнено как часть транзакции с указанным кодом.
	 * 
	 * @param f
	 *            объект факультета
	 * @param trCode
	 *            код транзакции.
	 * @return id факультета, сохраненного в источнике данных
	 * @throws DAOException
	 *             если произошла ошибка при сохранении объекта факультета
	 */
	public int createFaculty(Faculty f, int trCode) throws DAOException;

	/**
	 * Получение объекта факультета по его id. При указании кода транзакции в
	 * качестве параметра <code>trCode</code> данное действие будет выполнено
	 * как часть транзакции с указанным кодом.
	 * 
	 * @param id
	 *            id факультета
	 * @param trCode
	 *            код транзакции.
	 * @param e
	 *            набор, в контексте которого подсчитывается количество
	 *            записавшихся абитуриентов
	 * @return объект факультета с указанным id
	 * @throws DAOException
	 *             если произошла ошибка при получении факультета с указанным id
	 */
	public Faculty getFaculty(int id, Enroll e, int trCode) throws DAOException;

	/**
	 * Обновление информации о факультете. При указании кода транзакции в
	 * качестве параметра <code>trCode</code> данное действие будет выполнено
	 * как часть транзакции с указанным кодом.
	 * 
	 * @param d
	 *            объект факультета с обновленными данными
	 * @param trCode
	 *            код транзакции.
	 * @throws DAOException
	 *             если произошла ошибка при обновлении данных факультета
	 */
	public void updateFaculty(Faculty f, int trCode) throws DAOException;

	/**
	 * Удаление факультета из системы. При указании кода транзакции в качестве
	 * параметра <code>trCode</code> данное действие будет выполнено как часть
	 * транзакции с указанным кодом.
	 * 
	 * @param id
	 *            id факультета
	 * @param trCode
	 *            код транзакции.
	 * @throws DAOException
	 *             если произошла ошибка при удалении факультета
	 */
	public void deleteFaculty(int id, int trCode) throws DAOException;

	/**
	 * Получение объекта факультета по указанному наименованию. При указании
	 * кода транзакции в качестве параметра <code>trCode</code> данное действие
	 * будет выполнено как часть транзакции с указанным кодом.
	 * 
	 * @param title
	 *            наименование факультета
	 * @param trCode
	 *            код транзакции
	 * @return объект факультета с указанным наименованием
	 * @throws DAOException
	 *             если произошла ошибка при получении факультета с указанным
	 *             наименованием
	 */
	public Faculty getFacultyByTitle(String title, int trCode) throws DAOException;

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
