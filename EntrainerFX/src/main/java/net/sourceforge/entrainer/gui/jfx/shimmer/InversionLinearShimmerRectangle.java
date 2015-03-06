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
package net.sourceforge.entrainer.gui.jfx.shimmer;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

// TODO: Auto-generated Javadoc
/**
 * Linear gradient shimmer using inverted colours.
 * 
 * @author burton
 *
 */
public class InversionLinearShimmerRectangle extends AbstractShimmer<LinearGradient> {

	/** The Constant CSS_ID. */
	public static final String CSS_ID = "shimmer-rectangle";

	/**
	 * Instantiates a new inversion linear shimmer rectangle.
	 */
	public InversionLinearShimmerRectangle() {
		super();
		setId(CSS_ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.gui.jfx.shimmer.AbstractShimmer#toString()
	 */
	@Override
	public String toString() {
		return "Inversion Linear Gradient";
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
		Stop stop0 = createStop(0, opacity);
		Color inverted = stop0.getColor().invert();
		Color c2 = new Color(inverted.getRed(), inverted.getGreen(), inverted.getBlue(), inverted.getOpacity() == 0 ? 0
				: rand.nextDouble());
		Stop stop1 = new Stop(1, c2);
		return new LinearGradient(0, 0, getWidth(), getHeight(), false, CycleMethod.NO_CYCLE, stop0, stop1);
	}

	private Stop createStop(double offset, double a) {
		return new Stop(offset, generateColor(a));
	}

}
