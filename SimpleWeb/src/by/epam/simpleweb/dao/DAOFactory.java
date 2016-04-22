package by.epam.simpleweb.dao;

import by.epam.simpleweb.dao.impl.UserDAOdb;

/**
 * Класс DAOFactory представляет собой фабрику для создания объектов DAO
 * 
 * @author User
 *
 */
public class DAOFactory {

	/**
	 * Объект фабрики
	 */
	private static final DAOFactory instance = new DAOFactory();

	private DAOFactory() {
	}

	/**
	 * Получение объекта фабрики
	 * 
	 * @return объект фабрики
	 */
	public static DAOFactory getInstance() {
		return instance;
	}

	/**
	 * Получение объекта DAO, управляющего пользователями
	 * 
	 * @return объект UserDAO
	 */
	public UserDAO getUserDAO() {
		return new UserDAOdb();
	}

}
