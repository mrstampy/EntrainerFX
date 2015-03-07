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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sourceforge.entrainer.guitools.GuiUtil;

// TODO: Auto-generated Javadoc
/**
 * Code liberated from the Squirrel Sql Client
 * 
 * https://sourceforge.net/projects/squirrel-sql/
 * 
 * @author burton
 * 
 */
public class EntrainerClassLoader extends URLClassLoader {
	private Map<String, Class<?>> classes = new HashMap<String, Class<?>>();

	/**
	 * Instantiates a new entrainer class loader.
	 *
	 * @param filename the filename
	 * @throws MalformedURLException the malformed url exception
	 */
	public EntrainerClassLoader(String filename) throws MalformedURLException {
		this(new File(filename).toURI().toURL());
	}

	/**
	 * Instantiates a new entrainer class loader.
	 *
	 * @param url the url
	 */
	public EntrainerClassLoader(URL url) {
		this(new URL[] { url });
	}

	/**
	 * Instantiates a new entrainer class loader.
	 *
	 * @param urls the urls
	 */
	public EntrainerClassLoader(URL[] urls) {
		super(urls);
	}

	/**
	 * Instantiates a new entrainer class loader.
	 *
	 * @param urls the urls
	 * @param parent the parent
	 */
	public EntrainerClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	/**
	 * Instantiates a new entrainer class loader.
	 *
	 * @param urls the urls
	 * @param parent the parent
	 * @param factory the factory
	 */
	public EntrainerClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
		super(urls, parent, factory);
	}

	/* (non-Javadoc)
	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
	 */
	public Class<?> loadClass(String className) throws ClassNotFoundException {
		Class<?> cls = classes.get(className);
		if (cls == null) {
			cls = super.loadClass(className);
			classes.put(className, cls);
		}
		return cls;
	}

	/**
	 * Gets the assignable classes.
	 *
	 * @param type the type
	 * @return the assignable classes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Class<?>[] getAssignableClasses(Class<?> type) throws IOException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		URL[] urls = getURLs();
		for (int i = 0; i < urls.length; ++i) {
			URL url = urls[i];
			File file = new File(url.getFile());
			if (!file.isDirectory() && file.exists() && file.canRead()) {
				ZipFile zipFile = null;
				try {
					zipFile = new ZipFile(file);
				} catch (IOException ex) {
					GuiUtil.handleProblem(ex);
				}
				for (Enumeration<? extends ZipEntry> en = zipFile.entries(); en.hasMoreElements();) {
					Class<?> cls = null;
					String entryName = en.nextElement().getName();
					String className = changeFileNameToClassName(entryName);
					if (className != null) {
						try {
							cls = loadClass(className);
						} catch (Throwable th) {
							System.out.println("Could not load class " + className);
						}
						if (cls != null) {
							if (type.isAssignableFrom(cls) && !classes.contains(cls)) {
								classes.add(cls);
							}
						}
					}
				}
			}
		}
		return (Class[]) classes.toArray(new Class[classes.size()]);
	}

	private String changeFileNameToClassName(String name) throws IllegalArgumentException {
		if (name == null) {
			throw new IllegalArgumentException("null File Name passed.");
		}
		String className = null;
		if (name.toLowerCase().endsWith(".class")) {
			className = name.replace('/', '.');
			className = className.replace('\\', '.');
			className = className.substring(0, className.length() - 6);
		}
		return className;
	}

	/* (non-Javadoc)
	 * @see java.net.URLClassLoader#findClass(java.lang.String)
	 */
	protected Class<?> findClass(String className) throws ClassNotFoundException {
		Class<?> cls = classes.get(className);
		if (cls == null) {
			cls = super.findClass(className);
			classes.put(className, cls);
		}
		return cls;
	}

}
