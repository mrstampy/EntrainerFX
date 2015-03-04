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
package net.sourceforge.entrainer.xml.program;

import java.awt.Color;

import javax.xml.bind.annotation.adapters.XmlAdapter;

// TODO: Auto-generated Javadoc
/**
 * The Class ColourAdapter.
 */
public class ColourAdapter extends XmlAdapter<String, Color> {

	/**
	 * Instantiates a new colour adapter.
	 */
	public ColourAdapter() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(Color v) throws Exception {
		return v.getRed() + "," + v.getGreen() + "," + v.getBlue();
	}

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Color unmarshal(String v) throws Exception {
		String[] values = v.split(",");
		if(values.length != 3) {
			throw new IllegalArgumentException("Colour string " + v + " is not valid.  Must be of the form 'r,g,b'.");
		}
		
		int r = Integer.parseInt(values[0]);
		int g = Integer.parseInt(values[1]);
		int b = Integer.parseInt(values[2]);
		
		return new Color(r, g, b);
	}

}
