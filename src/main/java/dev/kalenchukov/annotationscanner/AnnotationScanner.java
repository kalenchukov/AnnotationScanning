/*
 * Copyright © 2022 Алексей Каленчуков
 * GitHub: https://github.com/kalenchukov
 * E-mail: mailto:aleksey.kalenchukov@yandex.ru
 */

package dev.kalenchukov.annotationscanner;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Класс для поиска аннотированные классов.
 */
public class AnnotationScanner implements AnnotationScanning
{
	/**
	 * Устанавливает логгер для данного класса.
	 */
	@NotNull
	private static final Logger LOG = Logger.getLogger(AnnotationScanner.class);

	/**
	 * Локаль.
	 * Внимание! Необходима для логов, так как локализационные тексты используются только для логирования.
	 * Для изменения локали необходимо использовать {@link #setLocale(Locale)}.
	 */
	@NotNull
	private Locale locale = new Locale("ru", "RU");

	/**
	 * Устанавливает тексты локализации для логирования.
	 */
	@NotNull
	private ResourceBundle localeCore = ResourceBundle.getBundle("localizations/core", this.locale);

	/**
	 * Корневая директория для поиска аннотированных классов.
	 */
	@NotNull
	private final String pathRoot = this.getPathRoot();

	/**
	 * Коллекция пакетов в которых необходимо искать аннотированные классы.
	 * Для добавления пакетов необходимо использовать {@link #addPackage(String)}.
	 */
	@NotNull
	private final Set<String> pkgs = new HashSet<>();

	/**
	 * Коллекция найденных аннотированных классов.
	 */
	@NotNull
	private final List<Class<?>> annotatedClasses = new ArrayList<>();

	/**
	 * @see AnnotationScanning#setLocale(Locale)
	 */
	public void setLocale(@NotNull final Locale locale)
	{
		this.locale = locale;

		localeCore = ResourceBundle.getBundle("localizations/core", this.locale);
	}

	/**
	 * @see AnnotationScanning#addPackage(String)
	 */
	public void addPackage(@NotNull final String pkg)
	{
		this.pkgs.add(pkg);

		LOG.debug(String.format(
			localeCore.getString("00001"),
			pkg
		));
	}

	/**
	 * @see AnnotationScanning#removePackages()
	 */
	public void removePackages()
	{
		this.pkgs.clear();

		LOG.debug(localeCore.getString("00002"));
	}

	/**
	 * @see AnnotationScanning#findAnnotatedClasses(Class)
	 */
	@NotNull
	public List<Class<?>> findAnnotatedClasses(@NotNull final Class<? extends Annotation> annotationClass)
	{
		LOG.debug(String.format(
			localeCore.getString("00008"),
			this.pathRoot
		));

		annotatedClasses.clear();

		// Добавление корневого пакета, если нет ни одного
		if (pkgs.size() == 0) {
			this.addPackage(this.pathRoot);
		}

		for (String pkg : this.pkgs) {
			this.scanFolder(this.packageToPath(pkg), annotationClass);
		}

		return annotatedClasses;
	}

	/**
	 * Сканирует директорию на наличие файлов.
	 *
	 * @param path Строка в виде директории.
	 * @param annotationClass Аннотация которую необходимо искать в классах.
	 */
	private void scanFolder(@NotNull String path, @NotNull final Class<? extends Annotation> annotationClass)
	{
		if (path.startsWith(this.pathRoot)) {
			path = path.replace(this.pathRoot, "");
		}

		path = this.packageToPath(path);

		LOG.debug(String.format(
			localeCore.getString("00003"),
			path
		));

		try
		{
			File dir = new File(path);
			File[] files = Objects.requireNonNull(dir.listFiles());

			for (File file : files)
			{
				if (file.isHidden()) {
					continue;
				}

				if (!file.canRead()) {
					LOG.debug(String.format(
						localeCore.getString("00004"),
						path
					));
					continue;
				}

				if (file.isDirectory()) {
					this.scanFolder(file.getPath(), annotationClass);
				}
				else {
					this.checkFile(file.getPath(), annotationClass);
				}
			}
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	/**
	 * Проверяет файл на наличие необходимой аннотации.
	 *
	 * @param path Директория до файла.
	 * @param annotationClass Аннотация которую необходимо искать в классах.
	 */
	private void checkFile(@NotNull final String path, @NotNull final Class<? extends Annotation> annotationClass)
	{
		LOG.debug(String.format(
			localeCore.getString("00005"),
			path
		));

		if (!this.isCorrectFile(path)) {
			return;
		}

		try
		{
			Class<?> objectClass = Class.forName(this.pathToPackage(path).replace(".class", ""));

			if (objectClass.isAnnotationPresent(annotationClass))
			{
				annotatedClasses.add(objectClass);

				LOG.debug(String.format(
					localeCore.getString("00007"),
					path
				));
			}
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	/**
	 * Возвращает корневую директорию.
	 *
	 * @return Строка в виде директории.
	 */
	@NotNull
	private String getPathRoot()
	{
		return System.getProperty("user.dir") + "/target/classes/";
	}

	/**
	 * Преобразовывает пакет в директорию.
	 *
	 * @param pkg Строка в виде пакета.
	 * @return Строка в виде директории.
	 */
	@NotNull
	private String packageToPath(@NotNull final String pkg)
	{
		return this.pathRoot + pkg.replace(".", "/");
	}

	/**
	 * Преобразовывает директорию в пакет.
	 *
	 * @param path Строка в виде директории.
	 * @return Строка в виде пакета.
	 */
	@NotNull
	private String pathToPackage(@NotNull final String path)
	{
		return path.replace(pathRoot, "")
				   .replace("/", ".");
	}

	/**
	 * Проверяет корректность файла.
	 *
	 * @param path Директория до файла.
	 * @return Возвращает true, если в файле может присутствовать нужная аннотация, иначе false.
	 */
	@NotNull
	private Boolean isCorrectFile(@NotNull final String path)
	{
		if (!path.endsWith(".class"))
		{
			LOG.debug(String.format(
				localeCore.getString("00006"),
				path
			));

			return false;
		}

		String[] excludeFiles = {
			"module-info.class",
			"package-info.class"
		};

		for (String file : excludeFiles)
		{
			if (path.endsWith(file))
			{
				LOG.debug(String.format(
					localeCore.getString("00006"),
					path
				));

				return false;
			}
		}

		return true;
	}
}
