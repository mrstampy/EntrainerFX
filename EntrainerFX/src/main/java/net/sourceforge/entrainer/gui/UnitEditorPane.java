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
package net.sourceforge.entrainer.gui;

import static net.sourceforge.entrainer.mediator.MediatorConstants.AMPLITUDE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.ENTRAINMENT_FREQUENCY;
import static net.sourceforge.entrainer.mediator.MediatorConstants.FREQUENCY;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_ENTRAINER_MULTIPLE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_NOISE_AMPLITUDE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_PAN_AMPLITUDE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.START_ENTRAINMENT;
import static net.sourceforge.entrainer.mediator.MediatorConstants.START_FLASHING;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.guitools.MigHelper;
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
public class UnitEditorPane extends JPanel implements UnitEditorPaneConstants, UnitEditorListener {

	private static final long serialVersionUID = 1L;

	private JSpinner startFrequency;
	private JSpinner endFrequency;
	private JSpinner startEntrainmentFrequency;
	private JSpinner endEntrainmentFrequency;
	private JSpinner startAmplitude;
	private JSpinner endAmplitude;
	private JSpinner startPinkNoise;
	private JSpinner endPinkNoise;
	private JSpinner startPinkPanAmplitude;
	private JSpinner endPinkPanAmplitude;
	private JSpinner startPinkEntrainerMultiple;
	private JSpinner endPinkEntrainerMultiple;
	private JSpinner minutes;
	private JSpinner seconds;

