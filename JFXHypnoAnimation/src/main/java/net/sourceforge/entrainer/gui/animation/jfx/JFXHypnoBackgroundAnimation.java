/*
 *      ______      __             _                 _______  __
 *     / ____/___  / /__________ _(_)___  ___  _____/ ____/ |/ /
 *    / __/ / __ \/ __/ ___/ __ `/ / __ \/ _ \/ ___/ /_   |   / 
 *   / /___/ / / / /_/ /  / /_/ / / / / /  __/ /  / __/  /   |  
 *  /_____/_/ /_/\__/_/   \__,_/_/_/ /_/\___/_/  /_/    /_/|_|  
 *                                                          
 *
 * Copyright (C) 2008 - 2016 Burton Alexander
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
package net.sourceforge.entrainer.gui.animation.jfx;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import net.sourceforge.entrainer.gui.jfx.animation.AnimationRectangle2D;
import net.sourceforge.entrainer.gui.jfx.animation.JFXEntrainerAnimation;

/**
 * This animation paints a flashing colour in the background (analogous to
 * {@link FlashPanel}) and paints a rotating, changing GradientPaint with
 * transparency over top.
 * 
 * @author burton
 */
public class JFXHypnoBackgroundAnimation extends JFXEntrainerAnimation {

	private boolean hasAdded = false;
	private Color hypnoBackgroundColour = new Color(1, 1, 200.0 / 255.0, 1);

	public JFXHypnoBackgroundAnimation() {
		super();
		setBackgroundColour(Color.WHITE);
	}

	public void clearAnimation() {
		super.clearAnimation();
		hasAdded = false;
	}

	@Override
	protected AnimationRectangle2D getNewAnimationRectangle2D(Point2D position) {
		HypnoBackgroundRectangle background = new HypnoBackgroundRectangle(0, 0, getScreenSize().getWidth(),
				getScreenSize().getHeight());

		background.setPaint(hypnoBackgroundColour);
		background.setGradientXPos(getScreenSize().getWidth() * getRandomPositiveDouble());
		background.setGradientYPos(getScreenSize().getHeight());
		background.setGradientFactor(getRandomDouble() * 10);
		background.setGradientPaint(new LinearGradient(0, 0, background.getWidth(), background.getHeight(), true,
				CycleMethod.NO_CYCLE, new Stop(0, getRandomColourAndAlpha()), new Stop(1, getRandomColourAndAlpha())));

		return background;
	}

	@Override
	public void maybeAddNewAnimationRectangle() {
		if (!hasAdded && getCount() == 0) {
			add(getNewAnimationRectangle2D(null));
			hasAdded = true;
		}
	}

	@Override
	protected void move(GraphicsContext g2d, AnimationRectangle2D shape) {
		HypnoBackgroundRectangle background = (HypnoBackgroundRectangle) shape;

		g2d.setFill(background.getPaint());
		g2d.fill();
		g2d.setFill(background.getGradientPaint());
		g2d.fillRect(background.getMinX(), background.getMinY(), background.getWidth(), background.getHeight());

		setNewPaint(background);
		setNewPaintGradientPaint(background);
	}

	@Override
	public String toString() {
		return "Hypno Background!!!";
	}

	@Override
	public boolean useBackgroundColour() {
		return true;
	}

	@Override
	public boolean useDesktopBackground() {
		return false;
	}

	private void setNewPaint(HypnoBackgroundRectangle background) {
		if (background.getPaint().equals(hypnoBackgroundColour)) {
			background.setPaint(getRandomColour());
		} else {
			background.setPaint(hypnoBackgroundColour);
		}
	}

