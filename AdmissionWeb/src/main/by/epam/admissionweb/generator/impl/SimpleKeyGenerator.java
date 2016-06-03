package main.by.epam.admissionweb.generator.impl;

import java.util.Random;

import main.by.epam.admissionweb.generator.KeyGenerator;

/**
 * Класс <code>SimpleKeyGenerator</code> реализует интерфейс
 * {@link KeyGenarator} и представляет собой простой генератор случайных чисел.
 * 
 * @author Daria Krupenko
 *
 */
public class SimpleKeyGenerator implements KeyGenerator {

	private static final int MAX_VALUE = 999999;

	/**
	 * Генерирует случайное число в диапазоне от 0 до MAX_VALUE (999999)
	 * 
	 * @return случайное число
	 * @see Random
	 */
	@Override
	public int getGeneratedKey() {
		Random r = new Random();
		return r.nextInt(MAX_VALUE);
	}

}
