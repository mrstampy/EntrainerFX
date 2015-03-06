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

import static net.sourceforge.entrainer.mediator.MediatorConstants.START_FLASHING;

import java.net.URI;
import java.net.URL;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.sourceforge.entrainer.gui.FlashPanel;
import net.sourceforge.entrainer.gui.jfx.shimmer.AbstractShimmer;
import net.sourceforge.entrainer.gui.jfx.shimmer.LinearShimmerRectangle;
import net.sourceforge.entrainer.gui.jfx.shimmer.ShimmerRegister;
import net.sourceforge.entrainer.gui.jfx.trident.ColorPropertyInterpolator;
import net.sourceforge.entrainer.gui.jfx.trident.LinearGradientInterpolator;
import net.sourceforge.entrainer.gui.jfx.trident.RadialGradientInterpolator;
import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;
import net.sourceforge.entrainer.sound.MasterLevelController;
import net.sourceforge.entrainer.sound.SoundControl;
import net.sourceforge.entrainer.sound.jsyn.JSynSoundControl;
import net.sourceforge.entrainer.sound.tools.Panner;

import org.pushingpixels.trident.TridentConfig;

// TODO: Auto-generated Javadoc
/**
 * The Class EntrainerFXLite.
 */
public class EntrainerFXLite extends Application {

	// JSyn classes
	private SoundControl control;

	private FlashPanel messagePanel = new FlashPanel();

	// Button controls
	private SoundControlPane soundControlPane = new SoundControlPane();
	private SliderControlPane sliderControlPane = new SliderControlPane();
	private FlashPane checkBoxPane = new FlashPane();
	private PinkPanningPane pinkPanningPane = new PinkPanningPane();
	private ShimmerOptionsPane shimmerOptions = new ShimmerOptionsPane();

	private Sender sender = new SenderAdapter();

	private ImageView background = new ImageView();

	private GridPane gp = new GridPane();
	private Group group;
	private Scene scene;
	private Stage stage;

	@SuppressWarnings("unused")
	private MasterLevelController masterLevelController;

