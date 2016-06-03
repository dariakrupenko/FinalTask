package main.by.epam.admissionweb.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.by.epam.admissionweb.dao.exception.DAOException;
import main.by.epam.admissionweb.dao.pool.ConnectionPool;
import main.by.epam.admissionweb.dao.pool.exception.ConnectionPoolException;
import main.by.epam.admissionweb.dao.pool.impl.ConnectionPoolImpl;
import main.by.epam.admissionweb.entity.Applicant;
import main.by.epam.admissionweb.entity.Discipline;
import main.by.epam.admissionweb.entity.Enroll;
import main.by.epam.admissionweb.entity.Faculty;

/**
 * Класс <code>DBHelper</code> предназначен для выполнения действий, общих для
 * всех DAO-объектов.
 * <p>
 * Получение объекта класса <code>DBHelper</code> осуществляется путем вызова
 * статического метода <code>getInstance()</code> данного класса.
 * <p>
 * Назначение класса <code>DBHelper</code>:
 * <ul>
 * <li>конструирование объектов модели данных по результирующему набору</li>
 * <li>возвращение соединения с базой данных в пул соединений</li>
 * </ul>
 * 
 * @author Daria Krupenko
 *
 */
class DBHelper {

	/**
	 * Объект класса <code>DBHelper</code>, создается один раз при загрузке
	 * класса в память
	 */
	private static final DBHelper INSTANCE = new DBHelper();

	/**
	 * Начальный индекс по умолчанию (используется при выборке элементов в
	 * ограниченный список)
	 */
	static final int BEGIN_INDEX_DEFAULT = 0;

	/**
	 * Количество элементов в выборке по умолчанию
	 */
	static final int ELEMENTS_MAX_VALUE = Integer.MAX_VALUE;

	/**
	 * Статус зачисленных абитуриентов (набор закрыт)
	 */
	static final String ADMITTED_STATUS = "ADMITTED";

	/**
	 * Статус незачисленных абитуриентов (набор закрыт)
	 */
	static final String NOT_ADMITTED_STATUS = "NOT_ADMITTED";

	/**
	 * Статус временно зачисленных абитуриентов (набор открыт)
	 */
	static final String TEMP_ADMITTED_STATUS = "TEMP_ADMITTED";

	/**
	 * Статус временно незачисленных абитуриентов (набор открыт)
	 */
	static final String TEMP_NOT_ADMITTED_STATUS = "TEMP_NOT_ADMITTED";

	/**
	 * Код транзакции по умолчанию (используется для указания того, что операция
	 * не является частью бизнес-транзакции)
	 */
	static final int NO_TRANSACTION = -1;

	private DBHelper() {
	}

	/**
	 * Получение объекта класса <code>DBHelper</code>
	 * 
	 * @return объект класса <code>DBHelper</code>
	 */
	public static DBHelper getInstance() {
		return INSTANCE;
	}

	/**
	 * Конструирование объекта факультета по результирующему набору
	 * 
	 * @param conn
	 *            соединение с базой данных
	 * @param rs
	 *            результирующий набор
	 * @param e
	 *            набор, в рамках которого подсчитывается количество
	 *            записавшихся на факультет абитуриентов
	 * @return объект факультета
	 * @throws SQLException
	 *             если при чтении данных возникла ошибка
	 */
	Faculty constructFaculty(Connection conn, ResultSet rs, Enroll e) throws SQLException {
		Faculty f = new Faculty();
		f.setId(rs.getInt(DBHelper.ColumnLabel.FACULTY_ID));
		f.setTitle(rs.getString(DBHelper.ColumnLabel.FACULTY_TITLE));
		f.setDescription(rs.getString(DBHelper.ColumnLabel.FACULTY_DESCRIPTION));
		f.setPhone(rs.getString(DBHelper.ColumnLabel.FACULTY_PHONE));
		f.setAddress(rs.getString(DBHelper.ColumnLabel.FACULTY_ADDRESS));
		f.setDean(rs.getString(DBHelper.ColumnLabel.FACULTY_DEAN));
		f.setLogoname(rs.getString(DBHelper.ColumnLabel.FACULTY_LOGONAME));
		f.setPlan(rs.getInt(DBHelper.ColumnLabel.FACULTY_PLAN));
		f.setPassRate(rs.getInt(DBHelper.ColumnLabel.FACULTY_PASS_RATE));
		List<Discipline> dList = readDisciplines(conn, f.getId());
		f.setDisciplines(dList);
		int aCount = readApplicantsCount(conn, f.getId(), e);
		f.setApplicantsCount(aCount);
		return f;
	}

