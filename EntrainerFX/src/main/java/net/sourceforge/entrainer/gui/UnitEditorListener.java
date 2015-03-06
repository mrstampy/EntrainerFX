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
package net.sourceforge.entrainer.gui;

import java.util.EventListener;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving unitEditor events. The class that is
 * interested in processing a unitEditor event implements this interface, and
 * the object created with that class is registered with a component using the
 * component's <code>addUnitEditorListener<code> method. When
 * the unitEditor event occurs, that object's appropriate
 * method is invoked.
 *
 * @see UnitEditorEvent
 */
public interface UnitEditorListener extends EventListener {

	/**
	 * Unit editor event performed.
	 *
	 * @param e
	 *          the e
	 */
	void unitEditorEventPerformed(UnitEditorEvent e);
}
