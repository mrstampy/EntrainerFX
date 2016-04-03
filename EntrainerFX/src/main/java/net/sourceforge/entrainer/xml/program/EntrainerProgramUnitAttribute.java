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
package net.sourceforge.entrainer.xml.program;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

// TODO: Auto-generated Javadoc
/**
 * The Class EntrainerProgramUnitAttribute.
 */
@XmlType
@XmlAccessorType(value = XmlAccessType.FIELD)
public class EntrainerProgramUnitAttribute {

	@XmlAttribute
	private double start;

	@XmlAttribute
	private double end;

	/**
	 * Instantiates a new entrainer program unit attribute.
	 */
	public EntrainerProgramUnitAttribute() {

	}

	/**
	 * Instantiates a new entrainer program unit attribute.
	 *
	 * @param start
	 *          the start
	 * @param end
	 *          the end
	 */
	public EntrainerProgramUnitAttribute(double start, double end) {
		setStart(start);
		setEnd(end);
	}

	/**
	 * Gets the start.
	 *
	 * @return the start
	 */
	public double getStart() {
		return start;
	}

	/**
	 * Sets the start.
	 *
	 * @param start
	 *          the new start
	 */
	public void setStart(double start) {
		this.start = start;
	}

	/**
	 * Gets the end.
	 *
	 * @return the end
	 */
	public double getEnd() {
		return end;
	}

	/**
	 * Sets the end.
	 *
	 * @param end
	 *          the new end
	 */
	public void setEnd(double end) {
		this.end = end;
	}

}
