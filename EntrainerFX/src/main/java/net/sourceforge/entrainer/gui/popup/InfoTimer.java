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
package net.sourceforge.entrainer.gui.popup;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;

import net.sourceforge.entrainer.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * This is a superclass to control the appearance / dismissal of
 * {@link InfoPopup}'s.
 * 
 * @author burton
 *
 */
public abstract class InfoTimer extends Thread {
	private boolean shouldStop;
	private InfoPopup info;

	/**
	 * Instantiates a new info timer.
	 */
	public InfoTimer() {
		super();
	}

	/**
	 * Use the start() method to execute this thread.
	 */
	public final void run() {
		execute();
	}

	/**
	 * Call this method to dispose of the popup.
	 */
	public void dismiss() {
		if (info != null) {
			info.setVisible(false);
			info.dispose();
			info = null;
		}
		shouldStop = true;
	}

	/**
	 * Implement in subclasses to return true if the popup should be displayed.
	 *
	 * @return true, if successful
	 */
	protected abstract boolean shouldShowInfo();

	/**
	 * Subclasses should instantiate an {@link InfoPopup} subclass and set it as a
	 * property of this class via the setInfo() method.
	 * 
	 * @see InfoPopup
	 */
	protected abstract void createNewInfo();

	private synchronized void execute() {
		Utils.snooze(1000);
		if (shouldShowInfo()) {
			createNewInfo();
			info.pack();
			Point p = getInfoLocation(info.getSize());
			info.setLocation(p);
			info.setVisible(true);
			info.toFront();
			if (shouldStop) {
				dismiss();
			}
		}
	}

	private Point getInfoLocation(Dimension d) {
		Point point = new Point();
		Point mousePoint = MouseInfo.getPointerInfo().getLocation();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		if (mousePoint.x + d.width + 1 > screenSize.width) {
			point.x = mousePoint.x - d.width - 1;
		} else {
			point.x = mousePoint.x + 1;
		}

		if (mousePoint.y + d.height + 1 > screenSize.height) {
			point.y = mousePoint.y - d.height - 1;
		} else {
			point.y = mousePoint.y + 1;
		}

		return point;
	}

	/**
	 * Checks if is should stop.
	 *
	 * @return true, if is should stop
	 */
	public boolean isShouldStop() {
		return shouldStop;
	}

	/**
	 * Sets the should stop.
	 *
	 * @param shouldStop
	 *          the new should stop
	 */
	public void setShouldStop(boolean shouldStop) {
		this.shouldStop = shouldStop;
	}

	/**
	 * Gets the info.
	 *
	 * @return the info
	 */
	public InfoPopup getInfo() {
		return info;
	}

	/**
	 * Call this method from the createNewInfo() implementation with the
	 * appropriate {@link InfoPopup} subclass.
	 *
	 * @param info
	 *          the new info
	 */
	protected void setInfo(InfoPopup info) {
		this.info = info;
	}

}
