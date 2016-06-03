package test.by.epam.admissionweb.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import main.by.epam.admissionweb.dao.DAOFactory;
import main.by.epam.admissionweb.dao.DisciplineDAO;
import main.by.epam.admissionweb.dao.exception.DAOException;
import main.by.epam.admissionweb.dao.pool.ConnectionPool;
import main.by.epam.admissionweb.dao.pool.exception.ConnectionPoolException;
import main.by.epam.admissionweb.dao.pool.impl.ConnectionPoolImpl;
import main.by.epam.admissionweb.entity.Discipline;
import main.by.epam.admissionweb.entity.Faculty;

/**
 * Класс <code>DisciplineDAOTest</code> представляет собой JUnit Test Case и
 * предназначен для тестирования DAO-объекта {@link DisciplineDAO}.
 * 
 * @author Daria Krupenko
 * @see DisciplineDAO
 *
 */
public class DisciplineDAOTest {

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
	 * JUnit Test, который заключается в создании новой дисциплины, записи ее в
	 * источник данных и последующее удаление.
	 * <p>
	 * Данные действия не рассматриваются как единая бизнес-транзакция
	 * 
	 * @throws DAOException
	 *             если при чтении/записи данных произошла ошибка
	 */
	@Test
	public void testDiscipline() throws DAOException {
		DisciplineDAO dao = daoFactory.getDisciplineDAO();
		Discipline d = new Discipline();
		d.setTitle("NewDiscipline");
		d.setFaculties(new ArrayList<Faculty>());
		int dId = dao.createDiscipline(d, NO_TRANSACTION);
		d.setId(dId);
		Discipline dDb = dao.getDiscipline(dId, NO_TRANSACTION);
		assertEquals(d, dDb);
		dao.deleteDiscipline(dId, NO_TRANSACTION);
		dDb = dao.getDiscipline(dId, NO_TRANSACTION);
		assertNull(dDb);
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
