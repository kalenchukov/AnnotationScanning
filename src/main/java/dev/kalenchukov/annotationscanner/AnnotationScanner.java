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

public class AnnotationScanner implements AnnotationScanning
{
	@NotNull
	private final Set<String> pkgs = new HashSet<>();

	@NotNull
	private final List<Class<?>> annotatedClasses = new ArrayList<>();

	@NotNull
	private final String pathRoot = this.getPathRoot();

	public void addPackage(@NotNull final String pkg)
	{
		this.pkgs.add(pkg);
	}

	public void removePackages()
	{
		this.pkgs.clear();
	}

	@NotNull
	public List<Class<?>> findAnnotatedClasses(@NotNull final Class<? extends Annotation> annotationClass)
	{
		annotatedClasses.clear();

		// Добавление корневого пакета, если нет ни одного
		if (pkgs.size() == 0) {
			this.addPackage(this.pathRoot);
		}

		for (String pkg : this.pkgs) {
			this.getFolders(pkg, annotationClass);
		}

		return annotatedClasses;
	}

	private void getFolders(@NotNull String path, @NotNull final Class<? extends Annotation> annotationClass)
	{
		System.out.println("FOLDER: " + path);

		if (path.startsWith(this.pathRoot)) {
			path = path.replace(this.pathRoot, "");
		}

		try
		{
			File dir = new File(this.packageToPath(path));

			File[] files = dir.listFiles();

			if (files != null)
			{
				for (File file : files)
				{
					if (!file.getName().equals("module-info.class") && !file.getName().equals("package-info.class"))
					{
						if (file.isDirectory()) {
							this.getFolders(file.getPath(), annotationClass);
						}
						else {
							this.getFiles(file.getPath(), annotationClass);
						}
					}
				}
			}
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	private void getFiles(@NotNull final String path, @NotNull final Class<? extends Annotation> annotationClass)
	{
		System.out.println("FILE: " + path);

		try
		{
			Class<?> objectClass = Class.forName(this.pathToPackage(path).replace(".class", ""));

			if (objectClass.isAnnotationPresent(annotationClass)) {
				annotatedClasses.add(objectClass);
			}
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	@NotNull
	private String getPathRoot()
	{
		String pathRoot = System.getProperty("user.dir") + "/target/classes/";

		System.out.println("ROOT: " + pathRoot);

		return pathRoot;
	}

	@NotNull
	private String packageToPath(@NotNull final String packageName)
	{
		return this.pathRoot + packageName.replace(".", "/");
	}

	@NotNull
	private String pathToPackage(@NotNull final String path)
	{
		return path.replace(pathRoot, "")
				   .replace("/", ".");
	}
}
