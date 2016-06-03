package test.by.epam.admissionweb.service;

import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import main.by.epam.admissionweb.dao.pool.ConnectionPool;
import main.by.epam.admissionweb.dao.pool.exception.ConnectionPoolException;
import main.by.epam.admissionweb.dao.pool.impl.ConnectionPoolImpl;
import main.by.epam.admissionweb.entity.Enroll;
import main.by.epam.admissionweb.service.EnrollmentService;
import main.by.epam.admissionweb.service.ServiceFactory;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Класс <code>EnrollmentServiceTest</code> представляет собой JUnit Test Case и
 * предназначен для тестирования сервис-объекта {@link EnrollmentService}.
 * 
 * @author Daria Krupenko
 * @see EnrollmentService
 *
 */
public class EnrollmentServiceTest {

	/**
	 * Пул соединений с базой данных
	 */
	private static ConnectionPool pool;

	/**
	 * Фабрика для получения сервис-объектов
	 */
	private static ServiceFactory serviceFactory;

	/**
	 * Инициализация пула соединений с базой данных и фабрики получения
	 * сервис-объектов
	 * 
	 * @throws ConnectionPoolException
	 *             если при инициализации пула соединений произошла ошибка
	 * @see ConnectionPool
	 * @see ServiceFactory
	 */
	@BeforeClass
	public static void initTestContext() throws ConnectionPoolException {
		pool = ConnectionPoolImpl.getInstance();
		pool.initConnectionPool();
		serviceFactory = ServiceFactory.getInstance();
	}

	/**
	 * JUnit Test, который заключается в попытке открыть новый набор с
	 * невалидными данными: дата начала набора по своему значению наступает
	 * позднее даты окончания набора.
	 * 
	 * @throws ServiceException
	 *             если при анализе данных произошла ошибка
	 */
	@Test
	public void testEnroll() throws ServiceException {
		EnrollmentService service = serviceFactory.getEnrollmentService();
		Enroll e = new Enroll();
		Calendar c = Calendar.getInstance();
		Date bDate = c.getTime();
		c.roll(Calendar.MONTH, -1);
		Date eDate = c.getTime();
		e.setBeginDate(bDate);
		e.setEndDate(eDate);
		e.setStatus(true);
		Enroll eS = service.startEnroll(e);
		assertNull(eS);
	}

	/**
	 * Уничтожение пула соединений
	 * 
	 * @throws ConnectionPoolException
	 *             если при уничтожении пула соединений произошла ошибка
	 */
	@AfterClass
	public static void destroyTestContext() throws ConnectionPoolException {
		pool.destroyConnectionPool();
	}

}
