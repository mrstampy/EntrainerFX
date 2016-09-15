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

import java.io.Serializable;

import javafx.scene.effect.Effect;

// TODO: Auto-generated Javadoc
/**
 * The Class CurrentEffect represents the effect to be applied to the chosen
 * controls.
 */
public class CurrentEffect implements Serializable {
  private static final long serialVersionUID = -6880730514210377656L;

  private boolean opacity;

  private boolean pulse;

  private Effect effect;

  /**
   * Instantiates a new current effect.
   *
   * @param opacity
   *          the opacity
   * @param pulse
   *          the pulse
   * @param effect
   *          the effect
   */
  public CurrentEffect(boolean opacity, boolean pulse, Effect effect) {
    this.opacity = opacity;
    this.pulse = pulse;
    this.effect = effect;
  }

  /**
   * Checks if is opacity.
   *
   * @return true, if is opacity
   */
  public boolean isOpacity() {
    return opacity;
  }

  /**
   * Gets the effect.
   *
   * @return the effect
   */
  public Effect getEffect() {
    return effect;
  }

  /**
   * Checks if is pulse.
   *
   * @return true, if is pulse
   */
  public boolean isPulse() {
    return pulse;
  }

}
