package main.by.epam.admissionweb.command.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.command.exception.CommandException;
import main.by.epam.admissionweb.entity.Applicant;
import main.by.epam.admissionweb.entity.Discipline;
import main.by.epam.admissionweb.entity.Enroll;
import main.by.epam.admissionweb.entity.Faculty;
import main.by.epam.admissionweb.entity.RegisterRecord;

/**
 * Класс <code>CommandHelper</code> предназначен для выполнения действий, общих
 * для всех команд.
 * <p>
 * Получение объекта класса <code>CommandHelper</code> осуществляется путем
 * вызова статического метода <code>getInstance()</code> данного класса.
 * <p>
 * Назначение класса <code>CommandHelper</code>:
 * <ul>
 * <li>перенаправление запроса клиента на указанную страницу</li>
 * <li>обработка параметров определенного вида (id, дата, номер страницы и т.д.)
 * </li>
 * <li>конвертирование строки в формат даты</li>
 * <li>конструирование объекта модели данных</li>
 * </ul>
 * 
 * @author Daria Krupenko
 * @see Command
 *
 */
public class CommandHelper {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Количество элементов, выводимых на одну страницу при просмотре длинных
	 * списков, по умолчанию
	 */
	private static final int ELEMENTS_PER_PAGE_DEFAULT = 10;

	/**
	 * Формат даты (используется при конвертировании строк в формат даты)
	 */
	private static final String DATE_FORMAT = "yyyy-MM-dd";

	/**
	 * Часть параметра запроса (используется для извлечения параметра названия
	 * дисциплины)
	 */
	private static final String D_PARAM = "dId";

	/**
	 * Номер просматриваемой страницы по умолчанию
	 */
	public static final int REQUIRED_PAGE_DEFAULT = 0;

	/**
	 * Максимальное число элементов, которое может быть отображено на одной
	 * странице
	 */
	public static final int ELEMENTS_MAX_VALUE = Integer.MAX_VALUE;
	
	/**
	 * Часть значения параметра запроса (используется при извлечении баллов
	 * абитуриента по дисциплинам)
	 */
	private static final String D_PARAM_SCORES = "dInf";

	/**
	 * Разделитель (используется для разделения id и наименовая дисциплины при
	 * извлечении баллов абитуриента по дисциплинам)
	 */
	private static final String DELIMETER = ".";

	/**
	 * Объект класса <code>CommandHelper</code>, создается один раз при загрузке
	 * класса в память
	 */
	private static final CommandHelper INSTANCE = new CommandHelper();

	private CommandHelper() {
	}

	/**
	 * Получение объекта класса <code>CommandHelper</code>
	 * 
	 * @return объект класса <code>CommandHelper</code>
	 */
	public static CommandHelper getInstance() {
		return INSTANCE;
	}

	/**
	 * Перенаправление запроса клиента на указанную в параметре
	 * <code>pageName</code> страницу.
	 * 
	 * @param request
	 *            контекст запроса клиента
	 * @param response
	 *            контекст ответа на HTTP-запрос
	 * @param pageName
	 *            название страницы, на которую нужно перенаправить запрос
	 *            клиента
	 * @throws CommandException
	 *             если указанная страница отсутствует или содержит ошибки
	 * @see HttpServletRequest
	 * @see HttpServletResponse
	 * @see RequestDispatcher
	 */
	public void redirectToPage(HttpServletRequest request, HttpServletResponse response, String pageName)
			throws CommandException {
		LOGGER.debug("COMMAND HELPER : redirecting to page {}", pageName);
		RequestDispatcher dispatcher = request.getRequestDispatcher(pageName);
		if (dispatcher == null) {
			throw new CommandException("Unable to get RequestDispatcher", null);
		}
		try {
			dispatcher.forward(request, response);
		} catch (IOException | ServletException ex) {
			throw new CommandException("Unable to redirect to page " + pageName, ex);
		}

	}

	/**
	 * Преобразование строки id, указанной в параметре <code>idStr</code> в
	 * формат типа данных int
	 * 
	 * @param idStr
	 *            строка id
	 * @return id в формате int; 0 - если <code>idStr</code> равна null или не
	 *         может быть преобразована в тип данных int.
	 */
	public int parseId(String idStr) {
		int id = 0;
		try {
			id = Integer.parseInt(idStr);
			return id;
		} catch (NumberFormatException ex) {
			LOGGER.error("COMMAND HELPER : invalid id parameter format", ex);
			return 0;
		}
	}

