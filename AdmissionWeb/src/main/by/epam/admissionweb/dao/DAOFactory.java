package main.by.epam.admissionweb.dao;

import main.by.epam.admissionweb.dao.impl.ApplicantDAOdb;
import main.by.epam.admissionweb.dao.impl.DisciplineDAOdb;
import main.by.epam.admissionweb.dao.impl.EnrollmentDAOdb;
import main.by.epam.admissionweb.dao.impl.FacultyDAOdb;
import main.by.epam.admissionweb.dao.impl.RegisterDAOdb;

/**
 * Класс <code>DAOFactory</code> представляет собой фабрику для получения
 * объектов DAO - объектов доступа к данным (Data Access Object).
 * <p>
 * Получить объект класса <code>DAOFactory</code> можно при помощи вызова
 * статического метода <code>getInstance()</code> данного класса.
 * <p>
 * DAO-объекты предназначены для предоставляения единого интерфейса доступа к
 * различным источникам данных, например, к базе данных. Как правило,
 * DAO-объекты используются классами логики приложения для получения данных из
 * источника данных. Затем эти данные обрабатываются логикой приложения.
 * <p>
 * Класс <code>DAOFactory</code> предоставляет методы для получения
 * DAO-объектов, способных извлечь из источника данных необходимые для работы
 * приложения данные, а именно сведения о дисциплинах, наборах, факультетах,
 * абитуриентах учебного заведения.
 * 
 * @author Daria Krupenko
 *
 */
public class DAOFactory {

	/**
	 * Объект фабрики. Создается один раз при загрузке класса в память. Далее
	 * объект фабрики можно получить, вызвав статический метод
	 * <code>getInstance()</code> данного класса.
	 */
	private static final DAOFactory INSTANCE = new DAOFactory();

	/**
	 * DAO-объект для извлечения данных о дисциплинах
	 */
	private static final DisciplineDAO DISCIPLINE_DAO = new DisciplineDAOdb();

	/**
	 * DAO-объект для извлечения данных о наборах
	 */
	private static final EnrollmentDAO ENROLLMENT_DAO = new EnrollmentDAOdb();

	/**
	 * DAO-объект для извлечения данных о факультетах
	 */
	private static final FacultyDAO FACULTY_DAO = new FacultyDAOdb();

	/**
	 * DAO-объект для извлечения данных об абитуриентах
	 */
	private static final ApplicantDAO APPLICANT_DAO = new ApplicantDAOdb();

	/**
	 * DAO-объект для извлечения ведомости
	 */
	private static final RegisterDAO REGISTER_DAO = new RegisterDAOdb();

	/**
	 * Конструктор для создания объекта фабрики. Может быть вызван только из
	 * данного класса.
	 */
	private DAOFactory() {
	}

	/**
	 * Статический метод <code>getInstance()</code> предназначен для получения
	 * объекта фабрики с целью получения DAO-объектов.
	 * 
	 * @return объект фабрики для получения DAO-объектов
	 */
	public static DAOFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * Получение DAO-объекта, способного извлечь данные о дисциплинах из
	 * источника данных
	 * 
	 * @return DAO-объект для извлечения данных о дисциплинах учебного заведения
	 * @see DisciplineDAO
	 * @see DisciplineDAOdb
	 */
	public DisciplineDAO getDisciplineDAO() {
		return DISCIPLINE_DAO;
	}

	/**
	 * Получение DAO-объекта, способного извлечь данные о наборах из источника
	 * данных
	 * 
	 * @return DAO-объект для извлечения данных о наборах учебного заведения
	 * @see EnrollmentDAO
	 * @see EnrollmentDAOdb
	 */
	public EnrollmentDAO getEnrollmentDAO() {
		return ENROLLMENT_DAO;
	}

	/**
	 * Получение DAO-объекта, способного извлечь данные о факультетах из
	 * источника данных
	 * 
	 * @return DAO-объект для извлечения данных о факультетах учебного заведения
	 * @see FacultyDAO
	 * @see FacultyDAOdb
	 */
	public FacultyDAO getFacultyDAO() {
		return FACULTY_DAO;
	}

	/**
	 * Получение DAO-объекта, способного извлечь данные об абитуриентах из
	 * источника данных
	 * 
	 * @return DAO-объект для извлечения данных об абитуриентах учебного
	 *         заведения
	 * @see ApplicantDAO
	 * @see ApplicantDAOdb
	 */
	public ApplicantDAO getApplicantDAO() {
		return APPLICANT_DAO;
	}

	/**
	 * Получение DAO-объекта, способного извлечь ведомость из источника данных
	 * 
	 * @return DAO-объект для извлечения ведомости учебного заведения
	 * @see RegisterDAO
	 * @see RegisterDAOdb
	 */
	public RegisterDAO getRegisterDAO() {
		return REGISTER_DAO;
	}

}
