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
 * Class for positive doubles.
 *
 * @author burton
 *
 */
public class NumericTextField extends EntrainerTextField {
	private static final long serialVersionUID = 1L;

	private double maxValue = Double.MIN_VALUE;
	private double minValue = Double.MAX_VALUE;

	/**
	 * Instantiates a new numeric text field.
	 */
	public NumericTextField() {
		super();
		init();
	}

	/**
	 * Instantiates a new numeric text field.
	 *
	 * @param text
	 *          the text
	 */
	public NumericTextField(String text) {
		this();
		init(text);
	}

	/**
	 * Instantiates a new numeric text field.
	 *
	 * @param columns
	 *          the columns
	 */
	public NumericTextField(int columns) {
		super(columns);
		init();
	}

	/**
	 * Instantiates a new numeric text field.
	 *
	 * @param text
	 *          the text
	 * @param columns
	 *          the columns
	 */
	public NumericTextField(String text, int columns) {
		this(columns);
		init(text);
	}

	/**
	 * Parses the text.
	 *
	 * @param text
	 *          the text
	 * @return the double
	 */
	protected double parseText(String text) {
		if (!isNullOrBlank(text)) {
			return Double.parseDouble(text);
		}

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.widgets.EntrainerTextField#isValidCharacter(char,
	 * int)
	 */
	protected boolean isValidCharacter(char key, int position) {
		if (isValidMinus(key)) return true;

		return (isValidDecimal(key) || isNumeric(key)) && isInMaxRange(key, position);
	}

	private boolean isValidMinus(char key) {
		return key == '-' && getText().isEmpty();
	}

	private boolean isInMaxRange(char key, int position) {
		if (getMaxValue() == Double.MIN_VALUE) {
			return true;
		}

		double d = Double.parseDouble(getProposedText(key, position));

		return d <= getMaxValue();
	}

	private String getProposedText(char key, int position) {
		String text = getText();

		if (position < text.length()) {
			text = text.substring(0, position) + key + text.substring(position);
		} else {
			text = text + key;
		}

		return text;
	}

	private boolean isValidDecimal(int key) {
		if (key == '.') {
			if (getText().indexOf(key) > -1) {
				return false;
			}

			return true;
		}

		return false;
	}

	/**
	 * Inits the.
	 *
	 * @param text
	 *          the text
	 */
	protected void init(String text) {
		// check to see if valid.
		parseText(text);

		setText(text);
	}

	/**
	 * Inits the.
	 */
	protected void init() {
	}

	/**
	 * Call this method to return the double value of the text field.
	 *
	 * @return the number
	 */
	public double getNumber() {
		return parseText(getText());
	}

	/**
	 * Sets the number.
	 *
	 * @param d
	 *          the new number
	 */
	public void setNumber(double d) {
		setText("" + d);
	}

	/**
	 * Sets the number.
	 *
	 * @param l
	 *          the new number
	 */
	public void setNumber(long l) {
		setText("" + l);
	}

	/**
	 * Sets the number.
	 *
	 * @param i
	 *          the new number
	 */
	public void setNumber(int i) {
		setText("" + i);
	}

	/**
	 * Gets the max value.
	 *
	 * @return the max value
	 */
	public double getMaxValue() {
		return maxValue;
	}

	/**
	 * Sets the max value.
	 *
	 * @param maxValue
	 *          the new max value
	 */
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * Gets the min value.
	 *
	 * @return the min value
	 */
	public double getMinValue() {
		return minValue;
	}

	/**
	 * Sets the min value.
	 *
	 * @param minValue
	 *          the new min value
	 */
	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

}
