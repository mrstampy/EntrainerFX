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

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.sound.tools.FrequencyToHalfTimeCycle;
import net.sourceforge.entrainer.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class BackgroundFlasher.
 */
public class BackgroundFlasher {

	private FrequencyToHalfTimeCycle calculator = new FrequencyToHalfTimeCycle();

	private ImageView background;

	private volatile boolean running;
	
	private volatile boolean flashBackground;

	/**
	 * Instantiates a new background flasher.
	 *
	 * @param background the background
	 */
	public BackgroundFlasher(ImageView background) {
		this.background = background;
		initMediator();
	}

	private void startTransition() {
		Thread thread = new Thread("Background Image Flashing Thread") {
			public void run() {
				while (shouldRun()) {
					Utils.snooze(getMillis(), calculator.getNanos());

					invert();
				}

				reset();
			}

			private long getMillis() {
				long millis = calculator.getMillis();
				return millis > 5000 ? 5000l : millis;
			}
		};

		thread.setDaemon(true);

		thread.start();
	}

	private void invert() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				double o = background.getOpacity() == 0.25 ? 0.5 : 0.25;
				background.setOpacity(o);
			}
		});
	}

	private void reset() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				background.setOpacity(0.25);
			}
		});
	}
	
	private boolean shouldRun() {
		return running && flashBackground;
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case FLASH_BACKGROUND:
					flashBackground = e.getBooleanValue();
					
					startCheck();
					break;
				case START_ENTRAINMENT:
					running = e.getBooleanValue();

					startCheck();
					break;
				default:
					break;
				}
			}
			
			private void startCheck() {
				if (shouldRun()) startTransition();
			}

		});
	}
	
	public void dispose() {
		EntrainerMediator.getInstance().removeReceiver(this);
	}

}
