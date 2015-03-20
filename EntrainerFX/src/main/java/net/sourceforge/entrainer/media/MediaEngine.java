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
package net.sourceforge.entrainer.media;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;

// TODO: Auto-generated Javadoc
/**
 * The Class MediaEngine.
 */
public class MediaEngine {

	private Media media;
	private MediaPlayer player;

	private double amplitude;
	private double entrainmentAmplitude;

	private boolean enableMediaEntrainment;
	private boolean loop;

	private boolean flip = false;

	private Lock lock = new ReentrantLock();

	private Sender sender = new SenderAdapter();

	/**
	 * Instantiates a new media engine.
	 */
	public MediaEngine() {
		initMediator();
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addSender(sender);
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case MEDIA_AMPLITUDE:
					amplitude = e.getDoubleValue();
					reset();
					break;
				case MEDIA_ENTRAINMENT_STRENGTH:
					setEntrainmentAmplitude(e.getDoubleValue());
					break;
				case MEDIA_ENTRAINMENT:
					enableMediaEntrainment = e.getBooleanValue();
					evalPlayer();
					break;
				case MEDIA_LOOP:
					loop = e.getBooleanValue();
					break;
				case MEDIA_PAUSE:
					pause(e.getBooleanValue());
					break;
				case MEDIA_PLAY:
					play(e.getBooleanValue());
					break;
				case MEDIA_URI:
					setUri(e.getStringValue());
					break;
				case ENTRAINMENT_FREQUENCY_PULSE:
					entrain();
					break;
				case START_ENTRAINMENT:
					if (!e.getBooleanValue()) reset();
					break;
				default:
					break;
				}
			}
		});
	}

	private void reset() {
		if (player == null) return;

		player.setVolume(amplitude);
	}

	private void evalPlayer() {
		lock.lock();
		try {
			if (!enableMediaEntrainment) reset();
		} finally {
			lock.unlock();
		}
	}

	private void setEntrainmentAmplitude(double strength) {
		double frailty = 1 - strength;
		entrainmentAmplitude = frailty * amplitude;
	}

	/**
	 * Entrain.
	 */
	protected void entrain() {
		if (!enableMediaEntrainment) return;

		lock.lock();
		try {
			if (player == null) return;
			player.setVolume(flip ? amplitude : entrainmentAmplitude);
			flip = !flip;
		} finally {
			lock.unlock();
		}
	}

	private void setUri(String uri) {
		if (uri == null || uri.isEmpty()) return;
		try {
			media = new Media(uri);
		} catch (Exception e) {
			GuiUtil.handleProblem(e);
		}
	}

	private void play(boolean b) {
		if (b) {
			startPlayer();
		} else {
			lock.lock();
			try {
				player.stop();
				player.dispose();
				player = null;
			} finally {
				lock.unlock();
			}
		}
	}

	private void startPlayer() {
		player = new MediaPlayer(media);
		player.setVolume(amplitude);

		player.setOnEndOfMedia(() -> evalLoop());
		player.play();
	}

	private void evalLoop() {
		if (loop) {
			restart();
		} else {
			sendStop();
		}
	}

	private void sendStop() {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, false, MediatorConstants.MEDIA_PLAY));
	}

	private void restart() {
		lock.lock();
		try {
			player.dispose();
			play(true);
		} finally {
			lock.unlock();
		}
	}

	private void pause(boolean b) {
		if (b) {
			player.pause();
		} else {
			player.play();
		}
	}

}
