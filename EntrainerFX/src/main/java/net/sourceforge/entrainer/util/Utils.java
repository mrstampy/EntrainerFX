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

import java.awt.Desktop;
import java.net.URI;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import net.sourceforge.entrainer.guitools.GuiUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class Utils.
 */
public class Utils {
	private static Logger log = LoggerFactory.getLogger(Utils.class);

	/**
	 * Snooze.
	 *
	 * @param time the time
	 */
	public static void snooze(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			log.warn("Woke up unexpectedly from a snooze", e);
		}
	}

	/**
	 * Snooze.
	 *
	 * @param time the time
	 * @param nanos the nanos
	 */
	public static void snooze(long time, int nanos) {
		try {
			Thread.sleep(time, nanos);
		} catch (InterruptedException e) {
			log.warn("Woke up unexpectedly from a snooze", e);
		}
	}

	/**
	 * List system properties.
	 */
	public static void listSystemProperties() {
		Properties props = System.getProperties();

		Set<Entry<Object, Object>> set = props.entrySet();
		for (Entry<Object, Object> entry : set) {
			System.out.println(entry.getKey() + "=" + entry.getValue());
		}
	}

	/**
	 * Open browser.
	 *
	 * @param address the address
	 */
	public static void openBrowser(String address) {
		address = address.replace("\\", "/");
		address = address.replace(" ", "%20");
		try {
			openBrowser(URI.create(address));
		} catch (Throwable e) {
			GuiUtil.handleProblem(e);
		}
	}

	/**
	 * Open browser.
	 *
	 * @param uri the uri
	 */
	public static void openBrowser(URI uri) {
		try {
			Desktop.getDesktop().browse(uri);
		} catch (Throwable e) {
			GuiUtil.handleProblem(e);
		}
	}

}
