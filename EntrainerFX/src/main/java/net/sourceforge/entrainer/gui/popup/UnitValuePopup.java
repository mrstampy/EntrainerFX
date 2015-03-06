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
package net.sourceforge.entrainer.gui.popup;

import java.awt.Window;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.sound.AbstractSoundInterval;
import net.sourceforge.entrainer.xml.program.EntrainerProgramInterval;

// TODO: Auto-generated Javadoc
/**
 * Class to display the value of a unit parameter.
 * 
 * @author burton
 *
 */
public class UnitValuePopup extends InfoPopup {

	private static final long serialVersionUID = 1L;
	private MediatorConstants parameterName;
	private double value;
	private double time;
	private String units;
	private List<EntrainerProgramInterval> intervals;

	/**
	 * Instantiates a new unit value popup.
	 *
	 * @param owner
	 *          the owner
	 * @param parameterName
	 *          the parameter name
	 * @param value
	 *          the value
	 * @param time
	 *          the time
	 * @param units
	 *          the units
	 */
	public UnitValuePopup(Window owner, MediatorConstants parameterName, double value, double time, String units) {
		this(owner, parameterName, value, time, units, new ArrayList<EntrainerProgramInterval>());
	}

	/**
	 * Instantiates a new unit value popup.
	 *
	 * @param owner
	 *          the owner
	 * @param parameterName
	 *          the parameter name
	 * @param value
	 *          the value
	 * @param time
	 *          the time
	 * @param units
	 *          the units
	 * @param intervals
	 *          the intervals
	 */
	public UnitValuePopup(Window owner, MediatorConstants parameterName, double value, double time, String units,
			List<EntrainerProgramInterval> intervals) {
		super(owner);
		this.intervals = intervals;
		setParameterName(parameterName);
		setValue(value);
		setTime(time);
		setUnits(units);
		init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.gui.popup.InfoPopup#getContent()
	 */
	@Override
	protected String getContent() {
		StringBuffer buf = new StringBuffer();
		buf.append("<html><b>&nbsp;<u>" + getParameterName() + "</u>&nbsp;</b><br><br>");
		buf.append("Time: " + getMinutes() + ":" + getSeconds() + "<br>");
		buf.append("Value: " + getValue() + getUnits() + "<br>");

		if (intervals.size() > 0) {
			buf.append("<br><b>&nbsp;<u>Intervals</u>&nbsp;</b><br><br>");
			int i = 1;
			for (EntrainerProgramInterval s : intervals) {
				double d = AbstractSoundInterval.getInterval(s.getValue());
				buf.append("Interval " + i + ": " + getInterval(getValue(), d) + getUnits() + "<br>");
				i++;
			}

			buf.append("</html>");
		}

		return buf.toString();
	}

	private double getInterval(double base, double interval) {
		return base + (base * interval);
	}

	private int getMinutes() {
		return ((int) getTime()) / 60;
	}

	private String getSeconds() {
		int value = ((int) getTime()) % 60;

		DecimalFormat format = new DecimalFormat("00");
		return format.format(value);
	}

	/**
	 * Gets the parameter name.
	 *
	 * @return the parameter name
	 */
	public MediatorConstants getParameterName() {
		return parameterName;
	}

	/**
	 * Sets the parameter name.
	 *
	 * @param name
	 *          the new parameter name
	 */
	public void setParameterName(MediatorConstants name) {
		this.parameterName = name;
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
	 * Sets the value.
	 *
	 * @param value
	 *          the new value
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * Gets the time.
	 *
	 * @return the time
	 */
	public double getTime() {
		return time;
	}

	/**
	 * Sets the time.
	 *
	 * @param time
	 *          the new time
	 */
	public void setTime(double time) {
		this.time = time;
	}

	/**
	 * Gets the units.
	 *
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}

	/**
	 * Sets the units.
	 *
	 * @param units
	 *          the new units
	 */
	public void setUnits(String units) {
		this.units = units;
	}

}
