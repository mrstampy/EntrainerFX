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
package net.sourceforge.entrainer.sound.tools;

import java.math.BigDecimal;
import java.math.MathContext;

import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;
import net.sourceforge.entrainer.sound.MasterLevelController;
import net.sourceforge.entrainer.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * Utility class to calculate panning values.
 * 
 * @author burton
 */
public class Panner {

	private static final long PAN_PINK_DELAY = 50;
	private static final int FORWARD = 0;
	private static final int BACKWARD = 1;

	private double value = 0.5;

	private int direction = FORWARD;

	private long milliSleep;

	private BigDecimal minusOne = new BigDecimal(-1);

	private boolean isPanning = false;

	private boolean isPan;

	private Sender sender = new SenderAdapter();

	private static Panner instance;

	private MasterLevelController masterLevelController = new MasterLevelController();

	/**
	 * Gets the single instance of Panner.
	 *
	 * @return single instance of Panner
	 */
	public static Panner getInstance() {
		if (instance == null) instance = new Panner();

		return instance;
	}

	private Panner() {
		super();
		initMediator();
		setMilliSleep(PAN_PINK_DELAY);
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addSender(sender);
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case PINK_PAN:
					setPan(e.getBooleanValue());
					break;
				case START_ENTRAINMENT:
					if (e.getBooleanValue()) {
						if (!isPanning) {
							panPinkNoise();
						}
					} else {
						isPanning = false;
					}
					break;
				default:
					break;
				}
			}

		});
	}

	/**
	 * Clear mediator objects.
	 */
	public void clearMediatorObjects() {
		EntrainerMediator.getInstance().removeReceiver(this);
		EntrainerMediator.getInstance().removeSender(sender);
	}

	private void panPinkNoise() {
		isPanning = true;
		Thread t = new Thread() {
			public void run() {
				while (isPanning) {
					Utils.snooze(PAN_PINK_DELAY);
					if (isPan()) {
						calculatePinkPan();
					} else if (getValue() != 0.5) {
						reset();
					}
				}
				reset();
				isPanning = false;
			}
		};

		t.start();
	}

	private void reset() {
		setValue(0.5);
		firePinkNoisePanValueChangeEvent(getValue());
	}

	private void calculatePinkPan() {
		calculateValue();
		firePinkNoisePanValueChangeEvent(getValue());
	}

	private void firePinkNoisePanValueChangeEvent(double value) {
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, value, MediatorConstants.PINK_PAN_VALUE);
		sender.fireReceiverChangeEvent(e);
	}

	private void calculateValue() {
		BigDecimal entrainment = new BigDecimal(masterLevelController.getEntrainmentFrequency(), MathContext.DECIMAL64)
				.divide(new BigDecimal(masterLevelController.getPinkEntrainerMultiple(), MathContext.DECIMAL64),
						MathContext.DECIMAL64);

		BigDecimal time = new BigDecimal(getMilliSleep(), MathContext.DECIMAL64).divide(new BigDecimal(1000),
				MathContext.DECIMAL64);

		BigDecimal fraction = entrainment.multiply(time, MathContext.DECIMAL64);

		BigDecimal delta = new BigDecimal(masterLevelController.getPinkPanAmplitude(), MathContext.DECIMAL64)
				.multiply(fraction);

		double maxMinusDelta = getMaxAmplitude() - delta.doubleValue();
		double minPlusDelta = getMinAmplitude() + delta.doubleValue();

		if (isForward() && getValue() >= maxMinusDelta) {
			setDirection(BACKWARD);
		}

		if (isBackward()) {
			delta = delta.multiply(minusOne);
			if (getValue() <= minPlusDelta) {
				setDirection(FORWARD);
			}
		}

		double calc = getValue() + delta.doubleValue();

		if (!isOutOfRange(calc)) {
			setValue(calc);
		} else {
			setValue(calc >= getMaxAmplitude() ? maxMinusDelta : minPlusDelta);
		}
	}

	private boolean isOutOfRange(double calc) {
		return calc >= getMaxAmplitude() || calc <= getMinAmplitude();
	}

	private double getMinAmplitude() {
		return 0.5 - (masterLevelController.getPinkPanAmplitude() / 2);
	}

	private double getMaxAmplitude() {
		return (masterLevelController.getPinkPanAmplitude() / 2) + 0.5;
	}

	private boolean isForward() {
		return direction == FORWARD;
	}

	private boolean isBackward() {
		return direction == BACKWARD;
	}

	private void setDirection(int direction) {
		this.direction = direction;
	}

	private long getMilliSleep() {
		return milliSleep;
	}

	private void setMilliSleep(long milliSleep) {
		this.milliSleep = milliSleep;
	}

	private double getValue() {
		return value;
	}

	private void setValue(double value) {
		this.value = value;
	}

	private boolean isPan() {
		return isPan;
	}

	private void setPan(boolean isPan) {
		this.isPan = isPan;
	}

}
