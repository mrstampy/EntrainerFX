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
package net.sourceforge.entrainer.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.xml.Settings;

import org.apache.log4j.Logger;

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
				case ENTRAINMENT_FREQUENCY:
					processing = currentState.getEntrainmentFrequency() == null
							|| e.getDoubleValue() != currentState.getEntrainmentFrequency();
					if (!processing) break;

					message.setEntrainmentFrequency(e.getDoubleValue());
					currentState.setEntrainmentFrequency(e.getDoubleValue());

					break;
				case FLASH_COLOUR:
					processing = currentState.getFlashColour() == null
							|| (e.getColourValue() != null && !e.getColourValue().equals(currentState.getFlashColour()));
					if (!processing) break;

					message.setColour(e.getColourValue());
					currentState.setColour(e.getColourValue());

					break;
				case FLASH_BACKGROUND:
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
				case IS_FLASH:
					processing = currentState.getFlash() == null || e.getBooleanValue() != currentState.getFlash();
					if (!processing) break;

					message.setFlash(e.getBooleanValue());
					currentState.setFlash(e.getBooleanValue());

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
				case START_FLASHING:
					processing = currentState.getStartFlashing() == null
							|| e.getBooleanValue() != currentState.getStartFlashing();
					if (!processing) break;

					message.setStartFlashing(e.getBooleanValue());
					currentState.setStartFlashing(e.getBooleanValue());

					break;
				case IS_PSYCHEDELIC:
					processing = currentState.getPsychedelic() == null || e.getBooleanValue() != currentState.getPsychedelic();
					if (!processing) break;

					message.setPsychedelic(e.getBooleanValue());
					currentState.setPsychedelic(e.getBooleanValue());

					break;
				case IS_SHIMMER:
					processing = currentState.getShimmer() == null || e.getBooleanValue() != currentState.getShimmer();
					if (!processing) break;

					message.setShimmer(e.getBooleanValue());
					currentState.setShimmer(e.getBooleanValue());

					break;
				case DYNAMIC_BACKGROUND:
					processing = currentState.isDynamicPicture() == null
							|| e.getBooleanValue() != currentState.isDynamicPicture();
					if (!processing) break;

					message.setDynamicPicture(true);
					message.setStaticPicture(false);
					message.setNoPicture(false);

					currentState.setDynamicPicture(true);
					currentState.setStaticPicture(false);
					currentState.setNoPicture(false);

					break;
				case STATIC_BACKGROUND:
					processing = currentState.isStaticPicture() == null || e.getBooleanValue() != currentState.isStaticPicture();
					if (!processing) break;

					message.setDynamicPicture(false);
					message.setStaticPicture(true);
					message.setNoPicture(false);

					currentState.setDynamicPicture(false);
					currentState.setStaticPicture(true);
					currentState.setNoPicture(false);

					break;
				case NO_BACKGROUND:
					processing = currentState.isNoPicture() == null || e.getBooleanValue() != currentState.isNoPicture();
					if (!processing) break;

					message.setDynamicPicture(false);
					message.setStaticPicture(false);
					message.setNoPicture(true);

					currentState.setDynamicPicture(false);
					currentState.setStaticPicture(false);
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

}
