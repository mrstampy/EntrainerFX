/*
 *      ______      __             _                 _______  __
 *     / ____/___  / /__________ _(_)___  ___  _____/ ____/ |/ /
 *    / __/ / __ \/ __/ ___/ __ `/ / __ \/ _ \/ ___/ /_   |   / 
 *   / /___/ / / / /_/ /  / /_/ / / / / /  __/ /  / __/  /   |  
 *  /_____/_/ /_/\__/_/   \__,_/_/_/ /_/\___/_/  /_/    /_/|_|  
 *                                                          
 *
 * Copyright (C) 2008 - 2016 Burton Alexander
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;

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
    ButtonBase button = getButton(baseName);
    decorateButton(button, baseName);

    return button;
  }

  /**
   * Decorate button.
   *
   * @param button
   *          the button
   * @param baseName
   *          the base name
   */
  public static void decorateButton(ButtonBase button, String baseName) {
    button.setId(baseName);

    ImageView normal = new ImageView("/" + baseName + NORMAL_PART);
    ImageView hot = new ImageView("/" + baseName + HOT_PART);
    ImageView pressed = new ImageView("/" + baseName + PRESSED_PART);
    ImageView disabled = new ImageView("/" + baseName + DISABLED_PART);

    decorateButton(button, normal, hot, pressed, disabled);
  }

  /**
   * Decorate button.
   *
   * @param button
   *          the button
   * @param normalUri
   *          the normal uri
   * @param hotUri
   *          the hot uri
   */
  public static void decorateButton(ButtonBase button, String normalUri, String hotUri) {
    ImageView normal = new ImageView(normalUri);
    ImageView hot = new ImageView(hotUri);
    decorateButton(button, normal, hot);
  }

  /**
   * Decorate button.
   *
   * @param button
   *          the button
   * @param normal
   *          the normal
   * @param hot
   *          the hot
   */
  public static void decorateButton(ButtonBase button, ImageView normal, ImageView hot) {
    decorateButton(button, normal, hot, normal, null);
  }

  /**
   * Decorate button.
   *
   * @param button
   *          the button
   * @param normal
   *          the normal
   * @param hot
   *          the hot
   * @param pressed
   *          the pressed
   * @param disabled
   *          the disabled
   */
  public static void decorateButton(ButtonBase button, ImageView normal, ImageView hot, ImageView pressed,
      ImageView disabled) {
    button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    button.setStyle(BORDERLESS_BUTTON_STYLE);

    button.setGraphic(normal);

    button.setEffect(new DropShadow());

    button.setOnMouseEntered(e -> setButtonGraphic(button, e.isPrimaryButtonDown() ? pressed : hot));

    button.setOnMouseExited(e -> setButtonGraphic(button, normal));

    button.setOnMousePressed(e -> setButtonGraphic(button, pressed));

    button.setOnMouseReleased(e -> setButtonGraphic(button, normal));

    if (disabled != null) {
      button.disabledProperty().addListener(new ChangeListener<Boolean>() {

        @Override
        public void changed(ObservableValue<? extends Boolean> ob, Boolean from, Boolean to) {
          setButtonGraphic(button, from ? normal : disabled);
        }
      });
    }
  }

  private static void setButtonGraphic(final ButtonBase button, final ImageView graphic) {
    JFXUtils.runLater(() -> button.setGraphic(graphic));
  }

  private static ButtonBase getButton(String baseName) {
    if ("Play".equals(baseName) || "Stop".equals(baseName)) {
      return new Button();
    }

    return new ToggleButton();
  }

}
