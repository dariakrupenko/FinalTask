package main.by.epam.admissionweb.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.dao.RegisterDAO;
import main.by.epam.admissionweb.dao.exception.DAOException;
import main.by.epam.admissionweb.dao.exception.TransactionException;
import main.by.epam.admissionweb.dao.pool.ConnectionPool;
import main.by.epam.admissionweb.dao.pool.exception.ConnectionPoolException;
import main.by.epam.admissionweb.dao.pool.impl.ConnectionPoolImpl;
import main.by.epam.admissionweb.entity.Applicant;
import main.by.epam.admissionweb.entity.Discipline;
import main.by.epam.admissionweb.entity.Enroll;
import main.by.epam.admissionweb.entity.Faculty;
import main.by.epam.admissionweb.entity.RegisterRecord;
import main.by.epam.admissionweb.generator.KeyGenerator;
import main.by.epam.admissionweb.generator.KeyGeneratorFactory;

/**
 * Класс <code>RegisterDAOdb</code> реализует интерфейс {@link RegisterDAO} и
 * является DAO-объектом, способным производить операции чтения/записи
 * информации о ведомости заведения в базу данных MySQL.
 * <p>
 * Соединение с базой данных извлекается из пула соединений
 * {@link ConnectionPool}.
 * <p>
 * Реализуя интерфейс {@link RegisterDAO}, класс <code>RegisterDAOdb</code>
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
 * @see RegisterDAO
 * @see Register
 * @see DBHelper
 * @see Connection
 *
 */
