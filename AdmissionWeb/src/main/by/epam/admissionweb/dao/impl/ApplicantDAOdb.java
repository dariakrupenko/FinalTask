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

import main.by.epam.admissionweb.dao.ApplicantDAO;
import main.by.epam.admissionweb.dao.exception.DAOException;
import main.by.epam.admissionweb.dao.exception.TransactionException;
import main.by.epam.admissionweb.dao.pool.ConnectionPool;
import main.by.epam.admissionweb.dao.pool.exception.ConnectionPoolException;
import main.by.epam.admissionweb.dao.pool.impl.ConnectionPoolImpl;
import main.by.epam.admissionweb.entity.Applicant;
import main.by.epam.admissionweb.generator.KeyGenerator;
import main.by.epam.admissionweb.generator.KeyGeneratorFactory;

/**
 * Класс <code>ApplicantDAOdb</code> реализует интерфейс {@link ApplicantDAO} и
 * является DAO-объектом, способным производить операции чтения/записи
 * информации об абитуриентах учебного заведения в базу данных MySQL.
 * <p>
 * Соединение с базой данных извлекается из пула соединений
 * {@link ConnectionPool}.
 * <p>
 * Реализуя интерфейс {@link ApplicantDAO}, класс <code>ApplicantDAOdb</code>
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
 * @see ApplicantDAO
 * @see Applicant
 * @see DBHelper
 * @see Connection
 *
 */
