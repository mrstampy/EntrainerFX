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
package net.sourceforge.entrainer.xml.program;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

// TODO: Auto-generated Javadoc
/**
 * The Class EntrainerProgramUnitTime.
 */
@XmlType
@XmlAccessorType(value = XmlAccessType.FIELD)
public class EntrainerProgramUnitTime {
	
	@XmlAttribute
	private int minutes;
	
	@XmlAttribute
	private int seconds;
	
	/**
	 * Gets the minutes.
	 *
	 * @return the minutes
	 */
	public int getMinutes() {
		return minutes;
	}
	
	/**
	 * Sets the minutes.
	 *
	 * @param minutes the new minutes
	 */
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	
	/**
	 * Gets the seconds.
	 *
	 * @return the seconds
	 */
	public int getSeconds() {
		return seconds;
	}
	
	/**
	 * Sets the seconds.
	 *
	 * @param seconds the new seconds
	 */
	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

}
