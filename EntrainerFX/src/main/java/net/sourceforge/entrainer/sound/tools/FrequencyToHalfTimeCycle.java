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
package net.sourceforge.entrainer.sound.tools;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;

// TODO: Auto-generated Javadoc
/**
 * Class that accepts the frequency and converts it to half a cycle of time.
 * 
 * ie. a frequency of 5Hz is converted to 100 ms.
 * 
 * @author burton
 *
 */
public class FrequencyToHalfTimeCycle {

	private static final BigDecimal oneHalfSecond = new BigDecimal(500, MathContext.DECIMAL64);
	private static final BigDecimal lessThanZero = new BigDecimal(0.01, MathContext.DECIMAL64);
	private static final BigDecimal oneMillion = new BigDecimal(1000000);
	private static final MathContext scaleSix = new MathContext(6, RoundingMode.HALF_UP);

	private long millis;
	private int nanos;
	private double frequency;

	/**
	 * Instantiates a new frequency to half time cycle.
	 */
	public FrequencyToHalfTimeCycle() {
		initMediator();
	}

	/**
	 * Sets the frequency.
	 *
	 * @param frequency
	 *          the new frequency
	 */
	public void setFrequency(double frequency) {
		this.frequency = frequency;

		BigDecimal denominator = frequency > 0 ? new BigDecimal(frequency, MathContext.DECIMAL64) : lessThanZero;

		BigDecimal result = oneHalfSecond.divide(denominator, MathContext.DECIMAL64);

		setMillis(result.longValue());

		BigDecimal nanos = result.subtract(new BigDecimal(getMillis()), MathContext.DECIMAL64);
		nanos = nanos.multiply(oneMillion).round(scaleSix);

		setNanos(getNanoValue(nanos));
	}

	/**
	 * Cleanup.
	 */
	public void cleanup() {
		EntrainerMediator.getInstance().removeReceiver(this);
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case ENTRAINMENT_FREQUENCY:
					setFrequency(e.getDoubleValue());
					break;
				case DELTA_ENTRAINMENT_FREQUENCY:
					double delta = getDelta(e, getFrequency(), e.getEndValue());
					setFrequency(getFrequency() + delta);
					break;
				default:
					break;
				}
			}

		});
	}

	private int getNanoValue(BigDecimal nanos) {
		int val = nanos.intValue();

		return val < 0 ? 0 : val;
	}

	/**
	 * Gets the millis.
	 *
	 * @return the millis
	 */
	public long getMillis() {
		return millis;
	}

	private void setMillis(long millis) {
		this.millis = millis;
	}

	/**
	 * Gets the nanos.
	 *
	 * @return the nanos
	 */
	public int getNanos() {
		return nanos;
	}

	private void setNanos(int nanos) {
		if (nanos < 0 || nanos > 999999) nanos = 0;
		this.nanos = nanos;
	}

	/**
	 * Gets the frequency.
	 *
	 * @return the frequency
	 */
	public double getFrequency() {
		return frequency;
	}

}
