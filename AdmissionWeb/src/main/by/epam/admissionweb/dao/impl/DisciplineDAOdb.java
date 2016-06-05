package main.by.epam.admissionweb.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.dao.DisciplineDAO;
import main.by.epam.admissionweb.dao.exception.DAOException;
import main.by.epam.admissionweb.dao.exception.TransactionException;
import main.by.epam.admissionweb.dao.pool.ConnectionPool;
import main.by.epam.admissionweb.dao.pool.exception.ConnectionPoolException;
import main.by.epam.admissionweb.dao.pool.impl.ConnectionPoolImpl;
import main.by.epam.admissionweb.entity.Discipline;
import main.by.epam.admissionweb.generator.KeyGenerator;
import main.by.epam.admissionweb.generator.KeyGeneratorFactory;

/**
 * Класс <code>DisciplineDAOdb</code> реализует интерфейс {@link DisciplineDAO}
 * и является DAO-объектом, способным производить операции чтения/записи
 * информации о дисциплинах учебного заведения в базу данных MySQL.
 * <p>
 * Соединение с базой данных извлекается из пула соединений
 * {@link ConnectionPool}.
 * <p>
 * Реализуя интерфейс {@link DisciplineDAO}, класс <code>DisciplineDAOdb</code>
 * обеспечивает поддержку транзакций. Поддержка транзакций основана на механизме
 * сопоставления каждой конкретной транзакции с уникальным идентификатором.
 * Уникальный идентификатор отображается на транзакции с использованием карты,
 * где ключом является уникальный идентификатор, а значением - объект класса,
 * реализующего интерфейс <code>Connection</code>, связанный с транзакцией. В
 * контексте данного приложения такая карта называется <b>картой транзакций</b>.
 * <p>
 * При выполнении общих для всех DAO-объектов операций используется объект
 * класса {@link DBHelper}
 * 
 * @author Daria Krupenko
 * @see DisciplineDAO
 * @see Discipline
 * @see DBHelper
 * @see Connection
 *
 */
public class DisciplineDAOdb implements DisciplineDAO {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Карта транзакций (ключ - уникальный идентификатор транзакции, значение -
	 * <code>Connection</code>, на котором открыта транзакция)
	 */
	private static final Map<Integer, Connection> TR_MAP = new ConcurrentHashMap<Integer, Connection>();

	/**
	 * Получение списка дисциплин.
	 * <p>
	 * Количество элементов списка может быть ограничено и контролируется
	 * параметрами <code>beginIndex</code> и <code>elementsCount</code>.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param beginIndex
	 *            индекс дисциплины, с которой начинается построение списка
	 * @param elementsCount
	 *            количество требуемых дисциплин, начиная с
	 *            <code>beginIndex</code>
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return список дисциплин, начиная с дисциплины с индексом
	 *         <code>beginIndex</code>; количество элементов списка равно
	 *         <code>elementsCount</code>
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public List<Discipline> getDisciplinesList(int beginIndex, int elementsCount, int trCode) throws DAOException {
		LOGGER.debug("DAO : DisciplineDAOdb.getDisciplinesList(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		List<Discipline> list = new ArrayList<Discipline>();
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.DisciplineQuery.GET_DISCIPLINES_LIST);
			if (beginIndex == DBHelper.BEGIN_INDEX_DEFAULT && elementsCount == DBHelper.ELEMENTS_MAX_VALUE) {
				elementsCount = getDisciplinesNumber(trCode);
			}
			stat.setInt(1, beginIndex);
			stat.setInt(2, elementsCount);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				Discipline d = DBHelper.getInstance().constructDiscipline(conn, rs);
				list.add(d);
			}
			LOGGER.debug("DAO : DisciplineDAOdb.getDisciplinesList (list = {})", list);
			return list;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read disciplines list from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Получение количества дисциплин.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return количество дисциплин
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public int getDisciplinesNumber(int trCode) throws DAOException {
		LOGGER.debug("DAO : DisciplinesDAOdb.getDisciplinesNumber(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		int number = 0;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.DisciplineQuery.GET_DISCIPLINES_NUMBER);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				number = rs.getInt(1);
			}
			LOGGER.debug("DAO : DisciplineDAOdb.getDisciplinesNumber (number = {})", number);
			return number;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read disciplines number from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Получение объекта дисциплины по ее id.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param id
	 *            id дисциплины
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return объект дисциплины с указанным id
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public Discipline getDiscipline(int id, int trCode) throws DAOException {
		LOGGER.debug("DAO : DisciplineDAOdb.getDiscipline(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		Discipline d = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.DisciplineQuery.GET_DISCIPLINE);
			stat.setInt(1, id);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				d = DBHelper.getInstance().constructDiscipline(conn, rs);
			}
			LOGGER.debug("DAO : DisciplineDAOdb.getDiscipline (d = {})", d);
			return d;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read discipline from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Обновление информации о дисциплине.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param d
	 *            объект дисциплины с обновленными данными
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public void updateDiscipline(Discipline d, int trCode) throws DAOException {
		LOGGER.debug("DAO : DisciplineDAOdb.updateDiscipline(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.DisciplineQuery.UPDATE_DISCIPLINE);
			stat.setString(1, d.getTitle());
			stat.setInt(2, d.getId());
			stat.executeUpdate();
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to update discipline and write changes to the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Удаление дисциплины из системы.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param id
	 *            id дисциплины
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public void deleteDiscipline(int id, int trCode) throws DAOException {
		LOGGER.debug("DAO : DisciplineDAOdb.deleteDiscipline(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.DisciplineQuery.DELETE_DISCIPLINE);
			stat.setInt(1, id);
			stat.executeUpdate();
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to delete discipline from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Сохранение объекта дисциплины в источнике данных.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param d
	 *            объект дисциплины
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return id дисциплины, сохраненной в источнике данных
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	public int createDiscipline(Discipline d, int trCode) throws DAOException {
		LOGGER.debug("DAO : DisciplineDAOdb.createDiscipline(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		int id = 0;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.DisciplineQuery.CREATE_DISCIPLINE);
			stat.setString(1, d.getTitle());
			stat.executeUpdate();
			stat = conn.prepareStatement(DBHelper.DisciplineQuery.GET_DISCIPLINE_ID);
			stat.setString(1, d.getTitle());
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				id = rs.getInt(DBHelper.ColumnLabel.DISCIPLINE_ID);
			}
			return id;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to create discipline and write it to the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Получение объекта дисциплины по указанному наименованию.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param title
	 *            наименование дисциплины
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return объект дисциплины с указанным наименованием
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public Discipline getDisciplineByTitle(String title, int trCode) throws DAOException {
		LOGGER.debug("DAO : DisciplineDAOdb.getDisciplineByTitle(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		Discipline d = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.DisciplineQuery.GET_DISCIPLINE_BY_TITLE);
			stat.setString(1, title);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				d = DBHelper.getInstance().constructDiscipline(conn, rs);
			}
			LOGGER.debug("DAO : DisciplineDAOdb.getDisciplineByTitle (d = {})", d);
			return d;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read discipline from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Создание новой транзакции.
	 * <p>
	 * Как правило, вызывается классами логики приложения.
	 * <p>
	 * При создании новой транзакции генерируется уникальный идентификатор
	 * транзакции при помощи класса, реализующего интерфейс {@link KeyGenerator}
	 * . Транзакция создается на <code>Connection</code>, которое извлекается из
	 * пула соединений. Далее идентификатор транзакции и связанное с ней
	 * соединение <code>Connection</code> заносится в карту транзакций.
	 * 
	 * @return код транзакции
	 * @throws TransactionException
	 *             если не удается получить соединениуе из пула или открыть
	 *             транзакцию на данном соединении.
	 */
	@Override
	public int beginTransaction() throws TransactionException {
		try {
			KeyGenerator generator = KeyGeneratorFactory.getInstance().getKeyGenerator();
			int trCode = generator.getGeneratedKey();
			ConnectionPool pool = ConnectionPoolImpl.getInstance();
			Connection conn = pool.takeConnection();
			conn.setAutoCommit(false);
			TR_MAP.put(trCode, conn);
			LOGGER.debug("DAO : transaction begins; transaction code = {}", trCode);
			return trCode;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new TransactionException("DAO : Unable to begin transaction", ex);
		}
	}

