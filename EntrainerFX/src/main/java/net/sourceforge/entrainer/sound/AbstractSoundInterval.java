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
 * Abstract superclass for all sound interval implementations.
 * 
 * @author burton
 */
public abstract class AbstractSoundInterval extends AbstractSoundSettings implements SoundInterval {
	
	private int intervalNumerator;
	private int intervalDenominator;

	/**
	 * Instantiates a new abstract sound interval.
	 *
	 * @param numerator the numerator
	 * @param denominator the denominator
	 */
	public AbstractSoundInterval(int numerator, int denominator) {
		super();
		setIntervalDenominator(denominator);
		setIntervalNumerator(numerator);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundInterval#getIntervalNumerator()
	 */
	public int getIntervalNumerator() {
		return intervalNumerator;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundInterval#setIntervalNumerator(int)
	 */
	public void setIntervalNumerator(int intervalNumerator) {
		this.intervalNumerator = intervalNumerator;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundInterval#getIntervalDenominator()
	 */
	public int getIntervalDenominator() {
		return intervalDenominator;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundInterval#setIntervalDenominator(int)
	 */
	public void setIntervalDenominator(int intervalDenominator) {
		this.intervalDenominator = intervalDenominator;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundInterval#getDisplayString()
	 */
	public String getDisplayString() {
		return getIntervalNumerator() + "/" + getIntervalDenominator();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundInterval#isIntervalDisplayString(java.lang.String)
	 */
	public boolean isIntervalDisplayString(String displayString) {
		if(displayString == null || displayString.trim().length() == 0) {
			return false;
		}
		
		int idx = displayString.indexOf("/");
		if(idx <= 0 ) {
			return false;
		}
		
		return isInterval(getIntervalNumerator(displayString), getIntervalDenominator(displayString));
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundInterval#getIntervalDenominator(java.lang.String)
	 */
	public int getIntervalDenominator(String displayString) {
		return getDenominator(displayString);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundInterval#getIntervalNumerator(java.lang.String)
	 */
	public int getIntervalNumerator(String displayString) {
		return getNumerator(displayString);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundSettings#pause()
	 */
	public void pause() {
		stop();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundSettings#resume()
	 */
	public void resume() {
		start();
	}

	/**
	 * Gets the denominator.
	 *
	 * @param displayString the display string
	 * @return the denominator
	 */
	public static int getDenominator(String displayString) {
		int idx = displayString.indexOf("/");
		return Integer.parseInt(displayString.substring(idx + 1));
	}
	
	/**
	 * Gets the numerator.
	 *
	 * @param displayString the display string
	 * @return the numerator
	 */
	public static int getNumerator(String displayString) {
		int idx = displayString.indexOf("/");
		return Integer.parseInt(displayString.substring(0, idx));
	}
	
	/**
	 * Gets the interval.
	 *
	 * @param displayString the display string
	 * @return the interval
	 */
	public static double getInterval(String displayString) {
		return ((double)getNumerator(displayString)) / ((double)getDenominator(displayString));
	}

}
