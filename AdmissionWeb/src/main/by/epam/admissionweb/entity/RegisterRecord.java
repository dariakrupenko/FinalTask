package main.by.epam.admissionweb.entity;

import java.io.Serializable;
import java.util.Map;

/**
 * Класс <code>RegisterRecord</code> представляет собой объект-сущность модели
 * данных приложения и инкапсулирует состояние записи ведомости учебного
 * заведения.
 * <p>
 * Запись в ведомости асоциирована только с одним абитуриентом и хранит
 * результаты сдачи абитуриентом экзаменов. Кроме того, запись в ведомости
 * всегда асоциирована только с одним набором.
 * <p>
 * Статус абитуриента может находиться в одном из четырех состояний:
 * <ul>
 * <li>временно зачислен (набор открыт)</li>
 * <li>временно незачислен (набор открыт)</li>
 * <li>зачислен (набор закрыт)</li>
 * <li>незачислен (набор закрыт)</li>
 * </ul>
 * Объекты класса <code>RegisterRecord</code> могут быть сериализованы
 * 
 * @author Daria Krupenko
 * @see Serializable
 *
 */
public class RegisterRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Абитуриент, с которым ассоциирована запись
	 */
	private Applicant applicant;

	/**
	 * Факультет, на который записан абитуриент
	 */
	private Faculty faculty;

	/**
	 * Балл аттестата абитуриента
	 */
	private int certificateScore;

	/**
	 * Суммарный балл абитуриента
	 */
	private int totalScore;

	/**
	 * Статус абитуриента
	 */
	private String status;

	/**
	 * Набор, с которым ассоциирована запись
	 */
	private Enroll enroll;

	/**
	 * Баллы абитуриента по дисциплинам
	 */
	private Map<Discipline, Integer> scores;

	/**
	 * Конструирует объект записи ведомости со значением полей по умолчанию
	 */
	public RegisterRecord() {
	}

	/**
	 * Получение баллов абитуриента по дисциплинам
	 * 
	 * @return баллы абитуриента по дисциплинам
	 */
	public Map<Discipline, Integer> getScores() {
		return scores;
	}

	/**
	 * Установка баллов абитуриента по дисциплинам
	 * 
	 * @param scores
	 *            баллы абитуриента по дисциплинам
	 */
	public void setScores(Map<Discipline, Integer> scores) {
		this.scores = scores;
	}

	/**
	 * Получение абитуриента, с которым ассоциирована запись
	 * 
	 * @return абитуриент, с которым ассоциирована запись
	 */
	public Applicant getApplicant() {
		return applicant;
	}

	/**
	 * Установка абитуриента, с которым ассоциирована запись
	 * 
	 * @param applicant
	 *            абитуриент, с которым ассоциирована запись
	 */
	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	/**
	 * Получение факультета, на который записан абитуриент
	 * 
	 * @return факультет, на который записан абитуриент
	 */
	public Faculty getFaculty() {
		return faculty;
	}

	/**
	 * Установка факультета, на который записан абитуриент
	 * 
	 * @param faculty
	 *            факультет, на который записан абитуриент
	 */
	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}

	/**
	 * Получение балла аттестата абитуриента
	 * 
	 * @return балл аттестата абитуриента
	 */
	public int getCertificateScore() {
		return certificateScore;
	}

	/**
	 * Установка балла аттестата абитуриента
	 * 
	 * @param certificateScore
	 *            балл аттестата абитуриента
	 */
	public void setCertificateScore(int certificateScore) {
		this.certificateScore = certificateScore;
	}

	/**
	 * Получение суммарного балла абитуриента
	 * 
	 * @return суммарный балл абитуриента
	 */
	public int getTotalScore() {
		return totalScore;
	}

	/**
	 * Установка суммарного балла абитуриента
	 * 
	 * @param totalScore
	 *            суммарный балл абитуриента
	 */
	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	/**
	 * Получение статуса абитуриента
	 * 
	 * @return статус абитуриента
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Установка статуса абитуриента
	 * 
	 * @param status
	 *            статус абитуриента
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Получение набора, с которым ассоциирована запись
	 * 
	 * @return набор, с которым ассоциирована запись
	 */
	public Enroll getEnroll() {
		return enroll;
	}

	/**
	 * Установка набора, с которым ассоциирована запись
	 * 
	 * @param enroll
	 *            набор, с которым ассоциирована запись
	 */
	public void setEnroll(Enroll enroll) {
		this.enroll = enroll;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((applicant == null) ? 0 : applicant.hashCode());
		result = prime * result + certificateScore;
		result = prime * result + ((enroll == null) ? 0 : enroll.hashCode());
		result = prime * result + ((faculty == null) ? 0 : faculty.hashCode());
		result = prime * result + ((scores == null) ? 0 : scores.hashCode());
		result = prime * result + totalScore;
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
		RegisterRecord other = (RegisterRecord) obj;
		if (applicant == null) {
			if (other.applicant != null) {
				return false;
			}
		} else if (!applicant.equals(other.applicant)) {
			return false;
		}
		if (certificateScore != other.certificateScore) {
			return false;
		}
		if (enroll == null) {
			if (other.enroll != null) {
				return false;
			}
		} else if (!enroll.equals(other.enroll)) {
			return false;
		}
		if (faculty == null) {
			if (other.faculty != null) {
				return false;
			}
		} else if (!faculty.equals(other.faculty)) {
			return false;
		}
		if (scores == null) {
			if (other.scores != null) {
				return false;
			}
		} else if (!scores.equals(other.scores)) {
			return false;
		}

		if (totalScore != other.totalScore) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [applicant=" + applicant + ", faculty=" + faculty + ", certificateScore="
				+ certificateScore + ", totalScore=" + totalScore + ", status=" + status + ", enroll=" + enroll
				+ ", scores=" + scores + "]";
	}

}
