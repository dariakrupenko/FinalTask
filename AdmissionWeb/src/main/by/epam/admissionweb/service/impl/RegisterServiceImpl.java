package main.by.epam.admissionweb.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.dao.DAOFactory;
import main.by.epam.admissionweb.dao.RegisterDAO;
import main.by.epam.admissionweb.dao.exception.DAOException;
import main.by.epam.admissionweb.dao.exception.TransactionException;
import main.by.epam.admissionweb.entity.Applicant;
import main.by.epam.admissionweb.entity.Enroll;
import main.by.epam.admissionweb.entity.Faculty;
import main.by.epam.admissionweb.entity.RegisterRecord;
import main.by.epam.admissionweb.service.RegisterService;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>RegisterServiceImpl</code> реализует интерфейс
 * <code>RegisterService</code> и является сервис-объектом по обработке данных
 * ведомости учебного заведения.
 * 
 * @author Daria Krupenko
 *
 */
public class RegisterServiceImpl implements RegisterService {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Статус абитуриента по умолчанию при завершении записи на факультет
	 */
	private static final String DEFAULT_STATUS = "TEMP_NOT_ADMITTED";

	/**
	 * Запись абитуриента на факультет.
	 * <p>
	 * Перед записью на факультет метод получает текущий активный набор,
	 * устанавливает статус абитуриента по умолчанию и валидирует данные.
	 * <p>
	 * Запись на факультет представляет собой транзакцию, состоящую из
	 * нескольких действий: создание записи в ведомости, пересчет ведомости.
	 * 
	 * @param r
	 *            объект записи в ведомости, которая содержит всю необходимую
	 *            информацию для записи абитуриента
	 * @return запись ведомости, ассоциированная с абитуриентом, успешно
	 *         сохраненная в источнике данных; null - запись не прошла
	 *         валидацию.
	 * @throws ServiceException
	 *             при попытке чтения/записи информации из источника произошла
	 *             ошибка
	 * @see RegisterDAO
	 * @see EnrollmentService
	 */
	@Override
	public RegisterRecord registryApplicant(RegisterRecord r) throws ServiceException {
		LOGGER.debug("SERVICE : RegisterServiceImpl.registryApplicant()");
		RegisterDAO dao = DAOFactory.getInstance().getRegisterDAO();
		int trCode = 0;
		Enroll e = ServiceFactory.getInstance().getEnrollmentService().getCurrentEnroll();
		r.setEnroll(e);
		r.setStatus(DEFAULT_STATUS);
		int totalScore = calculateTotalScore(r);
		r.setTotalScore(totalScore);
		boolean isValid = ValidatorService.getInstance().validateRecord(r);
		if (!isValid) {
			return null;
		}
		try {
			trCode = dao.beginTransaction();
			dao.createRecord(r, trCode);
			recalculateRegister(r.getFaculty(), r.getEnroll(), trCode);
			dao.commitTransaction(trCode);
			return r;
		} catch (DAOException ex) {
			try {
				dao.rollbackTransaction(trCode);
			} catch (TransactionException ex1) {
				throw new ServiceException("SERVICE : Unable to registry applicant (transaction)", ex1);
			}
			throw new ServiceException("SERVICE : Unable to registry applicant", ex);
		}

	}

	/**
	 * Получение состояния о возможности записи на факультет.
	 * <p>
	 * Запись на факультет возможна, если открыт активный набор
	 * 
	 * @return true - запись на факультет возможна, false - если не открыто ни
	 *         одного набора
	 * @throws ServiceException
	 *             при попытке чтения/записи информации из источника произошла
	 *             ошибка
	 * @see EnrollmentService
	 */
	@Override
	public boolean isRegistryEnabled() throws ServiceException {
		LOGGER.debug("SERVICE : RegisterServiceImpl.isRegistryEnabled()");
		Enroll e = ServiceFactory.getInstance().getEnrollmentService().getCurrentEnroll();
		boolean isRegistryEnabled = e == null ? false : true;
		LOGGER.debug("SERVICE : RegisterServiceImpl.isRegistryEnabled ({})", isRegistryEnabled);
		return isRegistryEnabled;

	}

