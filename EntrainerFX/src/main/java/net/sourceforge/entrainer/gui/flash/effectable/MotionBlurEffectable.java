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
package net.sourceforge.entrainer.gui.flash.effectable;

import javafx.scene.effect.MotionBlur;

// TODO: Auto-generated Javadoc
/**
 * The Class MotionBlurEffectable.
 */
public class MotionBlurEffectable extends MotionBlur implements Effectable {

  /**
   * Instantiates a new motion blur effectable.
   */
  public MotionBlurEffectable() {
    super();
  }

  /**
   * Instantiates a new motion blur effectable.
   *
   * @param angle
   *          the angle
   * @param radius
   *          the radius
   */
  public MotionBlurEffectable(double angle, double radius) {
    super(angle, radius);
  }

}
