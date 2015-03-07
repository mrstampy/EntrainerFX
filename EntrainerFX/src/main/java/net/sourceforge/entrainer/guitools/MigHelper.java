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

/*
 * Copyright (C) 2008, 2009 Burton Alexander
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

import static net.miginfocom.layout.IDEUtil.BOTTOM;
import static net.miginfocom.layout.IDEUtil.CENTER;
import static net.miginfocom.layout.IDEUtil.LEFT;
import static net.miginfocom.layout.IDEUtil.RIGHT;
import static net.miginfocom.layout.IDEUtil.TOP;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

// TODO: Auto-generated Javadoc
/**
 * 
 * This class provides a wrapper around MigLayout and its associated components,
 * to ease the effort in using this layout manager. Limiting the flow of the
 * layout from left to right, top to bottom, it keeps track of x and y
 * positioning internally and allows chaining of commands. Its usage is very
 * simple:
 * 
 * <code><br><br>
 * 
 *  private Container layoutAContainer() {<br>
 *    &nbsp;&nbsp;MigHelper mh = new MigHelper();<br><br>
 *    
 *    &nbsp;&nbsp;// Set any layout constraints first<br>
 *    &nbsp;&nbsp;mh.setLayoutInsets(0,0,0,0).setLayoutFillX(true);<br><br>
 * 
 *    &nbsp;&nbsp;// adds a row, from left to right<br>
 *    &nbsp;&nbsp;mh.add(comp1).add(comp2).alignWest().addLast(comp3);<br><br>
 * 
 *    &nbsp;&nbsp;// layout other components as required<br><br>
 * 		
 *    &nbsp;&nbsp;return mh.getContainer(); // a JPanel, using the blank constructor<br>
 *  }<br><br>
 * 
 * </code>
 * 
 * See the available methods to determine their functionality.
 * 
 * @author burton
 */
public class MigHelper {

	private Container container;
	private MigLayout layout = new MigLayout();
	private LC layoutConstraints = new LC();
	private CC componentConstraints = new CC();
	private int x = 0;
	private int y = 0;
	private int spanX = 1;
	private int spanY = 1;
	private int split = 1;

	/**
	 * Convenience constructor, setting the container as a new JPanel.
	 */
	public MigHelper() {
		this(new JPanel());
	}

	/**
	 * Instantiate with the container on which you wish to lay out components.
	 *
	 * @param container the container
	 */
	public MigHelper(Container container) {
		super();
		setContainer(container);
	}

	/**
	 * Returns the current container.
	 *
	 * @return the container
	 */
	public Container getContainer() {
		return container;
	}

	/**
	 * Adds the specified component to the current row, flowing from left to
	 * right.
	 *
	 * @param c the c
	 * @return the mig helper
	 */
	public MigHelper add(Component c) {
		return add(c, false);
	}

	/**
	 * Adds the specified component as the last component of the current row.
	 *
	 * @param c the c
	 * @return the mig helper
	 */
	public MigHelper addLast(Component c) {
		return add(c, true);
	}

	private MigHelper add(Component c, boolean wrap) {
		if (wrap) {
			newLineAfter();
		}

		getContainer().add(c);
		layout.setComponentConstraints(c, componentConstraints);

		if (componentConstraints.isWrap()) {
			y += getSpanY();
			x = 0;
		} else {
			if (split == 1) {
				incrX();
			} else {
				split--;
			}
		}

		initCC();

		return this;
	}

	/**
	 * Convenience method to add a JLabel as the next component.
	 *
	 * @param label the label
	 * @return the mig helper
	 */
	public MigHelper add(String label) {
		return add(new JLabel(label));
	}

	/**
	 * Convenience method to add a JLabel as the last component of the current
	 * row.
	 *
	 * @param label the label
	 * @return the mig helper
	 */
	public MigHelper addLast(String label) {
		return add(new JLabel(label), true);
	}

	/**
	 * Allows the current component to be added with the specified string of
	 * constraints. See the documentation included with MigLayout for correct
	 * syntax.
	 *
	 * @param c the c
	 * @param constraints the constraints
	 * @return the mig helper
	 */
	public MigHelper add(Component c, String constraints) {
		constraints = ConstraintParser.prepare(constraints);
		componentConstraints = ConstraintParser.parseComponentConstraint(constraints);

		spanX = componentConstraints.getSpanX();
		spanY = componentConstraints.getSpanY();
		split = componentConstraints.getSplit();

		x += componentConstraints.getSkip();

		return add(c);
	}

