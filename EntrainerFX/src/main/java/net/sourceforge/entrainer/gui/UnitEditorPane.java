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
package net.sourceforge.entrainer.gui;

import static net.sourceforge.entrainer.mediator.MediatorConstants.AMPLITUDE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.ENTRAINMENT_FREQUENCY;
import static net.sourceforge.entrainer.mediator.MediatorConstants.FREQUENCY;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_ENTRAINER_MULTIPLE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_NOISE_AMPLITUDE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_PAN_AMPLITUDE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.START_ENTRAINMENT;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import net.sourceforge.entrainer.gui.jfx.ControlButtonFactory;
import net.sourceforge.entrainer.guitools.GridPaneHelper;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;
import net.sourceforge.entrainer.xml.program.EntrainerProgramUnit;
import net.sourceforge.entrainer.xml.program.UnitSetter;

// TODO: Auto-generated Javadoc
/**
 * JPanel which provides fields + validation for an entrainer xml unit element.
 * 
 * @author burton
 */
public class UnitEditorPane extends Tab implements UnitEditorListener {

	private Spinner<Double> startFrequency;
	private Spinner<Double> endFrequency;
	private Spinner<Double> startEntrainmentFrequency;
	private Spinner<Double> endEntrainmentFrequency;
	private Spinner<Double> startAmplitude;
	private Spinner<Double> endAmplitude;
	private Spinner<Double> startPinkNoise;
	private Spinner<Double> endPinkNoise;
	private Spinner<Double> startPinkPanAmplitude;
	private Spinner<Double> endPinkPanAmplitude;
	private Spinner<Double> startPinkEntrainerMultiple;
	private Spinner<Double> endPinkEntrainerMultiple;
	private Spinner<Integer> minutes;
	private Spinner<Integer> seconds;

	private ToggleButton testStart = new ToggleButton();
	private ToggleButton testEnd = new ToggleButton();

	private List<TestUnitListener> testListeners = new ArrayList<TestUnitListener>();

	private EntrainerProgramUnit unit = null;

	private Sender sender = new SenderAdapter();

	private static List<UnitEditorListener> listeners = new ArrayList<>();

	/**
	 * Instantiates a new unit editor pane.
	 *
	 * @param unit
	 *          the unit
	 */
	public UnitEditorPane(EntrainerProgramUnit unit) {
		super();
		initSpinners();
		setUnit(unit);
		init();

		listeners.add(this);
	}

