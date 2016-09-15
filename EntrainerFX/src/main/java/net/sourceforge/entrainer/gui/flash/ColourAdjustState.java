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
package net.sourceforge.entrainer.gui.flash;

import java.util.Random;

import javafx.scene.effect.ColorAdjust;

// TODO: Auto-generated Javadoc
/**
 * The Class ColourAdjustState.
 */
class ColourAdjustState {

  /** The default colour adjust. */
  static ColorAdjust DEFAULT_COLOUR_ADJUST = new ColorAdjust();

  private Random rand = new Random(System.nanoTime());
  private boolean colourAdjusting;

  private ColorAdjust colourAdjust = DEFAULT_COLOUR_ADJUST;

  /**
   * Instantiates a new colour adjust state.
   */
  ColourAdjustState() {
  }

  /**
   * Checks if is colour adjusting.
   *
   * @return true, if is colour adjusting
   */
  boolean isColourAdjusting() {
    return colourAdjusting;
  }

  /**
   * Sets the colour adjusting.
   *
   * @param colourAdjusting
   *          the new colour adjusting
   */
  void setColourAdjusting(boolean colourAdjusting) {
    this.colourAdjusting = colourAdjusting;
  }

  /**
   * Gets the color adjust.
   *
   * @return the color adjust
   */
  ColorAdjust getColorAdjust() {
    return colourAdjust;
  }

  /**
   * Evaluate for pulse.
   *
   * @param b
   *          the b
   */
  void evaluateForPulse(boolean b) {
    if (!colourAdjusting || !b) {
      colourAdjust = DEFAULT_COLOUR_ADJUST;
      return;
    }

    colourAdjust = randomColourAdjust();
  }

  private ColorAdjust randomColourAdjust() {
    return new ColorAdjust(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
  }
}
