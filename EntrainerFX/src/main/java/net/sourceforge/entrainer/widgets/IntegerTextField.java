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
package net.sourceforge.entrainer.widgets;

// TODO: Auto-generated Javadoc
/*
 * Copyright (C) 2008, 2009, 2010 Burton Alexander
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

/**
 * Class for positive integers.
 * 
 * @author burton
 *
 */
public class IntegerTextField extends NumericTextField {

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new integer text field.
	 */
	public IntegerTextField() {
		super();
	}

	/**
	 * Instantiates a new integer text field.
	 *
	 * @param text the text
	 */
	public IntegerTextField(String text) {
		super(text);
	}

	/**
	 * Instantiates a new integer text field.
	 *
	 * @param columns the columns
	 */
	public IntegerTextField(int columns) {
		super(columns);
	}

	/**
	 * Instantiates a new integer text field.
	 *
	 * @param text the text
	 * @param columns the columns
	 */
	public IntegerTextField(String text, int columns) {
		super(text, columns);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.widgets.NumericTextField#isValidCharacter(char, int)
	 */
	protected boolean isValidCharacter(char key, int position) {
		return isNumeric(key);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.widgets.NumericTextField#setNumber(double)
	 */
	public void setNumber(double d) {
		setNumber((int) d);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.widgets.NumericTextField#setNumber(long)
	 */
	public void setNumber(long l) {
		setNumber((int) l);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.widgets.NumericTextField#parseText(java.lang.String)
	 */
	protected double parseText(String text) {
		return (int) super.parseText(text);
	}

}
