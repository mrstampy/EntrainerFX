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
package net.sourceforge.entrainer.guitools;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

// TODO: Auto-generated Javadoc
/**
 * Making the use of GridPanes less paneful.
 * 
 * @author burton
 *
 */
public class GridPaneHelper {

	private GridPane pane;

	private List<Node> nodes = new ArrayList<>();

	private int row;
	private int column;

	/**
	 * Instantiates a new grid pane helper.
	 */
	public GridPaneHelper() {
		this(new GridPane());
	}

	/**
	 * Instantiates a new grid pane helper.
	 *
	 * @param pane
	 *          the pane
	 */
	public GridPaneHelper(GridPane pane) {
		this.pane = pane;
	}

	/**
	 * Skip.
	 *
	 * @return the grid pane helper
	 */
	public GridPaneHelper skip() {
		return skip(1);
	}

	/**
	 * Skip.
	 *
	 * @param num
	 *          the num
	 * @return the grid pane helper
	 */
	public GridPaneHelper skip(int num) {
		column += num;
		return this;
	}

	/**
	 * New line.
	 *
	 * @return the grid pane helper
	 */
	public GridPaneHelper newLine() {
		return newLine(1);
	}

	/**
	 * New line.
	 *
	 * @param num
	 *          the num
	 * @return the grid pane helper
	 */
	public GridPaneHelper newLine(int num) {
		row += num;
		column = 0;

		return this;
	}

	/**
	 * Adds the.
	 *
	 * @param label
	 *          the label
	 * @return the grid pane helper
	 */
	public GridPaneHelper add(String label) {
		return add(new Label(label));
	}

	/**
	 * Adds the.
	 *
	 * @param label
	 *          the label
	 * @param width
	 *          the width
	 * @param height
	 *          the height
	 * @return the grid pane helper
	 */
	public GridPaneHelper add(String label, int width, int height) {
		return add(new Label(label), width, height);
	}

	/**
	 * Adds the last.
	 *
	 * @param label
	 *          the label
	 * @return the grid pane helper
	 */
	public GridPaneHelper addLast(String label) {
		return addLast(new Label(label));
	}

	/**
	 * Adds the last.
	 *
	 * @param label
	 *          the label
	 * @param width
	 *          the width
	 * @param height
	 *          the height
	 * @return the grid pane helper
	 */
	public GridPaneHelper addLast(String label, int width, int height) {
		return addLast(new Label(label), width, height);
	}

	/**
	 * Adds the last.
	 *
	 * @param node
	 *          the node
	 * @return the grid pane helper
	 */
	public GridPaneHelper addLast(Node node) {
		return addLast(node, 1, 1);
	}

	/**
	 * Adds the last.
	 *
	 * @param node
	 *          the node
	 * @param width
	 *          the width
	 * @param height
	 *          the height
	 * @return the grid pane helper
	 */
	public GridPaneHelper addLast(Node node, int width, int height) {
		add(node, width, height);

		newLine();

		return this;
	}

	/**
	 * Adds the.
	 *
	 * @param node
	 *          the node
	 * @return the grid pane helper
	 */
	public GridPaneHelper add(Node node) {
		return add(node, 1, 1);
	}

	/**
	 * Adds the.
	 *
	 * @param node
	 *          the node
	 * @param width
	 *          the width
	 * @param height
	 *          the height
	 * @return the grid pane helper
	 */
	public GridPaneHelper add(Node node, int width, int height) {
		GridPane.setConstraints(node, column, row, width, height);

		skip(width);

		nodes.add(node);

		return this;
	}

	/**
	 * H grow.
	 *
	 * @param node
	 *          the node
	 * @param priority
	 *          the priority
	 * @return the grid pane helper
	 */
	public GridPaneHelper hGrow(Node node, Priority priority) {
		GridPane.setHgrow(node, priority);

		return this;
	}

	/**
	 * V grow.
	 *
	 * @param node
	 *          the node
	 * @param priority
	 *          the priority
	 * @return the grid pane helper
	 */
	public GridPaneHelper vGrow(Node node, Priority priority) {
		GridPane.setVgrow(node, priority);

		return this;
	}

	/**
	 * H alignment.
	 *
	 * @param node
	 *          the node
	 * @param pos
	 *          the pos
	 * @return the grid pane helper
	 */
	public GridPaneHelper hAlignment(Node node, HPos pos) {
		GridPane.setHalignment(node, pos);

		return this;
	}

	/**
	 * V alignment.
	 *
	 * @param node
	 *          the node
	 * @param pos
	 *          the pos
	 * @return the grid pane helper
	 */
	public GridPaneHelper vAlignment(Node node, VPos pos) {
		GridPane.setValignment(node, pos);

		return this;
	}

	/**
	 * Fill height.
	 *
	 * @param node
	 *          the node
	 * @param fill
	 *          the fill
	 * @return the grid pane helper
	 */
	public GridPaneHelper fillHeight(Node node, Boolean fill) {
		GridPane.setFillHeight(node, fill);

		return this;
	}

	/**
	 * Fill width.
	 *
	 * @param node
	 *          the node
	 * @param fill
	 *          the fill
	 * @return the grid pane helper
	 */
	public GridPaneHelper fillWidth(Node node, Boolean fill) {
		GridPane.setFillWidth(node, fill);

		return this;
	}

	/**
	 * Reset.
	 *
	 * @return the grid pane helper
	 */
	public GridPaneHelper reset() {
		nodes.clear();
		this.pane = new GridPane();
		row = 0;
		column = 0;

		return this;
	}

	/**
	 * Alignment.
	 *
	 * @param pos
	 *          the pos
	 * @return the grid pane helper
	 */
	public GridPaneHelper alignment(Pos pos) {
		pane.setAlignment(pos);

		return this;
	}

	/**
	 * Grid lines.
	 *
	 * @param b
	 *          the b
	 * @return the grid pane helper
	 */
	public GridPaneHelper gridLines(boolean b) {
		pane.setGridLinesVisible(b);

		return this;
	}

	/**
	 * H gap.
	 *
	 * @param gap
	 *          the gap
	 * @return the grid pane helper
	 */
	public GridPaneHelper hGap(double gap) {
		pane.setHgap(gap);

		return this;
	}

	/**
	 * V gap.
	 *
	 * @param gap
	 *          the gap
	 * @return the grid pane helper
	 */
	public GridPaneHelper vGap(double gap) {
		pane.setVgap(gap);

		return this;
	}

	/**
	 * Padding.
	 *
	 * @param insets
	 *          the insets
	 * @return the grid pane helper
	 */
	public GridPaneHelper padding(Insets insets) {
		pane.setPadding(insets);

		return this;
	}

	/**
	 * Gets the pane.
	 *
	 * @return the pane
	 */
	public GridPane getPane() {
		if (pane.getChildren().isEmpty()) pane.getChildren().addAll(nodes);

		return pane;
	}

}
