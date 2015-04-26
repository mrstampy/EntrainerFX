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

import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;

// TODO: Auto-generated Javadoc
/**
 * A thread safe list iterator using the {@link ThreadSafeList} instance to
 * manage concurrency.
 *
 * @author burton
 * @param <E>
 *          the element type
 */
public class ThreadSafeListIterator<E> implements ListIterator<E> {

	private ThreadSafeList<E> list;

	private AtomicInteger index = new AtomicInteger(-1);

	private int start;

	/**
	 * Instantiates a new thread safe list iterator.
	 *
	 * @param list
	 *          the list
	 */
	ThreadSafeListIterator(ThreadSafeList<E> list) {
		this(list, 0);
	}

	/**
	 * Instantiates a new thread safe list iterator.
	 *
	 * @param list
	 *          the list
	 * @param start
	 *          the start
	 */
	ThreadSafeListIterator(ThreadSafeList<E> list, int start) {
		this.list = list;
		this.start = start;
		index.set(start - 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ListIterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return list.readOp(() -> hasNextImpl());
	}

	private boolean hasNextImpl() {
		if (nextIndex() < start) index.set(start - 1);
		return nextIndex() < list.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ListIterator#next()
	 */
	@Override
	public E next() {
		return list.readOp(() -> nextImpl());
	}

	private E nextImpl() {
		if (!hasNextImpl()) return null;

		try {
			return list.get(nextIndex());
		} finally {
			index.incrementAndGet();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ListIterator#hasPrevious()
	 */
	@Override
	public boolean hasPrevious() {
		return list.readOp(() -> hasPreviousImpl());
	}

	private boolean hasPreviousImpl() {
		int size = list.size();
		if (previousIndex() > size) index.set(size);
		int idx = previousIndex();

		return size > start && idx >= start && idx < size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ListIterator#previous()
	 */
	@Override
	public E previous() {
		return list.readOp(() -> previousImpl());
	}

	private E previousImpl() {
		if (!hasPreviousImpl()) return null;

		try {
			return list.get(previousIndex());
		} finally {
			index.decrementAndGet();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ListIterator#nextIndex()
	 */
	@Override
	public int nextIndex() {
		return index.get() + 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ListIterator#previousIndex()
	 */
	@Override
	public int previousIndex() {
		return index.get() - 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ListIterator#remove()
	 */
	@Override
	public void remove() {
		list.remove(index.get());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ListIterator#set(java.lang.Object)
	 */
	@Override
	public void set(E e) {
		list.add(index.get(), e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ListIterator#add(java.lang.Object)
	 */
	@Override
	public void add(E e) {
		list.add(e);
	}

}
