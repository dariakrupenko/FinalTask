package main.by.epam.admissionweb.service.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.by.epam.admissionweb.entity.Applicant;
import main.by.epam.admissionweb.entity.Discipline;
import main.by.epam.admissionweb.entity.Enroll;
import main.by.epam.admissionweb.entity.Faculty;
import main.by.epam.admissionweb.entity.RegisterRecord;

/**
 * Класс <code>ValidatorService</code> представляет собой сервис-объект по
 * валидации данных.
 * <p>
 * Все ограничения по валидации данных сохраненены в виде констант во вложенном
 * статическом классе {@link Constraints}.
 * <p>
 * Данный класс виден только в рамках пакета с целью использования только
 * сервис-объектами
 * 
 * @author Daria Krupenko
 *
 */
class ValidatorService {

	/**
	 * Логгер
	 */
	private static final Logger LOGGER = LogManager.getRootLogger();

	/**
	 * Объект класса <code>ValidatorService</code>, создается один раз при
	 * загрузке класса в память
	 */
	private static final ValidatorService INSTANCE = new ValidatorService();

	private ValidatorService() {
	}

	/**
	 * Получение объекта класса <code>ValidatorService</code>
	 * 
	 * @return объект класса <code>ValidatorService</code>
	 */
	protected static ValidatorService getInstance() {
		return INSTANCE;
	}

	/**
	 * Валидация дисциплины
	 * 
	 * @param d
	 *            объект дисциплины
	 * @return false - если хотя бы один атрибут не прошел валидацию, true - в
	 *         противном случае
	 */
	protected boolean validateDiscipline(Discipline d) {
		LOGGER.debug("SERVICE : ValidatorService.validateDiscipline()");
		if (d == null) {
			LOGGER.debug("SERVICE : ValidatorService.validateDiscipline (failed : null)");
			return false;
		}
		boolean isTitleValid = validateString(d.getTitle(), Constraints.DISCIPLINE_TITLE_MIN_LENGTH,
				Constraints.DISCIPLINE_TITLE_MAX_LENGTH);
		LOGGER.debug("SERVICE : ValidatorService.validateDiscipline ({})", isTitleValid);
		return isTitleValid;
	}

	/**
	 * Валидация набора
	 * 
	 * @param d
	 *            объект набора
	 * @return false - если хотя бы один атрибут не прошел валидацию, true - в
	 *         противном случае
	 */
	protected boolean validateEnroll(Enroll e) {
		LOGGER.debug("SERVICE : ValidatorService.validateEnroll()");
		if (e == null) {
			LOGGER.debug("SERVICE : ValidatorService.validateEnroll (failed : null)");
			return false;
		}
		boolean isEndDateValid = validateDate(e.getEndDate(), e.getBeginDate(), null);
		LOGGER.debug("SERVICE : ValidatorService.validateEnroll ({})", isEndDateValid);
		return isEndDateValid;
	}

