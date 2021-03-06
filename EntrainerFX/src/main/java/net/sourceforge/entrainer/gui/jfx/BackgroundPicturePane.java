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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class BackgroundPicturePane.
 */
public class BackgroundPicturePane extends AbstractTitledPane {

	private String directoryName = "css";
	private TextField directory = new TextField(directoryName);
	private String pictureName = "";
	private TextField picture = new TextField(pictureName);

	private RadioButton dynamic = new RadioButton("Dynamic");
	private RadioButton staticPic = new RadioButton("Static");
	private RadioButton noPic = new RadioButton("No Image");

	private ToggleGroup picGroup = new ToggleGroup();
	private ColorPicker picker = new ColorPicker();

	private Spinner<Integer> duration = new Spinner<>(1, 120, 10);
	private int durationValue = 10;

	private Spinner<Integer> transition = new Spinner<>(1, 60, 5);
	private int transitionValue = 5;

	private CheckBox flashBackground = new CheckBox("Flash Background");

	private CheckBox staticPictureLock = new CheckBox("Lock Image");

	private CheckBox psychedelic = new CheckBox("Psychedelic");

	private GridPane pane;

	/**
	 * Instantiates a new background picture pane.
	 */
	public BackgroundPicturePane() {
		super("Background Options");
		init();
	}

	@Override
	protected Node getContentPane() {
		return pane;
	}

	public boolean isFlashBackground() {
		return flashBackground.isSelected();
	}

	public int getDuration() {
		return durationValue;
	}

	public int getTransition() {
		return transitionValue;
	}

	public String getPictureDirectory() {
		return directoryName;
	}

	public String getStaticPicture() {
		return pictureName;
	}

	public boolean isDynamic() {
		return dynamic.isSelected();
	}

	public boolean isStatic() {
		return staticPic.isSelected();
	}

	public boolean isNoBackground() {
		return noPic.isSelected();
	}

	public boolean isPictureLock() {
		return staticPictureLock.isSelected();
	}

	public Color getBackgroundColour() {
		return picker.getValue();
	}

	public boolean isPsychedelic() {
		return psychedelic.isSelected();
	}

	public void setFlashBackground(boolean b) {
		flashBackground.setSelected(b);
	}

	public void setDuration(int i) {
		durationValue = i;
		setDuration();
	}

	public void setTransition(int i) {
		transitionValue = i;
		setTransition();
	}

	public void setPictureDirectory(String dir) {
		setDirectory(dir);
	}

	public void setStaticPicture(String pic) {
		setPicture(pic);
	}

	public void setDynamic(boolean b) {
		setRadioButton(dynamic, b);
	}

	public void setStatic(boolean b) {
		setRadioButton(staticPic, b);
	}

	public void setNoBackground(boolean b) {
		setRadioButton(noPic, b);
	}

	public void setPictureLock(boolean b) {
		staticPictureLock.setSelected(b);
	}

	public void setBackgroundColor(Color c) {
		picker.setValue(c);
	}

	public void setPsychedelic(boolean b) {
		psychedelic.setSelected(b);
		JFXUtils.runLater(() -> setState());
	}

	private void setRadioButton(RadioButton rb, boolean b) {
		rb.setSelected(b);
		if (b) rb.fire();
	}

	protected void init() {
		directory.setEditable(false);
		picture.setEditable(false);

		initMediator();
		initRadioButtons();
		setEventHandlers();
		setTooltips();
		setWidths();
		layoutComponents();

		setTextFill(flashBackground);
		setTextFill(psychedelic);
		setTextFill(staticPictureLock);

		super.init();
	}

	private void setTooltips() {
		setTooltip(directory, "Single click to choose a new image directory");
		setTooltip(picture, "Single click to choose a new static image");
		setTooltip(duration, "Set the duration (seconds) a image is displayed before it changes");
		setTooltip(transition, "Set the transition time (seconds) to switch between images");
		setTooltip(dynamic, "Random background image from the chosen directory");
		setTooltip(staticPic, "Single background image");
		setTooltip(noPic, "No background image (choose colour)");
		setTooltip(flashBackground, "Flash Background Image at the Chosen Entrainment Frequency");
		setTooltip(staticPictureLock, "Prevents inadvertent static image changing if selected");
		setTooltip(psychedelic, "Background uses random flashing colours if selected");
	}

