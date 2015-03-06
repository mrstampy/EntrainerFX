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
package net.sourceforge.entrainer.pausethread;

import org.pausethread.PauseThreadAdapter;

// TODO: Auto-generated Javadoc
/**
 * This class allows threads to be paused and resumed via the event model.
 * 
 * @author burton
 *
 */
public abstract class EntrainerPausibleThread extends PauseThreadAdapter implements PauseListener {

	/**
	 * Instantiates a new entrainer pausible thread.
	 */
	public EntrainerPausibleThread() {
		super();
	}

	/**
	 * Instantiates a new entrainer pausible thread.
	 *
	 * @param name
	 *          the name
	 */
	public EntrainerPausibleThread(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.pausethread.PauseListener#pauseEventPerformed
	 * (net.sourceforge.entrainer.pausethread.PauseEvent)
	 */
	public void pauseEventPerformed(PauseEvent e) {
		if (e.isPause()) {
			pauseWork();
		} else if (e.isResume()) {
			resumeWork();
		}
	}

}
