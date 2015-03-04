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

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

// TODO: Auto-generated Javadoc
/**
 * Creates a radial shimmer effect.
 * 
 * @author burton
 */
public class RadialShimmerRectangle extends AbstractShimmer<RadialGradient> {

	/** The Constant CSS_ID. */
	public static final String CSS_ID = "shimmer-rectangle";
	private static int NUM_SAVED_STOPS = 9;

	private int angle;

	/**
	 * Instantiates a new radial shimmer rectangle.
	 */
	public RadialShimmerRectangle() {
		super();
		setId(CSS_ID);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.gui.jfx.shimmer.AbstractShimmer#createNewPaint(double)
	 */
	@Override
	protected RadialGradient createNewPaint(double opacity) {
		return new RadialGradient(getAngle(), 0.20, 0.5, 0.5, 0.5, true, CycleMethod.REFLECT, createStops(opacity));
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.gui.jfx.shimmer.AbstractShimmer#toString()
	 */
	public String toString() {
		return "Radial Gradient";
	}

	private int getAngle() {
		int current = angle;

		angle += 10;
		if (angle > 359) angle = angle - 360;

		return current;
	}

	private List<Stop> createStops(double a) {
		List<Stop> list = new ArrayList<Stop>();
		for (double i = 0; i < NUM_SAVED_STOPS + 1; i++) {
			list.add(createStop(i / NUM_SAVED_STOPS, a));
		}

		return list;
	}

	private Stop createStop(double offset, double a) {
		return new Stop(offset, generateColor(a));
	}
}
