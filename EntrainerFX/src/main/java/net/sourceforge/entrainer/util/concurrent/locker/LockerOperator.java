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

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

// TODO: Auto-generated Javadoc
/**
 * Super class to manage concurrency in a consistent manner regardless of the
 * underlying locking mechanisms. The use of {@link Runnable} is not for the
 * purpose of threading, rather to provide a wrapper around the execution of any
 * void operation for the purpose of enclosing the operation within a lock
 * boundary.
 * 
 * @author burton
 *
 * @param <LOCKER>
 *          a class which manages concurrency
 */
public abstract class LockerOperator<LOCKER> {

	private AtomicReference<LOCKER> locker = new AtomicReference<>();

	/**
	 * The various types of {@link LockerOperator}s.
	 * 
	 * @author burton
	 *
	 */
	public enum OperatorType {

		/** The reentrant read write. */
		REENTRANT_READ_WRITE,
		/** The stamped. */
		STAMPED,
		/** The reentrant. */
		REENTRANT;
	}

	private final OperatorType type;

	/**
	 * Instantiates a new locker operator.
	 *
	 * @param type
	 *          the type
	 * @param locker
	 *          the locker
	 */
	protected LockerOperator(OperatorType type, LOCKER locker) {
		this.type = type;
		setLocker(locker);
	}

	/**
	 * Returns the object used for lock boundaries.
	 *
	 * @return the locker
	 */
	public LOCKER getLocker() {
		return locker.get();
	}

	/**
	 * Sets the object used for lock boundaries.
	 *
	 * @param locker
	 *          the new locker
	 */
	public void setLocker(LOCKER locker) {
		Objects.requireNonNull(locker);

		this.locker.set(locker);
	}

	/**
	 * Returns the type of {@link LockerOperator}.
	 *
	 * @return the type
	 */
	public OperatorType getType() {
		return type;
	}

	/**
	 * Implementations enclose the execution of the {@link Returnable} within a
	 * 'read lock' returning the result.
	 *
	 * @param <T>
	 *          the generic type
	 * @param r
	 *          the r
	 * @return the t
	 */
	public abstract <T> T readOp(Returnable<T> r);

	/**
	 * Implementations enclose the execution of the {@link Returnable} within a
	 * 'write lock' returning the result.
	 *
	 * @param <T>
	 *          the generic type
	 * @param r
	 *          the r
	 * @return the t
	 */
	public abstract <T> T writeOp(Returnable<T> r);

	/**
	 * Implementations enclose the execution of the {@link Runnable} within a
	 * 'read lock'.
	 *
	 * @param r
	 *          the r
	 */
	public abstract void readOp(Runnable r);

	/**
	 * Implementations enclose the execution of the {@link Runnable} within a
	 * 'write lock'.
	 *
	 * @param r
	 *          the r
	 */
	public abstract void writeOp(Runnable r);

	/**
	 * Implementations enclose the execution of the {@link Returnable} within an
	 * 'optimistic read lock' returning the result.
	 *
	 * @param <T>
	 *          the generic type
	 * @param r
	 *          the r
	 * @return the t
	 */
	public abstract <T> T optimisticRead(Returnable<T> r);

	/**
	 * Implementations enclose the execution of the {@link Runnable} within an
	 * 'optimistic read lock'.
	 *
	 * @param r
	 *          the r
	 */
	public abstract void optimisticRead(Runnable r);

	/**
	 * Obtain a read lock outside of implemented execution boundaries. A
	 * corresponding call to {@link #readUnlock()} must occur and should occur in
	 * a finally block.
	 *
	 */
	public abstract void readLock();

	/**
	 * Unlock a read lock if it exists, else return.
	 */
	public abstract void readUnlock();

	/**
	 * Obtain a lock outside of implemented execution boundaries. A corresponding
	 * call to {@link #unlock()} must occur and should occur in a finally block.
	 *
	 */
	public abstract void lock();

	/**
	 * Unlock a lock if it exists, else return.
	 */
	public abstract void unlock();

	/**
	 * Returns true if a call to {@link #lock()} has been made and the
	 * corresponding call to {@link #unlock()} has not.
	 *
	 * @return true, if is locked
	 */
	public abstract boolean isLocked();

	/**
	 * Returns true if {@link #lock()}ed by the current thread.
	 *
	 * @return true, if is locked by current thread
	 */
	public abstract boolean isLockedByCurrentThread();

	/**
	 * Returns true if read locks exist.
	 *
	 * @return true, if is read locked
	 */
	public abstract boolean isReadLocked();

	/**
	 * Returns true if at least one read lock has been obtained by the current
	 * thread.
	 *
	 * @return true, if is read locked by current thread
	 */
	public abstract boolean isReadLockedByCurrentThread();

	/**
	 * Returns the current number of read locks on the current thread.
	 *
	 * @return the num read locks
	 */
	public abstract int getNumReadLocks();

}
