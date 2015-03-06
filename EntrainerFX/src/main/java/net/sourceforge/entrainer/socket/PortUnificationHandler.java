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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class PortUnificationHandler.
 */
public class PortUnificationHandler extends ByteToMessageDecoder {

	private NettyConnectionHandler nettyConnectionHandler;
	private WebSocketHandler webSocketHandler;

	/**
	 * Instantiates a new port unification handler.
	 *
	 * @param nettyConnectionHandler
	 *          the netty connection handler
	 * @param webSocketHandler
	 *          the web socket handler
	 */
	public PortUnificationHandler(NettyConnectionHandler nettyConnectionHandler, WebSocketHandler webSocketHandler) {
		this.nettyConnectionHandler = nettyConnectionHandler;
		this.webSocketHandler = webSocketHandler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.netty.handler.codec.ByteToMessageDecoder#decode(io.netty.channel.
	 * ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// Will use the first five bytes to detect a protocol.
		if (in.readableBytes() < 5) {
			return;
		}

		final int magic1 = in.getUnsignedByte(in.readerIndex());
		final int magic2 = in.getUnsignedByte(in.readerIndex() + 1);
		if (isHttp(magic1, magic2)) {
			switchToWebSockets(ctx);
		} else {
			switchToJava(ctx);
		}
	}

	private static boolean isHttp(int magic1, int magic2) {
		return
//@formatter:off
	    magic1 == 'G' && magic2 == 'E' || // GET
	    magic1 == 'P' && magic2 == 'O' || // POST
	    magic1 == 'P' && magic2 == 'U' || // PUT
	    magic1 == 'H' && magic2 == 'E' || // HEAD
	    magic1 == 'O' && magic2 == 'P' || // OPTIONS
	    magic1 == 'P' && magic2 == 'A' || // PATCH
	    magic1 == 'D' && magic2 == 'E' || // DELETE
	    magic1 == 'T' && magic2 == 'R' || // TRACE
	    magic1 == 'C' && magic2 == 'O';   // CONNECT
//@formatter:on
	}

	private void switchToWebSockets(ChannelHandlerContext ctx) throws Exception {
		ChannelPipeline pipeline = ctx.pipeline();

		pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("handler", webSocketHandler);
		webSocketHandler.channelActive(ctx);

		pipeline.remove(this);
	}

	private void switchToJava(ChannelHandlerContext ctx) throws Exception {
		ChannelPipeline pipeline = ctx.pipeline();

		pipeline.addLast(new LengthFieldBasedFrameDecoder(10000, 0, 4, 0, 4));
		pipeline.addLast(new StringDecoder());
		pipeline.addLast(new LengthFieldPrepender(4));
		pipeline.addLast(new StringEncoder());
		pipeline.addLast(nettyConnectionHandler);
		nettyConnectionHandler.channelActive(ctx);

		pipeline.remove(this);
	}
}