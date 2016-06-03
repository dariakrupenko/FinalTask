package main.by.epam.admissionweb.generator;

/**
 * Интерфейс <code>KeyGenerator</code> предоставляет метод для генерации
 * случайного числа, которое в дальнейшем может использоваться в качестве ключа.
 * 
 * @author Daria Krupenko
 *
 */
public interface KeyGenerator {

	/**
	 * Генерирует случайное число
	 * 
	 * @return случайное число
	 */
	public int getGeneratedKey();

}
