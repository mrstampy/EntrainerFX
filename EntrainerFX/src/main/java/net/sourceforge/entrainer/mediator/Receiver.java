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
package net.sourceforge.entrainer.mediator;

import java.util.EventListener;

// TODO: Auto-generated Javadoc
/**
 * Interface for receiver objects.
 * 
 * @author burton
 * 
 * @see EntrainerMediator
 *
 */
public interface Receiver extends EventListener {

	/**
	 * Implement to process delta changes.
	 *
	 * @param e
	 *          the e
	 * @param current
	 *          the current
	 * @param end
	 *          the end
	 * @return the delta
	 */
	double getDelta(ReceiverChangeEvent e, double current, double end);

	/**
	 * Implement to deal with the {@link ReceiverChangeEvent} appropriately.
	 *
	 * @param e
	 *          the e
	 */
	void receiverChangeEventPerformed(ReceiverChangeEvent e);

	/**
	 * Implement to return the object which instantiated the implementation of
	 * this interface.
	 *
	 * @return the source
	 */
	Object getSource();

	/**
	 * Checks if is pulse receiver.
	 *
	 * @return true, if is pulse receiver
	 */
	boolean isPulseReceiver();

}
