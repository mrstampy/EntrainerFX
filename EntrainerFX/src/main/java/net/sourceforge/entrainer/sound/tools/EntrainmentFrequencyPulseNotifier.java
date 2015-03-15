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
package net.sourceforge.entrainer.sound.tools;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;
import net.sourceforge.entrainer.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class EntrainmentFrequencyPulseNotifier.
 */
public class EntrainmentFrequencyPulseNotifier {

	private FrequencyToHalfTimeCycle calculator = new FrequencyToHalfTimeCycle();

	private AtomicBoolean run = new AtomicBoolean(false);

	private Sender sender = new SenderAdapter();

	private Lock runLock = new ReentrantLock();
	
	private Thread notificationThread;

	@SuppressWarnings("unused")
	private static EntrainmentFrequencyPulseNotifier notifier;

	/**
	 * Start.
	 */
	public static void start() {
		notifier = new EntrainmentFrequencyPulseNotifier();
	}

	private EntrainmentFrequencyPulseNotifier() {
		initMediator();
	}

	private void setRun(boolean b) {
		runLock.lock();
		try {
			if (b == isRun()) return;

			run.set(b);
			if (b) startNofificationThread();
		} finally {
			runLock.unlock();
		}
	}

	private boolean isRun() {
		return run.get();
	}

	private void startNofificationThread() {
		notificationThread  = new Thread(() -> execute(), "Entrainment cycle notification thread");
		
		notificationThread.start();
	}

	private void execute() {
		notificationThread.setPriority(Thread.MAX_PRIORITY);
		
		while (isRun()) {
			Utils.snooze(calculator.getMillis(), calculator.getNanos());

			if (isRun()) sendFrequencyCycleEvent(true);
		}

		sendFrequencyCycleEvent(false);
	}
	
	private void sendFrequencyCycleEvent(boolean b) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, b, MediatorConstants.ENTRAINMENT_FREQUENCY_PULSE));
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addSender(sender);
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case START_ENTRAINMENT:
					setRun(e.getBooleanValue());
					break;
				default:
					break;
				}
			}
		});
	}
}
