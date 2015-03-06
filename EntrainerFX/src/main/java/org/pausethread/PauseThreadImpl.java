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
 * Extend this class to create a thread that can be paused, resumed and
 * canceled. For an example check the source of the FileThread class.
 */
public abstract class PauseThreadImpl extends Thread implements PauseThread {
	private static int NORMAL = 0;
	private static int PAUSE = 1;
	private static int RESUME = 2;

	private int request;

	/**
	 * Constructs a PauseThread without setting the name.
	 */
	public PauseThreadImpl() {
		this("Pause Thread");
		request = NORMAL;
	}

	/**
	 * Constructs a PauseThread with the given name.
	 * 
	 * @param name
	 *          Pause Thread name
	 */
	public PauseThreadImpl(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pausethread.Pause#cancelWork()
	 */
	public final synchronized void cancelWork() {
		interrupt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pausethread.Pause#pauseWork()
	 */
	public final synchronized void pauseWork() {
		request = PAUSE;
		notify();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pausethread.Pause#resumeWork()
	 */
	public final synchronized void resumeWork() {
		if (request == PAUSE) {
			request = RESUME;
			notify();
		}
	}

	/**
	 * Checks if is paused.
	 *
	 * @return true, if is paused
	 */
	public final boolean isPaused() {
		return request == PAUSE;
	}

	/**
	 * Checks if is resumed.
	 *
	 * @return true, if is resumed
	 */
	public final boolean isResumed() {
		return request == RESUME;
	}

	/**
	 * Calling this method will pause the thread if a pause has been requested by
	 * calling the pauseWork() method. The thread will pause until resumeWork() is
	 * called.
	 * 
	 * @throws InterruptedException
	 *           thrown if the cancelWork() is called
	 */
	protected final void waitIfPauseRequest() throws InterruptedException {
		synchronized (this) {
			if (isInterrupted()) {
				throw new InterruptedException("Thread has been stopped.");
			} else if (request == PAUSE) {
				while (request != RESUME) {
					wait();
				}
				request = NORMAL;
			}
		}
	}

	/**
	 * This method invokes threadRun(). If cancelWork() is called threadStopped()
	 * is invoked.
	 */
	public final void run() {
		try {
			doWork();
			workDone();
		} catch (InterruptedException e) {
			workCanceled();
		}
	}

	/**
	 * Implement this method to do the actual work. waitIfPauseRequest() has to be
	 * called repeatingly so the thread will pause if a pause has been requested.
	 *
	 * @throws InterruptedException
	 *           the interrupted exception
	 */
	public abstract void doWork() throws InterruptedException;

	/**
	 * This method is invoked after doWork() has returned. Cleaning up any
	 * resources used should be done in this method.
	 */
	public abstract void workDone();

	/**
	 * This method is invoked after cancelWork() has been invoked. Cleaning up any
	 * resources used should be done in this method.
	 */
	public abstract void workCanceled();
}
