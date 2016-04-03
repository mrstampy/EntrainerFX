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
import javafx.scene.paint.Stop;
import javafx.scene.transform.Affine;
import net.sourceforge.entrainer.gui.jfx.animation.AnimationRectangle2D;
import net.sourceforge.entrainer.gui.jfx.animation.JFXEntrainerAnimation;

/**
 * This animation draws a square that varies in size and shape while rotating &
 * 'bouncing' off the screen edges. Should it exit the screen, a new one is
 * created.
 * 
 * @author burton
 */
public class JFXHypnoSquareAnimation extends JFXEntrainerAnimation {

	private boolean hasAdded = false;

	public JFXHypnoSquareAnimation() {
		super();
	}

	@Override
	protected AnimationRectangle2D getNewAnimationRectangle2D(Point2D position) {
		HypnoSquare square = new HypnoSquare(position.getX() - 200, position.getY() - 200, 400, 400);

		square.setXSpeedFactor(getRandomDouble());
		square.setYSpeedFactor(getRandomDouble());
		square.setWobbleFactor(getRandomPositiveDouble());
		square.setShrinkFactor(getRandomPositiveDouble());
		square.setRotationDegrees(getRandomPositiveDouble() * 20);
		square.setRotateFactor(square.getRotationDegrees());

		return square;
	}

	@Override
	protected void move(GraphicsContext g2d, AnimationRectangle2D shape) {
		HypnoSquare square = (HypnoSquare) shape;

		modifyRectangle2D(square);
		modifyPosition(square);
		modifySize(square);
		modifyRotation(square, g2d);

		if (square.getChangedDirectionCount() <= 0) {
			if (isAtScreenHorizontalEdge(square)) {
				changeXDirection(square);
			}

			if (isAtScreenVerticalEdge(shape)) {
				changeYDirection(square);
			}
		} else {
			if (!isAtScreenEdge(shape)) {
				square.setChangedDirectionCount(square.getChangedDirectionCount() - 1);
			}
		}

		if (isOffScreen(shape)) {
			removeFromAnimation(shape);
			hasAdded = false;
		}
	}

	public void maybeAddNewAnimationRectangle() {
		if (!hasAdded && getCount() == 0) {
			addSquare(getCenterOfEntrainerFrame(), null);
			hasAdded = true;
		}
	}

	@Override
	public String toString() {
		return "Hypno Square!!!";
	}

	private void changeYDirection(HypnoSquare square) {
		square.setYSpeedFactor(square.getYSpeedFactor() * -1);
		square.setRotateFactor(square.getRotateFactor() * -1);
		square.setChangedDirectionCount(10);
	}

	private void changeXDirection(HypnoSquare square) {
		square.setXSpeedFactor(square.getXSpeedFactor() * -1);
		square.setRotateFactor(square.getRotateFactor() * -1);
		square.setChangedDirectionCount(10);
	}

	private void modifyRotation(HypnoSquare square, GraphicsContext g2d) {
		Affine current = g2d.getTransform();

		g2d.setTransform(square.getAffine());
		g2d.translate(square.getCenterX(), square.getCenterY());
		g2d.rotate(square.getRotationDegrees());
		g2d.translate(-square.getCenterX(), -square.getCenterY());
		square.setRotationDegrees(square.getRotationDegrees() + square.getRotateFactor());

		g2d.setFill(square.getPaint());
		g2d.fillRect(square.getMinX(), square.getMinY(), square.getWidth(), square.getHeight());
		square.setPaint(getNewPaint(square));

		g2d.setTransform(current);
	}

	private void modifySize(HypnoSquare square) {
		double shrinkFactor = square.getShrinkFactor();
		double avgSize = (square.getWidth() + square.getHeight()) / 2;
		double width = square.getWidth();
		double height = square.getHeight();

		if (square.isShrinkingSize()) {
			if (avgSize / 400 < 0.5) {
				square.setShrinkingSize(false);
			} else {
				square.setWidth(width - shrinkFactor);
				square.setHeight(height - shrinkFactor);
			}
		} else {
			if (avgSize / 400 > 1.5) {
				square.setShrinkingSize(true);
			} else {
				square.setWidth(width + shrinkFactor);
				square.setHeight(height + shrinkFactor);
			}
		}
	}

