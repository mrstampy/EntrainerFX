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
package net.sourceforge.entrainer.gui;

import java.io.File;
import java.util.EventObject;

// TODO: Auto-generated Javadoc
/**
 * Event fired upon save of an xml file.
 * 
 * @author burton
 *
 */
public class XmlFileSaveEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private File xmlFile;

	/**
	 * Instantiates a new xml file save event.
	 *
	 * @param source
	 *          the source
	 * @param xmlFile
	 *          the xml file
	 */
	public XmlFileSaveEvent(Object source, File xmlFile) {
		super(source);
		setXmlFile(xmlFile);
	}

	/**
	 * Gets the xml file.
	 *
	 * @return the xml file
	 */
	protected File getXmlFile() {
		return xmlFile;
	}

	/**
	 * Sets the xml file.
	 *
	 * @param xmlFile
	 *          the new xml file
	 */
	protected void setXmlFile(File xmlFile) {
		this.xmlFile = xmlFile;
	}

}