	/**
	 * Извлечение списка дисциплин для факультета, id которого указан в
	 * параметре <code>id</code>
	 * 
	 * @param conn
	 *            соединений с базой данных
	 * @param id
	 *            id факультета
	 * @return список дисциплин, связанных с факультетом
	 * @throws SQLException
	 *             если при чтении данных возникла ошибка
	 */
	private List<Discipline> readDisciplines(Connection conn, int id) throws SQLException {
		List<Discipline> dList = new ArrayList<Discipline>();
		PreparedStatement stat = conn.prepareStatement(DBHelper.FacultyQuery.GET_DISCIPLINES_LIST_BY_FACULTY);
		stat.setInt(1, id);
		ResultSet rs = stat.executeQuery();
		while (rs.next()) {
			Discipline d = new Discipline();
			d.setId(rs.getInt(DBHelper.ColumnLabel.DISCIPLINE_ID));
			d.setTitle(rs.getString(DBHelper.ColumnLabel.DISCIPLINE_TITLE));
			dList.add(d);
		}
		return dList;
	}

	/**
	 * Извлечение количества записавшихся абитуриентов на факультет, id которого
	 * указан в параметре <code>id</code>
	 * 
	 * @param conn
	 *            соединение с базой данных
	 * @param id
	 *            id факультета
	 * @param e
	 *            набор, в рамках которого производится подсчет
	 * @return количество записавшихся на факультет абитуриентов
	 * @throws SQLException
	 *             если при чтении данных возникла ошибка
	 */
	private int readApplicantsCount(Connection conn, int id, Enroll e) throws SQLException {
		int aCount = 0;
		if (e == null) {
			return aCount;
		}
		PreparedStatement stat = conn.prepareStatement(RegisterQuery.GET_RECORDS_COUNT_BY_FACULTY);
		stat.setInt(1, id);
		stat.setInt(2, e.getId());
		ResultSet rs = stat.executeQuery();
		while (rs.next()) {
			aCount = rs.getInt(1);
		}
		return aCount;
	}

	/**
	 * Конструирование объекта дисициплины по результирующему набору
	 * 
	 * @param conn
	 *            соединение с базой данных
	 * @param rs
	 *            результирующий набор
	 * @return объект дисциплины
	 * @throws SQLException
	 *             если при чтении данных возникла ошибка
	 */
	Discipline constructDiscipline(Connection conn, ResultSet rs) throws SQLException {
		Discipline d = new Discipline();
		d.setId(rs.getInt(DBHelper.ColumnLabel.DISCIPLINE_ID));
		d.setTitle(rs.getString(DBHelper.ColumnLabel.DISCIPLINE_TITLE));
		List<Faculty> fList = readFacultiesByDiscipline(conn, d.getId());
		d.setFaculties(fList);
		return d;
	}

	/**
	 * Извлечение списка факультеты для дисциплины, id которой указан в
	 * параметре <code>id</code>
	 * 
	 * @param conn
	 *            соединений с базой данных
	 * @param id
	 *            id дисциплины
	 * @return список факультетов, связанных с дисциплиной
	 * @throws SQLException
	 *             если при чтении данных возникла ошибка
	 */
	private List<Faculty> readFacultiesByDiscipline(Connection conn, int id) throws SQLException {
		List<Faculty> fList = new ArrayList<Faculty>();
		PreparedStatement stat = conn.prepareStatement(DBHelper.DisciplineQuery.GET_FACULTIES_LIST_BY_DISCIPLINE);
		stat.setInt(1, id);
		ResultSet rs = stat.executeQuery();
		while (rs.next()) {
			Faculty f = constructFaculty(conn, rs, null);
			fList.add(f);
		}
		return fList;
	}

