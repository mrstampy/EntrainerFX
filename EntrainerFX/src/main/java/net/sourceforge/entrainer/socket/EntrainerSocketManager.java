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
package net.sourceforge.entrainer.socket;

import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.sourceforge.entrainer.gui.flash.FlashType;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.xml.Settings;

// TODO: Auto-generated Javadoc
/**
 * The {@link EntrainerSocketManager} opens a socket for the purpose of sending
 * and receiving {@link EntrainerStateMessage}s to and from the socket's
 * client(s). This allows external programs and devices to respond to Entrainer
 * state changes (think: a strobe light flashing at the same entrainment
 * frequency as Entrainer) and said programs and devices to control Entrainer's
 * state. Clients will receive messages that represent either a delta change
 * notification of the property which has changed state or a full current state
 * message, dependent upon Entrainer's {@link Settings#isDeltaSocketMessage()}
 * setting.
 * 
 * @author burton
 */
public class EntrainerSocketManager {
  private static final Logger log = Logger.getLogger(EntrainerSocketManager.class);

  private ServerBootstrap bootstrap;
  private Channel channel;

  private volatile EntrainerStateMessage currentState = new EntrainerStateMessage();

  private Executor executor = Executors.newCachedThreadPool();

  private NettyConnectionHandler nettyConnectionHandler = new NettyConnectionHandler(currentState);
  private WebSocketHandler webSocketHandler = new WebSocketHandler(currentState);

  /**
   * Instantiates a new entrainer socket manager.
   */
  public EntrainerSocketManager() {
    init();
  }

  /**
   * Binds the socket to the port specified in {@link Settings}.
   *
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws InvalidPortNumberException
   *           if the port number <= 0.
   */
  public void bind() throws IOException, InvalidPortNumberException {
    if (isBound()) return;

    String ipAddress = Settings.getInstance().getSocketIPAddress();
    if (ipAddress == null || ipAddress.trim().length() == 0) initIPAddress();

    ipAddress = Settings.getInstance().getSocketIPAddress();

    if (bootstrap == null) initAcceptor();

    int port = Settings.getInstance().getSocketPort();
    if (port <= 0) throw new InvalidPortNumberException(port);

    ChannelFuture cf = bootstrap.bind(ipAddress, port).syncUninterruptibly();
    if (cf.isSuccess()) {
      channel = cf.channel();
    } else {
      throw new RuntimeException("Could not bind to host " + ipAddress + " and port " + port, cf.cause());
    }
  }

  private void initIPAddress() throws UnknownHostException {
    Settings.getInstance().setSocketIPAddress(InetAddress.getLocalHost().getHostAddress());
  }

  /**
   * Unbinds the acceptor from the socket and kicks off any clients connected.
   */
  public void unbind() {
    if (!isBound()) return;

    nettyConnectionHandler.disconnectAll();
    webSocketHandler.disconnectAll();

    channel.close();

    channel = null;
  }

  /**
   * Returns true if the acceptor is bound to the socket.
   *
   * @return true, if is bound
   */
  public boolean isBound() {
    return bootstrap == null || channel == null ? false : channel.isActive();
  }

  /**
   * Returns the port on which the acceptor is bound, or -1 if it is not bound.
   *
   * @return the port number
   */
  public int getPortNumber() {
    if (!isBound()) return -1;
    InetSocketAddress addr = (InetSocketAddress) channel.localAddress();
    return addr == null ? -1 : addr.getPort();
  }

  /**
   * Gets the host name.
   *
   * @return the host name
   */
  public String getHostName() {
    if (!isBound()) return null;
    InetSocketAddress addr = (InetSocketAddress) channel.localAddress();
    return addr == null ? "n/a" : addr.getHostName();
  }

  // Broadcasts the message to all connected clients.
  private void broadcast(EntrainerStateMessage message) {
    try {
      nettyConnectionHandler.broadcast(message);
      webSocketHandler.broadcast(message);
    } catch (Throwable e) {
      log.error("Cannot send message", e);
    }
  }

