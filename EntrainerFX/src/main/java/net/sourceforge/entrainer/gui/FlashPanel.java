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

import java.util.Calendar;
import java.util.Random;

import javafx.animation.FillTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import javax.swing.SwingUtilities;

import net.sourceforge.entrainer.gui.jfx.JFXUtils;
import net.sourceforge.entrainer.gui.popup.NotificationWindow;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.sound.tools.FrequencyToHalfTimeCycle;
import net.sourceforge.entrainer.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * This class allows messages to be displayed and contains the flashing
 * functionality.
 * 
 * @author burton
 */
public class FlashPanel extends Rectangle {

	/** The Constant CSS_ID. */
	public static final String CSS_ID = "message-panel";
	private boolean canFlash = false;
	private boolean isStarted = false;
	private Color antiFlashColor = new Color(1, 1, 168.0 / 255.0, 0.25);
	private Color transparent = new Color(1, 1, 168.0 / 255.0, 0.0);
	// private Color transparent = new Color(1, 1, 168.0 / 255.0, 0);
	private boolean isFlash;
	private boolean isPsychedelic;
	private Color flashColor;
	private Color singleColor;
	private Random rand = new Random(Calendar.getInstance().getTimeInMillis());
	private volatile boolean isRunning = false;

	private FrequencyToHalfTimeCycle calculator = new FrequencyToHalfTimeCycle();

	private Runnable flashRunner;
	private volatile boolean timelineStarting;
	private volatile boolean flashEnabled;
	
	private FillTransition fill;

	/**
	 * Instantiates a new flash panel.
	 */
	public FlashPanel() {
		super(0, 0, 650, 70);
		init();
	}

	private void init() {
		setId(CSS_ID);
		flashRunner = new Runnable() {

			@Override
			public void run() {
				setFill(antiFlashColor.equals(getFill()) ? flashColor : antiFlashColor);
			}
		};
		initMediator();
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				setFill(transparent);
			}
		});
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case IS_FLASH:
					if (isFlash == e.getBooleanValue()) return;
					setFlash(e.getBooleanValue());
					checkStart();
					break;
				case IS_PSYCHEDELIC:
					if (isPsychedelic == e.getBooleanValue()) return;
					if (e.getBooleanValue()) {
						singleColor = flashColor;
					} else {
						flashColor = singleColor;
					}
					setPsychedelic(e.getBooleanValue());
					break;
				case FLASH_COLOUR:
					setFlashColor(e.getColourValue());
					singleColor = flashColor;
					setAntiflashColour();
					break;
				case START_FLASHING:
					setCanFlash(e.getBooleanValue());
					checkStart();
					break;
				case START_ENTRAINMENT:
					setStarted(e.getBooleanValue());
					checkStart();
					break;
				case MESSAGE:
					setMessage(e.getStringValue());
					break;
				default:
					break;
				}
			}

		});
	}

	private synchronized void checkStart() {
		if (canRunFlashingThread() && !isRunning) {
			isRunning = true;
			if (!timelineStarting) startColourFlashingThread();
		}
	}

	private boolean canRunFlashingThread() {
		return canFlash() && isStarted();
	}

	/**
	 * Clear mediator objects.
	 */
	public void clearMediatorObjects() {
		EntrainerMediator.getInstance().removeReceiver(this);
	}

	private void setMessage(final String msg) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new NotificationWindow(msg, EntrainerFX.getInstance());
			}
		});
	}

	private boolean canFlash() {
		return canFlash && isFlash();
	}

	private void setCanFlash(boolean canFlash) {
		this.canFlash = canFlash;
	}

	private void startColourFlashingThread() {
		Thread t = new Thread("Message Panel Flashing Thread") {
			public void run() {
				setPriority(Thread.MAX_PRIORITY);
				while (canRunFlashingThread()) {
					long l = getMillis() > 5000 ? 5000 : getMillis();
					Utils.snooze(l, getNanos());

					changeBackground();
					if (isPsychedelic()) {
						flashColor = generateRandomColour();
					}
				}
				isRunning = false;
				resetBackground();
				timelineStarting = false;
			}
		};

		t.start();
	}

	private Color generateRandomColour() {
		double r = rand.nextDouble();
		double g = rand.nextDouble();
		double b = rand.nextDouble();

		return new Color(r, g, b, 1);
	}

	private void enableFlash(boolean enable) {
		if (!isVisible()) return;

		this.flashEnabled = enable;

		timelineStarting = isStarted() && enable;

		if (enable && antiFlashColor.equals(getFill())) return;
		if (!enable && transparent.equals(getFill())) return;

		if (fill != null) {
			fill.stop();
			fill = null;
		}
		startNewFillTimeline();
	}

	private void startNewFillTimeline() {
		Color to = flashEnabled ? antiFlashColor : transparent;
		
		fill = new FillTransition(Duration.seconds(2), this);
		fill.setToValue(to);
		fill.setOnFinished(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				if(timelineStarting) {
					startColourFlashingThread();
				}
			}
		});

		fill.play();
	}

	private void changeBackground() {
		Platform.runLater(flashRunner);
	}

	/**
	 * Reset background.
	 */
	public void resetBackground() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				setFill(antiFlashColor);
			}
		});
	}

	/**
	 * Gets the millis.
	 *
	 * @return the millis
	 */
	public long getMillis() {
		return calculator.getMillis();
	}

	/**
	 * Gets the nanos.
	 *
	 * @return the nanos
	 */
	public int getNanos() {
		return calculator.getNanos();
	}

	/**
	 * Checks if is flash.
	 *
	 * @return true, if is flash
	 */
	public boolean isFlash() {
		return isFlash;
	}

	/**
	 * Sets the flash.
	 *
	 * @param isFlash the new flash
	 */
	public void setFlash(boolean isFlash) {
		this.isFlash = isFlash;
		enableFlash(isFlash);
	}

	/**
	 * Checks if is psychedelic.
	 *
	 * @return true, if is psychedelic
	 */
	public boolean isPsychedelic() {
		return isPsychedelic;
	}

	/**
	 * Sets the psychedelic.
	 *
	 * @param isPsychedelic the new psychedelic
	 */
	public void setPsychedelic(boolean isPsychedelic) {
		this.isPsychedelic = isPsychedelic;
	}

	/**
	 * Gets the flash color.
	 *
	 * @return the flash color
	 */
	public java.awt.Color getFlashColor() {
		return JFXUtils.fromJFXColor(flashColor);
	}

	/**
	 * Sets the flash color.
	 *
	 * @param flashColor the new flash color
	 */
	public void setFlashColor(java.awt.Color flashColor) {
		this.flashColor = JFXUtils.toJFXColor(flashColor);
	}

	/**
	 * Checks if is started.
	 *
	 * @return true, if is started
	 */
	public boolean isStarted() {
		return isStarted;
	}

	/**
	 * Sets the started.
	 *
	 * @param isStarted the new started
	 */
	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	/**
	 * Gets the entrainment frequency.
	 *
	 * @return the entrainment frequency
	 */
	public double getEntrainmentFrequency() {
		return calculator.getFrequency();
	}

	private void setAntiflashColour() {
		antiFlashColor = flashColor.invert();
		antiFlashColor = new Color(antiFlashColor.getRed(), antiFlashColor.getGreen(), antiFlashColor.getBlue(), 0.25);
		if (isFlash() && !isRunning) enableFlash(true);
	}

}
