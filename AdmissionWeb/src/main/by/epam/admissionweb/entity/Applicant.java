package main.by.epam.admissionweb.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Класс <code>Applicant</code> представляет собой объект-сущность модели данных
 * приложения и инкапсулирует состояние абитуриента учебного заведения.
 * <p>
 * Абитуриент обладает всеми необходимыми атрибутами, составляющими его личные
 * данные. Вся информация о записи абитуриента на факультет (баллы по
 * дисциплинами, балл аттестата, суммарный балл и т.д.) инкапсулирована в
 * объекте {@link RegisterRecord}, который в свою очередь, представляет собой
 * запись в ведомости.
 * <p>
 * Объекты класса <code>Applicant</code> могут быть сериализованы
 * 
 * @author Daria Krupenko
 * @see Serializable
 *
 */
public class Applicant implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Уникальный идентификатор абитуриента
	 */
	private int id;

	/**
	 * Логин абитуриента
	 */
	private String login;

	/**
	 * Пароль абитуриента
	 */
	private String password;

	/**
	 * ФИО абитуриента
	 */
	private String name;

	/**
	 * Электронный адрес абитуриента
	 */
	private String email;

	/**
	 * Дата рождения абитуриента
	 */
	private Date birthdate;

	/**
	 * Контактный телефон абитуриента
	 */
	private String phone;

	/**
	 * Адрес абитуриента
	 */
	private String address;

	/**
	 * Учебное заведение, которое закончил абитуриент
	 */
	private String school;

	/**
	 * Год окончания учебного заведения
	 */
	private int gradYear;

	/**
	 * Запись в ведомости, ассоциированная с данным абитуриентом
	 */
	private RegisterRecord record;

	/**
	 * Конструирует объект абитуриента со значением полей по умолчанию
	 */
	public Applicant() {
	}

	/**
	 * Получение уникального идентификатора абитуриента
	 * 
	 * @return уникальный идентификатор абитуриента
	 */
	public int getId() {
		return id;
	}

	/**
	 * Установка уникального идентификатора абитуриента
	 * 
	 * @param id
	 *            уникальный идентификатор абитуриента
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Получение логина абитуриента
	 * 
	 * @return логин абитуриента
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Установка логина абитуриента
	 * 
	 * @param login
	 *            логин абитуриента
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * Получение пароля абитуриента
	 * 
	 * @return пароль абитуриента
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Установка пароля абитуриента
	 * 
	 * @param password
	 *            пароль абитуриента
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Получение ФИО абитуриента
	 * 
	 * @return ФИО абитуриента
	 */
	public String getName() {
		return name;
	}

	/**
	 * Установка ФИО абитуриента
	 * 
	 * @param name
	 *            ФИО абитуриента
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Получение электронного адреса абитуриента
	 * 
	 * @return электронный адрес абитуриента
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Установка электронного адреса абитуриента
	 * 
	 * @param email
	 *            электронный адрес абитуриента
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Получение даты рождения абитуриента
	 * 
	 * @return дата рождения абитуриента
	 */
	public Date getBirthdate() {
		return birthdate;
	}

	/**
	 * Установка даты рождения абитуриента
	 * 
	 * @param birthdate
	 *            дата рождения абитуриента
	 */
	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	/**
	 * Получение контактного телефона абитуриента
	 * 
	 * @return контактный телефон абитуриента
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Установка контактного телефона абитуриента
	 * 
	 * @param phone
	 *            контактный телефон абитуриента
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * Получение адреса абитуриента
	 * 
	 * @return адрес абитуриента
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Установка адреса абитуриента
	 * 
	 * @param address
	 *            адрес абитуриента
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Получение учебного заведения, которое заканчивал абитуриент
	 * 
	 * @return учебное заведение, которое заканчивал абитуриент
	 */
	public String getSchool() {
		return school;
	}

	/**
	 * Установка учебного заведения, которое заканчивал абитуриент
	 * 
	 * @param school
	 *            учебное заведение, которое заканчивал абитуриент
	 */
	public void setSchool(String school) {
		this.school = school;
	}

	/**
	 * Получение года окончания учебного заведения
	 * 
	 * @return год окончания учебного заведения
	 */
	public int getGradYear() {
		return gradYear;
	}

	/**
	 * Установка года окончания учебного заведения
	 * 
	 * @param gradYear
	 *            год окончания учебного заведения
	 */
	public void setGradYear(int gradYear) {
		this.gradYear = gradYear;
	}

	/**
	 * Получение записи в ведомости, асоциированной с абитуриентом
	 * 
	 * @return запись в ведомости, асоциированная с абитуриентом
	 */
	public RegisterRecord getRecord() {
		return record;
	}

	/**
	 * Установка записи в ведомости, асоциированной с абитуриентом
	 * 
	 * @param record
	 *            запись в ведомости, асоциированная с абитуриентом
	 */
	public void setRecord(RegisterRecord record) {
		this.record = record;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((birthdate == null) ? 0 : birthdate.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + gradYear;
		result = prime * result + id;
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((school == null) ? 0 : school.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Applicant other = (Applicant) obj;
		if (address == null) {
			if (other.address != null) {
				return false;
			}
		} else if (!address.equals(other.address)) {
			return false;
		}
		if (birthdate == null) {
			if (other.birthdate != null) {
				return false;
			}
		} else if (!birthdate.equals(other.birthdate)) {
			return false;
		}
		if (email == null) {
			if (other.email != null) {
				return false;
			}
		} else if (!email.equals(other.email)) {
			return false;
		}
		if (gradYear != other.gradYear) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (login == null) {
			if (other.login != null) {
				return false;
			}
		} else if (!login.equals(other.login)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (password == null) {
			if (other.password != null) {
				return false;
			}
		} else if (!password.equals(other.password)) {
			return false;
		}
		if (phone == null) {
			if (other.phone != null) {
				return false;
			}
		} else if (!phone.equals(other.phone)) {
			return false;
		}
		if (school == null) {
			if (other.school != null) {
				return false;
			}
		} else if (!school.equals(other.school)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id=" + id + ", login=" + login + ", password=" + password + ", name="
				+ name + ", email=" + email + ", birthdate=" + birthdate + ", phone=" + phone + ", address=" + address
				+ ", school=" + school + ", gradYear=" + gradYear + "]";
	}

}
