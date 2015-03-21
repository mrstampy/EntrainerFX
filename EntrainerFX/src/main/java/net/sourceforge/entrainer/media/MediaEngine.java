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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
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

	private ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> sf;

	/**
	 * Instantiates a new media engine.
	 */
	public MediaEngine() {
		initMediator();
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addSender(sender);
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this, true) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case MEDIA_AMPLITUDE:
					amplitude = e.getDoubleValue();
					if(player != null) player.setVolume(amplitude);
					break;
				case MEDIA_ENTRAINMENT_STRENGTH:
					setEntrainmentAmplitude(e.getDoubleValue());
					break;
				case MEDIA_ENTRAINMENT:
					enableMediaEntrainment = e.getBooleanValue();
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
					entrain(e.getBooleanValue());
					break;
				case MEDIA_TIME:
					setPlayerTime(e.getDoubleValue());
					break;
				default:
					break;
				}
			}
		});
	}

	private void setPlayerTime(double d) {
		if(player == null) return;
		
		player.seek(player.getTotalDuration().subtract(Duration.seconds(d)));
	}

	private void setEntrainmentAmplitude(double strength) {
		double frailty = 1 - strength;
		entrainmentAmplitude = frailty * amplitude;
	}

	/**
	 * Entrain.
	 *
	 * @param b
	 *          the b
	 */
	protected void entrain(boolean b) {
		if (!enableMediaEntrainment || !b) return;

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
		if (media != null && media.getSource().equals(uri)) return;
		
		try {
			media = new Media(uri);
			if(player != null) player.dispose();
			player = new MediaPlayer(media);
			player.statusProperty().addListener(new ChangeListener<Status>() {

				@Override
				public void changed(ObservableValue<? extends Status> observable, Status oldValue, Status newValue) {
					notifyPlayTime(newValue);
				}
			});
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
				player.seek(player.getStartTime());
				if (sf != null) sf.cancel(true);
			} finally {
				lock.unlock();
			}
		}
	}

	private void startPlayer() {
		if (stillPlaying()) return;

		player.setVolume(amplitude);

		player.setOnEndOfMedia(() -> evalLoop());
		player.play();

		startMediaTimeThread();
	}

	private void startMediaTimeThread() {
		sf = svc.scheduleAtFixedRate(() -> fireTimeRemaining(), 1, 1, TimeUnit.SECONDS);
	}

	private void fireTimeRemaining() {
		if (!stillPlaying()) {
			sf.cancel(true);
			return;
		}

		double length = media.getDuration().toSeconds();

		double currentPos = player.getCurrentTime().toSeconds();

		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, length - currentPos, MediatorConstants.MEDIA_TIME));
	}

	private void notifyPlayTime(Status newValue) {
		switch(newValue) {
		case READY:
		case STOPPED:
		case HALTED:
			break;
		default:
			return;
		}
				
		Duration d = media.getDuration();
		if(d == Duration.UNKNOWN || d == Duration.INDEFINITE) return;
		
		double seconds = d.toSeconds();
		
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, seconds, MediatorConstants.MEDIA_TIME));
		
		player.seek(player.getStartTime());
	}

	private boolean stillPlaying() {
		if(player == null || player.getStatus() == null) return false;
		switch(player.getStatus()) {
		case PLAYING:
		case PAUSED:
			return true;
		default:
			return false;
		}
	}

	private void evalLoop() {
		if (loop) {
			player.seek(player.getStartTime());
			play(true);
		} else {
			sendStop();
		}
	}

	private void sendStop() {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, false, MediatorConstants.MEDIA_PLAY));
		if (sf != null) sf.cancel(true);
	}

	private void pause(boolean b) {
		if (b) {
			player.pause();
		} else {
			player.play();
		}
	}

	public Media getMedia() {
		return media;
	}

	public MediaPlayer getPlayer() {
		return player;
	}

	public double getAmplitude() {
		return amplitude;
	}

	public double getEntrainmentAmplitude() {
		return entrainmentAmplitude;
	}

	public boolean isEnableMediaEntrainment() {
		return enableMediaEntrainment;
	}

	public boolean isLoop() {
		return loop;
	}

	public boolean isFlip() {
		return flip;
	}

	public Lock getLock() {
		return lock;
	}

	public Sender getSender() {
		return sender;
	}

	public ScheduledExecutorService getSvc() {
		return svc;
	}

	public ScheduledFuture<?> getSf() {
		return sf;
	}

}