	/**
	 * Returns the constraints object for the layout.
	 *
	 * @return the layout constraints
	 */
	public LC getLayoutConstraints() {
		return layoutConstraints;
	}

	/**
	 * Sets the layout constraints object.
	 *
	 * @param lc the lc
	 * @return the mig helper
	 */
	public MigHelper setLayoutConstraints(LC lc) {
		this.layoutConstraints = lc;
		layout.setLayoutConstraints(lc);

		return this;
	}

	/**
	 * Sets the layout constraints with the specified string. Refer to the
	 * documentation included with MigLayout for the correct syntax.
	 *
	 * @param lc the lc
	 * @return the mig helper
	 */
	public MigHelper setLayoutConstraints(String lc) {
		lc = ConstraintParser.prepare(lc);
		return setLayoutConstraints(ConstraintParser.parseLayoutConstraint(lc));
	}

	/**
	 * Gets the row constraints.
	 *
	 * @return the row constraints
	 */
	public AC getRowConstraints() {
		Object rc = layout.getRowConstraints();
		if (rc instanceof AC) {
			return (AC) rc;
		}

		String rcs = (String) rc;
		rcs = ConstraintParser.prepare(rcs);

		return ConstraintParser.parseRowConstraints(rcs);
	}

	/**
	 * Gets the column constraints.
	 *
	 * @return the column constraints
	 */
	public AC getColumnConstraints() {
		Object cc = layout.getColumnConstraints();
		if (cc instanceof AC) {
			return (AC) cc;
		}

		String ccs = (String) cc;
		ccs = ConstraintParser.prepare(ccs);

		return ConstraintParser.parseColumnConstraints(ccs);
	}

	/**
	 * Sets the row constraints.
	 *
	 * @param rc the rc
	 * @return the mig helper
	 */
	public MigHelper setRowConstraints(AC rc) {
		layout.setRowConstraints(rc);

		return this;
	}

	/**
	 * Allows the row constraints to be set with the specified string. See the
	 * documentation included with MigLayout for correct syntax.
	 *
	 * @param rc the rc
	 * @return the mig helper
	 */
	public MigHelper setRowConstraints(String rc) {
		layout.setRowConstraints(rc);

		return this;
	}

	/**
	 * Sets the column constraints.
	 *
	 * @param cc the cc
	 * @return the mig helper
	 */
	public MigHelper setColumnConstraints(AC cc) {
		layout.setColumnConstraints(cc);

		return this;
	}

	/**
	 * Allows the column constraints to be set with the specified string. See the
	 * documentation included with MigLayout for correct syntax.
	 *
	 * @param cc the cc
	 * @return the mig helper
	 */
	public MigHelper setColumnConstraints(String cc) {
		layout.setColumnConstraints(cc);

		return this;
	}

	/**
	 * Returns the current component constraints object.
	 *
	 * @return the component constraints
	 */
	public CC getComponentConstraints() {
		return componentConstraints;
	}

	/**
	 * Sets the current component constraints object, overriding the internal
	 * instance.
	 *
	 * @param componentConstraints the new component constraints
	 */
	public void setComponentConstraints(CC componentConstraints) {
		this.componentConstraints = componentConstraints;
	}

	/**
	 * Sets the grow weight for the current column.
	 *
	 * @param weight the weight
	 * @return the mig helper
	 */
	public MigHelper setColumnGrowWeight(float weight) {
		return setColumnGrowWeights(weight, x);
	}

	/**
	 * Sets the grow weight for the specified columns.
	 *
	 * @param weight the weight
	 * @param indexes the indexes
	 * @return the mig helper
	 */
	public MigHelper setColumnGrowWeights(float weight, int... indexes) {
		AC columnConstraints = getColumnConstraints();

		columnConstraints.grow(weight, indexes);
		setColumnConstraints(columnConstraints);

		return this;
	}