	/**
	 * Преобразование номера текущей страницы в формате строки, указанной в
	 * параметре <code>pageStr</code> в формат типа данных int
	 * 
	 * @param pageStr
	 *            строка номера текущей страницы
	 * @return номер текущей страницы в формате int; 0 - если
	 *         <code>pageStr</code> равна null или не может быть преобразована в
	 *         тип данных int.
	 */
	public int parseCurrentPage(String pageStr) {
		if (pageStr == null || pageStr.isEmpty()) {
			return 0;
		} else {
			try {
				return Integer.parseInt(pageStr);
			} catch (NumberFormatException ex) {
				LOGGER.error("COMMAND HELPER : invalid page parameter format", ex);
				return 0;
			}
		}
	}

	/**
	 * Преобразование направления прокрутки страниц в формате строки (параметр
	 * <code>directionStr</code>) к типу данных boolean.
	 * 
	 * @param directionStr
	 *            направление прокрутки страниц в формате строки
	 * @return true - если прокрутка производится вперед или
	 *         <code>directionStr</code> равен null или имеет неверный формат;
	 *         false - если прокрутка страниц производится назад
	 */
	public boolean parseDirection(String directionStr) {
		if (directionStr == null || directionStr.isEmpty()) {
			return true;
		} else {
			try {
				return Boolean.parseBoolean(directionStr);
			} catch (NumberFormatException ex) {
				LOGGER.error("COMMAND HELPER : invalid direction parameter format", ex);
				return true;
			}
		}
	}

	/**
	 * Преобразование статуса абитуриентов из формата строки в тип данных
	 * boolean
	 * 
	 * @param statusStr
	 *            статус абитуриента в формате строки
	 * @return true - если статус абитуриентов положительный или не может быть
	 *         приведен к типу данных boolean; false - если статус абитуриентов
	 *         отрицательный
	 */
	public boolean parseStatus(String statusStr) {
		if (statusStr == null || statusStr.isEmpty()) {
			return true;
		} else {
			try {
				return Boolean.parseBoolean(statusStr);
			} catch (NumberFormatException ex) {
				LOGGER.error("COMMAND HELPER : invalid applicant status parameter format", ex);
				return true;
			}
		}
	}

	/**
	 * Приведение количества элементов в виде объекта
	 * <code>elementsPerPageDefault</code> к типу int
	 * 
	 * @param elementsPerPageObj
	 *            количество элементов списка, выводимых на одну страницу,
	 *            инкапсулированное в объект класса <code>Object</code>
	 * @return количество элементов, выводимых на страницу, или значение по
	 *         умолчанию (если привести объект <code>elementsPerPageObj</code>
	 *         не удается)
	 */
	public int parseElementsPerPage(Object elementsPerPageObj) {
		if (elementsPerPageObj == null) {
			return ELEMENTS_PER_PAGE_DEFAULT;
		} else if (elementsPerPageObj instanceof String) {
			try {
				return Integer.parseInt((String) elementsPerPageObj);
			} catch (NumberFormatException ex) {
				LOGGER.error("COMMAND HELPER : invalid elements-per-page parameter format", ex);
				return ELEMENTS_PER_PAGE_DEFAULT;
			}
		} else if (elementsPerPageObj instanceof Integer) {
			return (Integer) elementsPerPageObj;
		} else {
			return ELEMENTS_PER_PAGE_DEFAULT;
		}
	}

