/*
 * Copyright © 2022 Алексей Каленчуков
 * GitHub: https://github.com/kalenchukov
 * E-mail: mailto:aleksey.kalenchukov@yandex.ru
 */

package dev.kalenchukov.annotationscanner;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Locale;

public interface AnnotationScanning
{
	/**
	 * Устанавливает локализацию.
	 *
	 * @param locale Локаль.
	 */
	void setLocale(@NotNull Locale locale);

	/**
	 * Добавляет пакет в котором необходимо искать аннотированные классы.
	 *
	 * @param pkg Пакет.
	 */
	void addPackage(@NotNull String pkg);

	/**
	 * Удаляет все установленные пакеты в котором необходимо искать аннотированные классы.
	 */
	void removePackages();

	/**
	 * Выполняет поиск классов которые содержат указанную аннотацию.
	 *
	 * @param annotationClass Аннотация которую необходимо искать в классах.
	 * @return Коллекцию классов которые содержат искомую аннотацию.
	 */
	@NotNull
	List<@NotNull Class<?>> findAnnotatedClasses(@NotNull Class<? extends Annotation> annotationClass);
}
