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
 * Creates a linear gradient shimmer effect, replacing the Swing-based
 * {@link ShimmerPane} and {@link ShimmerRectangle}.
 * 
 * @author burton
 */
@SuppressWarnings("deprecation")
public class FlashingLinearShimmerRectangle extends AbstractFlashingShimmer<LinearGradient> {

	/** The Constant NAME. */
	public static final String NAME = "Flashing Linear Gradient";

	/** The Constant CSS_ID. */
	public static final String CSS_ID = "shimmer-rectangle";

	/**
	 * Instantiates a new flashing linear shimmer rectangle.
	 */
	public FlashingLinearShimmerRectangle() {
		super();
		setId(CSS_ID);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.gui.jfx.shimmer.AbstractShimmer#toString()
	 */
	public String toString() {
		return NAME;
	}

}
