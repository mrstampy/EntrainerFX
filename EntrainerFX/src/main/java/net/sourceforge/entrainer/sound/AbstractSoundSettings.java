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
 * Abstract superclass for sound setting implementations.
 * 
 * @author burton
 */
public abstract class AbstractSoundSettings implements SoundSettings {
	private double pinkPan;
	private boolean isPlaying;
	private boolean isPaused;

	/**
	 * Instantiates a new abstract sound settings.
	 */
	public AbstractSoundSettings() {
		super();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundSettings#setEntrainmentFrequency(double)
	 */
	public void setEntrainmentFrequency(double d) {
		setRightFrequency(getLeftFrequency() + d);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundSettings#setAmplitude(double)
	 */
	public void setAmplitude(double d) {
		setLeftAmplitude(d);
		setRightAmplitude(d);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundSettings#getEntrainmentFrequency()
	 */
	public double getEntrainmentFrequency() {
		return getRightFrequency() - getLeftFrequency();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundSettings#setPinkPanAmplitude(double)
	 */
	public void setPinkPanAmplitude(double d) {
		pinkPan = d;
		double right = d >= 0.5 ? 1.0 : 1 - ((0.5 - d) * 2);
		double left = d <= 0.5 ? 1.0 : 1 - ((d - 0.5) * 2);

		right = 1 - right;
		left = 1 - left;

		setPinkPanLeftAmplitude(left);
		setPinkPanRightAmplitude(right);
	}

	/**
	 * Gets the pink pan.
	 *
	 * @return the pink pan
	 */
	public double getPinkPan() {
		return pinkPan;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundSettings#isPlaying()
	 */
	public boolean isPlaying() {
		return isPlaying;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundSettings#setPlaying(boolean)
	 */
	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundSettings#isPaused()
	 */
	public boolean isPaused() {
		return isPaused;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundSettings#setPaused(boolean)
	 */
	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}

}
