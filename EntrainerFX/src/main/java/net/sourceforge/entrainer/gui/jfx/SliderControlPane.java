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
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.GridPane;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;

// TODO: Auto-generated Javadoc
/**
 * The Class SliderControlPane.
 */
public class SliderControlPane extends GridPane {

	/** The Constant CSS_ID. */
	public static final String CSS_ID = "slider-control-pane";
	private Slider entrainmentFrequency = new Slider(0, 40, 10);
	private Slider frequency = new Slider(20, 500, 200);
	private Slider amplitude = new Slider(0, 1, 0.5);
	private Slider pinkNoise = new Slider(0, 1, 0);

	// Formatting for label values
	private DecimalFormat entrainmentFormat = new DecimalFormat("#0.00Hz");
	private DecimalFormat amplitudeFormat = new DecimalFormat("#0%");
	private DecimalFormat frequencyFormat = new DecimalFormat("##0Hz");

	private Label entrainmentValue = new Label();
	private Label frequencyValue = new Label();
	private Label amplitudeValue = new Label();
	private Label pinkNoiseValue = new Label();

	private Sender sender = new SenderAdapter();

	/**
	 * Instantiates a new slider control pane.
	 */
	public SliderControlPane() {
		super();
		init();
	}

	private void init() {
		initMediator();

		setId(CSS_ID);

		setHgap(10);
		setVgap(20);
		setPadding(new Insets(10));

		initSlider(entrainmentFrequency, entrainmentValue, entrainmentFormat, MediatorConstants.ENTRAINMENT_FREQUENCY);
		initSlider(frequency, frequencyValue, frequencyFormat, MediatorConstants.FREQUENCY);
		initSlider(amplitude, amplitudeValue, amplitudeFormat, MediatorConstants.AMPLITUDE);
		initSlider(pinkNoise, pinkNoiseValue, amplitudeFormat, MediatorConstants.PINK_NOISE_AMPLITUDE);

		entrainmentFrequency.setBlockIncrement(0.01);
		amplitude.setBlockIncrement(0.01);
		pinkNoise.setBlockIncrement(0.01);
		frequency.setBlockIncrement(1.0);

		addSlider("Entrainment Frequency", entrainmentFrequency, entrainmentValue, 0);
		addSlider("Frequency", frequency, frequencyValue, 1);
		addSlider("Amplitude", amplitude, amplitudeValue, 2);
		addSlider("Pink Noise", pinkNoise, pinkNoiseValue, 3);
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addSender(sender);
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case AMPLITUDE:
					setAmplitudeValue(e.getDoubleValue());
					break;
				case ENTRAINMENT_FREQUENCY:
					setEntrainmentFrequencyValue(e.getDoubleValue());
					break;
				case FREQUENCY:
					setFrequencyValue(e.getDoubleValue());
					break;
				case PINK_NOISE_AMPLITUDE:
					setPinkNoiseValue(e.getDoubleValue());
					break;
				default:
					break;

				}
			}

		});
	}

	private void addSlider(String label, Slider slider, Label value, int row) {
		slider.setId(label);
		add(new Label(label), 0, row);
		add(slider, 1, row);
		add(value, 2, row);
	}

	private void initSlider(final Slider slider, final Label label, final DecimalFormat format,
			final MediatorConstants event) {

		Reflection reflection = new Reflection();
		reflection.setFraction(0.5);
		reflection.setTopOffset(0.1);

		slider.setEffect(new Blend(BlendMode.COLOR_BURN, new InnerShadow(), reflection));

		slider.setMinWidth(350);

		slider.valueProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable arg0) {
				double value = slider.getValue();
				label.setText(format.format(value));
				fireReceiverChangeEvent(value, event);
			}
		});

		label.setText(format.format(slider.getValue()));
		fireReceiverChangeEvent(slider.getValue(), event);
	}

	private void fireReceiverChangeEvent(double value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	/**
	 * Sets the amplitude value.
	 *
	 * @param value
	 *          the new amplitude value
	 */
	public void setAmplitudeValue(double value) {
		setValue(value, amplitude);
	}

	/**
	 * Sets the entrainment frequency value.
	 *
	 * @param value
	 *          the new entrainment frequency value
	 */
	public void setEntrainmentFrequencyValue(double value) {
		setValue(value, entrainmentFrequency);
	}

	/**
	 * Sets the frequency value.
	 *
	 * @param value
	 *          the new frequency value
	 */
	public void setFrequencyValue(double value) {
		setValue(value, frequency);
	}

	/**
	 * Sets the pink noise value.
	 *
	 * @param value
	 *          the new pink noise value
	 */
	public void setPinkNoiseValue(double value) {
		setValue(value, pinkNoise);
	}

	private void setValue(final double value, final Slider slider) {
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				slider.setValue(value);
			}
		});
	}

	/**
	 * Gets the entrainment frequency.
	 *
	 * @return the entrainment frequency
	 */
	public Slider getEntrainmentFrequency() {
		return entrainmentFrequency;
	}

	/**
	 * Gets the frequency.
	 *
	 * @return the frequency
	 */
	public Slider getFrequency() {
		return frequency;
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
	 * Gets the pink noise.
	 *
	 * @return the pink noise
	 */
	public Slider getPinkNoise() {
		return pinkNoise;
	}

}
