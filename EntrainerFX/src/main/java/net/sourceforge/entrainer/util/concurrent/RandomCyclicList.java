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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.entrainer.util.concurrent.locker.LockerOperator;
import net.sourceforge.entrainer.util.concurrent.locker.LockerOperator.OperatorType;
import net.sourceforge.entrainer.util.concurrent.locker.Returnable;

// TODO: Auto-generated Javadoc
/**
 * A {@link ThreadSafeList} which provides the ability to cycle forwards or
 * reverse in the list and can return a random element from the list.
 *
 * @author burton
 * @param <E>
 *          the element type
 */
public class RandomCyclicList<E> extends ThreadSafeList<E> {

	private RandomCyclicInteger rci = new RandomCyclicInteger(0);

	private enum IdxType {
		NEXT, CURRENT, RANDOM;
	}

	/**
	 * Creates a new {@link RandomCyclicList} with an ArrayList as the delegate
	 * and an {@link OperatorType#REENTRANT_READ_WRITE} operator to manage
	 * concurrency.
	 */
	public RandomCyclicList() {
		this(new ArrayList<>());
	}

	/**
	 * Creates a new {@link RandomCyclicList} with an ArrayList as the delegate
	 * and the specified operator type to manage concurrency.
	 *
	 * @param type
	 *          the type
	 */
	public RandomCyclicList(OperatorType type) {
		this(new ArrayList<>(), type);
	}

	/**
	 * Creates a new {@link RandomCyclicList} with the specified list as the
	 * delegate and an {@link OperatorType#REENTRANT_READ_WRITE} operator to
	 * manage concurrency.
	 *
	 * @param c
	 *          the c
	 */
	public RandomCyclicList(List<E> c) {
		this(c, OperatorType.REENTRANT_READ_WRITE);
	}

	/**
	 * Creates a new {@link RandomCyclicList} with the specified list as the
	 * delegate and the specified operator type to manage concurrency.
	 *
	 * @param c
	 *          the c
	 * @param type
	 *          the type
	 */
	public RandomCyclicList(List<E> c, OperatorType type) {
		super(c, type);
		rci.setOperator(getOperator());
	}

	/**
	 * Overridden to ensure the underlying {@link RandomCyclicInteger} is using
	 * the same operator.
	 *
	 * @param operator
	 *          the new operator
	 */
	public void setOperator(LockerOperator<?> operator) {
		LockerOperator<?> existing = getOperator();

		if (existing == null) {
			super.setOperator(operator);
		} else {
			existing.writeOp(() -> setOperatorImpl(operator));
		}
	}

	private void setOperatorImpl(LockerOperator<?> operator) {
		super.setOperator(operator);
		rci.setOperator(operator);
	}

	/**
	 * Returns true if cycling forward in the list.
	 *
	 * @return true, if is forward
	 */
	public boolean isForward() {
		return rci.isForward();
	}

	/**
	 * If true will cycle forward, else reverse.
	 *
	 * @param b
	 *          the new forward
	 */
	public void setForward(boolean b) {
		rci.setForward(b);
	}

	/**
	 * Returns the next element in the list.
	 *
	 * @return the next
	 * @see #isForward()
	 */
	public E getNext() {
		return getImpl(IdxType.NEXT);
	}

	/**
	 * Returns the current element in the list.
	 *
	 * @return the current
	 */
	public E getCurrent() {
		return getImpl(IdxType.CURRENT);
	}

	/**
	 * Returns a random element from the list.
	 *
	 * @return the random
	 */
	public E getRandom() {
		return getImpl(IdxType.RANDOM);
	}

	/**
	 * Overridden to ensure the underlying {@link RandomCyclicInteger} is kept in
	 * sync with list size changes.
	 *
	 * @param <T>
	 *          the generic type
	 * @param r
	 *          the r
	 * @return the t
	 */
	public <T> T writeOp(Returnable<T> r) {
		try {
			getOperator().lock();
			try {
				return r.execute();
			} finally {
				sizeCheck();
				getOperator().unlock();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Overridden to ensure the underlying {@link RandomCyclicInteger} is kept in
	 * sync with list size changes.
	 *
	 * @param r
	 *          the r
	 */
	public void writeOp(Runnable r) {
		try {
			getOperator().lock();
			try {
				r.run();
			} finally {
				sizeCheck();
				getOperator().unlock();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void sizeCheck() {
		if (rci == null) return; // instantiating...

		int size = getDelegate().size();
		if (size != rci.getSize()) rci.setSize(size);
	}

	private E getImpl(IdxType type) {
		return readOp(() -> getDelegate().isEmpty() ? null : getDelegate().get(getIndex(type)));
	}

	private int getIndex(IdxType type) {
		switch (type) {
		case CURRENT:
			return rci.getCurrent();
		case NEXT:
			return rci.getNext();
		default:
			return rci.getRandom();
		}
	}
}
