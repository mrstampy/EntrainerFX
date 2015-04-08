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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.sourceforge.entrainer.gui.jfx.shimmer.AbstractShimmer;
import net.sourceforge.entrainer.gui.jfx.shimmer.ShimmerRegister;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class ShimmerOptionsPane.
 */
public class ShimmerOptionsPane extends AbstractTitledPane {

	private ComboBox<String> shimmers = new ComboBox<String>();
	private CheckBox shimmer = new CheckBox("Shimmer");
	private CheckBox applyShimmer = new CheckBox("Flash Shimmer");
	private HBox fp;

	/**
	 * Instantiates a new shimmer options pane.
	 */
	public ShimmerOptionsPane() {
		super("Shimmer Options");
		init();
	}

	/**
	 * Checks if is flash shimmer.
	 *
	 * @return true, if is flash shimmer
	 */
	public boolean isFlashShimmer() {
		return applyShimmer.isSelected();
	}

	/**
	 * Sets the flash shimmer.
	 *
	 * @param b
	 *          the new flash shimmer
	 */
	public void setFlashShimmer(boolean b) {
		if (applyShimmer.isSelected() == b) return;
		applyShimmer.setSelected(b);
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

	private void setToolTips() {
		setTooltip(shimmer, "Adds a shimmer effect to the application");
		setTooltip(applyShimmer, "Apply the chosen flash effect selected in the Flash Options to the shimmers");

		setOnMouseClicked(e -> localDoc(e));
	}

	private void localDoc(MouseEvent e) {
		if (!(e.isMetaDown() && e.getClickCount() == 1)) return;

		Utils.openLocalDocumentation("shimmers.html");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.gui.jfx.AbstractTitledPane#init()
	 */
	protected void init() {
		initMediator();
		setToolTips();
		setText("Shimmer Options");

		shimmers.getItems().addAll(ShimmerRegister.getShimmerNames());
		initCheckBox(shimmer, MediatorConstants.IS_SHIMMER);

		fp = new HBox(10, getCheckBoxLayout(), shimmers);

		shimmers.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				fireShimmerSelected(ShimmerRegister.getShimmer(shimmers.getValue()));
			}
		});

		applyShimmer.setOnAction(e -> applyShimmerClicked());

		fp.setAlignment(Pos.CENTER);

		super.init();
	}

	private void applyShimmerClicked() {
		fireReceiverChangeEvent(applyShimmer.isSelected(), MediatorConstants.APPLY_FLASH_TO_SHIMMER);
	}

	private Node getCheckBoxLayout() {
		return new VBox(10, shimmer, applyShimmer);
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
				case APPLY_FLASH_TO_SHIMMER:
					JFXUtils.runLater(() -> setFlashShimmer(e.getBooleanValue()));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.gui.jfx.AbstractTitledPane#getContentPane()
	 */
	@Override
	protected Node getContentPane() {
		return fp;
	}

}
