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
package net.sourceforge.entrainer.gui.jfx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating ControlButton objects.
 */
public class ControlButtonFactory {

	/** The Constant BORDERLESS_BUTTON_STYLE. */
	public static final String BORDERLESS_BUTTON_STYLE = "-fx-background-radius: 50; -fx-background-insets: 50;";

	/** The Constant NORMAL_PART. */
	public static final String NORMAL_PART = "-Normal.png";

	/** The Constant HOT_PART. */
	public static final String HOT_PART = "-Hot.png";

	/** The Constant PRESSED_PART. */
	public static final String PRESSED_PART = "-Pressed.png";

	/** The Constant DISABLED_PART. */
	public static final String DISABLED_PART = "-Disabled.png";

	/**
	 * Creates a new ControlButton object.
	 *
	 * @param baseName
	 *          the base name
	 * @return the button base
	 */
	public static ButtonBase createButton(String baseName) {
		final ButtonBase button = getButton(baseName);

		button.setId(baseName);

		button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		button.setStyle(BORDERLESS_BUTTON_STYLE);

		final ImageView normal = new ImageView("/" + baseName + NORMAL_PART);
		final ImageView hot = new ImageView("/" + baseName + HOT_PART);
		final ImageView pressed = new ImageView("/" + baseName + PRESSED_PART);
		final ImageView disabled = new ImageView("/" + baseName + DISABLED_PART);

		button.setGraphic(normal);

		button.setEffect(new DropShadow());

		button.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				setButtonGraphic(button, arg0.isPrimaryButtonDown() ? pressed : hot);
			}
		});

		button.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				setButtonGraphic(button, normal);
			}
		});

		button.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				setButtonGraphic(button, pressed);
			}
		});

		button.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				setButtonGraphic(button, normal);
			}
		});

		button.disabledProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> ob, Boolean from, Boolean to) {
				setButtonGraphic(button, from ? normal : disabled);
			}
		});

		return button;
	}

	private static void setButtonGraphic(final ButtonBase button, final ImageView graphic) {
		button.setGraphic(graphic);
	}

	private static ButtonBase getButton(String baseName) {
		if ("Play".equals(baseName) || "Stop".equals(baseName)) {
			return new Button();
		}

		return new ToggleButton();
	}

}
