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
package net.sourceforge.entrainer.gui.jfx.trident;

import java.util.List;

import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import org.pushingpixels.trident.TridentConfig;
import org.pushingpixels.trident.interpolator.PropertyInterpolator;

// TODO: Auto-generated Javadoc
/**
 * Interpolates a JavaFX {@link RadialGradient} using the Trident animation
 * library. This implementation only works for clockwise (positive)
 * interpolations of the {@link RadialGradient#getFocusAngle()}
 * 
 * @author burton
 * @see TridentConfig#addPropertyInterpolator(PropertyInterpolator)
 */
public class RadialGradientInterpolator extends AbstractGradientInterpolator implements
		PropertyInterpolator<RadialGradient> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.trident.interpolator.PropertyInterpolator#
	 * getBasePropertyClass()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Class getBasePropertyClass() {
		return RadialGradient.class;
	}

	/**
	 * Only works for clockwise focus angle rotations!.
	 *
	 * @param rg1
	 *          the rg1
	 * @param rg2
	 *          the rg2
	 * @param f
	 *          the f
	 * @return the radial gradient
	 */
	@Override
	public RadialGradient interpolate(RadialGradient rg1, RadialGradient rg2, float f) {
		List<Stop> startStops = rg1.getStops();
		List<Stop> endStops = rg2.getStops();

		validate(rg1.isProportional(),
				rg2.isProportional(),
				rg1.getCycleMethod(),
				rg2.getCycleMethod(),
				startStops,
				endStops);

		List<Stop> newStops = interpolateStops(f, startStops, endStops);

		double centerX = rg1.getCenterX() + ((rg2.getCenterX() - rg1.getCenterX()) * f);
		double centerY = rg1.getCenterY() + ((rg2.getCenterY() - rg1.getCenterY()) * f);

		double focus1 = rg1.getFocusAngle();
		double focus2 = rg2.getFocusAngle();
		if (focus2 < focus1) focus1 = focus1 - 360;

		double focusAngle = focus1 + ((focus2 - focus1) * f);
		double focusDistance = rg1.getFocusDistance() + ((rg2.getFocusDistance() - rg1.getFocusDistance()) * f);
		double radius = rg1.getRadius() + ((rg2.getRadius() - rg1.getRadius()) * f);

		RadialGradient rg = new RadialGradient(focusAngle, focusDistance, centerX, centerY, radius, rg1.isProportional(),
				rg1.getCycleMethod(), newStops);

		return rg;
	}

}
