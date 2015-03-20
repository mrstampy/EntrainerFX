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

import java.math.BigDecimal;
import java.math.MathContext;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

// TODO: Auto-generated Javadoc
/**
 * The Class EntrainerProgramUnit.
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class EntrainerProgramUnit {

	@XmlElement
	private EntrainerProgramUnitTime time;

	@XmlElement
	private EntrainerProgramUnitAttribute amplitude;

	@XmlElement
	private EntrainerProgramUnitAttribute frequency;

	@XmlElement
	private EntrainerProgramUnitAttribute entrainmentFrequency;

	@XmlElement
	private EntrainerProgramUnitAttribute pinkNoise;

	@XmlElement
	private EntrainerProgramUnitAttribute pinkPan;

	@XmlElement
	private EntrainerProgramUnitAttribute pinkEntrainerMultiple;

	@XmlElement
	private EntrainerProgramUnitAttribute mediaAmplitude;

	@XmlElement
	private EntrainerProgramUnitAttribute mediaEntrainmentStrength;

	@XmlTransient
	private double deltaFrequency = 0.0;
	@XmlTransient
	private boolean hasDeltaFrequency;
	@XmlTransient
	private double deltaEntrainment = 0.0;
	@XmlTransient
	private boolean hasDeltaEntrainment;
	@XmlTransient
	private double deltaAmplitude = 0.0;
	@XmlTransient
	private boolean hasDeltaAmplitude;
	@XmlTransient
	private double deltaPinkNoise = 0.0;
	@XmlTransient
	private boolean hasDeltaPinkNoise;
	@XmlTransient
	private double deltaPinkPan = 0.0;
	@XmlTransient
	private boolean hasDeltaPinkPan;
	@XmlTransient
	private double deltaPinkEntrainerMultiple = 0.0;
	@XmlTransient
	private boolean hasDeltaPinkEntrainerMultiple;
	@XmlTransient
	private double deltaMediaAmplitude = 0.0;
	@XmlTransient
	private boolean hasDeltaMediaAmplitude;
	@XmlTransient
	private double deltaMediaEntrainmentStrength = 0.0;
	@XmlTransient
	private boolean hasDeltaMediaEntrainmentStrength;

	@XmlTransient
	private StartUnitSetter startUnitSetter;
	@XmlTransient
	private EndUnitSetter endUnitSetter;

	/**
	 * Instantiates a new entrainer program unit.
	 */
	public EntrainerProgramUnit() {
		startUnitSetter = new StartUnitSetter(this);
		endUnitSetter = new EndUnitSetter(this);
	}

	/**
	 * Gets the time.
	 *
	 * @return the time
	 */
	public EntrainerProgramUnitTime getTime() {
		if (time == null) {
			time = new EntrainerProgramUnitTime();
		}
		return time;
	}

	/**
	 * Sets the time.
	 *
	 * @param time
	 *          the new time
	 */
	public void setTime(EntrainerProgramUnitTime time) {
		this.time = time;
	}

	/**
	 * Gets the amplitude.
	 *
	 * @return the amplitude
	 */
	public EntrainerProgramUnitAttribute getAmplitude() {
		return amplitude;
	}

	/**
	 * Sets the amplitude.
	 *
	 * @param amplitude
	 *          the new amplitude
	 */
	public void setAmplitude(EntrainerProgramUnitAttribute amplitude) {
		this.amplitude = amplitude;
	}

	/**
	 * Gets the frequency.
	 *
	 * @return the frequency
	 */
	public EntrainerProgramUnitAttribute getFrequency() {
		return frequency;
	}

	/**
	 * Sets the frequency.
	 *
	 * @param frequency
	 *          the new frequency
	 */
	public void setFrequency(EntrainerProgramUnitAttribute frequency) {
		this.frequency = frequency;
	}

	/**
	 * Gets the entrainment frequency.
	 *
	 * @return the entrainment frequency
	 */
	public EntrainerProgramUnitAttribute getEntrainmentFrequency() {
		return entrainmentFrequency;
	}

	/**
	 * Sets the entrainment frequency.
	 *
	 * @param entrainmentFrequency
	 *          the new entrainment frequency
	 */
	public void setEntrainmentFrequency(EntrainerProgramUnitAttribute entrainmentFrequency) {
		this.entrainmentFrequency = entrainmentFrequency;
	}

	/**
	 * Gets the pink noise.
	 *
	 * @return the pink noise
	 */
	public EntrainerProgramUnitAttribute getPinkNoise() {
		return pinkNoise;
	}

	/**
	 * Sets the pink noise.
	 *
	 * @param pinkNoise
	 *          the new pink noise
	 */
	public void setPinkNoise(EntrainerProgramUnitAttribute pinkNoise) {
		this.pinkNoise = pinkNoise;
	}

	/**
	 * Gets the pink pan.
	 *
	 * @return the pink pan
	 */
	public EntrainerProgramUnitAttribute getPinkPan() {
		return pinkPan;
	}

	/**
	 * Sets the pink pan.
	 *
	 * @param pinkPan
	 *          the new pink pan
	 */
	public void setPinkPan(EntrainerProgramUnitAttribute pinkPan) {
		this.pinkPan = pinkPan;
	}

	/**
	 * Gets the pink entrainer multiple.
	 *
	 * @return the pink entrainer multiple
	 */
	public EntrainerProgramUnitAttribute getPinkEntrainerMultiple() {
		return pinkEntrainerMultiple;
	}

	/**
	 * Sets the pink entrainer multiple.
	 *
	 * @param pinkEntrainerMultiple
	 *          the new pink entrainer multiple
	 */
	public void setPinkEntrainerMultiple(EntrainerProgramUnitAttribute pinkEntrainerMultiple) {
		this.pinkEntrainerMultiple = pinkEntrainerMultiple;
	}

	/**
	 * Gets the time in millis.
	 *
	 * @return the time in millis
	 */
	public long getTimeInMillis() {
		if (getTime() == null) {
			return 0;
		}

		long milliMinutes = getTime().getMinutes() * 60 * 1000;
		long milliSeconds = getTime().getSeconds() * 1000;

		return milliMinutes + milliSeconds;
	}

	/**
	 * Returns the pink noise entrainer multiple delta (change/second).
	 *
	 * @return the pink entrainer multiple delta per second
	 */
	public double getPinkEntrainerMultipleDeltaPerSecond() {
		if (!hasDeltaPinkEntrainerMultiple) {
			deltaPinkEntrainerMultiple = getDeltaPerSecond(getStartPinkEntrainerMultiple(), getEndPinkEntrainerMultiple());
			hasDeltaPinkEntrainerMultiple = true;
		}

		return deltaPinkEntrainerMultiple;
	}

	/**
	 * Gets the start pink entrainer multiple.
	 *
	 * @return the start pink entrainer multiple
	 */
	public double getStartPinkEntrainerMultiple() {
		return getPinkEntrainerMultiple() == null ? 100 : getPinkEntrainerMultiple().getStart();
	}

	/**
	 * Sets the start pink entrainer multiple.
	 *
	 * @param start
	 *          the new start pink entrainer multiple
	 */
	public void setStartPinkEntrainerMultiple(double start) {
		setPinkEntrainerMultiple(getStartProgramUnit(getPinkEntrainerMultiple(), start));
	}

	/**
	 * Gets the end pink entrainer multiple.
	 *
	 * @return the end pink entrainer multiple
	 */
	public double getEndPinkEntrainerMultiple() {
		return getPinkEntrainerMultiple() == null ? 100 : getPinkEntrainerMultiple().getEnd();
	}

	/**
	 * Sets the end pink entrainer multiple.
	 *
	 * @param end
	 *          the new end pink entrainer multiple
	 */
	public void setEndPinkEntrainerMultiple(double end) {
		setPinkEntrainerMultiple(getEndProgramUnit(getPinkEntrainerMultiple(), end));
	}

	/**
	 * Returns the frequency delta (change/second).
	 *
	 * @return the frequency delta per second
	 */
	public double getFrequencyDeltaPerSecond() {
		if (!hasDeltaFrequency) {
			deltaFrequency = getDeltaPerSecond(getStartFrequency(), getEndFrequency());
			hasDeltaFrequency = true;
		}
		return deltaFrequency;
	}

	/**
	 * Gets the start frequency.
	 *
	 * @return the start frequency
	 */
	public double getStartFrequency() {
		return getFrequency() == null ? 100 : getFrequency().getStart();
	}

	/**
	 * Sets the start frequency.
	 *
	 * @param start
	 *          the new start frequency
	 */
	public void setStartFrequency(double start) {
		setFrequency(getStartProgramUnit(getFrequency(), start));
	}

	/**
	 * Gets the end frequency.
	 *
	 * @return the end frequency
	 */
	public double getEndFrequency() {
		return getFrequency() == null ? 100 : getFrequency().getEnd();
	}

	/**
	 * Sets the end frequency.
	 *
	 * @param end
	 *          the new end frequency
	 */
	public void setEndFrequency(double end) {
		setFrequency(getEndProgramUnit(getFrequency(), end));
	}

	/**
	 * Returns the entrainment frequency delta (change/second).
	 *
	 * @return the entrainment frequency delta per second
	 */
	public double getEntrainmentFrequencyDeltaPerSecond() {
		if (!hasDeltaEntrainment) {
			deltaEntrainment = getDeltaPerSecond(getStartEntrainmentFrequency(), getEndEntrainmentFrequency());
			hasDeltaEntrainment = true;
		}
		return deltaEntrainment;
	}

	/**
	 * Gets the end entrainment frequency.
	 *
	 * @return the end entrainment frequency
	 */
	public double getEndEntrainmentFrequency() {
		return getEntrainmentFrequency() == null ? 10 : getEntrainmentFrequency().getEnd();
	}

	/**
	 * Sets the end entrainment frequency.
	 *
	 * @param end
	 *          the new end entrainment frequency
	 */
	public void setEndEntrainmentFrequency(double end) {
		setEntrainmentFrequency(getEndProgramUnit(getEntrainmentFrequency(), end));
	}

	/**
	 * Gets the start entrainment frequency.
	 *
	 * @return the start entrainment frequency
	 */
	public double getStartEntrainmentFrequency() {
		return getEntrainmentFrequency() == null ? 10 : getEntrainmentFrequency().getStart();
	}

	/**
	 * Sets the start entrainment frequency.
	 *
	 * @param start
	 *          the new start entrainment frequency
	 */
	public void setStartEntrainmentFrequency(double start) {
		setEntrainmentFrequency(getStartProgramUnit(getEntrainmentFrequency(), start));
	}

	/**
	 * Returns the amplitude delta (change/second).
	 *
	 * @return the amplitude delta per second
	 */
	public double getAmplitudeDeltaPerSecond() {
		if (!hasDeltaAmplitude) {
			deltaAmplitude = getDeltaPerSecond(getStartAmplitude(), getEndAmplitude());
			hasDeltaAmplitude = true;
		}
		return deltaAmplitude;
	}

	/**
	 * Gets the start amplitude.
	 *
	 * @return the start amplitude
	 */
	public double getStartAmplitude() {
		return getAmplitude() == null ? 0.5 : getAmplitude().getStart();
	}

	/**
	 * Sets the start amplitude.
	 *
	 * @param start
	 *          the new start amplitude
	 */
	public void setStartAmplitude(double start) {
		setAmplitude(getStartProgramUnit(getAmplitude(), start));
	}

	private EntrainerProgramUnitAttribute getStartProgramUnit(EntrainerProgramUnitAttribute unitAttribute, double start) {
		if (unitAttribute == null) {
			unitAttribute = new EntrainerProgramUnitAttribute(start, 0);
		} else {
			unitAttribute.setStart(start);
		}

		return unitAttribute;
	}

	private EntrainerProgramUnitAttribute getEndProgramUnit(EntrainerProgramUnitAttribute unitAttribute, double end) {
		if (unitAttribute == null) {
			unitAttribute = new EntrainerProgramUnitAttribute(0, end);
		} else {
			unitAttribute.setEnd(end);
		}

		return unitAttribute;
	}

	/**
	 * Gets the end amplitude.
	 *
	 * @return the end amplitude
	 */
	public double getEndAmplitude() {
		return getAmplitude() == null ? 0.5 : getAmplitude().getEnd();
	}

	/**
	 * Sets the end amplitude.
	 *
	 * @param end
	 *          the new end amplitude
	 */
	public void setEndAmplitude(double end) {
		setAmplitude(getEndProgramUnit(getAmplitude(), end));
	}

	/**
	 * Returns the pink noise delta (change/second).
	 *
	 * @return the pink noise delta per second
	 */
	public double getPinkNoiseDeltaPerSecond() {
		if (!hasDeltaPinkNoise) {
			deltaPinkNoise = getDeltaPerSecond(getStartPinkNoise(), getEndPinkNoise());
			hasDeltaPinkNoise = true;
		}

		return deltaPinkNoise;
	}

	/**
	 * Gets the end pink noise.
	 *
	 * @return the end pink noise
	 */
	public double getEndPinkNoise() {
		return getPinkNoise() == null ? 0.5 : getPinkNoise().getEnd();
	}

	/**
	 * Sets the end pink noise.
	 *
	 * @param end
	 *          the new end pink noise
	 */
	public void setEndPinkNoise(double end) {
		setPinkNoise(getEndProgramUnit(getPinkNoise(), end));
	}

	/**
	 * Gets the start pink noise.
	 *
	 * @return the start pink noise
	 */
	public double getStartPinkNoise() {
		return getPinkNoise() == null ? 0.5 : getPinkNoise().getStart();
	}

	/**
	 * Sets the start pink noise.
	 *
	 * @param start
	 *          the new start pink noise
	 */
	public void setStartPinkNoise(double start) {
		setPinkNoise(getStartProgramUnit(getPinkNoise(), start));
	}

	/**
	 * Returns the pink noise pan delta (change/second).
	 *
	 * @return the pink pan delta per second
	 */
	public double getPinkPanDeltaPerSecond() {
		if (!hasDeltaPinkPan) {
			deltaPinkPan = getDeltaPerSecond(getStartPinkPan(), getEndPinkPan());
			hasDeltaPinkPan = true;
		}

		return deltaPinkPan;
	}

	/**
	 * Gets the end pink pan.
	 *
	 * @return the end pink pan
	 */
	public double getEndPinkPan() {
		return getPinkPan() == null ? 0.5 : getPinkPan().getEnd();
	}

	/**
	 * Sets the end pink pan.
	 *
	 * @param end
	 *          the new end pink pan
	 */
	public void setEndPinkPan(double end) {
		setPinkPan(getEndProgramUnit(getPinkPan(), end));
	}

	/**
	 * Gets the start pink pan.
	 *
	 * @return the start pink pan
	 */
	public double getStartPinkPan() {
		return getPinkPan() == null ? 0.5 : getPinkPan().getStart();
	}

	/**
	 * Sets the start pink pan.
	 *
	 * @param start
	 *          the new start pink pan
	 */
	public void setStartPinkPan(double start) {
		setPinkPan(getStartProgramUnit(getPinkPan(), start));
	}

	// returns the delta for every second
	private double getDeltaPerSecond(double start, double end) {
		BigDecimal bigStart = new BigDecimal(start);
		BigDecimal bigEnd = new BigDecimal(end);
		BigDecimal millis = new BigDecimal(getTimeInMillis() / 1000);

		BigDecimal difference = bigEnd.subtract(bigStart, MathContext.DECIMAL64);

		// if there is no time, this is a transitional unit created from
		// the end values of the last as the start values, and the start
		// values of the current as the end values. Return the
		// difference.
		if (millis.doubleValue() == 0) {
			return difference.doubleValue();
		}

		BigDecimal delta = difference.divide(millis, MathContext.DECIMAL64);

		return delta.doubleValue();
	}

	/**
	 * Gets the start unit setter.
	 *
	 * @return the start unit setter
	 */
	public UnitSetter getStartUnitSetter() {
		return startUnitSetter;
	}

	/**
	 * Gets the end unit setter.
	 *
	 * @return the end unit setter
	 */
	public UnitSetter getEndUnitSetter() {
		return endUnitSetter;
	}

	/**
	 * Gets the start media amplitude.
	 *
	 * @return the start media amplitude
	 */
	public double getStartMediaAmplitude() {
		return getMediaAmplitude() == null ? 1.0 : getMediaAmplitude().getStart();
	}

	/**
	 * Gets the end media amplitude.
	 *
	 * @return the end media amplitude
	 */
	public double getEndMediaAmplitude() {
		return getMediaAmplitude() == null ? 1.0 : getMediaAmplitude().getEnd();
	}

	/**
	 * Gets the start media entrainment strength.
	 *
	 * @return the start media entrainment strength
	 */
	public double getStartMediaEntrainmentStrength() {
		return getMediaEntrainmentStrength() == null ? 0.5 : getMediaEntrainmentStrength().getStart();
	}

	/**
	 * Gets the end media entrainment strength.
	 *
	 * @return the end media entrainment strength
	 */
	public double getEndMediaEntrainmentStrength() {
		return getMediaEntrainmentStrength() == null ? 0.5 : getMediaEntrainmentStrength().getEnd();
	}

	/**
	 * Gets the media amplitude delta per second.
	 *
	 * @return the media amplitude delta per second
	 */
	public double getMediaAmplitudeDeltaPerSecond() {
		if (!hasDeltaMediaAmplitude) {
			deltaMediaAmplitude = getDeltaPerSecond(getStartMediaAmplitude(), getEndMediaAmplitude());
			hasDeltaMediaAmplitude = true;
		}

		return deltaMediaAmplitude;
	}

	/**
	 * Gets the media entrainment strength delta per second.
	 *
	 * @return the media entrainment strength delta per second
	 */
	public double getMediaEntrainmentStrengthDeltaPerSecond() {
		if (!hasDeltaMediaEntrainmentStrength) {
			deltaMediaEntrainmentStrength = getDeltaPerSecond(getStartMediaEntrainmentStrength(),
					getEndMediaEntrainmentStrength());
			hasDeltaMediaEntrainmentStrength = true;
		}

		return deltaMediaEntrainmentStrength;
	}

	/**
	 * Gets the media amplitude.
	 *
	 * @return the media amplitude
	 */
	public EntrainerProgramUnitAttribute getMediaAmplitude() {
		return mediaAmplitude;
	}

	/**
	 * Sets the media amplitude.
	 *
	 * @param mediaAmplitude the new media amplitude
	 */
	public void setMediaAmplitude(EntrainerProgramUnitAttribute mediaAmplitude) {
		this.mediaAmplitude = mediaAmplitude;
	}

	/**
	 * Gets the media entrainment strength.
	 *
	 * @return the media entrainment strength
	 */
	public EntrainerProgramUnitAttribute getMediaEntrainmentStrength() {
		return mediaEntrainmentStrength;
	}

	/**
	 * Sets the media entrainment strength.
	 *
	 * @param mediaEntrainmentStrength the new media entrainment strength
	 */
	public void setMediaEntrainmentStrength(EntrainerProgramUnitAttribute mediaEntrainmentStrength) {
		this.mediaEntrainmentStrength = mediaEntrainmentStrength;
	}

}
