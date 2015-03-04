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
package net.sourceforge.entrainer.mediator;

import net.sourceforge.entrainer.xml.program.EntrainerProgramUnit;

// TODO: Auto-generated Javadoc
/**
 * Abstract implementation of the {@link Receiver} interface.
 * 
 * @author burton
 * @see EntrainerMediator
 * @see Receiver
 */
public abstract class ReceiverAdapter implements Receiver {
	private Object source;

	/**
	 * Instantiate with the object which is interested in notification of property
	 * changes.
	 *
	 * @param source the source
	 */
	public ReceiverAdapter(Object source) {
		super();
		setSource(source);
	}

	/**
	 * Implementation preventing the {@link Sender} which created the event from
	 * receiving notification of the event. To prevent circular event calling,
	 * classes of the same type are prevented from sending each other messages via
	 * the mediator. Should future development require this, simply create a blank
	 * subclass for one of the instances.
	 *
	 * @param e the e
	 */
	public void receiverChangeEventPerformed(ReceiverChangeEvent e) {
		if (!e.getSource().getClass().equals(getSource().getClass())) {
			processReceiverChangeEvent(e);
		}
	}

	/**
	 * Implement appropriately in subclasses to deal with event notification.
	 *
	 * @param e the e
	 */
	protected abstract void processReceiverChangeEvent(ReceiverChangeEvent e);

	/**
	 * Returns the object with which this class was instantiated.
	 *
	 * @return the source
	 */
	public Object getSource() {
		return source;
	}

	/**
	 * Sets the source.
	 *
	 * @param source the new source
	 */
	protected void setSource(Object source) {
		this.source = source;
	}

	/**
	 * Implementation to return the correct delta for a changing property.
	 * Prevents the property from exceeding the end value. Returns -
	 * Double.MAX_VALUE should this method be called inappropriately.
	 *
	 * @param e the e
	 * @param current the current
	 * @param end the end
	 * @return the delta
	 * @see EntrainerProgramUnit
	 */
	public double getDelta(ReceiverChangeEvent e, double current, double end) {
		switch (e.getParm()) {
		case DELTA_AMPLITUDE:
		case DELTA_ENTRAINMENT_FREQUENCY:
		case DELTA_FREQUENCY:
		case DELTA_PINK_NOISE_AMPLITUDE:
		case DELTA_PINK_ENTRAINER_MULTIPLE:
		case DELTA_PINK_PAN_AMPLITUDE:
			double delta = e.getDoubleValue();
			boolean forward = delta >= 0;
			if (forward) {
				if (delta + current > end) {
					return end - current;
				}
			} else if (delta + current < end) {
				return end - current;
			}

			return delta;
		default:
			return -Double.MAX_VALUE;
		}
	}

}