	/**
	 * Получение состояния записи на факультет для конкретного абитуриента.
	 * 
	 * @param applicant
	 *            абитуриент, состояние записи для которого проверяется
	 * @return true - абитуриент уже записан на факультет, false - в противном
	 *         случае
	 * @throws ServiceException
	 *             при попытке чтения/записи информации из источника произошла
	 *             ошибка
	 */
	@Override
	public boolean isApplicantRegistered(Applicant applicant) throws ServiceException {
		LOGGER.debug("SERVICE : RegisterServiceImpl.isApplicantRegistered()");
		boolean isApplicantRegistered = getRecord(applicant) == null ? false : true;
		LOGGER.debug("SERVICE : RegisterServiceImpl.isApplicantRegistered ({})", isApplicantRegistered);
		return isApplicantRegistered;
	}

	/**
	 * Обновление записи ведомости, ассоциированной с абитуриентом
	 * <p>
	 * Данные, сохраненные в ведомости, не могут быть обновлены. Данный метод
	 * получает лишь последнее состояние записи в ведомости.
	 * 
	 * @param a
	 *            объект абитуриента
	 * @return обновленная запись в ведомости
	 * @throws ServiceException
	 *             при попытке чтения/записи информации из источника произошла
	 *             ошибка
	 */
	@Override
	public RegisterRecord updateRecordInf(Applicant a) throws ServiceException {
		LOGGER.debug("SERVICE : RegisterServiceImpl.updateRecordInf()");
		return getRecord(a);
	}

	/**
	 * Получение записи ведомости, ассоциированной с абитуриентом
	 * 
	 * @param a
	 *            объект абитуриента, для которого нужно получить запись
	 *            ведомости
	 * @return запись ведомости, ассоциированной с абитуриентом
	 * @throws ServiceException
	 *             при попытке чтения/записи информации из источника произошла
	 *             ошибка
	 * @see RegisterDAO
	 */
	@Override
	public RegisterRecord getRecord(Applicant a) throws ServiceException {
		LOGGER.debug("SERVICE : RegisterServiceImpl.getRecord()");
		try {
			DAOFactory factory = DAOFactory.getInstance();
			RegisterDAO dao = factory.getRegisterDAO();
			return dao.getRecord(a, ServiceHelper.NO_TRANSACTION);
		} catch (DAOException ex) {
			throw new ServiceException("SERVICE : Unable to get the record by applicant", ex);
		}

	}

	/**
	 * Отмена записи на факультет.
	 * <p>
	 * Отмена записи на факультет представляет собой транзакцию, состоящую из
	 * нескольких операций: удаление записи из ведомости, пересчет ведомости.
	 * 
	 * @param a
	 *            абитуриент, который отменяет запись на факультет
	 * @return объект абитуриента с отмененной записью на факультет (пустой
	 *         записью)
	 * @throws ServiceException
	 *             при попытке чтения/записи информации из источника произошла
	 *             ошибка
	 * @see ApplicantDAO
	 * @see RegisterDAO
	 */
	@Override
	public Applicant cancelRegistry(Applicant a) throws ServiceException {
		LOGGER.debug("SERVICE : RegisterServiceImpl.cancelRegistry");
		RegisterDAO dao = DAOFactory.getInstance().getRegisterDAO();
		Enroll e = ServiceFactory.getInstance().getEnrollmentService().getCurrentEnroll();
		Applicant newA = null;
		int trCode = 0;
		if (e != null) {
			try {
				trCode = dao.beginTransaction();
				dao.deleteRecord(a, trCode);
				recalculateRegister(a.getRecord().getFaculty(), a.getRecord().getEnroll(), trCode);
				newA = DAOFactory.getInstance().getApplicantDAO().getApplicant(a.getId(), trCode);
				if (newA == null) {
					throw new ServiceException("SERVICE : Unable to get applicant after deleting his record", null);
				}
				dao.commitTransaction(trCode);
				LOGGER.debug("SERVICE : RegisterServiceImpl.cancelRegistry (newA = {})", newA);
				return newA;
			} catch (DAOException ex) {
				try {
					dao.rollbackTransaction(trCode);
				} catch (TransactionException ex1) {
					throw new ServiceException("SERVICE : Unable to cancel registry (transaction)", ex1);
				}
				throw new ServiceException("SERVICE : Unable to cancel registry", ex);
			}
		} else {
			return null;
		}
	}

