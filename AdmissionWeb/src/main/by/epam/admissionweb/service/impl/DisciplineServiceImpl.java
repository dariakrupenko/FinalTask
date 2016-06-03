package main.by.epam.admissionweb.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.dao.DAOFactory;
import main.by.epam.admissionweb.dao.DisciplineDAO;
import main.by.epam.admissionweb.dao.exception.DAOException;
import main.by.epam.admissionweb.entity.Discipline;
import main.by.epam.admissionweb.service.DisciplineService;
import main.by.epam.admissionweb.service.EnrollmentService;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.AlreadyExistsException;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>DisciplineServiceImpl</code> реализует интерфейс
 * <code>DisciplineService</code> и является сервис-объектом по обработке данных
 * о дисциплинах учебного заведения.
 * 
 * @author Daria Krupenko
 *
 */
public class DisciplineServiceImpl implements DisciplineService {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Получение списка дисциплин. Количество элементов списка может быть
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
	 * @return список дисциплин, привязанный к номеру страницы
	 *         <code>requiredPage</code>; null - не найдено ни одной дисциплины
	 * @throws ServiceException
	 *             при попытке чтении/записи информации из источника произошла
	 *             ошибка
	 * @see DisciplineDAO
	 */
	@Override
	public List<Discipline> getDisciplinesList(int requiredPage, int elementsCount) throws ServiceException {
		LOGGER.debug("SERVICE : DisciplineServiceImpl.getDisciplinesList()");
		try {
			DAOFactory factory = DAOFactory.getInstance();
			DisciplineDAO dao = factory.getDisciplineDAO();
			int beginIndex = ServiceHelper.REQUIRED_PAGE_DEFAULT;
			if (requiredPage != ServiceHelper.REQUIRED_PAGE_DEFAULT
					&& elementsCount != ServiceHelper.ELEMENTS_MAX_VALUE) {
				beginIndex = elementsCount * (requiredPage - 1);
			}
			List<Discipline> list = dao.getDisciplinesList(beginIndex, elementsCount, ServiceHelper.NO_TRANSACTION);
			return list;
		} catch (DAOException ex) {
			throw new ServiceException("SERVICE : Unable to get disciplines list", ex);
		}
	}

	/**
	 * Получение количества дисциплин
	 * 
	 * @return количество дисциплин
	 * @throws ServiceException
	 *             при попытке чтении/записи информации из источника произошла
	 *             ошибка
	 * @see DisciplineDAO
	 */
	@Override
	public int getDisciplinesNumber() throws ServiceException {
		LOGGER.debug("SERVICE : DisciplineServiceImpl.getDisciplinesNumber()");
		try {
			DAOFactory factory = DAOFactory.getInstance();
			DisciplineDAO dao = factory.getDisciplineDAO();
			int disciplinesNumber = dao.getDisciplinesNumber(ServiceHelper.NO_TRANSACTION);
			return disciplinesNumber;
		} catch (DAOException ex) {
			throw new ServiceException("SERVICE : Unable to get disciplines number", ex);
		}
	}

	/**
	 * Получение дисциплины по ее id.
	 * 
	 * @param id
	 *            id дисциплины
	 * @return объект дисциплины; null - дисциплина не найдена
	 * @throws ServiceException
	 *             при попытке чтении/записи информации из источника произошла
	 *             ошибка
	 * @see DisciplineDAO
	 */
	@Override
	public Discipline getDiscipline(int id) throws ServiceException {
		LOGGER.debug("SERVICE : DisciplineServiceImpl.getDiscipline()");
		try {
			DAOFactory factory = DAOFactory.getInstance();
			DisciplineDAO dao = factory.getDisciplineDAO();
			Discipline d = dao.getDiscipline(id, ServiceHelper.NO_TRANSACTION);
			return d;
		} catch (DAOException ex) {
			throw new ServiceException("SERVICE : Unable to get the discipline by id", ex);
		}
	}

