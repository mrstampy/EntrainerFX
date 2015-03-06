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
package net.sourceforge.entrainer.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import net.sourceforge.entrainer.gui.jfx.shimmer.AbstractShimmer;

// TODO: Auto-generated Javadoc
/**
 * A wrapper around an AWT Rectangle to provide an object to the Trident
 * animation framework.
 *
 * @author burton
 * @deprecated now using {@link AbstractShimmer} subclasses.
 */
public class ShimmerRectangle {

	private Dimension panelSize;
	private Rectangle2D rect;
	private Color color1;
	private Color color2;
	private float opacity;

	/**
	 * Instantiates a new shimmer rectangle.
	 *
	 * @param panelSize
	 *          the panel size
	 */
	public ShimmerRectangle(Dimension panelSize) {
		init(panelSize);
	}

	/**
	 * Called from {@link ShimmerPane}'s paint method.
	 *
	 * @param g2d
	 *          the g2d
	 */
	public void paint(Graphics2D g2d) {
		g2d.setPaint(getGradientPaint());
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getOpacity()));
		g2d.fill(getRect());
	}

	private Paint getGradientPaint() {
		if (getColor1() == null || getColor2() == null) {
			return new Color(0f, 0f, 0f, 0f);
		}
		return new GradientPaint(0, 0, getColor1(), getPanelSize().width, getPanelSize().height, getColor2());
	}

	/**
	 * Inits the.
	 *
	 * @param panelSize
	 *          the panel size
	 */
	void init(Dimension panelSize) {
		setPanelSize(panelSize);
		setRect(new Rectangle2D.Double(0, 0, getPanelSize().getWidth(), getPanelSize().getHeight()));
	}

	private Dimension getPanelSize() {
		return panelSize;
	}

	private void setPanelSize(Dimension size) {
		this.panelSize = size;
	}

	private Rectangle2D getRect() {
		return rect;
	}

	private void setRect(Rectangle2D rect) {
		this.rect = rect;
	}

	/**
	 * Gets the color1.
	 *
	 * @return the color1
	 */
	public Color getColor1() {
		return color1;
	}

	/**
	 * Sets the color1.
	 *
	 * @param color
	 *          the new color1
	 */
	public void setColor1(Color color) {
		this.color1 = color;
	}

	/**
	 * Gets the color2.
	 *
	 * @return the color2
	 */
	public Color getColor2() {
		return color2;
	}

	/**
	 * Sets the color2.
	 *
	 * @param color2
	 *          the new color2
	 */
	public void setColor2(Color color2) {
		this.color2 = color2;
	}

	/**
	 * Gets the opacity.
	 *
	 * @return the opacity
	 */
	public float getOpacity() {
		return opacity;
	}

	/**
	 * Sets the opacity.
	 *
	 * @param opacity
	 *          the new opacity
	 */
	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

}
