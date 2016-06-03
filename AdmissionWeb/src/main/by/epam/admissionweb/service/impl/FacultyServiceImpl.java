package main.by.epam.admissionweb.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.dao.DAOFactory;
import main.by.epam.admissionweb.dao.FacultyDAO;
import main.by.epam.admissionweb.dao.exception.DAOException;
import main.by.epam.admissionweb.entity.Enroll;
import main.by.epam.admissionweb.entity.Faculty;
import main.by.epam.admissionweb.service.EnrollmentService;
import main.by.epam.admissionweb.service.FacultyService;
import main.by.epam.admissionweb.service.RegisterService;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.AlreadyExistsException;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>FacultyServiceImpl</code> реализует интерфейс
 * <code>FacultyService</code> и является сервис-объектом по обработке данных о
 * факультетах учебного заведения.
 * 
 * @author Daria Krupenko
 *
 */
public class FacultyServiceImpl implements FacultyService {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Получение списка факультетов. Количество элементов списка может быть
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
	 * @return список факультетов, привязанный к номеру страницы
	 *         <code>requiredPage</code>; null - не найдено ни одной дисциплины
	 * @throws ServiceException
	 *             при попытке чтении/записи информации из источника произошла
	 *             ошибка
	 * @see FacultyDAO
	 */
	@Override
	public List<Faculty> getFacultiesList(int requiredPage, int elementsCount) throws ServiceException {
		LOGGER.debug("SERVICE : FacultyServiceImpl.getFacultiesList()");
		try {
			EnrollmentService eService = ServiceFactory.getInstance().getEnrollmentService();
			Enroll e = eService.getLastEnroll();
			DAOFactory factory = DAOFactory.getInstance();
			FacultyDAO dao = factory.getFacultyDAO();
			int beginIndex = ServiceHelper.REQUIRED_PAGE_DEFAULT;
			if (requiredPage != ServiceHelper.REQUIRED_PAGE_DEFAULT
					&& elementsCount != ServiceHelper.ELEMENTS_MAX_VALUE) {
				beginIndex = elementsCount * (requiredPage - 1);
			}
			List<Faculty> list = dao.getFacultiesList(beginIndex, elementsCount, e, ServiceHelper.NO_TRANSACTION);
			return list;
		} catch (DAOException ex) {
			throw new ServiceException("SERVICE : Unable to get faculties list", ex);
		}
	}

	/**
	 * Получение количества факультетов
	 * 
	 * @return количество факультетов
	 * @throws ServiceException
	 *             при попытке чтении/записи информации из источника произошла
	 *             ошибка
	 * @see FacultyDAO
	 */
	@Override
	public int getFacultiesNumber() throws ServiceException {
		LOGGER.debug("SERVICE : FacultyServiceImpl.getFacultiesNumber()");
		try {
			DAOFactory factory = DAOFactory.getInstance();
			FacultyDAO dao = factory.getFacultyDAO();
			int facultiesNumber = dao.getFacultiesNumber(ServiceHelper.NO_TRANSACTION);
			return facultiesNumber;
		} catch (DAOException ex) {
			throw new ServiceException("SERVICE : Unable to get faculties number", ex);
		}
	}

	/**
	 * Добавление факультета в систему
	 * <p>
	 * Перед добавлением факультета метод осуществляет валидацию данных,
	 * обращаясь к объекту {@link ValidatorService}.
	 * <p>
	 * Метод также запрещает добавление, если факультет с указанным
	 * наименованием уже существует
	 * 
	 * @param f
	 *            объект факультета
	 * @return объект факультета, добавленного в систему
	 * @throws AlreadyExistsException
	 *             факультет с указанным наименованием уже существует
	 * @throws ServiceException
	 *             при попытке чтении/записи информации из источника произошла
	 *             ошибка
	 * @see FacultyDAO
	 */
	@Override
	public Faculty addFaculty(Faculty f) throws ServiceException {
		LOGGER.debug("SERVICE : FacultyServiceImpl.addFaculty()");
		ValidatorService service = ValidatorService.getInstance();
		boolean isValid = service.validateFaculty(f);
		if (!isValid) {
			return null;
		} else {
			try {
				DAOFactory factory = DAOFactory.getInstance();
				FacultyDAO dao = factory.getFacultyDAO();
				Faculty fDb = dao.getFacultyByTitle(f.getTitle(), ServiceHelper.NO_TRANSACTION);
				if (fDb != null) {
					throw new AlreadyExistsException("SERVICE : Duplicate faculty", null);
				}
				int id = dao.createFaculty(f, ServiceHelper.NO_TRANSACTION);
				f.setId(id);
				LOGGER.debug("SERVICE : FacultyServiceImpl.addFaculty (f = {})", f);
				return f;
			} catch (DAOException ex) {
				throw new ServiceException("SERVICE : Unable to add faculty", ex);
			}
		}
	}

