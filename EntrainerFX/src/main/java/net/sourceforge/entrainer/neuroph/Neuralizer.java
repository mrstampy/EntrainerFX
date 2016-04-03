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

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;

import com.github.mrstampy.esp.dsp.lab.SignalProcessedListener;

// TODO: Auto-generated Javadoc
/**
 * The Class Neuralizer.
 */
public class Neuralizer implements SignalProcessedListener {

	/** The Constant BUF_SIZE. */
	static final int BUF_SIZE = 41;

	private NetworkContainer network;

	private int numOutputNeurons;

	private List<NetworkProcessedListener> listeners = new ArrayList<>();

	/**
	 * Instantiates a new neuralizer.
	 *
	 * @param numNeurons
	 *          the num neurons
	 */
	public Neuralizer(int numNeurons) {
		setNumOutputNeurons(numNeurons);
		initNetwork();
	}

	/**
	 * Adds the network processed listener.
	 *
	 * @param l
	 *          the l
	 */
	public void addNetworkProcessedListener(NetworkProcessedListener l) {
		listeners.add(l);
	}

	/**
	 * Removes the network processed listener.
	 *
	 * @param l
	 *          the l
	 */
	public void removeNetworkProcessedListener(NetworkProcessedListener l) {
		listeners.remove(l);
	}

	/**
	 * Gets the signal weights.
	 *
	 * @return the signal weights
	 */
	public Double[] getSignalWeights() {
		return network.getWeights();
	}

	/**
	 * Sets the signal weights.
	 *
	 * @param weights
	 *          the new signal weights
	 */
	public void setSignalWeights(double[] weights) {
		assert weights != null && weights.length == getSignalWeights().length;

		network.setWeights(weights);
	}

	/**
	 * Gets the weight.
	 *
	 * @param inNeuronIdx
	 *          the in neuron idx
	 * @param outNeuronIdx
	 *          the out neuron idx
	 * @param inLayerIdx
	 *          the in layer idx
	 * @return the weight
	 */
	public double getWeight(int inNeuronIdx, int outNeuronIdx, int inLayerIdx) {
		return network.getWeight(inNeuronIdx, outNeuronIdx, inLayerIdx);
	}

	/**
	 * Sets the weight.
	 *
	 * @param inNeuronIdx
	 *          the in neuron idx
	 * @param outNeuronIdx
	 *          the out neuron idx
	 * @param weight
	 *          the weight
	 * @param inLayerIdx
	 *          the in layer idx
	 */
	public void setWeight(int inNeuronIdx, int outNeuronIdx, double weight, int inLayerIdx) {
		network.setWeight(inNeuronIdx, outNeuronIdx, weight, inLayerIdx);
	}

	/**
	 * Train signal network.
	 *
	 * @param inputs
	 *          the inputs
	 * @param expectedOutput
	 *          the expected output
	 * @param weights
	 *          the weights
	 * @return the double[]
	 */
	public double[] trainSignalNetwork(double[][] inputs, double[] expectedOutput, double... weights) {
		return network.trainNetwork(inputs, expectedOutput, weights);
	}

	/**
	 * Train signal network.
	 *
	 * @param dataSet
	 *          the data set
	 * @param weights
	 *          the weights
	 * @return the double[]
	 */
	public double[] trainSignalNetwork(DataSet dataSet, double... weights) {
		return network.trainNetwork(dataSet, weights);
	}

	/**
	 * Randomize signal weights.
	 */
	public void randomizeSignalWeights() {
		network.getNetwork().randomizeWeights();
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
		double[] trimmed = new double[BUF_SIZE];
		System.arraycopy(processed, 0, trimmed, 0, BUF_SIZE);
		notifyListeners(network.process(trimmed));
	}

	private void notifyListeners(double[] processed) {
		for (NetworkProcessedListener l : listeners) {
			l.networkProcessed(processed);
		}
	}

	private void initNetwork() {
		network = new NetworkContainer(new MultiLayerPerceptron(BUF_SIZE, BUF_SIZE + 1, getNumOutputNeurons()));

		labelNeurons(network.getNetwork().getInputNeurons(), "input");
		labelNeurons(network.getNetwork().getOutputNeurons(), "output");
	}

	private void labelNeurons(Neuron[] neurons, String type) {
		int i = 0;
		for (Neuron n : neurons) {
			n.setLabel(type + " " + i);
			i++;
		}
	}

	/**
	 * Gets the num output neurons.
	 *
	 * @return the num output neurons
	 */
	public int getNumOutputNeurons() {
		return numOutputNeurons;
	}

	/**
	 * Sets the num output neurons.
	 *
	 * @param numNeurons
	 *          the new num output neurons
	 */
	public void setNumOutputNeurons(int numNeurons) {
		this.numOutputNeurons = numNeurons;
	}

	/**
	 * Gets the network layers count.
	 *
	 * @return the network layers count
	 */
	public int getNetworkLayersCount() {
		return network.getLayersCount();
	}

	/**
	 * Gets the network neurons count.
	 *
	 * @param layerIdx
	 *          the layer idx
	 * @return the network neurons count
	 */
	public int getNetworkNeuronsCount(int layerIdx) {
		return network.getNeuronsCount(layerIdx);
	}

	/**
	 * Gets the network.
	 *
	 * @return the network
	 */
	public NeuralNetwork<?> getNetwork() {
		return network.getNetwork();
	}

	/**
	 * Sets the network.
	 *
	 * @param nn
	 *          the new network
	 */
	public void setNetwork(NeuralNetwork<?> nn) {
		network.setNetwork(nn);
	}

	/**
	 * Gets the network container.
	 *
	 * @return the network container
	 */
	public NetworkContainer getNetworkContainer() {
		return network;
	}

}
