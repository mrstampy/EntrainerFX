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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.Clipboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import net.sourceforge.entrainer.gui.flash.CurrentEffect;
import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.media.MediaEngine;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class MediaPlayerPane.
 */
public class MediaPlayerPane extends AbstractTitledPane {

	private ButtonBase play = ControlButtonFactory.createButton("Play");
	private ButtonBase stop = ControlButtonFactory.createButton("Stop");
	private ButtonBase pause = ControlButtonFactory.createButton("Pause");

	private CheckBox enableMedia = new CheckBox("Enable Media Entrainment");
	private CheckBox loop = new CheckBox("Loop");

	private Slider amplitude = new Slider(0, 1, 1);
	private DecimalFormat amplitudeFormat = new DecimalFormat("#0%");
	private Label amplitudeValue = new Label();

	private Slider strength = new Slider(0, 1, 0.5);
	private DecimalFormat strengthFormat = new DecimalFormat("#0%");
	private Label strengthValue = new Label();

	private Slider trackPosition = new Slider(0, 1, 0);
	private Label timeRemaining = new Label();
	private DecimalFormat remainingFormat = new DecimalFormat("00");

	private MediaView view = new MediaView();

	private TextField media = new TextField("");

	private GridPane pane = new GridPane();

	private String mediaName = null;

	private boolean isUrl = false;

	private double mediaTime;

	private MediaEngine engine = new MediaEngine();

	private AtomicBoolean internalTimeRemaining = new AtomicBoolean(false);

	private boolean flashMedia;

	/**
	 * Instantiates a new media player pane.
	 */
	public MediaPlayerPane() {
		super("Media Player Controls");
		init();
	}

	/**
	 * Sets the controls disabled.
	 *
	 * @param b
	 *          the new controls disabled
	 */
	public void setControlsDisabled(boolean b) {
		amplitude.setDisable(b);
		strength.setDisable(b);
	}

	private boolean isPlaying() {
		return play.isDisable() && (!pause.isDisable() || !stop.isDisable());
	}

	/**
	 * Sets the amplitude value.
	 *
	 * @param value
	 *          the new amplitude value
	 */
	public void setAmplitudeValue(double value) {
		setValue(value, amplitude);
	}

	/**
	 * Sets the amplitude value.
	 *
	 * @param value
	 *          the new amplitude value
	 */
	public void setStrengthValue(double value) {
		setValue(value, strength);
	}

	/**
	 * Gets the amplitude.
	 *
	 * @return the amplitude
	 */
	public Slider getAmplitude() {
		return amplitude;
	}

