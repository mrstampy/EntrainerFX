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

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.sourceforge.entrainer.gui.flash.FlashType;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class FlashOptionsPane.
 */
public class FlashOptionsPane extends AbstractTitledPane {

	private CheckBox flashBackground = new CheckBox("Flash Background");

	// additive effects
	private CheckBox opacity = new CheckBox("Opacity");
	private CheckBox bloom = new CheckBox("Bloom");
	private CheckBox boxBlur = new CheckBox("Box Blur");
	private CheckBox gaussianBlur = new CheckBox("Gaussian Blur");
	private CheckBox glow = new CheckBox("Glow");
	private CheckBox motionBlur = new CheckBox("Motion Blur");
	private CheckBox sepiaTone = new CheckBox("Sepia Tone");
	private CheckBox shadow = new CheckBox("Shadow");

	// non-additive effects
	private CheckBox lighting = new CheckBox("Lighting");
	private CheckBox colourAdjust = new CheckBox("Random Colour Adjust");

	private GridPane contentPane = new GridPane();

	private HBox options = new HBox();

	private GridPane additives;

	/**
	 * Instantiates a new flash options pane.
	 */
	public FlashOptionsPane() {
		super("Flash Options");
		init();
	}

	/**
	 * Checks if is flash background.
	 *
	 * @return true, if is flash background
	 */
	public boolean isFlashBackground() {
		return flashBackground.isSelected();
	}

