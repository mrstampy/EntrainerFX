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

import javafx.geometry.Dimension2D;
import javafx.scene.image.ImageView;

// TODO: Auto-generated Javadoc
/**
 * The Class ImageViewScaler.
 */
public class ImageViewScaler {

	/**
	 * Scales the ImageView centred in the dimensions specified.
	 *
	 * @param view
	 *          the view
	 * @param area
	 *          the area
	 */
	public static void scale(ImageView view, Dimension2D area) {
		if (view == null || view.getOpacity() == 0 || view.getImage() == null) return;

		double pw = view.getImage().getWidth();
		double ph = view.getImage().getHeight();
		Dimension2D pic = new Dimension2D(pw, ph);

		double vw = area.getWidth();
		double vh = area.getHeight();

		view.setFitHeight(0);
		view.setFitWidth(0);

		if (isOutsideArea(pic, area)) {
			// find the minimum distance from pic dimensions
			// to view area (w or h diff)
			scaleOutside(view, pic, area);
		} else if (isInsideArea(pic, area)) {
			// find the maximum distance from pic dimensions
			// to view area (w or h diff)
			scaleInside(view, pic, area);
		} else {
			// find which dimension is inside the view area,
			// scale to fit
			scaleMixed(view, pic, area);
		}

		double fh = view.getFitHeight() - vh;
		view.setY(0 - (fh / 2));

		double fw = view.getFitWidth() - vw;
		view.setX(0 - (fw / 2));
	}

	// which pic dimension is inside the area? Use that.
	private static void scaleMixed(ImageView view, Dimension2D pic, Dimension2D area) {
		double cw = getCalculatedWidth(pic, area);
		double ch = getCalculatedHeight(pic, area);

		double wd = area.getWidth() - cw;

		if (wd >= 0) {
			setAreaFitWidth(view, pic, area, ch);
		} else {
			setAreaFitHeight(view, pic, area, cw);
		}
	}

	// which axis is greatest difference? Use that.
	private static void scaleInside(ImageView view, Dimension2D pic, Dimension2D area) {
		double cw = getCalculatedWidth(pic, area);
		double ch = getCalculatedHeight(pic, area);

		double wd = area.getWidth() - cw;
		double hd = area.getHeight() - ch;

		if (wd > hd) {
			setAreaFitWidth(view, pic, area, ch);
		} else {
			setAreaFitHeight(view, pic, area, cw);
		}
	}

	private static boolean isInsideArea(Dimension2D pic, Dimension2D area) {
		return pic.getWidth() <= area.getWidth() && pic.getHeight() <= area.getHeight();
	}

	// which axis is the least difference? Use that.
	private static void scaleOutside(ImageView view, Dimension2D pic, Dimension2D area) {
		double cw = getCalculatedWidth(pic, area);
		double ch = getCalculatedHeight(pic, area);

		double wd = cw - area.getWidth();
		double hd = ch - area.getHeight();

		if (wd < hd) {
			setAreaFitWidth(view, pic, area, ch);
		} else {
			setAreaFitHeight(view, pic, area, cw);
		}
	}

	private static void setAreaFitWidth(ImageView view, Dimension2D pic, Dimension2D area, double ch) {
		view.setFitWidth(area.getWidth());
		view.setFitHeight(ch);
	}

	private static double getCalculatedHeight(Dimension2D pic, Dimension2D area) {
		return pic.getHeight() * area.getWidth() / pic.getWidth();
	}

	private static void setAreaFitHeight(ImageView view, Dimension2D pic, Dimension2D area, double cw) {
		view.setFitHeight(area.getHeight());
		view.setFitWidth(cw);
	}

	private static double getCalculatedWidth(Dimension2D pic, Dimension2D area) {
		return pic.getWidth() * area.getHeight() / pic.getHeight();
	}

	private static boolean isOutsideArea(Dimension2D pic, Dimension2D area) {
		return pic.getWidth() >= area.getWidth() && pic.getHeight() >= area.getHeight();
	}

	private ImageViewScaler() {
	}

}
