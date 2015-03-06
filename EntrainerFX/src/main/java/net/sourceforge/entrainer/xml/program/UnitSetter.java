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
package net.sourceforge.entrainer.xml.program;

// TODO: Auto-generated Javadoc
/**
 * This interface allows access to either the start or the end values as
 * determined by the implementations. Adding a property change listener allows
 * objects to listen in on changes to the values.
 * 
 * @author burton
 *
 */
public interface UnitSetter {

	/**
	 * Sets the amplitude.
	 *
	 * @param d
	 *          the new amplitude
	 */
	public void setAmplitude(double d);

	/**
	 * Gets the amplitude.
	 *
	 * @return the amplitude
	 */
	public double getAmplitude();

	/**
	 * Sets the entrainment frequency.
	 *
	 * @param d
	 *          the new entrainment frequency
	 */
	public void setEntrainmentFrequency(double d);

	/**
	 * Gets the entrainment frequency.
	 *
	 * @return the entrainment frequency
	 */
	public double getEntrainmentFrequency();

	/**
	 * Sets the frequency.
	 *
	 * @param d
	 *          the new frequency
	 */
	public void setFrequency(double d);

	/**
	 * Gets the frequency.
	 *
	 * @return the frequency
	 */
	public double getFrequency();

	/**
	 * Sets the pink entrainer multiple.
	 *
	 * @param d
	 *          the new pink entrainer multiple
	 */
	public void setPinkEntrainerMultiple(double d);

	/**
	 * Gets the pink entrainer multiple.
	 *
	 * @return the pink entrainer multiple
	 */
	public double getPinkEntrainerMultiple();

	/**
	 * Sets the pink noise.
	 *
	 * @param d
	 *          the new pink noise
	 */
	public void setPinkNoise(double d);

	/**
	 * Gets the pink noise.
	 *
	 * @return the pink noise
	 */
	public double getPinkNoise();

	/**
	 * Sets the pink pan amplitude.
	 *
	 * @param d
	 *          the new pink pan amplitude
	 */
	public void setPinkPanAmplitude(double d);

	/**
	 * Gets the pink pan amplitude.
	 *
	 * @return the pink pan amplitude
	 */
	public double getPinkPanAmplitude();

	/**
	 * Gets the unit.
	 *
	 * @return the unit
	 */
	public EntrainerProgramUnit getUnit();
}
