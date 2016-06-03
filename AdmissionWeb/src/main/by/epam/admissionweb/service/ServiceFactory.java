package main.by.epam.admissionweb.service;

import main.by.epam.admissionweb.service.impl.ApplicantServiceImpl;
import main.by.epam.admissionweb.service.impl.DisciplineServiceImpl;
import main.by.epam.admissionweb.service.impl.EnrollmentServiceImpl;
import main.by.epam.admissionweb.service.impl.FacultyServiceImpl;
import main.by.epam.admissionweb.service.impl.PageManagerServiceImpl;
import main.by.epam.admissionweb.service.impl.RegisterServiceImpl;

/**
 * Класс <code>ServiceFactory</code> является фабрикой для получения
 * сервис-объектов, инкапсулирующих логику приложения.
 * <p>
 * 
 * Получить объект класса <code>ServiceFactory</code> можно при помощи вызова
 * статического метода <code>getInstance()</code> данного класса.
 * <p>
 * Сервис-объекты обрабатывают данные, полученные из источника данных
 * посредством DAO-объектов, в соответсвии с логикой приложения.
 * Кроме того, сервис-объекты обязаны обеспечивать валидацию данных.
 * <p>
 * Сервис-объекты управляют <b>бизнес-транзакцией</b> - последовательностью
 * операций, которые должны быть выполнены как одно неразрывное действие.
 * <p>
 * Класс <code>ServiceFactory</code> предоставляет методы для получения
 * сервис-объектов, инкапсулирующих логику приложения относительно объектов
 * модели данных.
 * 
 * @author Daria Krupenko
 * @see DAOFactory
 *
 */
public class ServiceFactory {

	/**
	 * Объект фабрики. Создается один раз при загрузке класса в память. Далее
	 * объект фабрики можно получить, вызвав статический метод
	 * <code>getInstance()</code> данного класса.
	 */
	private static final ServiceFactory INSTANCE = new ServiceFactory();

	/**
	 * Сервис-объект для обработки данных об абитуриентах
	 */
	private static final ApplicantService APPLICANT_SERVICE = new ApplicantServiceImpl();

	/**
	 * Сервис-объект для обработки данных о дисциплинах
	 */
	private static final DisciplineService DISCIPLINE_SERVICE = new DisciplineServiceImpl();

	/**
	 * Сервис-объект для обработки данных о наборах
	 */
	private static final EnrollmentService ENROLLMENT_SERVICE = new EnrollmentServiceImpl();

	/**
	 * Сервис-объект для обработки данных о факультетах
	 */
	private static final FacultyService FACULTY_SERVICE = new FacultyServiceImpl();

	/**
	 * Сервис-объект для поддержки постраничного построения длинных списков
	 */
	private static final PageManagerService PAGE_MANAGER_SERVICE = new PageManagerServiceImpl();

	/**
	 * Сервис-объект для обработки данных ведомости абитуриентов
	 */
	private static final RegisterService REGISTER_SERVICE = new RegisterServiceImpl();

	/**
	 * Конструктор для создания объекта фабрики. Может быть вызван только из
	 * данного класса.
	 */
	private ServiceFactory() {
	}

	/**
	 * Статический метод <code>getInstance()</code> предназначен для получения
	 * объекта фабрики с целью получения сервис-объектов.
	 * 
	 * @return объект фабрики для получения сервис-объектов
	 */
	public static ServiceFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * Получение сервис-объекта для обработки данных об абитуриентах
	 * 
	 * @return сервис-объект для обработки данных об абитуриентах
	 * @see ApplicantService
	 * @see ApplicantServiceImpl
	 */
	public ApplicantService getApplicantService() {
		return APPLICANT_SERVICE;
	}

	/**
	 * Получение сервис-объекта для обработки данных о дисциплинах
	 * 
	 * @return сервис-объект для обработки данных о дисциплинах
	 * @see DisciplineService
	 * @see DisciplineServiceImpl
	 */
	public DisciplineService getDisciplineService() {
		return DISCIPLINE_SERVICE;
	}

	/**
	 * Получение сервис-объекта для обработки данных о наборах
	 * 
	 * @return сервис-объект для обработки данных о наборах
	 * @see EnrollmentService
	 * @see EnrollmentServiceImpl
	 */
	public EnrollmentService getEnrollmentService() {
		return ENROLLMENT_SERVICE;
	}

	/**
	 * Получение сервис-объекта для обработки данных о факультетах
	 * 
	 * @return сервис-объект для обработки данных о факультетах
	 * @see FacultyService
	 * @see FacultyServiceImpl
	 */
	public FacultyService getFacultyService() {
		return FACULTY_SERVICE;
	}

	/**
	 * Получение сервис-объекта для поддержки постраничного построения длинных
	 * списков
	 * 
	 * @return сервис-объект для поддержки постраничного построения длинных
	 *         списков
	 * @see PageManagerService
	 * @see PageManagerServiceImpl
	 */
	public PageManagerService getPageManagerService() {
		return PAGE_MANAGER_SERVICE;
	}

	/**
	 * Получение сервис-объекта для обработки данных ведомости
	 * 
	 * @return сервис-объект для обработки данных ведомости
	 * @see RegisterService
	 * @see RegisterServiceImpl
	 */
	public RegisterService getRegisterService() {
		return REGISTER_SERVICE;
	}

}
