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

import java.text.DecimalFormat;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.GridPane;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.sound.MasterLevelController;

// TODO: Auto-generated Javadoc
/**
 * The Class PinkPanningPane.
 */
public class PinkPanningPane extends AbstractTitledPane {

	/** The Constant CSS_ID. */
	public static final String CSS_ID = "pink-panning-pane";
	private Slider amplitude = new Slider(0, 1, 0.5);
	private Slider multiple = new Slider(0, 512, 32);
	private CheckBox panCheck = new CheckBox("Pan");

	private DecimalFormat amplitudeFormat = new DecimalFormat("#0%");
	private DecimalFormat multipleFormat = new DecimalFormat("#");

	private Label amplitudeValue = new Label("100");
	private Label multipleValue = new Label("1.00");

	private GridPane gridPane = new GridPane();

	private MasterLevelController masterLevelController = new MasterLevelController();

	/**
	 * Instantiates a new pink panning pane.
	 */
	public PinkPanningPane() {
		this(true);
	}

	/**
	 * Instantiates a new pink panning pane.
	 *
	 * @param showSliders
	 *          the show sliders
	 */
	public PinkPanningPane(boolean showSliders) {
		super("Pink Noise Pan Options");
		init(showSliders);
	}

	@Override
	protected Node getContentPane() {
		return gridPane;
	}

	/**
	 * Clear mediator objects.
	 */
	public void clearMediatorObjects() {
		super.clearMediatorObjects();
		masterLevelController.clearMediator();
	}

	/**
	 * Sets the enabled.
	 *
	 * @param enable
	 *          the new enabled
	 */
	public void setEnabled(boolean enable) {
		amplitude.setDisable(!enable);
		multiple.setDisable(!enable);
		panCheck.setDisable(!enable);
	}

	private void init(boolean showSliders) {
		setId(CSS_ID);

		initMediator();

		gridPane.setHgap(10);
		gridPane.setVgap(20);
		gridPane.setPadding(new Insets(10));

		GridPane.setHalignment(panCheck, HPos.CENTER);

		if (showSliders) {
			initSlider(amplitude, amplitudeValue, amplitudeFormat, MediatorConstants.PINK_PAN_AMPLITUDE);
			initSlider(multiple, multipleValue, multipleFormat, MediatorConstants.PINK_ENTRAINER_MULTIPLE);

			amplitude.setBlockIncrement(0.01);
		}

		panCheck.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				panCheck.setUserData(Boolean.TRUE);
				fireReceiverChangeEvent(panCheck.isSelected(), MediatorConstants.PINK_PAN);
			}
		});

		gridPane.add(panCheck, 0, 0);

		if (showSliders) {
			addSlider("Pan Amplitude", amplitude, amplitudeValue, 1);
			addSlider("Entrainer Multiple", multiple, multipleValue, 2);
		}
		
		setTextFill(panCheck);
		
		super.init();
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case PINK_PAN_AMPLITUDE:
					setAmplitude(masterLevelController.getPinkPanAmplitude());
					if (!panCheck.isSelected()) {
						setPan(true);
					}
					break;
				case PINK_ENTRAINER_MULTIPLE:
				case DELTA_PINK_ENTRAINER_MULTIPLE:
					setMultiple(masterLevelController.getPinkEntrainerMultiple());
					break;
				case PINK_PAN:
					setPan(e.getBooleanValue());
					break;
				case DELTA_PINK_PAN_AMPLITUDE:
					setAmplitude(masterLevelController.getPinkPanAmplitude());
					break;
				default:
					break;
				}
			}

		});
	}

	private void addSlider(String label, Slider slider, Label value, int row) {
		slider.setId(label);
		Label title = new Label(label);
		setTextFill(title);
		setTextFill(value);
		gridPane.add(title, 0, row);
		gridPane.add(slider, 1, row);
		gridPane.add(value, 2, row);
	}

	private void initSlider(final Slider slider, final Label label, final DecimalFormat format,
			final MediatorConstants event) {
		setTextFill(label);
		slider.setEffect(new InnerShadow());

		slider.setMinWidth(300);

		slider.valueProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable arg0) {
				double value = slider.getValue();
				label.setText(format.format(value));
				fireReceiverChangeEvent(value, event);
				slider.setUserData(Boolean.TRUE);
			}
		});

		label.setText(format.format(slider.getValue()));
		fireReceiverChangeEvent(slider.getValue(), event);
	}

	/**
	 * Sets the pan.
	 *
	 * @param booleanValue
	 *          the new pan
	 */
	public void setPan(final boolean booleanValue) {
		if (panCheck.getUserData() != null) {
			panCheck.setUserData(null);
			return;
		}

		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				panCheck.setSelected(booleanValue);
			}
		});
	}

	/**
	 * Sets the multiple.
	 *
	 * @param value
	 *          the new multiple
	 */
	public void setMultiple(double value) {
		setValue(value, multiple);
	}

	/**
	 * Sets the amplitude.
	 *
	 * @param value
	 *          the new amplitude
	 */
	public void setAmplitude(double value) {
		setValue(value, amplitude);
	}

	private void setValue(final double value, final Slider slider) {
		if (slider.getUserData() != null) {
			slider.setUserData(null);
			return;
		}

		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				slider.setValue(value);
			}
		});
	}

	/**
	 * Gets the amplitude.
	 *
	 * @return the amplitude
	 */
	public Slider getAmplitude() {
		return amplitude;
	}

	/**
	 * Gets the multiple.
	 *
	 * @return the multiple
	 */
	public Slider getMultiple() {
		return multiple;
	}

	/**
	 * Gets the pan check.
	 *
	 * @return the pan check
	 */
	public CheckBox getPanCheck() {
		return panCheck;
	}

}
