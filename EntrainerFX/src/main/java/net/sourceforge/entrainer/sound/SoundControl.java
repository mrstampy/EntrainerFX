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

import java.io.File;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Interface for sound control classes.
 * 
 * @author burton
 *
 */
public interface SoundControl extends SoundSettings {
	
	/**
	 * Adds the interval control.
	 *
	 * @param displayString the display string
	 */
	void addIntervalControl(String displayString);
	
	/**
	 * Removes the interval control.
	 *
	 * @param displayString the display string
	 */
	void removeIntervalControl(String displayString);
	
	/**
	 * Gets the intervals.
	 *
	 * @return the intervals
	 */
	List<String> getIntervals();
	
	/**
	 * Adds the interval control.
	 *
	 * @param intervalNumerator the interval numerator
	 * @param intervalDenominator the interval denominator
	 */
	void addIntervalControl(int intervalNumerator, int intervalDenominator);
	
	/**
	 * Removes the interval control.
	 *
	 * @param intervalNumerator the interval numerator
	 * @param intervalDenominator the interval denominator
	 */
	void removeIntervalControl(int intervalNumerator, int intervalDenominator);
	
	/**
	 * Sets the wav file.
	 *
	 * @param wavFile the new wav file
	 */
	void setWavFile(File wavFile);
	
	/**
	 * Teardown.
	 */
	void teardown();
	
	/**
	 * Gets the wav file.
	 *
	 * @return the wav file
	 */
	File getWavFile();
	
	/**
	 * Set to true to indicate the sounds should be recorded.
	 *
	 * @param isRecord the new record
	 */
	void setRecord(boolean isRecord);
	
	/**
	 * Returns true if sounds should be recorded to the file set in
	 * setWavFile implementation.
	 *
	 * @return true, if is record
	 */
	boolean isRecord();
	
	/**
	 * Exit.
	 */
	void exit();

}