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

import java.util.concurrent.locks.ReentrantReadWriteLock;

// TODO: Auto-generated Javadoc
/**
 * {@link LockerOperator} implementation using {@link ReentrantReadWriteLock}s.
 *
 * @author burton
 */
public class ReentrantReadWriteOperator extends LockerOperator<ReentrantReadWriteLocker> {

	private ThreadLocal<Integer> numReadLocks = new ThreadLocal<>();
	private ThreadLocal<Boolean> locked = new ThreadLocal<>();

	/**
	 * Instantiates a new reentrant read write operator.
	 */
	public ReentrantReadWriteOperator() {
		super(OperatorType.REENTRANT_READ_WRITE, new ReentrantReadWriteLocker());
	}

	/**
	 * Instantiates a new reentrant read write operator.
	 *
	 * @param fair
	 *          the fair
	 */
	public ReentrantReadWriteOperator(boolean fair) {
		super(OperatorType.REENTRANT_READ_WRITE, new ReentrantReadWriteLocker(fair));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#readOp(
	 * net.sourceforge.entrainer.util.concurrent.locker.Returnable)
	 */
	@Override
	public <T> T readOp(Returnable<T> r) {
		if (isReadLockedByCurrentThread()) {
			incrementReadLocks();
			return r.execute();
		}

		getLocker().readLock();
		incrementReadLocks();
		try {
			return r.execute();
		} finally {
			getLocker().readUnlock();
			decrementReadLocks();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#writeOp
	 * (net.sourceforge.entrainer.util.concurrent.locker.Returnable)
	 */
	@Override
	public <T> T writeOp(Returnable<T> r) {
		if (isLockedByCurrentThread()) return r.execute();

		getLocker().lock();
		setLocked(true);
		try {
			return r.execute();
		} finally {
			getLocker().unlock();
			setLocked(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#readOp(
	 * java.lang.Runnable)
	 */
	@Override
	public void readOp(Runnable r) {
		if (isReadLockedByCurrentThread()) {
			incrementReadLocks();
			r.run();
			return;
		}

		getLocker().readLock();
		incrementReadLocks();
		try {
			r.run();
		} finally {
			getLocker().readUnlock();
			decrementReadLocks();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#writeOp
	 * (java.lang.Runnable)
	 */
	@Override
	public void writeOp(Runnable r) {
		if (isLockedByCurrentThread()) {
			r.run();
			return;
		}

		getLocker().lock();
		setLocked(true);
		try {
			r.run();
		} finally {
			getLocker().unlock();
			setLocked(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#optimisticRead
	 * (net.sourceforge.entrainer.util.concurrent.locker.Returnable)
	 */
	@Override
	public <T> T optimisticRead(Returnable<T> r) {
		return readOp(r);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#optimisticRead
	 * (java.lang.Runnable)
	 */
	@Override
	public void optimisticRead(Runnable r) {
		readOp(r);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#readLock()
	 */
	@Override
	public void readLock() {
		if (isReadLockedByCurrentThread()) {
			incrementReadLocks();
			return;
		}

		getLocker().readLock();
		incrementReadLocks();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#readUnlock
	 * ()
	 */
	@Override
	public void readUnlock() {
		if (!isReadLockedByCurrentThread()) return;

		decrementReadLocks();

		if (getNumReadLocks() == 0) getLocker().readUnlock();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#lock()
	 */
	@Override
	public void lock() {
		if (isLockedByCurrentThread()) return;

		getLocker().lock();
		setLocked(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#unlock()
	 */
	@Override
	public void unlock() {
		if (!isLockedByCurrentThread()) return;

		getLocker().unlock();
		setLocked(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#isLocked()
	 */
	@Override
	public boolean isLocked() {
		return getLocker().isLocked();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#
	 * isLockedByCurrentThread()
	 */
	@Override
	public boolean isLockedByCurrentThread() {
		Boolean b = locked.get();

		return b != null && b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#isReadLocked
	 * ()
	 */
	@Override
	public boolean isReadLocked() {
		return getLocker().getReadLockCount() > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#
	 * isReadLockedByCurrentThread()
	 */
	@Override
	public boolean isReadLockedByCurrentThread() {
		return getNumReadLocks() > 0;
	}

	private void incrementReadLocks() {
		numReadLocks.set(getNumReadLocks() + 1);
	}

	private void decrementReadLocks() {
		numReadLocks.set(getNumReadLocks() - 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#getNumReadLocks
	 * ()
	 */
	@Override
	public int getNumReadLocks() {
		Integer num = numReadLocks.get();

		if (num == null) {
			num = 0;
			numReadLocks.set(0);
		}

		return num;
	}

	private void setLocked(boolean b) {
		locked.set(b);
	}

}
