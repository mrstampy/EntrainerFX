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
import static net.sourceforge.entrainer.mediator.MediatorConstants.ANIMATION_DESKTOP_BACKGROUND;
import static net.sourceforge.entrainer.mediator.MediatorConstants.ANIMATION_PROGRAM;

import java.io.File;
import java.net.URI;
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
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import net.sourceforge.entrainer.gui.jfx.animation.JFXAnimationRegister;
import net.sourceforge.entrainer.gui.jfx.animation.JFXEntrainerAnimation;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.xml.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class AnimationPane.
 */
public class AnimationPane extends AbstractTitledPane {

	private ComboBox<JFXEntrainerAnimation> animations = new ComboBox<JFXEntrainerAnimation>();
	private CheckBox animation = new CheckBox("Run Animations");
	private CheckBox useDesktopAsBackground = new CheckBox("Use Desktop Background");
	private TextField animationBackground = new TextField();
	private boolean comboBoxInited = false;
	private FlowPane fp;

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
		setSelected(selected, animation);
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
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				checkBox.setSelected(selected);
			}
		});
	}

	/**
	 * Sets the animation tool tip.
	 *
	 * @param toolTip
	 *          the new animation tool tip
	 */
	public void setAnimationToolTip(String toolTip) {
		setToolTip(toolTip, animation);
	}

	private void setToolTip(final String toolTip, final Control node) {
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				node.setTooltip(new Tooltip(toolTip));
			}
		});
	}

	/**
	 * Gets the animation.
	 *
	 * @return the animation
	 */
	public CheckBox getAnimation() {
		return animation;
	}

	public CheckBox getUseDesktopAsBackground() {
		return useDesktopAsBackground;
	}

	protected void init() {
		initMediator();
		initGui();
		super.init();
	}

	private void initGui() {
		animationBackground.setEditable(false);
		animationBackground.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				if (arg0.getClickCount() != 2) return;
				showImageChooser();
			}
		});

		animations.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				JFXEntrainerAnimation val = animations.getValue();
				if (val != null) {
					fireAnimationSelection(val.toString());
					setBackgroundFields(val);
				}
			}
		});

		useDesktopAsBackground.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				fireDesktopBackground();
				if (useDesktopAsBackground.isSelected()) {
					fireBackgroundSelection(null);
					animationBackground.setText("");
				} else {
					showImageChooser();
				}
			}
		});
		
		setTextFill(useDesktopAsBackground);

		initCheckBox(animation, MediatorConstants.IS_ANIMATION);

		fp = new FlowPane();
		fp.setPadding(new Insets(10));
		fp.setHgap(10);
		fp.setVgap(10);

		fp.getChildren().add(animations);
		fp.getChildren().add(getVBox());

		expandedProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable arg0) {
				refreshAnimations();
			}
		});
		
		fp.setAlignment(Pos.CENTER);
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

	private void initEntrainerAnimations() {
		if (comboBoxInited) return;

		// Resize the combo box
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				initEntrainerAnimation(Settings.getInstance().getAnimationProgram());
				useDesktopAsBackground.setSelected(Settings.getInstance().isDesktopBackground());
				initAnimationBackground(Settings.getInstance().getAnimationBackground());
				animations.show();
				animations.hide();
				comboBoxInited = true;
			}
		});
	}

	private Node getVBox() {
		VBox vbox = new VBox();
		vbox.setSpacing(10);
		vbox.getChildren().add(animation);
		vbox.getChildren().add(useDesktopAsBackground);
		vbox.getChildren().add(getAnimationBackground());

		return vbox;
	}

	private HBox getAnimationBackground() {
		HBox hbox = new HBox();
		Label label = new Label("Select Background Picture");
		setTextFill(label);
		HBox.setMargin(label, new Insets(0, 5, 5, 5));

		hbox.getChildren().add(label);
		hbox.getChildren().add(animationBackground);

		return hbox;
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

		if (f != null) {
			URI imageFile = f.toURI();
			String externalForm = imageFile.toString();
			fireBackgroundSelection(externalForm);
			initAnimationBackground(externalForm);
		} else {
			useDesktopAsBackground.setSelected(true);
		}
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(final ReceiverChangeEvent e) {
				switch (e.getParm()) {

				case ANIMATION_BACKGROUND:
					initEntrainerAnimations();
					initAnimationBackground(e.getStringValue());
					break;
				case ANIMATION_DESKTOP_BACKGROUND:
					initEntrainerAnimations();
					JFXUtils.runLater(new Runnable() {

						@Override
						public void run() {
							useDesktopAsBackground.setSelected(e.getBooleanValue());
						}
					});
					break;
				case ANIMATION_PROGRAM:
					initEntrainerAnimations();
					initEntrainerAnimation(e.getStringValue());
					break;
				case START_ENTRAINMENT:
					JFXUtils.runLater(new Runnable() {

						@Override
						public void run() {
							getAnimation().setDisable(e.getBooleanValue());
						}
					});
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
			useDesktopAsBackground.setSelected(false);
			animationBackground.setDisable(true);
			return;
		}

		if (selectedItem.useDesktopBackground()) {
			useDesktopAsBackground.setSelected(true);
			animationBackground.setDisable(false);
			animationBackground.setText("");
		} else {
			useDesktopAsBackground.setSelected(false);
			animationBackground.setDisable(true);
		}
	}

	private void initAnimationBackground(final String s) {
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				useDesktopAsBackground.setSelected(null == s);
				if (!useDesktopAsBackground.isSelected()) {
					animationBackground.setText(s);
				}
			}
		});
	}

	private void fireDesktopBackground() {
		fireReceiverChangeEvent(useDesktopAsBackground.isSelected(), ANIMATION_DESKTOP_BACKGROUND);
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

	@Override
	protected Node getContentPane() {
		return fp;
	}

}
