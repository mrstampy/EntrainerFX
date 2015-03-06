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
package net.sourceforge.entrainer.gui;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.guitools.MigHelper;

// TODO: Auto-generated Javadoc
/**
 * Superclass to display help dialogs in html. Subclasses must override the
 * getContent() method.
 * 
 * @author burton
 *
 */
public abstract class InformationDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	/** The ok. */
	protected JButton ok = new JButton("Ok");

	/**
	 * Instantiate with the title of the dialog.
	 *
	 * @param title
	 *          the title
	 */
	protected InformationDialog(String title) {
		super((Frame) null, title, true);
		init();
	}

	/**
	 * Override in subclasses to return the html string for display.
	 *
	 * @return the content, as an html string
	 */
	protected abstract String getContent();

	/**
	 * Returns the String representation of the URL of the given resource.
	 *
	 * @param img
	 *          the img
	 * @return the image url
	 */
	protected String getImageUrl(String img) {
		URL url = getClass().getResource(img);

		return url.toExternalForm();
	}

	private void init() {
		addListeners();

		layoutComponents();
	}

	/**
	 * Override in subclasses, if necessary.
	 * 
	 * @see License
	 */
	protected void layoutComponents() {
		MigHelper mh = new MigHelper(getContentPane());
		mh.setLayoutInsets(0, 0, 0, 0);

		mh.grow(100).addLast(getInfoPane()).grow(100).add(getButtonPanel());
	}

	private void addListeners() {
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GuiUtil.fadeOutVisibleFalse(InformationDialog.this, 500);
			}
		});
	}

	/**
	 * Gets the button panel.
	 *
	 * @return the button panel
	 */
	protected Container getButtonPanel() {
		JPanel jp = new JPanel();
		jp.setBorder(new BevelBorder(BevelBorder.RAISED));
		MigHelper mh = new MigHelper(jp);
		mh.add(ok);

		return mh.getContainer();
	}

	/**
	 * Returns the {@link JEditorPane} containing the html returned from the
	 * implementation of getContents().
	 *
	 * @return the info pane
	 */
	protected JEditorPane getInfoPane() {
		JEditorPane pane = new JEditorPane("text/html", getContent());
		pane.setEditable(false);

		return pane;
	}

}
