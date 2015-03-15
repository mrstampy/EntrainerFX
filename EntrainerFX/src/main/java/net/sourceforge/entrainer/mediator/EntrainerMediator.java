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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

// TODO: Auto-generated Javadoc
/**
 * This class exists to decouple classes. Rather than having gui classes setting
 * values in utility or sound classes, the class that wishes to notify about
 * changes to values simply constructs a {@link ReceiverChangeEvent} object with
 * the appropriate parameters and uses a {@link Sender} object to notify all
 * {@link Receiver}'s of the event. Each implementation of a receiver will
 * determine which parameters are of interest and will take the appropriate
 * action.
 * 
 * @author burton
 * @see Sender
 * @see SenderAdapter
 * @see Receiver
 * @see ReceiverAdapter
 * @see ReceiverChangeEvent
 * @see MediatorConstants
 */
public class EntrainerMediator {
	private List<Sender> senders = new CopyOnWriteArrayList<Sender>();
	private List<Receiver> receivers = new CopyOnWriteArrayList<Receiver>();

	private static EntrainerMediator mediator = new EntrainerMediator();

	private EventHandler<MessageEvent> messageEventHandler;

	private Disruptor<MessageEvent> disruptor;

	private RingBuffer<MessageEvent> rb;

	/**
	 * Returns the only instance of this class.
	 *
	 * @return single instance of EntrainerMediator
	 */
	public static EntrainerMediator getInstance() {
		return mediator;
	}

	private EntrainerMediator() {
		super();

		initMessageEventHandler();
		initDisruptor();
		rb = disruptor.start();
	}

	/**
	 * Adds a {@link Sender} to the list of senders.
	 *
	 * @param sender
	 *          the sender
	 */
	public synchronized void addSender(Sender sender) {
		senders.add(sender);
	}

	/**
	 * Removes the specified {@link Sender} from the list.
	 *
	 * @param sender
	 *          the sender
	 */
	public synchronized void removeSender(Sender sender) {
		senders.remove(sender);
	}

	/**
	 * Adds a {@link Receiver} to the list of receivers.
	 *
	 * @param receiver
	 *          the receiver
	 */
	public void addReceiver(Receiver receiver) {
		receivers.add(receiver);
	}

	/**
	 * Adds the first receiver.
	 *
	 * @param receiver
	 *          the receiver
	 */
	public void addFirstReceiver(Receiver receiver) {
		receivers.add(0, receiver);
	}

	/**
	 * Removes the {@link Receiver} associated with the object which instantiated
	 * it.
	 *
	 * @param source
	 *          the source
	 */
	public void removeReceiver(Object source) {
		if (source != null) {
			for (int i = receivers.size() - 1; i >= 0; i--) {
				if (receivers.get(i).getSource().equals(source)) {
					receivers.remove(i);
				}
			}
		}
	}

	/**
	 * Notify receivers.
	 *
	 * @param e
	 *          the e
	 */
	void notifyReceivers(final ReceiverChangeEvent e) {
		long seq = rb.next();
		MessageEvent be = rb.get(seq);
		be.setMessage(e);
		rb.publish(seq);
	}

	@SuppressWarnings("unchecked")
	private void initDisruptor() {
		disruptor = new Disruptor<MessageEvent>(new MessageEventFactory(), 16, Executors.newCachedThreadPool());

		disruptor.handleEventsWith(messageEventHandler);
	}

	private void initMessageEventHandler() {
		messageEventHandler = new EventHandler<MessageEvent>() {

			@Override
			public void onEvent(final MessageEvent event, long sequence, boolean endOfBatch) throws Exception {
				sendEvent(event.getMessage());
			}
		};
	}

	protected void sendEvent(ReceiverChangeEvent e) {
		for (Receiver receiver : receivers) {
			if (e.getSource() == receiver.getSource()) continue;
			receiver.receiverChangeEventPerformed(e);
		}
	}

}
