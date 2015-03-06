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
package net.sourceforge.entrainer.neuroph;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Simple functions to perform on outputs.
 */
public class NeurophUtil {

	/**
	 * Weighted moving average.
	 *
	 * @param wma
	 *          the wma
	 * @param current
	 *          the current
	 * @param size
	 *          the size
	 * @return the double
	 */
	public static double weightedMovingAverage(List<Double> wma, double current, int size) {
		wma.add(0, current);
		while (wma.size() > size) {
			wma.remove(wma.size() - 1);
		}

		return weightedMovingAverage(wma);
	}

	/**
	 * Weighted moving average.
	 *
	 * @param wma
	 *          the wma
	 * @return the double
	 */
	public static double weightedMovingAverage(List<Double> wma) {
		double bd = 0;
		int j = 0;
		for (int i = wma.size(); i > 0; i--) {
			Double val = wma.get(wma.size() - i);
			bd = bd + (val.doubleValue() * i);
			j += i;
		}

		bd = new BigDecimal(bd).divide(new BigDecimal(j), 5, RoundingMode.HALF_UP).doubleValue();

		return bd;
	}

	/**
	 * Normalize.
	 *
	 * @param raw
	 *          the raw
	 * @return the list
	 */
	public static List<Double> normalize(List<Double> raw) {
		List<Double> normalized = new ArrayList<Double>();

		BigDecimal max = getMax(raw);
		for (Double d : raw) {
			normalized.add(new BigDecimal(d).divide(max, 3, RoundingMode.HALF_UP).doubleValue());
		}

		return normalized;
	}

	private static BigDecimal getMax(List<Double> list) {
		double d = Double.MIN_VALUE;

		for (Double dbl : list) {
			d = Math.max(d, dbl.doubleValue());
		}

		return new BigDecimal(d);
	}

	private NeurophUtil() {

	}

}
