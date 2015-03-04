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
package net.sourceforge.entrainer.socket;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

// TODO: Auto-generated Javadoc
/**
 * The Class FlashColour.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class FlashColour {

	@XmlElement
	private int red;

	@XmlElement
	private int green;

	@XmlElement
	private int blue;

	@XmlElement
	private int alpha;

	/**
	 * Gets the red.
	 *
	 * @return the red
	 */
	public int getRed() {
		return red;
	}

	/**
	 * Sets the red.
	 *
	 * @param red the new red
	 */
	public void setRed(int red) {
		this.red = red;
	}

	/**
	 * Gets the green.
	 *
	 * @return the green
	 */
	public int getGreen() {
		return green;
	}

	/**
	 * Sets the green.
	 *
	 * @param green the new green
	 */
	public void setGreen(int green) {
		this.green = green;
	}

	/**
	 * Gets the blue.
	 *
	 * @return the blue
	 */
	public int getBlue() {
		return blue;
	}

	/**
	 * Sets the blue.
	 *
	 * @param blue the new blue
	 */
	public void setBlue(int blue) {
		this.blue = blue;
	}

	/**
	 * Gets the alpha.
	 *
	 * @return the alpha
	 */
	public int getAlpha() {
		return alpha;
	}

	/**
	 * Sets the alpha.
	 *
	 * @param alpha the new alpha
	 */
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	@JsonIgnore
	public Color getColor() {
		return new Color(getRed(), getGreen(), getBlue(), getAlpha());
	}

	/**
	 * Sets the color.
	 *
	 * @param c the new color
	 */
	@JsonIgnore
	public void setColor(Color c) {
		if (c == null) return;
		setRed(c.getRed());
		setGreen(c.getGreen());
		setBlue(c.getBlue());
		setAlpha(c.getAlpha());
	}

}