	private void setNewPaintGradientPaint(HypnoBackgroundRectangle background) {
		Color color1 = background.getGradientPaint() == null ? getRandomColourAndAlpha() : background.getGradientPaint()
				.getStops().get(1).getColor();

		double xPos = (background.getGradientYPos() > 0 && background.getGradientYPos() < getScreenSize().getHeight() ? 0
				: background.getGradientXPos());
		double yPos = (background.getGradientXPos() > 0 && background.getGradientXPos() < getScreenSize().getWidth() ? 0
				: background.getGradientYPos());

		background.setGradientPaint(new LinearGradient(xPos, yPos, background.getWidth() - xPos, background.getHeight()
				- yPos, false, CycleMethod.NO_CYCLE, new Stop(0, color1), new Stop(1, getRandomColourAndAlpha())));

		setNewGradientPaintPosition(background);
	}

	private void setNewGradientPaintPosition(HypnoBackgroundRectangle background) {
		setNewGradientPaintXPosition(background);
		setNewGradientPaintYPosition(background);
	}

	private void setNewGradientPaintYPosition(HypnoBackgroundRectangle background) {
		if (isInYBounds(background)) {
			background.setGradientYPos(background.getGradientYPos() + background.getGradientFactor());
		}

		if (isInYBounds(background)) {
			return;
		}

		if (background.getGradientYPos() > getScreenSize().getHeight()) {
			background.setGradientYPos(getScreenSize().getHeight());
			if (background.getGradientXPos() == getScreenSize().getWidth()) {
				resetGradientFactor(background);
			}
		} else if (background.getGradientYPos() < 0) {
			background.setGradientYPos(0);
			if (background.getGradientXPos() == 0) {
				resetGradientFactor(background);
			}
		}

		background.setGradientXPos(background.getGradientXPos() + background.getGradientFactor());
	}

	private void setNewGradientPaintXPosition(HypnoBackgroundRectangle background) {
		if (isInXBounds(background)) {
			background.setGradientXPos(background.getGradientXPos() + background.getGradientFactor());
		}

		if (isInXBounds(background)) {
			return;
		}

		if (background.getGradientXPos() > getScreenSize().getWidth()) {
			background.setGradientXPos(getScreenSize().getWidth());
			if (background.getGradientYPos() == getScreenSize().getHeight()) {
				resetGradientFactor(background);
			}
		} else if (background.getGradientXPos() < 0) {
			background.setGradientXPos(0);
			if (background.getGradientYPos() == 0) {
				resetGradientFactor(background);
			}
		}

		background.setGradientYPos(background.getGradientYPos() + background.getGradientFactor());
	}

	private void resetGradientFactor(HypnoBackgroundRectangle background) {
		background.setGradientFactor(background.getGradientFactor() * -1);
	}

	private boolean isInXBounds(HypnoBackgroundRectangle background) {
		return background.getGradientXPos() < getScreenSize().getWidth() && background.getGradientXPos() > 0;
	}

	private boolean isInYBounds(HypnoBackgroundRectangle background) {
		return background.getGradientYPos() < getScreenSize().getHeight() && background.getGradientYPos() > 0;
	}

	private class HypnoBackgroundRectangle extends AnimationRectangle2D {
		private Paint paint;
		private LinearGradient gradientPaint;
		private double gradientXPos;
		private double gradientYPos;
		private double gradientFactor;

		public HypnoBackgroundRectangle(double x, double y, double w, double h) {
			super(x, y, w, h);
		}

		public Paint getPaint() {
			return paint;
		}

		public void setPaint(Paint paint) {
			this.paint = paint;
		}

		public LinearGradient getGradientPaint() {
			return gradientPaint;
		}

		public void setGradientPaint(LinearGradient gradientPaint) {
			this.gradientPaint = gradientPaint;
		}

		public double getGradientXPos() {
			return gradientXPos;
		}

		public void setGradientXPos(double gradientXPos) {
			this.gradientXPos = gradientXPos;
		}

		public double getGradientYPos() {
			return gradientYPos;
		}

		public void setGradientYPos(double gradientYPos) {
			this.gradientYPos = gradientYPos;
		}

		public double getGradientFactor() {
			return gradientFactor;
		}

		public void setGradientFactor(double factor) {
			this.gradientFactor = factor;
		}
	}

}
