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

import main.by.epam.admissionweb.dao.FacultyDAO;
import main.by.epam.admissionweb.dao.exception.DAOException;
import main.by.epam.admissionweb.dao.exception.TransactionException;
import main.by.epam.admissionweb.dao.pool.ConnectionPool;
import main.by.epam.admissionweb.dao.pool.exception.ConnectionPoolException;
import main.by.epam.admissionweb.dao.pool.impl.ConnectionPoolImpl;
import main.by.epam.admissionweb.entity.Discipline;
import main.by.epam.admissionweb.entity.Enroll;
import main.by.epam.admissionweb.entity.Faculty;
import main.by.epam.admissionweb.generator.KeyGenerator;
import main.by.epam.admissionweb.generator.KeyGeneratorFactory;

/**
 * Класс <code>FacultyDAOdb</code> реализует интерфейс {@link FacultyDAO} и
 * является DAO-объектом, способным производить операции чтения/записи
 * информации о наборах учебного заведения в базу данных MySQL.
 * <p>
 * Соединение с базой данных извлекается из пула соединений
 * {@link ConnectionPool}.
 * <p>
 * Реализуя интерфейс {@link FacultyDAO}, класс <code>FacultyDAOdb</code>
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
 * @see FacultyDAO
 * @see Faculty
 * @see DBHelper
 * @see Connection
 *
 */
