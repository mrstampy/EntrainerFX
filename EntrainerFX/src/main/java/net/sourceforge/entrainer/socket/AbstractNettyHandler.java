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
package net.sourceforge.entrainer.socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.awt.Color;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.entrainer.gui.flash.FlashType;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractNettyHandler.
 *
 * @param <I>
 *          the generic type
 */
public abstract class AbstractNettyHandler<I extends Object> extends SimpleChannelInboundHandler<I> {
	private static final Logger log = Logger.getLogger(AbstractNettyHandler.class);

	private EntrainerStateMessage currentState;

	private Sender sender = new SenderAdapter();

	private EntrainerStateMessageMarshal marshal = new EntrainerStateMessageMarshal();

	private ObjectMapper jsonMapper = new ObjectMapper();

	private ChannelGroup jsonGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private ChannelGroup xmlGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	private List<Channel> uncategorized = Collections.synchronizedList(new ArrayList<Channel>());

	/**
	 * Instantiates a new abstract netty handler.
	 *
	 * @param currentState
	 *          the current state
	 */
	protected AbstractNettyHandler(EntrainerStateMessage currentState) {
		this.currentState = currentState;
		EntrainerMediator.getInstance().addSender(sender);
		jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, Boolean.TRUE);
		jsonMapper.setSerializationInclusion(Include.NON_NULL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.netty.channel.ChannelInboundHandlerAdapter#channelActive(io.netty.channel
	 * .ChannelHandlerContext)
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		uncategorized.add(channel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.netty.channel.ChannelInboundHandlerAdapter#channelInactive(io.netty.
	 * channel.ChannelHandlerContext)
	 */
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		jsonGroup.remove(ctx.channel());
		xmlGroup.remove(ctx.channel());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel
	 * .ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, I msg) throws Exception {
		if (uncategorized.contains(ctx.channel())) {
			categorizeChannel(ctx.channel(), msg);
		}

		channelRead1(ctx, msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.
	 * channel.ChannelHandlerContext, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("Unexpected exception", cause);

		Channel channel = ctx.channel();
		if (channel.isActive()) {
			channel.writeAndFlush("An unexpected error has occurred, disconnecting from EntrainerFX").sync();
		}
		channel.close();
	}

	/**
	 * Channel read1.
	 *
	 * @param ctx
	 *          the ctx
	 * @param msg
	 *          the msg
	 * @throws Exception
	 *           the exception
	 */
	protected abstract void channelRead1(ChannelHandlerContext ctx, I msg) throws Exception;

	/**
	 * Checks if is json.
	 *
	 * @param ctx
	 *          the ctx
	 * @return true, if is json
	 */
	protected boolean isJson(ChannelHandlerContext ctx) {
		return jsonGroup.contains(ctx.channel());
	}

	/**
	 * Sets the entrainer state.
	 *
	 * @param ctx
	 *          the ctx
	 * @param message
	 *          the message
	 * @throws Exception
	 *           the exception
	 */
	protected void setEntrainerState(ChannelHandlerContext ctx, String message) throws Exception {
		if (isJson(ctx)) {
			StringReader reader = new StringReader(message);
			setEntrainerState(ctx, jsonMapper.readValue(reader, EntrainerStateMessage.class));
		} else {
			setEntrainerState(ctx, marshal.unmarshal(message));
		}
	}

	private void categorizeChannel(Channel channel, I msg) {
		if (msg instanceof String && ((String) msg).startsWith("<")) {
			xmlGroup.add(channel);
		} else {
			jsonGroup.add(channel);
		}

		uncategorized.remove(channel);
	}

	/**
	 * Broadcast.
	 *
	 * @param message
	 *          the message
	 * @throws Exception
	 *           the exception
	 */
	public void broadcast(final EntrainerStateMessage message) throws Exception {
		if (!jsonGroup.isEmpty()) {
			TextWebSocketFrame msg = new TextWebSocketFrame(getJson(message));
			jsonGroup.writeAndFlush(msg);
		}

		if (!xmlGroup.isEmpty()) {
			xmlGroup.writeAndFlush(marshal.marshalMessage(message));
		}
	}

	private String getJson(EntrainerStateMessage message) throws JsonGenerationException, JsonMappingException,
			IOException {
		StringWriter writer = new StringWriter();

		jsonMapper.writeValue(writer, message);

		return writer.toString();
	}

	/**
	 * Active sessions.
	 *
	 * @return the int
	 */
	public int activeSessions() {
		return jsonGroup.size() + xmlGroup.size();
	}

	/**
	 * Disconnect all.
	 */
	public void disconnectAll() {
		disconnect(jsonGroup);
		disconnect(xmlGroup);
	}

	private void disconnect(ChannelGroup group) {
		for (Channel channel : group) {
			channel.close();
		}
	}

	/**
	 * Gets the current state.
	 *
	 * @return the current state
	 */
	public EntrainerStateMessage getCurrentState() {
		return currentState;
	}

	// Called when an EntrainerStateMessage is received from a client
	private void setEntrainerState(ChannelHandlerContext ctx, EntrainerStateMessage esm) throws Exception {
		if (esm == null) return;

		fireReceiverChangeEvent(esm.getBloom(), FlashType.BLOOM, MediatorConstants.FLASH_TYPE);
		fireReceiverChangeEvent(esm.getBoxBlur(), FlashType.BOX_BLUR, MediatorConstants.FLASH_TYPE);
		fireReceiverChangeEvent(esm.getColourAdjust(), FlashType.COLOUR_ADJUST, MediatorConstants.FLASH_TYPE);
		fireReceiverChangeEvent(esm.getGaussianBlur(), FlashType.GAUSSIAN_BLUR, MediatorConstants.FLASH_TYPE);
		fireReceiverChangeEvent(esm.getGlow(), FlashType.GLOW, MediatorConstants.FLASH_TYPE);
		fireReceiverChangeEvent(esm.getLighting(), FlashType.LIGHTING, MediatorConstants.FLASH_TYPE);
		fireReceiverChangeEvent(esm.getMotionBlur(), FlashType.MOTION_BLUR, MediatorConstants.FLASH_TYPE);
		fireReceiverChangeEvent(esm.getOpacity(), FlashType.OPACITY, MediatorConstants.FLASH_TYPE);
		fireReceiverChangeEvent(esm.getSepiaTone(), FlashType.SEPIA_TONE, MediatorConstants.FLASH_TYPE);
		fireReceiverChangeEvent(esm.getShadow(), FlashType.SHADOW, MediatorConstants.FLASH_TYPE);

		fireReceiverChangeEvent(esm.getFlashEntrainerFX(), MediatorConstants.APPLY_FLASH_TO_ENTRAINER_FX);
		
		fireReceiverChangeEvent(esm.getAnimation(), MediatorConstants.IS_ANIMATION);
		fireReceiverChangeEvent(esm.getFlashAnimation(), MediatorConstants.APPLY_FLASH_TO_ANIMATION);
		fireReceiverChangeEvent(esm.getAnimationBackgroundColour(), MediatorConstants.ANIMATION_COLOR_BACKGROUND);
		fireReceiverChangeEvent(esm.getAnimationBackgroundPic(), MediatorConstants.ANIMATION_BACKGROUND);
		fireReceiverChangeEvent(esm.getAnimationName(), MediatorConstants.ANIMATION_PROGRAM);
		
		fireReceiverChangeEvent(esm.getShimmer(), MediatorConstants.IS_SHIMMER);
		fireReceiverChangeEvent(esm.getFlashShimmer(), MediatorConstants.APPLY_FLASH_TO_SHIMMER);
		fireReceiverChangeEvent(esm.getShimmerName(), MediatorConstants.SHIMMER_RECTANGLE);

		fireReceiverChangeEvent(esm.getFlashBackground(), MediatorConstants.APPLY_FLASH_TO_BACKGROUND);
		fireReceiverChangeEvent(esm.getDynamicPicture(), MediatorConstants.DYNAMIC_BACKGROUND);
		fireReceiverChangeEvent(esm.getPictureDirectory(), MediatorConstants.BACKGROUND_PIC_DIR);
		fireReceiverChangeEvent(esm.getDynamicDuration(), MediatorConstants.BACKGROUND_DURATION_SECONDS);
		fireReceiverChangeEvent(esm.getDynamicTransition(), MediatorConstants.BACKGROUND_TRANSITION_SECONDS);
		fireReceiverChangeEvent(esm.getStaticPicture(), MediatorConstants.STATIC_BACKGROUND);
		fireReceiverChangeEvent(esm.getStaticPictureFile(), MediatorConstants.BACKGROUND_PIC);
		fireReceiverChangeEvent(esm.getStaticPictureLock(), MediatorConstants.STATIC_PICTURE_LOCK);
		fireReceiverChangeEvent(esm.getNoPicture(), MediatorConstants.NO_BACKGROUND);
		fireReceiverChangeEvent(esm.getBackgroundColour(), MediatorConstants.NO_BACKGROUND_COLOUR);

		fireReceiverChangeEvent(esm.getMediaEntrainment(), MediatorConstants.MEDIA_ENTRAINMENT);
		fireReceiverChangeEvent(esm.getFlashMedia(), MediatorConstants.APPLY_FLASH_TO_MEDIA);
		fireReceiverChangeEvent(esm.getMediaUri(), MediatorConstants.MEDIA_URI);
		fireReceiverChangeEvent(esm.getMediaLoop(), MediatorConstants.MEDIA_LOOP);
		fireReceiverChangeEvent(esm.getMediaAmplitude(), MediatorConstants.MEDIA_AMPLITUDE);
		fireReceiverChangeEvent(esm.getMediaEntrainmentStrength(), MediatorConstants.MEDIA_ENTRAINMENT_STRENGTH);
		fireReceiverChangeEvent(esm.getMediaPlay(), MediatorConstants.MEDIA_PLAY);
		fireReceiverChangeEvent(esm.getMediaPause(), MediatorConstants.MEDIA_PAUSE);
		
		fireReceiverChangeEvent(esm.getPinkPan(), MediatorConstants.PINK_PAN);
		fireReceiverChangeEvent(esm.getAmplitude(), MediatorConstants.AMPLITUDE);
		fireReceiverChangeEvent(esm.getEntrainmentFrequency(), MediatorConstants.ENTRAINMENT_FREQUENCY);
		fireReceiverChangeEvent(esm.getFrequency(), MediatorConstants.FREQUENCY);
		fireReceiverChangeEvent(esm.getIntervalAdd(), MediatorConstants.INTERVAL_ADD);
		fireReceiverChangeEvent(esm.getIntervalRemove(), MediatorConstants.INTERVAL_REMOVE);
		fireReceiverChangeEvent(esm.getPinkNoiseAmplitude(), MediatorConstants.PINK_NOISE_AMPLITUDE);
		fireReceiverChangeEvent(esm.getPinkNoiseMultiple(), MediatorConstants.PINK_ENTRAINER_MULTIPLE);
		fireReceiverChangeEvent(esm.getPinkPanAmplitude(), MediatorConstants.PINK_PAN_AMPLITUDE);
		fireReceiverChangeEvent(esm.getStartEntrainment(), MediatorConstants.START_ENTRAINMENT);

		if (esm.getRequestState() != null) {
			Channel channel = ctx.channel();
			if (jsonGroup.contains(channel)) {
				TextWebSocketFrame msg = new TextWebSocketFrame(getJson(currentState));
				channel.writeAndFlush(msg);
			} else {
				channel.writeAndFlush(marshal.marshalMessage(currentState));
			}
		}
	}

	private void fireReceiverChangeEvent(FlashColour c, MediatorConstants parm) {
		if (c == null) return;
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, new Color(c.getRed(), c.getGreen(), c.getBlue(), c
				.getAlpha()), parm));
	}

	private void fireReceiverChangeEvent(Boolean b, FlashType type, MediatorConstants parm) {
		if (b == null) return;
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, type, b, parm));
	}

	private void fireReceiverChangeEvent(Boolean b, MediatorConstants parm) {
		if (b == null) return;
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, b, parm);
		sender.fireReceiverChangeEvent(e);
	}

	private void fireReceiverChangeEvent(Double b, MediatorConstants parm) {
		if (b == null) return;
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, b, parm);
		sender.fireReceiverChangeEvent(e);
	}

	private void fireReceiverChangeEvent(String b, MediatorConstants parm) {
		if (b == null) return;
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, b, parm);
		sender.fireReceiverChangeEvent(e);
	}

	private void fireReceiverChangeEvent(Integer b, MediatorConstants parm) {
		if (b == null) return;
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, b, parm);
		sender.fireReceiverChangeEvent(e);
	}

}
