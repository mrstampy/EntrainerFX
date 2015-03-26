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

import static net.sourceforge.entrainer.mediator.MediatorConstants.ANIMATION_BACKGROUND;
import static net.sourceforge.entrainer.mediator.MediatorConstants.ANIMATION_PROGRAM;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ListIterator;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import net.sourceforge.entrainer.gui.jfx.animation.JFXAnimationRegister;
import net.sourceforge.entrainer.gui.jfx.animation.JFXEntrainerAnimation;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class AnimationPane.
 */
public class AnimationPane extends AbstractTitledPane {
	private static final Logger log = LoggerFactory.getLogger(AnimationPane.class);

	private ComboBox<JFXEntrainerAnimation> animations = new ComboBox<JFXEntrainerAnimation>();
	private CheckBox runAnimation = new CheckBox("Run Animations");
	private CheckBox useColourAsBackground = new CheckBox("Use Colour Background");
	private TextField animationBackground = new TextField();
	private GridPane pane;
	private URI imageFile;

	/**
	 * Instantiates a new animation pane.
	 */
	public AnimationPane() {
		super("Animation Options");
		init();
	}

	/**
	 * Sets the animation selected.
	 *
	 * @param selected
	 *          the new animation selected
	 */
	public void setAnimationSelected(boolean selected) {
		setSelected(selected, runAnimation);
	}

	/**
	 * Gets the selected animation name.
	 *
	 * @return the selected animation name
	 */
	public String getSelectedAnimationName() {
		JFXEntrainerAnimation selected = animations.getValue();

		return selected == null ? null : selected.toString();
	}

	/**
	 * Gets the animation background picture.
	 *
	 * @return the animation background picture
	 */
	public String getAnimationBackgroundPicture() {
		return animationBackground.getText();
	}

	private void setSelected(final boolean selected, final CheckBox checkBox) {
		if (checkBox.isSelected() == selected) return;
		JFXUtils.runLater(() -> checkBox.setSelected(selected));
	}

	private void setToolTips() {
		setTooltip(runAnimation, "Run Animation During Entrainment Session");
		setTooltip(useColourAsBackground, "If true will use a random colour as the animation background");
		setTooltip(animationBackground, "Click to select a background image for animations");
		setTooltip(animations, "The list of available animations");
	}

	/**
	 * Gets the animation.
	 *
	 * @return the animation
	 */
	public CheckBox getRunAnimation() {
		return runAnimation;
	}

