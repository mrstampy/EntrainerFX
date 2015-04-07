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
package net.sourceforge.entrainer.gui;

import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextAlignment;
import net.sourceforge.entrainer.Version;
import net.sourceforge.entrainer.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * Simple dialog to display information about {@link EntrainerFX}.
 * 
 * @author burton
 *
 */
public class About extends DialogPane implements Version {
	private Label label;

	public About() {
		init();
	}

	private void init() {
		label = new Label();
		label.setText(getHtmlText());
		label.setTextAlignment(TextAlignment.CENTER);
		
		getButtonTypes().add(ButtonType.OK);
		
		setContent(label);
		
		label.setOnMouseClicked(e -> showDoco(e));
	}

	private void showDoco(MouseEvent e) {
		if (!(e.isMetaDown() && e.getClickCount() == 1)) return;

		Utils.openLocalDocumentation("index.html");
	}

	protected String getHtmlText() {
		StringBuffer buf = new StringBuffer("Entrainer ");
		buf.append(VERSION);

		buf.append("\nRelease Date: ");
		buf.append(RELEASE);
		buf.append("\n\nEntrainer Home Page: http://entrainer.sourceforge.net/");
		buf.append("\nProject Home Page: https://sourceforge.net/projects/entrainer");

		buf.append("\n\nCopyright Burton Alexander, 2008 - 2015");
		buf.append("\n\nThis program creates entrainment frequencies, allowing the user to control the base frequency, the entrainment frequency");
		buf.append("\nand the amplitude of the sound waves.  It is best used with high quality headphones.");

		buf.append("\n\nJSyn binaries provided under license from Mobileer Incorporated solely for use with Entrainer.");

		buf.append("\n\nWritten to scratch an entrainment itch.");

		buf.append("\n\nWARNING WARNING WARNING!!! Do not use if you suffer from epilepsy or any related medical conditions.");

		return buf.toString();
	}

}
