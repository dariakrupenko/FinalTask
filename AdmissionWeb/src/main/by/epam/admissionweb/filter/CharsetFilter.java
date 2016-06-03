package main.by.epam.admissionweb.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Класс <code>CharsetFilter</code> реализует интерфейс <code>Filter</code> и
 * представляет собой фильтр, основное назначение которого - установка требуемой
 * кодировки в тело HTTP-запроса и HTTP-ответа.
 * <p>
 * Требуемая кодировка извлекается на этапе инициализации фильтра посредством
 * объекта <code>FilterConfig</code>.
 * 
 * @author Daria Krupenko
 * @see FilterConfig
 *
 */
public class CharsetFilter implements Filter {

	/**
	 * Название параметра, значением которого является требуемая кодировка
	 */
	private static final String ENCODING_PARAM = "character-encoding";

	/**
	 * Требуемая кодировка
	 */
	private String encoding;

	@Override
	public void destroy() {
	}

	/**
	 * Инициализация фильтра: извлечение требуемой кодировку посредством объекта
	 * <code>FilterConfig</code>
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		encoding = config.getInitParameter(ENCODING_PARAM);
	}

	/**
	 * Установка требуемой кодировки в тело HTTP-запроса и HTTP-ответа. Далее
	 * управление передается по цепочке фильтров
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		request.setCharacterEncoding(encoding);
		response.setCharacterEncoding(encoding);
		chain.doFilter(request, response);
	}

}
