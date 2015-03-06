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

import static net.sourceforge.entrainer.gui.CustomIntervalConstants.CIC_DENOMINATOR_NAME;
import static net.sourceforge.entrainer.gui.CustomIntervalConstants.CIC_DIALOG_NAME;
import static net.sourceforge.entrainer.gui.CustomIntervalConstants.CIC_NUMERATOR_NAME;
import static net.sourceforge.entrainer.gui.CustomIntervalConstants.CIC_OK_BUTTON;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.guitools.MigHelper;
import net.sourceforge.entrainer.widgets.IntegerTextField;

// TODO: Auto-generated Javadoc
/**
 * This class adds a custom interval to the {@link IntervalMenu} which
 * instantiated it.
 * 
 * @author burton
 *
 */
public class CustomInterval extends JDialog {

	private static final long serialVersionUID = 1L;

	private IntegerTextField numerator = new CustomIntegerTextField(5);
	private IntegerTextField denominator = new IntegerTextField(5);

	private JButton ok = new JButton(CIC_OK_BUTTON);

	private boolean isOk;
	private IntervalMenu menu;

	/**
	 * Instantiates a new custom interval.
	 *
	 * @param arg0
	 *          the arg0
	 * @param menu
	 *          the menu
	 */
	public CustomInterval(Frame arg0, IntervalMenu menu) {
		super(arg0, CIC_DIALOG_NAME, true);
		this.menu = menu;
		init();
	}

	/**
	 * Instantiates a new custom interval.
	 *
	 * @param arg0
	 *          the arg0
	 * @param menu
	 *          the menu
	 */
	public CustomInterval(Dialog arg0, IntervalMenu menu) {
		super(arg0, CIC_DIALOG_NAME, true);
		this.menu = menu;
		init();
	}

	/**
	 * Gets the numerator.
	 *
	 * @return the numerator
	 */
	public int getNumerator() {
		return (int) numerator.getNumber();
	}

	/**
	 * Gets the denominator.
	 *
	 * @return the denominator
	 */
	public int getDenominator() {
		return (int) denominator.getNumber();
	}

	/**
	 * Gets the display string.
	 *
	 * @return the display string
	 */
	public String getDisplayString() {
		return getNumerator() + "/" + getDenominator();
	}

	/**
	 * Gets the interval.
	 *
	 * @return the interval
	 */
	public double getInterval() {
		return ((double) getNumerator()) / ((double) getDenominator());
	}

	private void init() {
		setResizable(false);
		initLayout();
		addListeners();
		numerator.setName(CIC_NUMERATOR_NAME);
		denominator.setName(CIC_DENOMINATOR_NAME);
		ok.setName(CIC_OK_BUTTON);
	}

	private void addListeners() {
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okPressed();
			}
		});

		numerator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okPressed();
			}
		});

		denominator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okPressed();
			}
		});
	}

	private void okPressed() {
		if (validated()) {
			isOk = true;
			GuiUtil.fadeOutAndDispose(this, 500);
		}
	}

	private boolean validated() {
		StringBuilder builder = new StringBuilder();
		builder.append(getNumeratorErrors());
		builder.append(getDenominatorErrors());
		builder.append(getEquivalentError());
		if (builder.toString().length() > 0) {
			JOptionPane.showMessageDialog(this, builder.toString(), "Errors", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

	private String getEquivalentError() {
		if (menu.isEquivalentFraction(getDisplayString())) {
			return "The interval " + getDisplayString() + "  (" + getInterval() + ")  already exists.\n\n";
		}

		return "";
	}

	private String getNumeratorErrors() {
		if (numerator.getNumber() == 0) {
			return "Numerator must not be = 0\n\n";
		}

		if (getInterval() <= -1) {
			return "Negative intervals must be between -1 and 0\n\n";
		}

		return "";
	}

	private String getDenominatorErrors() {
		if (denominator.getNumber() <= 0) {
			return "Denominator must be > 0\n\n";
		}

		return "";
	}

	private void initLayout() {
		MigHelper mh = new MigHelper(getContentPane());

		mh.setLayoutFillX(true).setLayoutInsets(0, 2, 0, 2);

		mh.addLast(getContentPanel()).grow(100).add(getButtonPanel());
	}

	private Container getContentPanel() {
		MigHelper mh = new MigHelper();

		mh.add(numerator).add(" / ").addLast(denominator);

		return mh.getContainer();
	}

	private Container getButtonPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.RAISED));

		MigHelper mh = new MigHelper(panel);
		mh.add(ok);

		return mh.getContainer();
	}

	/**
	 * Returns true if the ok button was pressed and the values passed validation.
	 *
	 * @return true, if is ok
	 */
	public boolean isOk() {
		return isOk;
	}

	/**
	 * The Class CustomIntegerTextField.
	 */
	class CustomIntegerTextField extends IntegerTextField {
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new custom integer text field.
		 *
		 * @param cols
		 *          the cols
		 */
		public CustomIntegerTextField(int cols) {
			super(cols);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.sourceforge.entrainer.widgets.IntegerTextField#isValidCharacter(char,
		 * int)
		 */
		@Override
		protected boolean isValidCharacter(char key, int position) {
			return isNumeric(key) || (position == 0 && key == '-');
		}
	}

}
