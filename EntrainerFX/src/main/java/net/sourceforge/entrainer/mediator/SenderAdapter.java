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
package net.sourceforge.entrainer.mediator;

// TODO: Auto-generated Javadoc
/**
 * Concrete implementation of the {@link Sender} interface.
 * 
 * @author burton
 * 
 * @see EntrainerMediator
 * @see ReceiverChangeEvent
 */
public class SenderAdapter implements Sender {
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new sender adapter.
	 */
	public SenderAdapter() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.mediator.Sender#fireReceiverChangeEvent(net.
	 * sourceforge.entrainer.mediator.ReceiverChangeEvent)
	 */
	public synchronized void fireReceiverChangeEvent(ReceiverChangeEvent e) {
		EntrainerMediator.getInstance().notifyReceivers(e);
	}

}
