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

import java.util.EventObject;

// TODO: Auto-generated Javadoc
/**
 * This event indicates either the start or the finish of an Entrainer XML file
 * execution.
 * 
 * @author burton
 *
 */
public class SleeperManagerEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	/** The Constant STARTED. */
	public static final int STARTED = 0;

	/** The Constant STOPPED. */
	public static final int STOPPED = 1;

	private int action;

	/**
	 * Instantiates a new sleeper manager event.
	 *
	 * @param source
	 *          the source
	 * @param action
	 *          the action
	 */
	public SleeperManagerEvent(Object source, int action) {
		super(source);

		setAction(action);
	}

	/**
	 * Checks if is started.
	 *
	 * @return true, if is started
	 */
	public boolean isStarted() {
		return action == STARTED;
	}

	/**
	 * Checks if is stopped.
	 *
	 * @return true, if is stopped
	 */
	public boolean isStopped() {
		return action == STOPPED;
	}

	private void setAction(int action) {
		this.action = action;
	}

}