	/**
	 * Получение факультета по его id.
	 * 
	 * @param id
	 *            id факультета
	 * @return объект факультета; null - факультет не найден
	 * @throws ServiceException
	 *             при попытке чтении/записи информации из источника произошла
	 *             ошибка
	 * @see FacultyDAO
	 */
	@Override
	public Faculty getFaculty(int id) throws ServiceException {
		LOGGER.debug("SERVICE : FacultyServiceImpl.getFaculty()");
		try {
			EnrollmentService eService = ServiceFactory.getInstance().getEnrollmentService();
			Enroll e = eService.getLastEnroll();
			DAOFactory factory = DAOFactory.getInstance();
			FacultyDAO dao = factory.getFacultyDAO();
			Faculty f = dao.getFaculty(id, e, ServiceHelper.NO_TRANSACTION);
			return f;
		} catch (DAOException ex) {
			throw new ServiceException("SERVICE : Unable to get faculty by id", ex);
		}
	}

	/**
	 * Обновление факультета.
	 * <p>
	 * Перед обновлением факультета метод осуществляет валидацию данных,
	 * обращаясь к объекту {@link ValidatorService}.
	 * <p>
	 * Метод также запрещает обновление данных, если факультет с указанным
	 * наименованием уже существует
	 * <p>
	 * Метод востановит прежний список дисциплин если активен текущий набор,
	 * если существует закрытый набор - метод также восттановить и план набора.
	 * 
	 * @param f
	 *            объект факультета с обновляемыми данными
	 * @return объект факультета с обновленными данными; null - объект
	 *         факультета не прошел валидацию
	 * @throws AlreadyExistsException
	 *             факультет с указанным наименованием уже существует
	 * @throws ServiceException
	 *             при попытке чтении/записи информации из источника произошла
	 *             ошибка
	 * @see FacultyDAO
	 */
	@Override
	public Faculty updateFaculty(Faculty f) throws ServiceException {
		LOGGER.debug("SERVICE : FacultyServiceImpl.updateFaculty()");
		ValidatorService service = ValidatorService.getInstance();
		boolean isValid = service.validateFaculty(f);
		if (!isValid) {
			return null;
		} else {
			try {
				DAOFactory factory = DAOFactory.getInstance();
				FacultyDAO dao = factory.getFacultyDAO();
				Faculty fDb = dao.getFacultyByTitle(f.getTitle(), ServiceHelper.NO_TRANSACTION);
				if (fDb != null && fDb.getId() != f.getId()) {
					throw new AlreadyExistsException("SERVICE : Duplicate faculty", null);
				}
				Faculty oldF = getFaculty(f.getId());
				EnrollmentService eService = ServiceFactory.getInstance().getEnrollmentService();
				Enroll e = eService.getLastEnroll();
				if (e != null) {
					if (!oldF.getDisciplines().equals(f.getDisciplines())) {
						LOGGER.debug("SERVICE : FacultyServiceImpl.updateFaculty (restore disciplines list)");
						f.setDisciplines(oldF.getDisciplines());
					}
					if ((oldF.getPlan() != f.getPlan()) && !e.isStatus()) {
						LOGGER.debug("SERVICE : FacultyServiceImpl.updateFaculty (restore plan)");
						f.setPlan(oldF.getPlan());
					}
				}
				dao.updateFaculty(f, ServiceHelper.NO_TRANSACTION);
				if (f.getPlan() != oldF.getPlan() && e != null) {
					RegisterService rService = ServiceFactory.getInstance().getRegisterService();
					rService.recalculateRegister(f, e, ServiceHelper.NO_TRANSACTION);
				}
				LOGGER.debug("SERVICE : FacultyServiceImpl.updateFaculty (f = {})", f);
				return f;
			} catch (DAOException ex) {
				throw new ServiceException("SERVICE : Unable to update the faculty", ex);
			}
		}
	}

	/**
	 * Удаление факультета
	 * 
	 * @param id
	 *            id факультета, который необходимо удалить
	 * @throws ServiceException
	 *             при попытке чтении/записи информации из источника произошла
	 *             ошибка
	 * @see FacultyDAO
	 */
	@Override
	public void deleteFaculty(int id) throws ServiceException {
		LOGGER.debug("SERVICE : FacultyServiceImpl.deleteFaculty()");
		try {
			DAOFactory factory = DAOFactory.getInstance();
			FacultyDAO dao = factory.getFacultyDAO();
			dao.deleteFaculty(id, ServiceHelper.NO_TRANSACTION);
		} catch (DAOException ex) {
			throw new ServiceException("SERVICE : Unable to delete the faculty", ex);
		}
	}

	/**
	 * Получение возможности удаления факультета
	 * <p>
	 * 
	 * @return true - в рамках логики приложения нет причин для запрета удаления
	 *         факультета.
	 */
	@Override
	public boolean isDeleteEnable() {
		LOGGER.debug("SERVICE : FacultyServiceImpl.isDeleteEnable()");
		return true;
	}

}
