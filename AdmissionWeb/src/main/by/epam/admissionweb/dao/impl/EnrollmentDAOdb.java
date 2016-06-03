package main.by.epam.admissionweb.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.dao.EnrollmentDAO;
import main.by.epam.admissionweb.dao.exception.DAOException;
import main.by.epam.admissionweb.dao.exception.TransactionException;
import main.by.epam.admissionweb.dao.pool.ConnectionPool;
import main.by.epam.admissionweb.dao.pool.exception.ConnectionPoolException;
import main.by.epam.admissionweb.dao.pool.impl.ConnectionPoolImpl;
import main.by.epam.admissionweb.entity.Enroll;
import main.by.epam.admissionweb.generator.KeyGenerator;
import main.by.epam.admissionweb.generator.KeyGeneratorFactory;

/**
 * Класс <code>EnrollmentDAOdb</code> реализует интерфейс {@link EnrollmentDAO}
 * и является DAO-объектом, способным производить операции чтения/записи
 * информации о наборах учебного заведения в базу данных MySQL.
 * <p>
 * Соединение с базой данных извлекается из пула соединений
 * {@link ConnectionPool}.
 * <p>
 * Реализуя интерфейс {@link EnrollmentDAO}, класс <code>EnrollmentDAOdb</code>
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
 * @see EnrollmentDAO
 * @see Enroll
 * @see DBHelper
 * @see Connection
 *
 */
