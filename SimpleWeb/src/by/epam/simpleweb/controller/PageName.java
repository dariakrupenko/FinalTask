package by.epam.simpleweb.controller;

/**
 * Класс PageName предназначен для хранения констант, представляющих собой пути
 * к JSP-страницам
 * 
 * @author User
 *
 */
public class PageName {

	/**
	 * Главная страница приложения
	 */
	public static final String INDEX_PAGE = "index.jsp";
	
	/**
	 * Страница регистрации
	 */
	public static final String REGISTRATION_PAGE = "registration.jsp";
	
	/**
	 * Страница входа
	 */
	public static final String LOGIN_PAGE = "login.jsp";
	
	/**
	 * Страница ошибки
	 */
	public static final String ERROR_PAGE = "WEB-INF/jsp/error.jsp";
	
	/**
	 * USER-страница (отображается при удачной авторизации пользователя)
	 */
	public static final String USER_PAGE = "WEB-INF/jsp/user_page.jsp";
}
