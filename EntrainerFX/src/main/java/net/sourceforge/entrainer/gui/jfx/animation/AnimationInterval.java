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
package net.sourceforge.entrainer.gui.jfx.animation;

import net.sourceforge.entrainer.sound.AbstractSoundInterval;

// TODO: Auto-generated Javadoc
/**
 * Blank implementation of AbstractSoundInterval for animations.
 * 
 * @author burton
 *
 */
public class AnimationInterval extends AbstractSoundInterval {

	/**
	 * Instantiates a new animation interval.
	 *
	 * @param numerator
	 *          the numerator
	 * @param denominator
	 *          the denominator
	 */
	public AnimationInterval(int numerator, int denominator) {
		super(numerator, denominator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.sound.SoundInterval#getInterval()
	 */
	public double getInterval() {
		return ((double) getIntervalNumerator()) / ((double) getIntervalDenominator());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.sound.SoundInterval#isInterval(int, int)
	 */
	public boolean isInterval(int num, int denom) {
		return num == getIntervalNumerator() && denom == getIntervalDenominator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.sound.SoundSettings#getLeftAmplitude()
	 */
	public double getLeftAmplitude() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.sound.SoundSettings#getLeftFrequency()
	 */
	public double getLeftFrequency() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.sound.SoundSettings#getPinkNoiseAmplitude()
	 */
	public double getPinkNoiseAmplitude() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.sound.SoundSettings#getPinkPanLeftAmplitude()
	 */
	public double getPinkPanLeftAmplitude() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.sound.SoundSettings#getPinkPanRightAmplitude()
	 */
	public double getPinkPanRightAmplitude() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.sound.SoundSettings#getRightAmplitude()
	 */
	public double getRightAmplitude() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.sound.SoundSettings#getRightFrequency()
	 */
	public double getRightFrequency() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.sound.SoundSettings#setLeftAmplitude(double)
	 */
	public void setLeftAmplitude(double d) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.sound.SoundSettings#setLeftFrequency(double)
	 */
	public void setLeftFrequency(double d) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.sound.SoundSettings#setPinkNoiseAmplitude(double)
	 */
	public void setPinkNoiseAmplitude(double d) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.sound.SoundSettings#setPinkPanLeftAmplitude(double
	 * )
	 */
	public void setPinkPanLeftAmplitude(double d) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.sound.SoundSettings#setPinkPanRightAmplitude(
	 * double)
	 */
	public void setPinkPanRightAmplitude(double d) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.sound.SoundSettings#setRightAmplitude(double)
	 */
	public void setRightAmplitude(double d) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.sound.SoundSettings#setRightFrequency(double)
	 */
	public void setRightFrequency(double d) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.sound.SoundSettings#start()
	 */
	public void start() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.sound.SoundSettings#stop()
	 */
	public void stop() {
		// TODO Auto-generated method stub

	}

}
