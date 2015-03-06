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
package org.pausethread;

// TODO: Auto-generated Javadoc
/**
 * Code from the PauseThreads project
 * 
 * https://sourceforge.net/projects/pausethreads/
 *
 * Implements the Adapter pattern so every abstract method in PauseThread does
 * not have to be implemented
 */
public class PauseThreadAdapter extends PauseThreadImpl {

	/**
	 * Instantiates a new pause thread adapter.
	 */
	public PauseThreadAdapter() {
		super();
	}

	/**
	 * Instantiates a new pause thread adapter.
	 *
	 * @param name
	 *          the name
	 */
	public PauseThreadAdapter(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pausethread.PauseThreadImpl#doWork()
	 */
	@Override
	public void doWork() throws InterruptedException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pausethread.PauseThreadImpl#workDone()
	 */
	@Override
	public void workDone() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pausethread.PauseThreadImpl#workCanceled()
	 */
	@Override
	public void workCanceled() {
	}
}
