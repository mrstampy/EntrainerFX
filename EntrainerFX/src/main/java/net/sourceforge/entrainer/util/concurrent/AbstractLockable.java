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
package net.sourceforge.entrainer.util.concurrent;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.StampedLock;

import net.sourceforge.entrainer.util.concurrent.locker.LockerOperator;
import net.sourceforge.entrainer.util.concurrent.locker.LockerOperator.OperatorType;
import net.sourceforge.entrainer.util.concurrent.locker.ReentrantOperator;
import net.sourceforge.entrainer.util.concurrent.locker.ReentrantReadWriteOperator;
import net.sourceforge.entrainer.util.concurrent.locker.Returnable;
import net.sourceforge.entrainer.util.concurrent.locker.StampedOperator;

// TODO: Auto-generated Javadoc
/**
 * Convenience superclass of objects which use {@link LockerOperator}s to manage
 * thread concurrency.
 * 
 * @author burton
 *
 */
public abstract class AbstractLockable {

	private AtomicReference<LockerOperator<?>> operator = new AtomicReference<>();

	/**
	 * Instantiate with the desired {@link LockerOperator}.
	 *
	 * @param operator
	 *          the operator
	 */
	protected AbstractLockable(LockerOperator<?> operator) {
		setOperator(operator);
	}

	/**
	 * Instantiate specifying the {@link LockerOperator} {@link OperatorType}.
	 *
	 * @param type
	 *          the type
	 */
	protected AbstractLockable(OperatorType type) {
		switch (type) {
		case REENTRANT_READ_WRITE:
			setOperator(new ReentrantReadWriteOperator());
			break;
		case STAMPED:
			setOperator(new StampedOperator());
			break;
		case REENTRANT:
			setOperator(new ReentrantOperator());
			break;
		default:
			break;
		}
	}

	/**
	 * Returns the {@link LockerOperator} used to manage concurrency.
	 *
	 * @return the operator
	 */
	public LockerOperator<?> getOperator() {
		return operator.get();
	}

	/**
	 * Sets the {@link LockerOperator} to manage concurrency.
	 *
	 * @param operator
	 *          the new operator
	 */
	public void setOperator(LockerOperator<?> operator) {
		Objects.requireNonNull(operator);

		this.operator.set(operator);
	}

	/**
	 * Performs a read lock operation returning the returnable value.
	 *
	 * @param <T>
	 *          the generic type
	 * @param r
	 *          the r
	 * @return the t
	 */
	public <T> T readOp(Returnable<T> r) {
		return getOperator().readOp(r);
	}

	/**
	 * Performs a write lock operation returning the returnable value.
	 *
	 * @param <T>
	 *          the generic type
	 * @param r
	 *          the r
	 * @return the t
	 */
	public <T> T writeOp(Returnable<T> r) {
		return getOperator().writeOp(r);
	}

	/**
	 * Performs a read lock operation.
	 *
	 * @param r
	 *          the r
	 */
	public void readOp(Runnable r) {
		getOperator().readOp(r);
	}

	/**
	 * Performs a write lock operation.
	 *
	 * @param r
	 *          the r
	 */
	public void writeOp(Runnable r) {
		getOperator().writeOp(r);
	}

	/**
	 * Performs an optimistic read (small) operation returning the returnable
	 * value. See the documentation of {@link StampedLock} for more information.
	 *
	 * @param <T>
	 *          the generic type
	 * @param r
	 *          the r
	 * @return the t
	 */
	public <T> T optimisticRead(Returnable<T> r) {
		return getOperator().optimisticRead(r);
	}

	/**
	 * Performs an optimistic read (small) operation. See the documentation of
	 * {@link StampedLock} for more information.
	 *
	 * @param r
	 *          the r
	 */
	public void optimisticRead(Runnable r) {
		getOperator().optimisticRead(r);
	}
}
