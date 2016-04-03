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
package net.sourceforge.entrainer.pausethread;

import java.util.EventObject;

// TODO: Auto-generated Javadoc
/**
 * Notifies {@link PauseListener}'s that a thread should either pause or resume.
 * 
 * @author burton
 *
 */
public class PauseEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	/** The Constant PAUSE. */
	public static final int PAUSE = 0;

	/** The Constant RESUME. */
	public static final int RESUME = 1;

	private int action;

	/**
	 * Instantiates a new pause event.
	 *
	 * @param source
	 *          the source
	 * @param action
	 *          the action
	 */
	public PauseEvent(Object source, int action) {
		super(source);
		setAction(action);
	}

	/**
	 * Checks if is pause.
	 *
	 * @return true, if is pause
	 */
	public boolean isPause() {
		return getAction() == PAUSE;
	}

	/**
	 * Checks if is resume.
	 *
	 * @return true, if is resume
	 */
	public boolean isResume() {
		return getAction() == RESUME;
	}

	private int getAction() {
		return action;
	}

	private void setAction(int action) {
		this.action = action;
	}

}
