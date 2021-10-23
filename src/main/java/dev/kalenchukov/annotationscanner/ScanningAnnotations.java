package dev.kalenchukov.annotationscanner;

import java.lang.annotation.Annotation;
import java.util.Set;

public interface ScanningAnnotations
{
	void addPackage(String pkg);
	Set<Class<?>> findAnnotation(Class<? extends Annotation> annotationClass);
}