	/**
	 * Sets the grow weight for the current row.
	 *
	 * @param weight the weight
	 * @return the mig helper
	 */
	public MigHelper setRowGrowWeight(float weight) {
		return setRowGrowWeights(weight, y);
	}

	/**
	 * Sets the grow weight for the specified rows.
	 *
	 * @param weight the weight
	 * @param indexes the indexes
	 * @return the mig helper
	 */
	public MigHelper setRowGrowWeights(float weight, int... indexes) {
		AC rowConstraints = getRowConstraints();

		rowConstraints.grow(weight, indexes);
		setRowConstraints(rowConstraints);

		return this;
	}

	/**
	 * Sets the shrink weight for the current column.
	 *
	 * @param weight the weight
	 * @return the mig helper
	 */
	public MigHelper setColumnShrinkWeight(float weight) {
		return setColumnShrinkWeights(weight, x);
	}

	/**
	 * Sets the shrink weight for the specified columns.
	 *
	 * @param weight the weight
	 * @param indexes the indexes
	 * @return the mig helper
	 */
	public MigHelper setColumnShrinkWeights(float weight, int... indexes) {
		AC columnConstraints = getColumnConstraints();

		columnConstraints.shrink(weight, indexes);
		setColumnConstraints(columnConstraints);

		return this;
	}

	/**
	 * Sets the shrink weight for the current row.
	 *
	 * @param weight the weight
	 * @return the mig helper
	 */
	public MigHelper setRowShrinkWeight(float weight) {
		return setRowShrinkWeights(weight, y);
	}

	/**
	 * Sets the shrink weight for the specified rows.
	 *
	 * @param weight the weight
	 * @param indexes the indexes
	 * @return the mig helper
	 */
	public MigHelper setRowShrinkWeights(float weight, int... indexes) {
		AC rowConstraints = getRowConstraints();

		rowConstraints.shrink(weight, indexes);
		setRowConstraints(rowConstraints);

		return this;
	}

	private void newLineAfter() {
		componentConstraints.wrap();
	}

	/**
	 * Pushes the next added component in the x and y directions.
	 *
	 * @return the mig helper
	 */
	public MigHelper push() {
		componentConstraints.push();

		return this;
	}

	/**
	 * Pushes the next added component in the x direction, maximum weighting.
	 *
	 * @return the mig helper
	 */
	public MigHelper pushX() {
		componentConstraints.pushX();

		return this;
	}

	/**
	 * Pushes the next added component in the x direction with the specified
	 * weighting.
	 *
	 * @param f the f
	 * @return the mig helper
	 */
	public MigHelper pushX(float f) {
		componentConstraints.pushX(f);

		return this;
	}

	/**
	 * Pushes the next added component in the y direction, maximum weighting.
	 *
	 * @return the mig helper
	 */
	public MigHelper pushY() {
		componentConstraints.pushY();

		return this;
	}

	/**
	 * Pushes the next added component in the y direction with the specified
	 * weighting.
	 *
	 * @param f the f
	 * @return the mig helper
	 */
	public MigHelper pushY(float f) {
		componentConstraints.pushY(f);

		return this;
	}

	/**
	 * Increments the x position by 1.
	 *
	 * @return the mig helper
	 */
	public MigHelper incrX() {
		return incrX(getSpanX());
	}

	/**
	 * Increments the x position by the specified value.
	 *
	 * @param val the val
	 * @return the mig helper
	 */
	public MigHelper incrX(int val) {
		x += val;

		return this;
	}

	/**
	 * Resets all properties, including x and y positioning.
	 *
	 * @return the mig helper
	 */
	public MigHelper resetAll() {
		x = 0;
		y = 0;

		initCC();
		reset();

		return this;
	}

	/**
	 * Resets to the following defaults: <code><br><br>
	 * 
	 * fill = false<br><br>
	 * 
	 * center layout position<br><br>
	 * 
	 * insets of 5,5,5,5<br><br>
	 * 
	 * width, height, split = 1<br><br>
	 * 
	 * 
	 * </code>.
	 *
	 * @return the mig helper
	 */
	public MigHelper reset() {
		setLayoutFill(false);
		setLayoutCenter();
		setLayoutInsets(5, 5, 5, 5);
		spanX = 1;
		spanY = 1;
		split = 1;

		return this;
	}

