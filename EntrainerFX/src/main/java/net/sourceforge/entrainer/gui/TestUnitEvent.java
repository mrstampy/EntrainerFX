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
package net.sourceforge.entrainer.gui;

import java.util.EventObject;

import net.sourceforge.entrainer.xml.program.EntrainerProgramUnit;
import net.sourceforge.entrainer.xml.program.UnitSetter;

// TODO: Auto-generated Javadoc
/**
 * Event object fired when the 'test' buttons on the {@link UnitEditorPane} are
 * pressed.
 * 
 * @author burton
 *
 */
public class TestUnitEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	/** The Constant TERMINAL_START. */
	public static final int TERMINAL_START = 0;

	/** The Constant TERMINAL_END. */
	public static final int TERMINAL_END = 1;

	/** The Constant ACTION_START. */
	public static final int ACTION_START = 2;

	/** The Constant ACTION_STOP. */
	public static final int ACTION_STOP = 3;

	private EntrainerProgramUnit unit;
	private int terminal;
	private int action;

	/**
	 * Instantiate with the source, the {@link EntrainerProgramUnit}, the terminal
	 * (start/end) and the action (start/stop).
	 *
	 * @param source
	 *          the source
	 * @param unit
	 *          the unit
	 * @param terminal
	 *          the terminal
	 * @param action
	 *          the action
	 */
	public TestUnitEvent(Object source, EntrainerProgramUnit unit, int terminal, int action) {
		super(source);
		setUnit(unit);
		setTerminal(terminal);
		setAction(action);
	}

	/**
	 * Checks if is action start.
	 *
	 * @return true, if is action start
	 */
	public boolean isActionStart() {
		return action == ACTION_START;
	}

	/**
	 * Checks if is action stop.
	 *
	 * @return true, if is action stop
	 */
	public boolean isActionStop() {
		return action == ACTION_STOP;
	}

	/**
	 * Checks if is terminal start.
	 *
	 * @return true, if is terminal start
	 */
	public boolean isTerminalStart() {
		return terminal == TERMINAL_START;
	}

	/**
	 * Checks if is terminal end.
	 *
	 * @return true, if is terminal end
	 */
	public boolean isTerminalEnd() {
		return terminal == TERMINAL_END;
	}

	/**
	 * Gets the unit.
	 *
	 * @return the unit
	 */
	public EntrainerProgramUnit getUnit() {
		return unit;
	}

	/**
	 * Sets the unit.
	 *
	 * @param unit
	 *          the new unit
	 */
	public void setUnit(EntrainerProgramUnit unit) {
		this.unit = unit;
	}

	/**
	 * Gets the unit setter.
	 *
	 * @return the unit setter
	 */
	public UnitSetter getUnitSetter() {
		if (isTerminalStart()) {
			return getUnit().getStartUnitSetter();
		}

		return getUnit().getEndUnitSetter();
	}

	/**
	 * Sets the terminal.
	 *
	 * @param terminal
	 *          the new terminal
	 */
	protected void setTerminal(int terminal) {
		this.terminal = terminal;
	}

	/**
	 * Sets the action.
	 *
	 * @param action
	 *          the new action
	 */
	protected void setAction(int action) {
		this.action = action;
	}

}