	/**
	 * Sets the flash background.
	 *
	 * @param b
	 *          the new flash background
	 */
	public void setFlashBackground(boolean b) {
		flashBackgroundEvent(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.gui.jfx.AbstractTitledPane#init()
	 */
	protected void init() {
		initMediator();
		setCheckboxFills();
		setTooltips();
		setEventHandlers();

		initLayout();

		super.init();
	}

	private void setCheckboxFills() {
		setTextFill(flashBackground);
		setTextFill(opacity);
		setTextFill(bloom);
		setTextFill(boxBlur);
		setTextFill(gaussianBlur);
		setTextFill(glow);
		setTextFill(motionBlur);
		setTextFill(sepiaTone);
		setTextFill(shadow);
		setTextFill(lighting);
		setTextFill(colourAdjust);
	}

	private void initLayout() {
		contentPane.setPadding(new Insets(10));
		contentPane.setAlignment(Pos.CENTER);

		Node options = getOptions();

		GridPane.setConstraints(flashBackground, 0, 0, 1, 1, HPos.LEFT, VPos.CENTER);
		GridPane.setConstraints(options, 0, 1);
		GridPane.setMargin(flashBackground, new Insets(10, 10, 20, 0));

		contentPane.getChildren().addAll(flashBackground, options);
	}

	private Node getOptions() {
		options.getChildren().addAll(getAdditiveOptions(), getNonAdditiveOptions());
		options.setDisable(true);
		options.setAlignment(Pos.CENTER);

		return options;
	}

	private Node getAdditiveOptions() {
		Label title = new Label("Additive Options");
		setTextFill(title);

		Insets insets = new Insets(10);

		additives = new GridPane();

		int row = 0, col = 0;

		GridPane.setConstraints(title, col, row, 2, 1);
		GridPane.setHalignment(title, HPos.CENTER);

		row++;

		GridPane.setConstraints(opacity, col++, row);
		GridPane.setConstraints(bloom, col, row);

		col = 0;
		row++;

		GridPane.setConstraints(boxBlur, col++, row);
		GridPane.setConstraints(gaussianBlur, col, row);

		col = 0;
		row++;

		GridPane.setConstraints(glow, col++, row);
		GridPane.setConstraints(motionBlur, col, row);

		col = 0;
		row++;

		GridPane.setConstraints(sepiaTone, col++, row);
		GridPane.setConstraints(shadow, col, row);

		GridPane.setMargin(opacity, insets);
		GridPane.setMargin(bloom, insets);
		GridPane.setMargin(boxBlur, insets);
		GridPane.setMargin(gaussianBlur, insets);
		GridPane.setMargin(glow, insets);
		GridPane.setMargin(motionBlur, insets);
		GridPane.setMargin(sepiaTone, insets);
		GridPane.setMargin(shadow, insets);

		additives.getChildren().addAll(title, opacity, bloom, boxBlur, gaussianBlur, glow, motionBlur, sepiaTone, shadow);

		return additives;
	}

	private Node getNonAdditiveOptions() {
		Label title = new Label("Non Additive Options");
		setTextFill(title);

		VBox box = new VBox(10, title, new HBox(10, colourAdjust, lighting));

		box.setAlignment(Pos.TOP_CENTER);

		return box;
	}

	private void setTooltips() {
		setTooltip(flashBackground, "Flash Background Image at the Chosen Entrainment Frequency");
		setTooltip(opacity, "Use the image opacity for flashing");
		setTooltip(bloom, "Bloom effect for flashing");
		setTooltip(boxBlur, "Box Blur effect for flashing");
		setTooltip(gaussianBlur, "Gaussian Blur effect for flashing");
		setTooltip(glow, "Glow effect for flashing");
		setTooltip(lighting, "Lighting effect for flashing");
		setTooltip(motionBlur, "Motion Blur effect for flashing");
		setTooltip(sepiaTone, "Sepia Tone effect for flashing");
		setTooltip(shadow, "Shadow effect for flashing");
		setTooltip(colourAdjust, "Random Colour Adjust effect for flashing");
	}

	private void setTooltip(Control node, String tip) {
		node.setTooltip(new Tooltip(tip));
	}

	private void setEventHandlers() {
		flashBackground.setOnAction(e -> flashBackgroundClicked());
		opacity.setOnAction(e -> opacityClicked());
		bloom.setOnAction(e -> bloomClicked());
		boxBlur.setOnAction(e -> boxBlurClicked());
		gaussianBlur.setOnAction(e -> gaussianBlurClicked());
		glow.setOnAction(e -> glowClicked());
		lighting.setOnAction(e -> lightingClicked());
		motionBlur.setOnAction(e -> motionBlurClicked());
		sepiaTone.setOnAction(e -> sepiaToneClicked());
		shadow.setOnAction(e -> shadowClicked());
		colourAdjust.setOnAction(e -> colourAdjustClicked());
	}

	private void opacityClicked() {
		fireEvent(FlashType.OPACITY, opacity.isSelected());
	}

	private void bloomClicked() {
		fireEvent(FlashType.BLOOM, bloom.isSelected());
	}

	private void boxBlurClicked() {
		fireEvent(FlashType.BOX_BLUR, boxBlur.isSelected());
	}

	private void gaussianBlurClicked() {
		fireEvent(FlashType.GAUSSIAN_BLUR, gaussianBlur.isSelected());
	}

	private void glowClicked() {
		fireEvent(FlashType.GLOW, glow.isSelected());
	}

	private void lightingClicked() {
		fireEvent(FlashType.LIGHTING, lighting.isSelected());
		disableAdditives();
	}

	private void motionBlurClicked() {
		fireEvent(FlashType.MOTION_BLUR, motionBlur.isSelected());
	}

	private void sepiaToneClicked() {
		fireEvent(FlashType.SEPIA_TONE, sepiaTone.isSelected());
	}

	private void shadowClicked() {
		fireEvent(FlashType.SHADOW, shadow.isSelected());
	}

	private void colourAdjustClicked() {
		fireEvent(FlashType.COLOUR_ADJUST, colourAdjust.isSelected());
		disableAdditives();
	}

	private void fireEvent(FlashType type, boolean b) {
		fireReceiverChangeEvent(type, b, MediatorConstants.FLASH_TYPE);
	}

	private void flashBackgroundClicked() {
		boolean b = flashBackground.isSelected();

		fireReceiverChangeEvent(b, MediatorConstants.FLASH_BACKGROUND);

		options.setDisable(!b);
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case FLASH_BACKGROUND:
					if (flashBackground.isSelected() == e.getBooleanValue()) return;
					JFXUtils.runLater(() -> flashBackgroundEvent(e.getBooleanValue()));
					break;
				case FLASH_TYPE:
					JFXUtils.runLater(() -> evaluateFlashType(((FlashType) e.getOption()), e.getBooleanValue()));
					break;
				default:
					break;
				}
			}

		});
	}

	private void evaluateFlashType(FlashType flashType, boolean b) {
		switch (flashType) {
		case BLOOM:
			bloom.setSelected(b);
			break;
		case BOX_BLUR:
			boxBlur.setSelected(b);
			break;
		case COLOUR_ADJUST:
			colourAdjustEvent(b);
			break;
		case GAUSSIAN_BLUR:
			gaussianBlur.setSelected(b);
			break;
		case GLOW:
			glow.setSelected(b);
			break;
		case LIGHTING:
			lightingEvent(b);
			break;
		case MOTION_BLUR:
			motionBlur.setSelected(b);
			break;
		case OPACITY:
			opacity.setSelected(b);
			break;
		case SEPIA_TONE:
			sepiaTone.setSelected(b);
			break;
		case SHADOW:
			shadow.setSelected(b);
			break;
		default:
			break;
		}
	}

	private void lightingEvent(boolean b) {
		lighting.setSelected(b);

		disableAdditives();
	}

	private void colourAdjustEvent(boolean b) {
		colourAdjust.setSelected(b);

		disableAdditives();
	}

	private void disableAdditives() {
		boolean b = lighting.isSelected() || colourAdjust.isSelected();

		bloom.setDisable(b);
		boxBlur.setDisable(b);
		gaussianBlur.setDisable(b);
		glow.setDisable(b);
		motionBlur.setDisable(b);
		sepiaTone.setDisable(b);
		shadow.setDisable(b);

		colourAdjust.setDisable(lighting.isSelected());
		lighting.setDisable(colourAdjust.isSelected());
	}

	private void flashBackgroundEvent(boolean b) {
		flashBackground.setSelected(b);
		options.setDisable(!b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.gui.jfx.AbstractTitledPane#getContentPane()
	 */
	@Override
	protected Node getContentPane() {
		return contentPane;
	}

}
