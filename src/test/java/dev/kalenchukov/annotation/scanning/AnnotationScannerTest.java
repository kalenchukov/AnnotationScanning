/*
 * Copyright © 2022 Алексей Каленчуков
 * GitHub: https://github.com/kalenchukov
 * E-mail: mailto:aleksey.kalenchukov@yandex.ru
 */

package dev.kalenchukov.annotation.scanning;

import dev.kalenchukov.annotation.scanning.test.annotations.MyAnnotation;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class AnnotationScannerTest
{
	/**
	 * Проверка поиска по одному пакету.
	 */
	@Test
	public void findAnnotatedClasses1()
	{
		AnnotationScanning annotationScanner = new AnnotationScanner();
		annotationScanner.addPackage("dev.kalenchukov.annotationscanner.test.packages");
		List<Class<?>> annotatedClasses = annotationScanner.findAnnotatedClasses(MyAnnotation.class);

		assertEquals(4, annotatedClasses.size());
	}

	/**
	 * Проверка поиска по двум пакетам.
	 */
	@Test
	public void findAnnotatedClasses2()
	{
		AnnotationScanning annotationScanner = new AnnotationScanner();
		annotationScanner.addPackage("dev.kalenchukov.annotationscanner.test.packages.films");
		annotationScanner.addPackage("dev.kalenchukov.annotationscanner.test.packages.musics");
		List<Class<?>> annotatedClasses = annotationScanner.findAnnotatedClasses(MyAnnotation.class);

		assertEquals(4, annotatedClasses.size());
	}

	/**
	 * Проверка поиска без указания пакетов.
	 */
	@Test
	public void findAnnotatedClasses3()
	{
		AnnotationScanning annotationScanner = new AnnotationScanner();
		List<Class<?>> annotatedClasses = annotationScanner.findAnnotatedClasses(MyAnnotation.class);

		assertEquals(4, annotatedClasses.size());
	}
}