	private void initCC() {
		componentConstraints = new CC();
		componentConstraints.setCellX(x);
		componentConstraints.setCellY(y);
		componentConstraints.setSplit(split);

		alignCenter();
	}

	/**
	 * Gets the span x.
	 *
	 * @return the span x
	 */
	public int getSpanX() {
		return spanX;
	}

	/**
	 * Sets the number of cells to span in the x direction.
	 *
	 * @param width the width
	 * @return the mig helper
	 */
	public MigHelper setSpanX(int width) {
		this.spanX = width;
		componentConstraints.setSpanX(width);

		return this;
	}

	/**
	 * Gets the span y.
	 *
	 * @return the span y
	 */
	public int getSpanY() {
		return spanY;
	}

	/**
	 * Sets the number of cells to span in the y direction.
	 *
	 * @param height the height
	 * @return the mig helper
	 */
	public MigHelper setSpanY(int height) {
		this.spanY = height;
		componentConstraints.setSpanY(height);

		return this;
	}

	/**
	 * If invoked, the next component added will span the rest of the row.
	 *
	 * @return the mig helper
	 */
	public MigHelper spanX() {
		componentConstraints.spanX();

		return this;
	}

	/**
	 * If invoked, the next component added will span the rest of the column.
	 *
	 * @return the mig helper
	 */
	public MigHelper spanY() {
		componentConstraints.spanY();

		return this;
	}

	/**
	 * If invoked, the next component added will skip one row (x) position.
	 *
	 * @return the mig helper
	 */
	public MigHelper skip() {
		return skip(1);
	}

	/**
	 * If invoked the next component added will skip the specified number of row
	 * (x) positions.
	 *
	 * @param cells the cells
	 * @return the mig helper
	 */
	public MigHelper skip(int cells) {
		componentConstraints.setCellX(componentConstraints.getCellX() + cells);
		x += cells;

		return this;
	}

	/**
	 * Sets the padding around the next component added.
	 *
	 * @param top the top
	 * @param left the left
	 * @param bottom the bottom
	 * @param right the right
	 * @return the mig helper
	 */
	public MigHelper pad(int top, int left, int bottom, int right) {
		componentConstraints.pad(top, left, bottom, right);

		return this;
	}

	/**
	 * Tells the layout manager to grow the next added component by what factor in
	 * both the x and y directions. Must be in the range of 0 -> 100. Analogous to
	 * weightx and weighty of GridBagConstraints.
	 *
	 * @param f the f
	 * @return the mig helper
	 */
	public MigHelper grow(float f) {
		growX(f);
		return growY(f);
	}

	/**
	 * Tells the layout manager to grow the next added component by what factor in
	 * the x direction. Must be in the range of 0 -> 100.
	 *
	 * @param f the f
	 * @return the mig helper
	 */
	public MigHelper growX(float f) {
		componentConstraints.growX(f);

		return this;
	}

	/**
	 * Tells the layout manager to grow the next added component and by what
	 * factor in the y direction. Must be in the range of 0 -> 100.
	 *
	 * @param f the f
	 * @return the mig helper
	 */
	public MigHelper growY(float f) {
		componentConstraints.growY(f);

		return this;
	}

	/**
	 * Tells the layout manager to shrink the next added component by what factor
	 * in both the x and y directions. Must be in the range of 0 -> 100. Analogous
	 * to weightx and weighty of GridBagConstraints.
	 *
	 * @param f the f
	 * @return the mig helper
	 */
	public MigHelper shrink(float f) {
		shrinkX(f);
		return shrinkY(f);
	}

	/**
	 * Tells the layout manager to shrink the next added component by what factor
	 * in the x direction. Must be in the range of 0 -> 100.
	 *
	 * @param f the f
	 * @return the mig helper
	 */
	public MigHelper shrinkX(float f) {
		componentConstraints.shrinkX(f);

		return this;
	}

	/**
	 * Tells the layout manager to shrink the next added component by what factor
	 * in the y direction. Must be in the range of 0 -> 100.
	 *
	 * @param f the f
	 * @return the mig helper
	 */
	public MigHelper shrinkY(float f) {
		componentConstraints.shrinkY(f);

		return this;
	}

