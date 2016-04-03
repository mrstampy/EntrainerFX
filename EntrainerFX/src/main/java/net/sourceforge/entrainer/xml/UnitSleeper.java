/*
 *      ______      __             _                 _______  __
 *     / ____/___  / /__________ _(_)___  ___  _____/ ____/ |/ /
 *    / __/ / __ \/ __/ ___/ __ `/ / __ \/ _ \/ ___/ /_   |   / 
 *   / /___/ / / / /_/ /  / /_/ / / / / /  __/ /  / __/  /   |  
 *  /_____/_/ /_/\__/_/   \__,_/_/_/ /_/\___/_/  /_/    /_/|_|  
 *                                                          
 *
 * Copyright (C) 2008 - 2016 Burton Alexander
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

import static net.sourceforge.entrainer.mediator.MediatorConstants.DELTA_AMPLITUDE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.DELTA_ENTRAINMENT_FREQUENCY;
import static net.sourceforge.entrainer.mediator.MediatorConstants.DELTA_FREQUENCY;
import static net.sourceforge.entrainer.mediator.MediatorConstants.DELTA_PINK_ENTRAINER_MULTIPLE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.DELTA_PINK_NOISE_AMPLITUDE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.DELTA_PINK_PAN_AMPLITUDE;

import java.util.List;

import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;
import net.sourceforge.entrainer.pausethread.EntrainerPausibleThread;
import net.sourceforge.entrainer.pausethread.PauseEvent;
import net.sourceforge.entrainer.util.Utils;
import net.sourceforge.entrainer.xml.program.EntrainerProgramUnit;
import net.sourceforge.entrainer.xml.program.EntrainerProgramUnitAttribute;

// TODO: Auto-generated Javadoc
/**
 * This class manages event notification for one of the three sound controls.
 * 
 * @author burton
 */
public class UnitSleeper {

	private List<EntrainerProgramUnit> units;
	private boolean shouldRun = true;

	private Sender sender = new SenderAdapter();

	/**
	 * Instantiate with a list of {@link EntrainerProgramUnit}'s and the sleeper
	 * type.
	 *
	 * @param units
	 *          the units
	 */
	public UnitSleeper(List<EntrainerProgramUnit> units) {
		super();
		initMediator();
		setUnits(units);
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addSender(sender);
	}

	/**
	 * Clear mediator objects.
	 */
	public void clearMediatorObjects() {
		EntrainerMediator.getInstance().removeSender(sender);
	}

	/**
	 * Call this method to halt sleeper execution.
	 */
	public void stop() {
		shouldRun = false;
	}

	/**
	 * Returns true if this UnitSleeper is running.
	 *
	 * @return true, if is running
	 */
	public boolean isRunning() {
		return shouldRun;
	}

	/**
	 * Call this method to start sleeper execution.
	 *
	 * @return the entrainer pausible thread
	 */
	public EntrainerPausibleThread start() {
		shouldRun = true;
		EntrainerPausibleThread starterThread = new EntrainerPausibleThread("Units Processor Thread") {
			private EntrainerPausibleThread unitThread;

			@Override
			public void doWork() {
				EntrainerProgramUnit last = null;
				for (EntrainerProgramUnit unit : units) {
					if (!shouldRun) {
						continue;
					}
					unitThread = getThread(unit, last);
					unitThread.start();
					while (unitThread.isAlive() && shouldRun) {
						Utils.snooze(1000);
					}
					last = unit;
				}
				shouldRun = false;
			}

			@Override
			public void pauseEventPerformed(PauseEvent e) {
				unitThread.pauseEventPerformed(e);
				super.pauseEventPerformed(e);
			}
		};

		starterThread.start();

		return starterThread;
	}

	// Thread which fires UnitSleeperEvents
	private EntrainerPausibleThread getThread(final EntrainerProgramUnit unit, final EntrainerProgramUnit last) {
		EntrainerPausibleThread t = new EntrainerPausibleThread("Unit Processor Thread") {
			private EntrainerPausibleThread countDown;
			private long time;

			@Override
			public void doWork() {
				time = unit.getTimeInMillis();
				countDown = getCountdownThread(time);
				countDown.start();

				if (last != null) {
					fireUnitSleeperEvent(getUnitDifference(last, unit));
				}

				while (countDown.isAlive() && shouldRun) {
					Utils.snooze(1000);
					if (!isPaused()) {
						fireUnitSleeperEvent(unit);
					}
				}
			}

			@Override
			public void pauseEventPerformed(PauseEvent e) {
				countDown.pauseEventPerformed(e);
				super.pauseEventPerformed(e);
			}
		};

		return t;
	}

