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

import static net.sourceforge.entrainer.mediator.MediatorConstants.AMPLITUDE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.ENTRAINMENT_FREQUENCY;
import static net.sourceforge.entrainer.mediator.MediatorConstants.FREQUENCY;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_ENTRAINER_MULTIPLE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_NOISE_AMPLITUDE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_PAN_AMPLITUDE;

// TODO: Auto-generated Javadoc
/**
 * The Class EndUnitSetter.
 */
public class EndUnitSetter extends AbstractUnitSetter {

	/**
	 * Instantiates a new end unit setter.
	 *
	 * @param unit
	 *          the unit
	 */
	public EndUnitSetter(EntrainerProgramUnit unit) {
		super(unit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.xml.program.UnitSetter#setAmplitude(double)
	 */
	public void setAmplitude(double d) {
		double old = getUnit().getEndAmplitude();
		getUnit().setEndAmplitude(d);
		if (d != old) {
			firePropertyChangeEvent(AMPLITUDE, old, d);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.xml.program.UnitSetter#setEntrainmentFrequency
	 * (double)
	 */
	public void setEntrainmentFrequency(double d) {
		double old = getUnit().getEndEntrainmentFrequency();
		getUnit().setEndEntrainmentFrequency(d);
		if (d != old) {
			firePropertyChangeEvent(ENTRAINMENT_FREQUENCY, old, d);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.xml.program.UnitSetter#setFrequency(double)
	 */
	public void setFrequency(double d) {
		double old = getUnit().getEndFrequency();
		getUnit().setEndFrequency(d);
		if (d != old) {
			firePropertyChangeEvent(FREQUENCY, old, d);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.xml.program.UnitSetter#setPinkEntrainerMultiple
	 * (double)
	 */
	public void setPinkEntrainerMultiple(double d) {
		double old = getUnit().getEndPinkEntrainerMultiple();
		getUnit().setEndPinkEntrainerMultiple(d);
		if (d != old) {
			firePropertyChangeEvent(PINK_ENTRAINER_MULTIPLE, old, d);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.xml.program.UnitSetter#setPinkNoise(double)
	 */
	public void setPinkNoise(double d) {
		double old = getUnit().getEndPinkNoise();
		getUnit().setEndPinkNoise(d);
		if (d != old) {
			firePropertyChangeEvent(PINK_NOISE_AMPLITUDE, old, d);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.xml.program.UnitSetter#setPinkPanAmplitude(double
	 * )
	 */
	public void setPinkPanAmplitude(double d) {
		double old = getUnit().getEndPinkPan();
		getUnit().setEndPinkPan(d);
		if (d != old) {
			firePropertyChangeEvent(PINK_PAN_AMPLITUDE, old, d);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.xml.program.UnitSetter#getAmplitude()
	 */
	public double getAmplitude() {
		return getUnit().getEndAmplitude();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.xml.program.UnitSetter#getEntrainmentFrequency()
	 */
	public double getEntrainmentFrequency() {
		return getUnit().getEndEntrainmentFrequency();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.xml.program.UnitSetter#getFrequency()
	 */
	public double getFrequency() {
		return getUnit().getEndFrequency();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.xml.program.UnitSetter#getPinkEntrainerMultiple()
	 */
	public double getPinkEntrainerMultiple() {
		return getUnit().getEndPinkEntrainerMultiple();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.xml.program.UnitSetter#getPinkNoise()
	 */
	public double getPinkNoise() {
		return getUnit().getEndPinkNoise();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.xml.program.UnitSetter#getPinkPanAmplitude()
	 */
	public double getPinkPanAmplitude() {
		return getUnit().getEndPinkPan();
	}

}
