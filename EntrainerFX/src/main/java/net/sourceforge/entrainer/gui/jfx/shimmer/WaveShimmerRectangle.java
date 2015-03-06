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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

// TODO: Auto-generated Javadoc
/**
 * Creates a wave gradient shimmer effect.
 * 
 * @author burton
 */
public class WaveShimmerRectangle extends AbstractShimmer<LinearGradient> {

	/** The Constant NAME. */
	public static final String NAME = "Wave Gradient";

	/** The Constant CSS_ID. */
	public static final String CSS_ID = "shimmer-rectangle";

	private static BigDecimal NUM_STOPS = new BigDecimal(10);

	/**
	 * Instantiates a new wave shimmer rectangle.
	 */
	public WaveShimmerRectangle() {
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
		return new LinearGradient(0, 0, getWidth(), getHeight(), false, CycleMethod.NO_CYCLE,
				opacity == 0 ? createStops(opacity) : createStops(getP1().getStops()));
	}

	private List<Stop> createStops(double opacity) {
		List<Stop> to = new ArrayList<Stop>();

		for (int i = 0; i <= NUM_STOPS.intValue(); i++) {
			to.add(createStop(createDouble(i), opacity));
		}

		return to;
	}

	private List<Stop> createStops(List<Stop> from) {
		List<Stop> to = new ArrayList<Stop>();

		to.add(new Stop(0, generateColor(rand.nextDouble())));

		for (double i = 1; i <= NUM_STOPS.intValue(); i++) {
			to.add(new Stop(createDouble(i), from.get((int) i - 1).getColor()));
		}

		return to;
	}

	private double createDouble(double idx) {
		BigDecimal num = new BigDecimal(idx);

		double val = num.divide(NUM_STOPS, 6, RoundingMode.HALF_UP).doubleValue();

		return val < 1 ? val : 1;
	}

	private Stop createStop(double offset, double a) {
		return new Stop(offset, generateColor(a));
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