	private void setTooltip(Control node, String tip) {
		node.setTooltip(new Tooltip(tip));
	}

	private void layoutComponents() {
		pane = new GridPane();
		pane.setPadding(new Insets(10));

		int col = 0;
		int row = 0;

		GridPane.setConstraints(flashBackground, col, row++, 2, 1);
		GridPane.setMargin(flashBackground, new Insets(0, 0, 0, 5));

		GridPane.setConstraints(dynamic, col++, row);
		GridPane.setMargin(dynamic, new Insets(5));

		Node dir = getDirectoryPane();
		GridPane.setConstraints(dir, col++, row, 2, 1);
		GridPane.setMargin(dir, new Insets(5));

		col++;
		Node spin = getTransitionDurationPane();
		GridPane.setConstraints(spin, col, row, 4, 1);
		GridPane.setMargin(spin, new Insets(5));

		row++;
		col = 0;

		GridPane.setConstraints(staticPic, col++, row);
		GridPane.setMargin(staticPic, new Insets(0, 5, 5, 5));
		Node pic = getPicFilePane();
		GridPane.setConstraints(pic, col++, row, 2, 1);
		GridPane.setMargin(pic, new Insets(0, 5, 5, 5));

		col++;
		GridPane.setConstraints(staticPictureLock, col, row);
		GridPane.setMargin(staticPictureLock, new Insets(0, 5, 5, 5));

		row++;
		col = 0;

		GridPane.setConstraints(noPic, col++, row);
		GridPane.setMargin(noPic, new Insets(15, 5, 5, 5));
		GridPane.setConstraints(picker, col++, row);
		GridPane.setMargin(picker, new Insets(15, 5, 5, 5));
		GridPane.setConstraints(psychedelic, col, row);
		GridPane.setMargin(psychedelic, new Insets(15, 5, 5, 5));

		pane.getChildren().addAll(flashBackground,
				dynamic,
				dir,
				spin,
				staticPic,
				pic,
				staticPictureLock,
				noPic,
				picker,
				psychedelic);

		pane.setAlignment(Pos.CENTER);
	}

	private void initRadioButton(RadioButton rb) {
		setTextFill(rb);
	}

	private void setState() {
		setDynamicState();
		setStaticState();
		setNoPicState();
	}

	private void setDynamicState() {
		boolean b = !dynamic.isSelected();

		directory.setDisable(b);
		transition.setDisable(b);
		duration.setDisable(b);
	}

	private void setStaticState() {
		boolean b = !staticPic.isSelected();

		picture.setDisable(b);
	}

	private void setNoPicState() {
		boolean b = !noPic.isSelected();

		picker.setDisable(b || psychedelic.isSelected());
		psychedelic.setDisable(b);
	}

	private Node getTransitionDurationPane() {
		VBox box = new VBox(10);

		box.getChildren().addAll(getDurationPane(), getTransitionPane());

		return box;
	}

	private void setWidths() {
		duration.setPrefWidth(70);
		transition.setPrefWidth(70);

		picture.setPrefWidth(200);
		directory.setPrefWidth(200);
	}

	private Node getDurationPane() {
		HBox hbox = new HBox(10);

		hbox.setAlignment(Pos.CENTER_RIGHT);
		hbox.getChildren().addAll(createLabel("Duration (sec)"), duration);

		return hbox;
	}

	private Label createLabel(String text) {
		Label label = new Label(text);
		setTextFill(label);
		return label;
	}

	private Node getTransitionPane() {
		HBox hbox = new HBox(10);

		hbox.setAlignment(Pos.CENTER_RIGHT);
		hbox.getChildren().addAll(createLabel("Transition (sec)"), transition);

		return hbox;
	}

	private Node getPicFilePane() {
		HBox box = new HBox(10);

		box.setAlignment(Pos.CENTER_RIGHT);
		box.getChildren().addAll(createLabel("Image File"), picture);

		return box;
	}

	private Node getDirectoryPane() {
		HBox box = new HBox(10);

		box.setAlignment(Pos.CENTER_RIGHT);
		box.getChildren().addAll(createLabel("Image Directory"), directory);

		return box;
	}