public class EnrollmentDAOdb implements EnrollmentDAO {

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
	 * Получение списка наборов.
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
	 *            индекс набора, с которого начинается построение списка
	 * @param elementsCount
	 *            количество требуемых наборов, начиная с
	 *            <code>beginIndex</code>
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return список наборов, начиная с набора с индексом
	 *         <code>beginIndex</code>; количество элементов списка равно
	 *         <code>elementsCount</code>
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public List<Enroll> getEnrollsList(int beginIndex, int elementsCount, int trCode) throws DAOException {
		LOGGER.debug("DAO : EnrollmentDAOdb.getEnrollsList(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		List<Enroll> list = new ArrayList<Enroll>();
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.EnrollmentQuery.GET_ENROLLS_LIST);
			if (beginIndex == DBHelper.BEGIN_INDEX_DEFAULT && elementsCount == DBHelper.ELEMENTS_MAX_VALUE) {
				elementsCount = getEnrollsNumber(trCode);
			}
			stat.setInt(1, beginIndex);
			stat.setInt(2, elementsCount);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				Enroll e = DBHelper.getInstance().constructEnroll(rs);
				list.add(e);
			}
			LOGGER.debug("DAO : EnrollmentDAOdb.getEnrollsList (list = {})", list);
			return list;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read enrolls list from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Получение количества наборов.
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
	 * @return количество наборов
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public int getEnrollsNumber(int trCode) throws DAOException {
		LOGGER.debug("DAO : EnrollmentDAOdb.getEnrollsNumber(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		int number = 0;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.EnrollmentQuery.GET_ENROLLS_NUMBER);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				number = rs.getInt(1);
			}
			LOGGER.debug("DAO : EnrollmentDAOdb.getEnrollsNumber (number = {})", number);
			return number;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read enrolls number from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Сохранение объекта набора в источнике данных.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param e
	 *            объект набора
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return id набора, сохраненного в источнике данных
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public int createEnroll(Enroll e, int trCode) throws DAOException {
		LOGGER.debug("DAO : EnrollmentDAOdb.createEnroll(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		int id = 0;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = null;
			stat = conn.prepareStatement(DBHelper.EnrollmentQuery.CREATE_ENROLL);
			setEnrollParameters(stat, e);
			stat.executeUpdate();
			stat = conn.prepareStatement(DBHelper.EnrollmentQuery.GET_ENROLL_ID);
			stat.setDate(1, new Date(e.getBeginDate().getTime()));
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				id = rs.getInt(1);
			}
			return id;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to create enroll and write it to the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Удаление набора из системы.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param id
	 *            id набора
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public void deleteEnroll(int id, int trCode) throws DAOException {
		LOGGER.debug("DAO : EnrollmentDAOdb.deleteEnroll(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.EnrollmentQuery.DELETE_ENROLL);
			stat.setInt(1, id);
			stat.executeUpdate();
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to delete enroll from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}

	}

	/**
	 * Получение наборов с указанным статусом.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param b
	 *            статус набора: true - активный, false - закрытый
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return список наборов с указанным статусом
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public List<Enroll> getEnrollsByStatus(boolean b, int trCode) throws DAOException {
		LOGGER.debug("DAO : EnrollmentDAOdb.getEnrollsByStatus(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		List<Enroll> list = new ArrayList<Enroll>();
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.EnrollmentQuery.GET_ENROLLS_BY_STATUS);
			stat.setBoolean(1, b);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				Enroll e = DBHelper.getInstance().constructEnroll(rs);
				list.add(e);
			}
			LOGGER.debug("DAO : EnrollmentDAOdb.getEnrollsByStatus (list = {})", list);
			return list;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to get enrolls list by status from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Получение последнего набора учебного заведения.
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
	 * @return объект последнего набора учебного заведения
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public Enroll getLastEnroll(int trCode) throws DAOException {
		LOGGER.debug("DAO : EnrollmentDAOdb.getLastEnroll(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		Enroll e = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.EnrollmentQuery.GET_LAST_ENROLL);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				e = DBHelper.getInstance().constructEnroll(rs);
			}
			LOGGER.debug("DAO : EnrollmentDAOdb.getLastEnroll (e = {})", e);
			return e;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read last enroll from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Завершение набора.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param e
	 *            набор, который необоходимо завершить
	 * @param d
	 *            дата завершения набора
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public void completeEnroll(Enroll e, java.util.Date d, int trCode) throws DAOException {
		LOGGER.debug("DAO : EnrollmentDAOdb.completeEnroll(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.EnrollmentQuery.COMPLETE_ENROLL);
			stat.setDate(1, new Date(d.getTime()));
			stat.setInt(2, e.getId());
			stat.executeUpdate();
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to complete enroll and write changes to the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Сброс (обнуление) проходных баллов всех факультетов.
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
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public void resetPassRates(int trCode) throws DAOException {
		LOGGER.debug("DAO : EnrollmentDAOdb.resetPassRates(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.FacultyQuery.RESET_PASS_RATES);
			stat.executeUpdate();
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to reset pass rates and write changes to the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Поменять статусы абитуриентов в контексте завершения набора.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param isAdmitted
	 *            статус абитуриентов; true - завершить статусы зачисленных
	 *            абитуриентов; false - завершить статусы незачисленных
	 *            абитуриентов.
	 * @param e
	 *            набор, в рамках которого меняются статусы абитуриентов
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public void completeStatuses(boolean isAdmitted, Enroll e, int trCode) throws DAOException {
		LOGGER.debug("DAO : EnrollmentDAOdb.resetPassRates(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.RegisterQuery.COMPLETE_STATUSES);
			if (isAdmitted) {
				changeStatuses(stat, DBHelper.ADMITTED_STATUS, DBHelper.TEMP_ADMITTED_STATUS, e.getId());
			} else {
				changeStatuses(stat, DBHelper.NOT_ADMITTED_STATUS, DBHelper.TEMP_NOT_ADMITTED_STATUS, e.getId());
			}
			stat.executeUpdate();
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to reset pass rates and write changes to the database", ex);
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
		try {
			Connection conn = TR_MAP.get(trCode);
			conn.rollback();
			TR_MAP.remove(trCode);
			LOGGER.debug("DAO : transaction rollback; transaction code = {}", trCode);
		} catch (SQLException ex) {
			throw new TransactionException("DAO : Unable to rollback transaction", ex);
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
	 * набора.
	 * 
	 * @param stat
	 *            подготовленный запрос
	 * @param e
	 *            объект набора
	 * @throws SQLException
	 *             если при установке какого-либо параметра произошла ошибка
	 */
	private void setEnrollParameters(PreparedStatement stat, Enroll e) throws SQLException {
		stat.setDate(1, new Date(e.getBeginDate().getTime()));
		stat.setDate(2, new Date(e.getEndDate().getTime()));
		stat.setBoolean(3, e.isStatus());
	}

	/**
	 * Установить параметры подготовленного запроса
	 * <code>PreparedStatement</code>, предназначенного для изменения статуса
	 * абитуриента в рамках набора, id которого указан параметром
	 * <code>id</code>
	 * 
	 * @param stat
	 *            подготовленный запрос
	 * @param newStatus
	 *            новый статус
	 * @param oldStatus
	 *            старый статус
	 * @param id
	 *            id набора, в рамках которого меняется статус абитуриента
	 * @throws SQLException
	 *             если при установке какого-либо параметра произошла ошибка
	 */
	private void changeStatuses(PreparedStatement stat, String newStatus, String oldStatus, int id)
			throws SQLException {
		stat.setString(1, newStatus);
		stat.setString(2, oldStatus);
		stat.setInt(3, id);
	}

}
