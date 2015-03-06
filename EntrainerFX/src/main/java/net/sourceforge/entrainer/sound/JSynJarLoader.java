/*
 * Copyright (C) 2008 - 2014 Burton Alexander
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

import net.sourceforge.entrainer.xml.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class JSynJarLoader.
 */
public class JSynJarLoader {
	private static JSynClassLoader classLoader;
	private static SoundControl soundControl;
	private static File jsynEntrainerJar;

	/**
	 * Load j syn jar.
	 *
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException
	 *           the class not found exception
	 * @throws InstantiationException
	 *           the instantiation exception
	 * @throws IllegalAccessException
	 *           the illegal access exception
	 */
	@SuppressWarnings("deprecation")
	public static void loadJSynJar() throws IOException, ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		if (classLoader != null) {
			soundControl.teardown();
			classLoader = null;
			System.gc();
			System.gc();
		}

		if (jsynEntrainerJar == null) {
			jsynEntrainerJar = new File("lib/JSynEntrainer.jar");
		}

		Settings settings = Settings.getInstance();
		String jsynJar = settings.isNativeJsyn() ? settings.getJsynNativeJar() : settings.getJsynJavaJar();

		File jsyn = new File(jsynJar);

		URL[] urls = new URL[2];
		urls[0] = jsyn.toURL();
		urls[1] = jsynEntrainerJar.exists() ? jsynEntrainerJar.toURL() : null;

		classLoader = new JSynClassLoader(urls);

		Class<?> c = classLoader.findClass(JSynClassLoader.J_SYN_SOUND_CONTROL_CLASS);
		if (c == null) {
			c = ClassLoader.getSystemClassLoader().loadClass(JSynClassLoader.J_SYN_SOUND_CONTROL_CLASS);
		}
		Object soundControl = c.newInstance();
		JSynJarLoader.soundControl = ((SoundControl) soundControl);
	}

	/**
	 * Gets the sound control.
	 *
	 * @return the sound control
	 */
	public static SoundControl getSoundControl() {
		return soundControl;
	}

}
