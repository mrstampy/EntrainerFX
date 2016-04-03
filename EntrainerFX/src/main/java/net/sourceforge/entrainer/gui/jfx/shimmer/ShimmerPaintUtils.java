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
package net.sourceforge.entrainer.gui.jfx.shimmer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

// TODO: Auto-generated Javadoc
/**
 * The Class ShimmerPaintUtils.
 */
public class ShimmerPaintUtils {
	private static Random rand = new Random(System.nanoTime());

	private static int NUM_SAVED_STOPS = 9;

	private static BigDecimal NUM_WAVE_STOPS = new BigDecimal(10);

	/**
	 * Creates the new paint.
	 *
	 * @param opacity
	 *          the opacity
	 * @param width
	 *          the width
	 * @param height
	 *          the height
	 * @return the linear gradient
	 */
	public static LinearGradient createLinearGradient(double opacity, double width, double height) {
		Stop stop0 = createStop(0, opacity);
		Color inverted = stop0.getColor().invert();
		Color c2 = new Color(inverted.getRed(), inverted.getGreen(), inverted.getBlue(), inverted.getOpacity() == 0 ? 0
				: rand.nextDouble());
		Stop stop1 = new Stop(1, c2);
		return new LinearGradient(0, 0, width, height, false, CycleMethod.NO_CYCLE, stop0, stop1);
	}

	/**
	 * Creates the new paint.
	 *
	 * @param opacity
	 *          the opacity
	 * @param angle
	 *          the angle
	 * @return the radial gradient
	 */
	public static RadialGradient createRadialGradient(double opacity, double angle) {
		return new RadialGradient(angle, 0.20, 0.5, 0.5, 0.5, true, CycleMethod.REFLECT, createStops(opacity));
	}

	/**
	 * Generates a random colour using the specified opacity.
	 *
	 * @param a
	 *          the alpha (opacity) value.
	 * @return the color
	 */
	public static Color generateColor(double a) {
		return new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), a);
	}

	/**
	 * Creates the wave stop.
	 *
	 * @param opacity
	 *          the opacity
	 * @param width
	 *          the width
	 * @param height
	 *          the height
	 * @param stops
	 *          the stops
	 * @return the linear gradient
	 */
	public static LinearGradient createWaveStop(double opacity, double width, double height, List<Stop> stops) {
		return new LinearGradient(0, 0, width, height, false, CycleMethod.NO_CYCLE, opacity == 0 ? createWaveStops(opacity)
				: createWaveStops(stops));
	}

	private static List<Stop> createWaveStops(double opacity) {
		List<Stop> to = new ArrayList<Stop>();

		for (int i = 0; i <= NUM_WAVE_STOPS.intValue(); i++) {
			to.add(createStop(createWaveDouble(i), opacity));
		}

		return to;
	}

	private static List<Stop> createWaveStops(List<Stop> from) {
		List<Stop> to = new ArrayList<Stop>();

		to.add(new Stop(0, ShimmerPaintUtils.generateColor(rand.nextDouble())));

		for (double i = 1; i <= NUM_WAVE_STOPS.intValue(); i++) {
			to.add(new Stop(createWaveDouble(i), from.get((int) i - 1).getColor()));
		}

		return to;
	}

	private static double createWaveDouble(double idx) {
		BigDecimal num = new BigDecimal(idx);

		double val = num.divide(NUM_WAVE_STOPS, 6, RoundingMode.HALF_UP).doubleValue();

		return val < 1 ? val : 1;
	}

	private static Stop createStop(double offset, double a) {
		return new Stop(offset, generateColor(a));
	}

	private static List<Stop> createStops(double a) {
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

	private static List<Color> getColourList(double a) {
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

	private ShimmerPaintUtils() {
	}

}