	/**
	 * Aligns the next added component at the center of the cell.
	 *
	 * @return the mig helper
	 */
	public MigHelper alignCenter() {
		setAlignX("center");
		return setAlignY("center");
	}

	/**
	 * Aligns the next added component at the north-west of the cell.
	 *
	 * @return the mig helper
	 */
	public MigHelper alignNorthWest() {
		setAlignX("left");
		return setAlignY("top");
	}

	/**
	 * Aligns the next added component at the north of the cell.
	 *
	 * @return the mig helper
	 */
	public MigHelper alignNorth() {
		setAlignX("center");
		return setAlignY("top");
	}

	/**
	 * Aligns the next added component at the north-east of the cell.
	 *
	 * @return the mig helper
	 */
	public MigHelper alignNorthEast() {
		setAlignX("right");
		return setAlignY("top");
	}

	/**
	 * Aligns the next added component at the west of the cell.
	 *
	 * @return the mig helper
	 */
	public MigHelper alignWest() {
		setAlignX("left");
		return setAlignY("center");
	}

	/**
	 * Aligns the next added component at the east of the cell.
	 *
	 * @return the mig helper
	 */
	public MigHelper alignEast() {
		setAlignX("right");
		return setAlignY("center");
	}

	/**
	 * Aligns the next added component at the south-west of the cell.
	 *
	 * @return the mig helper
	 */
	public MigHelper alignSouthWest() {
		setAlignX("left");
		return setAlignY("bottom");
	}

	/**
	 * Aligns the next added component at the south of the cell.
	 *
	 * @return the mig helper
	 */
	public MigHelper alignSouth() {
		setAlignX("center");
		return setAlignY("bottom");
	}

	/**
	 * Aligns the next added component at the south-east of the cell.
	 *
	 * @return the mig helper
	 */
	public MigHelper alignSouthEast() {
		setAlignX("right");
		return setAlignY("bottom");
	}

	private MigHelper setAlignX(String s) {
		componentConstraints.alignX(s);

		return this;
	}

	private MigHelper setAlignY(String s) {
		componentConstraints.alignY(s);

		return this;
	}

	/**
	 * Sets the docking side as the east. Refer to the documentation included with
	 * MigLayout for a thorough explanation.
	 *
	 * @return the mig helper
	 */
	public MigHelper dockEast() {
		componentConstraints.dockEast();

		return this;
	}

	/**
	 * Sets the docking side as the west. Refer to the documentation included with
	 * MigLayout for a thorough explanation.
	 *
	 * @return the mig helper
	 */
	public MigHelper dockWest() {
		componentConstraints.dockWest();

		return this;
	}

	/**
	 * Sets the docking side as the north. Refer to the documentation included
	 * with MigLayout for a thorough explanation.
	 *
	 * @return the mig helper
	 */
	public MigHelper dockNorth() {
		componentConstraints.dockNorth();

		return this;
	}

	/**
	 * Sets the docking side as the south. Refer to the documentation included
	 * with MigLayout for a thorough explanation.
	 *
	 * @return the mig helper
	 */
	public MigHelper dockSouth() {
		componentConstraints.dockSouth();

		return this;
	}

	/**
	 * If hidden the bounds of the next component added will be calculated as if
	 * the component was visible.
	 *
	 * @return the mig helper
	 */
	public MigHelper hideNormal() {
		return setHideMode(0);
	}

	/**
	 * If hidden the size of the next component added will be 0, 0 but the gaps
	 * remain.
	 *
	 * @return the mig helper
	 */
	public MigHelper hideWithGaps() {
		return setHideMode(1);
	}

	/**
	 * If hidden the size of the next component added will be 0, 0 and gaps set to
	 * zero.
	 *
	 * @return the mig helper
	 */
	public MigHelper hideNoGaps() {
		return setHideMode(2);
	}

	/**
	 * If hidden the next component added will be disregarded completely and not
	 * take up a cell in the grid.
	 *
	 * @return the mig helper
	 */
	public MigHelper hideCompletely() {
		return setHideMode(3);
	}

	private MigHelper setHideMode(int mode) {
		componentConstraints.setHideMode(mode);

		return this;
	}