	private void setEventHandlers() {
		dynamic.setOnAction(e -> dynamicButtonPressed());

		staticPic.setOnAction(e -> staticButtonPressed());

		noPic.setOnAction(e -> noPicButtonPressed());

		picker.setOnAction(e -> setBackgroundColour(picker.getValue()));

		directory.setOnMouseClicked(e -> directoryClicked(e));

		picture.setOnMouseClicked(e -> pictureClicked(e));

		duration.setOnMouseClicked(e -> durationChanged());

		transition.setOnMouseClicked(e -> transitionChanged());

		flashBackground.setOnAction(e -> flashBackgroundClicked());

		staticPictureLock.setOnAction(e -> pictureLockClicked());

		psychedelic.setOnAction(e -> psychedelicClicked());
	}

	private void psychedelicClicked() {
		fireReceiverChangeEvent(psychedelic.isSelected(), MediatorConstants.IS_PSYCHEDELIC);
		JFXUtils.runLater(() -> setState());
		if (!psychedelic.isSelected()) {
			if (picker.getValue() == null) picker.setValue(Color.ROYALBLUE);
			JFXUtils.runLater(() -> setBackgroundColour(picker.getValue()));
		}
	}

	private void pictureLockClicked() {
		fireReceiverChangeEvent(staticPictureLock.isSelected(), MediatorConstants.STATIC_PICTURE_LOCK);
	}

	private void flashBackgroundClicked() {
		fireReceiverChangeEvent(flashBackground.isSelected(), MediatorConstants.FLASH_BACKGROUND);
	}

	private void initRadioButtons() {
		dynamic.setToggleGroup(picGroup);
		staticPic.setToggleGroup(picGroup);
		noPic.setToggleGroup(picGroup);

		initRadioButton(dynamic);
		initRadioButton(staticPic);
		initRadioButton(noPic);

		dynamic.setSelected(true);
		JFXUtils.runLater(() -> setState());
	}

	private void transitionChanged() {
		if (transitionValue == transition.getValue()) return;

		transitionValue = transition.getValue();

		fireReceiverChangeEvent(transitionValue, MediatorConstants.BACKGROUND_TRANSITION_SECONDS);
	}

	private void durationChanged() {
		if (durationValue == duration.getValue()) return;

		durationValue = duration.getValue();

		fireReceiverChangeEvent(durationValue, MediatorConstants.BACKGROUND_DURATION_SECONDS);
	}

	private void pictureClicked(MouseEvent e) {
		if (e.getButton() != MouseButton.PRIMARY) return;

		String file = pictureName;

		if (file == null || file.trim().length() == 0) file = "./";

		File picFile = new File(file);
		FileChooser fc = new FileChooser();
		fc.setTitle("Choose static picture");
		fc.setInitialDirectory(picFile.getParentFile());
		fc.setInitialFileName(picFile.getName());

		File newPic = fc.showOpenDialog(null);

		if (newPic == null) return;

		pictureName = newPic.getAbsolutePath();

		picture.setText(newPic.getName());

		fireReceiverChangeEvent(pictureName, MediatorConstants.BACKGROUND_PIC);

		if (!staticPictureLock.isSelected()) {
			staticPictureLock.setSelected(true);
			pictureLockClicked();
		}
	}

	private void directoryClicked(MouseEvent e) {
		if (e.getButton() != MouseButton.PRIMARY) return;

		DirectoryChooser dc = new DirectoryChooser();

		dc.setTitle("Choose picture directory");
		dc.setInitialDirectory(new File(directoryName));
		File newDir = dc.showDialog(null);

		if (newDir == null) return;

		directoryName = newDir.getAbsolutePath();

		directory.setText(newDir.getName());

		fireReceiverChangeEvent(directoryName, MediatorConstants.BACKGROUND_PIC_DIR);
	}

	private void staticButtonPressed() {
		staticButtonPressed(true);
	}

	private void dynamicButtonPressed() {
		staticButtonPressed(false);
	}

	private void noPicButtonPressed() {
		JFXUtils.runLater(() -> setState());
		setSpinnersDisabled(true);
		setTextFieldsDisabled(true);

		setBackgroundColour(picker.getValue());

		fireReceiverChangeEvent(true, MediatorConstants.NO_BACKGROUND);
	}

