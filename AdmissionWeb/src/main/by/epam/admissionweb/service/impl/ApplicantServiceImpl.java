package main.by.epam.admissionweb.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.dao.ApplicantDAO;
import main.by.epam.admissionweb.dao.DAOFactory;
import main.by.epam.admissionweb.dao.exception.DAOException;
import main.by.epam.admissionweb.entity.Applicant;
import main.by.epam.admissionweb.entity.RegisterRecord;
import main.by.epam.admissionweb.service.ApplicantService;
import main.by.epam.admissionweb.service.exception.AlreadyExistsException;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>ApplicantServiceImpl</code> реализует интерфейс
 * <code>ApplicantService</code> и является сервис-объектом по обработке данных
 * об абитуриентах учебного заведения.
 * 
 * @author Daria Krupenko
 *
 */
public class ApplicantServiceImpl implements ApplicantService {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Логгер для дублирования записи информации о зарегистрированных в системе
	 * абитуриентах
	 */
	private static final Logger REGISTRATION_LOGGER = LogManager.getLogger(ApplicantServiceImpl.class);

	/**
	 * Регистрация абитуриента в системе.
	 * <p>
	 * Перед регистрацией абитуриента метод осуществляет валидацию данных,
	 * обращаясь к объекту {@link ValidatorService}.
	 * <p>
	 * Метод также запрещает регистрацию абитуриента, если указанный логин уже
	 * задействован другим абитуриентом.
	 * 
	 * @param a
	 *            объект абитуриента
	 * @return объект абитуриента, зарегистрированного в системе; null - если
	 *         объект абитуриента не прошел валидацию.
	 * @throws AlreadyExistsException
	 *             абитуриент с указанным логином уже зарегистрирован в системе.
	 * @throws ServiceException
	 *             при попытке чтении/записи информации из источника произошла
	 *             ошибка
	 * @see ApplicantDAO
	 */
	@Override
	public Applicant registrateApplicant(Applicant a) throws ServiceException {
		LOGGER.debug("SERVICE : ApplicantServiceImpl.registrateApplicant()");
		ValidatorService service = ValidatorService.getInstance();
		boolean isValid = service.validateApplicant(a);
		if (!isValid) {
			return null;
		} else {
			try {
				DAOFactory factory = DAOFactory.getInstance();
				ApplicantDAO dao = factory.getApplicantDAO();
				Applicant aDb = dao.getApplicantByLogin(a.getLogin(), ServiceHelper.NO_TRANSACTION);
				if (aDb != null) {
					throw new AlreadyExistsException("Duplicate applicant", null);
				}
				int id = dao.createApplicant(a, ServiceHelper.NO_TRANSACTION);
				a.setId(id);
				LOGGER.debug("SERVICE : ApplicantServiceImpl.registrateApplicant (a = {})", a);
				REGISTRATION_LOGGER.info(a);
				return a;
			} catch (DAOException ex) {
				throw new ServiceException("SERVICE : Unable to registrate applicant", ex);
			}
		}
	}

	/**
	 * Авторизация абитуриента с указанным логином и паролем.
	 * <p>
	 * Если абитуриент успешно авторизован, из источника данных извлекается
	 * запись ведомости, ассоциированная с данным абитуриентом.
	 * 
	 * @param login
	 *            логин абитуриента
	 * @param password
	 *            пароль абитуриента
	 * @return авторизованный абитуриент; null - если абитуриент с указанным
	 *         логином и паролем не найден.
	 * @throws ServiceException
	 *             при попытке чтении/записи информации из источника произошла
	 *             ошибка
	 * @see ApplicantDAO
	 * @see RegisterDAO
	 */
	@Override
	public Applicant loginApplicant(String login, String password) throws ServiceException {
		LOGGER.debug("SERVICE : ApplicantServiceImpl.loginApplicant()");
		try {
			DAOFactory factory = DAOFactory.getInstance();
			ApplicantDAO dao = factory.getApplicantDAO();
			Applicant a = dao.getApplicantByLoginPassword(login, password, ServiceHelper.NO_TRANSACTION);
			if (a != null) {
				RegisterRecord r = factory.getRegisterDAO().getRecord(a, ServiceHelper.NO_TRANSACTION);
				a.setRecord(r);
			}
			LOGGER.debug("SERVICE : ApplicantServiceImpl.loginApplicant (a = {})", a);
			return a;
		} catch (DAOException ex) {
			throw new ServiceException("SERVICE : Unable to get applicant by login and password", ex);
		}
	}