	/**
	 * Валидация факультета
	 * 
	 * @param d
	 *            объект факультета
	 * @return false - если хотя бы один атрибут не прошел валидацию, true - в
	 *         противном случае
	 */
	protected boolean validateFaculty(Faculty f) {
		LOGGER.debug("SERVICE : ValidatorService.validateFaculty()");
		if (f == null) {
			LOGGER.debug("SERVICE : ValidatorService.validateFaculty (failed : null)");
			return false;
		}
		if (!validateString(f.getTitle(), Constraints.FACULTY_TITLE_MIN_LENGTH, Constraints.FACULTY_TITLE_MAX_LENGTH)) {
			LOGGER.debug("SERVICE : ValidatorService (invalid title)");
			return false;
		}
		if (!validateString(f.getLogoname(), Constraints.FACULTY_LOGONAME_MIN_LENGTH,
				Constraints.FACULTY_LOGONAME_MAX_LENGTH)) {
			LOGGER.debug("SERVICE : ValidatorService (invalid logoname)");
			return false;
		}
		if (!validateString(f.getAddress(), Constraints.FACULTY_ADDRESS_MIN_LENGTH,
				Constraints.FACULTY_ADDRESS_MAX_LENGTH)) {
			LOGGER.debug("SERVICE : ValidatorService (invalid address)");
			return false;
		}
		if (!validateString(f.getDean(), Constraints.FACULTY_DEAN_MIN_LENGTH, Constraints.FACULTY_DEAN_MAX_LENGTH)) {
			LOGGER.debug("SERVICE : ValidatorService (invalid dean)");
			return false;
		}
		if (!validatePhoneNumber(f.getPhone(), Constraints.FACULTY_PHONE_LENGTH)) {
			LOGGER.debug("SERVICE : ValidatorService (invalid phone)");
			return false;
		}
		if (!validateCollection(f.getDisciplines(), Constraints.FACULTY_DISCIPLINES_MIN,
				Constraints.FACULTY_DISCIPLINES_MAX)) {
			LOGGER.debug("SERVICE : ValidatorService (invalid disciplines list)");
			return false;
		}
		if (!validateNumber(f.getPlan(), Constraints.FACULTY_PLAN_MIN, Constraints.FACULTY_PLAN_MAX)) {
			LOGGER.debug("SERVICE : ValidatorService (invalid plan)");
			return false;
		}
		LOGGER.debug("SERVICE : ValidatorService.validateFaculty (true)");
		return true;
	}

	/**
	 * Валидация абитуриента
	 * 
	 * @param d
	 *            объект абитуриента
	 * @return false - если хотя бы один атрибут не прошел валидацию, true - в
	 *         противном случае
	 */
	protected boolean validateApplicant(Applicant a) {
		LOGGER.debug("SERVICE : ValidatorService.validateApplicant()");
		if (a == null) {
			LOGGER.debug("SERVICE : ValidatorService.validateApplicant (failed : null)");
			return false;
		}
		if (!validateLoginPassword(a.getLogin(), Constraints.APPLICANT_LOGIN_MIN_LENGTH,
				Constraints.APPLICANT_LOGIN_MAX_LENGTH)) {
			LOGGER.debug("SERVICE : ValidatorService (invalid login)");
			return false;
		}
		if (!validateLoginPassword(a.getPassword(), Constraints.APPLICANT_PASSWORD_MIN_LENGTH,
				Constraints.APPLICANT_PASSWORD_MAX_LENGTH)) {
			LOGGER.debug("SERVICE : ValidatorService (invalid password)");
			return false;
		}
		if (!validateString(a.getName(), Constraints.APPLICANT_NAME_MIN_LENGTH,
				Constraints.APPLICANT_NAME_MAX_LENGTH)) {
			LOGGER.debug("SERVICE : ValidatorService (invalid name)");
			return false;
		}
		if (!validateEmail(a.getEmail(), Constraints.APPLICANT_EMAIL_MIN_LENGTH,
				Constraints.APPLICANT_EMAIL_MAX_LENGTH)) {
			LOGGER.debug("SERVICE : ValidatorService (invalid email)");
			return false;
		}
		if (!validatePhoneNumber(a.getPhone(), Constraints.APPLICANT_PHONE_LENGTH)) {
			LOGGER.debug("SERVICE : ValidatorService (invalid phone)");
			return false;
		}
		if (!validateString(a.getAddress(), Constraints.APPLICANT_ADDRESS_MIN_LENGTH,
				Constraints.APPLICANT_ADDRESS_MAX_LENGTH)) {
			LOGGER.debug("SERVICE : ValidatorService (invalid address)");
			return false;
		}
		if (!validateDate(a.getBirthdate(), null, new Date())) {
			LOGGER.debug("SERVICE : ValidatorService (invalid birthdate)");
			return false;
		}
		if (!validateString(a.getSchool(), Constraints.APPLICANT_SCHOOL_MIN_LENGTH,
				Constraints.APPLICANT_SCHOOL_MAX_LENGTH)) {
			LOGGER.debug("SERVICE : ValidatorService (invalid school)");
			return false;
		}
		if (!validateNumber(a.getGradYear(), Constraints.APPLICANT_YEAR_MIN,
				Calendar.getInstance().get(Calendar.YEAR))) {
			LOGGER.debug("SERVICE : ValidatorService (invalid graduation year)");
			return false;
		}
		LOGGER.debug("SERVICE : ValidatorService.validateApplicant (true)");
		return true;
	}

