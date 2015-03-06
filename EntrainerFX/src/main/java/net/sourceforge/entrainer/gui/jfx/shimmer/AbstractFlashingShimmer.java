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
package net.sourceforge.entrainer.gui.jfx.shimmer;

import javafx.scene.paint.Paint;
import net.sourceforge.entrainer.gui.jfx.JFXUtils;
import net.sourceforge.entrainer.sound.tools.FrequencyToHalfTimeCycle;
import net.sourceforge.entrainer.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractFlashingShimmer.
 *
 * @param <P>
 *          the generic type
 */
public abstract class AbstractFlashingShimmer<P extends Paint> extends AbstractShimmer<P> {

	private FrequencyToHalfTimeCycle calculator = new FrequencyToHalfTimeCycle();

	private boolean runFlash = false;

	/**
	 * Instantiates a new abstract flashing shimmer.
	 */
	protected AbstractFlashingShimmer() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.gui.jfx.shimmer.AbstractShimmer#start()
	 */
	public void start() {
		super.start();
		startOpacityFlashingThread();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.gui.jfx.shimmer.AbstractShimmer#stop()
	 */
	public void stop() {
		super.stop();
		runFlash = false;
	}

	private void startOpacityFlashingThread() {
		runFlash = true;
		Thread t = new Thread("Shimmer Flashing Thread") {
			public void run() {
				setPriority(Thread.MAX_PRIORITY);
				double o = getOpacity();
				double half = o / 2;
				while (runFlash) {
					long l = getMillis() > 5000 ? 5000 : getMillis();
					Utils.snooze(l, getNanos());

					setShimmerOpacity(getOpacity() == o ? half : o);
				}
				setShimmerOpacity(o);
			}
		};

		t.start();
	}

	private void setShimmerOpacity(final double o) {
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				setOpacity(o);
			}
		});
	}

	/**
	 * Gets the millis.
	 *
	 * @return the millis
	 */
	protected long getMillis() {
		return calculator.getMillis();
	}

	/**
	 * Gets the nanos.
	 *
	 * @return the nanos
	 */
	protected int getNanos() {
		return calculator.getNanos();
	}

	/**
	 * Gets the entrainment frequency.
	 *
	 * @return the entrainment frequency
	 */
	protected double getEntrainmentFrequency() {
		return calculator.getFrequency();
	}

}
