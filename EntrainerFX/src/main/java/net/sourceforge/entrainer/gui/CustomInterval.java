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

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.util.converter.IntegerStringConverter;

// TODO: Auto-generated Javadoc
/**
 * This class adds a custom interval to the {@link IntervalMenu} which
 * instantiated it.
 * 
 * @author burton
 *
 */
public class CustomInterval extends DialogPane {

	private TextField numerator = new TextField();
	private TextField denominator = new TextField();

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
	public CustomInterval(IntervalMenu menu) {
		super();
		this.menu = menu;
		init();
	}

	/**
	 * Gets the numerator.
	 *
	 * @return the numerator
	 */
	public int getNumerator() {
		return (int) numerator.getTextFormatter().getValue();
	}

	/**
	 * Gets the denominator.
	 *
	 * @return the denominator
	 */
	public int getDenominator() {
		return (int) denominator.getTextFormatter().getValue();
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
		initFields();
		initLayout();
	}

	private void initFields() {
		numerator.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
		denominator.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
		getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		numerator.setMaxWidth(70);
		denominator.setMaxWidth(70);
		numerator.setAlignment(Pos.CENTER_RIGHT);
		denominator.setAlignment(Pos.CENTER_RIGHT);
	}

	public boolean validated() {
		StringBuilder builder = new StringBuilder();
		builder.append(getNumeratorErrors());
		builder.append(getDenominatorErrors());
		builder.append(getEquivalentError());
		if (builder.toString().length() > 0) {
			Alert alert = new Alert(AlertType.ERROR, builder.toString(), ButtonType.OK);
			alert.setTitle("Errors");
			alert.showAndWait();

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
		if (getNumerator() == 0) {
			return "Numerator must not be = 0\n\n";
		}

		if (getInterval() <= -1) {
			return "Negative intervals must be between -1 and 0\n\n";
		}

		return "";
	}

	private String getDenominatorErrors() {
		if (getDenominator() <= 0) {
			return "Denominator must be > 0\n\n";
		}

		return "";
	}

	private void initLayout() {
		HBox box = new HBox(10);
		box.setAlignment(Pos.CENTER);
		Label slash = new Label("/");
		
		box.getChildren().addAll(numerator, slash, denominator);
		
		setContent(box);
	}

	/**
	 * Returns true if the ok button was pressed and the values passed validation.
	 *
	 * @return true, if is ok
	 */
	public boolean isOk() {
		return isOk;
	}

}