	/**
	 * Конструирование объекта набора по результирующему набору
	 * 
	 * @param conn
	 *            соединение с базой данных
	 * @param rs
	 *            результирующий набор
	 * @return объект набора
	 * @throws SQLException
	 *             если при чтении данных возникла ошибка
	 */
	Enroll constructEnroll(ResultSet rs) throws SQLException {
		Enroll e = new Enroll();
		e.setId(rs.getInt(DBHelper.ColumnLabel.ENROLL_ID));
		e.setBeginDate(rs.getDate(DBHelper.ColumnLabel.ENROLL_BEGIN_DATE));
		e.setEndDate(rs.getDate(DBHelper.ColumnLabel.ENROLL_END_DATE));
		e.setStatus(rs.getBoolean(DBHelper.ColumnLabel.ENROLL_ACTIVE));
		return e;
	}

	/**
	 * Конструирование объекта абитуриента по результирующему набору
	 * 
	 * @param conn
	 *            соединение с базой данных
	 * @param rs
	 *            результирующий набор
	 * @return объект абитуриента
	 * @throws SQLException
	 *             если при чтении данных возникла ошибка
	 */
	Applicant constructApplicant(ResultSet rs) throws SQLException {
		Applicant a = new Applicant();
		a.setId(rs.getInt(DBHelper.ColumnLabel.APPLICANT_ID));
		a.setLogin(rs.getString(DBHelper.ColumnLabel.APPLICANT_LOGIN));
		a.setPassword(rs.getString(DBHelper.ColumnLabel.APPLICANT_PASSWORD));
		a.setName(rs.getString(DBHelper.ColumnLabel.APPLICANT_NAME));
		a.setEmail(rs.getString(DBHelper.ColumnLabel.APPLICANT_EMAIL));
		a.setPhone(rs.getString(DBHelper.ColumnLabel.APPLICANT_PHONE));
		a.setAddress(rs.getString(DBHelper.ColumnLabel.APPLICANT_ADDRESS));
		a.setBirthdate(rs.getDate(DBHelper.ColumnLabel.APPLICANT_BDATE));
		a.setSchool(rs.getString(DBHelper.ColumnLabel.APPLICANT_SCHOOL));
		a.setGradYear(rs.getInt(DBHelper.ColumnLabel.APPLICANT_YEAR));
		return a;
	}

	/**
	 * Возвращение соединения с базой данных обратно в пул соединений
	 * 
	 * @param pool
	 *            пул соединений с базой данных
	 * @param conn
	 *            соединений
	 * @throws DAOException
	 *             при возвращении соединения в пул произошла ошибка
	 */
	void returnConnection(ConnectionPool pool, Connection conn) throws DAOException {
		try {
			if (pool != null && !conn.isClosed()) {
				ConnectionPoolImpl.getInstance().returnConnection(conn);
			}
		} catch (ConnectionPoolException | SQLException ex) {
			throw new DAOException("DAO : Unable to return connection", ex);
		}
	}

	/**
	 * Статический вложенный класс <code>EnrollmentQuery</code> содержит
	 * строковые константы, представляющие собой SQL-запросы к таблице,
	 * содержащей информацию о наборах учебного заведения
	 * 
	 * @author Daria Krupenko
	 * @see Enroll
	 *
	 */
	static class EnrollmentQuery {
		static final String GET_ENROLLS_LIST = "SELECT id, begin_date, end_date, active"
				+ " FROM enrolls ORDER BY begin_date LIMIT ?,?";
		static final String GET_ENROLLS_NUMBER = "SELECT COUNT(id) FROM enrolls";
		static final String CREATE_ENROLL = "INSERT INTO enrolls(begin_date, end_date, active)" + " VALUES(?,?,?)";
		static final String GET_ENROLL_ID = "SELECT id FROM enrolls WHERE begin_date = ?";
		static final String DELETE_ENROLL = "DELETE FROM enrolls WHERE id = ?";
		static final String GET_LAST_ENROLL = "SELECT id,begin_date,end_date,active FROM"
				+ " enrolls WHERE end_date = (SELECT MAX(end_date) FROM enrolls)";
		static final String COMPLETE_ENROLL = "UPDATE enrolls SET end_date = ?, active = 0 WHERE id = ?";
		static final String GET_ENROLLS_BY_STATUS = "SELECT id, begin_date, end_date, active FROM enrolls"
				+ " WHERE active = ?";
	}

