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
package net.sourceforge.entrainer.xml;

import static net.sourceforge.entrainer.mediator.MediatorConstants.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.sourceforge.entrainer.mediator.MediatorConstants;

// TODO: Auto-generated Javadoc
/**
 * Convenience class to create a property change listener for unit setters.
 *  
 * @author burton
 *
 */
public abstract class UnitSetterPropertyChangeListener implements PropertyChangeListener {
	private String propertyName;
	private double oldValue;
	private double newValue;

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent e) {
		setPropertyName(e.getPropertyName());
		setOldValue(((Double)e.getOldValue()).doubleValue());
		setNewValue(((Double)e.getNewValue()).doubleValue());
		
		propertyChangeImpl();
	}
	
	/**
	 * Implement in subclasses to deal with the property change event.
	 */
	protected abstract void propertyChangeImpl();

	/**
	 * Gets the property name.
	 *
	 * @return the property name
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Sets the property name.
	 *
	 * @param propertyName the new property name
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * Gets the old value.
	 *
	 * @return the old value
	 */
	public double getOldValue() {
		return oldValue;
	}

	/**
	 * Sets the old value.
	 *
	 * @param oldValue the new old value
	 */
	public void setOldValue(double oldValue) {
		this.oldValue = oldValue;
	}

	/**
	 * Gets the new value.
	 *
	 * @return the new value
	 */
	public double getNewValue() {
		return newValue;
	}

	/**
	 * Sets the new value.
	 *
	 * @param newValue the new new value
	 */
	public void setNewValue(double newValue) {
		this.newValue = newValue;
	}
	
	/**
	 * Checks if is amplitude.
	 *
	 * @return true, if is amplitude
	 */
	public boolean isAmplitude() {
		return isProperty(AMPLITUDE);
	}
	
	/**
	 * Checks if is entrainment frequency.
	 *
	 * @return true, if is entrainment frequency
	 */
	public boolean isEntrainmentFrequency() {
		return isProperty(ENTRAINMENT_FREQUENCY);
	}
	
	/**
	 * Checks if is frequency.
	 *
	 * @return true, if is frequency
	 */
	public boolean isFrequency() {
		return isProperty(FREQUENCY);
	}
	
	/**
	 * Checks if is pink entrainer multiple.
	 *
	 * @return true, if is pink entrainer multiple
	 */
	public boolean isPinkEntrainerMultiple() {
		return isProperty(PINK_ENTRAINER_MULTIPLE);
	}
	
	/**
	 * Checks if is pink noise.
	 *
	 * @return true, if is pink noise
	 */
	public boolean isPinkNoise() {
		return isProperty(PINK_NOISE_AMPLITUDE);
	}
	
	/**
	 * Checks if is pink pan.
	 *
	 * @return true, if is pink pan
	 */
	public boolean isPinkPan() {
		return isProperty(PINK_PAN_AMPLITUDE);
	}
	
	/**
	 * Checks if is property.
	 *
	 * @param name the name
	 * @return true, if is property
	 */
	protected boolean isProperty(MediatorConstants name) {
		return name.equals(getPropertyName());
	}

}
