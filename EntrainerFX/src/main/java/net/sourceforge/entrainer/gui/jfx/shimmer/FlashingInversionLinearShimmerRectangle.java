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

import javafx.scene.paint.LinearGradient;

// TODO: Auto-generated Javadoc
/**
 * The Class FlashingInversionLinearShimmerRectangle.
 */
public class FlashingInversionLinearShimmerRectangle extends AbstractFlashingShimmer<LinearGradient> {

	/**
	 * Instantiates a new flashing inversion linear shimmer rectangle.
	 */
	public FlashingInversionLinearShimmerRectangle() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.gui.jfx.shimmer.AbstractShimmer#toString()
	 */
	@Override
	public String toString() {
		return "Flashing Inversion Linear Gradient";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.gui.jfx.shimmer.AbstractShimmer#createNewPaint
	 * (double)
	 */
	@Override
	protected LinearGradient createNewPaint(double opacity) {
		return ShimmerPaintUtils.createLinearGradient(opacity, getWidth(), getHeight());
	}

}
