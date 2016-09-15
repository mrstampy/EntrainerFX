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

import org.neuroph.core.Connection;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.Weight;
import org.neuroph.core.data.DataSet;

// TODO: Auto-generated Javadoc
/**
 * Encapsulates a {@link NeuralNetwork}.
 */
public class NetworkContainer {

  private NeuralNetwork<?> network;

  /**
   * Instantiates a new network container.
   *
   * @param network
   *          the network
   */
  public NetworkContainer(NeuralNetwork<?> network) {
    setNetwork(network);
  }

  /**
   * Train network.
   *
   * @param dataSet
   *          the data set
   * @param weights
   *          the weights
   * @return the double[]
   */
  public double[] trainNetwork(DataSet dataSet, double... weights) {
    assert dataSet != null;
    assert weights == null || weights.length == getWeights().length;

    return trainNetworkImpl(dataSet, weights);
  }

  /**
   * Train network.
   *
   * @param inputs
   *          the inputs
   * @param expectedOutput
   *          the expected output
   * @param weights
   *          the weights
   * @return the double[]
   */
  public double[] trainNetwork(double[][] inputs, double[] expectedOutput, double... weights) {
    assert inputs != null && inputs.length > 0 && inputs[0].length == getNeuronsCount(0);
    assert expectedOutput != null && expectedOutput.length == getNeuronsCount(getLayersCount() - 1);
    assert weights == null || weights.length == getWeights().length;

    return trainNetworkImpl(createDataSet(inputs, expectedOutput), weights);
  }

  /**
   * Gets the weighting between the specified neurons. The inNeuronIdx is
   * expected to exist in the layer specified by layerIdx, and the outNeuronIdx
   * is expected to exist in layerIdx + 1.
   *
   * @param inNeuronIdx
   *          the in neuron idx
   * @param outNeuronIdx
   *          the out neuron idx
   * @param layerIdx
   *          the layer idx
   * @return the weight
   */
  public double getWeight(int inNeuronIdx, int outNeuronIdx, int layerIdx) {
    assertForWeights(inNeuronIdx, outNeuronIdx, layerIdx);

    Connection c = getConnection(inNeuronIdx, outNeuronIdx, layerIdx);

    return c == null ? -1 : c.getWeight().getValue();
  }

  /**
   * Sets the weight. The inNeuronIdx is expected to exist in the layer
   * specified by layerIdx, and the outNeuronIdx is expected to exist in
   * layerIdx + 1.
   *
   * @param inNeuronIdx
   *          the in neuron idx
   * @param outNeuronIdx
   *          the out neuron idx
   * @param weight
   *          the weight
   * @param layerIdx
   *          the layer idx
   */
  public void setWeight(int inNeuronIdx, int outNeuronIdx, double weight, int layerIdx) {
    assertForWeights(inNeuronIdx, outNeuronIdx, layerIdx);

    Connection c = getConnection(inNeuronIdx, outNeuronIdx, layerIdx);

    c.setWeight(new Weight(weight));
  }

  /**
   * Process the specified input, returning the output.
   *
   * @param input
   *          the input
   * @return the double[]
   */
  public double[] process(double[] input) {
    assert input != null && input.length == getNeuronsCount(0);

    network.setInput(input);
    network.calculate();

    return network.getOutput();
  }

  /**
   * Save the network to the specified file.
   *
   * @param file
   *          the file
   */
  public void save(String file) {
    assert file != null;

    network.save(file);
  }

  /**
   * Load a network.
   *
   * @param file
   *          the file
   * @return the network container
   */
  public static NetworkContainer load(String file) {
    assert file != null;

    return new NetworkContainer(NeuralNetwork.createFromFile(file));
  }

  private double[] trainNetworkImpl(DataSet dataSet, double... weights) {
    if (weights != null) network.setWeights(weights);

    network.learn(dataSet);

    return network.getOutput();
  }

  private void assertForWeights(int inNeuronIdx, int outNeuronIdx, int layerIdx) {
    assert layerIdx >= 0 && getLayersCount() >= layerIdx + 1;
    assert isValidIndex(inNeuronIdx, layerIdx);
    assert isValidIndex(outNeuronIdx, layerIdx + 1);
  }

  private boolean isValidIndex(int inNeuronIdx, int layerIdx) {
    return inNeuronIdx >= 0 && inNeuronIdx < network.getLayerAt(layerIdx).getNeuronsCount();
  }

  private Connection getConnection(int inNeuronIdx, int outNeuronIdx, int layerIdx) {
    Neuron in = network.getLayerAt(layerIdx).getNeuronAt(inNeuronIdx);
    Neuron out = network.getLayerAt(layerIdx + 1).getNeuronAt(outNeuronIdx);

    return out.getConnectionFrom(in);
  }

  /**
   * Convenience method to create a data set for training.
   *
   * @param inputs
   *          the inputs
   * @param expectedOutput
   *          the expected output
   * @return the data set
   */
  public DataSet createDataSet(double[][] inputs, double[] expectedOutput) {
    DataSet ds = new DataSet(inputs[0].length, expectedOutput.length);

    for (int i = 0; i < inputs.length; i++) {
      ds.addRow(inputs[i], expectedOutput);
    }

    return ds;
  }

  /**
   * Gets the weights.
   *
   * @return the weights
   */
  public Double[] getWeights() {
    return network.getWeights();
  }

  /**
   * Sets the weights.
   *
   * @param weights
   *          the new weights
   */
  public void setWeights(double[] weights) {
    network.setWeights(weights);
  }

  /**
   * Gets the layers count.
   *
   * @return the layers count
   */
  public int getLayersCount() {
    return network.getLayersCount();
  }

  /**
   * Gets the neurons count.
   *
   * @param layerIdx
   *          the layer idx
   * @return the neurons count
   */
  public int getNeuronsCount(int layerIdx) {
    assert layerIdx >= 0 && getLayersCount() > layerIdx;

    return network.getLayerAt(layerIdx).getNeuronsCount();
  }

  /**
   * Gets the network.
   *
   * @return the network
   */
  public NeuralNetwork<?> getNetwork() {
    return network;
  }

  /**
   * Sets the network.
   *
   * @param network
   *          the new network
   */
  public void setNetwork(NeuralNetwork<?> network) {
    assert network != null;

    this.network = network;
  }

}
