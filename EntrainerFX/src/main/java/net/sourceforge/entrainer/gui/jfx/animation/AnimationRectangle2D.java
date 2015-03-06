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

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

// TODO: Auto-generated Javadoc
/**
 * Convenience class to enable modification of {@link Rectangle2D} settings.
 * Provides convenience methods to determine and set center & radius.
 * 
 * @author burton
 * @see JFXEntrainerAnimation
 *
 */
public class AnimationRectangle2D {

	private Rectangle2D delegate;

	/**
	 * Instantiates a new animation rectangle2 d.
	 *
	 * @param arg0
	 *          the arg0
	 * @param arg1
	 *          the arg1
	 * @param arg2
	 *          the arg2
	 * @param arg3
	 *          the arg3
	 */
	public AnimationRectangle2D(double arg0, double arg1, double arg2, double arg3) {
		delegate = new Rectangle2D(arg0, arg1, arg2, arg3);
	}

	/**
	 * Contains.
	 *
	 * @param x
	 *          the x
	 * @param y
	 *          the y
	 * @return true, if successful
	 */
	public boolean contains(double x, double y) {
		return delegate.contains(x, y);
	}

	/**
	 * Contains.
	 *
	 * @param x
	 *          the x
	 * @param y
	 *          the y
	 * @param w
	 *          the w
	 * @param h
	 *          the h
	 * @return true, if successful
	 */
	public boolean contains(double x, double y, double w, double h) {
		return delegate.contains(x, y, w, h);
	}

	/**
	 * Contains.
	 *
	 * @param p
	 *          the p
	 * @return true, if successful
	 */
	public boolean contains(Point2D p) {
		return delegate.contains(p);
	}

	/**
	 * Contains.
	 *
	 * @param rect
	 *          the rect
	 * @return true, if successful
	 */
	public boolean contains(AnimationRectangle2D rect) {
		return contains(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
	}

	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	public double getHeight() {
		return delegate.getHeight();
	}

	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public double getWidth() {
		return delegate.getWidth();
	}

	/**
	 * Gets the max x.
	 *
	 * @return the max x
	 */
	public double getMaxX() {
		return delegate.getMaxX();
	}

	/**
	 * Gets the max y.
	 *
	 * @return the max y
	 */
	public double getMaxY() {
		return delegate.getMaxY();
	}

	/**
	 * Gets the min x.
	 *
	 * @return the min x
	 */
	public double getMinX() {
		return delegate.getMinX();
	}

	/**
	 * Gets the min y.
	 *
	 * @return the min y
	 */
	public double getMinY() {
		return delegate.getMinY();
	}

	/**
	 * Intersects.
	 *
	 * @param x
	 *          the x
	 * @param y
	 *          the y
	 * @param w
	 *          the w
	 * @param h
	 *          the h
	 * @return true, if successful
	 */
	public boolean intersects(double x, double y, double w, double h) {
		return delegate.intersects(x, y, w, h);
	}

	/**
	 * Intersects.
	 *
	 * @param rect
	 *          the rect
	 * @return true, if successful
	 */
	public boolean intersects(Rectangle2D rect) {
		return delegate.intersects(rect);
	}

	/**
	 * Intersects.
	 *
	 * @param rect
	 *          the rect
	 * @return true, if successful
	 */
	public boolean intersects(AnimationRectangle2D rect) {
		return intersects(rect.getDelegate());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return delegate.hashCode() + super.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return delegate.toString();
	}

	/**
	 * Sets the delegate.
	 *
	 * @param delegate
	 *          the new delegate
	 */
	public void setDelegate(Rectangle2D delegate) {
		this.delegate = delegate;
	}

	/**
	 * Gets the delegate.
	 *
	 * @return the delegate
	 */
	public Rectangle2D getDelegate() {
		return delegate;
	}

	/**
	 * Sets the x.
	 *
	 * @param x
	 *          the new x
	 */
	public void setX(double x) {
		setDelegate(new Rectangle2D(x, getMinY(), getWidth(), getHeight()));
	}

	/**
	 * Sets the y.
	 *
	 * @param y
	 *          the new y
	 */
	public void setY(double y) {
		setDelegate(new Rectangle2D(getMinX(), y, getWidth(), getHeight()));
	}

	/**
	 * Sets the width.
	 *
	 * @param w
	 *          the new width
	 */
	public void setWidth(double w) {
		setDelegate(new Rectangle2D(getMinX(), getMinY(), w, getHeight()));
	}

	/**
	 * Sets the height.
	 *
	 * @param h
	 *          the new height
	 */
	public void setHeight(double h) {
		setDelegate(new Rectangle2D(getMinX(), getMinY(), getWidth(), h));
	}

	/**
	 * Gets the center x.
	 *
	 * @return the center x
	 */
	public double getCenterX() {
		return getMinX() + getWidth() / 2;
	}

	/**
	 * Gets the center y.
	 *
	 * @return the center y
	 */
	public double getCenterY() {
		return getMinY() + getHeight() / 2;
	}

	/**
	 * Sets the center x.
	 *
	 * @param xCenter
	 *          the new center x
	 */
	public void setCenterX(double xCenter) {
		double current = getCenterX();
		double diff = xCenter - current;

		setX(getMinX() + diff);
	}

	/**
	 * Sets the center y.
	 *
	 * @param yCenter
	 *          the new center y
	 */
	public void setCenterY(double yCenter) {
		double current = getCenterY();
		double diff = yCenter - current;

		setY(getMinY() + diff);
	}

	/**
	 * Gets the radius x.
	 *
	 * @return the radius x
	 */
	public double getRadiusX() {
		return getWidth() / 2;
	}

	/**
	 * Gets the radius y.
	 *
	 * @return the radius y
	 */
	public double getRadiusY() {
		return getHeight() / 2;
	}

	/**
	 * Sets the radius x.
	 *
	 * @param xRad
	 *          the new radius x
	 */
	public void setRadiusX(double xRad) {
		double current = getRadiusX();
		double newX = getMinX() + current - xRad;
		double newW = 2 * xRad;

		setDelegate(new Rectangle2D(newX, getMinY(), newW, getHeight()));
	}

	/**
	 * Sets the radius y.
	 *
	 * @param yRad
	 *          the new radius y
	 */
	public void setRadiusY(double yRad) {
		double current = getRadiusY();
		double newY = getMinY() + current - yRad;
		double newH = 2 * yRad;

		setDelegate(new Rectangle2D(getMinX(), newY, getWidth(), newH));
	}

}