	/**
	 * Removes the listener.
	 */
	public void removeListener() {
		listeners.remove(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.gui.UnitEditorListener#unitEditorEventPerformed
	 * (net.sourceforge.entrainer.gui.UnitEditorEvent)
	 */
	@Override
	public void unitEditorEventPerformed(UnitEditorEvent e) {
		int index = getIndex();

		if (e.getIdx() == index + 1) {
			setFromNext(e);
		} else if (e.getIdx() == index - 1) {
			setFromPrev(e);
		}
	}

	private void setFromPrev(UnitEditorEvent e) {
		switch (e.getParm()) {
		case END_AMPLITUDE:
			startAmplitude.getValueFactory().setValue(e.getValue());
			break;
		case END_FREQUENCY:
			startFrequency.getValueFactory().setValue(e.getValue());
			break;
		case END_ENTRAINMENT_FREQUENCY:
			startEntrainmentFrequency.getValueFactory().setValue(e.getValue());
			break;
		case END_PINK_ENTRAINER_MULTIPLE:
			startPinkEntrainerMultiple.getValueFactory().setValue(e.getValue());
			break;
		case END_PINK_NOISE:
			startPinkNoise.getValueFactory().setValue(e.getValue());
			break;
		case END_PINK_PAN_AMPLITUDE:
			startPinkPanAmplitude.getValueFactory().setValue(e.getValue());
			break;
		default:
			break;
		}
	}

	private void setFromNext(UnitEditorEvent e) {
		switch (e.getParm()) {
		case START_AMPLITUDE:
			endAmplitude.getValueFactory().setValue(e.getValue());
			break;
		case START_FREQUENCY:
			endFrequency.getValueFactory().setValue(e.getValue());
			break;
		case START_ENTRAINMENT_FREQUENCY:
			endEntrainmentFrequency.getValueFactory().setValue(e.getValue());
			break;
		case START_PINK_ENTRAINER_MULTIPLE:
			endPinkEntrainerMultiple.getValueFactory().setValue(e.getValue());
			break;
		case START_PINK_NOISE:
			endPinkNoise.getValueFactory().setValue(e.getValue());
			break;
		case START_PINK_PAN_AMPLITUDE:
			endPinkPanAmplitude.getValueFactory().setValue(e.getValue());
			break;
		default:
			break;
		}
	}

	private int getIndex() {
		return getTabPane().getTabs().indexOf(this);
	}

	/**
	 * Clear mediator objects.
	 */
	public void clearMediatorObjects() {
		EntrainerMediator.getInstance().removeSender(sender);
	}

	/**
	 * Adds the specified {@link ChangeListener} to the minutes and seconds
	 * fields.
	 *
	 * @param l
	 *          the l
	 */
	public void addTimeChangeListener(InvalidationListener l) {
		minutes.getValueFactory().valueProperty().addListener(l);
		seconds.getValueFactory().valueProperty().addListener(l);
	}

	/**
	 * Adds the specified {@link TestUnitListener} to the list.
	 *
	 * @param l
	 *          the l
	 */
	public void addTestUnitListener(TestUnitListener l) {
		if (l != null & !testListeners.contains(l)) {
			testListeners.add(l);
		}
	}

	/**
	 * Fire test unit event.
	 *
	 * @param button
	 *          the button
	 */
	protected void fireTestUnitEvent(ToggleButton button) {
		int action = button.isSelected() ? TestUnitEvent.ACTION_START : TestUnitEvent.ACTION_STOP;
		int terminal = button == testStart ? TestUnitEvent.TERMINAL_START : TestUnitEvent.TERMINAL_END;

		TestUnitEvent e = new TestUnitEvent(this, getUnit(), terminal, action);

		fireReceiverChangeEvents(e.getUnitSetter());

		fireReceiverChangeEvent(button.isSelected(), START_ENTRAINMENT);

		for (TestUnitListener l : testListeners) {
			l.testUnitEventPerformed(e);
		}
	}

	private void fireReceiverChangeEvent(boolean value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	private void fireReceiverChangeEvents(UnitSetter unitSetter) {
		fireReceiverChangeEvent(unitSetter.getAmplitude(), AMPLITUDE);
		fireReceiverChangeEvent(unitSetter.getEntrainmentFrequency(), ENTRAINMENT_FREQUENCY);
		fireReceiverChangeEvent(unitSetter.getFrequency(), FREQUENCY);
		fireReceiverChangeEvent(unitSetter.getPinkEntrainerMultiple(), PINK_ENTRAINER_MULTIPLE);
		fireReceiverChangeEvent(unitSetter.getPinkNoise(), PINK_NOISE_AMPLITUDE);
		fireReceiverChangeEvent(unitSetter.getPinkPanAmplitude(), PINK_PAN_AMPLITUDE);
	}

	private void fireReceiverChangeEvent(double value, MediatorConstants parm) {
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, value, parm);
		sender.fireReceiverChangeEvent(e);
	}

	private void fireReceiverChangeEvent(Object value, MediatorConstants parm) {
		Double d = null;
		if (value instanceof Double) {
			d = ((Double) value);
		} else if (value instanceof Integer) {
			d = ((Integer) value).doubleValue();
		}
		if (d != null) {
			fireReceiverChangeEvent(d.doubleValue(), parm);
		}
	}

	private void init() {
		initMediator();
		addListeners();
		initButtons();
		layoutComponents();
	}

	private void initButtons() {
		ControlButtonFactory.decorateButton(testStart, "Play");
		ControlButtonFactory.decorateButton(testEnd, "Play");

		testStart.setUserData("Start");
		testEnd.setUserData("End");
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addSender(sender);
	}

	private void addListeners() {
		testStart.setOnAction(e -> toggleButtonPressed(testStart, testEnd));
		testEnd.setOnAction(e -> toggleButtonPressed(testEnd, testStart));

		addValueListeners();
	}

	private void addValueListeners() {
		startAmplitude.setOnMouseClicked(e -> startAmplitudeChanged());
		startAmplitude.focusedProperty().addListener(e -> startAmplitudeChanged());

		startEntrainmentFrequency.setOnMouseClicked(e -> startEntrainmentFrequencyChanged());
		startEntrainmentFrequency.focusedProperty().addListener(e -> startEntrainmentFrequencyChanged());

		startFrequency.setOnMouseClicked(e -> startFrequencyChanged());
		startFrequency.focusedProperty().addListener(e -> startFrequencyChanged());

		startPinkEntrainerMultiple.setOnMouseClicked(e -> startPinkEntrainerMultipleChanged());
		startPinkEntrainerMultiple.focusedProperty().addListener(e -> startPinkEntrainerMultipleChanged());

		startPinkNoise.setOnMouseClicked(e -> startPinkNoiseChanged());
		startPinkNoise.focusedProperty().addListener(e -> startPinkNoiseChanged());

		startPinkPanAmplitude.setOnMouseClicked(e -> startPinkPanAmplitudeChanged());
		startPinkPanAmplitude.focusedProperty().addListener(e -> startPinkPanAmplitudeChanged());

		endAmplitude.setOnMouseClicked(e -> endAmplitudeChanged());
		endAmplitude.focusedProperty().addListener(e -> endAmplitudeChanged());

		endEntrainmentFrequency.setOnMouseClicked(e -> endEntrainmentFrequencyChanged());
		endEntrainmentFrequency.focusedProperty().addListener(e -> endEntrainmentFrequencyChanged());

		endFrequency.setOnMouseClicked(e -> endFrequencyChanged());
		endFrequency.focusedProperty().addListener(e -> endFrequencyChanged());

		endPinkEntrainerMultiple.setOnMouseClicked(e -> endPinkEntrainerMultipleChanged());
		endPinkEntrainerMultiple.focusedProperty().addListener(e -> endPinkEntrainerMultipleChanged());

		endPinkNoise.setOnMouseClicked(e -> endPinkNoiseChanged());
		endPinkNoise.focusedProperty().addListener(e -> endPinkNoiseChanged());

		endPinkPanAmplitude.setOnMouseClicked(e -> endPinkPanChanged());
		endPinkPanAmplitude.focusedProperty().addListener(e -> endPinkPanChanged());
	}

	private void fireUnitEditorEvent(UnitEditorParm parm, double value) {
		UnitEditorEvent event = new UnitEditorEvent(this, getIndex(), parm, value);
		for (UnitEditorListener l : listeners) {
			l.unitEditorEventPerformed(event);
		}
	}

	private void toggleButtonPressed(ToggleButton pressed, ToggleButton other) {
		other.setDisable(pressed.isSelected());

		setFieldsDisabled(pressed.isSelected(), pressed.getUserData().toString());

		fireTestUnitEvent(pressed);
	}

	private void setFieldsDisabled(boolean enabled, String buttonText) {
		minutes.setDisable(enabled);
		seconds.setDisable(enabled);
		if ("Start".equals(buttonText)) {
			setEndFieldsDisabled(enabled);
		} else {
			setStartFieldsDisabled(enabled);
		}
	}

	private void setStartFieldsDisabled(boolean b) {
		startFrequency.setDisable(b);
		startEntrainmentFrequency.setDisable(b);
		startAmplitude.setDisable(b);
		startPinkNoise.setDisable(b);
		startPinkPanAmplitude.setDisable(b);
		startPinkEntrainerMultiple.setDisable(b);
	}

	private void setEndFieldsDisabled(boolean b) {
		endFrequency.setDisable(b);
		endEntrainmentFrequency.setDisable(b);
		endAmplitude.setDisable(b);
		endPinkNoise.setDisable(b);
		endPinkPanAmplitude.setDisable(b);
		endPinkEntrainerMultiple.setDisable(b);
	}

	private void layoutComponents() {
		VBox box = new VBox(10, getTimeContainer(), getStartEndContainer());
		box.setAlignment(Pos.CENTER);

		setContent(box);
	}

	private Node getTimeContainer() {
		GridPaneHelper gph = new GridPaneHelper();

		gph.add("Minutes:").add(minutes);
		gph.add("Seconds:").addLast(seconds);

		return gph.alignment(Pos.CENTER).padding(new Insets(10)).hGap(10).vGap(10).getPane();
	}

	private Node getStartEndContainer() {
		GridPaneHelper gph = new GridPaneHelper();

		addLine(gph, "Entrainment Frequency (0 -> 40Hz)", startEntrainmentFrequency, endEntrainmentFrequency);
		addLine(gph, "Frequency (20 -> 500Hz)", startFrequency, endFrequency);
		addLine(gph, "Amplitude (0 -> 100)", startAmplitude, endAmplitude);
		addLine(gph, "Pink Noise (0 -> 100)", startPinkNoise, endPinkNoise);
		addLine(gph, "Pink Noise Pan (0 -> 100)", startPinkPanAmplitude, endPinkPanAmplitude);
		addLine(gph,
				"Pink Noise Entrainment Frequency Multiple (1 -> 512)",
				startPinkEntrainerMultiple,
				endPinkEntrainerMultiple);

		gph.skip(2).add(testStart).skip().addLast(testEnd);

		return gph.alignment(Pos.CENTER).padding(new Insets(10)).hGap(10).vGap(10).getPane();
	}

	private void initSpinners() {
		startFrequency = createSpinner(100.0, 20.0, 500.0, 0.1);
		endFrequency = createSpinner(100.0, 20.0, 500.0, 0.1);
		startEntrainmentFrequency = createSpinner(10.0, 0.5, 40.0, 0.01);
		endEntrainmentFrequency = createSpinner(10.0, 0.5, 40.0, 0.01);
		startAmplitude = createSpinner(50, 0.0, 100, 1.0);
		endAmplitude = createSpinner(50, 0.0, 100, 1.0);
		startPinkNoise = createSpinner(50, 0.0, 100, 1.0);
		endPinkNoise = createSpinner(50, 0.0, 100, 1.0);
		startPinkPanAmplitude = createSpinner(50, 0.0, 100, 1.0);
		endPinkPanAmplitude = createSpinner(50, 0.0, 100, 1.0);
		startPinkEntrainerMultiple = createSpinner(100.0, 1.0, 512.0, 1.0);
		endPinkEntrainerMultiple = createSpinner(100.0, 1.0, 512.0, 1.0);
		minutes = createSpinner(0, 0, 240, 1);
		seconds = createSpinner(0, 0, 60, 1);
	}

	private Spinner<Double> createSpinner(double val, double min, double max, double incr) {
		Spinner<Double> spin = new Spinner<>(min, max, val, incr);

		spin.setPrefWidth(70);

		return spin;
	}

	private Spinner<Integer> createSpinner(int val, int min, int max, int incr) {
		Spinner<Integer> spin = new Spinner<>(min, max, val, incr);

		spin.setPrefWidth(70);

		return spin;
	}

	private void addLine(GridPaneHelper gph, String label, Spinner<Double> start, Spinner<Double> end) {
		gph.add(label).add("Start:").add(start).add("End:").addLast(end);
	}

	/**
	 * Call this method to get a string of errors in this unit. If the string is
	 * blank, there are no errors.
	 *
	 * @return the string
	 */
	public String validateFields() {
		StringBuffer buf = new StringBuffer();

		buf.append(validateTime());
		buf.append(validateFrequency());
		buf.append(validateEntrainmentFrequency());
		buf.append(validateAmplitude());
		buf.append(validatePinkNoise());
		buf.append(validatePinkNoisePan());
		buf.append(validatePinkNoiseEntrainmentMultiple());

		return buf.toString();
	}

	private String validatePinkNoiseEntrainmentMultiple() {
		StringBuffer buf = new StringBuffer();

		if (getValue(startPinkPanAmplitude.getValue()) > 0 || getValue(endPinkPanAmplitude.getValue()) > 0) {
			if (getValue(startPinkEntrainerMultiple.getValue()) < 1) {
				buf.append("Start pink noise EFM must be > 1 (and should be largish)\n\n");
			}
			if (getValue(endPinkEntrainerMultiple.getValue()) < 1) {
				buf.append("End pink noise EFM must be > 1 (and should be largish)\n\n");
			}
		}

		return buf.toString();
	}

	private String validatePinkNoisePan() {
		StringBuffer buf = new StringBuffer();

		if (getValue(startPinkPanAmplitude.getValue()) > 100) {
			buf.append("Start pink noise pan must be between 0 and 100\n\n");
		}
		if (getValue(endPinkPanAmplitude.getValue()) > 100) {
			buf.append("End pink noise pan must be between 0 and 100\n\n");
		}

		if (getValue(startPinkNoise.getValue()) == 0 && getValue(endPinkNoise.getValue()) == 0) {
			if (getValue(startPinkPanAmplitude.getValue()) > 0 || getValue(endPinkPanAmplitude.getValue()) > 0) {
				buf.append("Pink noise must be set for pink noise panning\n\n");
			}
		}

		return buf.toString();
	}

	private String validatePinkNoise() {
		StringBuffer buf = new StringBuffer();

		if (getValue(startPinkNoise.getValue()) > 100) {
			buf.append("Start pink noise must be between 0 and 100\n\n");
		}
		if (getValue(endPinkNoise.getValue()) > 100) {
			buf.append("End pink noise must be between 0 and 100\n\n");
		}

		return buf.toString();
	}

	private String validateAmplitude() {
		StringBuffer buf = new StringBuffer();

		if (getValue(startAmplitude.getValue()) > 100) {
			buf.append("Start amplitude must be between 0 and 100\n\n");
		}
		if (getValue(endAmplitude.getValue()) > 100) {
			buf.append("End amplitude must be between 0 and 100\n\n");
		}
		if (getValue(startAmplitude.getValue()) == 0 && getValue(endAmplitude.getValue()) == 0) {
			buf.append("Amplitude must have a start or an end > 0\n\n");
		}

		return buf.toString();
	}

	private String validateFrequency() {
		StringBuffer buf = new StringBuffer();

		if (getValue(startFrequency.getValue()) < 20 || getValue(startFrequency.getValue()) > 500) {
			buf.append("Start frequency must be in the range of 20 to 500Hz\n\n");
		}
		if (getValue(endFrequency.getValue()) < 20 || getValue(endFrequency.getValue()) > 500) {
			buf.append("End frequency must be in the range of 20 to 500Hz\n\n");
		}

		return buf.toString();
	}

	private String validateEntrainmentFrequency() {
		StringBuffer buf = new StringBuffer();

		if (getValue(startEntrainmentFrequency.getValue()) > 40) {
			buf.append("Start entrainment frequency must be in the range of 0 to 40Hz\n\n");
		}
		if (getValue(endEntrainmentFrequency.getValue()) > 40) {
			buf.append("End entrainment frequency must be in the range of 0 to 40Hz\n\n");
		}
		if (getValue(startEntrainmentFrequency.getValue()) == 0 && getValue(endEntrainmentFrequency.getValue()) == 0) {
			buf.append("Entrainment frequency must have either a start or an end > 0\n\n");
		}

		return buf.toString();
	}

	private String validateTime() {
		StringBuffer buf = new StringBuffer();

		if (getValue(minutes.getValue()) == 0 && getValue(seconds.getValue()) == 0) {
			buf.append("Time must be entered\n\n");
		}

		return buf.toString();
	}

	private void setUnitFields(EntrainerProgramUnit unit) {
		startFrequency.getValueFactory().setValue(unit.getStartFrequency());
		endFrequency.getValueFactory().setValue(unit.getEndFrequency());
		startEntrainmentFrequency.getValueFactory().setValue(unit.getStartEntrainmentFrequency());
		endEntrainmentFrequency.getValueFactory().setValue(unit.getEndEntrainmentFrequency());
		startAmplitude.getValueFactory().setValue(unit.getStartAmplitude() * 100);
		endAmplitude.getValueFactory().setValue(unit.getEndAmplitude() * 100);
		startPinkNoise.getValueFactory().setValue(unit.getStartPinkNoise() * 100);
		endPinkNoise.getValueFactory().setValue(unit.getEndPinkNoise() * 100);
		startPinkPanAmplitude.getValueFactory().setValue(unit.getStartPinkPan() * 100);
		endPinkPanAmplitude.getValueFactory().setValue(unit.getEndPinkPan() * 100);
		startPinkEntrainerMultiple.getValueFactory().setValue(unit.getStartPinkEntrainerMultiple());
		endPinkEntrainerMultiple.getValueFactory().setValue(unit.getEndPinkEntrainerMultiple());
		minutes.getValueFactory().setValue(unit.getTime().getMinutes());
		seconds.getValueFactory().setValue(unit.getTime().getSeconds());
	}

	/**
	 * Sets the specified {@link EntrainerProgramUnit} to this panel.
	 *
	 * @param unit
	 *          the new unit
	 */
	public void setUnit(EntrainerProgramUnit unit) {
		if (unit == null) {
			return;
		}

		this.unit = unit;
		setUnitFields(unit);
	}

	/**
	 * Returns the {@link EntrainerProgramUnit} populated with the fields from
	 * this panel.
	 *
	 * @return the unit
	 */
	public EntrainerProgramUnit getUnit() {
		setUnitStartAmplitude();
		setUnitStartEntrainmentFrequency();
		setUnitStartFrequency();
		setUnitStartPinkEntrainerMultiple();
		setUnitStartPinkNoise();
		setUnitStartPinkPan();

		setUnitEndAmplitude();
		setUnitEndEntrainmentFrequency();
		setUnitEndFrequency();
		setUnitEndPinkEntrainerMultiple();
		setUnitEndPinkNoise();
		setUnitEndPinkPan();

		unit.getTime().setMinutes(getValue(minutes.getValue()).intValue());
		unit.getTime().setSeconds(getValue(seconds.getValue()).intValue());

		return unit;
	}

	private void setUnitStartPinkPan() {
		unit.getStartUnitSetter().setPinkPanAmplitude(getDecimalValue(startPinkPanAmplitude));
	}

	private void setUnitEndPinkPan() {
		unit.getEndUnitSetter().setPinkPanAmplitude(getDecimalValue(endPinkPanAmplitude));
	}

	private void setUnitStartPinkNoise() {
		unit.getStartUnitSetter().setPinkNoise(getDecimalValue(startPinkNoise));
	}

	private void setUnitEndPinkNoise() {
		unit.getEndUnitSetter().setPinkNoise(getDecimalValue(endPinkNoise));
	}

	private double getDecimalValue(Spinner<Double> spinner) {
		return getValue(spinner.getValue()) / 100;
	}

	private void setUnitStartPinkEntrainerMultiple() {
		unit.getStartUnitSetter().setPinkEntrainerMultiple(getValue(startPinkEntrainerMultiple.getValue()));
	}

	private void setUnitEndPinkEntrainerMultiple() {
		unit.getEndUnitSetter().setPinkEntrainerMultiple(getValue(endPinkEntrainerMultiple.getValue()));
	}

	private void setUnitStartEntrainmentFrequency() {
		unit.getStartUnitSetter().setEntrainmentFrequency(getValue(startEntrainmentFrequency.getValue()));
	}

	private void setUnitEndEntrainmentFrequency() {
		unit.getEndUnitSetter().setEntrainmentFrequency(getValue(endEntrainmentFrequency.getValue()));
	}

	private void setUnitStartFrequency() {
		unit.getStartUnitSetter().setFrequency(getValue(startFrequency.getValue()));
	}

	private void setUnitEndFrequency() {
		unit.getEndUnitSetter().setFrequency(getValue(endFrequency.getValue()));
	}

	private void setUnitStartAmplitude() {
		unit.getStartUnitSetter().setAmplitude(getDecimalValue(startAmplitude));
	}

	private void setUnitEndAmplitude() {
		unit.getEndUnitSetter().setAmplitude(getDecimalValue(endAmplitude));
	}

	private Double getValue(Object value) {
		Double d = null;
		if (value instanceof Double) {
			d = ((Double) value);
		} else if (value instanceof Integer) {
			d = ((Integer) value).doubleValue();
		}

		return d;
	}

	/**
	 * Fire all changed.
	 */
	public void fireAllChanged() {
		Double value = getValue(startAmplitude.getValue());
		fireUnitEditorEvent(UnitEditorParm.START_AMPLITUDE, value);

		value = getValue(startEntrainmentFrequency.getValue());
		fireUnitEditorEvent(UnitEditorParm.START_ENTRAINMENT_FREQUENCY, value);

		value = getValue(startFrequency.getValue());
		fireUnitEditorEvent(UnitEditorParm.START_FREQUENCY, value);

		value = getValue(startPinkEntrainerMultiple.getValue());
		fireUnitEditorEvent(UnitEditorParm.START_PINK_ENTRAINER_MULTIPLE, value);

		value = getValue(startPinkNoise.getValue());
		fireUnitEditorEvent(UnitEditorParm.START_PINK_NOISE, value);

		value = getValue(startPinkPanAmplitude.getValue());
		fireUnitEditorEvent(UnitEditorParm.START_PINK_PAN_AMPLITUDE, value);

		value = getValue(endAmplitude.getValue());
		fireUnitEditorEvent(UnitEditorParm.END_AMPLITUDE, value);

		value = getValue(endEntrainmentFrequency.getValue());
		fireUnitEditorEvent(UnitEditorParm.END_ENTRAINMENT_FREQUENCY, value);

		value = getValue(endFrequency.getValue());
		fireUnitEditorEvent(UnitEditorParm.END_FREQUENCY, value);

		value = getValue(endPinkEntrainerMultiple.getValue());
		fireUnitEditorEvent(UnitEditorParm.END_PINK_ENTRAINER_MULTIPLE, value);

		value = getValue(endPinkNoise.getValue());
		fireUnitEditorEvent(UnitEditorParm.END_PINK_NOISE, value);

		value = getValue(endPinkPanAmplitude.getValue());
		fireUnitEditorEvent(UnitEditorParm.END_PINK_PAN_AMPLITUDE, value);
	}

	private void startAmplitudeChanged() {
		setUnitStartAmplitude();
		double decimalValue = getDecimalValue(startAmplitude);
		Double value = getValue(startAmplitude.getValue());
		fireReceiverChangeEvent(decimalValue, AMPLITUDE);
		fireUnitEditorEvent(UnitEditorParm.START_AMPLITUDE, value);
	}

	private void startEntrainmentFrequencyChanged() {
		setUnitStartEntrainmentFrequency();
		Double value = getValue(startEntrainmentFrequency.getValue());
		fireReceiverChangeEvent(value, ENTRAINMENT_FREQUENCY);
		fireUnitEditorEvent(UnitEditorParm.START_ENTRAINMENT_FREQUENCY, value);
	}

	private void startFrequencyChanged() {
		setUnitStartFrequency();
		Double value = getValue(startFrequency.getValue());
		fireReceiverChangeEvent(value, FREQUENCY);
		fireUnitEditorEvent(UnitEditorParm.START_FREQUENCY, value);
	}

	private void startPinkEntrainerMultipleChanged() {
		setUnitStartPinkEntrainerMultiple();
		Double value = getValue(startPinkEntrainerMultiple.getValue());
		fireReceiverChangeEvent(value, PINK_ENTRAINER_MULTIPLE);
		fireUnitEditorEvent(UnitEditorParm.START_PINK_ENTRAINER_MULTIPLE, value);
	}

	private void startPinkNoiseChanged() {
		setUnitStartPinkNoise();
		double decimalValue = getDecimalValue(startPinkNoise);
		Double value = getValue(startPinkNoise.getValue());
		fireReceiverChangeEvent(decimalValue, PINK_NOISE_AMPLITUDE);
		fireUnitEditorEvent(UnitEditorParm.START_PINK_NOISE, value);
	}

	private void startPinkPanAmplitudeChanged() {
		setUnitStartPinkPan();
		double decimalValue = getDecimalValue(startPinkPanAmplitude);
		Double value = getValue(startPinkPanAmplitude.getValue());
		fireReceiverChangeEvent(decimalValue, PINK_PAN_AMPLITUDE);
		fireUnitEditorEvent(UnitEditorParm.START_PINK_PAN_AMPLITUDE, value);
	}

	private void endAmplitudeChanged() {
		setUnitEndAmplitude();
		double decimalValue = getDecimalValue(endAmplitude);
		Double value = getValue(endAmplitude.getValue());
		fireReceiverChangeEvent(decimalValue, AMPLITUDE);
		fireUnitEditorEvent(UnitEditorParm.END_AMPLITUDE, value);
	}

	private void endEntrainmentFrequencyChanged() {
		setUnitEndEntrainmentFrequency();
		Double value = getValue(endEntrainmentFrequency.getValue());
		fireReceiverChangeEvent(value, ENTRAINMENT_FREQUENCY);
		fireUnitEditorEvent(UnitEditorParm.END_ENTRAINMENT_FREQUENCY, value);
	}

	private void endFrequencyChanged() {
		setUnitEndFrequency();
		Double value = getValue(endFrequency.getValue());
		fireReceiverChangeEvent(value, FREQUENCY);
		fireUnitEditorEvent(UnitEditorParm.END_FREQUENCY, value);
	}

	private void endPinkEntrainerMultipleChanged() {
		setUnitEndPinkEntrainerMultiple();
		Double value = getValue(endPinkEntrainerMultiple.getValue());
		fireReceiverChangeEvent(value, PINK_ENTRAINER_MULTIPLE);
		fireUnitEditorEvent(UnitEditorParm.END_PINK_ENTRAINER_MULTIPLE, value);
	}

	private void endPinkNoiseChanged() {
		setUnitEndPinkNoise();
		double decimalValue = getDecimalValue(endPinkNoise);
		Double value = getValue(endPinkNoise.getValue());
		fireReceiverChangeEvent(decimalValue, PINK_NOISE_AMPLITUDE);
		fireUnitEditorEvent(UnitEditorParm.END_PINK_NOISE, value);
	}

	private void endPinkPanChanged() {
		setUnitEndPinkPan();
		double decimalValue = getDecimalValue(endPinkPanAmplitude);
		Double value = getValue(endPinkPanAmplitude.getValue());
		fireReceiverChangeEvent(decimalValue, PINK_PAN_AMPLITUDE);
		fireUnitEditorEvent(UnitEditorParm.END_PINK_PAN_AMPLITUDE, value);
	}

}
