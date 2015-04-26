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

import java.util.concurrent.locks.StampedLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * {@link LockerOperator} implementation using {@link StampedLock}s.
 * 
 * @author burton
 *
 */
public class StampedOperator extends LockerOperator<StampedLock> {
	private static final Logger log = LoggerFactory.getLogger(StampedOperator.class);

	private ThreadLocal<Long> readLocks = new ThreadLocal<>();
	private ThreadLocal<Integer> numReadLocks = new ThreadLocal<>();

	private ThreadLocal<Long> writeLocks = new ThreadLocal<>();

	/**
	 * Instantiates a new stamped operator.
	 */
	public StampedOperator() {
		super(OperatorType.STAMPED, new StampedLock());
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

		readLocks.set(getLocker().readLock());
		incrementReadLocks();
		try {
			return r.execute();
		} finally {
			getLocker().unlockRead(readLocks.get());
			decrementReadLocks();
			if (getNumReadLocks() == 0) readLocks.set(0l);
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

		writeLocks.set(getLocker().writeLock());
		try {
			return r.execute();
		} finally {
			getLocker().unlockWrite(writeLocks.get());
			writeLocks.set(0l);
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

		readLocks.set(getLocker().readLock());
		incrementReadLocks();
		try {
			r.run();
		} finally {
			getLocker().unlockRead(readLocks.get());
			decrementReadLocks();
			if (getNumReadLocks() == 0) readLocks.set(0l);
		}
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

		writeLocks.set(getLocker().writeLock());
		try {
			r.run();
		} finally {
			getLocker().unlockWrite(writeLocks.get());
			writeLocks.set(0l);
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
		long stamp = getLocker().tryOptimisticRead();
		if (stamp <= 0) return readOp(r);

		boolean check = true;
		T val;
		try {
			val = r.execute();
		} catch (Exception e) {
			log.debug("Unexpected exception, upgrading to read lock", e);
			val = readOp(r);
			check = false;
		} finally {
			if (check && !getLocker().validate(stamp)) val = readOp(r);
		}

		return val;
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
		long stamp = getLocker().tryOptimisticRead();
		if (stamp <= 0) {
			readOp(r);
			return;
		}

		boolean check = true;
		try {
			r.run();
		} catch (Exception e) {
			log.debug("Unexpected exception, upgrading to read lock", e);
			readOp(r);
			check = false;
		} finally {
			if (check && !getLocker().validate(stamp)) readOp(r);
		}
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

		readLocks.set(getLocker().readLock());
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

		if (getNumReadLocks() == 1) getLocker().unlockRead(readLocks.get());

		decrementReadLocks();

		if (getNumReadLocks() == 0) readLocks.set(0l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#lock()
	 */
	@Override
	public void lock() {
		if (isLockedByCurrentThread()) return;

		writeLocks.set(getLocker().writeLock());
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

		try {
			getLocker().unlock(writeLocks.get());
		} finally {
			writeLocks.set(0l);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#isLocked()
	 */
	@Override
	public boolean isLocked() {
		return getLocker().isWriteLocked();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#
	 * isLockedByCurrentThread()
	 */
	@Override
	public boolean isLockedByCurrentThread() {
		Long stamp = writeLocks.get();
		return stamp != null && stamp > 0;
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
		return getLocker().isReadLocked();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.util.concurrent.locker.LockerOperator#
	 * isReadLockedByCurrentThread()
	 */
	@Override
	public boolean isReadLockedByCurrentThread() {
		Long stamp = readLocks.get();
		return stamp != null && stamp > 0;
	}

}
