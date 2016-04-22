package by.epam.simpleweb.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CharsetFilter implements Filter {

	private static final Logger logger = LogManager.getRootLogger();

	private String encoding;

	public void destroy() {
	}

	public void init(FilterConfig config) throws ServletException {
		encoding = config.getInitParameter("character-encoding");
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		request.setCharacterEncoding(encoding);
		response.setCharacterEncoding(encoding);
		logger.debug("CharsetFilter : {} encoding has been successfully setted", encoding);
		chain.doFilter(request, response);
	}

}
