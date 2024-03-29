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

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Класс для поиска аннотированных классов.
 *
 * @author Алексей Каленчуков
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
	private static final Logger LOG = LogManager.getLogger(AnnotationScanner.class);

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
	 * {@inheritDoc}
	 *
	 * @param locale {@inheritDoc}
	 * @throws NullPointerException если в качестве {@code locale} передан {@code null}.
	 */
	@Override
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
	 * {@inheritDoc}
	 *
	 * @param pkg {@inheritDoc}
	 * @throws NullPointerException если в качестве {@code pkg} передан {@code null}.
	 */
	@Override
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
	 * {@inheritDoc}
	 */
	@Override
	public void removePackages()
	{
		this.pkgs.clear();

		LOG.debug(this.localeLogs.getString("00002"));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param annotationClass {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws NullPointerException если в качестве {@code annotationClass} передан {@code null}.
	 */
	@NotNull
	@Override
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
	 * @param directory директория.
	 * @param annotationClass аннотация которую необходимо искать в классах.
	 * @throws NullPointerException если в качестве {@code directory} передан {@code null}.
	 * @throws NullPointerException если в качестве {@code annotationClass} передан {@code null}.
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
	 * @param path путь до файла.
	 * @param annotationClass аннотация которую необходимо искать в классах.
	 * @throws NullPointerException если в качестве {@code path} передан {@code null}.
	 * @throws NullPointerException если в качестве {@code annotationClass} передан {@code null}.
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
	 * @return корневая директория.
	 */
	@NotNull
	private String getRootDirectory()
	{
		return System.getProperty("user.dir") +
			File.separator + "target" +
			File.separator + "classes" +
			File.separator;
	}

	/**
	 * Преобразовывает пакет в директорию.
	 *
	 * @param pkg пакет.
	 * @return директория.
	 * @throws NullPointerException если в качестве {@code pkg} передан {@code null}.
	 */
	@NotNull
	private String packageToDirectory(@NotNull final String pkg)
	{
		Objects.requireNonNull(pkg);

		return this.rootDirectory + pkg.replace(".", File.separator);
	}

	/**
	 * Преобразовывает директорию в пакет.
	 *
	 * @param directory директория.
	 * @return пакет.
	 * @throws NullPointerException если в качестве {@code directory} передан {@code null}.
	 */
	@NotNull
	private String directoryToPackage(@NotNull final String directory)
	{
		Objects.requireNonNull(directory);

		return directory.replace(this.rootDirectory, "")
						.replace(File.separator, ".");
	}

	/**
	 * Проверяет корректность файла.
	 *
	 * @param directory директория до файла.
	 * @return возвращает true, если в файле может присутствовать нужная аннотация, иначе false.
	 * @throws NullPointerException если в качестве {@code directory} передан {@code null}.
	 */
	private boolean isCorrectFile(@NotNull final String directory)
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
