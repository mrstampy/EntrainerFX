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

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.entrainer.util.concurrent.locker.LockerOperator.OperatorType;

// TODO: Auto-generated Javadoc
/**
 * This thread safe class cycles through integer values from zero to the size
 * specified - 1, looping indefinitely when a boundary (0, size - 1) is reached.
 * 
 * @author burton
 *
 */
public class RandomCyclicInteger extends AbstractLockable {

	private AtomicInteger size = new AtomicInteger();

	private AtomicInteger value = new AtomicInteger();
	private AtomicBoolean forward = new AtomicBoolean(true);

	private Random rand = new Random(System.nanoTime());

	/**
	 * Instantiate with a size >= 0. Default
	 * {@link OperatorType#REENTRANT_READ_WRITE}.
	 *
	 * @param size
	 *          the size
	 */
	public RandomCyclicInteger(int size) {
		this(size, OperatorType.REENTRANT_READ_WRITE);
	}

	/**
	 * Instantiate with a size >= 0 and an {@link OperatorType} to manage
	 * concurrency.
	 *
	 * @param size
	 *          the size
	 * @param type
	 *          the type
	 */
	public RandomCyclicInteger(int size, OperatorType type) {
		super(type);
		setSize(size);
	}

	/**
	 * Returns a random value between 0 and size - 1 inclusive.
	 *
	 * @return the random
	 */
	public int getRandom() {
		return optimisticRead(() -> rand.nextInt(getSize()));
	}

	/**
	 * Returns the next value. Will be previous + 1 if forward, previous - 1 if
	 * reverse, looping on the boundaries (0, size - 1)
	 *
	 * @return the next
	 */
	public int getNext() {
		return readOp(() -> getNextImpl());
	}

	/**
	 * Returns the current value.
	 *
	 * @return the current
	 */
	public int getCurrent() {
		return optimisticRead(() -> value.get());
	}

	/**
	 * Returns true if iterating forward.
	 *
	 * @return true, if is forward
	 */
	public boolean isForward() {
		return forward.get();
	}

	/**
	 * Calls to {@link #getNext()} will return previous + 1 if true, else previous
	 * - 1, looping on the boundaries (0, size - 1).
	 *
	 * @param b
	 *          the new forward
	 */
	public void setForward(boolean b) {
		forward.set(b);
	}

	/**
	 * Returns the size for cycling.
	 *
	 * @return the size
	 */
	public int getSize() {
		return size.get();
	}

	/**
	 * Sets the size for cycling.
	 *
	 * @param size
	 *          the new size
	 */
	public void setSize(int size) {
		writeOp(() -> setSizeImpl(size));
	}

	private void setSizeImpl(int size) {
		if (getSize() == size) return;

		if (size < 0) throw new IllegalStateException("size must be >= 0: " + size);

		this.size.set(size);
	}

	private int getNextImpl() {
		return value.getAndUpdate(operand -> calculateValue(operand));
	}

	private int calculateValue(int operand) {
		return isForward() ? getForwardValue(operand) : getReverseValue(operand);
	}

	private int getForwardValue(int operand) {
		return operand >= getSize() - 1 ? 0 : operand + 1;
	}

	private int getReverseValue(int operand) {
		return operand <= 0 ? getSize() - 1 : operand - 1;
	}

}
