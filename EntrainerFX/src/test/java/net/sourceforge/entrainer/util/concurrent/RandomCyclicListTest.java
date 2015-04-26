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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sourceforge.entrainer.util.concurrent.locker.LockerOperator.OperatorType;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * Unit test to test the basic functionality of the {@link RandomCyclicList}.
 */
public class RandomCyclicListTest {

	private static final int ITEM_COUNT = 1000;

	private static final int POOL_SIZE = 1000;

	private RandomCyclicList<String> rcl = new RandomCyclicList<>(OperatorType.REENTRANT_READ_WRITE);

	private static ExecutorService svc = Executors.newFixedThreadPool(POOL_SIZE);

	private ConcurrentLinkedQueue<String> output = new ConcurrentLinkedQueue<>();

	private int[] counts = new int[ITEM_COUNT];

	/**
	 * Before.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Before
	public void before() throws Exception {
		if (rcl.isEmpty()) populate();
		Arrays.fill(counts, 0);
	}

	/**
	 * After.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@After
	public void after() throws Exception {
		output.clear();
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

	private void populate() {
		for (int i = 0; i < ITEM_COUNT; i++) {
			rcl.add("Item " + i);
		}
	}

	/**
	 * Test cycle.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void testCycle() throws Exception {
		rcl.setForward(true);
		testCycleImpl();
	}

	/**
	 * Test reverse cycle.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void testReverseCycle() throws Exception {
		rcl.setForward(false);
		testCycleImpl();
	}

	private void testCycleImpl() throws InterruptedException {
		CountDownLatch cdl = new CountDownLatch(POOL_SIZE);

		for (int i = 0; i < POOL_SIZE; i++) {
			svc.execute(() -> startNextThread(cdl));
		}

		cdl.await();

		output.forEach(e -> eval(e));

		for (int i = 0; i < ITEM_COUNT; i++) {
			assertEquals(POOL_SIZE, counts[i]);
		}
	}

	private void eval(String e) {
		String[] parts = e.split(" ");

		int idx = Integer.parseInt(parts[1]);

		counts[idx] = counts[idx] + 1;
	}

	private void startNextThread(CountDownLatch cdl) {
		for (int i = 0; i < ITEM_COUNT; i++) {
			output.add(rcl.getNext());
		}

		cdl.countDown();
	}

}
