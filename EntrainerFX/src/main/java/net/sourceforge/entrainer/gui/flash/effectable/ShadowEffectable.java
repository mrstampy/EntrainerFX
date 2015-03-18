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
package net.sourceforge.entrainer.gui.flash.effectable;

import javafx.scene.effect.BlurType;
import javafx.scene.effect.Shadow;
import javafx.scene.paint.Color;

// TODO: Auto-generated Javadoc
/**
 * The Class ShadowEffectable.
 */
public class ShadowEffectable extends Shadow implements Effectable {

	/**
	 * Instantiates a new shadow effectable.
	 */
	public ShadowEffectable() {
		super();
	}

	/**
	 * Instantiates a new shadow effectable.
	 *
	 * @param radius the radius
	 * @param color the color
	 */
	public ShadowEffectable(double radius, Color color) {
		super(radius, color);
	}

	/**
	 * Instantiates a new shadow effectable.
	 *
	 * @param blurType the blur type
	 * @param color the color
	 * @param radius the radius
	 */
	public ShadowEffectable(BlurType blurType, Color color, double radius) {
		super(blurType, color, radius);
	}

}
