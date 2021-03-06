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
package net.sourceforge.entrainer.gui.jfx;

import java.util.Random;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import net.sourceforge.entrainer.Version;
import net.sourceforge.entrainer.guitools.GuiUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class EntrainerFXSplash.
 */
public class EntrainerFXSplash extends Application implements Version {
	private StackPane stack;
	private Stage stage;
	private ImageView splash;
	private Label title;
	private Label version;

	private Random rand = new Random(System.currentTimeMillis());

	/**
	 * Instantiates a new entrainer fx splash.
	 */
	public EntrainerFXSplash() {

	}

	/**
	 * Instantiates a new entrainer fx splash.
	 *
	 * @param fromApp
	 *          the from app
	 */
	public EntrainerFXSplash(boolean fromApp) {
		init();
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				try {
					start(new Stage());
				} catch (Exception e) {
					GuiUtil.handleProblem(e);
				}
			}
		});
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *          the arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * To front.
	 */
	public void toFront() {
		stage.toFront();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#init()
	 */
	@Override
	public void init() {
		splash = new ImageView(new Image("/brain.jpg"));
		Glow glow = new Glow(0.65);
		glow.setInput(new DropShadow());
		splash.setEffect(glow);
		splash.setOpacity(0.6);

		title = new Label("Entrainer FX");
		title.setAlignment(Pos.CENTER);
		title.setFont(Font.font(null, FontWeight.EXTRA_BOLD, 70));

		InnerShadow is = new InnerShadow();
		is.setOffsetX(5);
		is.setOffsetY(5);
		is.setColor(Color.GOLD);
		is.setInput(new Bloom());

		title.setEffect(is);

		version = new Label(VERSION);
		version.setAlignment(Pos.CENTER);
		version.setFont(Font.font(null, FontWeight.EXTRA_BOLD, 30));
		version.setEffect(is);

		Label sub = new Label("For the only journey that matters...");
		sub.setFont(Font.font(null, FontWeight.EXTRA_BOLD, 25));
		sub.setAlignment(Pos.CENTER);
		sub.setEffect(is);

		VBox splashLayout = new VBox(20);
		splashLayout.getChildren().addAll(title, version, splash, sub);
		splashLayout.setPadding(new Insets(20));
		splashLayout.setAlignment(Pos.CENTER);

		stack = new StackPane();
		stack.getChildren().addAll(splashLayout);
		stack.setAlignment(Pos.CENTER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(final Stage initStage) throws Exception {
		this.stage = initStage;
		Scene splashScene = new Scene(stack);

		initStage.setOpacity(0);
		initStage.initStyle(StageStyle.UNDECORATED);
		initStage.setScene(splashScene);

		stack.opacityProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number old, Number newVal) {
				initStage.setOpacity(newVal.doubleValue());
			}
		});

		initFade(initStage);

		initStage.centerOnScreen();
		initStage.show();

		initFill(splashScene);
	}

	private void initFill(Scene splashScene) {
		Rectangle r = new Rectangle(0, 0, splashScene.getWidth(), splashScene.getHeight());
		stack.getChildren().add(0, r);

		final FillTransition filler = new FillTransition(Duration.millis(1500), r, createColor(), createColor());
		filler.setOnFinished(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				filler.setFromValue(filler.getToValue());
				filler.setToValue(createColor());
				filler.play();
			}
		});

		filler.play();
	}

	private void initFade(final Stage initStage) {
		new SequentialTransition(getFadeIn(), getScaling(), getFadeOut(initStage)).play();
	}

	private Transition getFadeIn() {
		FadeTransition ft = new FadeTransition(Duration.seconds(6), stack);

		ft.setFromValue(0);
		ft.setToValue(1);

		return ft;
	}

	private Transition getFadeOut(final Stage initStage) {
		FadeTransition ft = new FadeTransition(Duration.seconds(3), stack);

		ft.setFromValue(1);
		ft.setToValue(0);
		ft.setOnFinished(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				initStage.close();
			}
		});

		RotateTransition rt = new RotateTransition(Duration.millis(600), splash);

		rt.setInterpolator(Interpolator.LINEAR);
		rt.setCycleCount(5);
		rt.setToAngle(360);

		ScaleTransition st = new ScaleTransition(Duration.seconds(3), splash);

		st.setByX(-2.0);
		st.setByY(-2.0);
		st.setInterpolator(Interpolator.EASE_BOTH);

		return new ParallelTransition(ft, rt, st);
	}

	private Transition getScaling() {
		ScaleTransition st = new ScaleTransition(Duration.seconds(10), splash);

		st.setByX(0.75);
		st.setByY(0.75);
		st.setInterpolator(Interpolator.EASE_BOTH);

		return st;
	}

	private Color createColor() {
		return new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 0.5);
	}

}