	private JToggleButton testStart = new JToggleButton();
	private JToggleButton testEnd = new JToggleButton();

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
			startAmplitude.setValue(e.getValue());
			break;
		case END_FREQUENCY:
			startFrequency.setValue(e.getValue());
			break;
		case END_ENTRAINMENT_FREQUENCY:
			startEntrainmentFrequency.setValue(e.getValue());
			break;
		case END_PINK_ENTRAINER_MULTIPLE:
			startPinkEntrainerMultiple.setValue(e.getValue());
			break;
		case END_PINK_NOISE:
			startPinkNoise.setValue(e.getValue());
			break;
		case END_PINK_PAN_AMPLITUDE:
			startPinkPanAmplitude.setValue(e.getValue());
			break;
		default:
			break;
		}
	}

	private void setFromNext(UnitEditorEvent e) {
		switch (e.getParm()) {
		case START_AMPLITUDE:
			endAmplitude.setValue(e.getValue());
			break;
		case START_FREQUENCY:
			endFrequency.setValue(e.getValue());
			break;
		case START_ENTRAINMENT_FREQUENCY:
			endEntrainmentFrequency.setValue(e.getValue());
			break;
		case START_PINK_ENTRAINER_MULTIPLE:
			endPinkEntrainerMultiple.setValue(e.getValue());
			break;
		case START_PINK_NOISE:
			endPinkNoise.setValue(e.getValue());
			break;
		case START_PINK_PAN_AMPLITUDE:
			endPinkPanAmplitude.setValue(e.getValue());
			break;
		default:
			break;
		}
	}

	private int getIndex() {
		JTabbedPane parent = (JTabbedPane) getParent();

		return parent.indexOfComponent(this);
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
	public void addTimeChangeListener(ChangeListener l) {
		minutes.addChangeListener(l);
		seconds.addChangeListener(l);
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
	protected void fireTestUnitEvent(JToggleButton button) {
		int action = button.isSelected() ? TestUnitEvent.ACTION_START : TestUnitEvent.ACTION_STOP;
		int terminal = button == testStart ? TestUnitEvent.TERMINAL_START : TestUnitEvent.TERMINAL_END;

		TestUnitEvent e = new TestUnitEvent(this, getUnit(), terminal, action);

		fireReceiverChangeEvents(e.getUnitSetter());

		fireReceiverChangeEvent(button.isSelected(), START_FLASHING);
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
		layoutComponents();
		setComponentNames();
	}

	private void setComponentNames() {
		startFrequency.setName(UEPC_START_FREQUENCY_NAME);
		endFrequency.setName(UEPC_END_FREQUENCY_NAME);
		startEntrainmentFrequency.setName(UEPC_START_ENTRAINMENT_FREQUENCY_NAME);
		endEntrainmentFrequency.setName(UEPC_END_ENTRAINMENT_FREQUENCY_NAME);
		startAmplitude.setName(UEPC_START_AMPLITUDE_NAME);
		endAmplitude.setName(UEPC_END_AMPLITUDE_NAME);
		startPinkNoise.setName(UEPC_START_PINK_NOISE_NAME);
		endPinkNoise.setName(UEPC_END_PINK_NOISE_NAME);
		startPinkPanAmplitude.setName(UEPC_START_PINK_PAN_AMPLITUDE_NAME);
		endPinkPanAmplitude.setName(UEPC_END_PINK_PAN_AMPLITUDE_NAME);
		startPinkEntrainerMultiple.setName(UEPC_START_PINK_ENTRAINER_MULTIPLE_NAME);
		endPinkEntrainerMultiple.setName(UEPC_END_PINK_ENTRAINER_MULTIPLE_NAME);
		minutes.setName(UEPC_MINUTES_NAME);
		seconds.setName(UEPC_SECONDS_NAME);
		testStart.setName(UEPC_TEST_START_NAME);
		testEnd.setName(UEPC_TEST_END_NAME);
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addSender(sender);
	}

	private void addListeners() {
		testStart.setActionCommand("Start");
		testEnd.setActionCommand("End");
		GuiUtil.initButton("Play", testStart, "Test Start Parameters");
		GuiUtil.initButton("Play", testEnd, "Test End Parameters");
		testStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				toggleButtonPressed(testStart, testEnd);
			}
		});

		testEnd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				toggleButtonPressed(testEnd, testStart);
			}
		});

		addValueListeners();
	}

	private void addValueListeners() {
		startAmplitude.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				startAmplitudeChanged();
			}
		});
		startAmplitude.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				startAmplitudeChanged();
			}
		});

		startEntrainmentFrequency.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				startEntrainmentFrequencyChanged();
			}
		});
		startEntrainmentFrequency.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				startEntrainmentFrequencyChanged();
			}
		});

		startFrequency.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				startFrequencyChanged();
			}
		});
		startFrequency.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				startFrequencyChanged();
			}
		});

		startPinkEntrainerMultiple.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				startPinkEntrainerMultipleChanged();
			}
		});
		startPinkEntrainerMultiple.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				startPinkEntrainerMultipleChanged();
			}
		});

		startPinkNoise.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				startPinkNoiseChanged();
			}
		});
		startPinkNoise.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				startPinkNoiseChanged();
			}
		});

		startPinkPanAmplitude.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				startPinkPanAmplitudeChanged();
			}
		});
		startPinkPanAmplitude.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				startPinkPanAmplitudeChanged();
			}
		});

		endAmplitude.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				endAmplitudeChanged();
			}
		});
		endAmplitude.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				endAmplitudeChanged();
			}
		});

		endEntrainmentFrequency.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				endEntrainmentFrequencyChanged();
			}
		});
		endEntrainmentFrequency.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				endEntrainmentFrequencyChanged();
			}
		});

		endFrequency.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				endFrequencyChanged();
			}
		});
		endFrequency.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				endFrequencyChanged();
			}
		});

		endPinkEntrainerMultiple.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				endPinkEntrainerMultipleChanged();
			}
		});
		endPinkEntrainerMultiple.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				endPinkEntrainerMultipleChanged();
			}
		});

		endPinkNoise.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				endPinkNoiseChanged();
			}
		});
		endPinkNoise.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				endPinkNoiseChanged();
			}
		});

		endPinkPanAmplitude.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				endPinkPanChanged();
			}
		});
		endPinkPanAmplitude.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				endPinkPanChanged();
			}
		});
	}

	private void fireUnitEditorEvent(UnitEditorParm parm, double value) {
		UnitEditorEvent event = new UnitEditorEvent(this, getIndex(), parm, value);
		for (UnitEditorListener l : listeners) {
			l.unitEditorEventPerformed(event);
		}
	}

	private void toggleButtonPressed(JToggleButton pressed, JToggleButton other) {
		other.setEnabled(!pressed.isSelected());

		setFieldsEnabled(!pressed.isSelected(), pressed.getActionCommand());

		fireTestUnitEvent(pressed);
	}

	private void setFieldsEnabled(boolean enabled, String buttonText) {
		minutes.setEnabled(enabled);
		seconds.setEnabled(enabled);
		if ("Start".equals(buttonText)) {
			setEndFieldsEnabled(enabled);
		} else {
			setStartFieldsEnabled(enabled);
		}
	}

	private void setStartFieldsEnabled(boolean enabled) {
		startFrequency.setEnabled(enabled);
		startEntrainmentFrequency.setEnabled(enabled);
		startAmplitude.setEnabled(enabled);
		startPinkNoise.setEnabled(enabled);
		startPinkPanAmplitude.setEnabled(enabled);
		startPinkEntrainerMultiple.setEnabled(enabled);
	}

	private void setEndFieldsEnabled(boolean enabled) {
		endFrequency.setEnabled(enabled);
		endEntrainmentFrequency.setEnabled(enabled);
		endAmplitude.setEnabled(enabled);
		endPinkNoise.setEnabled(enabled);
		endPinkPanAmplitude.setEnabled(enabled);
		endPinkEntrainerMultiple.setEnabled(enabled);
	}

	private void layoutComponents() {
		MigHelper mh = new MigHelper(this);
		mh.setLayoutFillX(true);
		mh.growX(100);
		mh.setLayoutInsets(0, 0, 0, 0);

		mh.addLast(getTimeContainer()).add(getStartEndContainer());
	}

	private Container getTimeContainer() {
		JPanel jp = new JPanel();
		jp.setBorder(new BevelBorder(BevelBorder.RAISED));
		MigHelper mh = new MigHelper(jp);

		mh.alignWest().add("Minutes:").alignEast().add(minutes);
		mh.alignWest().add("Seconds:").alignEast().add(seconds);

		return mh.getContainer();
	}

	private Container getStartEndContainer() {
		JPanel jp = new JPanel();
		jp.setBorder(new BevelBorder(BevelBorder.LOWERED));
		MigHelper mh = new MigHelper(jp);
		mh.setLayoutInsets(10, 10, 10, 10);

		addLine(mh, "Entrainment Frequency (0 -> 40Hz)", startEntrainmentFrequency, endEntrainmentFrequency);
		addLine(mh, "Frequency (20 -> 500Hz)", startFrequency, endFrequency);
		addLine(mh, "Amplitude (0 -> 100)", startAmplitude, endAmplitude);
		addLine(mh, "Pink Noise (0 -> 100)", startPinkNoise, endPinkNoise);
		addLine(mh, "Pink Noise Pan (0 -> 100)", startPinkPanAmplitude, endPinkPanAmplitude);
		addLine(mh,
				"Pink Noise Entrainment Frequency Multiple (1 -> 512)",
				startPinkEntrainerMultiple,
				endPinkEntrainerMultiple);

		mh.skip(2).add(testStart).skip().add(testEnd);

		return mh.getContainer();
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

	private JSpinner createSpinner(double val, double min, double max, double incr) {
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(val, min, max, incr));

		setSpinnerSize(spinner);

		return spinner;
	}

	private JSpinner createSpinner(int val, int min, int max, int incr) {
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(val, min, max, incr));

		setSpinnerSize(spinner);

		return spinner;
	}

	private void setSpinnerSize(JSpinner spinner) {
		Dimension constant = new Dimension(70, 20);
		spinner.setMinimumSize(constant);
	}

	private void addLine(MigHelper mh, String label, JSpinner start, JSpinner end) {
		mh.alignWest().add(label).alignEast().add("Start:").alignWest().add(start);

		mh.alignEast().add("End:").alignWest().addLast(end);
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
		startFrequency.setValue(unit.getStartFrequency());
		endFrequency.setValue(unit.getEndFrequency());
		startEntrainmentFrequency.setValue(unit.getStartEntrainmentFrequency());
		endEntrainmentFrequency.setValue(unit.getEndEntrainmentFrequency());
		startAmplitude.setValue(unit.getStartAmplitude() * 100);
		endAmplitude.setValue(unit.getEndAmplitude() * 100);
		startPinkNoise.setValue(unit.getStartPinkNoise() * 100);
		endPinkNoise.setValue(unit.getEndPinkNoise() * 100);
		startPinkPanAmplitude.setValue(unit.getStartPinkPan() * 100);
		endPinkPanAmplitude.setValue(unit.getEndPinkPan() * 100);
		startPinkEntrainerMultiple.setValue(unit.getStartPinkEntrainerMultiple());
		endPinkEntrainerMultiple.setValue(unit.getEndPinkEntrainerMultiple());
		minutes.setValue(unit.getTime().getMinutes());
		seconds.setValue(unit.getTime().getSeconds());
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

	private double getDecimalValue(JSpinner spinner) {
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
