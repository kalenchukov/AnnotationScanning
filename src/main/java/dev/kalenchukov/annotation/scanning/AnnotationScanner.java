/*
 * Copyright © 2022 Алексей Каленчуков
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

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Класс для поиска аннотированных классов.
 */
public class AnnotationScanner implements AnnotationScanning
{
	/**
	 * Локализация.
	 */
	@NotNull
	private Locale locale;

	/**
	 * Корневая директория для поиска аннотированных классов.
	 */
	@NotNull
	private final String rootDirectory;

	/**
	 * Коллекция пакетов в которых необходимо искать аннотированные классы.
	 */
	@NotNull
	private final Set<@NotNull String> pkgs;

	/**
	 * Коллекция найденных аннотированных классов.
	 */
	@NotNull
	private final List<@NotNull Class<?>> annotatedClasses;

	/**
	 * Локализованные тексты логирования.
	 */
	@NotNull
	private ResourceBundle localeLogs;

	/**
	 * Логгер для данного класса.
	 */
	@NotNull
	private static final Logger LOG = Logger.getLogger(AnnotationScanner.class);

	/**
	 * Конструктор для {@code AnnotationScanner}.
	 */
	public AnnotationScanner()
	{
		this.locale = new Locale("ru", "RU");
		this.rootDirectory = this.getRootDirectory();
		this.pkgs = new HashSet<>();
		this.annotatedClasses = new ArrayList<>();
		this.localeLogs = ResourceBundle.getBundle(
			"annotation/scanning/localizations/logs",
			this.locale
		);
	}

	/**
	 * @see AnnotationScanning#setLocale(Locale)
	 */
	public void setLocale(@NotNull final Locale locale)
	{
		Objects.requireNonNull(locale);

		if (!this.locale.equals(locale))
		{
			this.locale = locale;

			this.localeLogs = ResourceBundle.getBundle(
				"annotation/scanning/localizations/logs",
				this.locale
			);
		}
	}

	/**
	 * @see AnnotationScanning#addPackage(String)
	 */
	public void addPackage(@NotNull final String pkg)
	{
		Objects.requireNonNull(pkg);

		this.pkgs.add(pkg);

		LOG.debug(String.format(
			this.localeLogs.getString("00001"),
			pkg
		));
	}

	/**
	 * @see AnnotationScanning#removePackages()
	 */
	public void removePackages()
	{
		this.pkgs.clear();

		LOG.debug(this.localeLogs.getString("00002"));
	}

	/**
	 * @see AnnotationScanning#findAnnotatedClasses(Class)
	 */
	@NotNull
	public List<@NotNull Class<?>> findAnnotatedClasses(@NotNull final Class<? extends Annotation> annotationClass)
	{
		Objects.requireNonNull(annotationClass);

		LOG.debug(String.format(
			this.localeLogs.getString("00008"),
			this.rootDirectory
		));

		annotatedClasses.clear();

		for (String pkg : this.pkgs) {
			this.scanDirectory(this.packageToDirectory(pkg), annotationClass);
		}

		// Сканирование корневой директории, если не добавлено ни одного пакета для сканирования
		if (pkgs.size() == 0) {
			this.scanDirectory(this.rootDirectory, annotationClass);
		}

		return annotatedClasses;
	}

	/**
	 * Сканирует директорию на наличие файлов.
	 *
	 * @param directory Директория.
	 * @param annotationClass Аннотация которую необходимо искать в классах.
	 */
	private void scanDirectory(@NotNull String directory, @NotNull final Class<? extends Annotation> annotationClass)
	{
		Objects.requireNonNull(directory);
		Objects.requireNonNull(annotationClass);

		LOG.debug(String.format(
			this.localeLogs.getString("00003"),
			directory
		));

		try
		{
			File dir = new File(directory);
			File[] files = Objects.requireNonNull(dir.listFiles());

			for (File file : files)
			{
				if (file.isHidden()) {
					continue;
				}

				if (!file.canRead()) {
					LOG.debug(String.format(
						this.localeLogs.getString("00004"),
						directory
					));
					continue;
				}

				if (file.isDirectory()) {
					this.scanDirectory(file.getPath(), annotationClass);
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
	 * @param path Путь до файла.
	 * @param annotationClass Аннотация которую необходимо искать в классах.
	 */
	private void checkFile(@NotNull final String path, @NotNull final Class<? extends Annotation> annotationClass)
	{
		Objects.requireNonNull(path);
		Objects.requireNonNull(annotationClass);

		LOG.debug(String.format(
			this.localeLogs.getString("00005"),
			path
		));

		if (!this.isCorrectFile(path)) {
			return;
		}

		try
		{
			Class<?> objectClass = Class.forName(this.directoryToPackage(path).replaceAll("\\.class$", ""));

			if (objectClass.isAnnotationPresent(annotationClass))
			{
				annotatedClasses.add(objectClass);

				LOG.debug(String.format(
					this.localeLogs.getString("00007"),
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
	 * @return Корневая директория.
	 */
	@NotNull
	private String getRootDirectory()
	{
		return System.getProperty("user.dir") + "/target/classes/";
	}

	/**
	 * Преобразовывает пакет в директорию.
	 *
	 * @param pkg Пакет.
	 * @return Директория.
	 */
	@NotNull
	private String packageToDirectory(@NotNull final String pkg)
	{
		Objects.requireNonNull(pkg);

		return this.rootDirectory + pkg.replace(".", "/");
	}

	/**
	 * Преобразовывает директорию в пакет.
	 *
	 * @param directory Директория.
	 * @return Пакет.
	 */
	@NotNull
	private String directoryToPackage(@NotNull final String directory)
	{
		Objects.requireNonNull(directory);

		return directory.replace(this.rootDirectory, "")
						.replace("/", ".");
	}

	/**
	 * Проверяет корректность файла.
	 *
	 * @param directory Директория до файла.
	 * @return Возвращает true, если в файле может присутствовать нужная аннотация, иначе false.
	 */
	@NotNull
	private Boolean isCorrectFile(@NotNull final String directory)
	{
		Objects.requireNonNull(directory);

		if (!directory.endsWith(".class"))
		{
			LOG.debug(String.format(
				this.localeLogs.getString("00006"),
				directory
			));

			return false;
		}

		String[] excludeFiles = {
			"module-info.class",
			"package-info.class"
		};

		for (String file : excludeFiles)
		{
			if (directory.endsWith(file))
			{
				LOG.debug(String.format(
					this.localeLogs.getString("00006"),
					directory
				));

				return false;
			}
		}

		return true;
	}
}
