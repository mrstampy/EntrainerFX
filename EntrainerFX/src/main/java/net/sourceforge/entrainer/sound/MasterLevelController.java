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
package net.sourceforge.entrainer.sound;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class MasterLevelController.
 */
public class MasterLevelController {

	/** The lite. */
	public static boolean lite = false;

	private BigDecimal amplitude = BigDecimal.ZERO;
	private BigDecimal leftFrequency = BigDecimal.ZERO;
	private BigDecimal entrainmentFrequency = BigDecimal.ZERO;
	private BigDecimal pinkNoiseAmplitude = BigDecimal.ZERO;
	private BigDecimal pinkPanAmplitude = BigDecimal.ZERO;
	private BigDecimal pinkEntrainerMultiple = BigDecimal.ZERO;

	private final SoundControl soundControl;

	private Lock lock = new ReentrantLock(true);

	/**
	 * Instantiates a new master level controller.
	 *
	 * @param soundControl
	 *          the sound control
	 */
	public MasterLevelController(SoundControl soundControl) {
		this.soundControl = soundControl;
		init();
	}

	/**
	 * Instantiates a new master level controller.
	 */
	public MasterLevelController() {
		this(null);
	}

	/**
	 * Gets the amplitude.
	 *
	 * @return the amplitude
	 */
	public double getAmplitude() {
		return amplitude.doubleValue();
	}

	/**
	 * Gets the frequency.
	 *
	 * @return the frequency
	 */
	public double getFrequency() {
		return leftFrequency.doubleValue();
	}

	/**
	 * Gets the entrainment frequency.
	 *
	 * @return the entrainment frequency
	 */
	public double getEntrainmentFrequency() {
		double val = entrainmentFrequency.doubleValue();
		int precision = val < 10 ? 3 : 4;
		return entrainmentFrequency.round(new MathContext(precision)).doubleValue();
	}

	/**
	 * Gets the pink noise amplitude.
	 *
	 * @return the pink noise amplitude
	 */
	public double getPinkNoiseAmplitude() {
		return pinkNoiseAmplitude.doubleValue();
	}

	/**
	 * Gets the pink entrainer multiple.
	 *
	 * @return the pink entrainer multiple
	 */
	public double getPinkEntrainerMultiple() {
		return pinkEntrainerMultiple.doubleValue();
	}

	/**
	 * Gets the pink pan amplitude.
	 *
	 * @return the pink pan amplitude
	 */
	public double getPinkPanAmplitude() {
		return pinkPanAmplitude.doubleValue();
	}

	/**
	 * Clear mediator.
	 */
	public void clearMediator() {
		EntrainerMediator.getInstance().removeReceiver(this);
	}

	private void init() {
		EntrainerMediator.getInstance().addFirstReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				if(!process(e)) return;
				lock.lock();
				double delta;
				try {
					switch (e.getParm()) {
					case AMPLITUDE:
						amplitude = new BigDecimal(e.getDoubleValue());
						if (soundControl != null) soundControl.setAmplitude(amplitude.doubleValue());
						break;
					case FREQUENCY:
						leftFrequency = new BigDecimal(e.getDoubleValue());
						if (soundControl != null) {
							soundControl.setLeftFrequency(leftFrequency.doubleValue());
							soundControl.setRightFrequency(getRightFrequency().doubleValue());
						}
						break;
					case ENTRAINMENT_FREQUENCY:
						entrainmentFrequency = new BigDecimal(e.getDoubleValue());
						if (soundControl != null) soundControl.setRightFrequency(getRightFrequency().doubleValue());
						break;
					case PINK_NOISE_AMPLITUDE:
						pinkNoiseAmplitude = new BigDecimal(e.getDoubleValue());
						if (soundControl != null) soundControl.setPinkNoiseAmplitude(pinkNoiseAmplitude.doubleValue());
						break;
					case PINK_PAN_AMPLITUDE:
						pinkPanAmplitude = new BigDecimal(e.getDoubleValue());
						break;
					case PINK_PAN_VALUE:
						if (soundControl != null) soundControl.setPinkPanAmplitude(e.getDoubleValue());
						break;
					case PINK_ENTRAINER_MULTIPLE:
						pinkEntrainerMultiple = new BigDecimal(e.getDoubleValue());
						break;
					case DELTA_AMPLITUDE:
						delta = getDelta(e, amplitude.doubleValue(), e.getEndValue());
						amplitude = add(amplitude, delta);
						if (soundControl != null) soundControl.setAmplitude(amplitude.doubleValue());
						break;
					case DELTA_FREQUENCY:
						delta = getDelta(e, leftFrequency.doubleValue(), e.getEndValue());
						leftFrequency = add(leftFrequency, delta);
						if (soundControl != null) {
							soundControl.setLeftFrequency(leftFrequency.doubleValue());
							soundControl.setRightFrequency(getRightFrequency().doubleValue());
						}
						break;
					case DELTA_ENTRAINMENT_FREQUENCY:
						delta = getDelta(e, entrainmentFrequency.doubleValue(), e.getEndValue());
						entrainmentFrequency = add(entrainmentFrequency, delta);
						if (soundControl != null) soundControl.setRightFrequency(getRightFrequency().doubleValue());
						break;
					case DELTA_PINK_NOISE_AMPLITUDE:
						delta = getDelta(e, pinkNoiseAmplitude.doubleValue(), e.getEndValue());
						pinkNoiseAmplitude = add(pinkNoiseAmplitude, delta);
						if (soundControl != null) soundControl.setPinkNoiseAmplitude(pinkNoiseAmplitude.doubleValue());
						break;
					case DELTA_PINK_PAN_AMPLITUDE:
						delta = getDelta(e, pinkPanAmplitude.doubleValue(), e.getEndValue());
						pinkPanAmplitude = add(pinkPanAmplitude, delta);
						break;
					case DELTA_PINK_ENTRAINER_MULTIPLE:
						delta = getDelta(e, pinkEntrainerMultiple.doubleValue(), e.getEndValue());
						pinkEntrainerMultiple = add(pinkEntrainerMultiple, delta);
						break;
					default:
						break;

					}
				} finally {
					lock.unlock();
				}
			}
		});
	}
	
	private boolean process(ReceiverChangeEvent e) {
		switch (e.getParm()) {
		case AMPLITUDE:
		case FREQUENCY:
		case ENTRAINMENT_FREQUENCY:
		case PINK_NOISE_AMPLITUDE:
		case PINK_PAN_AMPLITUDE:
		case PINK_PAN_VALUE:
		case PINK_ENTRAINER_MULTIPLE:
		case DELTA_AMPLITUDE:
		case DELTA_FREQUENCY:
		case DELTA_ENTRAINMENT_FREQUENCY:
		case DELTA_PINK_NOISE_AMPLITUDE:
		case DELTA_PINK_PAN_AMPLITUDE:
		case DELTA_PINK_ENTRAINER_MULTIPLE:
			return true;
		default:
			return false;
		}

	}

	private BigDecimal getRightFrequency() {
		return leftFrequency.add(entrainmentFrequency);
	}

	private BigDecimal add(BigDecimal bd, double delta) {
		return bd.add(new BigDecimal(delta));
	}

}
