package main.by.epam.admissionweb.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Класс <code>Enroll</code> представляет собой объект-сущность модели данных
 * приложения и инкапсулирует состояние набора учебного заведения.
 * <p>
 * Набор в учебное заведение представляет собой период, в течени которого
 * абитуриенты могут записаться на интересующий их факультет или отменить
 * запись. Набор может находиться в одном из двух состояний: активный или
 * закрытый.
 * <p>
 * Объекты класса <code>Enroll</code> могут быть сериализованы
 * 
 * @author Daria Krupenko
 * @see Serializable
 *
 */
public class Enroll implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Уникальный идентификатор набора
	 */
	private int id;

	/**
	 * Дата начала набора
	 */
	private Date beginDate;

	/**
	 * Дата окончания набора
	 */
	private Date endDate;

	/**
	 * Состояние набора
	 */
	private boolean status;

	/**
	 * Конструирует объект набора со значением полей по умолчанию
	 */
	public Enroll() {
	}

	/**
	 * Получение уникального идентификатора набора
	 * 
	 * @return уникальный идентификатор набора
	 */
	public int getId() {
		return id;
	}

	/**
	 * Установка уникального идентификатора набора
	 * 
	 * @param id
	 *            уникальный идентификатор набора
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Получение даты начала набора
	 * 
	 * @return дата начала набора
	 */
	public Date getBeginDate() {
		return beginDate;
	}

	/**
	 * Установка даты начала набора
	 * 
	 * @param beginDate
	 *            дата начала набора
	 */
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	/**
	 * Получение даты окончания набора
	 * 
	 * @return дата окончания набора
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Установка даты окончания набора
	 * 
	 * @param endDate
	 *            дата окончания набора
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Получение состояния набора
	 * 
	 * @return состояние набора
	 */
	public boolean isStatus() {
		return status;
	}

	/**
	 * Установка состояния набора
	 * 
	 * @param status
	 *            состояние набора
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((beginDate == null) ? 0 : beginDate.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + id;
		result = prime * result + (status ? 1231 : 1237);
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
		Enroll other = (Enroll) obj;
		if (beginDate == null) {
			if (other.beginDate != null) {
				return false;
			}
		} else if (!beginDate.equals(other.beginDate)) {
			return false;
		}
		if (endDate == null) {
			if (other.endDate != null) {
				return false;
			}
		} else if (!endDate.equals(other.endDate)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (status != other.status) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id=" + id + ", beginDate=" + beginDate + ", endDate=" + endDate
				+ ", status=" + status + "]";
	}

}