public class FacultyDAOdb implements FacultyDAO {

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
	 * Получение списка факультетов.
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
	 *            индекс факультета, с которого начинается построение списка
	 * @param elementsCount
	 *            количество требуемых факультетов, начиная с
	 *            <code>beginIndex</code>
	 * @param e
	 *            набор, в контексте которого подсчитывается количество
	 *            записавшихся абитуриентов
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return список факультетов, начиная с факультета с индексом
	 *         <code>beginIndex</code>; количество элементов списка равно
	 *         <code>elementsCount</code>
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public List<Faculty> getFacultiesList(int beginIndex, int elementsCount, Enroll e, int trCode) throws DAOException {
		LOGGER.debug("DAO : FacultyDAOdb.getFacultiesList(transactionCode = {})", trCode);
		DBHelper helper = DBHelper.getInstance();
		ConnectionPool pool = null;
		Connection conn = null;
		List<Faculty> list = new ArrayList<Faculty>();
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.FacultyQuery.GET_FACULTIES_LIST);
			if (beginIndex == DBHelper.BEGIN_INDEX_DEFAULT && elementsCount == DBHelper.ELEMENTS_MAX_VALUE) {
				elementsCount = getFacultiesNumber(trCode);
			}
			stat.setInt(1, beginIndex);
			stat.setInt(2, elementsCount);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				Faculty f = helper.constructFaculty(conn, rs, e);
				list.add(f);
			}
			LOGGER.debug("DAO : FacultyDAOdb.getFacultiesList (list = {})", list);
			return list;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to get faculties list from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Получение количества факультетов.
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
	 * @return количество факультетов
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public int getFacultiesNumber(int trCode) throws DAOException {
		LOGGER.debug("DAO : FacultyDAOdb.getFacultiesNumber(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		int number = 0;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.FacultyQuery.GET_FACULTIES_NUMBER);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				number = rs.getInt(1);
			}
			LOGGER.debug("DAO : FacultyDAOdb.getFacultiesNumber (number = {})", number);
			return number;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read faculties number from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Сохранение объекта факультета в источнике данных.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param f
	 *            объект факультета
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return id факультета, сохраненного в источнике данных
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public int createFaculty(Faculty f, int trCode) throws DAOException {
		LOGGER.debug("DAO : FacultyDAOdb.createFaculty(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		int id = 0;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
				conn.setAutoCommit(false);
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.FacultyQuery.CREATE_FACULTY);
			setFacultyParameters(stat, f);
			stat.executeUpdate();
			stat = conn.prepareStatement(DBHelper.FacultyQuery.GET_FACULTY_ID);
			stat.setString(1, f.getTitle());
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				id = rs.getInt(1);
			}
			f.setId(id);
			insertDisciplinesByFaculty(conn, f);
			if (trCode == DBHelper.NO_TRANSACTION) {
				conn.commit();
				conn.setAutoCommit(true);
			}
			return id;
		} catch (ConnectionPoolException | SQLException ex) {
			if (trCode == DBHelper.NO_TRANSACTION) {
				try {
					conn.rollback();
				} catch (SQLException ex1) {
					throw new DAOException("DAO : Unable to create faculty and write it to the database", ex1);
				}
			}
			throw new DAOException("DAO : Unable to create faculty and write it to the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Получение объекта факультета по его id.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param id
	 *            id факультета
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @param e
	 *            набор, в контексте которого подсчитывается количество
	 *            записавшихся абитуриентов
	 * @return объект факультета с указанным id
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public Faculty getFaculty(int id, Enroll e, int trCode) throws DAOException {
		LOGGER.debug("DAO : FacultyDAOdb.getFaculty(transaction code = {})", trCode);
		DBHelper helper = DBHelper.getInstance();
		ConnectionPool pool = null;
		Connection conn = null;
		Faculty f = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.FacultyQuery.GET_FACULTY);
			stat.setInt(1, id);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				f = helper.constructFaculty(conn, rs, e);
			}
			LOGGER.debug("DAO : FacultyDAOdb.getFaculty (f = {})", f);
			return f;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read faculty from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Обновление информации о факультете.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param d
	 *            объект факультета с обновленными данными
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public void updateFaculty(Faculty f, int trCode) throws DAOException {
		LOGGER.debug("DAO : FacultyDAOdb.updateFaculty(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
				conn.setAutoCommit(false);
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.FacultyQuery.UPDATE_FACULTY);
			setFacultyParameters(stat, f);
			stat.setInt(8, f.getId());
			stat.executeUpdate();
			stat = conn.prepareStatement(DBHelper.FacultyQuery.REMOVE_DISCIPLINES_BY_FACULTY);
			stat.setInt(1, f.getId());
			stat.executeUpdate();
			insertDisciplinesByFaculty(conn, f);
			if (trCode == DBHelper.NO_TRANSACTION) {
				conn.commit();
				conn.setAutoCommit(true);
			}
		} catch (ConnectionPoolException | SQLException ex) {
			if (trCode == DBHelper.NO_TRANSACTION) {
				try {
					conn.rollback();
				} catch (SQLException ex1) {
					throw new DAOException(
							"DAO : Unable to update faculty and write it to the database (some problems with transaction rollback)",
							ex1);
				}
			}
			throw new DAOException("DAO : Unable to update faculty and write it to the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}

	}

	/**
	 * Удаление факультета из системы.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param id
	 *            id факультета
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public void deleteFaculty(int id, int trCode) throws DAOException {
		LOGGER.debug("DAO : FacultyDAOdb.deleteFaculty(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.FacultyQuery.DELETE_FACULTY);
			stat.setInt(1, id);
			stat.executeUpdate();
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to delete faculty from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Получение объекта факультета по указанному наименованию.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param title
	 *            наименование факультета
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return объект факультета с указанным наименованием
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public Faculty getFacultyByTitle(String title, int trCode) throws DAOException {
		LOGGER.debug("DAO : FacultyDAOdb.getFacultyByTitle(transaction code = {})", trCode);
		DBHelper helper = DBHelper.getInstance();
		ConnectionPool pool = null;
		Connection conn = null;
		Faculty f = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.FacultyQuery.GET_FACULTY_BY_TITLE);
			stat.setString(1, title);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				f = helper.constructFaculty(conn, rs, null);
			}
			LOGGER.debug("DAO : FacultyDAOdb.getFacultyByTitle (f = {})", f);
			return f;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read faculty from the database", ex);
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

	/**
	 * Установка параметров подготовленного запроса
	 * <code>PreparedStatement</code> в соответствии с атрибутами сущности
	 * факультета.
	 * 
	 * @param stat
	 *            подготовленный запрос
	 * @param f
	 *            объект факультета
	 * @throws SQLException
	 *             если при установке какого-либо параметра произошла ошибка
	 */
	private void setFacultyParameters(PreparedStatement stat, Faculty f) throws SQLException {
		stat.setString(1, f.getTitle());
		stat.setString(2, f.getDescription());
		stat.setString(3, f.getLogoname());
		stat.setString(4, f.getPhone());
		stat.setString(5, f.getAddress());
		stat.setString(6, f.getDean());
		stat.setInt(7, f.getPlan());
	}

	/**
	 * Запись данных в ассоциативную таблицу для создания связи между
	 * факультетом, указанным в параметре <code>f</code>, и его дисциплинами.
	 * 
	 * @param conn
	 *            соединение с базой данных
	 * @param f
	 *            объект факультета (содержит в себе список дисциплин)
	 * @throws SQLException
	 *             если не удалось установить какой-либо параметр или выполнить
	 *             запрос на обновление базы данных
	 */
	private void insertDisciplinesByFaculty(Connection conn, Faculty f) throws SQLException {
		if (f.getDisciplines() != null && !f.getDisciplines().isEmpty()) {
			for (Discipline d : f.getDisciplines()) {
				PreparedStatement stat = conn.prepareStatement(DBHelper.FacultyQuery.ADD_DISCIPLINE_TO_FACULTY);
				stat.setInt(1, f.getId());
				stat.setInt(2, d.getId());
				stat.executeUpdate();
			}
		}
	}

}