	private void setBackgroundColour(Color color) {
		java.awt.Color awt = JFXUtils.fromJFXColor(color);

		fireReceiverChangeEvent(awt, MediatorConstants.NO_BACKGROUND_COLOUR);
	}

	private void staticButtonPressed(boolean pressed) {
		setSpinnersDisabled(pressed);
		setTextFieldsDisabled(false);
		JFXUtils.runLater(() -> setState());

		fireReceiverChangeEvent(pressed, pressed ? MediatorConstants.STATIC_BACKGROUND
				: MediatorConstants.DYNAMIC_BACKGROUND);
	}

	private void setSpinnersDisabled(boolean pressed) {
		duration.setDisable(pressed);
		transition.setDisable(pressed);
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case STATIC_BACKGROUND:
					JFXUtils.runLater(() -> setStaticButton());
					break;
				case DYNAMIC_BACKGROUND:
					JFXUtils.runLater(() -> setDynamicButton());
					break;
				case NO_BACKGROUND:
					JFXUtils.runLater(() -> setNoBackgroundButton());
					break;
				case NO_BACKGROUND_COLOUR:
					JFXUtils.runLater(() -> setNoBackgroundColour(e.getColourValue()));
					break;
				case BACKGROUND_PIC:
					JFXUtils.runLater(() -> setPicture(e.getStringValue()));
					break;
				case BACKGROUND_PIC_DIR:
					JFXUtils.runLater(() -> setDirectory(e.getStringValue()));
					break;
				case BACKGROUND_DURATION_SECONDS:
					durationValue = (int) e.getDoubleValue();
					JFXUtils.runLater(() -> setDuration());
					break;
				case BACKGROUND_TRANSITION_SECONDS:
					transitionValue = (int) e.getDoubleValue();
					JFXUtils.runLater(() -> setTransition());
					break;
				case FLASH_BACKGROUND:
					if (flashBackground.isSelected() == e.getBooleanValue()) return;
					JFXUtils.runLater(() -> flashBackground.setSelected(e.getBooleanValue()));
					break;
				case STATIC_PICTURE_LOCK:
					if (staticPictureLock.isSelected() == e.getBooleanValue()) return;
					JFXUtils.runLater(() -> staticPictureLock.setSelected(e.getBooleanValue()));
					break;
				case IS_PSYCHEDELIC:
					if (psychedelic.isSelected() == e.getBooleanValue()) return;
					JFXUtils.runLater(() -> setPsychedelic(e.getBooleanValue()));
				default:
					break;
				}
			}

		});
	}

	private void setDuration() {
		if(durationValue <= 0) durationValue = 10;
		duration.getValueFactory().setValue(durationValue);
	}

	private void setTransition() {
		if(transitionValue <= 0) transitionValue = 5;
		transition.getValueFactory().setValue(transitionValue);
	}

	private void setNoBackgroundColour(java.awt.Color color) {
		Color jfx = JFXUtils.toJFXColor(color);

		picker.setValue(jfx);
	}

	private void setNoBackgroundButton() {
		setSpinnersDisabled(true);
		setTextFieldsDisabled(true);
		noPic.setSelected(true);
		JFXUtils.runLater(() -> setState());
	}

	private void setDynamicButton() {
		setSpinnersDisabled(false);
		setTextFieldsDisabled(false);
		dynamic.setSelected(true);
		JFXUtils.runLater(() -> setState());
	}

	private void setStaticButton() {
		setSpinnersDisabled(true);
		setTextFieldsDisabled(false);
		staticPic.setSelected(true);
		JFXUtils.runLater(() -> setState());
	}

	private void setTextFieldsDisabled(boolean b) {
		directory.setDisable(b);
		picture.setDisable(b);
	}

	private void setDirectory(String name) {
		if(name == null) return;
		
		File dir = new File(name);

		directoryName = dir.getAbsolutePath();

		directory.setText(dir.getName());
	}

	private void setPicture(String name) {
		if (staticPictureLock.isSelected() && pictureName != null && !"".equals(pictureName)) return;

		File pic = new File(name);

		pictureName = pic.getAbsolutePath();

		picture.setText(pic.getName());
	}

}
