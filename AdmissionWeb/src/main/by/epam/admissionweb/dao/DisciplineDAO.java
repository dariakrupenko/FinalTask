package main.by.epam.admissionweb.dao;

import java.util.List;

import main.by.epam.admissionweb.dao.exception.DAOException;
import main.by.epam.admissionweb.dao.exception.TransactionException;
import main.by.epam.admissionweb.entity.Discipline;

/**
 * Интерфейс <code>DisciplineDAO</code> предоставляет основные методы для чтения
 * и записи информации о дисциплинах учебного заведения.
 * <p>
 * Интерфейс <code>DisciplineDAO</code> также предоставляет поддержку выполнения
 * бизнес-транзакций, так как каждый метод данного интерфейса сопровождается
 * кодом транзакции в качестве параметра. Работа с бизнес-транзакциями
 * осуществляется посредством методов <code>beginTransaction()</code>,
 * <code>rollbackTransaction()</code>, <code>commitTransaction()</code>, которые
 * позволяют начать, откатить или завершить транзакцию соотвественно.
 * <p>
 * Управление транзакцией осуществляется со стороны логики приложения. Интерфейс
 * <code>DisciplineDAO</code> предоставляет лишь механизм поддержки транзакций.
 * <p>
 * Методы чтения/записи данных выбрасывают {@link DAOException}, сигнализирующее
 * об ошибке чтения/записи данных. Методы поддержки транзакций выбрасывают
 * исключение {@link TransactionException}, сигнализирующее об ошибке
 * организации транзакции.
 * 
 * @author Daria Krupenko
 * @see Discipline
 * @see DAOException
 * @see TransactioException
 *
 */
public interface DisciplineDAO {

	/**
	 * Получение списка дисциплин. Количество элементов списка может быть
	 * ограничено и контролируется параметрами <code>beginIndex</code> и
	 * <code>elementsCount</code>. При указании кода транзакции в качестве
	 * параметра <code>trCode</code> данное действие будет выполнено как часть
	 * транзакции с указанным кодом.
	 * 
	 * @param beginIndex
	 *            индекс дисциплины, с которой начинается построение списка
	 * @param elementsCount
	 *            количество требуемых дисциплин, начиная с
	 *            <code>beginIndex</code>
	 * @param trCode
	 *            код транзакции.
	 * @return список дисциплин, начиная с дисциплины с индексом
	 *         <code>beginIndex</code>; количество элементов списка равно
	 *         <code>elementsCount</code>
	 * @throws DAOException
	 *             если произошла ошибка при получении списка дисциплин
	 */
	public List<Discipline> getDisciplinesList(int beginIndex, int elementsCount, int trCode) throws DAOException;

	/**
	 * Получение количества дисциплин. При указании кода транзакции в качестве
	 * параметра <code>trCode</code> данное действие будет выполнено как часть
	 * транзакции с указанным кодом.
	 * 
	 * @param trCode
	 *            код транзакции.
	 * @return количество дисциплин
	 * @throws DAOException
	 *             если произошла ошибка при получении количества дисциплин
	 */
	public int getDisciplinesNumber(int trCode) throws DAOException;

	/**
	 * Получение объекта дисциплины по ее id. При указании кода транзакции в
	 * качестве параметра <code>trCode</code> данное действие будет выполнено
	 * как часть транзакции с указанным кодом.
	 * 
	 * @param id
	 *            id дисциплины
	 * @param trCode
	 *            код транзакции.
	 * @return объект дисциплины с указанным id
	 * @throws DAOException
	 *             если произошла ошибка при получении дисциплины с указанным id
	 */
	public Discipline getDiscipline(int id, int trCode) throws DAOException;

	/**
	 * Обновление информации о дисциплине. При указании кода транзакции в
	 * качестве параметра <code>trCode</code> данное действие будет выполнено
	 * как часть транзакции с указанным кодом.
	 * 
	 * @param d
	 *            объект дисциплины с обновленными данными
	 * @param trCode
	 *            код транзакции.
	 * @throws DAOException
	 *             если произошла ошибка при обновлении данных дисциплины
	 */
	public void updateDiscipline(Discipline d, int trCode) throws DAOException;

	/**
	 * Удаление дисциплины из системы. При указании кода транзакции в качестве
	 * параметра <code>trCode</code> данное действие будет выполнено как часть
	 * транзакции с указанным кодом.
	 * 
	 * @param id
	 *            id дисциплины
	 * @param trCode
	 *            код транзакции.
	 * @throws DAOException
	 *             если произошла ошибка при удалении дисциплины
	 */
	public void deleteDiscipline(int id, int trCode) throws DAOException;

	/**
	 * Сохранение объекта дисциплины в источнике данных. При указании кода
	 * транзакции в качестве параметра <code>trCode</code> данное действие будет
	 * выполнено как часть транзакции с указанным кодом.
	 * 
	 * @param d
	 *            объект дисциплины
	 * @param trCode
	 *            код транзакции.
	 * @return id дисциплины, сохраненной в источнике данных
	 * @throws DAOException
	 *             если произошла ошибка при сохранении объекта дисциплины
	 */
	public int createDiscipline(Discipline d, int trCode) throws DAOException;

	/**
	 * Получение объекта дисциплины по указанному наименованию. При указании
	 * кода транзакции в качестве параметра <code>trCode</code> данное действие
	 * будет выполнено как часть транзакции с указанным кодом.
	 * 
	 * @param title
	 *            наименование дисциплины
	 * @param trCode
	 *            код транзакции
	 * @return объект дисциплины с указанным наименованием
	 * @throws DAOException
	 *             если произошла ошибка при получении дисциплины с указанным
	 *             наименованием
	 */
	public Discipline getDisciplineByTitle(String title, int trCode) throws DAOException;

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