	/**
	 * Статический вложенный класс <code>ApplicantQuery</code> содержит
	 * строковые константы, представляющие собой SQL-запросы к таблице,
	 * содержащей информацию об абитуриентах учебного заведения
	 * 
	 * @author Daria Krupenko
	 * @see Applicant
	 *
	 */
	static class ApplicantQuery {
		static final String CREATE_APPLICANT = "INSERT INTO applicants(login,password,name,email,"
				+ "phone,address,birthdate,school,grad_year) VALUES(?,?,?,?,?,?,?,?,?)";
		static final String GET_APPLICANT_ID = "SELECT id FROM applicants WHERE login = ?";
		static final String GET_APPLICANT_BY_LOGIN_PASSWORD = "SELECT id,login,password,name,email,"
				+ "phone,address,birthdate,school,grad_year FROM applicants WHERE login = ? AND password = ?";
		static final String GET_APPLICANT = "SELECT id, login, password, name, email,"
				+ " phone, address, birthdate, school, grad_year FROM applicants WHERE id = ?";
		static final String UPDATE_APPLICANT = "UPDATE applicants SET login = ?, password = ?, name = ?,"
				+ " email = ?, phone = ?, address = ?, birthdate = ?, school = ?, grad_year = ? WHERE id = ?";
		static final String GET_APPLICANTS_LIST = "SELECT id, login, password, name, email, phone, address, birthdate, school, grad_year"
				+ " FROM applicants ORDER BY name LIMIT ?,?";
		static final String GET_APPLICANTS_NUMBER = "SELECT COUNT(id) FROM applicants";
		static final String GET_APPLICANT_BY_LOGIN = "SELECT id,login,password,name,email,"
				+ "phone,address,birthdate,school,grad_year FROM applicants WHERE login = ?";

	}

	/**
	 * Статический вложенный класс <code>DisciplineQuery</code> содержит
	 * строковые константы, представляющие собой SQL-запросы к таблице,
	 * содержащей информацию о дисциплинах учебного заведения
	 * 
	 * @author Daria Krupenko
	 * @see Discipline
	 *
	 */
	static class DisciplineQuery {
		static final String GET_DISCIPLINES_LIST = "SELECT id, title FROM disciplines ORDER BY title LIMIT ?,?";
		static final String GET_FACULTIES_LIST_BY_DISCIPLINE = "SELECT faculties.id, faculties.title, faculties.description, faculties.phone,"
				+ " faculties.address, faculties.dean, faculties.logoname, faculties.plan,"
				+ " faculties.pass_rate FROM faculties INNER JOIN faculties_disciplines"
				+ " ON faculties.id = faculties_disciplines.faculties_id"
				+ " WHERE faculties_disciplines.disciplines_id = ? ORDER BY faculties.title";
		static final String GET_DISCIPLINES_NUMBER = "SELECT COUNT(id) FROM disciplines";
		static final String GET_DISCIPLINE = "SELECT id, title FROM disciplines WHERE id = ?";
		static final String UPDATE_DISCIPLINE = "UPDATE disciplines SET title = ? WHERE id = ?";
		static final String DELETE_DISCIPLINE = "DELETE FROM disciplines WHERE id = ?";
		static final String CREATE_DISCIPLINE = "INSERT INTO disciplines(title) VALUES(?)";
		static final String GET_DISCIPLINE_ID = "SELECT id FROM disciplines WHERE title = ?";
		static final String GET_DISCIPLINE_BY_TITLE = "SELECT id, title FROM disciplines WHERE title = ?";
	}

