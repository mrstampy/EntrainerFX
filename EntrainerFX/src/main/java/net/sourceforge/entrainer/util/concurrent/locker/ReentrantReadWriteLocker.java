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
package net.sourceforge.entrainer.util.concurrent.locker;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

// TODO: Auto-generated Javadoc
/**
 * Object to aggregate read/write lock functionality.
 *
 * @author burton
 */
public class ReentrantReadWriteLocker {

	private ReentrantReadWriteLock lock;
	private ReadLock readLock;
	private WriteLock writeLock;

	/**
	 * Instantiates a new reentrant read write locker.
	 */
	public ReentrantReadWriteLocker() {
		this(false);
	}

	/**
	 * Instantiates a new reentrant read write locker.
	 *
	 * @param fair
	 *          the fair
	 */
	public ReentrantReadWriteLocker(boolean fair) {
		lock = new ReentrantReadWriteLock(fair);
		readLock = lock.readLock();
		writeLock = lock.writeLock();
	}

	/**
	 * Checks if is locked.
	 *
	 * @return true, if is locked
	 */
	public boolean isLocked() {
		return lock.isWriteLocked();
	}

	/**
	 * Checks if is fair.
	 *
	 * @return true, if is fair
	 */
	public boolean isFair() {
		return lock.isFair();
	}

	/**
	 * Checks if is locked by current thread.
	 *
	 * @return true, if is locked by current thread
	 */
	public boolean isLockedByCurrentThread() {
		return lock.isWriteLockedByCurrentThread();
	}

	/**
	 * Checks for queued threads.
	 *
	 * @return true, if successful
	 */
	public boolean hasQueuedThreads() {
		return lock.hasQueuedThreads();
	}

	/**
	 * Checks for queued thread.
	 *
	 * @param t
	 *          the t
	 * @return true, if successful
	 */
	public boolean hasQueuedThread(Thread t) {
		return lock.hasQueuedThread(t);
	}

	/**
	 * Checks for waiters.
	 *
	 * @param c
	 *          the c
	 * @return true, if successful
	 */
	public boolean hasWaiters(Condition c) {
		return lock.hasWaiters(c);
	}

	/**
	 * Gets the queue length.
	 *
	 * @return the queue length
	 */
	public int getQueueLength() {
		return lock.getQueueLength();
	}

	/**
	 * Gets the wait queue length.
	 *
	 * @param c
	 *          the c
	 * @return the wait queue length
	 */
	public int getWaitQueueLength(Condition c) {
		return lock.getWaitQueueLength(c);
	}

	/**
	 * Lock.
	 */
	public void lock() {
		int holdCount = getReadHoldCount();

		if (holdCount > 0) throw new IllegalStateException("Cannot upgrade lock, read hold count " + holdCount);

		writeLock.lock();
	}

	/**
	 * Unlock.
	 */
	public void unlock() {
		if (getHoldCount() > 0) writeLock.unlock();
	}

	/**
	 * Lock interruptibly.
	 *
	 * @throws InterruptedException
	 *           the interrupted exception
	 */
	public void lockInterruptibly() throws InterruptedException {
		writeLock.lockInterruptibly();
	}

	/**
	 * New condition.
	 *
	 * @return the condition
	 */
	public Condition newCondition() {
		return writeLock.newCondition();
	}

	/**
	 * Gets the hold count.
	 *
	 * @return the hold count
	 */
	public int getHoldCount() {
		return writeLock.getHoldCount();
	}

	/**
	 * Try lock.
	 *
	 * @return true, if successful
	 */
	public boolean tryLock() {
		return writeLock.tryLock();
	}

	/**
	 * Try lock.
	 *
	 * @param timeout
	 *          the timeout
	 * @param units
	 *          the units
	 * @return true, if successful
	 * @throws InterruptedException
	 *           the interrupted exception
	 */
	public boolean tryLock(long timeout, TimeUnit units) throws InterruptedException {
		return writeLock.tryLock(timeout, units);
	}

	/**
	 * Read lock.
	 */
	public void readLock() {
		readLock.lock();
	}

	/**
	 * Read unlock.
	 */
	public void readUnlock() {
		if (getReadHoldCount() > 0) readLock.unlock();
	}

	/**
	 * Gets the read hold count.
	 *
	 * @return the read hold count
	 */
	public int getReadHoldCount() {
		return lock.getReadHoldCount();
	}

	/**
	 * Gets the read lock count.
	 *
	 * @return the read lock count
	 */
	public int getReadLockCount() {
		return lock.getReadLockCount();
	}

	/**
	 * Read lock interruptibly.
	 *
	 * @throws InterruptedException
	 *           the interrupted exception
	 */
	public void readLockInterruptibly() throws InterruptedException {
		readLock.lockInterruptibly();
	}

	/**
	 * New read condition.
	 *
	 * @return the condition
	 */
	public Condition newReadCondition() {
		return readLock.newCondition();
	}

	/**
	 * Try read lock.
	 *
	 * @return true, if successful
	 */
	public boolean tryReadLock() {
		return readLock.tryLock();
	}

	/**
	 * Try read lock.
	 *
	 * @param timeout
	 *          the timeout
	 * @param units
	 *          the units
	 * @return true, if successful
	 * @throws InterruptedException
	 *           the interrupted exception
	 */
	public boolean tryReadLock(long timeout, TimeUnit units) throws InterruptedException {
		return readLock.tryLock(timeout, units);
	}
}