public class RegisterDAOdb implements RegisterDAO {

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
	 * Сохранение записи ведомости в источнике данных.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param r
	 *            объект записи ведомости
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return id записи ведомости, сохраненной в источнике данных
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public void createRecord(RegisterRecord r, int trCode) throws DAOException {
		LOGGER.debug("DAO : RegisterDAOdb.createRecord(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
				conn.setAutoCommit(false);
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.RegisterQuery.CREATE_RECORD);
			setRecordParameters(stat, r);
			stat.executeUpdate();
			insertScoresByRecord(conn, r);
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
							"DAO : Unable to add record to the database (some problems with transaction rollback)",
							ex1);
				}
			}
			throw new DAOException("DAO : Unable to add record to the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}

	}

	/**
	 * Получение объекта записи ведомости абитуриента.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param a
	 *            абитуриент, чью запись нужно прочесть из ведомости
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return объект записи ведомости абитуриента
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public RegisterRecord getRecord(Applicant a, int trCode) throws DAOException {
		LOGGER.debug("DAO : RegisterDAOdb.getRecord(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		RegisterRecord r = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.RegisterQuery.GET_RECORD_BY_APPLICANT);
			stat.setInt(1, a.getId());
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				r = constructRecord(conn, rs, a.getId());
			}
			LOGGER.debug("DAO : RegisterDAOdb.getRecord (r = {})", r);
			return r;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read register record from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Удаление записи ведомости абитуриента из системы.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param a
	 *            абитуриент, чью запись нужно удалить из ведомости
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public void deleteRecord(Applicant a, int trCode) throws DAOException {
		LOGGER.debug("DAO : RegisterDAOdb.deleteRecord(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
				conn.setAutoCommit(false);
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.RegisterQuery.DELETE_RECORD);
			stat.setInt(1, a.getId());
			stat.executeUpdate();
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to delete record from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}

	}

	/**
	 * Получение количества записей ведомости в рамках указанного набора.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param e
	 *            набор, в рамках которого нужно получить ведомость
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return количество записей ведомости в рамках набора <code>e</code>
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public int getRecordsNumber(Enroll e, int trCode) throws DAOException {
		LOGGER.debug("DAO : RegisterDAOdb.getRecordsNumber(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		int number = 0;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.RegisterQuery.GET_RECORDS_NUMBER);
			stat.setInt(1, e.getId());
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				number = rs.getInt(1);
			}
			LOGGER.debug("DAO : RegisterDAOdb.getRecordsNumber (number = {})", number);
			return number;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to get records number from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Получение ведомости.
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
	 *            индекс записи ведомости, с которой начинается построение
	 *            списка
	 * @param elementsCount
	 *            количество требуемых записей ведомости, начиная с
	 *            <code>beginIndex</code>
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return ведомость, начиная с записи с индексом <code>beginIndex</code>;
	 *         количество элементов списка равно <code>elementsCount</code>
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public List<RegisterRecord> getRegister(int beginIndex, int elementsCount, Enroll e, int trCode)
			throws DAOException {
		LOGGER.debug("DAO : RegisterDAOdb.getRegister(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		List<RegisterRecord> list = new ArrayList<RegisterRecord>();
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.RegisterQuery.GET_REGISTER);
			stat.setInt(1, e.getId());
			if (beginIndex == DBHelper.BEGIN_INDEX_DEFAULT && elementsCount == DBHelper.ELEMENTS_MAX_VALUE) {
				elementsCount = getRecordsNumber(e, trCode);
			}
			stat.setInt(2, beginIndex);
			stat.setInt(3, elementsCount);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				RegisterRecord r = constructRecord(conn, rs, rs.getInt(DBHelper.ColumnLabel.APPLICANT_ID));
				list.add(r);
			}
			LOGGER.debug("DAO : RegisterDAOdb.getRegister (list = {})", list);
			return list;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to get register list from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Получение количества записей ведомости в рамках указанного набора,
	 * факультета и статуса абитуриентов.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param isAdmitted
	 *            статус абитуриента: true - (временно)зачислен, false -
	 *            (временно)незачислен
	 * @param f
	 *            факультет, в рамках которого необходимо получить ведомость
	 * @param e
	 *            набор, в рамках которого нужно получить ведомость
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return количество записей ведомости в рамках набора, факультета и
	 *         статуса абитуриентов <code>e</code>
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public int getRecordsNumberByStatusAndFaculty(boolean isAdmitted, Faculty f, Enroll e, int trCode)
			throws DAOException {
		LOGGER.debug("DAO : RegisterDAOdb.getRecordsNumberByStatusAndFaculty(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		int number = 0;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn
					.prepareStatement(DBHelper.RegisterQuery.GET_RECORDS_NUMBER_BY_STATUS_FACULTY_ENROLL);
			String status = null;
			String tempStatus = null;
			if (isAdmitted) {
				status = DBHelper.ADMITTED_STATUS;
				tempStatus = DBHelper.TEMP_ADMITTED_STATUS;
			} else {
				status = DBHelper.NOT_ADMITTED_STATUS;
				tempStatus = DBHelper.TEMP_NOT_ADMITTED_STATUS;
			}
			stat.setString(1, status);
			stat.setString(2, tempStatus);
			stat.setInt(3, f.getId());
			stat.setInt(4, e.getId());
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				number = rs.getInt(1);
			}
			LOGGER.debug("DAO : RegisterDAOdb.getRecordsNumberByStatusAndFaculty (number = {})", number);
			return number;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read records number by status and faculty from database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Получение ведомости.
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
	 *            индекс записи ведомости, с которой начинается построение
	 *            списка
	 * @param elementsCount
	 *            количество требуемых записей ведомости, начиная с
	 *            <code>beginIndex</code>
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return ведомость, начиная с записи с индексом <code>beginIndex</code>;
	 *         количество элементов списка равно <code>elementsCount</code>
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public List<RegisterRecord> getRegisterByStatusAndFaculty(int beginIndex, int elementsCount, boolean isAdmitted,
			Faculty f, Enroll e, int trCode) throws DAOException {
		LOGGER.debug("DAO : RegisterDAOdb.getRecordsNumberByStatusAndFaculty(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		List<RegisterRecord> list = new ArrayList<RegisterRecord>();
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.RegisterQuery.GET_RECORDS_BY_STATUS_FACULTY_ENROLL);
			String status = null;
			String tempStatus = null;
			if (isAdmitted) {
				status = DBHelper.ADMITTED_STATUS;
				tempStatus = DBHelper.TEMP_ADMITTED_STATUS;
			} else {
				status = DBHelper.NOT_ADMITTED_STATUS;
				tempStatus = DBHelper.TEMP_NOT_ADMITTED_STATUS;
			}
			stat.setString(1, status);
			stat.setString(2, tempStatus);
			stat.setInt(3, f.getId());
			stat.setInt(4, e.getId());
			stat.setInt(5, beginIndex);
			stat.setInt(6, elementsCount);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				RegisterRecord r = constructRecord(conn, rs, rs.getInt(DBHelper.ColumnLabel.REGISTER_APPLICANTS_ID));
				list.add(r);
			}
			LOGGER.debug("DAO : RegisterDAOdb.getRecordsNumberByStatusAndFaculty (list = {})", list);
			return list;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read records list by status and faculty from database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Установить новый проходной балл факультета.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param f
	 *            факультет, проходной балл которого нужно изменить
	 * @param newPassRate
	 *            новый проходной балл
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public void setNewPassRate(Faculty f, int newPassRate, int trCode) throws DAOException {
		LOGGER.debug("DAO : RegisterDAOdb.setNewPassRate(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			LOGGER.debug("CONNECTION = {}", conn);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			LOGGER.debug("CONNECTION = {}", conn);
			PreparedStatement stat = conn.prepareStatement(DBHelper.FacultyQuery.SET_PASS_RATE);
			stat.setInt(1, newPassRate);
			stat.setInt(2, f.getId());
			stat.executeUpdate();
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read records list by status and faculty from database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Обновление статусов абитуриентов в рамках факультета и набора.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
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
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public void updateStatuses(boolean isAdmitted, Faculty f, Enroll e, int trCode) throws DAOException {
		LOGGER.debug("DAO : RegisterDAOdb.updateStatuses(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			List<Integer> list = null;
			String query = null;
			if (isAdmitted) {
				list = readStatusIds(conn, f, e, DBHelper.RegisterQuery.GET_ADMITTED_RECORDS, isAdmitted);
				query = DBHelper.RegisterQuery.UPDATE_ADMITTED_STATUSES;
			} else {
				list = readStatusIds(conn, f, e, DBHelper.RegisterQuery.GET_NOT_ADMITTED_RECORDS, isAdmitted);
				query = DBHelper.RegisterQuery.UPDATE_NOT_ADMITTED_STATUSES;
			}
			for (int i : list) {
				PreparedStatement stat = conn.prepareStatement(query);
				stat.setInt(1, i);
				stat.executeUpdate();
			}
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read records list by status and faculty from database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}

	}

	/**
	 * Получить изменившийся проходной балл указанного факультета.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param f
	 *            факультет, проходной балл которого необходимо получить
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return изменившийся проходной балл указанного факультета
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public int getChangedPassRate(Faculty f, int trCode) throws DAOException {
		LOGGER.debug("DAO : RegisterDAOdb.readChangedPassRate(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.RegisterQuery.GET_CHANGED_PASS_RATE);
			stat.setInt(1, f.getId());
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
			return 0;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read records list by status and faculty from database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Получение количества записей ведомости в рамках указанного факультета и
	 * набора.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param f
	 *            факультет, в рамках которого нужно получить количество записей
	 *            ведомости
	 * @param e
	 *            набор, в рамках которого нужно получить количество записей
	 *            ведомости
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return количество записей ведомости в рамках указанного набора и
	 *         факультета
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public int getRecordsNumberByFaculty(Faculty f, Enroll e, int trCode) throws DAOException {
		LOGGER.debug("DAO : RegisterDAOdb.readRecordsNumberByFaculty(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.RegisterQuery.GET_RECORDS_COUNT_BY_FACULTY);
			stat.setInt(1, f.getId());
			stat.setInt(2, e.getId());
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				LOGGER.debug("RN");
				return rs.getInt(1);
			}
			return 0;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read records list by status and faculty from database", ex);
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
	 * записи ведомости.
	 * 
	 * @param stat
	 *            подготовленный запрос
	 * @param r
	 *            объект записи ведомости
	 * @throws SQLException
	 *             если при установке какого-либо параметра произошла ошибка
	 */
	private void setRecordParameters(PreparedStatement stat, RegisterRecord r) throws SQLException {
		stat.setInt(1, r.getApplicant().getId());
		stat.setInt(2, r.getFaculty().getId());
		stat.setInt(3, r.getCertificateScore());
		stat.setInt(4, r.getTotalScore());
		stat.setString(5, r.getStatus());
		stat.setInt(6, r.getEnroll().getId());
	}

	/**
	 * Запись данных в ассоциативную таблицу для создания связи между записью
	 * ведомости, указанной в параметре <code>r</code>, дисциплинами и баллами
	 * по соответсвующим дисциплинам
	 * 
	 * @param conn
	 *            соединение с базой данных
	 * @param r
	 *            объект записи ведомости
	 * @throws SQLException
	 *             если не удалось установить какой-либо параметр или выполнить
	 *             запрос на обновление базы данных
	 */
	private void insertScoresByRecord(Connection conn, RegisterRecord r) throws SQLException {
		for (Map.Entry<Discipline, Integer> s : r.getScores().entrySet()) {
			PreparedStatement stat = conn.prepareStatement(DBHelper.RegisterQuery.ADD_DISCIPLINE_BY_RECORD);
			stat.setInt(1, r.getApplicant().getId());
			stat.setInt(2, s.getKey().getId());
			stat.setInt(3, s.getValue());
			stat.executeUpdate();
		}
	}

	/**
	 * Конструирование сущности записи ведомости посредством последовательного
	 * извлечения данных из разных таблиц.
	 * 
	 * @param conn
	 *            соединение с базой данных
	 * @param rs
	 *            результирующий набор, который представляет собой выборку из
	 *            таблицы с информацией о ведомости
	 * @param id
	 *            id абитуриента, которому принадлежит конструируемая запись
	 *            ведомости
	 * @return запись ведомости, содержащая полную информацию об абитуриенте,
	 *         факультет, наборе, а также дополнительную информацию (балл
	 *         аттестата, суммарный балл, статус)
	 * @throws SQLException
	 *             если при чтении данных произошла ошибка
	 */
	private RegisterRecord constructRecord(Connection conn, ResultSet rs, int id) throws SQLException {
		RegisterRecord r = new RegisterRecord();
		Applicant a = readApplicant(conn, id);
		r.setApplicant(a);
		r.setCertificateScore(rs.getInt(DBHelper.ColumnLabel.REGISTER_CERT_SCORE));
		r.setTotalScore(rs.getInt(DBHelper.ColumnLabel.REGISTER_TOTAL_SCORE));
		r.setStatus(rs.getString(DBHelper.ColumnLabel.REGISTER_STATUS));
		Enroll e = readEnroll(conn, id);
		r.setEnroll(e);
		Faculty f = readFaculty(conn, id, e);
		r.setFaculty(f);
		Map<Discipline, Integer> scores = readScores(conn, id);
		r.setScores(scores);
		return r;
	}

	/**
	 * Извлечение информации о факультете, связанном с записью ведомости,
	 * абитуриент которой имеет id, указанный параметром <code>id</code>.
	 * 
	 * @param conn
	 *            соединение с базой данных
	 * @param id
	 *            id абитуриента
	 * @param e
	 *            набор, в рамках которого извлекается информация о факультете
	 * @return объект факультета со всей необходимой информацией
	 * @throws SQLException
	 *             если при чтении данных произошла ошибка
	 */
	private Faculty readFaculty(Connection conn, int id, Enroll e) throws SQLException {
		DBHelper helper = DBHelper.getInstance();
		Faculty f = null;
		PreparedStatement stat = conn.prepareStatement(DBHelper.RegisterQuery.GET_FACULTY_BY_RECORD);
		stat.setInt(1, id);
		ResultSet rs = stat.executeQuery();
		while (rs.next()) {
			f = helper.constructFaculty(conn, rs, e);
		}
		return f;
	}

	/**
	 * Извлечение информации о наборе, связанном с записью ведомости, абитуриент
	 * которой имеет id, указанный параметром <code>id</code>.
	 * 
	 * @param conn
	 *            соединение с базой данных
	 * @param id
	 *            id абитуриента
	 * @return объект набора со всей необходимой информацией
	 * @throws SQLException
	 *             если при чтении данных произошла ошибка
	 */
	private Enroll readEnroll(Connection conn, int id) throws SQLException {
		DBHelper helper = DBHelper.getInstance();
		Enroll e = null;
		PreparedStatement stat = conn.prepareStatement(DBHelper.RegisterQuery.GET_ENROLL_BY_RECORD);
		stat.setInt(1, id);
		ResultSet rs = stat.executeQuery();
		while (rs.next()) {
			e = helper.constructEnroll(rs);
		}
		return e;
	}

	/**
	 * Извлечение информации об абитуриента, которой имеет id, указанный
	 * параметром <code>id</code>.
	 * 
	 * @param conn
	 *            соединение с базой данных
	 * @param id
	 *            id абитуриента
	 * @return объект абитуриента со всей необходимой информацией
	 * @throws SQLException
	 *             если при чтении данных произошла ошибка
	 */
	private Applicant readApplicant(Connection conn, int id) throws SQLException {
		DBHelper helper = DBHelper.getInstance();
		Applicant a = null;
		PreparedStatement stat = conn.prepareStatement(DBHelper.ApplicantQuery.GET_APPLICANT);
		stat.setInt(1, id);
		ResultSet rs = stat.executeQuery();
		while (rs.next()) {
			a = helper.constructApplicant(rs);
		}
		return a;
	}

	/**
	 * Извлечение информации о баллах по дисциплинам абитуриента, которой имеет
	 * id, указанный параметром <code>id</code>.
	 * 
	 * @param conn
	 *            соединение с базой данных
	 * @param id
	 *            id абитуриента
	 * @return карта, где ключ - дисциплина, значение - балл по соответствующей
	 *         дисциплине
	 * @throws SQLException
	 *             если при чтении данных произошла ошибка
	 */
	private Map<Discipline, Integer> readScores(Connection conn, int id) throws SQLException {
		Map<Discipline, Integer> map = new TreeMap<Discipline, Integer>();
		PreparedStatement stat = conn.prepareStatement(DBHelper.RegisterQuery.GET_SCORES_BY_RECORD);
		stat.setInt(1, id);
		ResultSet rs = stat.executeQuery();
		while (rs.next()) {
			Discipline d = DBHelper.getInstance().constructDiscipline(conn, rs);
			int score = rs.getInt(DBHelper.ColumnLabel.REGISTER_DISCIPLINE_SCORE);
			map.put(d, score);
		}
		return map;
	}

	/**
	 * Получение списка, содержащего id абитуриентов, которым в рамках набора
	 * <code>e</code> и в соответствии с планом набора факультета, указанного в
	 * параметре <code>f</code>, необходимо присвоить статус, указанный в
	 * параметре <code>isAdmitted</code>.
	 * 
	 * @param conn
	 *            соединение с базой данных
	 * @param f
	 *            факультет
	 * @param e
	 *            набор
	 * @param query
	 *            строка SQL-запроса (варьируется в зависимости от параметра
	 *            <code>isAdmitted</code>)
	 * @param isAdmitted
	 *            статус абитуриентов; true - (временно)зачислен, false -
	 *            (временно)незачислен
	 * @return список id абитуриентов с определенным статусом
	 * @throws SQLException
	 *             если при чтении данных произошла ошибка
	 */
	private List<Integer> readStatusIds(Connection conn, Faculty f, Enroll e, String query, boolean isAdmitted)
			throws SQLException {
		List<Integer> list = new ArrayList<Integer>();
		PreparedStatement stat = conn.prepareStatement(query);
		stat.setInt(1, f.getId());
		stat.setInt(2, e.getId());
		stat.setInt(3, f.getPlan());
		if (!isAdmitted) {
			stat.setInt(4, DBHelper.ELEMENTS_MAX_VALUE);
		}
		ResultSet rs = stat.executeQuery();
		while (rs.next()) {
			list.add(rs.getInt(1));
		}
		return list;
	}

}
