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
package net.sourceforge.entrainer.gui;

import java.util.EventObject;

// TODO: Auto-generated Javadoc
/**
 * The Class UnitEditorEvent.
 */
@SuppressWarnings("serial")
public class UnitEditorEvent extends EventObject {
	
	private final int idx;
	private final UnitEditorParm parm;
	private final double value;

	/**
	 * Instantiates a new unit editor event.
	 *
	 * @param source the source
	 * @param idx the idx
	 * @param parm the parm
	 * @param value the value
	 */
	public UnitEditorEvent(Object source, int idx, UnitEditorParm parm, double value) {
		super(source);
		this.idx = idx;
		this.value = value;
		this.parm = parm;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Gets the parm.
	 *
	 * @return the parm
	 */
	public UnitEditorParm getParm() {
		return parm;
	}
	
	/**
	 * Gets the idx.
	 *
	 * @return the idx
	 */
	public int getIdx() {
		return idx;
	}

}
