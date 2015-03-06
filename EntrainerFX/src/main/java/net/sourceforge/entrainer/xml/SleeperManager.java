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
package net.sourceforge.entrainer.xml;

import static net.sourceforge.entrainer.mediator.MediatorConstants.AMPLITUDE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.ENTRAINMENT_FREQUENCY;
import static net.sourceforge.entrainer.mediator.MediatorConstants.FREQUENCY;
import static net.sourceforge.entrainer.mediator.MediatorConstants.INTERVAL_ADD;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_ENTRAINER_MULTIPLE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_NOISE_AMPLITUDE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_PAN;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_PAN_AMPLITUDE;
import static net.sourceforge.entrainer.xml.program.EntrainerProgramUtil.unmarshal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.entrainer.gui.EntrainerFX;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;
import net.sourceforge.entrainer.pausethread.EntrainerPausibleThread;
import net.sourceforge.entrainer.pausethread.PauseEvent;
import net.sourceforge.entrainer.util.Utils;
import net.sourceforge.entrainer.xml.program.EntrainerProgram;
import net.sourceforge.entrainer.xml.program.EntrainerProgramInterval;
import net.sourceforge.entrainer.xml.program.EntrainerProgramUnit;

// TODO: Auto-generated Javadoc
/**
 * Class to manage {@link UnitSleeper}'s.
 *
 * @author burton
 */
public class SleeperManager {

	private EntrainerProgram xml;
	private UnitSleeper sleeper;

	private List<SleeperManagerListener> listeners = new ArrayList<SleeperManagerListener>();

	private Sender sender = new SenderAdapter();

	/**
	 * Instantiates a new sleeper manager.
	 *
	 * @param fileName
	 *          the file name
	 */
	public SleeperManager(String fileName) {
		super();
		initMediator();
		setEntrainerXml(fileName);
		createSleepers();
		fireStartParameters();
	}

	/**
	 * Inits the global settings.
	 */
	public void initGlobalSettings() {
		getXml().initGlobalSettings();
		fireStartParameters();
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addSender(sender);
	}

	/**
	 * Fire start parameters.
	 */
	public void fireStartParameters() {
		if (getUnits().size() > 0) {
			fireReceiverChangeEvent(getStartAmplitude(), AMPLITUDE);
			fireReceiverChangeEvent(getStartEntrainmentFrequency(), ENTRAINMENT_FREQUENCY);
			fireReceiverChangeEvent(getStartFrequency(), FREQUENCY);
			fireReceiverChangeEvent(getStartPinkEntrainerMultiple(), PINK_ENTRAINER_MULTIPLE);
			fireReceiverChangeEvent(getStartPinkNoise(), PINK_NOISE_AMPLITUDE);
			fireReceiverChangeEvent(getStartPinkPan(), PINK_PAN_AMPLITUDE);
			fireReceiverChangeEvent(xml.isPinkPan(), PINK_PAN);
			for (EntrainerProgramInterval i : getXml().getIntervals()) {
				fireReceiverChangeEvent(i.getValue(), INTERVAL_ADD);
			}
		}
	}

	private void fireReceiverChangeEvent(String value, MediatorConstants parm) {
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, value, parm);
		sender.fireReceiverChangeEvent(e);
	}

	private void fireReceiverChangeEvent(double value, MediatorConstants parm) {
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, value, parm);
		sender.fireReceiverChangeEvent(e);
	}

	private void fireReceiverChangeEvent(boolean value, MediatorConstants parm) {
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, value, parm);
		sender.fireReceiverChangeEvent(e);
	}

	/**
	 * Adds the sleeper manager listener.
	 *
	 * @param l
	 *          the l
	 */
	public void addSleeperManagerListener(SleeperManagerListener l) {
		listeners.add(l);
	}

	/**
	 * Fire sleeper manager event.
	 *
	 * @param action
	 *          the action
	 */
	protected void fireSleeperManagerEvent(int action) {
		SleeperManagerEvent e = new SleeperManagerEvent(this, action);
		for (SleeperManagerListener l : listeners) {
			l.sleeperManagerEventPerformed(e);
		}
	}

	/**
	 * Call this method to start the automation of {@link EntrainerFX}.
	 *
	 * @return the entrainer pausible thread
	 */
	public EntrainerPausibleThread start() {
		EntrainerPausibleThread t = new EntrainerPausibleThread("SleeperManager Thread") {
			private EntrainerPausibleThread sleeperThread;

			@Override
			public void doWork() {
				sleeperThread = sleeper.start();
				Utils.snooze(50);

				fireSleeperManagerEvent(SleeperManagerEvent.STARTED);
				boolean isRunning = true;

				while (isRunning) {
					Utils.snooze(1000);
					isRunning = sleeper.isRunning();
				}

				fireSleeperManagerEvent(SleeperManagerEvent.STOPPED);
			}

			@Override
			public void pauseEventPerformed(PauseEvent e) {
				sleeperThread.pauseEventPerformed(e);
				super.pauseEventPerformed(e);
			}
		};

		t.start();

		return t;
	}

	/**
	 * Call this method to stop {@link EntrainerFX} automation.
	 */
	public void stop() {
		sleeper.stop();
	}

	/**
	 * Gets the starting frequency from the {@link EntrainerProgramUnit} list.
	 *
	 * @return the start frequency
	 */
	public double getStartFrequency() {
		return getUnits().get(0).getStartFrequency();
	}

	/**
	 * Gets the starting entrainment frequency from the
	 * {@link EntrainerProgramUnit} list.
	 *
	 * @return the start entrainment frequency
	 */
	public double getStartEntrainmentFrequency() {
		return getUnits().get(0).getStartEntrainmentFrequency();
	}

	/**
	 * Gets the starting amplitude from the {@link EntrainerProgramUnit} list.
	 *
	 * @return the start amplitude
	 */
	public double getStartAmplitude() {
		return getUnits().get(0).getStartAmplitude();
	}

	/**
	 * Gets the starting pink noise value from the {@link EntrainerProgramUnit}
	 * list.
	 *
	 * @return the start pink noise
	 */
	public double getStartPinkNoise() {
		return getUnits().get(0).getStartPinkNoise();
	}

	/**
	 * Gets the starting pink noise panning from the {@link EntrainerProgramUnit}
	 * list.
	 *
	 * @return the start pink pan
	 */
	public double getStartPinkPan() {
		return getUnits().get(0).getStartPinkPan();
	}

	/**
	 * Gets the starting pink noise entrainer multiple value from the
	 * {@link EntrainerProgramUnit} list.
	 *
	 * @return the start pink entrainer multiple
	 */
	public double getStartPinkEntrainerMultiple() {
		return getUnits().get(0).getStartPinkEntrainerMultiple();
	}

	/**
	 * Clear mediator objects.
	 */
	public void clearMediatorObjects() {
		EntrainerMediator.getInstance().removeSender(sender);
		sleeper.clearMediatorObjects();
	}

	private void createSleepers() {
		sleeper = getUnitSleeper();
	}

	private List<EntrainerProgramUnit> getUnits() {
		return getXml().getUnits();
	}

	private UnitSleeper getUnitSleeper() {
		return new UnitSleeper(new LinkedList<EntrainerProgramUnit>(getXml().getUnits()));
	}

	private void setEntrainerXml(String fileName) {
		xml = unmarshal(fileName);
	}

	/**
	 * Gets the xml.
	 *
	 * @return the xml
	 */
	public EntrainerProgram getXml() {
		return xml;
	}

}