	/**
	 * Instantiates a new entrainer fx lite.
	 */
	public EntrainerFXLite() {
		control = new JSynSoundControl();
		MasterLevelController.lite = true;
		masterLevelController = new MasterLevelController(control);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#init()
	 */
	@Override
	public void init() {
		TridentConfig.getInstance().addPropertyInterpolator(new ColorPropertyInterpolator());
		TridentConfig.getInstance().addPropertyInterpolator(new LinearGradientInterpolator());
		TridentConfig.getInstance().addPropertyInterpolator(new RadialGradientInterpolator());

		gp.setAlignment(Pos.BASELINE_CENTER);
		GridPane.setConstraints(soundControlPane, 0, 0);
		GridPane.setConstraints(sliderControlPane, 1, 0);
		GridPane.setValignment(sliderControlPane, VPos.BOTTOM);
		GridPane.setConstraints(messagePanel, 0, 1, 2, 1);
		GridPane.setValignment(messagePanel, VPos.BOTTOM);
		GridPane.setVgrow(messagePanel, Priority.ALWAYS);
		GridPane.setConstraints(checkBoxPane, 0, 2, 2, 1);
		GridPane.setConstraints(pinkPanningPane, 0, 3, 2, 1);
		GridPane.setConstraints(shimmerOptions, 0, 4, 2, 1);
		gp.setPadding(new Insets(5));
		gp.getChildren().addAll(soundControlPane,
				sliderControlPane,
				checkBoxPane,
				pinkPanningPane,
				messagePanel,
				shimmerOptions);

		wireButtons();

		initToolTips();
		createPanner();
		group = new Group();

		soundControlPane.getPause().setVisible(false);
		soundControlPane.getRecord().setVisible(false);
		checkBoxPane.getColourChooser().setVisible(false);
	}

	private void initState() {
		fireReceiverChangeEvent(7.83, MediatorConstants.ENTRAINMENT_FREQUENCY);
		fireReceiverChangeEvent(120, MediatorConstants.FREQUENCY);
		fireReceiverChangeEvent(0.8, MediatorConstants.AMPLITUDE);
		fireReceiverChangeEvent(.75, MediatorConstants.PINK_NOISE_AMPLITUDE);
		fireReceiverChangeEvent(true, MediatorConstants.PINK_PAN);
		fireReceiverChangeEvent(0.8, MediatorConstants.PINK_PAN_AMPLITUDE);
		fireReceiverChangeEvent(0.8, MediatorConstants.PINK_PAN_VALUE);
		fireReceiverChangeEvent(120, MediatorConstants.PINK_ENTRAINER_MULTIPLE);
		fireReceiverChangeEvent(LinearShimmerRectangle.NAME, MediatorConstants.SHIMMER_RECTANGLE);
		fireReceiverChangeEvent(true, MediatorConstants.IS_SHIMMER);
		fireReceiverChangeEvent(true, MediatorConstants.IS_FLASH);
		fireReceiverChangeEvent(true, MediatorConstants.IS_PSYCHEDELIC);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(final Stage stage) throws Exception {
		this.stage = stage;

		scene = new Scene(group);
		URL url = EntrainerFXLite.class.getResource("/entrainer.css");
		String css = null;
		if (url == null) {
			URI uri = JFXUtils.getEntrainerCSS();
			if (uri != null) css = uri.toString();
		} else {
			css = url.toExternalForm();
		}

		if (css != null) {
			scene.getStylesheets().add(css);
		}

		background.setId("background-image");
		background.setOpacity(0.25);
		group.getChildren().add(background);
		group.getChildren().add(gp);
		gp.setMinSize(background.getFitWidth(), background.getFitHeight());

		stage.setScene(scene);
		stage.centerOnScreen();
		stage.setResizable(false);
		stage.show();
		scaleBackground();
		initMediator();
		initState();
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addSender(sender);
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case START_ENTRAINMENT:
					if (e.getSource() == soundControlPane) break;
					soundControlPane.setPlaying(e.getBooleanValue());
					break;
				case SHIMMER_RECTANGLE:
					setShimmerRectangle(e.getStringValue());
				default:
					break;

				}
			}

		});
	}

	private void setShimmerRectangle(final String stringValue) {
		if (stringValue == null || stringValue.isEmpty()) return;

		final AbstractShimmer<?> shimmer = ShimmerRegister.getShimmer(stringValue);
		if (shimmer == null) return;

		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				swapShimmers(shimmer);
			}
		});
	}

	private void swapShimmers(final AbstractShimmer<?> shimmer) {
		try {
			AbstractShimmer<?> old = (AbstractShimmer<?>) group.getChildren().get(1);
			if (old == shimmer) return;

			group.getChildren().remove(old);
			old.stop();
			old.setInUse(false);
		} catch (ClassCastException gulp) {

		}

		try {
			shimmer.setInUse(true);
			group.getChildren().add(1, shimmer);
			if (control.isPlaying()) shimmer.start();
		} catch (Throwable e) {
			GuiUtil.handleProblem(e);
		}
	}

	private void scaleBackground() {
		if (background.getImage() == null) {
			scene.setFill(Color.DARKSEAGREEN);
			unexpandTitledPanes();
			return;
		}

		double backWidth = background.getImage().getWidth();
		double backHeight = background.getImage().getHeight();
		double ratio = backWidth / backHeight;

		double newWidth = gp.getWidth() + 10;

		background.setFitWidth(ratio < 1 ? newWidth : newWidth * ratio);
		background.setFitHeight(background.getFitWidth() / ratio);

		stage.setWidth(newWidth);
		stage.setHeight(background.getFitHeight());

		double xDiff = background.getFitWidth() - stage.getWidth();
		if (xDiff > 0) background.setX(0 - xDiff / 2);

		double yDiff = background.getFitHeight() - stage.getHeight();
		if (yDiff > 0) background.setY(0 - yDiff / 2);

		// For strangely shaped pictures
		if (stage.getX() < 0 || stage.getY() < 0) {
			stage.setX(10);
			stage.setY(10);
		}

		unexpandTitledPanes();

		setShimmerSizes();
	}

	private void setShimmerSizes() {
		for (AbstractShimmer<?> shimmer : ShimmerRegister.getShimmers()) {
			shimmer.setWidth(stage.getWidth());
			shimmer.setHeight(stage.getHeight());
		}
	}

	private void unexpandTitledPanes() {
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				checkBoxPane.setExpanded(false);
				pinkPanningPane.setExpanded(false);
				shimmerOptions.setExpanded(false);
			}
		});
	}

	private void initToolTips() {
		soundControlPane.setPlayToolTip("Start Entrainment");
		soundControlPane.setRecordToolTip("Flag/Clear Recording to '.wav' File");
		soundControlPane.setStopToolTip("Stop Entrainment");
		soundControlPane.setPauseToolTip("Pause/Resume an Entrainer Program");

		checkBoxPane.setPsychedelicToolTip("Cause the Flashing Colour to Randomly Change");
		checkBoxPane.setFlashToolTip("Flash Colours at the Chosen Entrainment Frequency");
		shimmerOptions.setShimmerToolTip("Adds a shimmer effect to the application");
	}

	private void fireReceiverChangeEvent(boolean value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	private void fireReceiverChangeEvent(double value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	private void fireReceiverChangeEvent(String value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	private void wireButtons() {
		checkBoxPane.getFlash().addEventHandler(javafx.event.ActionEvent.ACTION,
				new EventHandler<javafx.event.ActionEvent>() {

					@Override
					public void handle(javafx.event.ActionEvent arg0) {
						flashClicked();
					}
				});
	}

	private void flashClicked() {
		fireReceiverChangeEvent(checkBoxPane.getFlash().isSelected() && !soundControlPane.getStop().isDisabled(),
				START_FLASHING);
	}

	private void createPanner() {
		Panner.getInstance();
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

}
