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

import net.sourceforge.entrainer.guitools.GuiUtil;

// TODO: Auto-generated Javadoc
/**
 * Basic overview for the xml editor.
 *  
 * @author burton
 *
 */
public class XmlEditorAbout extends InformationDialog {

	private static final long serialVersionUID = 1L;
	private static XmlEditorAbout instance = new XmlEditorAbout();

	private XmlEditorAbout() {
		super("XML Editor Help");
	}
	
	/**
	 * Show dialog.
	 */
	public static void showDialog() {
		GuiUtil.showDialog(instance);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.gui.InformationDialog#getContent()
	 */
	@Override
	protected String getContent() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("The XML Editor allows the user to edit or create Entrainer xml files.");
		buf.append(" Each tab represents a unit of time in which each of the fields vary by");
		buf.append("<br>the specified amounts.  The tab title reflects the cumulative time of each");
		buf.append(" of the previous units, and indicates the range of time in which the unit");
		buf.append("<br>will be executed.  During unit execution the properties vary from the");
		buf.append(" 'Start' value to the 'End' value.");
		
		buf.append("<br><br><u><b>Global Values:</b></u>");
		buf.append("<br><br>Flash - true or false.  Will cause Entrainer to change colour at the");
		buf.append(" specified entrainment frequency if true.");
		
		buf.append("<br><br>Psychedelic - true or false.  Will randomly change the colour Entrainer");
		buf.append(" uses to flash.  Flash must be set to true for this to have meaning.");
		
		buf.append("<br><br>Choose Colour (default yellow) - Sets the colour Entrainer uses to");
		buf.append(" flash.  Flash must be true, and this value is overridden when Psychedelic");
		buf.append(" is true.");
		
		buf.append("<br><br><u><b>Unit Values:</b></u>");
		buf.append("<br><br>Minutes/Seconds - The total time the unit is executed to vary the properties");
		buf.append(" from the 'Start' values to the 'End' values");
		
		buf.append("<br><br><u><b>Unit Properties:</b></u>");
		buf.append("<br><br>Entrainment Frequency - The entrainment frequency in Hz, from 0 to 40Hz");
		buf.append("<br><br>Frequency - The base frequency in Hz, from 20 to 500Hz");
		buf.append("<br><br>Amplitude - The volume, from 0 to 1");
		buf.append("<br><br>Pink Noise - Pink noise volume, from 0 to 1");
		buf.append("<br><br>Pink Noise Pan - The amplitude of the panning for pink noise, from 0 to 1");
		buf.append("<br><br>Pink Noise Entrainment Frequency Multiple - The value used as a multiple");
		buf.append(" of the entrainment frequency to determine the frequency of the pink noise");
		buf.append("<br>panning, from 1 to 512");
		
		return buf.toString();
	}

}
