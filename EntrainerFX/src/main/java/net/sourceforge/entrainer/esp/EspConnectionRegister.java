/*
 *      ______      __             _                 _______  __
 *     / ____/___  / /__________ _(_)___  ___  _____/ ____/ |/ /
 *    / __/ / __ \/ __/ ___/ __ `/ / __ \/ _ \/ ___/ /_   |   / 
 *   / /___/ / / / /_/ /  / /_/ / / / / /  __/ /  / __/  /   |  
 *  /_____/_/ /_/\__/_/   \__,_/_/_/ /_/\___/_/  /_/    /_/|_|  
 *                                                          
 *
 * Copyright (C) 2008 - 2016 Burton Alexander
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
package net.sourceforge.entrainer.esp;

import java.util.List;

import com.github.mrstampy.esp.dsp.lab.RawEspConnection;

// TODO: Auto-generated Javadoc
/**
 * The Class EspConnectionRegister.
 */
public final class EspConnectionRegister {

	private static EspConnectionLoader loader = new EspConnectionLoader();

	private static RawEspConnection connection;

	static {
		loader.loadAllConnections();
	}

	private EspConnectionRegister() {
	}

	/**
	 * Gets the esp connections.
	 *
	 * @return the esp connections
	 */
	public static List<RawEspConnection> getEspConnections() {
		return loader.getEspConnections();
	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public static boolean isEmpty() {
		return loader.isEmpty();
	}

	/**
	 * Gets the current connection.
	 *
	 * @return the current connection
	 */
	public static RawEspConnection getCurrentConnection() {
		if (connection == null) {
			setDefaultConnection();
		}

		return connection;
	}

	/**
	 * Gets the esp connection.
	 *
	 * @param name
	 *          the name
	 * @return the esp connection
	 */
	public static RawEspConnection getEspConnection(String name) {
		if (connection != null && connection.getName().equals(name)) return getCurrentConnection();

		for (RawEspConnection conn : getEspConnections()) {
			if (conn.getName().equals(name)) {
				connection = conn;
				return conn;
			}
		}

		return null;
	}

	private static void setDefaultConnection() {
		for (RawEspConnection conn : getEspConnections()) {
			connection = conn;
			break;
		}
	}

}
