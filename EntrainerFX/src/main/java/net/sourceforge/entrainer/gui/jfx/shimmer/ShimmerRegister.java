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

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Convenience class to create singleton instances of all
 * {@link AbstractShimmer}s.
 * 
 * @author burton
 */
public class ShimmerRegister {

  private static List<AbstractShimmer<?>> shimmers = new ArrayList<AbstractShimmer<?>>();

  static {
    shimmers.add(new ColorShimmerRectangle());
    shimmers.add(new LinearShimmerRectangle());
    shimmers.add(new RadialShimmerRectangle());
    shimmers.add(new InversionLinearShimmerRectangle());
    shimmers.add(new InversionRadialShimmerRectangle());
    shimmers.add(new WaveShimmerRectangle());
  }

  /**
   * Gets the shimmers.
   *
   * @return the shimmers
   */
  public static List<AbstractShimmer<?>> getShimmers() {
    return new ArrayList<AbstractShimmer<?>>(shimmers);
  }

  /**
   * Returns a shimmer instance based upon the
   * {@link AbstractShimmer#toString()} implementation.
   *
   * @param name
   *          the name
   * @return the shimmer
   */
  public static AbstractShimmer<?> getShimmer(String name) {
    for (AbstractShimmer<?> shimmer : getShimmers()) {
      if (shimmer.toString().equals(name)) return shimmer;
    }

    return null;
  }

  /**
   * Gets a list of all shimmer names.
   *
   * @return the shimmer names
   */
  public static List<String> getShimmerNames() {
    List<String> names = new ArrayList<String>();

    for (AbstractShimmer<?> shimmer : getShimmers()) {
      names.add(shimmer.toString());
    }

    return names;
  }

}
