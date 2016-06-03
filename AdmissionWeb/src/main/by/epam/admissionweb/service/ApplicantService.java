package main.by.epam.admissionweb.service;

import java.util.List;

import main.by.epam.admissionweb.entity.Applicant;
import main.by.epam.admissionweb.service.exception.ServiceException;

/**
 * Интерфейс <code>ApplicantService</code> предоставляет основные методы для
 * обработки данных об абитуриентах учебного заведения.
 * <p>
 * Реализации интерфейса <code>ApplicantService</code> инкапсулируют логику
 * приложения по работе с данными об абитуриентах.
 * <p>
 * Все методы интерфейса <code>ApplicantService</code> могут выбросить
 * исключение {@link ServiceException}, которое сигнализирует об ошибке
 * обработки данных либо служит классом-оберткой для исключений чтения/записи
 * данных из источника.
 * 
 * @author Daria Krupenko
 * @see Applicant
 * @see ServiceException
 *
 */
public interface ApplicantService {

	/**
	 * Получение абитуриента по его id.
	 * 
	 * @param id
	 *            id абитуриента
	 * @return объект абитуриента
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public Applicant getApplicant(int id) throws ServiceException;

	/**
	 * Получение количества абитуриентов
	 * 
	 * @return количество абитуриентов
	 * 
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public int getApplicantsNumber() throws ServiceException;

	/**
	 * Получение списка абитуриентов. Количество элементов списка может быть
	 * ограничено при указании номера страницы <code>requiredPage</code> и
	 * количества элементов на странице <code>elementsCount</code>.
	 * 
	 * @param requiredPage
	 *            номер требуемой страницы
	 * @param elementsCount
	 *            количество элементов на одной странице
	 * @return список абитуриентов, привязанный к номеру страницы
	 *         <code>requiredPage</code>
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public List<Applicant> getApplicantsList(int requiredPage, int elementsCount) throws ServiceException;

	/**
	 * Регистрация абитуриента в системе.
	 * 
	 * @param a
	 *            объект абитуриента
	 * @return объект абитуриента, зарегистрированного в системе
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public Applicant registrateApplicant(Applicant a) throws ServiceException;

	/**
	 * Авторизация абитуриента с указанным логином и паролем
	 * 
	 * @param login
	 *            логин абитуриента
	 * @param password
	 *            пароль абитуриента
	 * @return авторизованный абитуриент
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public Applicant loginApplicant(String login, String password) throws ServiceException;

	/**
	 * Обновление данных абитуриента.
	 * 
	 * @param a
	 *            объект абитуриента с обновляемыми данными
	 * @return объект абитуриента с обновленными данными
	 * @throws ServiceException
	 *             при обработке данных произошла ошибка
	 */
	public Applicant updateApplicant(Applicant a) throws ServiceException;

}
