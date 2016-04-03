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
package net.sourceforge.entrainer.gui.jfx.trident;

import javafx.scene.paint.Color;

import org.pushingpixels.trident.TridentConfig;
import org.pushingpixels.trident.interpolator.PropertyInterpolator;

// TODO: Auto-generated Javadoc
/**
 * Interpolates a JavaFX {@link Color} using the Trident animation library.
 * 
 * @author burton
 * @see TridentConfig#addPropertyInterpolator(PropertyInterpolator)
 */
public class ColorPropertyInterpolator implements PropertyInterpolator<Color> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.trident.interpolator.PropertyInterpolator#
	 * getBasePropertyClass()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Class getBasePropertyClass() {
		return Color.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pushingpixels.trident.interpolator.PropertyInterpolator#interpolate
	 * (java.lang.Object, java.lang.Object, float)
	 */
	@Override
	public Color interpolate(Color from, Color to, float fraction) {
		try {
			double r = (to.getRed() - from.getRed()) * fraction;
			double g = (to.getGreen() - from.getGreen()) * fraction;
			double b = (to.getBlue() - from.getBlue()) * fraction;
			double a = (to.getOpacity() - from.getOpacity()) * fraction;

			return new Color(from.getRed() + r, from.getGreen() + g, from.getBlue() + b, from.getOpacity() + a);
		} catch (Throwable e) {
			e.printStackTrace();
			return to;
		}
	}

}
