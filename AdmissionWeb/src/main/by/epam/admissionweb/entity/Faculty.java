package main.by.epam.admissionweb.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Класс <code>Faculty</code> представляет собой объект-сущность модели данных
 * приложения и инкапсулирует состояние факультета учебного заведения.
 * <p>
 * Кроме общей информации, факультет содержит количество записанных абитуриентов
 * и список дисциплин, которые необходимо сдать для поступления на данный
 * факультет.
 * <p>
 * Объекты класса <code>Faculty</code> могут быть сериализованы
 * 
 * @author Daria Krupenko
 * @see Serializable
 *
 */
public class Faculty implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Уникальный идентификатор факультета
	 */
	private int id;

	/**
	 * Наименование факультета
	 */
	private String title;

	/**
	 * Описание факультета
	 */
	private String description;

	/**
	 * Телефон деканата факультета
	 */
	private String phone;

	/**
	 * Адрес деканата факультета
	 */
	private String address;

	/**
	 * ФИО декана факультета
	 */
	private String dean;

	/**
	 * Имя файла с логотипом факультета
	 */
	private String logoname;

	/**
	 * План набора на факультет
	 */
	private int plan;

	/**
	 * Проходной балл факультета
	 */
	private int passRate;

	/**
	 * Список дисциплин, ассоциированных с факультетом
	 */
	private List<Discipline> disciplines;

	/**
	 * Количество записанных абитуриентов
	 */
	private int applicantsCount;

	/**
	 * Конструирует объект факультета со значением полей по умолчанию
	 */
	public Faculty() {
	}

	/**
	 * Получение уникального идентификатора факультета
	 * 
	 * @return уникальный идентификатор факультета
	 */
	public int getId() {
		return id;
	}

	/**
	 * Установка уникального идентификатора факультета
	 * 
	 * @param id
	 *            уникальный идентификатор факультета
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Получение наименования факультета
	 * 
	 * @return наименование факультета
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Установка наименования факультета
	 * 
	 * @param title
	 *            наименование факультета
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Получение описания факультета
	 * 
	 * @return описание факультета
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Установка описания факультета
	 * 
	 * @param description
	 *            описание факультета
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Получение телефона деканата факультета
	 * 
	 * @return телефон деканата факультета
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Установка телефона деканата факультета
	 * 
	 * @param phone
	 *            телефон деканата факультета
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * Получение адреса деканата факультета
	 * 
	 * @return адрес деканата факультета
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Установка адреса деканата факультета
	 * 
	 * @param address
	 *            адрес деканата факультета
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Получение ФИО декана факультета
	 * 
	 * @return ФИО декана факультета
	 */
	public String getDean() {
		return dean;
	}

	/**
	 * Установка ФИО декана факультета
	 * 
	 * @param dean
	 *            ФИО декана факультета
	 */
	public void setDean(String dean) {
		this.dean = dean;
	}

	/**
	 * Получение имени файла с логотипом факультета
	 * 
	 * @return имя файла с логотипом факультета
	 */
	public String getLogoname() {
		return logoname;
	}

	/**
	 * Установка имени файла с логотипом факультета
	 * 
	 * @param logoname
	 *            имя файла с логотипом факультета
	 */
	public void setLogoname(String logoname) {
		this.logoname = logoname;
	}

	/**
	 * Получение плана набора на факультет
	 * 
	 * @return план набора на факультет
	 */
	public int getPlan() {
		return plan;
	}

	/**
	 * Установка плана набора на факультет
	 * 
	 * @param plan
	 *            план набора на факультет
	 */
	public void setPlan(int plan) {
		this.plan = plan;
	}

	/**
	 * Получение проходного балла факультета
	 * 
	 * @return проходной балл факультета
	 */
	public int getPassRate() {
		return passRate;
	}

	/**
	 * Установка проходного балла факультета
	 * 
	 * @param passRate
	 *            проходной балл факультета
	 */
	public void setPassRate(int passRate) {
		this.passRate = passRate;
	}

	/**
	 * Получение списка дисциплин, ассоциированных с факультетом
	 * 
	 * @return список дисциплин, ассоциированных с факультетом
	 */
	public List<Discipline> getDisciplines() {
		return disciplines;
	}

	/**
	 * Установка списка дисциплин, ассоциированных с факультетом
	 * 
	 * @param disciplines
	 *            список дисциплин, ассоциированных с факультетом
	 */
	public void setDisciplines(List<Discipline> disciplines) {
		this.disciplines = disciplines;
	}

	/**
	 * Получение количества записанных абитуриентов
	 * 
	 * @return количество записанных абитуриентов
	 */
	public int getApplicantsCount() {
		return applicantsCount;
	}

	/**
	 * Установка количества записанных абитуриентов
	 * 
	 * @param applicantsCount
	 *            количество записанных абитуриентов
	 */
	public void setApplicantsCount(int applicantsCount) {
		this.applicantsCount = applicantsCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + applicantsCount;
		result = prime * result + ((dean == null) ? 0 : dean.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + id;
		result = prime * result + ((logoname == null) ? 0 : logoname.hashCode());
		result = prime * result + passRate;
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + plan;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		Faculty other = (Faculty) obj;
		if (address == null) {
			if (other.address != null) {
				return false;
			}
		} else if (!address.equals(other.address)) {
			return false;
		}
		if (applicantsCount != other.applicantsCount) {
			return false;
		}
		if (dean == null) {
			if (other.dean != null) {
				return false;
			}
		} else if (!dean.equals(other.dean)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (logoname == null) {
			if (other.logoname != null) {
				return false;
			}
		} else if (!logoname.equals(other.logoname)) {
			return false;
		}
		if (passRate != other.passRate) {
			return false;
		}
		if (phone == null) {
			if (other.phone != null) {
				return false;
			}
		} else if (!phone.equals(other.phone)) {
			return false;
		}
		if (plan != other.plan) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id=" + id + ", title=" + title + ", description=" + description
				+ ", phone=" + phone + ", address=" + address + ", dean=" + dean + ", logoname=" + logoname + ", plan="
				+ plan + ", passRate=" + passRate + ", applicantsCount=" + applicantsCount + "]";
	}

}
