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

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.AfterClass;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * Unit test to test the basic functionality of {@link RandomCyclicInteger}.
 */
public class RandomCyclicIntegerTest {

	private static final int SIZE = 1000;

	private static final int POOL_SIZE = 1000;

	private RandomCyclicInteger rci = new RandomCyclicInteger(SIZE);

	private static ExecutorService svc = Executors.newFixedThreadPool(POOL_SIZE);

	private ConcurrentLinkedQueue<Integer> output = new ConcurrentLinkedQueue<>();

	private int[] counts = new int[SIZE];

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
	 * Test rci.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void testRci() throws Exception {
		CountDownLatch cdl = new CountDownLatch(POOL_SIZE);

		for (int i = 0; i < POOL_SIZE; i++) {
			svc.execute(() -> startNextThread(cdl));
		}

		cdl.await();

		output.forEach(e -> eval(e));

		for (int count : counts) {
			assertEquals(POOL_SIZE, count);
		}
	}

	/**
	 * Test reverse rci.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void testReverseRci() throws Exception {
		rci.setForward(false);
		testRci();
	}

	private void eval(Integer e) {
		counts[e] = counts[e] + 1;
	}

	private void startNextThread(CountDownLatch cdl) {
		for (int i = 0; i < SIZE; i++) {
			output.add(rci.getNext());
		}

		cdl.countDown();
	}

}