	/**
	 * Sets the grow priority in the x direction of the next component added.
	 *
	 * @param priority the priority
	 * @return the mig helper
	 */
	public MigHelper setGrowPriorityX(int priority) {
		componentConstraints.growPrioX(priority);

		return this;
	}

	/**
	 * Sets the grow priority in the y direction of the next component added.
	 *
	 * @param priority the priority
	 * @return the mig helper
	 */
	public MigHelper setGrowPriorityY(int priority) {
		componentConstraints.growPrioY(priority);

		return this;
	}

	/**
	 * Sets the shrink priority in the x direction of the next component added.
	 *
	 * @param priority the priority
	 * @return the mig helper
	 */
	public MigHelper setShrinkPriorityX(int priority) {
		componentConstraints.shrinkPrioX(priority);

		return this;
	}

	/**
	 * Sets the shrink priority in the y direction of the next component added.
	 *
	 * @param priority the priority
	 * @return the mig helper
	 */
	public MigHelper setShrinkPriorityY(int priority) {
		componentConstraints.shrinkPrioY(priority);

		return this;
	}

	/**
	 * Sets in how many parts the next cell (that this constraint's component will
	 * be in) should be split in. If for instance it is split in two, the next
	 * component will also share the same cell. Note that the cell can also span a
	 * number of cells, which means that you can for instance span three cells and
	 * split that big cell for two components. Split can be set to a very high
	 * value to make all components in the same row/column share the same cell
	 *
	 * @param parts the parts
	 * @return the mig helper
	 */
	public MigHelper setSplit(int parts) {
		componentConstraints.setSplit(parts);
		split = parts;

		return this;
	}

	/**
	 * Sets a metadata tag name for the next component. See the MigLayout
	 * documentation for a thorough explanation.
	 *
	 * @param tagName the tag name
	 * @return the mig helper
	 */
	public MigHelper setTag(String tagName) {
		componentConstraints.setTag(tagName);

		return this;
	}

	/**
	 * Sets the gap before and after the component in the x direction.
	 *
	 * @param before the before
	 * @param after the after
	 * @return the mig helper
	 */
	public MigHelper gapX(int before, int after) {
		return gapX("" + before, "" + after);
	}

	/**
	 * Sets the gap before and after the component in the x direction.
	 *
	 * @param before the before
	 * @param after the after
	 * @return the mig helper
	 */
	public MigHelper gapX(String before, String after) {
		componentConstraints.gapX(before, after);

		return this;
	}

	/**
	 * Sets the gap before and after the component in the y direction.
	 *
	 * @param before the before
	 * @param after the after
	 * @return the mig helper
	 */
	public MigHelper gapY(int before, int after) {
		return gapY("" + before, "" + after);
	}

	/**
	 * Sets the gap before and after the component in the x direction.
	 *
	 * @param before the before
	 * @param after the after
	 * @return the mig helper
	 */
	public MigHelper gapY(String before, String after) {
		componentConstraints.gapY(before, after);

		return this;
	}

	/**
	 * Sets the width.
	 *
	 * @param width the width
	 * @return the mig helper
	 */
	public MigHelper setWidth(int width) {
		componentConstraints.width("" + width);

		return this;
	}

	/**
	 * Sets the height.
	 *
	 * @param height the height
	 * @return the mig helper
	 */
	public MigHelper setHeight(int height) {
		componentConstraints.height("" + height);

		return this;
	}

	/**
	 * Sets the insets for the layout.
	 *
	 * @param top the top
	 * @param left the left
	 * @param bottom the bottom
	 * @param right the right
	 * @return the mig helper
	 */
	public MigHelper setLayoutInsets(int top, int left, int bottom, int right) {
		return setLayoutInsets("" + top, "" + left, "" + bottom, "" + right);
	}

	/**
	 * Sets the insets for the layout. See the documentation included with
	 * MigLayout for the correct syntax.
	 *
	 * @param top the top
	 * @param left the left
	 * @param bottom the bottom
	 * @param right the right
	 * @return the mig helper
	 */
	public MigHelper setLayoutInsets(String top, String left, String bottom, String right) {
		layoutConstraints.insets(top, left, bottom, right);

		return this;
	}