	// Thread to exist for the duration of the current unit.
	private EntrainerPausibleThread getCountdownThread(final long millis) {
		EntrainerPausibleThread t = new EntrainerPausibleThread("Countdown Thread") {
			private long remainder = millis;

			@Override
			public void doWork() {
				while (remainder > 0) {
					Utils.snooze(200);
					if (!isPaused()) {
						remainder -= 200;
					}
				}
			}
		};

		return t;
	}

	private EntrainerProgramUnit getUnitDifference(EntrainerProgramUnit last, EntrainerProgramUnit current) {
		EntrainerProgramUnit unit = new EntrainerProgramUnit();

		unit.setAmplitude(new EntrainerProgramUnitAttribute(last.getEndAmplitude(), current.getStartAmplitude()));
		unit.setEntrainmentFrequency(new EntrainerProgramUnitAttribute(last.getEndEntrainmentFrequency(), current
				.getStartEntrainmentFrequency()));
		unit.setFrequency(new EntrainerProgramUnitAttribute(last.getEndFrequency(), current.getStartFrequency()));
		unit.setPinkEntrainerMultiple(new EntrainerProgramUnitAttribute(last.getEndPinkEntrainerMultiple(), current
				.getStartPinkEntrainerMultiple()));
		unit.setPinkNoise(new EntrainerProgramUnitAttribute(last.getEndPinkNoise(), current.getStartPinkNoise()));
		unit.setPinkPan(new EntrainerProgramUnitAttribute(last.getEndPinkPan(), current.getStartPinkPan()));

		return unit;
	}

	/**
	 * Fires a sleeper event with the given delta.
	 *
	 * @param unit
	 *          the unit
	 */
	protected void fireUnitSleeperEvent(EntrainerProgramUnit unit) {
		fireReceiverChangeEvent(unit.getAmplitudeDeltaPerSecond(), unit.getEndAmplitude(), DELTA_AMPLITUDE);
		fireReceiverChangeEvent(unit.getFrequencyDeltaPerSecond(), unit.getEndFrequency(), DELTA_FREQUENCY);
		fireReceiverChangeEvent(unit.getEntrainmentFrequencyDeltaPerSecond(),
				unit.getEndEntrainmentFrequency(),
				DELTA_ENTRAINMENT_FREQUENCY);
		fireReceiverChangeEvent(unit.getPinkEntrainerMultipleDeltaPerSecond(),
				unit.getEndPinkEntrainerMultiple(),
				DELTA_PINK_ENTRAINER_MULTIPLE);
		fireReceiverChangeEvent(unit.getPinkNoiseDeltaPerSecond(), unit.getEndPinkNoise(), DELTA_PINK_NOISE_AMPLITUDE);
		fireReceiverChangeEvent(unit.getPinkPanDeltaPerSecond(), unit.getEndPinkPan(), DELTA_PINK_PAN_AMPLITUDE);
		fireReceiverChangeEvent(unit.getMediaAmplitudeDeltaPerSecond(),
				unit.getEndMediaAmplitude(),
				MediatorConstants.DELTA_MEDIA_AMPLITUDE);
		fireReceiverChangeEvent(unit.getMediaEntrainmentStrengthDeltaPerSecond(),
				unit.getEndMediaEntrainmentStrength(),
				MediatorConstants.DELTA_MEDIA_ENTRAINMENT_STRENGTH);
	}

	private void fireReceiverChangeEvent(double delta, double endValue, MediatorConstants parm) {
		if (delta != 0) {
			ReceiverChangeEvent e = new ReceiverChangeEvent(this, delta, endValue, parm);
			sender.fireReceiverChangeEvent(e);
		}
	}

	/**
	 * Gets the units.
	 *
	 * @return the units
	 */
	public List<EntrainerProgramUnit> getUnits() {
		return units;
	}

	private void setUnits(List<EntrainerProgramUnit> units) {
		this.units = units;
	}

}
