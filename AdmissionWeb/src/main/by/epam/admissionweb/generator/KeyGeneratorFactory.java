package main.by.epam.admissionweb.generator;

import main.by.epam.admissionweb.generator.impl.SimpleKeyGenerator;

/**
 * Класс <code>KeyGeneratorFactory</code> представляет собой фабрику для
 * получения генератора ключей.
 * <p>
 * Получение объекта класса <code>KeyGeneratorFactory</code> осуществляется
 * путем вызова статического метода <code>getInstance()</code> данного класса.
 * <p>
 * По умолчанию фабрика предоставляет объект класса {@link SimpleKeyGenerator}.
 * 
 * @author Daria Krupenko
 * @see KeyGenerator
 * @see SimpleKeyGenarator
 *
 */
public class KeyGeneratorFactory {

	/**
	 * Объект класса <code>KeyGeneratorFactory</code>, создается один раз при
	 * загрузке класса в память
	 */
	private static final KeyGeneratorFactory INSTANCE = new KeyGeneratorFactory();

	/**
	 * Реализация <code>KeyGenerator</code> по умолчанию
	 */
	private static final KeyGenerator SIMPLE_KEY_GENERATOR = new SimpleKeyGenerator();

	private KeyGeneratorFactory() {
	}

	/**
	 * Получение объекта класса <code>KeyGeneratorFactory</code>
	 * 
	 * @return объект класса <code>KeyGeneratorFactory</code>
	 */
	public static KeyGeneratorFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * Получение генератора ключей по умолчанию
	 * 
	 * @return объект <code>KeyGenerator</code>
	 */
	public KeyGenerator getKeyGenerator() {
		return SIMPLE_KEY_GENERATOR;
	}

}