  private void initAcceptor() {
    bootstrap = new ServerBootstrap();
    bootstrap.option(ChannelOption.SO_REUSEADDR, Boolean.TRUE);
    EventLoopGroup parent = new NioEventLoopGroup();
    EventLoopGroup worker = new NioEventLoopGroup();

    bootstrap.group(parent, worker);
    bootstrap.channel(NioServerSocketChannel.class);
    bootstrap.childHandler(new ChannelInitializer<Channel>() {

      @Override
      protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new PortUnificationHandler(nettyConnectionHandler, webSocketHandler));
      }
    });
  }

  // Mediator receiver initialization, listening for state changes in the
  // Entrainer program. Connected clients are notified of the state changes.
  private void init() {
    EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

      @Override
      protected void processReceiverChangeEvent(final ReceiverChangeEvent e) {
        Runnable processor = new Runnable() {
          final ReceiverChangeEvent rce = e;

          @Override
          public void run() {
            processMessageImpl(rce);
          }
        };

        executor.execute(processor);
      }

      private void processMessageImpl(ReceiverChangeEvent e) {
        EntrainerStateMessage message = new EntrainerStateMessage();
        double delta;
        boolean processing = true;

        switch (e.getParm()) {

        case AMPLITUDE:
          processing = currentState.getAmplitude() == null || e.getDoubleValue() != currentState.getAmplitude();
          if (!processing) break;

          message.setAmplitude(e.getDoubleValue());
          currentState.setAmplitude(message.getAmplitude());

          break;
        case DELTA_AMPLITUDE:
          delta = getDelta(e, currentState.getAmplitude(), e.getEndValue());
          message.setAmplitude(currentState.getAmplitude() + delta);
          message.setDeltaAmplitude(e.getDoubleValue());

          currentState.setAmplitude(message.getAmplitude());
          currentState.setDeltaAmplitude(message.getDeltaAmplitude());

          break;
        case DELTA_ENTRAINMENT_FREQUENCY:
          delta = getDelta(e, currentState.getEntrainmentFrequency(), e.getEndValue());
          message.setEntrainmentFrequency(currentState.getEntrainmentFrequency() + delta);
          message.setDeltaEntrainmentFrequency(e.getDoubleValue());

          currentState.setEntrainmentFrequency(message.getEntrainmentFrequency());
          currentState.setDeltaEntrainmentFrequency(message.getDeltaEntrainmentFrequency());

          break;
        case DELTA_FREQUENCY:
          delta = getDelta(e, currentState.getFrequency(), e.getEndValue());
          message.setFrequency(currentState.getFrequency() + delta);
          message.setDeltaFrequency(e.getDoubleValue());

          currentState.setFrequency(message.getFrequency());
          currentState.setDeltaFrequency(message.getDeltaFrequency());

          break;
        case DELTA_PINK_ENTRAINER_MULTIPLE:
          delta = getDelta(e, currentState.getPinkNoiseMultiple(), e.getEndValue());
          message.setPinkNoiseMultiple(currentState.getPinkNoiseMultiple() + delta);
          message.setDeltaPinkEntrainerMultiple(e.getDoubleValue());

          currentState.setPinkNoiseMultiple(message.getPinkNoiseMultiple());
          currentState.setDeltaPinkEntrainerMultiple(message.getDeltaPinkEntrainerMultiple());

          break;
        case DELTA_PINK_NOISE_AMPLITUDE:
          delta = getDelta(e, currentState.getPinkNoiseAmplitude(), e.getEndValue());
          message.setPinkNoiseAmplitude(currentState.getPinkNoiseAmplitude() + delta);
          message.setDeltaPinkNoiseAmplitude(e.getDoubleValue());

          currentState.setPinkNoiseAmplitude(message.getPinkNoiseAmplitude());
          currentState.setDeltaPinkNoiseAmplitude(message.getDeltaPinkNoiseAmplitude());

          break;
        case DELTA_PINK_PAN_AMPLITUDE:
          delta = getDelta(e, currentState.getPinkPanAmplitude(), e.getEndValue());
          message.setPinkPanAmplitude(currentState.getPinkPanAmplitude() + delta);
          message.setDeltaPinkPanAmplitude(e.getDoubleValue());

          currentState.setPinkPanAmplitude(message.getPinkPanAmplitude());
          currentState.setDeltaPinkPanAmplitude(message.getDeltaPinkPanAmplitude());

          break;
        case DELTA_MEDIA_AMPLITUDE:
          delta = getDelta(e, currentState.getMediaAmplitude(), e.getEndValue());
          message.setMediaAmplitude(currentState.getMediaAmplitude() + delta);
          message.setDeltaMediaAmplitude(e.getDoubleValue());

          currentState.setMediaAmplitude(message.getMediaAmplitude());
          currentState.setDeltaMediaAmplitude(message.getDeltaMediaAmplitude());

          break;
        case DELTA_MEDIA_ENTRAINMENT_STRENGTH:
          delta = getDelta(e, currentState.getMediaEntrainmentStrength(), e.getEndValue());
          message.setMediaEntrainmentStrength(currentState.getMediaEntrainmentStrength() + delta);
          message.setDeltaMediaEntrainmentStrength(e.getDoubleValue());

          currentState.setMediaEntrainmentStrength(message.getMediaEntrainmentStrength());
          currentState.setDeltaMediaEntrainmentStrength(message.getDeltaMediaEntrainmentStrength());

          break;
        case ENTRAINMENT_FREQUENCY:
          processing = currentState.getEntrainmentFrequency() == null
              || e.getDoubleValue() != currentState.getEntrainmentFrequency();
          if (!processing) break;

          message.setEntrainmentFrequency(e.getDoubleValue());
          currentState.setEntrainmentFrequency(e.getDoubleValue());

          break;
        case APPLY_FLASH_TO_BACKGROUND:
          processing = currentState.getFlashBackground() == null
              || e.getBooleanValue() != currentState.getFlashBackground();
          if (!processing) break;

          message.setFlashBackground(e.getBooleanValue());
          currentState.setFlashBackground(e.getBooleanValue());

          break;
        case FREQUENCY:
          processing = currentState.getFrequency() == null || e.getDoubleValue() != currentState.getFrequency();
          if (!processing) break;

          message.setFrequency(e.getDoubleValue());
          currentState.setFrequency(e.getDoubleValue());

          break;
        case INTERVAL_ADD:
        case CUSTOM_INTERVAL_ADD:
          processing = !e.getStringValue().equals(currentState.getIntervalAdd());
          if (!processing) break;

          message.setIntervalAdd(e.getStringValue());
          currentState.setIntervalAdd(e.getStringValue());
          currentState.setIntervalRemove(null);

          break;
        case INTERVAL_REMOVE:
        case CUSTOM_INTERVAL_REMOVE:
          processing = !e.getStringValue().equals(currentState.getIntervalRemove());
          if (!processing) break;

          message.setIntervalRemove(e.getStringValue());
          currentState.setIntervalRemove(e.getStringValue());
          currentState.setIntervalAdd(null);

          break;
        case IS_ANIMATION:
          processing = currentState.getAnimation() == null || e.getBooleanValue() != currentState.getAnimation();
          if (!processing) break;

          message.setAnimation(e.getBooleanValue());
          currentState.setAnimation(e.getBooleanValue());

          break;
        case PINK_ENTRAINER_MULTIPLE:
          processing = currentState.getPinkNoiseMultiple() == null
              || e.getDoubleValue() != currentState.getPinkNoiseMultiple();
          if (!processing) break;

          message.setPinkNoiseMultiple(e.getDoubleValue());
          currentState.setPinkNoiseMultiple(e.getDoubleValue());

          break;
        case PINK_NOISE_AMPLITUDE:
          processing = currentState.getPinkNoiseAmplitude() == null
              || e.getDoubleValue() != currentState.getPinkNoiseAmplitude();
          if (!processing) break;

          message.setPinkNoiseAmplitude(e.getDoubleValue());
          currentState.setPinkNoiseAmplitude(e.getDoubleValue());

          break;
        case PINK_PAN:
          processing = currentState.getPinkPan() == null || e.getBooleanValue() != currentState.getPinkPan();
          if (!processing) break;

          message.setPinkPan(e.getBooleanValue());
          currentState.setPinkPan(e.getBooleanValue());

          break;
        case PINK_PAN_AMPLITUDE:
          processing = currentState.getPinkPanAmplitude() == null
              || e.getDoubleValue() != currentState.getPinkPanAmplitude();
          if (!processing) break;

          message.setPinkPanAmplitude(e.getDoubleValue());
          currentState.setPinkPanAmplitude(e.getDoubleValue());

          break;
        case START_ENTRAINMENT:
          processing = currentState.getStartEntrainment() == null
              || e.getBooleanValue() != currentState.getStartEntrainment();
          if (!processing) break;

          message.setStartEntrainment(e.getBooleanValue());
          currentState.setStartEntrainment(e.getBooleanValue());

          break;
        case STATIC_PICTURE_LOCK:
          processing = currentState.getStaticPictureLock() == null
              || e.getBooleanValue() != currentState.getStaticPictureLock();
          if (!processing) break;

          message.setStaticPictureLock(e.getBooleanValue());
          currentState.setStaticPictureLock(e.getBooleanValue());

          break;
        case IS_SHIMMER:
          processing = currentState.getShimmer() == null || e.getBooleanValue() != currentState.getShimmer();
          if (!processing) break;

          message.setShimmer(e.getBooleanValue());
          currentState.setShimmer(e.getBooleanValue());

          break;
        case SHIMMER_RECTANGLE:
          processing = canProcess(currentState.getShimmerName(), e.getStringValue());
          if (!processing) break;

          message.setShimmerName(e.getStringValue());
          currentState.setShimmerName(e.getStringValue());

          break;
        case DYNAMIC_BACKGROUND:
          processing = currentState.getDynamicPicture() == null
              || e.getBooleanValue() != currentState.getDynamicPicture();
          if (!processing) break;

          message.setDynamicPicture(true);

          currentState.setDynamicPicture(true);
          currentState.setStaticPicture(null);
          currentState.setNoPicture(null);

          break;
        case ANIMATION_BACKGROUND:
          processing = canProcess(currentState.getAnimationBackgroundPic(), e.getStringValue());
          if (!processing) break;

          message.setAnimationBackgroundPic(e.getStringValue());
          currentState.setAnimationBackgroundPic(e.getStringValue());

          break;
        case ANIMATION_COLOR_BACKGROUND:
          processing = canProcess(currentState.getAnimationBackgroundColour(), e.getBooleanValue());
          if (!processing) break;

          message.setAnimationBackgroundColour(e.getBooleanValue());
          currentState.setAnimationBackgroundColour(e.getBooleanValue());

          break;
        case ANIMATION_PROGRAM:
          processing = canProcess(currentState.getAnimationName(), e.getStringValue());
          if (!processing) break;

          message.setAnimationName(e.getStringValue());
          currentState.setAnimationName(e.getStringValue());

          break;
        case STATIC_BACKGROUND:
          processing = currentState.getStaticPicture() == null
              || e.getBooleanValue() != currentState.getStaticPicture();
          if (!processing) break;

          message.setStaticPicture(true);

          currentState.setDynamicPicture(null);
          currentState.setStaticPicture(true);
          currentState.setNoPicture(null);

          break;
        case NO_BACKGROUND:
          processing = currentState.getNoPicture() == null || e.getBooleanValue() != currentState.getNoPicture();
          if (!processing) break;

          message.setNoPicture(true);

          currentState.setDynamicPicture(null);
          currentState.setStaticPicture(null);
          currentState.setNoPicture(true);

          break;
        case NO_BACKGROUND_COLOUR:
          Color cb = e.getColourValue();
          processing = currentState.getBackgroundColour() == null
              || (cb != null && !cb.equals(currentState.getBackgroundColour()));
          if (!processing) break;

          message.setNoBackgroundColor(cb);
          currentState.setNoBackgroundColor(cb);

          break;
        case BACKGROUND_PIC:
          processing = !e.getStringValue().equals(currentState.getStaticPictureFile());
          if (!processing) break;

          message.setStaticPictureFile(e.getStringValue());
          currentState.setStaticPictureFile(e.getStringValue());

          break;
        case BACKGROUND_PIC_DIR:
          processing = !e.getStringValue().equals(currentState.getPictureDirectory());
          if (!processing) break;

          message.setPictureDirectory(e.getStringValue());
          currentState.setPictureDirectory(e.getStringValue());

          break;
        case BACKGROUND_DURATION_SECONDS:
          processing = currentState.getDynamicDuration() == null
              || !currentState.getDynamicDuration().equals(e.getDoubleValue());
          if (!processing) break;

          message.setDynamicDuration((int) e.getDoubleValue());
          currentState.setDynamicDuration((int) e.getDoubleValue());

          break;
        case BACKGROUND_TRANSITION_SECONDS:
          processing = currentState.getDynamicTransition() == null
              || !currentState.getDynamicTransition().equals(e.getDoubleValue());
          if (!processing) break;

          message.setDynamicTransition((int) e.getDoubleValue());
          currentState.setDynamicTransition((int) e.getDoubleValue());

          break;
        case FLASH_TYPE:
          processing = evaluateFlashType((FlashType) e.getOption(), e.getBooleanValue(), message);
          break;
        case MEDIA_AMPLITUDE:
          processing = canProcess(currentState.getMediaAmplitude(), e.getDoubleValue());
          if (!processing) break;

          message.setMediaAmplitude(e.getDoubleValue());
          currentState.setMediaAmplitude(e.getDoubleValue());

          break;
        case MEDIA_ENTRAINMENT:
          processing = canProcess(currentState.getMediaEntrainment(), e.getBooleanValue());
          if (!processing) break;

          message.setMediaEntrainment(e.getBooleanValue());
          currentState.setMediaEntrainment(e.getBooleanValue());

          break;
        case MEDIA_LOOP:
          processing = canProcess(currentState.getMediaLoop(), e.getBooleanValue());
          if (!processing) break;

          message.setMediaLoop(e.getBooleanValue());
          currentState.setMediaLoop(e.getBooleanValue());

          break;
        case MEDIA_ENTRAINMENT_STRENGTH:
          processing = canProcess(currentState.getMediaEntrainmentStrength(), e.getDoubleValue());
          if (!processing) break;

          message.setMediaEntrainmentStrength(e.getDoubleValue());
          currentState.setMediaEntrainmentStrength(e.getDoubleValue());

          break;
        case MEDIA_URI:
          processing = canProcess(currentState.getMediaUri(), e.getStringValue());
          if (!processing) break;

          message.setMediaUri(e.getStringValue());
          currentState.setMediaUri(e.getStringValue());

          break;
        case MEDIA_PLAY:
          processing = canProcess(currentState.getMediaPlay(), e.getBooleanValue());
          if (!processing) break;

          message.setMediaPlay(e.getBooleanValue());
          currentState.setMediaPlay(e.getBooleanValue());

          break;
        case MEDIA_PAUSE:
          processing = canProcess(currentState.getMediaPause(), e.getBooleanValue());
          if (!processing) break;

          message.setMediaPause(e.getBooleanValue());
          currentState.setMediaPause(e.getBooleanValue());

          break;
        case APPLY_FLASH_TO_ANIMATION:
          processing = canProcess(currentState.getFlashAnimation(), e.getBooleanValue());
          if (!processing) break;

          message.setFlashAnimation(e.getBooleanValue());
          currentState.setFlashAnimation(e.getBooleanValue());

          break;
        case APPLY_FLASH_TO_ENTRAINER_FX:
          processing = canProcess(currentState.getFlashEntrainerFX(), e.getBooleanValue());
          if (!processing) break;

          message.setFlashEntrainerFX(e.getBooleanValue());
          currentState.setFlashEntrainerFX(e.getBooleanValue());

          break;
        case APPLY_FLASH_TO_MEDIA:
          processing = canProcess(currentState.getFlashMedia(), e.getBooleanValue());
          if (!processing) break;

          message.setFlashMedia(e.getBooleanValue());
          currentState.setFlashMedia(e.getBooleanValue());

          break;
        case APPLY_FLASH_TO_SHIMMER:
          processing = canProcess(currentState.getFlashShimmer(), e.getBooleanValue());
          if (!processing) break;

          message.setFlashShimmer(e.getBooleanValue());
          currentState.setFlashShimmer(e.getBooleanValue());

          break;
        default:
          processing = false;
          break;
        }

        if (!processing || !isBound()) return;

        if (Settings.getInstance().isDeltaSocketMessage()) {
          broadcast(message);
        } else {
          broadcast(currentState);
        }
      }

    });
  }

  private boolean evaluateFlashType(FlashType option, boolean b, EntrainerStateMessage message) {
    switch (option) {
    case BLOOM:
      if (!canProcess(currentState.getBloom(), b)) return false;

      message.setBloom(b);
      currentState.setBloom(b);

      break;
    case BOX_BLUR:
      if (!canProcess(currentState.getBoxBlur(), b)) return false;

      message.setBoxBlur(b);
      currentState.setBoxBlur(b);

      break;
    case COLOUR_ADJUST:
      if (!canProcess(currentState.getColourAdjust(), b)) return false;

      message.setColourAdjust(b);
      currentState.setColourAdjust(b);

      break;
    case GAUSSIAN_BLUR:
      if (!canProcess(currentState.getGaussianBlur(), b)) return false;

      message.setGaussianBlur(b);
      currentState.setGaussianBlur(b);

      break;
    case GLOW:
      if (!canProcess(currentState.getGlow(), b)) return false;

      message.setGlow(b);
      currentState.setGlow(b);

      break;
    case LIGHTING:
      if (!canProcess(currentState.getLighting(), b)) return false;

      message.setLighting(b);
      currentState.setLighting(b);

      break;
    case MOTION_BLUR:
      if (!canProcess(currentState.getMotionBlur(), b)) return false;

      message.setMotionBlur(b);
      currentState.setMotionBlur(b);

      break;
    case OPACITY:
      if (!canProcess(currentState.getOpacity(), b)) return false;

      message.setOpacity(b);
      currentState.setOpacity(b);

      break;
    case SEPIA_TONE:
      if (!canProcess(currentState.getSepiaTone(), b)) return false;

      message.setSepiaTone(b);
      currentState.setSepiaTone(b);

      break;
    case SHADOW:
      if (!canProcess(currentState.getShadow(), b)) return false;

      message.setShadow(b);
      currentState.setShadow(b);

      break;
    default:
      break;
    }

    return true;
  }

  private boolean canProcess(Object current, Object newVal) {
    return current == null || !current.equals(newVal);
  }

}
