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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * Dialog to display the LICENSE.txt file.
 * 
 * @author burton
 *
 */
public class License extends DialogPane {

	private static String NOT_FOUND = "License not found.  Released under the GPL";

	private TextArea area = new TextArea();

	/**
	 * Instantiates a new license.
	 */
	public License() {
		area.setPrefColumnCount(55);
		area.setPrefRowCount(50);
		area.setText(getLicense());

		getButtonTypes().add(ButtonType.OK);

		setContent(area);
	}

	private String getLicense() {
		Optional<File> license = Utils.getLicenseFile();
		if (!license.isPresent()) {
			return NOT_FOUND;
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(license.get()));

			String line = reader.readLine();

			StringBuffer buf = new StringBuffer();
			while (line != null) {
				buf.append(line);
				buf.append("\n");
				line = reader.readLine();
			}

			return buf.toString();
		} catch (Exception e) {
			GuiUtil.handleProblem(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}

		return NOT_FOUND;
	}

}
