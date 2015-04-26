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

import java.util.concurrent.locks.ReentrantLock;

// TODO: Auto-generated Javadoc
/**
 * {@link LockerOperator} implementation using a {@link ReentrantLock}.
 * 
 * @author burton
 *
 */
public class ReentrantOperator extends LockerOperator<ReentrantLock> {

	/**
	 * Instantiates a new reentrant operator.
	 */
	public ReentrantOperator() {
		this(new ReentrantLock());
	}

	/**
	 * Instantiates a new reentrant operator.
	 *
	 * @param fair
	 *          the fair
	 */
	public ReentrantOperator(boolean fair) {
		this(new ReentrantLock(fair));
	}

	/**
	 * Instantiates a new reentrant operator.
	 *
	 * @param locker
	 *          the locker
	 */
	public ReentrantOperator(ReentrantLock locker) {
		super(OperatorType.REENTRANT, locker);
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
		return writeOp(r);
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
		getLocker().lock();
		try {
			return r.execute();
		} finally {
			getLocker().unlock();
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
		writeOp(r);
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
		getLocker().lock();
		try {
			r.run();
		} finally {
			getLocker().unlock();
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
		return writeOp(r);
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
		writeOp(r);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#readLock()
	 */
	@Override
	public void readLock() {
		getLocker().lock();
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
		getLocker().unlock();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#lock()
	 */
	@Override
	public void lock() {
		getLocker().lock();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#unlock()
	 */
	@Override
	public void unlock() {
		getLocker().unlock();
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
		return getLocker().isHeldByCurrentThread();
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
		return isLocked();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#
	 * isReadLockedByCurrentThread()
	 */
	@Override
	public boolean isReadLockedByCurrentThread() {
		return isLockedByCurrentThread();
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
		return isLocked() ? 1 : 0;
	}

}
