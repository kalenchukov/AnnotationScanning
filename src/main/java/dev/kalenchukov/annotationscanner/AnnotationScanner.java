/*
 * Copyright © 2021 Алексей Каленчуков
 * GitHub: https://github.com/kalenchukov
 * E-mail: mailto:aleksey.kalenchukov@yandex.ru
 */

package dev.kalenchukov.annotationscanner;

import java.io.*;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class AnnotationScanner implements ScanningAnnotations
{
	private Set<String> pkgs = new HashSet<>();

	public void addPackage(String pkg)
	{
		Objects.requireNonNull(pkg);

		this.pkgs.add(pkg);
	}

	public Set<Class<?>> findAnnotation(Class<? extends Annotation> annotationClass)
	{
		Objects.requireNonNull(annotationClass);

		Set<Class<?>> annotatedClasses = new HashSet<>();

		for (String pkg : pkgs)
		{
			annotatedClasses.addAll(loadPackage(pkg, annotationClass));
		}

		return annotatedClasses;
	}

	private Set<Class<?>> loadPackage(String pkg, Class<? extends Annotation> annotationClass)
	{
		Set<Class<?>> annotatedClasses = new HashSet<>();

		try
		{
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			URL url = classLoader.getResource(pkg.replace(".", "/"));

			if (url == null)
			{
				throw new NullPointerException("Bad package: " + pkg);
			}

			URI uri = url.toURI();
			File dir = new File(uri.getPath());

			FileFilter fileFilter = new FileFilter()
			{
				@Override
				public boolean accept(File pathname)
				{
					return (pathname.isFile());
				}
			};

			File[] files = dir.listFiles(fileFilter);

			if (files != null)
			{
				for (File file : files)
				{
					Class<?> object = Class.forName(pkg + "." + file.getName().replace(".class", ""));

					if (object.isAnnotationPresent(annotationClass))
					{
						annotatedClasses.add(object);
					}
				}
			}
		}
		catch(ClassNotFoundException | NullPointerException | URISyntaxException | SecurityException exception)
		{
			exception.printStackTrace();
		}

		return annotatedClasses;
	}
}
