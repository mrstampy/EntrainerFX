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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;
import net.sourceforge.entrainer.sound.MasterLevelController;

// TODO: Auto-generated Javadoc
/**
 * The Class SliderControlPane.
 */
public class SliderControlPane extends AbstractTitledPane {
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

	private GridPane pane = new GridPane();

	private Slider pinkAmplitude = new Slider(0, 1, 0.5);
	private Slider multiple = new Slider(0, 512, 32);
	private CheckBox panCheck = new CheckBox("Pan Pink Noise");

	private DecimalFormat pinkAmplitudeFormat = new DecimalFormat("#0%");
	private DecimalFormat multipleFormat = new DecimalFormat("#");

	private Label pinkAmplitudeValue = new Label("100");
	private Label multipleValue = new Label("1.00");

	private MasterLevelController masterLevelController = new MasterLevelController();

	private boolean showPanSliders;

	/**
	 * Instantiates a new slider control pane.
	 */
	public SliderControlPane() {
		this(true);
	}

	/**
	 * Instantiates a new slider control pane.
	 *
	 * @param showPanSliders
	 *          the show pan sliders
	 */
	public SliderControlPane(boolean showPanSliders) {
		super("Sound Options");
		this.showPanSliders = showPanSliders;
		init();
	}

	/**
	 * Sets the controls disabled.
	 *
	 * @param b
	 *          the new controls disabled
	 */
	public void setControlsDisabled(boolean b) {
		entrainmentFrequency.setDisable(b);
		frequency.setDisable(b);
		amplitude.setDisable(b);
		pinkNoise.setDisable(b);
		pinkAmplitude.setDisable(b);
		multiple.setDisable(b);
		panCheck.setDisable(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.gui.jfx.AbstractTitledPane#init()
	 */
	protected void init() {
		initLayout();
		super.init();
	}

	private void initLayout() {
		initMediator();

		setId(CSS_ID);

		pane.setHgap(10);
		pane.setVgap(20);
		pane.setPadding(new Insets(10));

		if (showPanSliders) {
			initSlider(entrainmentFrequency, entrainmentValue, entrainmentFormat, MediatorConstants.ENTRAINMENT_FREQUENCY);
			initSlider(frequency, frequencyValue, frequencyFormat, MediatorConstants.FREQUENCY);
			initSlider(amplitude, amplitudeValue, amplitudeFormat, MediatorConstants.AMPLITUDE);
			initSlider(pinkNoise, pinkNoiseValue, amplitudeFormat, MediatorConstants.PINK_NOISE_AMPLITUDE);
			initSlider(pinkAmplitude, pinkAmplitudeValue, pinkAmplitudeFormat, MediatorConstants.PINK_PAN_AMPLITUDE);
			initSlider(multiple, multipleValue, multipleFormat, MediatorConstants.PINK_ENTRAINER_MULTIPLE);

			pinkAmplitude.setBlockIncrement(0.01);
		}

		panCheck.setOnAction(e -> panChecked());

		entrainmentFrequency.setBlockIncrement(0.01);
		amplitude.setBlockIncrement(0.01);
		pinkNoise.setBlockIncrement(0.01);
		frequency.setBlockIncrement(1.0);

		int row = 0;

		if (showPanSliders) {
			addSlider("Entrainment Frequency", entrainmentFrequency, entrainmentValue, row++);
			addSlider("Frequency", frequency, frequencyValue, row++);
			addSlider("Volume", amplitude, amplitudeValue, row++);
		}

		pane.add(panCheck, 0, row++);

		if (showPanSliders) {
			addSlider("Pink Noise Volume", pinkNoise, pinkNoiseValue, row++);
			addSlider("Pan Amplitude", pinkAmplitude, pinkAmplitudeValue, row++);
			addSlider("Entrainer Multiple", multiple, multipleValue, row++);
		}

		setTextFill(panCheck);
		setTooltips();

		pane.setAlignment(Pos.CENTER);
	}

	private void setTooltips() {
		setTooltip(entrainmentFrequency, "Sets the entrainment frequency");
		setTooltip(frequency, "Sets the base frequency for entrainment");
		setTooltip(amplitude, "Sets the volume of the base frequency");
		setTooltip(pinkNoise, "Sets the volume of the generated pink noise");
		setTooltip(pinkAmplitude, "Sets the pan amplitude of the generated pink noise");
		setTooltip(multiple, "Sets the speed of pink noise panning");
		setTooltip(panCheck, "Enables/disables pink noise panning");
	}

	private void panChecked() {
		if (!pinkNoise.isDisable()) setPanSliderState();

		fireReceiverChangeEvent(panCheck.isSelected(), MediatorConstants.PINK_PAN);
	}

	private void setPanSliderState() {
		if (!showPanSliders) return;

		boolean disabled = !panCheck.isSelected();

		pinkAmplitude.setDisable(disabled);
		multiple.setDisable(disabled);
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addSender(sender);
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case AMPLITUDE:
				case DELTA_AMPLITUDE:
					setAmplitudeValue(masterLevelController.getAmplitude());
					break;
				case ENTRAINMENT_FREQUENCY:
				case DELTA_ENTRAINMENT_FREQUENCY:
					setEntrainmentFrequencyValue(masterLevelController.getEntrainmentFrequency());
					break;
				case FREQUENCY:
				case DELTA_FREQUENCY:
					setFrequencyValue(masterLevelController.getFrequency());
					break;
				case PINK_NOISE_AMPLITUDE:
				case DELTA_PINK_NOISE_AMPLITUDE:
					setPinkNoiseValue(masterLevelController.getPinkNoiseAmplitude());
					break;
				case PINK_PAN_AMPLITUDE:
					setPinkAmplitude(masterLevelController.getPinkPanAmplitude());
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
					setPinkAmplitude(masterLevelController.getPinkPanAmplitude());
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

		pane.add(title, 0, row);

		pane.add(slider, 1, row);
		pane.add(value, 2, row);
	}

	private void initSlider(final Slider slider, final Label label, final DecimalFormat format,
			final MediatorConstants event) {
		slider.setEffect(new InnerShadow());

		slider.setMinWidth(350);

		slider.valueProperty().addListener(e -> onSliderChange(slider, label, format, event));

		label.setText(format.format(slider.getValue()));
		fireReceiverChangeEvent(slider.getValue(), event);
	}

	private void onSliderChange(final Slider slider, final Label label, final DecimalFormat format,
			final MediatorConstants event) {
		double value = slider.getValue();
		label.setText(format.format(value));
		fireReceiverChangeEvent(value, event);
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
		JFXUtils.runLater(() -> slider.setValue(value));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.gui.jfx.AbstractTitledPane#getContentPane()
	 */
	@Override
	protected Node getContentPane() {
		return pane;
	}

	private void setPan(boolean booleanValue) {
		JFXUtils.runLater(() -> setPanCheck(booleanValue));
	}

	private void setPanCheck(boolean booleanValue) {
		panCheck.setSelected(booleanValue);
		if (!pinkNoise.isDisable()) setPanSliderState();
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
	public void setPinkAmplitude(double value) {
		setValue(value, pinkAmplitude);
	}

	/**
	 * Gets the amplitude.
	 *
	 * @return the amplitude
	 */
	public Slider getPinkAmplitude() {
		return pinkAmplitude;
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
