package main.by.epam.admissionweb.tag;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Класс <code>FormatElementsPerPage</code> является подклассом класса
 * <code>TagSupport</code> и представляет собой обработчик пользовательского
 * тега <b><code>adm:formatSelect</code></b>.
 * <p>
 * Пользовательский тег <b><code>adm:formatSelect</code></b> является тегом без
 * тела и предназначен для упрощения вывода раскрывающегося списка (<b>
 * <code>select</code></b>) для выбора количества элементов, выводимых на одну
 * страницу при просмотре длинных списков.
 * <p>
 * Варианты выбора перечисляются через запятую и указываются в атрибуте <b>
 * <code>variants</code></b> данного тега.
 * <p>
 * При анализе вариантов выбора списка используется класс
 * <code>StringTokenizer</code>
 * 
 * @author Daria Krupenko
 * @see TagSupport
 * @see StringTokenizer
 *
 */
public class FormatElementsPerPage extends TagSupport {

	private static final long serialVersionUID = 1L;

	/**
	 * Атрибут контекста сессии, сохраняющий выбранный пользователем вариант
	 * списка.
	 */
	private static final String ELEMENTS_PER_PAGE_ATTR = "elementsPerPage";

	/**
	 * Количество элементов, выводимых на одну страницу при просмотре длинных
	 * списков, по умолчанию
	 */
	private static final int ELEMENTS_PER_PAGE_DEFAULT = 10;

	/**
	 * Открывающий тег <code>select</code>
	 */
	private static final String SELECT_OPEN_TAG = "<select name=\"elements-per-page\">";

	/**
	 * Открывающий тег <code>option</code>
	 */
	private static final String OPTION_OPEN_TAG = "<option value=\"";

	/**
	 * Атрибут <code>selected</code> тега <code>select</code>
	 */
	private static final String SELECTED_ATTR = "\" selected>";

	/**
	 * Закрытие открывающего тега <code>select</code> без атрибута
	 * <code>selected</code>
	 */
	private static final String NO_SELECTED_ATTR = "\">";

	/**
	 * Закрывающий тег <code>option</code>
	 */
	private static final String OPTION_END_TAG = "</option>";

	/**
	 * Закрывающий тег <code>select</code>
	 */
	private static final String SELECT_END_TAG = "</select>";

	/**
	 * Разделитель вариантов выбора
	 */
	private static final String DELIMETER = ",";

	/**
	 * Строка, представляющая собой варианты выбора списка, разделенные запятой
	 */
	private String variants;

	/**
	 * Получение строки вариантов выбора списка
	 * 
	 * @return строка вариантов выбора списка
	 */
	public String getVariants() {
		return variants;
	}

	/**
	 * Установка строки вариантов выбора списка
	 * <p>
	 * Вызывается со страницы JSP при указании атриубута <code>variants</code>
	 * тега <code>adm:formatSelect</code>
	 * 
	 * @param variants
	 *            строка вариантов выбора списка
	 */
	public void setVariants(String variants) {
		this.variants = variants;
	}

	/**
	 * Анализ строки вариантов выбора списка и конструирование элемента
	 * <code>select</code> с учетом значения, выбранного пользователем, и
	 * сохраненного в контексте приложения.
	 * 
	 * @throws JspException
	 *             если при обработке тега возникла ошибка (не удается
	 *             анализировать варианты выбора списка)
	 */
	@Override
	public int doStartTag() throws JspException {
		int[] varArray = parseVariants();
		Object elementsPerPageObj = pageContext.getSession().getAttribute(ELEMENTS_PER_PAGE_ATTR);
		int elementsPerPage = ELEMENTS_PER_PAGE_DEFAULT;
		if (elementsPerPageObj != null && elementsPerPageObj instanceof Integer) {
			elementsPerPage = (Integer) elementsPerPageObj;
		}
		String selectString = constructSelectString(elementsPerPage, varArray);
		try {
			pageContext.getOut().write(selectString.toString());
		} catch (IOException ex) {
			throw new JspException("Unable to write string processed by <adm:formatSelect> tag", ex);
		}
		return SKIP_BODY;
	}

	/**
	 * Конструирование элемента <code>select</code> с учетом значения,
	 * выбранного пользователем, и сохраненного в контексте приложения.
	 * <p>
	 * Варианты выбора списка представлены в виде массива, указанного в
	 * параметре <code>varArray</code>
	 * 
	 * @param elementsPerPage
	 *            выбранное пользователем значение (при построении элемента
	 *            <select> данному значению присваивается атрибут
	 *            <code>selected</code>)
	 * @param varArray
	 *            массив вариантов выбора списка
	 * @return строка, представляющая собой сконструированный элемент
	 *         <code>select</code> с вложенными тегами <code>option</code>
	 */
	private String constructSelectString(int elementsPerPage, int[] varArray) {
		StringBuilder selectString = new StringBuilder(SELECT_OPEN_TAG);
		for (int v : varArray) {
			String optionString = null;
			if (v == elementsPerPage) {
				optionString = OPTION_OPEN_TAG + v + SELECTED_ATTR + v + OPTION_END_TAG;
			} else {
				optionString = OPTION_OPEN_TAG + v + NO_SELECTED_ATTR + v + OPTION_END_TAG;
			}
			selectString.append(optionString);
		}
		selectString.append(SELECT_END_TAG);
		return selectString.toString();
	}

	/**
	 * Анализ строки вариантов выбора списка и построение на ее основе массива,
	 * хранящего значения вариантов выбора списка.
	 * <p>
	 * При анализе вариантов выбора списка используется класс
	 * <code>StringTokenizer</code>
	 * 
	 * @return массив значений вариантов выбора списка
	 * @throws JspException
	 *             если при анализе строки произошла ошибка
	 */
	private int[] parseVariants() throws JspException {
		StringTokenizer tokenizer = new StringTokenizer(variants, DELIMETER);
		int[] varArray = new int[tokenizer.countTokens()];
		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			try {
				varArray[i] = Integer.parseInt(tokenizer.nextToken());
				i++;
			} catch (NumberFormatException ex) {
				throw new JspException("Invalid elements-per-page string format in <adm:formatSelect> tag", ex);
			}
		}
		return varArray;
	}

}
