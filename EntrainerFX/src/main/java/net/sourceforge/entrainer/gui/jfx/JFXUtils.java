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

import java.io.File;
import java.net.URI;
import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Dimension2D;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import net.sourceforge.entrainer.gui.flash.CurrentEffect;
import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.util.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class JFXUtils.
 */
public class JFXUtils {
	private static final Logger log = LoggerFactory.getLogger(JFXUtils.class);

	private static ColorAdjust defaultColourAdjust = new ColorAdjust();

	/**
	 * Sets the effect.
	 *
	 * @param node
	 *          the node
	 * @param effect
	 *          the effect
	 */
	public static void setEffect(Node node, CurrentEffect effect) {
		CacheHint orig = node.getCacheHint();
		boolean isSpeed = orig == CacheHint.SPEED;

		if (!isSpeed) node.setCacheHint(CacheHint.SPEED);

		runLater(() -> setEffectInNode(node, effect, isSpeed, orig));
	}

	private static void setEffectInNode(Node node, CurrentEffect effect, boolean isSpeed, CacheHint orig) {
		try {
			node.effectProperty().addListener(new ChangeListener<Effect>() {

				@Override
				public void changed(ObservableValue<? extends Effect> observable, Effect oldValue, Effect newValue) {
					if (!isSpeed) node.setCacheHint(orig);
					node.effectProperty().removeListener(this);
				}
			});
			node.setEffect(effect.getEffect());
			setOpacity(node, effect);
		} catch (Exception e) {
			log.error("Unexpected exception ", e);
		}
	}

	private static void setOpacity(Node node, CurrentEffect effect) {
		if (effect.isOpacity()) {
			flipOpacity(node, effect);
		} else if (node.getOpacity() != 1) {
			node.setOpacity(1);
		}
	}

	private static void flipOpacity(Node node, CurrentEffect effect) {
		if (effect.isPulse()) {
			double o = node.getOpacity() == 1 ? 0.5 : 1;
			node.setOpacity(o);
		} else {
			node.setOpacity(1);
		}
	}

	/**
	 * Reset effects.
	 *
	 * @param node
	 *          the node
	 */
	public static void resetEffects(Node node) {
		if (node == null) return;

		node.setOpacity(1);
		if (node.getEffect() instanceof ColorAdjust) node.setEffect(defaultColourAdjust);
		node.setEffect(null);
	}

	/**
	 * To jfx color.
	 *
	 * @param awt
	 *          the awt
	 * @return the color
	 */
	public static Color toJFXColor(java.awt.Color awt) {
		if (awt == null) return null;

		double r = ((double) awt.getRed()) / 255;
		double g = ((double) awt.getGreen()) / 255;
		double b = ((double) awt.getBlue()) / 255;
		double a = ((double) awt.getAlpha()) / 255;

		return new Color(r, g, b, a);
	}

	/**
	 * From jfx color.
	 *
	 * @param c
	 *          the c
	 * @return the java.awt. color
	 */
	public static java.awt.Color fromJFXColor(Color c) {
		if (c == null) return null;

		return new java.awt.Color((float) c.getRed(), (float) c.getGreen(), (float) c.getBlue(), (float) c.getOpacity());
	}

	/**
	 * Gets the entrainer css.
	 *
	 * @return the entrainer css
	 */
	public static URI getEntrainerCSS() {
		Optional<File> cssDir = Utils.getCssDir();
		File css = new File(cssDir.get(), "entrainerfx.css");

		return css.exists() ? css.toURI() : null;
	}

	/**
	 * Run later.
	 *
	 * @param run
	 *          the run
	 */
	public static void runLater(Runnable run) {
		if (Platform.isFxApplicationThread()) {
			runNow(run);
		} else {
			Platform.runLater(() -> runNow(run));
		}
	}

	private static void runNow(Runnable run) {
		try {
			run.run();
		} catch (Exception e) {
			log.error("Unexpected exception", e);
			GuiUtil.handleProblem(e);
		}
	}

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

}