	/**
	 * Gets the amplitude.
	 *
	 * @return the amplitude
	 */
	public Slider getStrength() {
		return strength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.gui.jfx.AbstractTitledPane#init()
	 */
	protected void init() {
		initMediator();
		initControls();
		initLayout();
		setTooltips();

		super.init();
	}

	private void setTooltips() {
		setTooltip(amplitude, "Sets the media volume");
		setTooltip(enableMedia, "Enables/disables media entrainment");
		setTooltip(loop, "Enables/disables looping of selected media");
		setTooltip(media, "Left click to select local media file, right click to paste media URI");
		setTooltip(pause, "Pauses/resumes media playback");
		setTooltip(play, "Plays selected media");
		setTooltip(stop, "Stops media playback");
		setTooltip(strength, "Sets media entrainment strength");
		setTooltip(trackPosition, "Set/displays the track's current position");
	}

	private void mediaClicked(MouseEvent e) {
		if (isPlaying()) return;

		switch (e.getButton()) {
		case PRIMARY:
			primaryButtonClicked();
			break;
		case SECONDARY:
			secondaryButtonClicked();
			break;
		default:
			break;
		}
	}

	private void primaryButtonClicked() {
		String file = mediaName;

		if (file == null || file.trim().length() == 0 || isUrl) file = "./";

		try {
			createFileMedia(file);
		} catch (URISyntaxException e) {
			GuiUtil.handleProblem(e);
		}
	}

	private void createFileMedia(String file) throws URISyntaxException {
		File mediaFile = new File(new URI(file));
		FileChooser fc = new FileChooser();
		fc.setTitle("Choose Media");
		fc.setInitialDirectory(mediaFile.getParentFile());
		fc.setInitialFileName(mediaFile.getName());

		File newMedia = fc.showOpenDialog(null);

		if (newMedia == null) return;

		mediaName = newMedia.toURI().toString();

		media.setText(newMedia.getName());

		fireReceiverChangeEvent(newMedia.toURI().toString(), MediatorConstants.MEDIA_URI);

		mediaTime = 0;
		isUrl = false;
	}

	private void secondaryButtonClicked() {
		Clipboard clip = Clipboard.getSystemClipboard();

		if (!clip.hasUrl()) return;

		mediaName = clip.getUrl();
		createUrlMedia();
	}

	private void createUrlMedia() {
		media.setText(mediaName);

		fireReceiverChangeEvent(mediaName, MediatorConstants.MEDIA_URI);

		mediaTime = 0;
		isUrl = true;
	}

	private void initControls() {
		play.setOnAction(e -> playMedia(true));
		stop.setOnAction(e -> playMedia(false));
		pause.setOnAction(e -> pauseClicked());
		media.setOnMouseClicked(e -> mediaClicked(e));
		enableMedia.setOnAction(e -> enableMediaClicked());
		loop.setOnAction(e -> loopClicked());

		setTextFill(enableMedia);
		setTextFill(loop);

		media.setEditable(false);
		media.setPrefWidth(200);

		setDisabled(pause, true);
		setDisabled(stop, true);

		view.setPreserveRatio(true);
		view.setCache(true);
		view.setCacheHint(CacheHint.QUALITY);
	}

	private void loopClicked() {
		boolean b = loop.isSelected();

		fireReceiverChangeEvent(b, MediatorConstants.MEDIA_LOOP);
	}

	private void enableMediaClicked() {
		boolean b = enableMedia.isSelected();

		strength.setDisable(!b);

		if (!b) view.setOpacity(1.0);

		fireReceiverChangeEvent(b, MediatorConstants.MEDIA_ENTRAINMENT);
	}

	private void playMedia(boolean b) {
		setPlayControls(b);

		fireReceiverChangeEvent(b, MediatorConstants.MEDIA_PLAY);
	}

	private void setPlayControls(boolean b) {
		setDisabled(stop, !b);
		setDisabled(play, b);
		setDisabled(pause, !b);
	}

	private void pauseClicked() {
		if (!isPlaying()) return;

		boolean paused = setPauseControls();

		fireReceiverChangeEvent(!paused, MediatorConstants.MEDIA_PAUSE);
	}

	private boolean setPauseControls() {
		boolean paused = isPaused();

		setDisabled(stop, !paused);
		return paused;
	}

	private boolean isPaused() {
		return stop.isDisable() && play.isDisable();
	}

	private void setDisabled(ButtonBase button, boolean b) {
		button.setDisable(b);
	}

	private void initLayout() {
		pane.setHgap(10);
		pane.setVgap(20);
		pane.setPadding(new Insets(10));

		initSlider(amplitude, amplitudeValue, amplitudeFormat, MediatorConstants.MEDIA_AMPLITUDE);
		initSlider(strength, strengthValue, strengthFormat, MediatorConstants.MEDIA_ENTRAINMENT_STRENGTH);
		initTrackPosition();

		amplitude.setBlockIncrement(0.01);
		strength.setBlockIncrement(1);
		trackPosition.setBlockIncrement(0.01);

		int row = 0;

		pane.add(enableMedia, 0, row++, 2, 1);

		pane.add(getMediaField(), 0, row++, 3, 1);

		GridPane.setHalignment(view, HPos.CENTER);
		pane.add(view, 0, row++, 3, 1);

		addSlider("Track Position", trackPosition, timeRemaining, row++);
		addSlider("Media Volume", amplitude, amplitudeValue, row++);
		addSlider("Entrainment Strength", strength, strengthValue, row++);

		pane.add(getButtonPanel(), 0, row++, 3, 1);

		pane.setAlignment(Pos.CENTER);
	}

	private Node getMediaField() {
		Label label = new Label("Select Media");
		setTextFill(label);

		HBox box = new HBox(10, label, media, loop);
		box.setAlignment(Pos.CENTER);

		return box;
	}

	private Node getButtonPanel() {
		HBox box = new HBox(10, play, pause, stop);
		box.setAlignment(Pos.CENTER);
		return box;
	}

	private void addSlider(String label, Slider slider, Label value, int row) {
		slider.setId(label);
		Label title = new Label(label);

		setTextFill(title);
		setTextFill(value);

		pane.add(title, 0, row);

		pane.add(slider, 1, row);
		pane.add(value, 2, row);
	}

	private void initSlider(final Slider slider, final Label label, final DecimalFormat format,
			final MediatorConstants event) {
		slider.setEffect(new InnerShadow());

		slider.setMinWidth(350);

		slider.valueProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable arg0) {
				double value = slider.getValue();
				label.setText(format.format(value));
				fireReceiverChangeEvent(value, event);
			}
		});

		label.setText(format.format(slider.getValue()));
		fireReceiverChangeEvent(slider.getValue(), event);
	}

	private void initTrackPosition() {
		trackPosition.setEffect(new InnerShadow());
		trackPosition.setMinWidth(350);
		trackPosition.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

		trackPosition.valueProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				if (internalTimeRemaining.get()) return;
				double value = trackPosition.getValue();
				setMediaTime(value);
				fireReceiverChangeEvent(value, MediatorConstants.MEDIA_TIME);
			}
		});
	}

	private void setValue(final double value, final Slider slider) {
		JFXUtils.runLater(() -> slider.setValue(value));
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this, true) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				boolean b = e.getBooleanValue();
				switch (e.getParm()) {
				case MEDIA_AMPLITUDE:
					setAmplitudeValue(e.getDoubleValue());
					break;
				case MEDIA_ENTRAINMENT_STRENGTH:
					setStrengthValue(e.getDoubleValue());
					break;
				case MEDIA_ENTRAINMENT:
					enableMedia.setSelected(b);
					strength.setDisable(!b);
					break;
				case MEDIA_LOOP:
					loop.setSelected(b);
					break;
				case MEDIA_PLAY:
					JFXUtils.runLater(() -> setPlayControls(b));
					break;
				case MEDIA_PAUSE:
					if (isPlaying()) setPauseControls();
					break;
				case MEDIA_URI:
					setMediaUri(e.getStringValue());
					break;
				case MEDIA_TIME:
					JFXUtils.runLater(() -> setMediaTime(e.getDoubleValue()));
					break;
				case APPLY_FLASH_TO_MEDIA:
					evaluateFlashToMedia(e);
					break;
				case FLASH_EFFECT:
					pulseView(e.getEffect());
					break;
				default:
					break;
				}
			}
		});
	}

	private void evaluateFlashToMedia(ReceiverChangeEvent e) {
		flashMedia = e.getBooleanValue();
		if (!flashMedia) JFXUtils.resetEffects(view);
	}

	private void pulseView(CurrentEffect currentEffect) {
		if (!flashMedia) return;

		if (!enableMedia.isSelected() || view.getMediaPlayer() == null || !isPlaying()) return;

		if(view.getFitHeight() > 0) JFXUtils.setEffect(view, currentEffect);
	}

	private void setMediaTime(double d) {
		if (mediaTime == 0) {
			mediaTime = d;
			trackPosition.setMax(d);
			resizeMediaView();
		}

		internalTimeRemaining.set(true);
		trackPosition.adjustValue(d);
		internalTimeRemaining.set(false);

		timeRemaining.setText(formatTimeRemaining(d));
	}

	private void resizeMediaView() {
		Media m = engine.getMedia();

		if (m.getWidth() == 0 || m.getHeight() == 0) {
			resetMediaView();
			return;
		}

		view.setMediaPlayer(engine.getPlayer());

		double width = 500;
		double height = m.getHeight() * width / m.getWidth();
		view.setFitWidth(width);
		view.setFitHeight(height);
	}

	private void resetMediaView() {
		view.setMediaPlayer(null);
		if (view.getFitWidth() == 0) return;

		view.setFitWidth(0);
		view.setFitHeight(0);
	}

	private String formatTimeRemaining(double d) {
		Duration dur = Duration.seconds(d);

		StringBuilder sb = new StringBuilder();
		int hours = (int) dur.toHours();
		int minutes = (int) dur.toMinutes() - (60 * hours);
		int seconds = (int) dur.toSeconds() - (minutes * 60) - (hours * 3600);
		if (hours > 0) {
			sb.append(remainingFormat.format(hours));
			sb.append(":");
		}
		sb.append(remainingFormat.format(minutes));
		sb.append(":");
		sb.append(remainingFormat.format(seconds));

		return sb.toString();
	}

	private void setMediaUri(String s) {
		mediaName = s == null || s.isEmpty() ? "./" : s;

		try {
			URI uri = new URI(mediaName);
			File f = new File(uri);

			boolean b = f.exists();
			media.setText(b ? f.getName() : mediaName);
			mediaTime = 0;
		} catch (URISyntaxException e) {
			GuiUtil.handleProblem(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.gui.jfx.AbstractTitledPane#getContentPane()
	 */
	@Override
	protected Node getContentPane() {
		return pane;
	}

}
