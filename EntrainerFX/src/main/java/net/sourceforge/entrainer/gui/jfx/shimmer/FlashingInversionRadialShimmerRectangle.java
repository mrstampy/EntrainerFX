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
package net.sourceforge.entrainer.gui.jfx.shimmer;

import javafx.scene.paint.RadialGradient;

// TODO: Auto-generated Javadoc
/**
 * The Class FlashingInversionRadialShimmerRectangle.
 */
public class FlashingInversionRadialShimmerRectangle extends AbstractFlashingShimmer<RadialGradient> {
	private static int NUM_SAVED_STOPS = 13;

	private int angle;

	/**
	 * Instantiates a new flashing inversion radial shimmer rectangle.
	 */
	public FlashingInversionRadialShimmerRectangle() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.gui.jfx.shimmer.AbstractShimmer#toString()
	 */
	@Override
	public String toString() {
		return "Flashing Inversion Radial Gradient";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.gui.jfx.shimmer.AbstractShimmer#createNewPaint
	 * (double)
	 */
	@Override
	protected RadialGradient createNewPaint(double opacity) {
		return ShimmerPaintUtils.createRadialGradient(opacity, getAngle());
	}

	private int getAngle() {
		int current = angle;

		angle += 10;
		if (angle > 359) angle = angle - 360;

		return current;
	}

}
