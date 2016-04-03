/*
 *      ______      __             _                 _______  __
 *     / ____/___  / /__________ _(_)___  ___  _____/ ____/ |/ /
 *    / __/ / __ \/ __/ ___/ __ `/ / __ \/ _ \/ ___/ /_   |   / 
 *   / /___/ / / / /_/ /  / /_/ / / / / /  __/ /  / __/  /   |  
 *  /_____/_/ /_/\__/_/   \__,_/_/_/ /_/\___/_/  /_/    /_/|_|  
 *                                                          
 *
 * Copyright (C) 2008 - 2016 Burton Alexander
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
package net.sourceforge.entrainer.neuroph;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.neuroph.core.NeuralNetwork;

import com.github.mrstampy.esp.dsp.lab.SignalProcessedListener;

// TODO: Auto-generated Javadoc
/**
 * The Class PowerDataAggregator stores in memory the power samples which are
 * then used to train the network.
 * 
 * @see NeuralNetwork#learn(org.neuroph.core.data.DataSet)
 */
public class SignalDataAggregator implements SignalProcessedListener {
	private List<double[]> samples = new ArrayList<double[]>();

	private Lock lock = new ReentrantLock(true);

	private static final int MAX_SAMPLES = 1000; // 1000 seconds! someone went
																								// walkabout...

	/**
	 * Clears the samples.
	 */
	public void clear() {
		lock.lock();
		try {
			samples.clear();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Gets the samples.
	 *
	 * @return the samples
	 */
	public double[][] getSamples() {
		lock.lock();
		try {
			if (samples.isEmpty()) return null;

			List<double[]> list = new ArrayList<double[]>(samples);

			int cols = list.get(0).length;
			int rows = list.size();

			double[][] d = new double[rows][cols];

			for (int i = 0; i < rows; i++) {
				double[] sample = list.get(i);
				for (int j = 0; j < cols; j++) {
					d[i][j] = sample[j];
				}
			}

			return d;
		} finally {
			lock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.esp.dsp.lab.SignalProcessedListener#signalProcessed
	 * (double[])
	 */
	@Override
	public void signalProcessed(double[] processed) {
		double[] d = new double[Neuralizer.BUF_SIZE];
		System.arraycopy(processed, 0, d, 0, d.length);

		lock.lock();
		try {
			samples.add(d);
			if (samples.size() > MAX_SAMPLES) samples.remove(0);
		} finally {
			lock.unlock();
		}
	}

}
