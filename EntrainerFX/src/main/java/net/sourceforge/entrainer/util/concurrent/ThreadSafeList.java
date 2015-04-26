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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

import net.sourceforge.entrainer.util.concurrent.locker.LockerOperator;
import net.sourceforge.entrainer.util.concurrent.locker.LockerOperator.OperatorType;

// TODO: Auto-generated Javadoc
/**
 * An implementation of a thread safe list which uses {@link LockerOperator}s to
 * manage concurrency.
 *
 * @author burton
 * @param <E>
 *          the element type
 */
public class ThreadSafeList<E> extends AbstractLockable implements List<E> {

	private AtomicReference<List<E>> list = new AtomicReference<>();

	/**
	 * Creates a new {@link ThreadSafeList} with an ArrayList delegate and
	 * {@link OperatorType#REENTRANT_READ_WRITE} operator for concurrency.
	 */
	public ThreadSafeList() {
		this(new ArrayList<>());
	}

	/**
	 * Creates a new {@link ThreadSafeList} with an ArrayList delegate and
	 * specified operator type for concurrency.
	 *
	 * @param type
	 *          the type
	 */
	public ThreadSafeList(OperatorType type) {
		this(new ArrayList<>(), type);
	}

	/**
	 * Creates a new {@link ThreadSafeList} with the specified delegate and
	 * {@link OperatorType#REENTRANT_READ_WRITE} operator for concurrency.
	 *
	 * @param delegate
	 *          the delegate
	 */
	public ThreadSafeList(List<E> delegate) {
		this(delegate, OperatorType.REENTRANT_READ_WRITE);
	}

	/**
	 * Creates a new {@link ThreadSafeList} with the specified delegate and
	 * operator type for concurrency.
	 *
	 * @param delegate
	 *          the delegate
	 * @param type
	 *          the type
	 */
	public ThreadSafeList(List<E> delegate, OperatorType type) {
		super(type);
		setDelegate(delegate);
	}

	/**
	 * Returns the delegate. Note that any actions taken directly on the delegate
	 * ARE NOT THREAD SAFE.
	 *
	 * @return the delegate
	 */
	public List<E> getDelegate() {
		return list.get();
	}

	/**
	 * Sets the delegate list. Thread safe.
	 *
	 * @param delegate
	 *          the new delegate
	 */
	public void setDelegate(List<E> delegate) {
		Objects.requireNonNull(delegate);

		writeOp(() -> list.set(delegate));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#size()
	 */
	@Override
	public int size() {
		return optimisticRead(() -> getDelegate().size());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return optimisticRead(() -> getDelegate().isEmpty());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		return readOp(() -> getDelegate().contains(o));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		return readOp(() -> copyDelegate().iterator());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#toArray()
	 */
	@Override
	public Object[] toArray() {
		return readOp(() -> getDelegate().toArray());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#toArray(java.lang.Object[])
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		return readOp(() -> getDelegate().toArray(a));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#add(java.lang.Object)
	 */
	@Override
	public boolean add(E e) {
		return writeOp(() -> getDelegate().add(e));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		return writeOp(() -> getDelegate().remove(o));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return readOp(() -> getDelegate().containsAll(c));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		return writeOp(() -> getDelegate().addAll(c));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		return writeOp(() -> getDelegate().removeAll(c));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		return writeOp(() -> getDelegate().retainAll(c));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#clear()
	 */
	@Override
	public void clear() {
		writeOp(() -> getDelegate().clear());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		return writeOp(() -> getDelegate().addAll(index, c));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#get(int)
	 */
	@Override
	public E get(int index) {
		return readOp(() -> getImpl(index));
	}

	private E getImpl(int index) {
		List<E> delegate = getDelegate();

		return index >= 0 && index < delegate.size() ? delegate.get(index) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	@Override
	public E set(int index, E element) {
		return writeOp(() -> getDelegate().set(index, element));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	@Override
	public void add(int index, E element) {
		writeOp(() -> getDelegate().add(index, element));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#remove(int)
	 */
	@Override
	public E remove(int index) {
		return writeOp(() -> removeImpl(index));
	}

	private E removeImpl(int index) {
		List<E> delegate = getDelegate();

		return (index >= 0 && index < delegate.size()) ? delegate.remove(index) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	@Override
	public int indexOf(Object o) {
		return readOp(() -> getDelegate().indexOf(o));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	@Override
	public int lastIndexOf(Object o) {
		return readOp(() -> getDelegate().lastIndexOf(o));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#listIterator()
	 */
	@Override
	public ListIterator<E> listIterator() {
		return new ThreadSafeListIterator<>(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#listIterator(int)
	 */
	@Override
	public ListIterator<E> listIterator(int index) {
		return new ThreadSafeListIterator<>(this, index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#subList(int, int)
	 */
	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return readOp(() -> getDelegate().subList(fromIndex, toIndex));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#replaceAll(java.util.function.UnaryOperator)
	 */
	@Override
	public void replaceAll(UnaryOperator<E> operator) {
		writeOp(() -> replaceAllImpl(operator));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#sort(java.util.Comparator)
	 */
	@Override
	public void sort(Comparator<? super E> c) {
		writeOp(() -> sortImpl(c));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#spliterator()
	 */
	@Override
	public Spliterator<E> spliterator() {
		return readOp(() -> Spliterators.spliterator(copyDelegate(), Spliterator.ORDERED));
	}

	private List<E> copyDelegate() {
		return new ArrayList<>(getDelegate());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void sortImpl(Comparator<? super E> c) {
		Object[] a = toArray();
		Arrays.sort(a, (Comparator) c);
		ListIterator<E> i = getDelegate().listIterator();
		for (Object e : a) {
			i.next();
			i.set((E) e);
		}
	}

	private void replaceAllImpl(UnaryOperator<E> operator) {
		Objects.requireNonNull(operator);
		final ListIterator<E> li = getDelegate().listIterator();
		while (li.hasNext()) {
			li.set(operator.apply(li.next()));
		}
	}

}
