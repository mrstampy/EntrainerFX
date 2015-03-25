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
package net.sourceforge.entrainer.gui.jfx.animation;

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;

import javafx.embed.swing.JFXPanel;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import javax.swing.JWindow;

import jfxtras.labs.util.Util;
import net.sourceforge.entrainer.gui.EntrainerFX;
import net.sourceforge.entrainer.gui.flash.CurrentEffect;
import net.sourceforge.entrainer.gui.jfx.JFXUtils;
import net.sourceforge.entrainer.gui.jfx.shimmer.ShimmerPaintUtils;
import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.xml.Settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * This class draws itself to cover the entire screen. It provides the canvas
 * upon which animations are drawn.
 * 
 * @author burton
 */
public class JFXAnimationWindow extends JWindow {
	private static final Logger log = LoggerFactory.getLogger(JFXAnimationWindow.class);

	private static final long serialVersionUID = 1L;

	private WritableImage background;
	private JFXEntrainerAnimation entrainerAnimation;
	private int yOffset;
	private Image customImage = null;

	private JFXPanel mainPanel = new JFXPanel();
	private Canvas canvas = new Canvas();

	private boolean isAnimating;

	private Runnable animator;

	/** The flash animation. */
	protected boolean flashAnimation;

	/** The started. */
	protected boolean started;

	private boolean colourBackground;

	/**
	 * Instantiates a new JFX animation window.
	 */
	public JFXAnimationWindow() {
		super();
		initEntrainerAnimation();
		setYOffset();
		initGui();
		initMediator();

		animator = new Runnable() {

			@Override
			public void run() {
				GraphicsContext gc = canvas.getGraphicsContext2D();
				drawBackground(gc);
				getEntrainerAnimation().animate(gc);
			}

			private void drawBackground(GraphicsContext gc) {
				if (!getEntrainerAnimation().useBackgroundColour()) {
					if (colourBackground || getCustomImage() == null) {
						gc.drawImage(background, 0, 0);
					} else {
						gc.drawImage(getCustomImage(), 0, 0);
					}
				} else {
					if (getEntrainerAnimation().getCustomImage() != null) {
						gc.drawImage(getEntrainerAnimation().getCustomImage(), 0, 0);
					}
				}
			}
		};
		canvas.setCache(true);
		canvas.setCacheHint(CacheHint.SPEED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Window#setVisible(boolean)
	 */
	public void setVisible(boolean b) {
		if (!b) {
			getEntrainerAnimation().clearAnimation();
		} else {
			initDefaultBackground();
		}
		super.setVisible(b);
	}

	private void paint() {
		JFXUtils.runLater(animator);
	}

	/**
	 * Gets the entrainer animation.
	 *
	 * @return the entrainer animation
	 */
	public JFXEntrainerAnimation getEntrainerAnimation() {
		return entrainerAnimation;
	}

	/**
	 * Sets the entrainer animation.
	 *
	 * @param entrainerAnimation
	 *          the new entrainer animation
	 */
	public void setEntrainerAnimation(JFXEntrainerAnimation entrainerAnimation) {
		if (this.entrainerAnimation != null) {
			this.entrainerAnimation.clearAnimation();
		}

		this.entrainerAnimation = entrainerAnimation;

		if (null != entrainerAnimation && entrainerAnimation.useBackgroundColour()) {
			setBackground(JFXUtils.fromJFXColor(entrainerAnimation.getBackgroundColour()));
		}
	}

	private void initEntrainerAnimation() {
		initEntrainerAnimation(Settings.getInstance().getAnimationProgram());
		initAnimationBackground(Settings.getInstance().getAnimationBackground());
	}

	private void initEntrainerAnimation(String stringRep) {
		List<JFXEntrainerAnimation> animations = JFXAnimationRegister.getEntrainerAnimations();
		if (null == stringRep && !animations.isEmpty()) {
			setEntrainerAnimation(animations.get(0));
		} else if (null == getEntrainerAnimation()) {
			setEntrainerAnimation(JFXAnimationRegister.getEntrainerAnimation(stringRep));
		} else if (!stringRep.equals(getEntrainerAnimation().toString())) {
			setEntrainerAnimation(JFXAnimationRegister.getEntrainerAnimation(stringRep));
		}
	}

	private void initGui() {
		Dimension size = getScreenSize();
		setSize(size);

		canvas.setWidth(size.getWidth());
		canvas.setHeight(size.getHeight());

		setLocation(new Point(0, getYOffset()));
		initDefaultBackground();

		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				Group group = new Group();
				group.getChildren().add(canvas);
				Scene scene = new Scene(group);
				mainPanel.setScene(scene);
			}
		});