	private void modifyPosition(HypnoSquare square) {
		square.setX(square.getMinX() + (square.getXSpeedFactor() * 20));
		square.setY(square.getMinY() + (square.getYSpeedFactor() * 20));
	}

	private void modifyRectangle2D(HypnoSquare square) {
		double wobbleFactor = square.getWobbleFactor();
		double width = square.getWidth();
		double height = square.getHeight();

		if (square.isShrinkingWidth()) {
			if (width / height < 0.75) {
				square.setShrinkingWidth(false);
			} else {
				square.setWidth(width - wobbleFactor);
				square.setHeight(height + wobbleFactor);
			}
		} else {
			if (height / width < 0.75) {
				square.setShrinkingWidth(true);
			} else {
				square.setWidth(width + wobbleFactor);
				square.setHeight(height - wobbleFactor);
			}
		}
	}

	private void addSquare(Point2D newPosition, LinearGradient paint) {
		addSquare((HypnoSquare) getNewAnimationRectangle2D(newPosition), paint);
	}

	private void addSquare(HypnoSquare square, LinearGradient paint) {
		if (paint == null) {
			paint = getNewPaint(square);
		}
		square.setPaint(paint);
		add(square);
	}

	private LinearGradient getNewPaint(HypnoSquare bounds) {
		Color color1 = bounds.getPaint() == null ? getRandomColourAndAlpha() : bounds.getPaint().getStops().get(1)
				.getColor();

		double x1 = bounds.getMinX();
		double y1 = bounds.getMinY();
		double x2 = x1 + bounds.getWidth();
		double y2 = y1 + bounds.getHeight();

		Color color2 = getRandomColourAndAlpha();

		return new LinearGradient(x1, y1, x2, y2, false, CycleMethod.NO_CYCLE, new Stop(0, color1), new Stop(1, color2));
	}

	public void clearAnimation() {
		super.clearAnimation();
		hasAdded = false;
	}

	private class HypnoSquare extends AnimationRectangle2D {
		private boolean isShrinkingWidth;
		private boolean isShrinkingSize;
		private double xSpeedFactor;
		private double ySpeedFactor;
		private double wobbleFactor;
		private double rotateFactor;
		private double shrinkFactor;
		private double rotationDegrees;
		private LinearGradient paint;
		private int changedDirectionCount;

		private Affine affine;

		public HypnoSquare(double x, double y, double w, double h) {
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

		public LinearGradient getPaint() {
			return paint;
		}

		public void setPaint(LinearGradient paint) {
			this.paint = paint;
		}

		public boolean isShrinkingSize() {
			return isShrinkingSize;
		}

		public void setShrinkingSize(boolean isShrinkingSize) {
			this.isShrinkingSize = isShrinkingSize;
		}

		public double getRotateFactor() {
			return rotateFactor;
		}

		public void setRotateFactor(double rotateFactor) {
			this.rotateFactor = rotateFactor;
		}

		public double getShrinkFactor() {
			return shrinkFactor;
		}

		public void setShrinkFactor(double shrinkFactor) {
			this.shrinkFactor = shrinkFactor;
		}

		public double getRotationDegrees() {
			return rotationDegrees;
		}

		public void setRotationDegrees(double rotationDegrees) {
			this.rotationDegrees = rotationDegrees;
		}

		public int getChangedDirectionCount() {
			return changedDirectionCount;
		}

		public void setChangedDirectionCount(int changedDirectionCount) {
			this.changedDirectionCount = changedDirectionCount;
		}

		public Affine getAffine() {
			if (affine == null) affine = new Affine();
			return affine;
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
