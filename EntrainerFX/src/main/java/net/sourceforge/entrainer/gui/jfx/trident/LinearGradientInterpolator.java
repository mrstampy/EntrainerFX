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
package net.sourceforge.entrainer.gui.jfx.trident;

import java.util.List;

import org.pushingpixels.trident.TridentConfig;
import org.pushingpixels.trident.interpolator.PropertyInterpolator;

import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

// TODO: Auto-generated Javadoc
/**
 * Interpolates a JavaFX {@link LinearGradient} using the Trident animation
 * library.
 * 
 * @author burton
 * @see TridentConfig#addPropertyInterpolator(PropertyInterpolator)
 */
public class LinearGradientInterpolator extends AbstractGradientInterpolator
    implements PropertyInterpolator<LinearGradient> {

  /*
   * (non-Javadoc)
   * 
   * @see org.pushingpixels.trident.interpolator.PropertyInterpolator#
   * getBasePropertyClass()
   */
  @SuppressWarnings("rawtypes")
  @Override
  public Class getBasePropertyClass() {
    return LinearGradient.class;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.pushingpixels.trident.interpolator.PropertyInterpolator#interpolate
   * (java.lang.Object, java.lang.Object, float)
   */
  @Override
  public LinearGradient interpolate(LinearGradient lg1, LinearGradient lg2, float f) {
    List<Stop> startStops = lg1.getStops();
    List<Stop> endStops = lg2.getStops();

    validate(lg1.isProportional(),
        lg2.isProportional(),
        lg1.getCycleMethod(),
        lg2.getCycleMethod(),
        startStops,
        endStops);

    double startX = lg1.getStartX() + ((lg2.getStartX() - lg1.getStartX()) * f);
    double startY = lg1.getStartY() + ((lg2.getStartY() - lg1.getStartY()) * f);
    double endX = lg1.getEndX() + ((lg2.getEndX() - lg1.getEndX()) * f);
    double endY = lg1.getEndY() + ((lg2.getEndY() - lg1.getEndY()) * f);

    List<Stop> newStops = interpolateStops(f, startStops, endStops);

    return new LinearGradient(startX, startY, endX, endY, lg1.isProportional(), lg1.getCycleMethod(), newStops);
  }

}
