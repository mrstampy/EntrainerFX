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
package net.sourceforge.entrainer.media;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;
import net.sourceforge.entrainer.sound.MasterLevelController;

// TODO: Auto-generated Javadoc
/**
 * The Class MediaEngine.
 */
public class MediaEngine {
  private static final Logger log = LoggerFactory.getLogger(MediaEngine.class);

  private static MediaEngine mediaEngine = new MediaEngine();

  /**
   * Gets the single instance of MediaEngine.
   *
   * @return single instance of MediaEngine
   */
  public static MediaEngine getInstance() {
    return mediaEngine;
  }

  private Media media;
  private MediaPlayer player;

  private double amplitude;

  private boolean enableMediaEntrainment;
  private boolean loop;

  private boolean flip = false;

  private Lock lock = new ReentrantLock();

  private Sender sender = new SenderAdapter();

  private ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor();
  private ScheduledFuture<?> sf;

  private ExecutorService pulseSvc = Executors.newSingleThreadExecutor();

  private double frailty;

  private MasterLevelController controller = new MasterLevelController();

  /**
   * Instantiates a new media engine.
   */
  private MediaEngine() {
    initMediator();
  }

  private void initMediator() {
    EntrainerMediator.getInstance().addSender(sender);
    EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this, true) {

      @Override
      protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
        switch (e.getParm()) {
        case MEDIA_AMPLITUDE:
        case DELTA_MEDIA_AMPLITUDE:
          amplitude = controller.getMediaVolume();
          setPlayerVolume(amplitude);
          break;
        case MEDIA_ENTRAINMENT_STRENGTH:
        case DELTA_MEDIA_ENTRAINMENT_STRENGTH:
          setEntrainmentAmplitude(controller.getMediaEntrainmentStrength());
          break;
        case MEDIA_ENTRAINMENT:
          entrainmentEnabled(e.getBooleanValue());
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
          svc.execute(() -> setUri(e.getStringValue()));
          break;
        case ENTRAINMENT_FREQUENCY_PULSE:
          pulseSvc.execute(() -> entrain(e.getBooleanValue()));
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

  private void setPlayerVolume(double d) {
    if (player == null) return;

    player.setVolume(d);
  }

  private void entrainmentEnabled(boolean b) {
    enableMediaEntrainment = b;
    if (!b) setPlayerVolume(amplitude);
  }

  private void setPlayerTime(double d) {
    if (player == null) return;

    player.seek(player.getTotalDuration().subtract(Duration.seconds(d)));
  }

  private void setEntrainmentAmplitude(double strength) {
    frailty = 1 - strength;
  }

  /**
   * Entrain.
   *
   * @param b
   *          the b
   */
  protected void entrain(boolean b) {
    if (!b) {
      setPlayerVolume(amplitude);
      return;
    }

    if (!enableMediaEntrainment) return;

    lock.lock();
    try {
      setPlayerVolume(flip ? amplitude : frailty * amplitude);
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
      if (player != null) player.dispose();
      player = new MediaPlayer(media);
      player.statusProperty().addListener(new ChangeListener<Status>() {

        @Override
        public void changed(ObservableValue<? extends Status> observable, Status oldValue, Status newValue) {
          notifyPlayTime(newValue);
        }
      });
    } catch (Exception e) {
      log.warn("URI {} is invalid", uri, e);
      media = null;
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
    if (stillPlaying() || player == null) return;

    setPlayerVolume(amplitude);

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
    switch (newValue) {
    case READY:
    case STOPPED:
    case HALTED:
      break;
    default:
      return;
    }

    Duration d = media.getDuration();
    if (d == Duration.UNKNOWN || d == Duration.INDEFINITE) return;

    double seconds = d.toSeconds();

    sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, seconds, MediatorConstants.MEDIA_TIME));

    player.seek(player.getStartTime());
  }

  private boolean stillPlaying() {
    if (player == null || player.getStatus() == null) return false;
    switch (player.getStatus()) {
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

  /**
   * Gets the media.
   *
   * @return the media
   */
  public Media getMedia() {
    return media;
  }

  /**
   * Gets the player.
   *
   * @return the player
   */
  public MediaPlayer getPlayer() {
    return player;
  }

  /**
   * Gets the amplitude.
   *
   * @return the amplitude
   */
  public double getAmplitude() {
    return amplitude;
  }

  /**
   * Checks if is enable media entrainment.
   *
   * @return true, if is enable media entrainment
   */
  public boolean isEnableMediaEntrainment() {
    return enableMediaEntrainment;
  }

  /**
   * Checks if is loop.
   *
   * @return true, if is loop
   */
  public boolean isLoop() {
    return loop;
  }

  /**
   * Checks if is flip.
   *
   * @return true, if is flip
   */
  public boolean isFlip() {
    return flip;
  }

}