	/**
	 * Валидация записи ведомости
	 * 
	 * @param d
	 *            объект записи ведомости
	 * @return false - если хотя бы один атрибут не прошел валидацию, true - в
	 *         противном случае
	 */
	protected boolean validateRecord(RegisterRecord r) {
		LOGGER.debug("SERVICE : ValidatorService.validateRecord()");
		if (r == null) {
			LOGGER.debug("SERVICE : ValidatorService.validateRecord(failed : null)");
			return false;
		}
		if (r.getApplicant() == null) {
			LOGGER.debug("SERVICE : ValidatorService.validateRecord(failed : null applicant)");
			return false;
		}
		if (r.getFaculty() == null) {
			LOGGER.debug("SERVICE : ValidatorService.validateRecord(failed : null faculty)");
			return false;
		}
		if (r.getScores() == null) {
			LOGGER.debug("SERVICE : ValidatorService.validateRecord(failed : null scores)");
			return false;
		}
		for (int score : r.getScores().values()) {
			if (!validateNumber(score, Constraints.SCORE_MIN, Constraints.SCORE_MAX)) {
				LOGGER.debug("SERVICE : ValidatorService.validateRecord (invalid score)");
				return false;
			}
		}
		if (!validateNumber(r.getCertificateScore(), Constraints.SCORE_MIN, Constraints.SCORE_MAX)) {
			LOGGER.debug("SERVICE : ValidatorService.validateRecord (invalid certificate score)");
			return false;
		}
		LOGGER.debug("SERVICE : ValidatorService.validateRecord (true)");
		return true;
	}

