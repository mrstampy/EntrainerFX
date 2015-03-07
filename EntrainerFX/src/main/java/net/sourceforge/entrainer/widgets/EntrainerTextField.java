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

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;
import javax.swing.text.Document;

// TODO: Auto-generated Javadoc
/**
 * Subclasses allow only specific characters in the text field. Implement
 * isValidCharacter to define what characters are valid.
 * 
 * @author burton
 */
public abstract class EntrainerTextField extends JTextField implements KeyEventDispatcher {

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new entrainer text field.
	 */
	public EntrainerTextField() {
		super();
		addKeyEventDispatcher();
	}

	/**
	 * Instantiates a new entrainer text field.
	 *
	 * @param text
	 *          the text
	 */
	public EntrainerTextField(String text) {
		super(text);
		addKeyEventDispatcher();
	}

	/**
	 * Instantiates a new entrainer text field.
	 *
	 * @param columns
	 *          the columns
	 */
	public EntrainerTextField(int columns) {
		super(columns);
		addKeyEventDispatcher();
	}

	/**
	 * Instantiates a new entrainer text field.
	 *
	 * @param text
	 *          the text
	 * @param columns
	 *          the columns
	 */
	public EntrainerTextField(String text, int columns) {
		super(text, columns);
		addKeyEventDispatcher();
	}

	/**
	 * Instantiates a new entrainer text field.
	 *
	 * @param doc
	 *          the doc
	 * @param text
	 *          the text
	 * @param columns
	 *          the columns
	 */
	public EntrainerTextField(Document doc, String text, int columns) {
		super(doc, text, columns);
		addKeyEventDispatcher();
	}

	/**
	 * Implement in subclasses to specify valid values.
	 *
	 * @param key
	 *          the key
	 * @param position
	 *          the position
	 * @return true, if is valid character
	 */
	protected abstract boolean isValidCharacter(char key, int position);

	/**
	 * Checks if is null or blank.
	 *
	 * @param text
	 *          the text
	 * @return true, if is null or blank
	 */
	public boolean isNullOrBlank(String text) {
		return text == null || text.trim().length() == 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.KeyEventDispatcher#dispatchKeyEvent(java.awt.event.KeyEvent)
	 */
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getSource() == this) {
			if (!isValidCharacter(e.getKeyChar(), getCaretPosition()) && isDisplayableCharacter(e.getKeyChar())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks if is numeric.
	 *
	 * @param key
	 *          the key
	 * @return true, if is numeric
	 */
	protected boolean isNumeric(char key) {
		return Character.isDigit(key);
	}

	/**
	 * Checks if is letter.
	 *
	 * @param key
	 *          the key
	 * @return true, if is letter
	 */
	protected boolean isLetter(char key) {
		return Character.isLetter(key);
	}

	/**
	 * Checks if is displayable character.
	 *
	 * @param key
	 *          the key
	 * @return true, if is displayable character
	 */
	protected boolean isDisplayableCharacter(char key) {
		return isNumeric(key) || isLetter(key) || key == ',' || key == '.' || key == '-' || key == '+' || key == '/'
				|| key == ';' || key == '=' || key == '[' || key == ']' || key == KeyEvent.VK_BACK_SLASH
				|| key == KeyEvent.VK_QUOTE || key == '"' || key == KeyEvent.VK_BACK_QUOTE || key == '&' || key == '*'
				|| key == '<' || key == '>' || key == '{' || key == '}' || key == '@' || key == ':' || key == '^' || key == '$'
				|| key == KeyEvent.VK_EURO_SIGN || key == '!' || key == KeyEvent.VK_INVERTED_EXCLAMATION_MARK || key == '#'
				|| key == '(' || key == ')' || key == '_' || key == ' ' || key == '%' || key == '`' || key == '~' || key == '|'
				|| key == '\\' || key == '\'';
	}

	private void addKeyEventDispatcher() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
	}

}