	/**
	 * Откат транзакции с указанным в параметре <code>trCode</code> кодом.
	 * <p>
	 * Как правило, вызывается классами логики приложения.
	 * <p>
	 * Откатываемая транзакция удаляется из карты транзакций.
	 * 
	 * @param trCode
	 *            код транзакции
	 * @throws TransactionException
	 *             если при откате транзакции произошла ошибка
	 */
	@Override
	public void rollbackTransaction(int trCode) throws TransactionException {
		Connection conn = null;
		try {
			conn = TR_MAP.get(trCode);
			conn.rollback();
			TR_MAP.remove(trCode);
			LOGGER.debug("DAO : transaction rollback; transaction code = {}", trCode);
		} catch (SQLException ex) {
			throw new TransactionException("DAO : Unable to rollback transaction", ex);
		} finally {
			try {
				conn.setAutoCommit(true);
				ConnectionPoolImpl.getInstance().returnConnection(conn);
			} catch (SQLException | ConnectionPoolException ex) {
				throw new TransactionException("DAO : Unable to return connection", ex);
			}
		}
	}

	/**
	 * Завершение транзакции с указанным в параметре <code>trCode</code> кодом.
	 * <p>
	 * Как правило, вызывается классами логики приложения.
	 * <p>
	 * При завершении транзакции она удаляется из карты транзакций и
	 * ассоциированное с ней соединение <code>Connection</code> возвращается в
	 * пул соединений с базой данных.
	 * 
	 * @param trCode
	 *            код транзакции
	 * @throws TransactionException
	 *             если не удается завершить транзакцию или вернуть ее в пул
	 *             соединений.
	 */
	@Override
	public void commitTransaction(int trCode) throws TransactionException {
		Connection conn = null;
		try {
			conn = TR_MAP.get(trCode);
			conn.commit();
			TR_MAP.remove(trCode);
			LOGGER.debug("DAO : transaction commit; transaction code = {}", trCode);
		} catch (SQLException ex) {
			throw new TransactionException("DAO : Unable to commit transaction", ex);
		} finally {
			try {
				conn.setAutoCommit(true);
				ConnectionPoolImpl.getInstance().returnConnection(conn);
			} catch (SQLException | ConnectionPoolException ex) {
				throw new TransactionException("DAO : Unable to return connection", ex);
			}
		}
	}

}
