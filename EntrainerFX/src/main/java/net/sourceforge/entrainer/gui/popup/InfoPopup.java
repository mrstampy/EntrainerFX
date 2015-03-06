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
package net.sourceforge.entrainer.gui.popup;

import java.awt.Color;
import java.awt.Window;

import javax.swing.JEditorPane;
import javax.swing.JWindow;

// TODO: Auto-generated Javadoc
/**
 * A superclass for all informational popup windows. Implement the getContent
 * method as appropriate, and call the init method after instantiation. The
 * information is displayed as an html string; html code necessary.
 * 
 * @author burton
 *
 */
public abstract class InfoPopup extends JWindow {
	private static final long serialVersionUID = 1L;
	private JEditorPane displayArea;
	private static InfoPopup previous = null;
	private static Color background = new Color(255, 255, 168);

	/**
	 * Instantiate with the owner window.
	 *
	 * @param owner
	 *          the owner
	 */
	public InfoPopup(Window owner) {
		super(owner);
		cleanup();
	}

	private void cleanup() {
		if (previous != null) {
			previous.dispose();
		}

		previous = this;
	}

	/**
	 * Mandatory: call this method after subclass has been instantiated ie. last
	 * method call in constructor.
	 */
	protected void init() {
		setBackground(background);
		setDisplayArea(new JEditorPane("text/html", getContent()));
		getDisplayArea().setBackground(background);
		getDisplayArea().setEditable(false);

		getContentPane().add(getDisplayArea());
	}

	/**
	 * Implement as appropriate in subclasses.
	 *
	 * @return the content
	 */
	protected abstract String getContent();

	/**
	 * Gets the display area.
	 *
	 * @return the display area
	 */
	protected JEditorPane getDisplayArea() {
		return displayArea;
	}

	/**
	 * Sets the display area.
	 *
	 * @param displayArea
	 *          the new display area
	 */
	protected void setDisplayArea(JEditorPane displayArea) {
		this.displayArea = displayArea;
	}

	/**
	 * Returns true if there is text to display.
	 *
	 * @return true, if successful
	 */
	public boolean shouldDisplay() {
		return getDisplayArea().getText() != null && getDisplayArea().getText().trim().length() > 0;
	}

}
