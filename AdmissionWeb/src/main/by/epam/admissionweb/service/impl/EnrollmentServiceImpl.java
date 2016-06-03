package main.by.epam.admissionweb.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.dao.DAOFactory;
import main.by.epam.admissionweb.dao.EnrollmentDAO;
import main.by.epam.admissionweb.dao.exception.DAOException;
import main.by.epam.admissionweb.dao.exception.TransactionException;
import main.by.epam.admissionweb.entity.Enroll;
import main.by.epam.admissionweb.service.EnrollmentService;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>EnrollmentServiceImpl</code> реализует интерфейс
 * <code>EnrollmentService</code> и является сервис-объектом по обработке данных
 * о наборах учебного заведения.
 * 
 * @author Daria Krupenko
 *
 */
public class EnrollmentServiceImpl implements EnrollmentService {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Получение списка наборов. Количество элементов списка может быть
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
	 * @return список наборов, привязанный к номеру страницы
	 *         <code>requiredPage</code>
	 * @throws ServiceException
	 *             при попытке чтения/записи информации из источника произошла
	 *             ошибка
	 * @see EnrollmentDAO
	 */
	@Override
	public List<Enroll> getEnrollsList(int requiredPage, int elementsCount) throws ServiceException {
		LOGGER.debug("SERVICE : EnrollmentServiceImpl.getEnrollsList()");
		try {
			DAOFactory factory = DAOFactory.getInstance();
			EnrollmentDAO dao = factory.getEnrollmentDAO();
			int beginIndex = ServiceHelper.REQUIRED_PAGE_DEFAULT;
			if (requiredPage != ServiceHelper.REQUIRED_PAGE_DEFAULT
					&& elementsCount != ServiceHelper.ELEMENTS_MAX_VALUE) {
				beginIndex = elementsCount * (requiredPage - 1);
			}
			List<Enroll> list = dao.getEnrollsList(beginIndex, elementsCount, ServiceHelper.NO_TRANSACTION);
			return list;
		} catch (DAOException ex) {
			throw new ServiceException("SERVICE : Unable to get enrolls list", ex);
		}
	}

	/**
	 * Получение количества наборов
	 * 
	 * @return количество наборов
	 * @throws ServiceException
	 *             при попытке чтения/записи информации из источника произошла
	 *             ошибка
	 * @see EnrollmentDAO
	 */
	@Override
	public int getEnrollsNumber() throws ServiceException {
		LOGGER.debug("SERVICE : EnrollmentServiceImpl.getEnrollsNumber()");
		try {
			DAOFactory factory = DAOFactory.getInstance();
			EnrollmentDAO dao = factory.getEnrollmentDAO();
			int enrollsNumber = dao.getEnrollsNumber(ServiceHelper.NO_TRANSACTION);
			return enrollsNumber;
		} catch (DAOException ex) {
			throw new ServiceException("SERVICE : Unable to get enrolls number", ex);
		}
	}

	/**
	 * Открыть набор.
	 * <p>
	 * Перед открытием набора метод осуществляет валидацию данных, обращаясь к
	 * объекту {@link ValidatorService}.
	 * <p>
	 * Операция открытия нового набора представляет собой транзакцию, состоящию
	 * из нескольких действий: удалить предыдущий набор, создать новый набор,
	 * обнулить проходные баллы всех факультетов.
	 * 
	 * @param e
	 *            объект набора, который необходимо открыть
	 * @return объект набора, который успешно открыт и сохранен в источнике
	 *         данных; null - объект набора не прошел валидацию.
	 * @throws ServiceException
	 *             при попытке чтения/записи информации из источника произошла
	 *             ошибка
	 * @see EnrollmentDAO
	 */
	@Override
	public Enroll startEnroll(Enroll e) throws ServiceException {
		LOGGER.debug("SERVICE : EnrollmentServiceImpl.startEnroll()");
		ValidatorService service = ValidatorService.getInstance();
		EnrollmentDAO dao = null;
		int trCode = 0;
		boolean isValid = service.validateEnroll(e);
		if (!isValid) {
			return null;
		} else {
			Enroll lastEnroll = getLastEnroll();
			DAOFactory factory = DAOFactory.getInstance();
			dao = factory.getEnrollmentDAO();
			try {
				trCode = dao.beginTransaction();
				if (lastEnroll != null) {
					dao.deleteEnroll(lastEnroll.getId(), trCode);
				}
				int id = dao.createEnroll(e, trCode);
				dao.resetPassRates(trCode);
				e.setId(id);
				dao.commitTransaction(trCode);
				LOGGER.debug("SERVICE : EnrollmentServiceImpl.startEnroll (e = {})", e);
				return e;
			} catch (DAOException ex) {
				try {
					dao.rollbackTransaction(trCode);
				} catch (TransactionException ex1) {
					throw new ServiceException("SERVICE : Unable to start enroll (transaction)", ex1);
				}
				throw new ServiceException("SERVICE : Unable to start enroll", ex);
			}

		}
	}

