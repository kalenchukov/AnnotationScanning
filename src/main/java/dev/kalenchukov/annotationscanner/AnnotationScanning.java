/*
 * Copyright © 2022 Алексей Каленчуков
 * GitHub: https://github.com/kalenchukov
 * E-mail: mailto:aleksey.kalenchukov@yandex.ru
 */

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
