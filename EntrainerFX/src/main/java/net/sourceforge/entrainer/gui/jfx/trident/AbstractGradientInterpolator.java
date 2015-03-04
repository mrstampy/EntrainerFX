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
package net.sourceforge.entrainer.gui.jfx.trident;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

// TODO: Auto-generated Javadoc
/**
 * Abstract superclass for interpolation of gradient based {@link Paint}
 * implementations.
 * 
 * @author burton
 */
public abstract class AbstractGradientInterpolator {

	private ColorPropertyInterpolator helper = new ColorPropertyInterpolator();

	/**
	 * Because of the nature of gradients, certain properties must be equal in
	 * order to perform a meaningful interpolation. Should any of these properties
	 * differ an {@link UnsupportedOperationException} will be thrown.
	 *
	 * @param prop1 the prop1
	 * @param prop2 the prop2
	 * @param cyc1 the cyc1
	 * @param cyc2 the cyc2
	 * @param st1 must be the same size as st2
	 * @param st2 must be the same size as st1
	 */
	protected void validate(boolean prop1, boolean prop2, CycleMethod cyc1, CycleMethod cyc2, List<Stop> st1,
			List<Stop> st2) {
		if (st1.size() != st2.size()) {
			throw new UnsupportedOperationException("Number of stops must be the same to animate Gradients");
		}

		if (cyc1 != cyc2) {
			throw new UnsupportedOperationException("Cycle method must be the same to animate Gradients");
		}

		if (prop1 != prop2) {
			throw new UnsupportedOperationException("Proportional setting must be the same to animate Gradients");
		}
	}

	/**
	 * Interpolate stops.
	 *
	 * @param f the f
	 * @param startStops the start stops
	 * @param endStops the end stops
	 * @return the list
	 */
	protected List<Stop> interpolateStops(float f, List<Stop> startStops, List<Stop> endStops) {
		List<Stop> newStops = new ArrayList<Stop>();
		for (int i = 0; i < startStops.size(); i++) {
			newStops.add(interpolateStop(startStops.get(i), endStops.get(i), f));
		}

		return newStops;
	}

	private Stop interpolateStop(Stop startStop, Stop endStop, float f) {
		double offset = startStop.getOffset() + ((endStop.getOffset() - startStop.getOffset()) * f);

		Color c = helper.interpolate(startStop.getColor(), endStop.getColor(), f);

		return new Stop(offset, c);
	}

}
