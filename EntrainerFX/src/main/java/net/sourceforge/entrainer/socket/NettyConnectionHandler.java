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

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

// TODO: Auto-generated Javadoc
/**
 * The Class NettyConnectionHandler.
 */
@Sharable
public class NettyConnectionHandler extends AbstractNettyHandler<String> {

	/** The marshal. */
	protected EntrainerStateMessageMarshal marshal = new EntrainerStateMessageMarshal();

	/**
	 * Instantiates a new netty connection handler.
	 *
	 * @param currentState the current state
	 */
	public NettyConnectionHandler(EntrainerStateMessage currentState) {
		super(currentState);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.socket.AbstractNettyHandler#channelRead1(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	protected void channelRead1(ChannelHandlerContext ctx, String msg) throws Exception {
		setEntrainerState(ctx, msg);
	}

}