		getContentPane().add(mainPanel);
	}

	private Dimension getScreenSize() {
		return GuiUtil.getWorkingVirtualScreenSize();
	}

	private void initDefaultBackground() {
		if (background != null) return;
		background = createColourBackground();
	}

	private WritableImage createColourBackground() {
		Dimension size = getScreenSize();
		Image image = Util.createBrushedMetalImage(size.getWidth(), size.getHeight(), ShimmerPaintUtils.generateColor(1));

		return new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight());
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this, true) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				MediatorConstants parm = e.getParm();
				switch (parm) {

				case ANIMATION_BACKGROUND:
					initAnimationBackground(e.getStringValue());
					break;
				case ANIMATION_PROGRAM:
					initEntrainerAnimation(e.getStringValue());
					break;
				case START_ENTRAINMENT:
					started = e.getBooleanValue();
					showAnimation();
					break;
				case IS_ANIMATION:
					isAnimating = e.getBooleanValue();
					showAnimation();
					break;
				case FLASH_EFFECT:
					if (!isVisible()) break;
					pulseReceived(e.getEffect());
					break;
				case ENTRAINMENT_FREQUENCY_PULSE:
					if (!isVisible()) break;
					if (e.getBooleanValue() && runAnimation()) {
						paint();
					} else {
						getEntrainerAnimation().clearAnimation();
						evaluateFlash(false);
					}
					break;
				case APPLY_FLASH_TO_ANIMATION:
					evaluateFlash(e.getBooleanValue());
					break;
				case ANIMATION_COLOR_BACKGROUND:
					initColourBackground(e.getBooleanValue());
					break;
				default:
					break;

				}

			}
		});
	}

	private boolean runAnimation() {
		return entrainerAnimation != null && isAnimating && started;
	}

	private void initColourBackground(boolean b) {
		colourBackground = b;
		if (background != null) return;
		background = b ? createColourBackground() : null;
	}

	private void evaluateFlash(boolean b) {
		flashAnimation = b;

		if (!flashAnimation) JFXUtils.resetEffects(canvas);
	}

	private void pulseReceived(CurrentEffect currentEffect) {
		if (!isAnimating || !flashAnimation) return;

		JFXUtils.setEffect(canvas, currentEffect);
	}

	private void initAnimationBackground(String animationBackground) {
		if (animationBackground == null || animationBackground.trim().length() == 0) {
			setCustomImage(null);
		} else {
			Image image = new Image(animationBackground, getScreenSize().getWidth(), getScreenSize().getHeight(), false, true);
			try {
				setCustomImage(new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight()));
			} catch (Exception e) {
				log.error("Unexpected exception for image {}", animationBackground, e);
				GuiUtil.handleProblem(e);
			}
		}
	}

	private int getYOffset() {
		return yOffset;
	}

	private void setYOffset() {
		if (isMac()) {
			setYOffset(22);
		} else {
			setYOffset(0);
		}
	}

	private boolean isMac() {
		return System.getProperty("os.name").contains("Mac");
	}

	private void setYOffset(int offset) {
		yOffset = offset;
	}

	private Image getCustomImage() {
		return customImage;
	}

	private void setCustomImage(WritableImage backgroundImage) {
		this.customImage = backgroundImage;
	}

	private void showAnimation() {
		boolean b = runAnimation();
		if (b == isVisible()) return;

		JFXUtils.runLater(() -> showAnimation(b));
	}

	private void showAnimation(boolean b) {
		setVisible(b);

		if (b) {
			EntrainerFX.getInstance().toFront();
		} else {
			getEntrainerAnimation().clearAnimation();
			Thread thread = new Thread("Animation colour background change") {
				public void run() {
					background = createColourBackground();
				}
			};
			
			thread.start();
		}
	}

}
