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
package net.sourceforge.entrainer.mediator;

import static net.sourceforge.entrainer.mediator.MediatorConstants.FLASH_COLOUR;

import java.awt.Color;
import java.util.EventObject;

// TODO: Auto-generated Javadoc
/**
 * Create an instance of this class and use an implementation of {@link Sender}
 * to notify all interested {@link Receiver}'s of changes to properties.
 * 
 * @author burton
 * @see EntrainerMediator
 * @see MediatorConstants
 */
public class ReceiverChangeEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	private double doubleValue;
	private boolean booleanValue;
	private Color colourValue;
	private String stringValue;

	private double endValue;

	private MediatorConstants parm;

	/**
	 * Instantiates a new receiver change event.
	 *
	 * @param source
	 *          the source
	 */
	protected ReceiverChangeEvent(Object source) {
		super(source);
	}

	/**
	 * Instantiates a new receiver change event.
	 *
	 * @param source
	 *          the source
	 * @param value
	 *          the value
	 * @param parm
	 *          the parm
	 */
	public ReceiverChangeEvent(Object source, double value, MediatorConstants parm) {
		super(source);
		setParm(parm);
		setDoubleValue(value);
	}

	/**
	 * Instantiates a new receiver change event.
	 *
	 * @param source
	 *          the source
	 * @param delta
	 *          the delta
	 * @param endValue
	 *          the end value
	 * @param parm
	 *          the parm
	 */
	public ReceiverChangeEvent(Object source, double delta, double endValue, MediatorConstants parm) {
		this(source, delta, parm);

		setEndValue(endValue);
	}

	/**
	 * Instantiates a new receiver change event.
	 *
	 * @param source
	 *          the source
	 * @param b
	 *          the b
	 * @param parm
	 *          the parm
	 */
	public ReceiverChangeEvent(Object source, boolean b, MediatorConstants parm) {
		super(source);
		setParm(parm);
		setBooleanValue(b);
	}

	/**
	 * Instantiates a new receiver change event.
	 *
	 * @param source
	 *          the source
	 * @param value
	 *          the value
	 * @param parm
	 *          the parm
	 */
	public ReceiverChangeEvent(Object source, String value, MediatorConstants parm) {
		super(source);
		setParm(parm);
		setStringValue(value);
	}

	/**
	 * Instantiates a new receiver change event.
	 *
	 * @param source
	 *          the source
	 * @param c
	 *          the c
	 */
	public ReceiverChangeEvent(Object source, Color c) {
		this(source, c, FLASH_COLOUR);
	}

	/**
	 * Instantiates a new receiver change event.
	 *
	 * @param source          the source
	 * @param c          the c
	 * @param parm the parm
	 */
	public ReceiverChangeEvent(Object source, Color c, MediatorConstants parm) {
		super(source);
		setParm(parm);
		setColourValue(c);
	}

	/**
	 * Gets the parm.
	 *
	 * @return the parm
	 */
	public MediatorConstants getParm() {
		return parm;
	}

	/**
	 * Sets the parm.
	 *
	 * @param parm
	 *          the new parm
	 */
	protected void setParm(MediatorConstants parm) {
		this.parm = parm;
	}

	/**
	 * Gets the end value.
	 *
	 * @return the end value
	 */
	public double getEndValue() {
		return endValue;
	}

	/**
	 * Sets the end value.
	 *
	 * @param endValue
	 *          the new end value
	 */
	public void setEndValue(double endValue) {
		this.endValue = endValue;
	}

	/**
	 * Gets the boolean value.
	 *
	 * @return the boolean value
	 */
	public boolean getBooleanValue() {
		return booleanValue;
	}

	private void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	/**
	 * Gets the colour value.
	 *
	 * @return the colour value
	 */
	public Color getColourValue() {
		return colourValue;
	}

	private void setColourValue(Color colourValue) {
		this.colourValue = colourValue;
	}

	/**
	 * Gets the string value.
	 *
	 * @return the string value
	 */
	public String getStringValue() {
		return stringValue;
	}

	private void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	private void setDoubleValue(double value) {
		this.doubleValue = value;
	}

	/**
	 * Gets the double value.
	 *
	 * @return the double value
	 */
	public double getDoubleValue() {
		return doubleValue;
	}

}