public class ApplicantDAOdb implements ApplicantDAO {

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
	 * Сохранение объекта абитуриента в источнике данных.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param a
	 *            объект абитуриента
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return id абитуриента, сохраненного в источнике данных
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public int createApplicant(Applicant a, int trCode) throws DAOException {
		LOGGER.debug("DAO : ApplicantDAOdb.createApplicant(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		int id = 0;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.ApplicantQuery.CREATE_APPLICANT);
			setApplicantParameters(stat, a);
			stat.executeUpdate();
			stat = conn.prepareStatement(DBHelper.ApplicantQuery.GET_APPLICANT_ID);
			stat.setString(1, a.getLogin());
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				id = rs.getInt(1);
			}
			return id;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to create applicant and write it to the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Получение абитуриента с указанным логином и паролем.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param login
	 *            логин абитуриента
	 * @param password
	 *            пароль абитуриента
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return объект абитуриента с указанным логином и паролем
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public Applicant getApplicantByLoginPassword(String login, String password, int trCode) throws DAOException {
		LOGGER.debug("DAO : ApplicantDAOdb.getApplicantByLoginAndPassword(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		Applicant a = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.ApplicantQuery.GET_APPLICANT_BY_LOGIN_PASSWORD);
			stat.setString(1, login);
			stat.setString(2, password);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				a = DBHelper.getInstance().constructApplicant(rs);
			}
			LOGGER.debug("DAO : ApplicantDAOdb.getApplicantByLoginAndPassword (a = {})", a);
			return a;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read applicant by login and password from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Обновление информации об абитуриенте.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param a
	 *            объект абитуриента с обновленными данными
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public void updateApplicant(Applicant a, int trCode) throws DAOException {
		LOGGER.debug("DAO : ApplicantDAOdb.updateApplicant(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.ApplicantQuery.UPDATE_APPLICANT);
			setApplicantParameters(stat, a);
			stat.setInt(10, a.getId());
			stat.executeUpdate();
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to update applicant and write changes to the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}

	}

	/**
	 * Получение объекта абитуриента по его id.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param id
	 *            id абитуриента
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return объект абитуриента с указанным id
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public Applicant getApplicant(int id, int trCode) throws DAOException {
		LOGGER.debug("DAO : ApplicantDAOdb.getApplicant(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		Applicant a = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.ApplicantQuery.GET_APPLICANT);
			stat.setInt(1, id);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				a = DBHelper.getInstance().constructApplicant(rs);
			}
			LOGGER.debug("DAO : ApplicantDAOdb.getApplicant (a = {})", a);
			return a;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read applicant by id from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Получение количества абитуриентов, зарегистрированных в системе.
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
	 * @return количество абитуриентов, зарегистрированных в системе
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public int getApplicantsNumber(int trCode) throws DAOException {
		LOGGER.debug("DAO : ApplicantsDAOdb.getApplicantsNumber(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		int number = 0;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.ApplicantQuery.GET_APPLICANTS_NUMBER);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				number = rs.getInt(1);
			}
			LOGGER.debug("DAO : ApplicantDAOdb.getApplicantsNumber (number = {})", number);
			return number;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read applicants number from the database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Получение списка абитуриентов, зарегистрированных в системе.
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
	 *            индекс абитуриента, с которого начинается построение списка
	 * @param elementsCount
	 *            количество требуемых абитуриентов, начиная с
	 *            <code>beginIndex</code>
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return список абитуриентов, начиная с абитуриента с индексом
	 *         <code>beginIndex</code>; количество элементов списка равно
	 *         <code>elementsCount</code>
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public List<Applicant> getApplicantsList(int beginIndex, int elementsCount, int trCode) throws DAOException {
		LOGGER.debug("DAO : ApplicantDAOdb.getApplicantsList(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		List<Applicant> list = new ArrayList<Applicant>();
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.ApplicantQuery.GET_APPLICANTS_LIST);
			if (beginIndex == DBHelper.BEGIN_INDEX_DEFAULT && elementsCount == DBHelper.ELEMENTS_MAX_VALUE) {
				elementsCount = getApplicantsNumber(trCode);
			}
			stat.setInt(1, beginIndex);
			stat.setInt(2, elementsCount);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				Applicant a = DBHelper.getInstance().constructApplicant(rs);
				list.add(a);
			}
			LOGGER.debug("DAO : ApplicantDAOdb.getApplicantsList (list = {})", list);
			return list;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read applicants list from database", ex);
		} finally {
			if (trCode == DBHelper.NO_TRANSACTION) {
				DBHelper.getInstance().returnConnection(pool, conn);
			}
		}
	}

	/**
	 * Получение объекта абитуриента по указанному логину.
	 * <p>
	 * При указании кода транзакции в качестве параметра <code>trCode</code>
	 * данное действие будет выполнено как часть транзакции с указанным кодом. В
	 * данном случае, соединение с базой данных извлекается из карты транзакций,
	 * в противном случае - из пула соединений с базой данных. В конце работы
	 * соединение возвращается в пул соединений, если операция не является
	 * частью транзакции.
	 * 
	 * @param login
	 *            логин абитуриента
	 * @param trCode
	 *            код транзакции; если равен значению по умолчанию (-1), данное
	 *            действие не рассматривается как часть какой-либо транзакции
	 * @return объект абитуриента с указанным логином
	 * @throws DAOException
	 *             если произошла ошибка при получении или возвращении
	 *             соединения в пул; если произошла ошибка чтения/записи данных
	 */
	@Override
	public Applicant getApplicantByLogin(String login, int trCode) throws DAOException {
		LOGGER.debug("DAO : ApplicantDAOdb.getApplicantByLogin(transaction code = {})", trCode);
		ConnectionPool pool = null;
		Connection conn = null;
		Applicant a = null;
		try {
			pool = ConnectionPoolImpl.getInstance();
			conn = TR_MAP.get(trCode);
			if (conn == null) {
				conn = pool.takeConnection();
			}
			PreparedStatement stat = conn.prepareStatement(DBHelper.ApplicantQuery.GET_APPLICANT_BY_LOGIN);
			stat.setString(1, login);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				a = DBHelper.getInstance().constructApplicant(rs);
			}
			LOGGER.debug("DAO : ApplicantDAOdb.getApplicant (a = {})", a);
			return a;
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to read applicant by login from the database", ex);
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
	 * абитуриента.
	 * 
	 * @param stat
	 *            подготовленный запрос
	 * @param a
	 *            объект абитуриента
	 * @throws SQLException
	 *             если при установке какого-либо параметра произошла ошибка
	 */
	private void setApplicantParameters(PreparedStatement stat, Applicant a) throws SQLException {
		stat.setString(1, a.getLogin());
		stat.setString(2, a.getPassword());
		stat.setString(3, a.getName());
		stat.setString(4, a.getEmail());
		stat.setString(5, a.getPhone());
		stat.setString(6, a.getAddress());
		stat.setDate(7, new Date(a.getBirthdate().getTime()));
		stat.setString(8, a.getSchool());
		stat.setInt(9, a.getGradYear());
	}
}
