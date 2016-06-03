package main.by.epam.admissionweb.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Класс <code>Discipline</code> представляет собой объект-сущность модели
 * данных приложения и инкапсулирует состояние дисциплины учебного заведения.
 * <p>
 * Дисциплина представляет собой наименование предмета, который необходимо
 * сдавать для поступления на какой-либо факультет.
 * <p>
 * Также, каждая дисциплина включает в себя список факультетов, которые требуют
 * сдачи данной дисциплины для посутпления
 * <p>
 * Объекты класса <code>Discipline</code> могут быть сериализованы
 * <p>
 * Объекты класса <code>Discipline</code> могут сравниваться по наименованию
 * благодаря реализации интерфейса <code>Comparable</code>
 * 
 * @author Daria Krupenko
 * @see Serializable
 * @see Comparable
 *
 */
public class Discipline implements Serializable, Comparable<Discipline> {

	private static final long serialVersionUID = 1L;

	/**
	 * Уникальный идентификатор дисциплины
	 */
	private int id;
	
	/**
	 * Наименование дисциплины
	 */
	private String title;
	
	/**
	 * Список факультетов, связанных с дисциплиной
	 */
	private List<Faculty> faculties;

	/**
	 * Конструирует объект дисциплины со значением полей по умолчанию
	 */
	public Discipline() {
	}

	/**
	 * Получение уникального идентификатора дисциплины
	 * 
	 * @return уникальный идентификатор дисциплины
	 */
	public int getId() {
		return id;
	}

	/**
	 * Установка уникального идентификатора дисциплины
	 * 
	 * @param id уникальный идентификатор дисциплины
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Получение наименования дисциплины
	 * 
	 * @return наименование дисциплины
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Установка наименования дисциплины
	 * 
	 * @param title наименование дисциплины
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Получение списка факультетов, связанных с дисциплиной
	 * 
	 * @return список факультетов, связанных с дисциплиной
	 */
	public List<Faculty> getFaculties() {
		return faculties;
	}

	/**
	 * Установка списка факультетов, связанных с дисциплиной
	 * 
	 * @param faculties список факультетов, связанных с дисциплиной
	 */
	public void setFaculties(List<Faculty> faculties) {
		this.faculties = faculties;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((faculties == null) ? 0 : faculties.hashCode());
		result = prime * result + id;
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
		Discipline other = (Discipline) obj;
		if (faculties == null) {
			if (other.faculties != null) {
				return false;
			}
		} else if (!faculties.equals(other.faculties)) {
			return false;
		}
		if (id != other.id) {
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
		return getClass().getSimpleName() + " [id=" + id + ", title=" + title + ", faculties=" + faculties + "]";
	}

	@Override
	public int compareTo(Discipline d) {
		return this.getTitle().compareTo(d.getTitle());
	}

}
