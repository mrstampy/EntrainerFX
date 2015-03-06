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
package net.sourceforge.entrainer.sound;

// TODO: Auto-generated Javadoc
/**
 * Interface for all sound interval implementations.
 *
 * @author burton
 */
public interface SoundInterval extends SoundSettings {

	/**
	 * Gets the display string.
	 *
	 * @return the display string
	 */
	String getDisplayString();

	/**
	 * Checks if is interval display string.
	 *
	 * @param displayString
	 *          the display string
	 * @return true, if is interval display string
	 */
	boolean isIntervalDisplayString(String displayString);

	/**
	 * Gets the interval numerator.
	 *
	 * @return the interval numerator
	 */
	int getIntervalNumerator();

	/**
	 * Gets the interval numerator.
	 *
	 * @param displayString
	 *          the display string
	 * @return the interval numerator
	 */
	int getIntervalNumerator(String displayString);

	/**
	 * Gets the interval denominator.
	 *
	 * @param displayString
	 *          the display string
	 * @return the interval denominator
	 */
	int getIntervalDenominator(String displayString);

	/**
	 * Sets the interval numerator.
	 *
	 * @param intervalNumerator
	 *          the new interval numerator
	 */
	void setIntervalNumerator(int intervalNumerator);

	/**
	 * Gets the interval denominator.
	 *
	 * @return the interval denominator
	 */
	int getIntervalDenominator();

	/**
	 * Sets the interval denominator.
	 *
	 * @param intervalDenominator
	 *          the new interval denominator
	 */
	void setIntervalDenominator(int intervalDenominator);

	/**
	 * Gets the interval.
	 *
	 * @return the interval
	 */
	double getInterval();

	/**
	 * Checks if is interval.
	 *
	 * @param num
	 *          the num
	 * @param denom
	 *          the denom
	 * @return true, if is interval
	 */
	boolean isInterval(int num, int denom);

}