	/**
	 * Валидация электронного адреса.
	 * <p>
	 * Электронный адрес не должен содержать пробелов и должен содержать символ
	 * '@'
	 * 
	 * @param email
	 *            электронный адрес
	 * @param minLength
	 *            минимально допустимя длина
	 * @param maxLength
	 *            макисмально допустимая длина
	 * @return true - электронный адрес прошел валидацию, иначе false
	 */
	private boolean validateEmail(String email, int minLength, int maxLength) {
		if (email == null || email.isEmpty()) {
			return false;
		}
		if (email.length() < minLength || email.length() > maxLength) {
			return false;
		}
		Pattern pattern = Pattern.compile(Constraints.EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	/**
	 * Валидация логина или пароля
	 * <p>
	 * Логин или пароль не должен содержать пробелы
	 * 
	 * @param loginpassword
	 *            логин или пароль
	 * @param minLength
	 *            минимально допустимя длина
	 * @param maxLength
	 *            макисмально допустимая длина
	 * @return true - логин или пароль прошел валидацию, иначе false
	 */
	private boolean validateLoginPassword(String loginpassword, int minLength, int maxLength) {
		if (loginpassword == null || loginpassword.isEmpty()) {
			return false;
		}
		if (loginpassword.contains(" ")) {
			return false;
		}
		if (loginpassword.length() < minLength || loginpassword.length() > maxLength) {
			return false;
		}
		return true;
	}

	/**
	 * Валидация строковых данных
	 * 
	 * @param str
	 *            строка
	 * @param minLength
	 *            минимально допустимя длина
	 * @param maxLength
	 *            макисмально допустимая длина
	 * @return true - строка прошла валидацию, иначе false
	 */
	private boolean validateString(String str, int minLength, int maxLength) {
		if (str == null || str.length() < minLength || str.length() > maxLength) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Валидация даты
	 * 
	 * @param date
	 *            дата
	 * @param minDate
	 *            минимально возможная дата
	 * @param maxDate
	 *            максимально возможная дата
	 * @return true - дата прошла валидацию, иначе false
	 */
	private boolean validateDate(Date date, Date minDate, Date maxDate) {
		if (date == null) {
			return false;
		}
		if (minDate != null && date.compareTo(minDate) < 0) {
			return false;
		}
		if (maxDate != null && date.compareTo(maxDate) > 0) {
			return false;
		}
		return true;
	}

	/**
	 * Валидация номера телефона
	 * <p>
	 * Строка номера телефона должна содержать только цифры
	 * 
	 * @param pNumber
	 *            номер телефона
	 * @param length
	 *            длина
	 * @return true - номер телефона прошел валидацию, иначе false
	 */
	private boolean validatePhoneNumber(String pNumber, int length) {
		if (pNumber == null) {
			return false;
		}
		if (pNumber.length() != length) {
			return false;
		}
		for (int i = 0; i < pNumber.length(); i++) {
			if (!Character.isDigit(pNumber.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Валидация коллекции объектов
	 * 
	 * @param collection
	 *            коллекция
	 * @param minSize
	 *            минимально допустимый размер коллекции
	 * @param maxSize
	 *            максимально допустимый размер коллекции
	 * @return true - коллекция прошла валидацию, иначе false
	 */
	private boolean validateCollection(Collection<?> collection, int minSize, int maxSize) {
		if (collection == null) {
			return false;
		}
		if (collection.size() < minSize) {
			return false;
		}
		if (collection.size() > maxSize) {
			return false;
		}
		return true;
	}

	/**
	 * Валидация числовых данных
	 * 
	 * @param number
	 *            число
	 * @param min
	 *            минимально возможное значение
	 * @param max
	 *            максимально возможное значение
	 * @return true - число прошло валидацию, иначе false
	 */
	private boolean validateNumber(int number, int min, int max) {
		if (number < min || number > max) {
			return false;
		}
		return true;
	}

	/**
	 * Класс <code>Constraints</code> содержит ограничения по валидации данных и
	 * доступен только из класса {@link ValidatorService}
	 * 
	 * @author Daria Krupenko
	 *
	 */
	private static class Constraints {
		private static final int DISCIPLINE_TITLE_MIN_LENGTH = 1;
		private static final int DISCIPLINE_TITLE_MAX_LENGTH = 30;

		private static final int FACULTY_TITLE_MIN_LENGTH = 1;
		private static final int FACULTY_TITLE_MAX_LENGTH = 65;
		private static final int FACULTY_LOGONAME_MIN_LENGTH = 1;
		private static final int FACULTY_LOGONAME_MAX_LENGTH = 10;
		private static final int FACULTY_PHONE_LENGTH = 12;
		private static final int FACULTY_ADDRESS_MIN_LENGTH = 1;
		private static final int FACULTY_ADDRESS_MAX_LENGTH = 55;
		private static final int FACULTY_DEAN_MIN_LENGTH = 1;
		private static final int FACULTY_DEAN_MAX_LENGTH = 40;
		private static final int FACULTY_PLAN_MIN = 1;
		private static final int FACULTY_PLAN_MAX = Integer.MAX_VALUE;
		private static final int FACULTY_DISCIPLINES_MIN = 1;
		private static final int FACULTY_DISCIPLINES_MAX = Integer.MAX_VALUE;

		private static final int APPLICANT_LOGIN_MIN_LENGTH = 1;
		private static final int APPLICANT_LOGIN_MAX_LENGTH = 40;
		private static final int APPLICANT_PASSWORD_MIN_LENGTH = 1;
		private static final int APPLICANT_PASSWORD_MAX_LENGTH = 40;
		private static final int APPLICANT_NAME_MIN_LENGTH = 1;
		private static final int APPLICANT_NAME_MAX_LENGTH = 40;
		private static final int APPLICANT_EMAIL_MIN_LENGTH = 1;
		private static final int APPLICANT_EMAIL_MAX_LENGTH = 30;
		private static final int APPLICANT_PHONE_LENGTH = 12;
		private static final int APPLICANT_ADDRESS_MIN_LENGTH = 1;
		private static final int APPLICANT_ADDRESS_MAX_LENGTH = 55;
		private static final int APPLICANT_SCHOOL_MIN_LENGTH = 1;
		private static final int APPLICANT_SCHOOL_MAX_LENGTH = 70;
		private static final int APPLICANT_YEAR_MIN = 1900;

		private static final int SCORE_MIN = 0;
		private static final int SCORE_MAX = 100;

		private static final String EMAIL_PATTERN = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
	}

}
