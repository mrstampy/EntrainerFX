package net.sourceforge.entrainer.gui.jfx;

import java.io.File;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;

public class BackgroundPicturePane extends TitledPane {
	
	private Sender sender = new SenderAdapter();

	private String directoryName = "css";
	private TextField directory = new TextField(directoryName);
	private String pictureName = "";
	private TextField picture = new TextField(pictureName);
	
	private ToggleButton staticPicture = new ToggleButton("Dynamic");
	
	private Spinner<Integer> duration = new Spinner<>(1, 120, 10);
	private int durationValue = 10;
	
	private Spinner<Integer> transition = new Spinner<>(1, 60, 5);
	private int transitionValue = 5;
	
	public BackgroundPicturePane() {
		super();
		init();
	}
	
	private void init() {
		setText("Background Picture Controls");
		
		directory.setEditable(false);
		picture.setEditable(false);
		
		initMediator();
		setEventHandlers();
		setTooltips();
		layoutComponents();
	}

	private void setTooltips() {
		setTooltip(directory, "Single click to choose a new picture directory");
		setTooltip(picture, "Single click to choose a new static picture");
		setTooltip(duration, "Set the duration (seconds) a picture is displayed before it changes");
		setTooltip(transition, "Set the transition time (seconds) to switch between pictures");
	}
	
	private void setTooltip(Control node, String tip) {
		node.setTooltip(new Tooltip(tip));
	}

	private void layoutComponents() {
		FlowPane fp = new FlowPane();
		
		fp.setPadding(new Insets(10));
		fp.setHgap(10);
		fp.setVgap(10);
		fp.setAlignment(Pos.CENTER);
		
		fp.getChildren().addAll(staticPicture, getFilePane(), getSpinnerPane());
		
		setContent(fp);
	}

	private Node getSpinnerPane() {
		VBox box = new VBox(10);
		
		box.getChildren().addAll(getDurationPane(), getTransitionPane());
		
		return box;
	}

	private Node getDurationPane() {
		HBox hbox = new HBox(10);

		hbox.setAlignment(Pos.CENTER_RIGHT);
		hbox.getChildren().addAll(new Label("Duration (sec)"), duration);
		
		return hbox;
	}

	private Node getTransitionPane() {
		HBox hbox = new HBox(10);
		
		hbox.setAlignment(Pos.CENTER_RIGHT);
		hbox.getChildren().addAll(new Label("Transition (sec)"), transition);
		
		return hbox;
	}

	private Node getFilePane() {
		VBox box = new VBox(10);
		
		box.getChildren().addAll(getDirectoryPane(), getPicFilePane());

		return box;
	}

	private Node getPicFilePane() {
		HBox box = new HBox(10);

		box.setAlignment(Pos.CENTER_RIGHT);
		box.getChildren().addAll(new Label("Static Picture"), picture);
		
		return box;
	}

	private Node getDirectoryPane() {
		HBox box = new HBox(10);
		
		box.setAlignment(Pos.CENTER_RIGHT);
		box.getChildren().addAll(new Label("Picture Directory"), directory);

		return box;
	}

	private void setEventHandlers() {
		staticPicture.setOnAction(e -> Platform.runLater(() -> staticPicButtonPressed()));
		
		directory.setOnMouseClicked(e -> Platform.runLater(() -> directoryClicked(e)));
		
		picture.setOnMouseClicked(e -> Platform.runLater(() -> pictureClicked(e)));

		duration.setOnMouseClicked(e -> durationChanged());
		
		transition.setOnMouseClicked(e -> transitionChanged());

		expandedProperty().addListener(e -> setOpacity(isExpanded() ? 1 : 0.25));
	}

	private void transitionChanged() {
		if(transitionValue == transition.getValue()) return;
		
		transitionValue = transition.getValue();
		
		fireReceiverChangeEvent(transitionValue, MediatorConstants.BACKGROUND_TRANSITION_SECONDS);
	}

	private void durationChanged() {
		if(durationValue == duration.getValue()) return;
		
		durationValue = duration.getValue();
		
		fireReceiverChangeEvent(durationValue, MediatorConstants.BACKGROUND_DURATION_SECONDS);
	}

	private void pictureClicked(MouseEvent e) {
		if(e.getButton() != MouseButton.PRIMARY) return;
		
		String file = pictureName;
		
		if(file == null || file.trim().length() == 0) file = "./";
		
		File picFile = new File(file);
		FileChooser fc = new FileChooser();
		fc.setTitle("Choose static picture");
		fc.setInitialDirectory(picFile.getParentFile());
		fc.setInitialFileName(picFile.getName());
		
		File newPic = fc.showOpenDialog(null);
		
		if(newPic == null) return;
		
		pictureName = newPic.getAbsolutePath();
		
		picture.setText(newPic.getName());
		
		fireReceiverChangeEvent(pictureName, MediatorConstants.BACKGROUND_PIC);
	}

	private void directoryClicked(MouseEvent e) {
		if(e.getButton() != MouseButton.PRIMARY) return;
		
		DirectoryChooser dc = new DirectoryChooser();
		
		dc.setTitle("Choose picture directory");
		dc.setInitialDirectory(new File(directoryName));
		File newDir = dc.showDialog(null);
		
		if(newDir == null) return;
		
		directoryName = newDir.getAbsolutePath();

		directory.setText(newDir.getName());
		
		fireReceiverChangeEvent(directoryName, MediatorConstants.BACKGROUND_PIC_DIR);
	}

	private void staticPicButtonPressed() {
		boolean pressed = staticPicture.isSelected();
		
		staticPicture.setText(pressed ? "Static" : "Dynamic");
		duration.setDisable(pressed);
		transition.setDisable(pressed);
		
		fireReceiverChangeEvent(pressed, MediatorConstants.STATIC_BACKGROUND);
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case STATIC_BACKGROUND:
					Platform.runLater(() -> staticPicture.setSelected(e.getBooleanValue()));
					break;
				case BACKGROUND_PIC:
					Platform.runLater(() -> picture.setText(e.getStringValue()));
					break;
				case BACKGROUND_PIC_DIR:
					Platform.runLater(() -> directory.setText(e.getStringValue()));
					break;
				case VARIABLE_BACKGROUND_PAUSE:
					break;
				case BACKGROUND_DURATION_SECONDS:
					durationValue = (int)e.getDoubleValue();
					Platform.runLater(() -> duration.getValueFactory().setValue(durationValue));
					break;
				case BACKGROUND_TRANSITION_SECONDS:
					transitionValue = (int)e.getDoubleValue();
					Platform.runLater(() -> transition.getValueFactory().setValue(transitionValue));
					break;
				default:
					break;
				}
			}

		});
	}

	private void fireReceiverChangeEvent(boolean value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	private void fireReceiverChangeEvent(String value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	private void fireReceiverChangeEvent(double value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

}
