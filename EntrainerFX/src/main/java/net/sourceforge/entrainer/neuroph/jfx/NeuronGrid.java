/*
 * Copyright (C) 2008 - 2014 Burton Alexander
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
package net.sourceforge.entrainer.neuroph.jfx;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import net.sourceforge.entrainer.gui.jfx.JFXUtils;
import net.sourceforge.entrainer.neuroph.Neuralizer;

// TODO: Auto-generated Javadoc
/**
 * The Class NeuronGrid.
 */
public class NeuronGrid {
	private GridPane gridPane;

	private int rows;
	private int cols;

	private NeuronFx[][] neurons;
	private NeuronFx[] flattened;

	/**
	 * Instantiates a new neuron grid.
	 *
	 * @param rows
	 *          the rows
	 * @param cols
	 *          the cols
	 */
	NeuronGrid(int rows, int cols) {
		setRows(rows);
		setCols(cols);

		init();
	}

	/**
	 * Instantiates a new neuron grid.
	 *
	 * @param neuralizer
	 *          the neuralizer
	 */
	public NeuronGrid(Neuralizer neuralizer) {
		this(1, neuralizer);
	}

	/**
	 * Instantiates a new neuron grid.
	 *
	 * @param rows
	 *          the rows
	 * @param neuralizer
	 *          the neuralizer
	 */
	public NeuronGrid(int rows, Neuralizer neuralizer) {
		this(rows, neuralizer.getNetwork().getOutputsCount() / rows);

		assert rows > 0;
		assert neuralizer.getNetwork().getOutputsCount() % rows == 0;
		init(neuralizer);
	}

	private void init(Neuralizer neuralizer) {
		neuralizer.addNetworkProcessedListener(t1 -> JFXUtils.runLater(() -> setValues(t1)));

		for (int i = 0; i < flattened.length; i++) {
			flattened[i].setTooltip(neuralizer.getNetwork().getOutputNeurons()[i].getLabel());
		}

		neuralizer.getNetwork().randomizeWeights(-20000, 20000);
	}

	private void setValues(double[] output) {
		for (int i = 0; i < output.length; i++) {
			flattened[i].setValue(output[i]);
			flattened[i].calculateNeuronOpacity();
		}
	}

	/**
	 * Gets the WMA size.
	 *
	 * @return the WMA size
	 */
	public int getWMASize() {
		return flattened[0].getWMASize();
	}

	/**
	 * Sets the WMA size.
	 *
	 * @param wmaSize
	 *          the new WMA size
	 */
	public void setWMASize(int wmaSize) {
		assert wmaSize > 1;

		for (NeuronFx nfx : flattened) {
			nfx.setWMASize(wmaSize);
		}
	}

	/**
	 * Gets the grid pane.
	 *
	 * @return the grid pane
	 */
	public GridPane getGridPane() {
		return gridPane;
	}

	/**
	 * Inits the.
	 */
	public void init() {
		initNeurons();

		gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);

		for (int i = 0; i < getCols(); i++) {
			for (int j = 0; j < getRows(); j++) {
				GridPane.setConstraints(neurons[i][j], i, j);
			}
		}

		gridPane.getChildren().addAll(flattened);

		gridPane.setStyle("-fx-background-color: black");
	}

	/**
	 * Gets the flattened.
	 *
	 * @return the flattened
	 */
	public NeuronFx[] getFlattened() {
		return flattened;
	}

	/**
	 * Sets the color.
	 *
	 * @param c
	 *          the new color
	 */
	public void setColor(Color c) {
		for (NeuronFx neuron : flattened) {
			neuron.setNeuronFill(c);
		}
	}

	/**
	 * Gets the neuron.
	 *
	 * @param colIdx
	 *          the col idx
	 * @param rowIdx
	 *          the row idx
	 * @return the neuron
	 */
	public NeuronFx getNeuron(int colIdx, int rowIdx) {
		assert colIdx >= 0 && colIdx < getCols();
		assert rowIdx >= 0 && rowIdx < getRows();

		return neurons[colIdx][rowIdx];
	}

	private void initNeurons() {
		neurons = new NeuronFx[getCols()][getRows()];
		flattened = new NeuronFx[getCols() * getRows()];
		int k = 0;
		for (int i = 0; i < getCols(); i++) {
			for (int j = 0; j < getRows(); j++) {
				neurons[i][j] = new NeuronFx();
				flattened[k] = neurons[i][j];

				k++;
			}
		}
	}

	/**
	 * Gets the rows.
	 *
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * Sets the rows.
	 *
	 * @param rows
	 *          the new rows
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * Gets the cols.
	 *
	 * @return the cols
	 */
	public int getCols() {
		return cols;
	}

	/**
	 * Sets the cols.
	 *
	 * @param cols
	 *          the new cols
	 */
	public void setCols(int cols) {
		this.cols = cols;
	}
}
