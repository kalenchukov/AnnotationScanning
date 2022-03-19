package dev.kalenchukov.annotationscanner.test;

import dev.kalenchukov.annotationscanner.AnnotationScanner;
import dev.kalenchukov.annotationscanner.AnnotationScanning;
import dev.kalenchukov.annotationscanner.test.annotations.MyAnnotation;

import java.util.List;

public class Run
{
	public static void main(String[] args)
	{
		AnnotationScanning annotationScanner = new AnnotationScanner();
		annotationScanner.addPackage("dev.kalenchukov.annotationscanner.test.packages.films");
		annotationScanner.addPackage("dev.kalenchukov.annotationscanner.test.packages.musics");
		List<Class<?>> annotatedClasses = annotationScanner.findAnnotatedClasses(MyAnnotation.class);

		for (Class<?> objectClass : annotatedClasses)
		{
			System.out.println(objectClass.getName() + ".class");
		}
	}
}