	/**
	 * Статический вложенный класс <code>RegisterQuery</code> содержит строковые
	 * константы, представляющие собой SQL-запросы к таблице, содержащей
	 * информацию о ведомости учебного заведения
	 * 
	 * @author Daria Krupenko
	 * @see RegisterRecord
	 *
	 */
	static class RegisterQuery {
		static final String COMPLETE_STATUSES = "UPDATE register SET status = ? WHERE status = ? AND enrolls_id = ?";
		static final String CREATE_RECORD = "INSERT INTO register(applicants_id, faculties_id,"
				+ "certificate_score,total_score,status,enrolls_id) VALUES(?,?,?,?,?,?)";
		static final String ADD_DISCIPLINE_BY_RECORD = "INSERT INTO register_disciplines"
				+ "(register_applicants_id,disciplines_id,score) VALUES(?,?,?)";
		static final String GET_CHANGED_PASS_RATE = "SELECT MIN(total_score) FROM register"
				+ " WHERE status='TEMP_ADMITTED' and faculties_id = ?";
		static final String UPDATE_ADMITTED_STATUSES = "UPDATE register SET status = 'TEMP_ADMITTED'"
				+ " WHERE applicants_id = ?";
		static final String UPDATE_NOT_ADMITTED_STATUSES = "UPDATE register SET status = 'TEMP_NOT_ADMITTED'"
				+ " WHERE applicants_id = ?";
		static final String GET_ADMITTED_RECORDS = "SELECT applicants_id FROM register"
				+ " WHERE faculties_id = ? AND enrolls_id = ? ORDER BY total_score DESC, certificate_score DESC LIMIT 0,?";
		static final String GET_NOT_ADMITTED_RECORDS = "SELECT applicants_id FROM register"
				+ " WHERE faculties_id = ? AND enrolls_id = ? ORDER BY total_score DESC, certificate_score DESC LIMIT ?,?";
		static final String GET_RECORDS_COUNT_BY_FACULTY = "SELECT COUNT(applicants_id)"
				+ " FROM register WHERE faculties_id = ? AND enrolls_id = ?";
		static final String GET_RECORD_BY_APPLICANT = "SELECT certificate_score, total_score, status FROM register"
				+ " WHERE applicants_id = ?";
		static final String GET_FACULTY_BY_RECORD = "SELECT faculties.id, faculties.title, faculties.description,"
				+ " faculties.logoname, faculties.phone, faculties.address, faculties.dean, faculties.plan, faculties.pass_rate"
				+ " FROM faculties INNER JOIN register ON faculties.id = register.faculties_id WHERE register.applicants_id = ?";
		static final String GET_ENROLL_BY_RECORD = "SELECT enrolls.id, enrolls.begin_date, enrolls.end_date,"
				+ " enrolls.active FROM enrolls INNER JOIN register ON enrolls.id = register.enrolls_id WHERE register.applicants_id = ?";
		static final String DELETE_RECORD = "DELETE FROM register WHERE applicants_id = ?";
		static final String GET_RECORDS_NUMBER = "SELECT COUNT(register.applicants_id) FROM register WHERE enrolls_id = ?";
		static final String GET_REGISTER = "SELECT applicants.id, applicants.name, faculties.id, register.certificate_score, register.total_score,"
				+ " register.status FROM faculties INNER JOIN register ON faculties.id = register.faculties_id"
				+ " INNER JOIN applicants ON register.applicants_id = applicants.id WHERE register.enrolls_id = ? ORDER BY applicants.name LIMIT ?,?";
		static final String GET_RECORDS_NUMBER_BY_STATUS_FACULTY_ENROLL = "SELECT COUNT(applicants_id)"
				+ " FROM register WHERE (status=? OR status = ?) AND faculties_id = ? AND enrolls_id = ?";
		static final String GET_RECORDS_BY_STATUS_FACULTY_ENROLL = "SELECT applicants_id, certificate_score,"
				+ " total_score, status FROM register WHERE (status = ? OR status = ?) AND faculties_id = ? AND enrolls_id = ? ORDER BY total_score DESC LIMIT ?,?";
		static final String GET_APPLICANT_BY_RECORD = "SELECT applicants.name FROM applicants WHERE id = ?";
		static final String GET_SCORES_BY_RECORD = "SELECT disciplines.id, disciplines.title, register_disciplines.score"
				+ " FROM disciplines INNER JOIN register_disciplines"
				+ " ON disciplines.id = register_disciplines.disciplines_id"
				+ " WHERE register_disciplines.register_applicants_id = ? ORDER BY disciplines.title";
	}

