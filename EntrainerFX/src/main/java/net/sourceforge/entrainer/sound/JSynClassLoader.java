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
package net.sourceforge.entrainer.sound;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sourceforge.entrainer.guitools.GuiUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class JSynClassLoader.
 */
public class JSynClassLoader extends URLClassLoader {

	/** The Constant J_SYN_SOUND_CONTROL_CLASS. */
	public static final String J_SYN_SOUND_CONTROL_CLASS = "net.sourceforge.entrainer.sound.jsyn.JSynSoundControl";
	private Map<String, Class<?>> loadedClasses = new HashMap<String, Class<?>>();

	/**
	 * Instantiates a new j syn class loader.
	 *
	 * @param urls
	 *          the urls
	 */
	public JSynClassLoader(URL[] urls) {
		super(urls, JSynClassLoader.class.getClassLoader());
		try {
			loadJSynJarFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.URLClassLoader#findClass(java.lang.String)
	 */
	@Override
	public Class<?> findClass(String className) throws ClassNotFoundException {
		Class<?> c = loadedClasses.get(className);

		// This exists to allow the Entrainer/JSyn bridge to play nice
		// with the IDE environment and the deployable environment.
		// In the IDE this method will return null.
		if (c == null) {
			try {
				c = super.findClass(className);
				if (c == null) {
					c = getParent().loadClass(className);
				}
			} catch (Throwable e) {
				return null;
			}
		}

		return c;
	}

	private void loadJSynJarFile() throws IOException {
		for (URL url : getURLs()) {
			if (url == null) continue;
			File file = new File(url.getFile());
			if (!file.isDirectory() && file.exists() && file.canRead()) {
				ZipFile zipFile = null;
				try {
					zipFile = new ZipFile(file);
				} catch (IOException ex) {
					GuiUtil.handleProblem(ex);
				}
				for (Enumeration<? extends ZipEntry> en = zipFile.entries(); en.hasMoreElements();) {
					ZipEntry entry = en.nextElement();
					String entryName = entry.getName();
					String className = changeFileNameToClassName(entryName);
					if (className != null) {
						try {
							Class<?> c = loadClass(className);
							loadedClasses.put(className, c);
						} catch (Throwable th) {
							th.printStackTrace();
						}
					}
				}
			}
		}
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

}
