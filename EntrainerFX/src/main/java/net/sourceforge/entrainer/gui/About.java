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
package net.sourceforge.entrainer.gui;

import net.sourceforge.entrainer.Version;
import net.sourceforge.entrainer.guitools.GuiUtil;

// TODO: Auto-generated Javadoc
/**
 * Simple dialog to display information about {@link EntrainerFX}.
 * 
 * TODO extract html to separate html file.
 * 
 * @author burton
 *
 */
public class About extends InformationDialog implements Version {

	private static final long serialVersionUID = 1L;

	private static About instance;

	private About() {
		super("About Entrainer");
	}

	/**
	 * Instantiates and shows the dialog.
	 */
	public static void showAboutDialog() {
		if (instance == null) {
			instance = new About();
		}
		instance.pack();
		GuiUtil.centerOnScreen(instance);
		GuiUtil.addFadeOutInvisibleListener(instance, 500);
		GuiUtil.fadeIn(instance, 500);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.gui.InformationDialog#getContent()
	 */
	@Override
	protected String getContent() {
		StringBuffer buf = new StringBuffer("<b><center>Entrainer ");
		buf.append(VERSION);
		buf.append("</center></b>");

		buf.append("<br><b><u>Release Date:</u></b> ");
		buf.append(RELEASE);
		buf.append("<br><br><b><u>Entrainer Home Page:</u></b> http://entrainer.sourceforge.net/");
		buf.append("<br><b><u>Project Home Page:</u></b> https://sourceforge.net/projects/entrainer");

		buf.append("<br><br>Copyright Burton Alexander, 2008 - 2014");
		buf.append("<br><br>This program creates entrainment frequencies, allowing the user to control the base frequency, the entrainment frequency");
		buf.append("<br>and the amplitude of the sound waves.  It is best used with high quality headphones.");

		buf.append("<br><br>JSyn binaries provided under license from Mobileer Incorporated solely for use with Entrainer.");

		buf.append("<br><br>Written to scratch an entrainment itch.");

		buf.append("<br><br><b><u>WARNING WARNING WARNING!!!</u></b> Do not use if you suffer from epilepsy or any related medical conditions.");

		return buf.toString();
	}

}