	/**
	 * Получение количества записей в ведомости
	 * <p>
	 * Перед получением количества записей метод получает последний набор.
	 * 
	 * @return количество записей в ведомости
	 * @throws ServiceException
	 *             при попытке чтения/записи информации из источника произошла
	 *             ошибка
	 * @see RegisterDAO
	 * @see EnrollmentService
	 */
	@Override
	public int getRecordsNumber() throws ServiceException {
		LOGGER.debug("SERVICE : RegisterServiceImpl.getRecordsNumber()");
		try {
			Enroll e = ServiceFactory.getInstance().getEnrollmentService().getLastEnroll();
			if (e != null) {
				DAOFactory factory = DAOFactory.getInstance();
				RegisterDAO dao = factory.getRegisterDAO();
				return dao.getRecordsNumber(e, ServiceHelper.NO_TRANSACTION);
			}
			return 0;
		} catch (DAOException ex) {
			throw new ServiceException("SERVICE : Unable to get records number", ex);
		}
	}

	/**
	 * Получение ведомости абитуриентов, содержащей список записей. Количество
	 * элементов списка может быть ограничено при указании номера страницы
	 * <code>requiredPage</code> и количества элементов на странице
	 * <code>elementsCount</code>.
	 * <p>
	 * Требуемая страница преобразуется в индекс элемента, с которого нужно
	 * начать построение списка.
	 * <p>
	 * Перед получением количества записей метод получает последний набор.
	 * 
	 * @param requiredPage
	 *            номер требуемой страницы
	 * @param elementsCount
	 *            количество элементов на одной странице
	 * @return список записей, привязанный к номеру страницы; null - если не
	 *         найдено ни одной записи <code>requiredPage</code>
	 * @throws ServiceException
	 *             при попытке чтения/записи информации из источника произошла
	 *             ошибка
	 * @see RegisterDAO
	 */
	@Override
	public List<RegisterRecord> getRegister(int requiredPage, int elementsCount) throws ServiceException {
		LOGGER.debug("SERVICE : RegisterServiceImpl.getRegister()");
		try {
			Enroll e = ServiceFactory.getInstance().getEnrollmentService().getLastEnroll();
			if (e != null) {
				DAOFactory factory = DAOFactory.getInstance();
				RegisterDAO dao = factory.getRegisterDAO();
				int beginIndex = ServiceHelper.REQUIRED_PAGE_DEFAULT;
				if (requiredPage != ServiceHelper.REQUIRED_PAGE_DEFAULT
						&& elementsCount != ServiceHelper.ELEMENTS_MAX_VALUE) {
					beginIndex = elementsCount * (requiredPage - 1);
				}
				List<RegisterRecord> list = dao.getRegister(beginIndex, elementsCount, e, ServiceHelper.NO_TRANSACTION);
				return list;
			}
			return null;
		} catch (DAOException ex) {
			throw new ServiceException("SERVICE : Unbale to get register", ex);
		}
	}

	/**
	 * Пересчет ведомости для заданного факультета в рамках заданного набора.
	 * Данная операция может являться частью бизнес-транзакции с кодом
	 * <code>trCode</code>.
	 * <p>
	 * Пересчет ведомости представляет собой транзакцию, состоящую из нескольких
	 * операций: обновление статусов зачисленных/незачисленных абитуриентов,
	 * вычисление нового проходного балла, обновление проходного балла.
	 * 
	 * @param f
	 *            факультет
	 * @param e
	 *            набор
	 * @param trCode
	 *            код транзакции
	 * @throws ServiceException
	 *             при попытке чтения/записи информации из источника произошла
	 *             ошибка
	 * @see RegisterDAO
	 */
	@Override
	public void recalculateRegister(Faculty f, Enroll e, int trCode) throws ServiceException {
		LOGGER.debug("come trcode = {}", trCode);
		LOGGER.debug("SERVICE : RegisterServiceImpl.recalculateRegister()");
		DAOFactory factory = DAOFactory.getInstance();
		RegisterDAO dao = factory.getRegisterDAO();
		try {
			if (trCode == ServiceHelper.NO_TRANSACTION) {
				trCode = dao.beginTransaction();
			}
			dao.updateStatuses(true, f, e, trCode);
			dao.updateStatuses(false, f, e, trCode);
			int newPassRate = dao.getChangedPassRate(f, trCode);
			int rNumber = dao.getRecordsNumberByFaculty(f, e, trCode);
			if (rNumber >= f.getPlan()) {
				dao.setNewPassRate(f, newPassRate, trCode);
			} else {
				dao.setNewPassRate(f, 0, trCode);
			}
			if (trCode == ServiceHelper.NO_TRANSACTION) {
				dao.commitTransaction(trCode);
			}
		} catch (DAOException ex) {
			try {
				dao.rollbackTransaction(trCode);
			} catch (TransactionException ex1) {
				throw new ServiceException("SERVICE : Unable to recalculate (transaction)", ex1);
			}
			throw new ServiceException("SERVICE : Unable to recalculate register", ex);
		}

	}

