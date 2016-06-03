package main.by.epam.admissionweb.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Класс <code>DatabaseErrorFilter</code> реализует интерфейс
 * <code>Filter</code> и представляет собой фильтр, основное назначение которого
 * - отслеживание наличия ошибки подключения к базе данных. Если подключение к
 * базе данных отсутствует - запрос перенаправляется на страницу ошибке, иначе -
 * управление передается цепочке фильтров.
 * <p>
 * Флаг ошибки подключения к базе данных находится в контексте приложения в
 * качестве атрибута.
 * 
 * @author Daria Krupenko
 *
 */
public class DatabaseErrorFilter implements Filter {

	/**
	 * Имя атрибута контекста приложения, содержащего флаг ошибки
	 */
	private static final String ERROR_ATTR = "databaseError";

	/**
	 * Путь к странице ошибки
	 */
	private static final String ERROR_PAGE = "/WEB-INF/error/database_error.jsp";

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	/**
	 * Отслеживание ошибки подключения к базе данных.
	 * <p>
	 * Если подключение к базе данных отсутствует - запрос перенаправляется на
	 * страницу ошибке, иначе - управление передается цепочке фильтров.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		boolean error = false;
		Object errorObj = request.getServletContext().getAttribute(ERROR_ATTR);
		if (errorObj instanceof Boolean) {
			error = (Boolean) errorObj;
		}
		if (error) {
			request.getRequestDispatcher(ERROR_PAGE).forward(request, response);
		} else {
			chain.doFilter(request, response);
		}
	}

}
