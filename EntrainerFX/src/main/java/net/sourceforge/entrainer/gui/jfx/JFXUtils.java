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
package net.sourceforge.entrainer.gui.jfx;

import java.io.File;
import java.net.URI;

import javafx.scene.paint.Color;

// TODO: Auto-generated Javadoc
/**
 * The Class JFXUtils.
 */
public class JFXUtils {

	/**
	 * To jfx color.
	 *
	 * @param awt the awt
	 * @return the color
	 */
	public static Color toJFXColor(java.awt.Color awt) {
		if (awt == null) return null;

		double r = ((double) awt.getRed()) / 255;
		double g = ((double) awt.getGreen()) / 255;
		double b = ((double) awt.getBlue()) / 255;
		double a = ((double) awt.getAlpha()) / 255;

		return new Color(r, g, b, a);
	}

	/**
	 * From jfx color.
	 *
	 * @param c the c
	 * @return the java.awt. color
	 */
	public static java.awt.Color fromJFXColor(Color c) {
		if (c == null) return null;

		return new java.awt.Color((float) c.getRed(), (float) c.getGreen(), (float) c.getBlue(), (float) c.getOpacity());
	}
	
	/**
	 * Gets the entrainer css.
	 *
	 * @return the entrainer css
	 */
	public static URI getEntrainerCSS() {
		File css = new File("css/entrainer.css");
		
		return css.exists() ? css.toURI() : null;
	}

}
