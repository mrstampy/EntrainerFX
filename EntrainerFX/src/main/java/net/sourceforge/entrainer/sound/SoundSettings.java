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
package net.sourceforge.entrainer.sound;

// TODO: Auto-generated Javadoc
/**
 * Super interface for all sound-related implementations.
 * 
 * @author burton
 */
public interface SoundSettings {

	/**
	 * Gets the entrainment frequency.
	 *
	 * @return the entrainment frequency
	 */
	double getEntrainmentFrequency();

	/**
	 * Sets the entrainment frequency.
	 *
	 * @param d
	 *          the new entrainment frequency
	 */
	void setEntrainmentFrequency(double d);

	/**
	 * Sets the left frequency.
	 *
	 * @param d
	 *          the new left frequency
	 */
	void setLeftFrequency(double d);

	/**
	 * Gets the left frequency.
	 *
	 * @return the left frequency
	 */
	double getLeftFrequency();

	/**
	 * Sets the right frequency.
	 *
	 * @param d
	 *          the new right frequency
	 */
	void setRightFrequency(double d);

	/**
	 * Gets the right frequency.
	 *
	 * @return the right frequency
	 */
	double getRightFrequency();

	/**
	 * Sets the amplitude.
	 *
	 * @param d
	 *          the new amplitude
	 */
	void setAmplitude(double d);

	/**
	 * Sets the pink noise amplitude.
	 *
	 * @param d
	 *          the new pink noise amplitude
	 */
	void setPinkNoiseAmplitude(double d);

	/**
	 * Gets the pink noise amplitude.
	 *
	 * @return the pink noise amplitude
	 */
	double getPinkNoiseAmplitude();

	/**
	 * Sets the left amplitude.
	 *
	 * @param d
	 *          the new left amplitude
	 */
	void setLeftAmplitude(double d);

	/**
	 * Gets the left amplitude.
	 *
	 * @return the left amplitude
	 */
	double getLeftAmplitude();

	/**
	 * Sets the right amplitude.
	 *
	 * @param d
	 *          the new right amplitude
	 */
	void setRightAmplitude(double d);

	/**
	 * Gets the right amplitude.
	 *
	 * @return the right amplitude
	 */
	double getRightAmplitude();

	/**
	 * Sets the pink pan left amplitude.
	 *
	 * @param d
	 *          the new pink pan left amplitude
	 */
	void setPinkPanLeftAmplitude(double d);

	/**
	 * Sets the pink pan right amplitude.
	 *
	 * @param d
	 *          the new pink pan right amplitude
	 */
	void setPinkPanRightAmplitude(double d);

	/**
	 * Sets the pink pan amplitude.
	 *
	 * @param d
	 *          the new pink pan amplitude
	 */
	void setPinkPanAmplitude(double d);

	/**
	 * Gets the pink pan left amplitude.
	 *
	 * @return the pink pan left amplitude
	 */
	double getPinkPanLeftAmplitude();

	/**
	 * Gets the pink pan right amplitude.
	 *
	 * @return the pink pan right amplitude
	 */
	double getPinkPanRightAmplitude();

	/**
	 * Call this method to start the JSyn sound classes.
	 */
	void start();

	/**
	 * Call this method to stop the JSyn sound classes.
	 */
	void stop();

	/**
	 * Pause.
	 */
	void pause();

	/**
	 * Sets the paused.
	 *
	 * @param isPaused
	 *          the new paused
	 */
	void setPaused(boolean isPaused);

	/**
	 * Resume.
	 */
	void resume();

	/**
	 * Checks if is paused.
	 *
	 * @return true, if is paused
	 */
	boolean isPaused();

	/**
	 * Ensure subclasses set this appropriately from start() method calls.
	 *
	 * @return true, if is playing
	 */
	boolean isPlaying();

	/**
	 * Ensure subclasses set this appropriately from stop() method calls.
	 *
	 * @param isPlaying
	 *          the new playing
	 */
	void setPlaying(boolean isPlaying);

}
