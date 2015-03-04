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
package net.sourceforge.entrainer.sound.jsyn;

// TODO: Auto-generated Javadoc
/**
 * Contains constants for line out & recording channels.
 * 
 * @author burton
 */
public enum JSynChannels {
	
	/** The lineout left. */
	LINEOUT_LEFT(0),
	
	/** The lineout right. */
	LINEOUT_RIGHT(1),
	
	/** The recording left. */
	RECORDING_LEFT(2),
	
	/** The recording right. */
	RECORDING_RIGHT(3);
	
	private int channel;
	
	/**
	 * Instantiates a new j syn channels.
	 *
	 * @param channel the channel
	 */
	JSynChannels(int channel) {
		this.channel = channel;
	}
	
	/**
	 * Gets the channel.
	 *
	 * @return the channel
	 */
	public int getChannel() {
		return channel;
	}
}