	/**
	 * Обновление данных абитуриента.
	 * <p>
	 * Перед обновлением данных абитуриента метод осуществляет валидацию данных,
	 * обращаясь к объекту {@link ValidatorService}.
	 * <p>
	 * Метод также запрещает обновление данных, если указанный логин уже
	 * задействован другим абитуриентом.
	 * 
	 * @param a
	 *            объект абитуриента с обновляемыми данными
	 * @return объект абитуриента с обновленными данными
	 * @throws AlreadyExistsException
	 *             абитуриент с указанным логином уже зарегистрирован в системе.
	 * @throws ServiceException
	 *             при попытке чтении/записи информации из источника произошла
	 *             ошибка
	 * @see ApplicantDAO
	 */
	@Override
	public Applicant updateApplicant(Applicant a) throws ServiceException {
		LOGGER.debug("SERVICE : ApplicantServiceImpl.updateApplicant()");
		ValidatorService service = ValidatorService.getInstance();
		boolean isValid = service.validateApplicant(a);
		if (!isValid) {
			return null;
		} else {
			try {
				DAOFactory factory = DAOFactory.getInstance();
				ApplicantDAO dao = factory.getApplicantDAO();
				Applicant aDb = dao.getApplicantByLogin(a.getLogin(), ServiceHelper.NO_TRANSACTION);
				if (aDb != null && aDb.getId() != a.getId()) {
					throw new AlreadyExistsException("SERVICE : Duplicate applicant", null);
				}
				dao.updateApplicant(a, ServiceHelper.NO_TRANSACTION);
				LOGGER.debug("SERVICE : ApplicantServiceImpl.updateApplicant (a = {})", a);
				return a;
			} catch (DAOException ex) {
				throw new ServiceException("SERVICE : Unable to update the applicant", ex);
			}
		}
	}

	/**
	 * Получение абитуриента по его id.
	 * <p>
	 * Если абитуриент найден, из источника данных извлекается запись ведомости,
	 * ассоциированная с данным абитуриентом.
	 * 
	 * @param id
	 *            id абитуриента
	 * @return объект абитуриента; null - если абитуриент не найден
	 * @throws ServiceException
	 *             при попытке чтении/записи информации из источника произошла
	 *             ошибка
	 * @see ApplicantDAO
	 * @see RegisterDAO
	 */
	@Override
	public Applicant getApplicant(int id) throws ServiceException {
		LOGGER.debug("SERVICE : ApplicantServiceImpl.getApplicant()");
		try {
			DAOFactory factory = DAOFactory.getInstance();
			ApplicantDAO dao = factory.getApplicantDAO();
			Applicant a = dao.getApplicant(id, ServiceHelper.NO_TRANSACTION);
			if (a != null) {
				RegisterRecord r = factory.getRegisterDAO().getRecord(a, ServiceHelper.NO_TRANSACTION);
				a.setRecord(r);
			}
			LOGGER.debug("SERVICE : ApplicantServiceImpl.getApplicant (a = {})", a);
			return a;
		} catch (DAOException ex) {
			throw new ServiceException("SERVICE : Unable to get the applicant by id", ex);
		}
	}

	/**
	 * Получение количества абитуриентов
	 * 
	 * @return количество абитуриентов
	 * 
	 * @throws ServiceException
	 *             при попытке чтении/записи информации из источника произошла
	 *             ошибка
	 * @see ApplicantDAO
	 */
	@Override
	public int getApplicantsNumber() throws ServiceException {
		LOGGER.debug("SERVICE : ApplicantServiceImpl.getApplicantsNumber()");
		try {
			DAOFactory factory = DAOFactory.getInstance();
			ApplicantDAO dao = factory.getApplicantDAO();
			int aNumber = dao.getApplicantsNumber(ServiceHelper.NO_TRANSACTION);
			return aNumber;
		} catch (DAOException ex) {
			throw new ServiceException("SERVICE : Unable to get applicants number", ex);
		}
	}

	/**
	 * Получение списка абитуриентов. Количество элементов списка может быть
	 * ограничено при указании номера страницы <code>requiredPage</code> и
	 * количества элементов на странице <code>elementsCount</code>.
	 * <p>
	 * Требуемая страница преобразуется в индекс элемента, с которого нужно
	 * начать построение списка.
	 * 
	 * @param requiredPage
	 *            номер требуемой страницы
	 * @param elementsCount
	 *            количество элементов на одной странице
	 * @return список абитуриентов, привязанный к номеру страницы
	 *         <code>requiredPage</code>
	 * @throws ServiceException
	 *             при попытке чтении/записи информации из источника произошла
	 *             ошибка
	 * @see ApplicantDAO
	 */
	@Override
	public List<Applicant> getApplicantsList(int requiredPage, int elementsCount) throws ServiceException {
		LOGGER.debug("SERVICE : ApplicantServiceImpl.getApplicantsList()");
		try {
			DAOFactory factory = DAOFactory.getInstance();
			ApplicantDAO dao = factory.getApplicantDAO();
			int beginIndex = ServiceHelper.REQUIRED_PAGE_DEFAULT;
			if (requiredPage != ServiceHelper.REQUIRED_PAGE_DEFAULT
					&& elementsCount != ServiceHelper.ELEMENTS_MAX_VALUE) {
				beginIndex = elementsCount * (requiredPage - 1);
			}
			List<Applicant> list = dao.getApplicantsList(beginIndex, elementsCount, ServiceHelper.NO_TRANSACTION);
			return list;
		} catch (DAOException ex) {
			throw new ServiceException(ex);
		}
	}

}