	/**
	 * Gets the use desktop as background.
	 *
	 * @return the use desktop as background
	 */
	public CheckBox getUseColourAsBackground() {
		return useColourAsBackground;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.gui.jfx.AbstractTitledPane#init()
	 */
	protected void init() {
		initMediator();
		initGui();
		setToolTips();
		super.init();
	}

	private void initGui() {
		animationBackground.setEditable(false);
		animationBackground.setOnMouseClicked(e -> showImageChooser());

		animations.setOnAction(e -> animationSelected());
		useColourAsBackground.setOnAction(e -> useColourClicked());

		setTextFill(useColourAsBackground);

		initCheckBox(runAnimation, MediatorConstants.IS_ANIMATION);

		pane = new GridPane();
		pane.setPadding(new Insets(10));
		pane.setAlignment(Pos.CENTER);
		pane.setHgap(10);
		pane.setVgap(10);

		GridPane.setConstraints(runAnimation, 0, 0);
		GridPane.setConstraints(useColourAsBackground, 0, 1);
		GridPane.setConstraints(animations, 1, 0);
		GridPane.setConstraints(animationBackground, 1, 1);

		pane.getChildren().addAll(runAnimation, animations, useColourAsBackground, animationBackground);

		expandedProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable arg0) {
				refreshAnimations();
			}
		});

		pane.setAlignment(Pos.CENTER);
	}

	/**
	 * Refresh animations.
	 */
	public void refreshAnimations() {
		final List<JFXEntrainerAnimation> all = JFXAnimationRegister.getEntrainerAnimations();

		List<JFXEntrainerAnimation> bag = animations.getItems();

		JFXEntrainerAnimation selected = animations.getValue();
		String name = selected == null ? null : selected.toString();

		for (JFXEntrainerAnimation animation : all) {
			if (!bag.contains(animation)) {
				removeOldAnimation(animation, bag);
				bag.add(animation);
			}
		}

		if (name != null) animations.setValue(JFXAnimationRegister.getEntrainerAnimation(name));

		if (all.isEmpty()) {
			throw new RuntimeException("Could not load animations");
		}
	}

	private void removeOldAnimation(JFXEntrainerAnimation animation, List<JFXEntrainerAnimation> bag) {
		ListIterator<JFXEntrainerAnimation> it = bag.listIterator();
		while (it.hasNext()) {
			JFXEntrainerAnimation existing = it.next();
			if (existing.toString().equals(animation.toString()) && existing != animation) {
				it.remove();
				it.add(animation);
			}
		}
	}

	/**
	 * Show image chooser.
	 */
	protected void showImageChooser() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Choose Background Image for Animation");
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.gif",
				"*.bmp", "*.png"));

		File f = chooser.showOpenDialog(null);

		if (f == null && imageFile == null) {
			useColourAsBackground.setSelected(true);
		} else if (f != null) {
			String externalForm = f.toURI().toString();
			fireBackgroundSelection(externalForm);
			initAnimationBackground(externalForm);
		}
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(final ReceiverChangeEvent e) {
				switch (e.getParm()) {

				case ANIMATION_BACKGROUND:
					initAnimationBackground(e.getStringValue());
					break;
				case ANIMATION_COLOR_BACKGROUND:
					JFXUtils.runLater(() -> useColourAsBackground.setSelected(e.getBooleanValue()));
					break;
				case ANIMATION_PROGRAM:
					initEntrainerAnimation(e.getStringValue());
					break;
				case IS_ANIMATION:
					setAnimationSelected(e.getBooleanValue());
					break;
				default:
					break;

				}

			}
		});
	}

	private void initEntrainerAnimation(final String animationProgram) {
		if (animationProgram != null && animationProgram.trim().length() > 0) {
			JFXUtils.runLater(new Runnable() {

				@Override
				public void run() {
					animations.setValue(JFXAnimationRegister.getEntrainerAnimation(animationProgram));
					setBackgroundFields(animations.getValue());
				}
			});
		}
	}

	private void setBackgroundFields(JFXEntrainerAnimation selectedItem) {
		if (selectedItem.useBackgroundColour()) {
			useColourAsBackground.setSelected(false);
			animationBackground.setDisable(true);
			return;
		}

		if (selectedItem.useDesktopBackground()) {
			useColourAsBackground.setSelected(true);
			animationBackground.setDisable(false);
		} else {
			useColourAsBackground.setSelected(false);
			animationBackground.setDisable(true);
		}
	}

	private void initAnimationBackground(final String s) {
		JFXUtils.runLater(() -> setBackgroundFile(s));
	}

	private void setBackgroundFile(String s) {
		if (s == null) {
			imageFile = null;
			animationBackground.setText("");
			return;
		}

		try {
			imageFile = new URI(s);
			File f = new File(imageFile.isAbsolute() ? imageFile : imageFile.normalize());
			animationBackground.setText(f.getName());
		} catch (URISyntaxException e) {
			log.error("Unexpected exception", e);
		}
	}

	private void fireDesktopBackground() {
		fireReceiverChangeEvent(useColourAsBackground.isSelected(), MediatorConstants.ANIMATION_COLOR_BACKGROUND);
	}

	private void fireAnimationSelection(String name) {
		fireReceiverChangeEvent(name, ANIMATION_PROGRAM);
	}

	private void fireBackgroundSelection(String name) {
		fireReceiverChangeEvent(name, ANIMATION_BACKGROUND);
	}

	private void initCheckBox(final CheckBox checkBox, final MediatorConstants parm) {
		checkBox.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				fireReceiverChangeEvent(checkBox.isSelected(), parm);
			}
		});
		setTextFill(checkBox);
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

	private void animationSelected() {
		JFXEntrainerAnimation val = animations.getValue();
		if (val != null) {
			fireAnimationSelection(val.toString());
			setBackgroundFields(val);
		}
	}

	private void useColourClicked() {
		fireDesktopBackground();
		if (!useColourAsBackground.isSelected()) {
			if (imageFile == null) {
				showImageChooser();
			} else {
				fireBackgroundSelection(imageFile.toString());
			}
		}
	}

}
