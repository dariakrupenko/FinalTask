package by.epam.simpleweb.dao;

import by.epam.simpleweb.dao.exception.DAOException;
import by.epam.simpleweb.entity.User;

/**
 * Интерфейс UserDAO должен быть реализован всеми классами, управляющими
 * доступом к информации о пользователях
 * 
 * @author User
 *
 */
public interface UserDAO {

	/**
	 * Сохранение нового пользователя
	 * 
	 * @param user
	 *            пользователь - объект класса User
	 * @throws DAOException
	 */
	public void createUser(User user) throws DAOException;

	/**
	 * Поиск пользователя по логину и паролю
	 * 
	 * @param login
	 *            логин
	 * @param password
	 *            пароль
	 * @return объект класса User, если пользователь с таким логином и паролем
	 *         существует, иначе null
	 * @throws DAOException
	 *             - ошибке при попытке доступа к информации о пользователях
	 */
	public User findUser(String login, String password) throws DAOException;

}
