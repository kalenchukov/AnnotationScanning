package dev.kalenchukov.annotationscanner;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.List;

public interface AnnotationScanning
{
	void addPackage(@NotNull String pkg);

	void removePackages();

	@NotNull
	List<Class<?>> findAnnotatedClasses(@NotNull Class<? extends Annotation> annotationClass);
}