	/**
	 * Получение количества записей в ведомости в рамках статуса абитуриентов и
	 * указанного факультета
	 * 
	 * @param isAdmitted
	 *            статус абитуриентов
	 * @param f
	 *            факультет
	 * @return количество записей в ведомости в рамках статуса абитуриентов и
	 *         указанного факультета
	 * @throws ServiceException
	 *             при попытке чтения/записи информации из источника произошла
	 *             ошибка
	 * @see RegisterDAO
	 */
	@Override
	public int getRecordsNumberByStatusAndFaculty(boolean isAdmitted, Faculty f) throws ServiceException {
		LOGGER.debug("SERVICE : RegisterServiceImpl.getRecordsNumberByStatusAndFaculty()");
		try {
			Enroll e = ServiceFactory.getInstance().getEnrollmentService().getLastEnroll();
			if (e != null) {
				DAOFactory factory = DAOFactory.getInstance();
				RegisterDAO dao = factory.getRegisterDAO();
				int recordsNumber = dao.getRecordsNumberByStatusAndFaculty(isAdmitted, f, e,
						ServiceHelper.NO_TRANSACTION);
				return recordsNumber;
			}
			return 0;
		} catch (DAOException ex) {
			throw new ServiceException("SERVICE : Unable to get records number by status and faculty", ex);
		}
	}

	/**
	 * Получение ведомости абитуриентов, содержащей список записей, в рамках
	 * статуса и факультета. Количество элементов списка может быть ограничено
	 * при указании номера страницы <code>requiredPage</code> и количества
	 * элементов на странице <code>elementsCount</code>.
	 * <p>
	 * Требуемая страница преобразуется в индекс элемента, с которого нужно
	 * начать построение списка.
	 * <p>
	 * Перед получением количества записей метод получает последний набор.
	 * 
	 * @param requiredPage
	 *            номер требуемой страницы
	 * @param elementsCount
	 *            количество элементов на одной странице
	 * @param isAdmitted
	 *            статус абитуриентов
	 * @param f
	 *            факультет
	 * @return список записей в рамках статуса и факультета, привязанный к
	 *         номеру страницы <code>requiredPage</code>
	 * @throws ServiceException
	 *             при попытке чтения/записи информации из источника произошла
	 *             ошибка
	 * @see RegisterDAO
	 */
	@Override
	public List<RegisterRecord> getRegisterByStatusAndFaculty(int requiredPage, int elementsCount, boolean isAdmitted,
			Faculty f) throws ServiceException {
		LOGGER.debug("SERVICE : RegisterServiceImpl.getRegisterByStatusAndFaculty()");
		try {
			Enroll e = ServiceFactory.getInstance().getEnrollmentService().getLastEnroll();
			if (e != null) {
				DAOFactory factory = DAOFactory.getInstance();
				RegisterDAO dao = factory.getRegisterDAO();
				int beginIndex = ServiceHelper.REQUIRED_PAGE_DEFAULT;
				if (requiredPage != ServiceHelper.REQUIRED_PAGE_DEFAULT
						&& elementsCount != ServiceHelper.ELEMENTS_MAX_VALUE) {
					beginIndex = elementsCount * (requiredPage - 1);
				}
				List<RegisterRecord> list = dao.getRegisterByStatusAndFaculty(beginIndex, elementsCount, isAdmitted, f,
						e, ServiceHelper.NO_TRANSACTION);
				return list;
			}
			return null;
		} catch (DAOException ex) {
			throw new ServiceException("SERVICE : Unable to get register by status and faculty", ex);
		}
	}

	/**
	 * Подсчет суммарного балла абитуриента, который складывается из баллов по
	 * дисциплинам и балла аттестата.
	 * 
	 * @param r
	 *            запись ведомости абитуриента
	 * @return суммарный балл
	 */
	private int calculateTotalScore(RegisterRecord r) {
		int totalScore = r.getCertificateScore();
		for (int s : r.getScores().values()) {
			totalScore += s;
		}
		return totalScore;
	}

}
