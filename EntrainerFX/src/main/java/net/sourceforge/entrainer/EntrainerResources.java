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
package net.sourceforge.entrainer;


// TODO: Auto-generated Javadoc
/**
 * The Interface EntrainerResources.
 */
public interface EntrainerResources {
	
	public static final String EFX_USER_HOME = System.getProperty("user.home") + "/EntrainerFX-Settings/";

	/** The Constant EFX_CONFIG_DIR. */
	public static final String EFX_CONFIG_DIR = "./";

	/** The Constant EFX_PROGRAM_DIR. */
	public static final String EFX_PROGRAM_DIR = EFX_CONFIG_DIR + "xml";
	
	public static final String EFX_USER_HOME_PROGRAM_DIR = EFX_USER_HOME + "xml";

	/** The Constant EFX_ANIMATION_DIR. */
	public static final String EFX_ANIMATION_DIR = EFX_CONFIG_DIR + "animation";

	public static final String EFX_USER_HOME_ANIMATION_DIR = EFX_USER_HOME + "animation";

	/** The Constant EFX_LAF_DIR. */
	public static final String EFX_LAF_DIR = EFX_CONFIG_DIR + "lafs";

	/** The Constant EFX_ESP_DIR. */
	public static final String EFX_ESP_DIR = EFX_CONFIG_DIR + "esp";

	public static final String EFX_USER_HOME_ESP_DIR = EFX_USER_HOME + "esp";

	/** The Constant EFX_OPENBCI_DIR. */
	public static final String EFX_OPENBCI_DIR = EFX_CONFIG_DIR + "esp.config";

	/** The Constant EFX_DOC_DIR. */
	public static final String EFX_DOC_DIR = EFX_CONFIG_DIR + "doc";

	public static final String EFX_USER_HOME_DOC_DIR = EFX_USER_HOME + "doc";
	
	public static final String EFX_CSS_DIR = EFX_CONFIG_DIR + "css";
	
	public static final String EFX_USER_HOME_CSS_DIR = EFX_USER_HOME + "css";

	/** The Constant EFX_SETTINGS_DIR. */
	public static final String EFX_SETTINGS_DIR = EFX_CONFIG_DIR;
	
	public static final String EFX_USER_HOME_SETTINGS_DIR = EFX_USER_HOME;

	/** The Constant EFX_LOGBACK_XML_DIR. */
	public static final String EFX_LOGBACK_XML_DIR = EFX_CONFIG_DIR;
}
