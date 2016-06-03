package test.by.epam.admissionweb.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import main.by.epam.admissionweb.dao.DAOFactory;
import main.by.epam.admissionweb.dao.FacultyDAO;
import main.by.epam.admissionweb.dao.exception.DAOException;
import main.by.epam.admissionweb.dao.pool.ConnectionPool;
import main.by.epam.admissionweb.dao.pool.exception.ConnectionPoolException;
import main.by.epam.admissionweb.dao.pool.impl.ConnectionPoolImpl;

import main.by.epam.admissionweb.entity.Faculty;

/**
 * Класс <code>FacultyDAOTest</code> представляет собой JUnit Test Case и
 * предназначен для тестирования DAO-объекта {@link FacultyDAO}.
 * 
 * @author Daria Krupenko
 * @see FacultyDAO
 *
 */
public class FacultyDAOTest {

	/**
	 * Пул соединений с базой данных
	 */
	private static ConnectionPool pool;

	/**
	 * Фабрика для получения DAO-объектов
	 */
	private static DAOFactory daoFactory;

	/**
	 * Код транзакции по умолчанию
	 */
	private static final int NO_TRANSACTION = -1;

	/**
	 * Инициализация пула соединений с базой данных и фабрики получения
	 * DAO-объектов
	 * 
	 * @throws ConnectionPoolException
	 *             если при инициализации пула соединений произошла ошибка
	 * @see ConnectionPool
	 * @see DAOFactory
	 */
	@BeforeClass
	public static void initTestContext() throws ConnectionPoolException {
		pool = ConnectionPoolImpl.getInstance();
		pool.initConnectionPool();
		daoFactory = DAOFactory.getInstance();
	}

	/**
	 * JUnit Test, который заключается в создании нового факультета, записи его
	 * в источник данных, затем факультет обновляется и изменения снова вносятся
	 * в источник данных. После этого факультет удаляется.
	 * <p>
	 * Данные действия не рассматриваются как единая бизнес-транзакция
	 * 
	 * @throws DAOException
	 *             если при чтении/записи данных произошла ошибка
	 */
	@Test
	public void testFaculty() throws DAOException {
		FacultyDAO dao = daoFactory.getFacultyDAO();
		Faculty f = new Faculty();
		f.setTitle("FITC");
		f.setDescription("FITC description");
		f.setLogoname("fitc.png");
		f.setPhone("375236985414");
		f.setAddress("BSUIR, Minsk City");
		f.setDean("FITC dean");
		f.setPlan(35);
		int id = dao.createFaculty(f, NO_TRANSACTION);
		f.setId(id);
		f.setPlan(10);
		dao.updateFaculty(f, NO_TRANSACTION);
		Faculty fDb = dao.getFaculty(id, null, NO_TRANSACTION);
		assertEquals(10, fDb.getPlan());
		dao.deleteFaculty(id, NO_TRANSACTION);
		Faculty deletedF = dao.getFaculty(id, null, NO_TRANSACTION);
		assertNull(deletedF);
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