	/**
	 * Статический вложенный класс <code>FacultyQuery</code> содержит строковые
	 * константы, представляющие собой SQL-запросы к таблице, содержащей
	 * информацию о факультетах учебного заведения
	 * 
	 * @author Daria Krupenko
	 * @see Faculty
	 *
	 */
	static class FacultyQuery {
		static final String GET_FACULTIES_LIST = "SELECT id, title, description, phone,"
				+ " address, dean, logoname, plan, pass_rate FROM faculties ORDER BY title LIMIT ?,?";
		static final String GET_DISCIPLINES_LIST_BY_FACULTY = "SELECT disciplines.id, disciplines.title"
				+ " FROM disciplines INNER JOIN faculties_disciplines"
				+ " ON disciplines.id = faculties_disciplines.disciplines_id"
				+ " WHERE faculties_disciplines.faculties_id = ? ORDER BY disciplines.title";
		static final String GET_FACULTIES_NUMBER = "SELECT COUNT(id) FROM faculties";
		static final String CREATE_FACULTY = "INSERT INTO faculties(title, description, logoname,"
				+ " phone, address, dean, plan) VALUES(?,?,?,?,?,?,?)";
		static final String ADD_DISCIPLINE_TO_FACULTY = "INSERT INTO faculties_disciplines("
				+ "faculties_id, disciplines_id) VALUES(?,?)";
		static final String GET_FACULTY_ID = "SELECT id FROM faculties WHERE title = ?";
		static final String GET_FACULTY = "SELECT id, title, description, logoname, phone, address,"
				+ " dean, plan, pass_rate FROM faculties WHERE id = ?";
		static final String REMOVE_DISCIPLINES_BY_FACULTY = "DELETE FROM faculties_disciplines WHERE faculties_id = ?";
		static final String UPDATE_FACULTY = "UPDATE faculties SET title=?, description=?, logoname=?,"
				+ " phone=?, address=?, dean=?, plan=? WHERE id = ?";
		static final String DELETE_FACULTY = "DELETE FROM faculties WHERE id = ?";
		static final String SET_PASS_RATE = "UPDATE faculties SET pass_rate = ? WHERE id = ?";
		static final String RESET_PASS_RATES = "UPDATE faculties SET pass_rate = 0";
		static final String GET_FACULTY_BY_TITLE = "SELECT id, title, description, logoname, phone, address,"
				+ " dean, plan, pass_rate FROM faculties WHERE title = ?";
	}

	/**
	 * Статический вложенный класс <code>ColumnLabel</code> содержит строковые
	 * константы, представляющий собой атрибуты, или поля, таблиц в базе данных
	 * приложения.
	 * 
	 * @author Daria Krupenko
	 *
	 */
	static class ColumnLabel {
		static final String DISCIPLINE_ID = "id";
		static final String DISCIPLINE_TITLE = "title";

		static final String FACULTY_ID = "id";
		static final String FACULTY_TITLE = "title";
		static final String FACULTY_DESCRIPTION = "description";
		static final String FACULTY_PHONE = "phone";
		static final String FACULTY_ADDRESS = "address";
		static final String FACULTY_DEAN = "dean";
		static final String FACULTY_LOGONAME = "logoname";
		static final String FACULTY_PLAN = "plan";
		static final String FACULTY_PASS_RATE = "pass_rate";

		static final String ENROLL_ID = "id";
		static final String ENROLL_BEGIN_DATE = "begin_date";
		static final String ENROLL_END_DATE = "end_date";
		static final String ENROLL_ACTIVE = "active";

		static final String APPLICANT_ID = "id";
		static final String APPLICANT_LOGIN = "login";
		static final String APPLICANT_PASSWORD = "password";
		static final String APPLICANT_NAME = "name";
		static final String APPLICANT_EMAIL = "email";
		static final String APPLICANT_PHONE = "phone";
		static final String APPLICANT_ADDRESS = "address";
		static final String APPLICANT_BDATE = "birthdate";
		static final String APPLICANT_SCHOOL = "school";
		static final String APPLICANT_YEAR = "grad_year";

		static final String REGISTER_DISCIPLINE_SCORE = "score";

		static final String REGISTER_CERT_SCORE = "certificate_score";
		static final String REGISTER_TOTAL_SCORE = "total_score";
		static final String REGISTER_STATUS = "status";
		static final String REGISTER_APPLICANTS_ID = "applicants_id";
	}

}
