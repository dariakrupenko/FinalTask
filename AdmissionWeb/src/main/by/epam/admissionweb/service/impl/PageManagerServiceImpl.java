package main.by.epam.admissionweb.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.service.PageManagerService;

/**
 * Класс <code>PageManagerServiceImpl</code> реализует интерфейс
 * {@link PageManagerService} и представляет собой сервис-объект для поддержки
 * постраничного построения длинных списков.
 * 
 * @author Daria Krupenko
 * @see PageManagerService
 *
 */
public class PageManagerServiceImpl implements PageManagerService {

	private static final Logger LOGGER = LogManager.getRootLogger();

	private static final int NULL_VALUE = 0;
	private static final int SCROLL_VALUE = 1;

	@Override
	public int getPagesNumber(int elementsNumber, int elementsNumberPerPage) {
		int pagesNumber;
		if (elementsNumber % elementsNumberPerPage != NULL_VALUE || elementsNumber == NULL_VALUE) {
			pagesNumber = elementsNumber / elementsNumberPerPage + SCROLL_VALUE;
		} else {
			pagesNumber = elementsNumber / elementsNumberPerPage;
		}
		LOGGER.debug("SERVICE : PageManagerServiceImpl.getPagesNumber (pages number = {})", pagesNumber);
		return pagesNumber;
	}

	@Override
	public int getRequiredPage(int currentPage, boolean next, int pagesNumber) {
		int page;
		if (next) {
			page = currentPage + SCROLL_VALUE;
			if (page > pagesNumber) {
				page = pagesNumber;
			}
		} else {
			page = currentPage - SCROLL_VALUE;
			if (page <= NULL_VALUE) {
				page = SCROLL_VALUE;
			}
		}
		return page;
	}
}