	/**
	 * Обновление дисциплины.
	 * <p>
	 * Перед обновлением дисциплины метод осуществляет валидацию данных,
	 * обращаясь к объекту {@link ValidatorService}.
	 * <p>
	 * Метод также запрещает обновление данных, если дисциплина с указанным
	 * наименованием уже существует
	 * 
	 * @param d
	 *            объект дисциплины с обновляемыми данными
	 * @return объект дисциплины с обновленными данными; null - объект
	 *         дисциплины не прошел валидацию
	 * @throws AlreadyExistsException
	 *             дисциплина с указанным наименованием уже существует
	 * @throws ServiceException
	 *             при попытке чтении/записи информации из источника произошла
	 *             ошибка
	 * @see DisciplineDAO
	 */
	@Override
	public Discipline updateDiscipline(Discipline d) throws ServiceException {
		LOGGER.debug("SERVICE : DisciplineServiceImpl.updateDiscipline()");
		ValidatorService service = ValidatorService.getInstance();
		boolean isValid = service.validateDiscipline(d);
		if (!isValid) {
			return null;
		} else {
			try {
				DAOFactory factory = DAOFactory.getInstance();
				DisciplineDAO dao = factory.getDisciplineDAO();
				Discipline dDb = dao.getDisciplineByTitle(d.getTitle(), ServiceHelper.NO_TRANSACTION);
				if (dDb != null && dDb.getId() != d.getId()) {
					throw new AlreadyExistsException("SERVICE : Duplicate discipline", null);
				}
				dao.updateDiscipline(d, ServiceHelper.NO_TRANSACTION);
				LOGGER.debug("SERVICE : DisciplineServiceImpl.updateDiscipline (d = {})", d);
				return d;
			} catch (DAOException ex) {
				throw new ServiceException("SERVICE : Unable to update the discipline", ex);
			}
		}
	}

	/**
	 * Удаление дисциплины
	 * 
	 * @param id
	 *            id дисциплины, которую необходимо удалить
	 * @throws ServiceException
	 *             при попытке чтении/записи информации из источника произошла
	 *             ошибка
	 * @see DisciplineDAO
	 */
	@Override
	public void deleteDiscipline(int id) throws ServiceException {
		LOGGER.debug("SERVICE : DisciplineServiceImpl.deleteDiscipline()");
		try {
			DAOFactory factory = DAOFactory.getInstance();
			DisciplineDAO dao = factory.getDisciplineDAO();
			dao.deleteDiscipline(id, ServiceHelper.NO_TRANSACTION);
		} catch (DAOException ex) {
			throw new ServiceException("SERVICE : Unable to delete discipline", ex);
		}
	}

	/**
	 * Добавление дисциплины в систему
	 * <p>
	 * Перед добавлением дисциплины метод осуществляет валидацию данных,
	 * обращаясь к объекту {@link ValidatorService}.
	 * <p>
	 * Метод также запрещает добавление, если дисциплина с указанным
	 * наименованием уже существует
	 * 
	 * @param d
	 *            объект дисциплины
	 * @return объект дисциплины, добавленной в систему
	 * @throws AlreadyExistsException
	 *             дисциплина с указанным наименованием уже существует
	 * @throws ServiceException
	 *             при попытке чтении/записи информации из источника произошла
	 *             ошибка
	 * @see DisciplineDAO
	 */
	@Override
	public Discipline addDiscipline(Discipline d) throws ServiceException {
		LOGGER.debug("SERVICE : DisciplineServiceImpl");
		ValidatorService service = ValidatorService.getInstance();
		boolean isValid = service.validateDiscipline(d);
		if (!isValid) {
			return null;
		} else {
			try {
				DAOFactory factory = DAOFactory.getInstance();
				DisciplineDAO dao = factory.getDisciplineDAO();
				Discipline dDb = dao.getDisciplineByTitle(d.getTitle(), ServiceHelper.NO_TRANSACTION);
				if (dDb != null) {
					throw new AlreadyExistsException("SERVICE : Duplicate discipline", null);
				}
				int id = dao.createDiscipline(d, ServiceHelper.NO_TRANSACTION);
				d.setId(id);
				LOGGER.debug("SERVICE : DisciplineServiceImpl.addDiscipline (d = {})", d);
				return d;
			} catch (DAOException ex) {
				throw new ServiceException("SERVICE : Unable to add discipline", ex);
			}
		}
	}

	/**
	 * Получение возможности удаления дисциплины
	 * <p>
	 * 
	 * @return true - если удаление дисциплины возможно; false - если существует
	 *         текущий активный набор
	 * @throws ServiceException
	 *             при попытке чтении/записи информации из источника произошла
	 *             ошибка
	 * @see EnrollmentService
	 */
	@Override
	public boolean isDeleteEnable() throws ServiceException {
		LOGGER.debug("SERVICE : DisciplineServiceImpl.isDeleteEnable()");
		EnrollmentService service = ServiceFactory.getInstance().getEnrollmentService();
		boolean isDeleteEnable = service.getLastEnroll() == null ? true : false;
		LOGGER.debug("SERVICE : DisciplineServiceImpl.isDeleteEnable ({})", isDeleteEnable);
		return isDeleteEnable;
	}

}
