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
package net.sourceforge.entrainer.gui;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class EntrainerFXResizer.
 */
public class EntrainerFXResizer {

	private ResizerListener listener;
	private Rectangle2D size;

	private boolean dragStarted;
	private boolean resize;

	private Lock lock = new ReentrantLock();

	private double screenX;
	private double screenY;

	private ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor();
	private boolean clicked;
	private Future<?> future;

	/**
	 * Instantiates a new entrainer fx resizer.
	 *
	 * @param listener
	 *          the listener
	 */
	public EntrainerFXResizer(ResizerListener listener) {
		this.listener = listener;
	}

	/**
	 * On release.
	 *
	 * @param e
	 *          the e
	 */
	public void onRelease(MouseEvent e) {
		dragStarted = false;
		resize = false;
	}

	/**
	 * On click.
	 *
	 * @param e
	 *          the e
	 */
	public void onClick(MouseEvent e) {
		clicked = true;
		if (future != null) future.cancel(true);

		future = svc.schedule(() -> clicked = false, 500, TimeUnit.MILLISECONDS);
	}

	/**
	 * On drag.
	 *
	 * @param e
	 *          the e
	 */
	public void onDrag(MouseEvent e) {
		lock.lock();
		try {
			if (dragStarted) {
				doDrag(e);
			} else {
				initDrag(e);
			}
			screenX = e.getScreenX();
			screenY = e.getScreenY();
		} finally {
			lock.unlock();
		}
	}

	private void doDrag(MouseEvent e) {
		if (resize) {
			resize(e);
		} else {
			reposition(e);
		}
	}

	private void initDrag(MouseEvent e) {
		if (future != null) future.cancel(true);
		resize = clicked;
		dragStarted = true;
		clicked = false;
	}

	private void reposition(MouseEvent e) {
		double minX = size.getMinX() + (e.getScreenX() - screenX);
		double minY = size.getMinY() + (e.getScreenY() - screenY);

		size = new Rectangle2D(minX, minY, size.getWidth(), size.getHeight());

		listener.resize(size);
	}

	private void resize(MouseEvent e) {
		double width = size.getWidth() + (e.getScreenX() - screenX);
		double height = size.getHeight() + (e.getScreenY() - screenY);

		size = new Rectangle2D(size.getMinX(), size.getMinY(), width, height);

		listener.resize(size);
	}

	/**
	 * Sets the size.
	 *
	 * @param size
	 *          the new size
	 */
	public void setSize(Rectangle2D size) {
		this.size = size;
	}

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public Rectangle2D getSize() {
		return size;
	}

	/**
	 * The listener interface for receiving resizer events. The class that is
	 * interested in processing a resizer event implements this interface, and the
	 * object created with that class is registered with a component using the
	 * component's <code>addResizerListener<code> method. When
	 * the resizer event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see ResizerEvent
	 */
	public interface ResizerListener {

		/**
		 * Resize.
		 *
		 * @param size
		 *          the size
		 */
		void resize(Rectangle2D size);
	}

}
