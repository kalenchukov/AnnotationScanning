/*
 * Copyright © 2022 Алексей Каленчуков
 * GitHub: https://github.com/kalenchukov
 * E-mail: mailto:aleksey.kalenchukov@yandex.ru
 */

package dev.kalenchukov.annotationscanner;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class AnnotationScanner implements AnnotationScanning
{
	@NotNull
	private final Set<String> pkgs = new HashSet<>();

	@NotNull
	private final List<Class<?>> annotatedClasses = new ArrayList<>();

	@NotNull
	private final String pathRoot = this.getPathRoot();

	public void addPackage(@NotNull String pkg)
	{
		this.pkgs.add(pkg);
	}

	public void removePackages()
	{
		this.pkgs.clear();
	}

	@NotNull
	public List<Class<?>> findAnnotatedClasses(@NotNull Class<? extends Annotation> annotationClass)
	{
		annotatedClasses.clear();

		for (String pkg : this.pkgs)
		{
			this.getFolders(pkg, annotationClass);
		}

		return annotatedClasses;
	}

	private void getFolders(@NotNull String path, @NotNull Class<? extends Annotation> annotationClass)
	{
		try
		{
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

			URL url = classLoader.getResource(path.replace(pathRoot, "")
												  .replace(".", "/"));

			if (url != null)
			{
				URI uri = url.toURI();
				File dir = new File(uri.getPath());

				File[] files = dir.listFiles();

				if (files != null)
				{
					for (File file : files)
					{
						if (file.isDirectory())
						{
							this.getFolders(file.getPath(), annotationClass);
						}
						else
						{
							this.getFiles(file.getPath(), annotationClass);
						}
					}
				}
			}
		}
		catch (NullPointerException | URISyntaxException | SecurityException exception)
		{
			exception.printStackTrace();
		}
	}

	private void getFiles(@NotNull String path, @NotNull Class<? extends Annotation> annotationClass)
	{
		try
		{
			Class<?> object = Class.forName(path.replace(pathRoot, "")
												.replace("/", ".")
												.replace(".class", ""));

			if (object.isAnnotationPresent(annotationClass))
			{
				annotatedClasses.add(object);
			}
		}
		catch (ClassNotFoundException exception)
		{
			exception.printStackTrace();
		}
	}

	@NotNull
	private String getPathRoot()
	{
		String path = "";

		try
		{
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

			URL url = classLoader.getResource("");

			if (url == null)
			{
				throw new NullPointerException();
			}

			path = url.toURI().getPath();
		}
		catch (NullPointerException | URISyntaxException exception)
		{
			exception.printStackTrace();
		}

		return path;
	}
}
