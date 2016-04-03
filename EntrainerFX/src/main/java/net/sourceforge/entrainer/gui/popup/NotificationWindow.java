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
package net.sourceforge.entrainer.gui.popup;

import java.net.URI;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import net.sourceforge.entrainer.gui.EntrainerFX;
import net.sourceforge.entrainer.gui.jfx.JFXUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class NotificationWindow.
 */
public class NotificationWindow extends Stage {

	/**
	 * Instantiates a new notification window.
	 *
	 * @param message
	 *          the message
	 */
	public NotificationWindow(String message) {
		super(StageStyle.TRANSPARENT);
		initOwner(EntrainerFX.getInstance().getStage());
		initGui(message);
		execute();
	}

	private void initGui(String message) {
		Label label = new Label(message);

		label.setFont(Font.font(16));
		label.setStyle("-fx-background-color: midnightblue");

		HBox box = new HBox(10, label);
		Scene scene = new Scene(box);
		URI css = JFXUtils.getEntrainerCSS();
		if (css != null) scene.getStylesheets().add(css.toString());

		setScene(scene);
	}

	private void execute() {
		SequentialTransition st = new SequentialTransition(getFadeIn(), getPause(), getFadeOut());

		st.onFinishedProperty().addListener(e -> hide());

		setOpacity(0);

		show();

		st.play();
	}

	private Animation getFadeOut() {
		return new Timeline(new KeyFrame(Duration.millis(1000), new KeyValue(opacityProperty(), 0)));
	}

	private Animation getFadeIn() {
		return new Timeline(new KeyFrame(Duration.millis(500), new KeyValue(opacityProperty(), 0.75)));
	}

	private Animation getPause() {
		return new PauseTransition(Duration.seconds(2));
	}

}