	/**
	 * Удаление набора.
	 * <p>
	 * Удаление набора невозможно, если он является активным набором.
	 * <p>
	 * Операция удаления набора представляет собой транзакцию, состоящую из
	 * нескольких действий: удаление набора, обнуление проходных баллов всех
	 * факультетов.
	 * 
	 * @param id
	 *            id набора, который должен быть удален
	 * @return true - если набор успешно удален, false - набор с if
	 *         <code>id</code> является активным
	 * @throws ServiceException
	 *             при попытке чтения/записи информации из источника произошла
	 *             ошибка
	 * @see EnrollmentDAO
	 */
	@Override
	public boolean deleteEnroll(int id) throws ServiceException {
		LOGGER.debug("SERVICE : EnrollmentServiceImpl.deleteEnroll()");
		EnrollmentDAO dao = DAOFactory.getInstance().getEnrollmentDAO();
		Enroll e = getCurrentEnroll();
		if (e != null && e.getId() == id) {
			return false;
		}
		int trCode = 0;
		try {
			trCode = dao.beginTransaction();
			dao.deleteEnroll(id, trCode);
			dao.resetPassRates(trCode);
			dao.commitTransaction(trCode);
			return true;
		} catch (DAOException ex) {
			try {
				dao.rollbackTransaction(trCode);
			} catch (TransactionException ex1) {
				throw new ServiceException("SERVICE : Unable to delete enroll (transaction)", ex1);
			}
			throw new ServiceException("SERVICE : Unable to delete enroll", ex);
		}
	}

	/**
	 * Получение текущего набора
	 * 
	 * @return текущий набор
	 * @throws ServiceException
	 *             при попытке чтения/записи информации из источника произошла
	 *             ошибка
	 * @see EnrollmentDAO
	 */
	@Override
	public Enroll getCurrentEnroll() throws ServiceException {
		LOGGER.debug("SERVICE : EnrollmentServiceImpl.getCurrentEnroll()");
		try {
			DAOFactory factory = DAOFactory.getInstance();
			EnrollmentDAO dao = factory.getEnrollmentDAO();
			List<Enroll> enrolls = dao.getEnrollsByStatus(true, ServiceHelper.NO_TRANSACTION);
			Enroll e = null;
			if (enrolls != null && !enrolls.isEmpty()) {
				e = enrolls.get(0);
			}
			LOGGER.debug("SERVICE : EnrollmentServiceImpl.getCurrentEnroll (e = {})", e);
			return e;
		} catch (DAOException ex) {
			throw new ServiceException("SERVICE : Unable to get current enroll", ex);
		}
	}

	/**
	 * Получение последнего набора
	 * 
	 * @return последний набор
	 * @throws ServiceException
	 *             при попытке чтения/записи информации из источника произошла
	 *             ошибка
	 * @see EnrollmentDAO
	 */
	@Override
	public Enroll getLastEnroll() throws ServiceException {
		LOGGER.debug("SERVICE : EnrollmentServiceImpl.getLastEnroll()");
		try {
			DAOFactory factory = DAOFactory.getInstance();
			EnrollmentDAO dao = factory.getEnrollmentDAO();
			Enroll e = dao.getLastEnroll(ServiceHelper.NO_TRANSACTION);
			return e;
		} catch (DAOException ex) {
			throw new ServiceException("Unable to get last enroll", ex);
		}

	}

	/**
	 * Получение состояния текущего набора.
	 * <p>
	 * Метод делегирует выполнение операций методу
	 * <code>getCurrentEnroll()</code>
	 * 
	 * @return true - в данный момент существует открытый набор; false - набор
	 *         закрыт или не найдено ни одного набора
	 * @throws ServiceException
	 *             при попытке чтения/записи информации из источника произошла
	 *             ошибка
	 */
	@Override
	public boolean isCurrentEnroll() throws ServiceException {
		LOGGER.debug("SERVICE : EnrollmentServiceImpl.isCurrentEnroll()");
		boolean isCurrentEnroll = getCurrentEnroll() == null ? false : true;
		LOGGER.debug("SERVICE : EnrollmentServiceImpl.isCurrentEnroll ({})", isCurrentEnroll);
		return isCurrentEnroll;
	}

	/**
	 * Подвести итоги текущего набора.
	 * <p>
	 * Перед подведение итогов метод получает текущий активный набора вызовом
	 * <code>getCurrentEnroll()</code>. Подведение итогов набора представляет
	 * собой транзакцию, состоящую из нескольких операций: поменять статусы
	 * зачисленных абитуриентов на постоянные, поменять статусы незачисленных
	 * абитуриентов на постоянные, поменять статус набора на закрытый.
	 * 
	 * @throws ServiceException
	 *             при попытке чтения/записи информации из источника произошла
	 *             ошибка
	 */
	@Override
	public void completeCurrentEnroll() throws ServiceException {
		LOGGER.debug("SERVICE : EnrollmentServiceImpl.completeCurrentEnroll()");
		EnrollmentDAO dao = DAOFactory.getInstance().getEnrollmentDAO();
		Enroll e = getCurrentEnroll();
		int trCode = 0;
		try {
			trCode = dao.beginTransaction();
			dao.completeStatuses(true, e, trCode);
			dao.completeStatuses(false, e, trCode);
			dao.completeEnroll(e, new Date(), trCode);
			dao.commitTransaction(trCode);
		} catch (DAOException ex) {
			try {
				dao.rollbackTransaction(trCode);
			} catch (TransactionException ex1) {
				throw new ServiceException("SERVICE : Unable to complete current enroll (transaction)", ex1);
			}
			throw new ServiceException("SERVICE : Unable to complete enroll", ex);
		}

	}

}
