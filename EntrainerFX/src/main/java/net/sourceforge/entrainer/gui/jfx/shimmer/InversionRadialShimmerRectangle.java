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

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

// TODO: Auto-generated Javadoc
/**
 * Creates a radial shimmer effect. Adjacent colours are inverted.
 * 
 * @author burton
 */
public class InversionRadialShimmerRectangle extends AbstractShimmer<RadialGradient> {

	/** The Constant CSS_ID. */
	public static final String CSS_ID = "shimmer-rectangle";
	private static int NUM_SAVED_STOPS = 9;

	private int angle;

	/**
	 * Instantiates a new inversion radial shimmer rectangle.
	 */
	public InversionRadialShimmerRectangle() {
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
	protected RadialGradient createNewPaint(double opacity) {
		return new RadialGradient(getAngle(), 0.20, 0.5, 0.5, 0.5, true, CycleMethod.REFLECT, createStops(opacity));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.gui.jfx.shimmer.AbstractShimmer#toString()
	 */
	public String toString() {
		return "Inversion Radial Gradient";
	}

	private int getAngle() {
		int current = angle;

		angle += 10;
		if (angle > 359) angle = angle - 360;

		return current;
	}

	private List<Stop> createStops(double a) {
		List<Stop> list = new ArrayList<Stop>();

		List<Color> colours = getColourList(a);
		int half = colours.size() / 2;

		for (double i = 0; i < colours.size(); i++) {
			list.add(new Stop(i / NUM_SAVED_STOPS, colours.get((int) i)));
		}

		if (half < colours.size()) {
			list.add(new Stop(half * 2 / NUM_SAVED_STOPS, colours.get(half * 2)));
		}

		return list;
	}

	private List<Color> getColourList(double a) {
		int numInversions = NUM_SAVED_STOPS / 2;

		List<Color> list = new ArrayList<>();
		for (int i = 0; i < numInversions; i++) {
			list.add(generateColor(a));
		}

		for (int i = numInversions - 1; i >= 0; i--) {
			Color c = list.get(i);
			list.add(c.invert());
		}

		if (numInversions < NUM_SAVED_STOPS) {
			list.add(generateColor(a));
		}

		return list;
	}
}
