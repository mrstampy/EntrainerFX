/*
 * Copyright (C) 2008 - 2015 Burton Alexander
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package net.sourceforge.entrainer.util;

/*
 * Copyright (C) 2008, 2009 Burton Alexander
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.entrainer.guitools.GuiUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The static methods are used by class-specific registers.
 * 
 * @author burton
 * 
 */
public class EntrainerRegister {
	private static final Logger log = LoggerFactory.getLogger(EntrainerRegister.class);

	private EntrainerRegister() {

	}

	/**
	 * Returns a list of jar and zip files in the specified directory.
	 *
	 * @param directory
	 *          the directory
	 * @return the package urls
	 */
	public static List<URL> getPackageUrls(String directory) {
		List<URL> urls = new ArrayList<URL>();
		File dir = new File(directory);
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; ++i) {
				File jarFile = files[i];
				String jarFileName = jarFile.getAbsolutePath();
				if (jarFile.isFile()
						&& (jarFileName.toLowerCase().endsWith(".zip") || jarFileName.toLowerCase().endsWith(".jar"))) {
					try {
						URL url = jarFile.toURI().toURL();
						if (!urls.contains(url)) {
							urls.add(url);
						}
					} catch (IOException ex) {
						GuiUtil.handleProblem(ex);
					}
				}
			}
		}

		return urls;
	}

	/**
	 * Recursively determines the jar & zip files in the given root directory &
	 * returns the list.
	 *
	 * @param directory
	 *          the directory
	 * @return the jar files in directory
	 */
	public static List<Path> getJarFilesInDirectory(String directory) {
		List<Path> jarPaths = new ArrayList<Path>();

		getJarFilesInDirectory(Paths.get(directory), jarPaths);

		return jarPaths;
	}

	private static void getJarFilesInDirectory(Path directory, List<Path> jarPaths) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
			for (Path path : stream) {
				BasicFileAttributeView view = Files.getFileAttributeView(path, BasicFileAttributeView.class);
				BasicFileAttributes attributes = view.readAttributes();
				if (attributes.isDirectory()) {
					getJarFilesInDirectory(path, jarPaths);
				} else if (path.toString().endsWith(".jar") || path.toString().endsWith(".zip")) {
					jarPaths.add(path);
				}
			}
		} catch (Exception e) {
			log.error("Could not load jar files from directory " + directory, e);
		}
	}

	/**
	 * Loads the classes specified by the list of URL's.
	 *
	 * @param <T>
	 *          the generic type
	 * @param jarUrls
	 *          the jar urls
	 * @param clazz
	 *          the clazz
	 * @param list
	 *          the list
	 * @return the entrainer class loader
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Object> EntrainerClassLoader loadClasses(List<URL> jarUrls, Class<T> clazz, List<T> list) {
		EntrainerClassLoader loader = null;
		try {
			URL[] urls = ((URL[]) jarUrls.toArray(new URL[jarUrls.size()]));
			loader = new EntrainerClassLoader(urls, EntrainerRegister.class.getClassLoader());
			Class<?>[] classes = loader.getAssignableClasses(clazz);
			for (int i = 0; i < classes.length; ++i) {
				Class<?> c = classes[i];
				int mods = c.getModifiers();
				if (Modifier.isAbstract(mods) || Modifier.isInterface(mods)) continue;
				try {
					T newInstance = (T) c.newInstance();
					clearExistingClassInstance(newInstance, list);
					list.add(newInstance);
				} catch (Throwable e) {
					// pro'ly an abstract superclass...
					log.warn("Could not load " + c, e);
				}
			}

		} catch (Throwable th) {
			GuiUtil.handleProblem(th);
		}

		return loader;
	}

	private static <T extends Object> void clearExistingClassInstance(T newInstance, List<T> list) {
		ListIterator<T> it = list.listIterator();
		while (it.hasNext()) {
			T old = it.next();
			if (old.getClass().getCanonicalName().equals(newInstance.getClass().getCanonicalName())) it.remove();
		}
	}

}