	/**
	 * Sets the grid gap for the layout.
	 *
	 * @param gapX the gap x
	 * @param gapY the gap y
	 * @return the mig helper
	 */
	public MigHelper setLayoutGridGap(int gapX, int gapY) {
		return setLayoutGridGap("" + gapX, "" + gapY);
	}

	/**
	 * Sets the grid gap for the layout. See the documentation included with
	 * MigLayout for the correct syntax.
	 *
	 * @param gapX the gap x
	 * @param gapY the gap y
	 * @return the mig helper
	 */
	public MigHelper setLayoutGridGap(String gapX, String gapY) {
		layoutConstraints.gridGap(gapX, gapY);

		return this;
	}

	/**
	 * Sets the fill to true or false for the layout.
	 *
	 * @param b the b
	 * @return the mig helper
	 */
	public MigHelper setLayoutFill(boolean b) {
		setLayoutFillX(b);
		return setLayoutFillY(b);
	}

	/**
	 * Sets the fill in the x direction for the layout.
	 *
	 * @param b the b
	 * @return the mig helper
	 */
	public MigHelper setLayoutFillX(boolean b) {
		layoutConstraints.setFillX(b);

		return this;
	}

	/**
	 * Sets the fill in the y direction for the layout.
	 *
	 * @param b the b
	 * @return the mig helper
	 */
	public MigHelper setLayoutFillY(boolean b) {
		layoutConstraints.setFillY(b);

		return this;
	}

	/**
	 * Centers the layout.
	 *
	 * @return the mig helper
	 */
	public MigHelper setLayoutCenter() {
		layoutConstraints.setAlignY(CENTER);
		layoutConstraints.setAlignX(CENTER);

		return this;
	}

	/**
	 * Sets the layout to the top left.
	 *
	 * @return the mig helper
	 */
	public MigHelper setLayoutTopLeft() {
		layoutConstraints.setAlignX(LEFT);
		layoutConstraints.setAlignY(TOP);

		return this;
	}

	/**
	 * Sets the layout to the top center.
	 *
	 * @return the mig helper
	 */
	public MigHelper setLayoutTopCenter() {
		layoutConstraints.setAlignX(CENTER);
		layoutConstraints.setAlignY(TOP);

		return this;
	}

	/**
	 * Sets the layout to the top right.
	 *
	 * @return the mig helper
	 */
	public MigHelper setLayoutTopRight() {
		layoutConstraints.setAlignX(RIGHT);
		layoutConstraints.setAlignY(TOP);

		return this;
	}

	/**
	 * Sets the layout to the left.
	 *
	 * @return the mig helper
	 */
	public MigHelper setLayoutLeft() {
		layoutConstraints.setAlignX(LEFT);
		layoutConstraints.setAlignY(CENTER);

		return this;
	}

	/**
	 * Sets the layout to the right.
	 *
	 * @return the mig helper
	 */
	public MigHelper setLayoutRight() {
		layoutConstraints.setAlignX(RIGHT);
		layoutConstraints.setAlignY(CENTER);

		return this;
	}

	/**
	 * Sets the layout to the bottom left.
	 *
	 * @return the mig helper
	 */
	public MigHelper setLayoutBottomLeft() {
		layoutConstraints.setAlignX(LEFT);
		layoutConstraints.setAlignY(BOTTOM);

		return this;
	}

	/**
	 * Sets the layout to the bottom center.
	 *
	 * @return the mig helper
	 */
	public MigHelper setLayoutBottomCenter() {
		layoutConstraints.setAlignX(CENTER);
		layoutConstraints.setAlignY(BOTTOM);

		return this;
	}

	/**
	 * Sets the layout to the bottom right.
	 *
	 * @return the mig helper
	 */
	public MigHelper setLayoutBottomRight() {
		layoutConstraints.setAlignX(RIGHT);
		layoutConstraints.setAlignY(BOTTOM);

		return this;
	}

	/**
	 * Sets the container for the MigLayout, and initializes the MigLayout
	 * components.
	 *
	 * @param container the container
	 * @return the mig helper
	 */
	public MigHelper setContainer(Container container) {
		if (container != null) {
			initMigComponents();
			container.setLayout(layout);
			resetAll();
		}

		this.container = container;

		return this;
	}

	private void initMigComponents() {
		layout.setLayoutConstraints(layoutConstraints);
		initCC();
	}

}
