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
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.entrainer.util.concurrent.locker.LockerOperator;
import net.sourceforge.entrainer.util.concurrent.locker.LockerOperator.OperatorType;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * Unit test to test the thread safety of {@link ThreadSafeList} using all the
 * various {@link LockerOperator} implementations.
 */
public class ThreadSafeListTest {

	private static final int SNOOZE = 2;

	private static final int THREADS = 50;

	private static ExecutorService svc = Executors.newFixedThreadPool(THREADS * 3);

	private ThreadSafeList<String> tsl;

	private volatile boolean running = false;

	private Random rand = new Random(System.nanoTime());
	private Random getRand = new Random(System.nanoTime());
	private Random removeRand = new Random(System.nanoTime());

	private AtomicInteger adds = new AtomicInteger();
	private AtomicInteger removes = new AtomicInteger();
	private AtomicInteger gets = new AtomicInteger();

	/**
	 * Before.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Before
	public void before() throws Exception {
		adds.set(0);
		removes.set(0);
		gets.set(0);
	}

	/**
	 * After class.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@AfterClass
	public static void afterClass() throws Exception {
		svc.shutdownNow();
	}

	/**
	 * Test get set remove stamped.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void testGetSetRemoveStamped() throws Exception {
		tsl = new ThreadSafeList<>(OperatorType.STAMPED);
		populateList();
		testGetSetRemoveImpl(OperatorType.STAMPED);
	}

	/**
	 * Test get set remove reentrant read write.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void testGetSetRemoveReentrantReadWrite() throws Exception {
		tsl = new ThreadSafeList<>(OperatorType.REENTRANT_READ_WRITE);
		populateList();
		testGetSetRemoveImpl(OperatorType.REENTRANT_READ_WRITE);
	}

	/**
	 * Test get set remove reentrant.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void testGetSetRemoveReentrant() throws Exception {
		tsl = new ThreadSafeList<>(OperatorType.REENTRANT);
		populateList();
		testGetSetRemoveImpl(OperatorType.REENTRANT);
	}

	/**
	 * Test list iterator reentrant read write.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void testListIteratorReentrantReadWrite() throws Exception {
		tsl = new ThreadSafeList<>(OperatorType.REENTRANT_READ_WRITE);
		populateList();

		testListIteratorImpl(OperatorType.REENTRANT_READ_WRITE);
	}

	/**
	 * Test list iterator reentrant.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void testListIteratorReentrant() throws Exception {
		tsl = new ThreadSafeList<>(OperatorType.REENTRANT);
		populateList();

		testListIteratorImpl(OperatorType.REENTRANT);
	}

	/**
	 * Test list iterator stamped.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void testListIteratorStamped() throws Exception {
		tsl = new ThreadSafeList<>(OperatorType.STAMPED);
		populateList();

		testListIteratorImpl(OperatorType.STAMPED);
	}

	private void testListIteratorImpl(OperatorType type) {
		running = true;

		svc.execute(() -> runListIterator(tsl.listIterator()));

		for (int i = 0; i < THREADS; i++) {
			svc.execute(() -> runAdd());
			svc.execute(() -> runRemove());
		}

		snooze(10000);

		running = false;

		System.out.println();
		System.out.println("List Iterator results for " + type);
		System.out.println();
		System.out.println("adds: " + adds.get());
		System.out.println("gets: " + gets.get());
		System.out.println("removes: " + removes.get());
		System.out.println();
	}

	private void runListIterator(ListIterator<String> it) {
		boolean forward = true;
		while (running) {
			snooze();

			if (forward) {
				iterForward(it);
			} else {
				iterBackward(it);
			}

			forward = !forward;
		}
	}

	private void iterBackward(ListIterator<String> it) {
		while (it.hasPrevious()) {
			it.previous();
			if (it.hasNext()) it.nextIndex();
			gets.incrementAndGet();
		}
	}

	private void iterForward(ListIterator<String> it) {
		while (it.hasNext()) {
			it.next();
			if (it.hasPrevious()) it.previousIndex();
			gets.incrementAndGet();
		}
	}

	private void testGetSetRemoveImpl(OperatorType type) {
		running = true;

		for (int i = 0; i < THREADS; i++) {
			svc.execute(() -> runAdd());
			svc.execute(() -> runRemove());
			svc.execute(() -> runGet());
		}

		snooze(10000);

		running = false;

		System.out.println();
		System.out.println("Get, Set, Remove results for " + type);
		System.out.println();
		System.out.println("adds: " + adds.get());
		System.out.println("gets: " + gets.get());
		System.out.println("removes: " + removes.get());
		System.out.println();
	}

	private void runAdd() {
		while (running) {
			snooze();
			tsl.add(Long.toString(System.nanoTime()));
			adds.incrementAndGet();
		}
	}

	private void runRemove() {
		while (running) {
			snooze();
			tsl.remove(removeRand.nextInt(getSize()));
			removes.incrementAndGet();
		}
	}

	private int getSize() {
		int size = tsl.size();
		while (size <= 0) {
			size = tsl.size();
		}

		return size;
	}

	private void runGet() {
		while (running) {
			snooze();
			tsl.get(getRand.nextInt(getSize()));
			gets.incrementAndGet();
		}
	}

	private void populateList() {
		for (int i = 0; i < 1000; i++) {
			tsl.add("Item " + i);
		}
	}

	private void snooze() {
		snooze(rand.nextInt(SNOOZE));
	}

	private void snooze(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}

}
