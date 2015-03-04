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

import java.awt.Container;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.guitools.MigHelper;

// TODO: Auto-generated Javadoc
/**
 * Dialog to display the LICENSE.txt file.
 *  
 * @author burton
 *
 */
public class License extends InformationDialog {

	private static final long serialVersionUID = 1L;
	private static String NOT_FOUND = "License not found.  Released under the GPL";

	private static License instance;
	
	private License() {
		super("License");
	}
	
	/**
	 * Convenience method to display the license dialog.
	 */
	public static void showLicenseDialog() {
		if(instance == null) {
			instance = new License();
		}
		
		GuiUtil.showDialog(instance);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.gui.InformationDialog#getButtonPanel()
	 */
	protected Container getButtonPanel() {
		ok.setIcon(GuiUtil.getIcon("/licenses-gpl.png"));
		ok.setText(null);
		JPanel jp = new JPanel();
		jp.setBorder(new BevelBorder(BevelBorder.RAISED));
		MigHelper mh = new MigHelper(jp);
		mh.add(ok);
		
		return mh.getContainer();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.gui.InformationDialog#layoutComponents()
	 */
	@Override
	protected void layoutComponents() {
		MigHelper mh = new MigHelper(getContentPane());
		mh.setLayoutInsets(0, 0, 0, 0).setLayoutFill(true);
		
		JEditorPane pane = getInfoPane();
		JScrollPane scroll = new JScrollPane(pane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(500, 500));
		
		mh.grow(100).addLast(scroll).grow(100).add(getButtonPanel());
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.gui.InformationDialog#getContent()
	 */
	@Override
	protected String getContent() {
		File f = new File(System.getProperty("user.dir") + "/LICENSE.txt");
		if (!f.exists()) {
			return NOT_FOUND;
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(f));

			String line = reader.readLine();

			StringBuffer buf = new StringBuffer();
			while (line != null) {
				buf.append(line);
				buf.append("<br>");
				line = reader.readLine();
			}

			return buf.toString();
		} catch (Exception e) {
			GuiUtil.handleProblem(e);
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch(IOException e) {
					//ignore
				}
			}
		}

		return NOT_FOUND;
	}

}
