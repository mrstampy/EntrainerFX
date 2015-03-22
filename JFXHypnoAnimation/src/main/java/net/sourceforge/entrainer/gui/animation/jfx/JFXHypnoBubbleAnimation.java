/*
 * Copyright (C) 2008 - 2013 Burton Alexander
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
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import net.sourceforge.entrainer.gui.jfx.animation.AnimationRectangle2D;
import net.sourceforge.entrainer.gui.jfx.animation.JFXEntrainerAnimation;

/**
 * This animation draws wobbly circles with GradientPaints, originating from the
 * center of the Entrainer frame, and 'popping' when they hit the screen edge.
 * 
 * @author burton
 */
public class JFXHypnoBubbleAnimation extends JFXEntrainerAnimation {

	public JFXHypnoBubbleAnimation() {
		super();
	}

	@Override
	protected AnimationRectangle2D getNewAnimationRectangle2D(Point2D position) {
		double sizeFactor = getRandomPositiveDouble();

		double diameter = sizeFactor * 200;
		if (diameter < 5) diameter = 5;

		HypnoBubble bubble = new HypnoBubble(position.getX() - (diameter / 2), position.getY() - (diameter / 2), diameter,
				diameter);

		bubble.setXSpeedFactor(getRandomDouble());
		bubble.setYSpeedFactor(getRandomDouble());
		bubble.setWobbleFactor(getRandomPositiveDouble());

		return bubble;
	}

	@Override
	protected void move(GraphicsContext g2d, AnimationRectangle2D shape) {
		HypnoBubble bubble = (HypnoBubble) shape;

		g2d.setFill(bubble.getPaint());
		g2d.fillOval(shape.getMinX(), shape.getMinY(), shape.getWidth(), shape.getHeight());

		modifyRectangle2D(bubble);
		modifyPosition(bubble);
		bubble.setPaint(getNewPaint(bubble));

		if (isAtScreenEdge(bubble)) {
			removeFromAnimation(bubble);
		}
	}

	public void maybeAddNewAnimationRectangle() {
		double f = getRandomPositiveDouble();
		if (f > 0.95) {
			addBubble(getCenterOfEntrainerFrame(), null);
		}
	}

	@Override
	public String toString() {
		return "Hypno Bubbles!!!";
	}

	private void modifyPosition(HypnoBubble bubble) {
		double speedX = bubble.getXSpeedFactor() * 5;
		double speedY = bubble.getYSpeedFactor() * 5;

		bubble.setCenterX(bubble.getCenterX() + speedX);
		bubble.setCenterY(bubble.getCenterY() + speedY);
	}

	private void modifyRectangle2D(HypnoBubble bubble) {
		double wobbleFactor = bubble.getWobbleFactor();
		double width = bubble.getWidth();
		double height = bubble.getHeight();

		if (bubble.isShrinkingWidth()) {
			if (width / height < 0.75) {
				bubble.setShrinkingWidth(false);
			} else {
				bubble.setRadiusX(bubble.getRadiusX() - wobbleFactor);
				bubble.setRadiusY(bubble.getRadiusY() + wobbleFactor);
			}
		} else {
			if (height / width < 0.75) {
				bubble.setShrinkingWidth(true);
			} else {
				bubble.setRadiusX(bubble.getRadiusX() + wobbleFactor);
				bubble.setRadiusY(bubble.getRadiusY() - wobbleFactor);
			}
		}
	}

	private void addBubble(Point2D newPosition, RadialGradient paint) {
		addBubble((HypnoBubble) getNewAnimationRectangle2D(newPosition), paint);
	}

	private void addBubble(HypnoBubble bubble, RadialGradient paint) {
		if (paint == null) {
			paint = getNewPaint(bubble);
		}

		bubble.setPaint(paint);
		add(bubble);
	}

	private RadialGradient getNewPaint(HypnoBubble bounds) {
		Color color1 = bounds.getPaint() == null ? getRandomColourAndAlpha() : bounds.getPaint().getStops().get(1)
				.getColor();

		Color color2 = bounds.getPaint() == null ? getRandomColourAndAlpha() : bounds.getPaint().getStops().get(2)
				.getColor();

		Color color3 = bounds.getPaint() == null ? getRandomColourAndAlpha() : bounds.getPaint().getStops().get(3)
				.getColor();

		Color color4 = bounds.getPaint() == null ? getRandomColourAndAlpha() : bounds.getPaint().getStops().get(4)
				.getColor();

		double centerX = bounds.getCenterX();
		double centerY = bounds.getCenterY();
		double radiusX = bounds.getRadiusX();
		double radiusY = bounds.getRadiusY();
		double radius = radiusX < radiusY ? radiusX : radiusY;

		Color color5 = getRandomColourAndAlpha();

		return new RadialGradient(0, 0, centerX, centerY, radius, false, CycleMethod.NO_CYCLE, new Stop(0, color1),
				new Stop(0.25, color2), new Stop(0.5, color3), new Stop(0.75, color4), new Stop(1, color5));
	}

	private class HypnoBubble extends AnimationRectangle2D {
		private boolean isShrinkingWidth;
		private double xSpeedFactor;
		private double ySpeedFactor;
		private double wobbleFactor;
		private RadialGradient paint;

		public HypnoBubble(double x, double y, double w, double h) {
			super(x, y, w, h);
		}

		public boolean isShrinkingWidth() {
			return isShrinkingWidth;
		}

		public void setShrinkingWidth(boolean isShrinkingWidth) {
			this.isShrinkingWidth = isShrinkingWidth;
		}

		public double getXSpeedFactor() {
			return xSpeedFactor;
		}

		public void setXSpeedFactor(double speedFactor) {
			this.xSpeedFactor = speedFactor;
		}

		public double getWobbleFactor() {
			return wobbleFactor;
		}

		public void setWobbleFactor(double wobbleFactor) {
			this.wobbleFactor = wobbleFactor;
		}

		public double getYSpeedFactor() {
			return ySpeedFactor;
		}

		public void setYSpeedFactor(double speedFactor) {
			ySpeedFactor = speedFactor;
		}

		public RadialGradient getPaint() {
			return paint;
		}

		public void setPaint(RadialGradient paint) {
			this.paint = paint;
		}
	}

	@Override
	public boolean useBackgroundColour() {
		return false;
	}

	@Override
	public boolean useDesktopBackground() {
		return true;
	}

}
