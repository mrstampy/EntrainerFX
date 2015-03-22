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
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
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
	
	private CheckBox applyBackground = new CheckBox("Background");
	private CheckBox applyShimmer = new CheckBox("Shimmer");
	private CheckBox applyAnimation = new CheckBox("Animation");

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

	/**
	 * Checks if is opacity.
	 *
	 * @return true, if is opacity
	 */
	public boolean isOpacity() {
		return opacity.isSelected();
	}

	/**
	 * Sets the opacity.
	 *
	 * @param b
	 *          the new opacity
	 */
	public void setOpacity(boolean b) {
		opacity.setSelected(b);
	}

	/**
	 * Checks if is bloom.
	 *
	 * @return true, if is bloom
	 */
	public boolean isBloom() {
		return bloom.isSelected();
	}

	/**
	 * Sets the bloom.
	 *
	 * @param b
	 *          the new bloom
	 */
	public void setBloom(boolean b) {
		bloom.setSelected(b);
	}

	/**
	 * Checks if is box blur.
	 *
	 * @return true, if is box blur
	 */
	public boolean isBoxBlur() {
		return boxBlur.isSelected();
	}

	/**
	 * Sets the box blur.
	 *
	 * @param b
	 *          the new box blur
	 */
	public void setBoxBlur(boolean b) {
		boxBlur.setSelected(b);
	}

	/**
	 * Checks if is gaussian blur.
	 *
	 * @return true, if is gaussian blur
	 */
	public boolean isGaussianBlur() {
		return gaussianBlur.isSelected();
	}

	/**
	 * Sets the gaussian blur.
	 *
	 * @param b
	 *          the new gaussian blur
	 */
	public void setGaussianBlur(boolean b) {
		gaussianBlur.setSelected(b);
	}

	/**
	 * Checks if is glow.
	 *
	 * @return true, if is glow
	 */
	public boolean isGlow() {
		return glow.isSelected();
	}

	/**
	 * Sets the glow.
	 *
	 * @param b
	 *          the new glow
	 */
	public void setGlow(boolean b) {
		glow.setSelected(b);
	}

	/**
	 * Checks if is motion blur.
	 *
	 * @return true, if is motion blur
	 */
	public boolean isMotionBlur() {
		return motionBlur.isSelected();
	}

	/**
	 * Sets the motion blur.
	 *
	 * @param b
	 *          the new motion blur
	 */
	public void setMotionBlur(boolean b) {
		motionBlur.setSelected(b);
	}

	/**
	 * Checks if is sepia tone.
	 *
	 * @return true, if is sepia tone
	 */
	public boolean isSepiaTone() {
		return sepiaTone.isSelected();
	}

	/**
	 * Sets the sepia tone.
	 *
	 * @param b
	 *          the new sepia tone
	 */
	public void setSepiaTone(boolean b) {
		sepiaTone.setSelected(b);
	}

	/**
	 * Checks if is shadow.
	 *
	 * @return true, if is shadow
	 */
	public boolean isShadow() {
		return shadow.isSelected();
	}

	/**
	 * Sets the shadow.
	 *
	 * @param b
	 *          the new shadow
	 */
	public void setShadow(boolean b) {
		shadow.setSelected(b);
	}

	/**
	 * Checks if is lighting.
	 *
	 * @return true, if is lighting
	 */
	public boolean isLighting() {
		return lighting.isSelected();
	}

	/**
	 * Sets the lighting.
	 *
	 * @param b
	 *          the new lighting
	 */
	public void setLighting(boolean b) {
		lightingEvent(b);
	}

	/**
	 * Checks if is colour adjust.
	 *
	 * @return true, if is colour adjust
	 */
	public boolean isColourAdjust() {
		return colourAdjust.isSelected();
	}

	/**
	 * Sets the colour adjust.
	 *
	 * @param b
	 *          the new colour adjust
	 */
	public void setColourAdjust(boolean b) {
		colourAdjustEvent(b);
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
		setTextFill(applyBackground);
		setTextFill(applyShimmer);
		setTextFill(applyAnimation);
	}

	private void initLayout() {
		contentPane.setPadding(new Insets(10));
		contentPane.setAlignment(Pos.CENTER);

		Node options = getOptions();
		
		Node apply = getApply();

		GridPane.setConstraints(apply, 0, 0);
		GridPane.setConstraints(options, 0, 1);

		contentPane.getChildren().addAll(apply, options);
	}

	private Node getApply() {
		Label lbl = new Label("Apply to:");
		setTextFill(lbl);
		
		Insets value = new Insets(0, 0, 20, 0);
		
		setAppliesDisabled(true);
		
		VBox vbox = new VBox(10, applyBackground, applyShimmer, applyAnimation);
		VBox.setMargin(applyAnimation, value);

		vbox.setAlignment(Pos.CENTER_LEFT);
		
		HBox box = new HBox(10, flashBackground, lbl, vbox);
		HBox.setMargin(flashBackground, value);
		HBox.setMargin(lbl, new Insets(0, 0, 20, 40));
		
		box.setAlignment(Pos.CENTER);
		
		return box;
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
		setTooltip(applyBackground, "Apply chosen flash effect to the background");
		setTooltip(applyShimmer, "Apply the chosen flash effect to the shimmers");
		setTooltip(applyAnimation, "Apply the chosen flash effect to the animations");
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
		applyBackground.setOnAction(e -> applyBackgroundClicked());
		applyShimmer.setOnAction(e -> applyShimmerClicked());
		applyAnimation.setOnAction(e -> applyAnimationClicked());
	}

	private void applyAnimationClicked() {
		fireReceiverChangeEvent(applyAnimation.isSelected(), MediatorConstants.APPLY_FLASH_TO_ANIMATION);
	}

	private void applyShimmerClicked() {
		fireReceiverChangeEvent(applyShimmer.isSelected(), MediatorConstants.APPLY_FLASH_TO_SHIMMER);
	}

	private void applyBackgroundClicked() {
		fireReceiverChangeEvent(applyBackground.isSelected(), MediatorConstants.APPLY_FLASH_TO_BACKGROUND);
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
		
		setAppliesDisabled(!b);
	}

	private void setAppliesDisabled(boolean b) {
		applyBackground.setDisable(b);
		applyShimmer.setDisable(b);
		applyAnimation.setDisable(b);
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
				case APPLY_FLASH_TO_BACKGROUND:
					JFXUtils.runLater(() -> applyBackground.setSelected(e.getBooleanValue()));
					break;
				case APPLY_FLASH_TO_SHIMMER:
					JFXUtils.runLater(() -> applyShimmer.setSelected(e.getBooleanValue()));
					break;
				case APPLY_FLASH_TO_ANIMATION:
					JFXUtils.runLater(() -> applyAnimation.setSelected(e.getBooleanValue()));
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
		setAppliesDisabled(!b);
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
