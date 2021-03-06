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
package net.sourceforge.entrainer.gui.jfx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import net.sourceforge.entrainer.gui.jfx.shimmer.AbstractShimmer;
import net.sourceforge.entrainer.gui.jfx.shimmer.ShimmerRegister;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class ShimmerOptionsPane.
 */
public class ShimmerOptionsPane extends AbstractTitledPane {

	private ComboBox<String> shimmers = new ComboBox<String>();
	private CheckBox shimmer = new CheckBox("Shimmer");
	private HBox fp;

	/**
	 * Instantiates a new shimmer options pane.
	 */
	public ShimmerOptionsPane() {
		super("Shimmer Options");
		init();
	}

	/**
	 * Gets the shimmer.
	 *
	 * @return the shimmer
	 */
	public CheckBox getShimmer() {
		return shimmer;
	}

	/**
	 * Sets the shimmer selected.
	 *
	 * @param selected
	 *          the new shimmer selected
	 */
	public void setShimmerSelected(boolean selected) {
		setSelected(selected, shimmer);
	}

	/**
	 * Sets the shimmer tool tip.
	 *
	 * @param toolTip
	 *          the new shimmer tool tip
	 */
	public void setShimmerToolTip(String toolTip) {
		setToolTip(toolTip, shimmer);
	}

	protected void init() {
		initMediator();
		setText("Shimmer Options");

		shimmers.getItems().addAll(ShimmerRegister.getShimmerNames());
		initCheckBox(shimmer, MediatorConstants.IS_SHIMMER);

		fp = new HBox();
		HBox.setMargin(shimmer, new Insets(5, 15, 5, 15));
		HBox.setMargin(shimmers, new Insets(0, 5, 0, 5));
		fp.setPadding(new Insets(10));
		fp.getChildren().add(shimmer);
		fp.getChildren().add(shimmers);

		shimmers.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				fireShimmerSelected(ShimmerRegister.getShimmer(shimmers.getValue()));
			}
		});

		setTextFill(shimmer);

		fp.setAlignment(Pos.CENTER);

		super.init();
	}

	private void setSelected(final boolean selected, final CheckBox checkBox) {
		if (checkBox.isSelected() == selected) return;
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				checkBox.setSelected(selected);
			}
		});
	}

	private void setToolTip(final String toolTip, final Control node) {
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				node.setTooltip(new Tooltip(toolTip));
			}
		});
	}

	private void initCheckBox(final CheckBox checkBox, final MediatorConstants parm) {
		checkBox.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				fireReceiverChangeEvent(checkBox.isSelected(), parm);
			}
		});
	}

	private void fireShimmerSelected(AbstractShimmer<?> shimmer) {
		if (shimmer == null) return;
		fireReceiverChangeEvent(shimmer.toString(), MediatorConstants.SHIMMER_RECTANGLE);
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {

				case SHIMMER_RECTANGLE:
					setItemSelected(e.getStringValue());
					break;
				case IS_SHIMMER:
					setShimmerSelected(e.getBooleanValue());
					break;
				default:
					break;

				}

			}
		});
	}

	private void setItemSelected(final String stringValue) {
		AbstractShimmer<?> shimmer = ShimmerRegister.getShimmer(stringValue);
		if (shimmer == null) return;
		String selected = shimmers.getValue();
		if (selected != null && selected.equals(shimmer.toString())) return;

		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				shimmers.setValue(stringValue);
			}
		});
	}

	/**
	 * Gets the shimmers.
	 *
	 * @return the shimmers
	 */
	public ComboBox<String> getShimmers() {
		return shimmers;
	}

	@Override
	protected Node getContentPane() {
		return fp;
	}

}