	/**
	 * Преобразование строки, представленной параметром <code>str</code>, в
	 * дату.
	 * 
	 * @param str
	 *            дата в формате строки
	 * @return объект класса <code>Date</code> или <code>null</code>, если
	 *         <code>str</code> имеет неверный формат
	 * @see SimpleDateFormat
	 */
	public Date parseDate(String str) {
		Date date = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
			return format.parse(str);
		} catch (ParseException ex) {
			LOGGER.error("COMMAND HELPER : invalid date parameter format", ex);
			date = null;
		}
		return date;
	}

	/**
	 * Преобразование плана набора в формате строки (параметр
	 * <code>planStr</code>) в тип данных int
	 * 
	 * @param planStr
	 *            план набора в формате строки
	 * @return план набора в формате типа данных int или -1, если
	 *         <code>planStr</code> имеет неверный формат или равен
	 *         <code>null</code>
	 */
	public int parsePlan(String planStr) {
		try {
			return Integer.parseInt(planStr);
		} catch (NumberFormatException ex) {
			LOGGER.error("COMMAND HELPER : invalid plan parameter format", ex);
			return -1;
		}
	}

	/**
	 * Преобразование года в формате строки (параметр <code>yearStr</code>) в
	 * тип данных int
	 * 
	 * @param yearStr
	 *            год в формате строки
	 * @return год в формате типа данных int или -1, если <code>yearStr</code>
	 *         имеет неверный формат или равен <code>null</code>
	 */
	public int parseYear(String yearStr) {
		try {
			return Integer.parseInt(yearStr);
		} catch (NumberFormatException ex) {
			LOGGER.error("COMMAND HELPER : invalid year parameter format", ex);
			return -1;
		}
	}

	/**
	 * Извлечение списка дисицплин из запроса (параметр <code>request</code>).
	 * <p>
	 * Если id дисциплины не может быть преобразован в тип данных int, то id =
	 * 0. Список факультетов для дисциплин не указывается.
	 * 
	 * @param request
	 *            контекст запроса клиента, из которого извлекаются нужные
	 *            параметры.
	 * @return список дисциплин (для каждой дисциплины указаны id и
	 *         наименование)
	 * @see Discipline
	 */
	public List<Discipline> parseDisciplines(HttpServletRequest request) {
		Enumeration<String> paramNames = request.getParameterNames();
		List<Discipline> list = new ArrayList<Discipline>();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			if (paramName.startsWith(D_PARAM)) {
				int dId = 0;
				try {
					dId = Integer.parseInt(paramName.substring(D_PARAM.length()));
				} catch (NumberFormatException ex) {
					dId = 0;
				}
				String dTitle = request.getParameter(paramName);
				Discipline d = constructDiscipline(dId, dTitle, null);
				list.add(d);
			}
		}
		return list;
	}

	/**
	 * Преобразование флага "admin" из строки (параметр <code>forAdminStr</code>
	 * ) в тип данных boolean
	 * <p>
	 * Флаг "admin" предназначен для идентификации направления запроса: является
	 * ли запрашиваемый ресурс частью управления приложением или предназначен
	 * для просмотра информации
	 * 
	 * @param forAdminStr
	 *            флаг "admin" в формате строки
	 * @return true - если запрашиваемый ресурс предназначен для администратора
	 *         приложения, false - в противном случае или
	 *         <code>forAdminStr</code> имеет неверный формат
	 */
	public boolean parseForAdmin(String forAdminStr) {
		try {
			return Boolean.parseBoolean(forAdminStr);
		} catch (NumberFormatException ex) {
			LOGGER.error("COMMAND HELPER : invalid for-admin parameter format", ex);
			return false;
		}
	}

	/**
	 * Конструирование объекта дисциплины
	 * 
	 * @param id
	 *            id дисциплины
	 * @param title
	 *            наименование дисциплины
	 * @param faculties
	 *            список факульетов, привязанных к дисциплине
	 * @return объект класса {@link Discipline}
	 * @see Discipline
	 */
	public Discipline constructDiscipline(int id, String title, List<Faculty> faculties) {
		Discipline d = new Discipline();
		d.setId(id);
		d.setTitle(title);
		d.setFaculties(faculties);
		return d;
	}

	/**
	 * Конструирование объекта абитуриента
	 * 
	 * @param id
	 *            id абитуриента
	 * @param login
	 *            логин абитуриента
	 * @param password
	 *            пароль абитуриента
	 * @param name
	 *            ФИО абитуриента
	 * @param email
	 *            электронный адрес абитуриента
	 * @param phone
	 *            телефон абитуриента
	 * @param address
	 *            адрес абитуриента
	 * @param bDate
	 *            дата рождения абитуриента
	 * @param school
	 *            школа/иное учебное заведение абитуриента
	 * @param year
	 *            год окончания учебного заведения
	 * @param record
	 *            запись в ведомости, связанная с абитуриентом
	 * @return объект класса {@link Applicant}
	 * @see Applicant
	 */
	public Applicant constructApplicant(int id, String login, String password, String name, String email, String phone,
			String address, Date bDate, String school, int year, RegisterRecord record) {
		Applicant a = new Applicant();
		a.setId(id);
		a.setLogin(login);
		a.setPassword(password);
		a.setName(name);
		a.setEmail(email);
		a.setPhone(phone);
		a.setAddress(address);
		a.setBirthdate(bDate);
		a.setSchool(school);
		a.setGradYear(year);
		a.setRecord(record);
		return a;
	}

	/**
	 * Конструирование объекта факультета
	 * 
	 * @param id
	 *            id факультета
	 * @param title
	 *            наименование факультета
	 * @param description
	 *            описание факультета
	 * @param logoname
	 *            имя файла с логотипом факультета
	 * @param phone
	 *            телефон факультета
	 * @param address
	 *            адрес факультета
	 * @param dean
	 *            ФИО декана факультета
	 * @param plan
	 *            план набора факультета
	 * @param disciplines
	 *            список дисциплин, связанных с факультетом
	 * @return объект класса {@link Faculty}
	 * @see Faculty
	 */
	public Faculty constructFaculty(int id, String title, String description, String logoname, String phone,
			String address, String dean, int plan, List<Discipline> disciplines) {
		Faculty f = new Faculty();
		f.setId(id);
		f.setTitle(title);
		f.setDescription(description);
		f.setLogoname(logoname);
		f.setPhone(phone);
		f.setAddress(address);
		f.setDean(dean);
		f.setPlan(plan);
		f.setDisciplines(disciplines);
		return f;
	}
	
	/**
	 * Конструирование объекта набора
	 * 
	 * @param id
	 *            id набора
	 * @param bDate
	 *            дата начала набора
	 * @param eDate
	 *            дата окончания набора
	 * @param active
	 *            true - если набор активен, false - в противном случае
	 * @return объект класса {@link Enroll}
	 * @see Enroll
	 */
	public Enroll constructEnroll(int id, Date bDate, Date eDate, boolean active) {
		Enroll e = new Enroll();
		e.setId(id);
		e.setBeginDate(bDate);
		e.setEndDate(eDate);
		e.setStatus(active);
		return e;
	}

	/**
	 * Преобразование ключа запроса в формате строки (параметр
	 * <code>keyStr</code>) в тип данных int
	 * 
	 * @param keyStr
	 *            ключ запроса в формате строки
	 * @return ключ запроса в формате типа данных int или -1, если
	 *         <code>keyStr</code> имеет неверный формат или равен
	 *         <code>null</code>
	 */
	private int parseKey(String keyStr) {
		if (keyStr != null && !keyStr.isEmpty()) {
			try {
				return Integer.parseInt(keyStr);
			} catch (NumberFormatException ex) {
				return -1;
			}
		} else {
			return -1;
		}
	}
	
	/**
	 * Конструирует объект записи ведомости
	 * 
	 * @param a
	 *            объект абитуриента
	 * @param f
	 *            объект факультета
	 * @param scores
	 *            карта, где ключ - дисциплина, значение - балл по
	 *            соответствующей дисциплине
	 * @param certScore
	 *            балл аттестата
	 * @param totalScore
	 *            суммарный балл
	 * @param e
	 *            объект текущего набор
	 * @return объект записи ведомости
	 */
	public RegisterRecord constructRecord(Applicant a, Faculty f, Map<Discipline, Integer> scores, int certScore,
			int totalScore, Enroll e) {
		RegisterRecord r = new RegisterRecord();
		r.setApplicant(a);
		r.setFaculty(f);
		r.setScores(scores);
		r.setCertificateScore(certScore);
		r.setTotalScore(totalScore);
		r.setEnroll(e);
		return r;
	}

	/**
	 * Извлечение баллов абитуриента по дисциплинам.
	 * <p>
	 * 
	 * @param request
	 *            контекст запроса для доступа к параметрам запроса
	 * @return карта, где ключом является дисциплина (с указанием id и
	 *         наименования), а значением - балл.
	 */
	public Map<Discipline, Integer> parseScores(HttpServletRequest request) {
		Enumeration<String> paramNames = request.getParameterNames();
		Map<Discipline, Integer> dMap = new HashMap<Discipline, Integer>();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			if (paramName.startsWith(D_PARAM_SCORES)) {
				int dId = Integer.parseInt(paramName.substring(D_PARAM_SCORES.length(), paramName.indexOf(DELIMETER)));
				String dTitle = paramName.substring(paramName.indexOf(DELIMETER) + 1);
				Discipline d = constructDiscipline(dId, dTitle, null);
				int score = Integer.parseInt(request.getParameter(paramName));
				dMap.put(d, score);
			}
		}
		return dMap;
	}

	/**
	 * Данный метод предотвращает повторную обработку запроса с одним и тем же
	 * ключом.
	 * <p>
	 * Данный метод сравнивает ключ текущего запроса с ключом запроса,
	 * выполняющегося в данный момент. Если ключи не совпадают, ключ текущего
	 * запроса устанавливается в качестве атрибута в контекст приложения. Так
	 * как метод предназначен для работы в многопоточном приложении, он помечен
	 * модификатором <code>synchronized</code>
	 * 
	 * @param keyName
	 *            строковое представление ключа текущего запроса
	 * @param request
	 *            контекст запроса клиента (используется для доступа к контексту
	 *            приложения)
	 * @return true - если текущий запрос должен быть выполнен; false - если
	 *         текущий запрос должен быть блокирован
	 */
	public synchronized boolean isAdditionEnabled(String keyName, HttpServletRequest request) {
		int key = parseKey(request.getParameter(CommandHelper.ParameterName.KEY));
		Object keyObj = request.getServletContext().getAttribute(keyName);
		int dKey = 0;
		if (keyObj != null && keyObj instanceof Integer) {
			dKey = (Integer) keyObj;
		}
		if (dKey != key) {
			LOGGER.debug("COMMAND HELPER : addition is enabled");
			request.getServletContext().setAttribute(keyName, key);
			return true;
		} else {
			LOGGER.debug("COMMAND HELPER : addition is not enabled");
			return false;
		}
	}

	/**
	 * Статический вложенный класс <code>AttributeName</code> содержит строковые
	 * константы, представляющие собой названия атрибутов, которые могут быть
	 * установлены/извлечены в качестве атрибутов в контекст
	 * запроса/сессии/приложения. Названия данных атрибутов часто используются
	 * разными командами.
	 * 
	 * @author Daria Krupenko
	 *
	 */
	public static class AttributeName {

		public static final String ADMIN = "admin";
		public static final String APPLICANT_TO_REQUEST = "appl";
		public static final String ERROR = "error";
		public static final String ELEMENTS_PER_PAGE = "elementsPerPage";
		public static final String LIST = "list";
		public static final String PAGE = "requiredPage";
		public static final String PAGES_NUMBER = "pagesNumber";
		public static final String NOT_VALID = "validationFailed";
		public static final String SUCCESS_ADDED = "successAdded";
		public static final String DISCIPLINE = "discipline";
		public static final String DELETE_UNABLE = "deleteUnable";
		public static final String SUCCESS_DELETED = "successDeleted";
		public static final String SUCCESS_UPDATED = "successUpdated";
		public static final String NO_CURRENT_ENROLL = "noCurrentEnroll";
		public static final String SUCCESS_COMPLETED = "successCompleted";
		public static final String IS_CURRENT_ENROLL = "isCurrentEnroll";
		public static final String SUCCESS_STARTED = "successStarted";
		public static final String ENROLL = "enroll";
		public static final String FACULTY = "faculty";
		public static final String D_LIST = "dList";
		public static final String STATUS = "admitted";
		public static final String LOGIN_FAILED = "loginFailed";
		public static final String APPLICANT = "applicant";
		public static final String NOT_REGISTERED = "notRegistered";
		public static final String SUCCESS_REGISTRATED = "successRegistrated";
		public static final String SCORES = "scores";
		public static final String CERTIFICATE = "certificate";
		public static final String SUCCESS_REGISTERED = "successRegistered";
		public static final String LOCALE = "locale";
		public static final String REGISTERED = "registered";
		public static final String UNREGISTRATED = "unregistrated";
		public static final String BEGIN_DATE = "beginDate";
		public static final String ALREADY_EXISTS = "alreadyExists";
		public static final String DISCIPLINE_KEY = "disciplineKey";
		public static final String ENROLL_KEY = "enrollKey";
		public static final String FACULTY_KEY = "facultyKey";
		public static final String RECORD_KEY = "recordKey";
		public static final String APPLICANT_KEY = "applicantKey";
		public static final String NOT_FOUND = "notFound";

	}

	/**
	 * Статический вложенный класс <code>ParameterName</code> содержит строковые
	 * константы, представляющие собой названия параметров, которые могут быть
	 * извлечены из контекста HTTP-запроса. Названия данных параметров часто
	 * используются разными командами.
	 * 
	 * @author Daria Krupenko
	 *
	 */
	public static class ParameterName {

		public static final String APPLICANT_ID = "applicant-id";
		public static final String CURRENT_PAGE = "current-page";
		public static final String DIRECTION = "next";
		public static final String TITLE = "title";
		public static final String DISCIPLINE_ID = "discipline-id";
		public static final String ENROLL_ID = "enroll-id";
		public static final String BEGIN_DATE = "begin-date";
		public static final String END_DATE = "end-date";
		public static final String DESCRIPTION = "description";
		public static final String LOGONAME = "logoname";
		public static final String PHONE = "phone";
		public static final String DEAN = "dean";
		public static final String ADDRESS = "address";
		public static final String PLAN = "plan";
		public static final String FACULTY_ID = "faculty-id";
		public static final String STATUS = "admitted";
		public static final String LOGIN = "login";
		public static final String PASSWORD = "password";
		public static final String NAME = "name";
		public static final String EMAIL = "email";
		public static final String BIRTHDATE = "birthdate";
		public static final String SCHOOL = "school";
		public static final String GRAD_YEAR = "gradyear";
		public static final String CERTIFICATE = "certificate-score";
		public static final String LANG = "lang";
		public static final String ELEMENTS_PER_PAGE = "elements-per-page";
		public static final String FOR_ADMIN = "for-admin";
		public static final String KEY = "key";

	}

	/**
	 * Статический вложенный класс <code>PageName</code> содержит строковые
	 * константы, представляющие собой относительные пути к JSP-страницам
	 * приложения.
	 * 
	 * @author Daria Krupenko
	 *
	 */
	public static class PageName {

		public static final String INDEX = "/index.jsp";
		public static final String ADMIN_LOGIN = "/admin_login.jsp";
		public static final String APPLICANT_LOGIN = "/applicant_login.jsp";
		public static final String APPLICANT_REGISTRATION = "/applicant_registration.jsp";
		public static final String FACULTIES_LIST = "/faculties_list.jsp";
		public static final String SETTINGS = "/settings.jsp";
		public static final String APPLICANT = "/WEB-INF/admin/applicants/applicant.jsp";
		public static final String APPLICANTS_LIST = "/WEB-INF/admin/applicants/applicants_list.jsp";
		public static final String ADD_DISCIPLINE = "/WEB-INF/admin/disciplines/add_discipline.jsp";
		public static final String DISCIPLINES_LIST = "/WEB-INF/admin/disciplines/disciplines_list.jsp";
		public static final String EDIT_DISCIPLINE = "/WEB-INF/admin/disciplines/edit_discipline.jsp";
		public static final String ENROLLS_LIST = "/WEB-INF/admin/enrollment/enrolls_list.jsp";
		public static final String START_ENROLL = "/WEB-INF/admin/enrollment/start_enroll.jsp";
		public static final String ADD_FACULTY = "/WEB-INF/admin/faculties/add_faculty.jsp";
		public static final String APPLICANTS_RATE = "/WEB-INF/admin/faculties/applicants_rate.jsp";
		public static final String EDIT_FACULTY = "/WEB-INF/admin/faculties/edit_faculty.jsp";
		public static final String FACULTIES_LIST_ADMIN = "/WEB-INF/admin/faculties/faculties_list_admin.jsp";
		public static final String GENERAL_STATISTICS = "/WEB-INF/admin/register/general_statistics.jsp";
		public static final String REGISTER = "/WEB-INF/admin/register/register.jsp";
		public static final String CONSOLE = "/WEB-INF/admin/console.jsp";
		public static final String ACCOUNT = "/WEB-INF/applicant/account.jsp";
		public static final String FACULTY = "/WEB-INF/applicant/faculty.jsp";
		public static final String REGISTRY_FOR_FACULTY = "/WEB-INF/applicant/registry_for_faculty.jsp";
		public static final String STATUS = "/WEB-INF/applicant/status.jsp";
		public static final String DEFAULT_ERROR = "/WEB-INF/error/default.jsp";
	}

}
