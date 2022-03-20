/*
 * Copyright © 2022 Алексей Каленчуков
 * GitHub: https://github.com/kalenchukov
 * E-mail: mailto:aleksey.kalenchukov@yandex.ru
 */

package dev.kalenchukov.annotationscanner.tests;

import dev.kalenchukov.annotationscanner.AnnotationScanner;
import dev.kalenchukov.annotationscanner.AnnotationScanning;
import dev.kalenchukov.annotationscanner.tests.annotations.MyAnnotation;

import java.util.List;

public final class Test
{
	public static void main(String[] args)
	{
		AnnotationScanning annotationScanner = new AnnotationScanner();
		annotationScanner.addPackage("dev.kalenchukov.annotationscanner.tests.packages.films");
//		annotationScanner.addPackage("dev.kalenchukov.annotationscanner.tests.packages.musics");
		List<Class<?>> annotatedClasses = annotationScanner.findAnnotatedClasses(MyAnnotation.class);

		for (Class<?> objectClass : annotatedClasses) {
			System.out.println("Найден класс: " + objectClass.getName() + ".class");
		}
	}
}
