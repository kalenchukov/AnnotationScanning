/*
 * Copyright © 2022-2023 Алексей Каленчуков
 * GitHub: https://github.com/kalenchukov
 * E-mail: mailto:aleksey.kalenchukov@yandex.ru
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.kalenchukov.annotation.scanning;

import dev.kalenchukov.annotation.scanning.test.annotations.MyAnnotation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс проверки методов класса {@link AnnotationScanner}.
 */
public class AnnotationScannerTest
{
	/**
	 * Проверка метода {@link AnnotationScanner#findAnnotatedClasses(Class)}.
	 */
	@Test
	public void testFindAnnotatedClasses()
	{
		AnnotationScanning annotationScanner = new AnnotationScanner();
		annotationScanner.addPackage("dev.kalenchukov.annotation.scanning.test.packages");
		List<Class<?>> annotatedClasses = annotationScanner.findAnnotatedClasses(MyAnnotation.class);

		assertEquals(4, annotatedClasses.size());
	}

	/**
	 * Проверка метода {@link AnnotationScanner#findAnnotatedClasses(Class)} по нескольким пакетам.
	 */
	@Test
	public void testFindAnnotatedClassesManyPackage()
	{
		AnnotationScanning annotationScanner = new AnnotationScanner();
		annotationScanner.addPackage("dev.kalenchukov.annotation.scanning.test.packages.films");
		annotationScanner.addPackage("dev.kalenchukov.annotation.scanning.test.packages.musics");
		List<Class<?>> annotatedClasses = annotationScanner.findAnnotatedClasses(MyAnnotation.class);

		assertEquals(4, annotatedClasses.size());
	}

	/**
	 * Проверка метода {@link AnnotationScanner#findAnnotatedClasses(Class)} без пакетов.
	 */
	@Test
	public void testFindAnnotatedClassesNotPackage()
	{
		AnnotationScanning annotationScanner = new AnnotationScanner();
		List<Class<?>> annotatedClasses = annotationScanner.findAnnotatedClasses(MyAnnotation.class);

		assertEquals(4, annotatedClasses.size());
	}
}