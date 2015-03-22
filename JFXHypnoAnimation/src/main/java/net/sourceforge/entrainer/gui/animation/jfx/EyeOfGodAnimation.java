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

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import net.sourceforge.entrainer.gui.jfx.animation.AnimationRectangle2D;
import net.sourceforge.entrainer.gui.jfx.animation.JFXEntrainerAnimation;

/**
 * ICU.
 * 
 * @author burton
 */
public class EyeOfGodAnimation extends JFXEntrainerAnimation {

  private static int NUM_REUSED_COLORS = 9;

  private boolean hasAdded = false;

  public EyeOfGodAnimation() {
    super();
  }

  public void clearAnimation() {
    super.clearAnimation();
    hasAdded = false;
  }

  @Override
  protected AnimationRectangle2D getNewAnimationRectangle2D(Point2D position) {
    double width = getScreenRectangle().getWidth();
    double height = getScreenRectangle().getHeight();

    double diameter = Math.sqrt(width * width + height * height);

    EyeOfGod eyeBall = new EyeOfGod(0, 0, diameter, diameter);
    
    eyeBall.setCenterX(width / 2);
    eyeBall.setCenterY(height / 2);

    return eyeBall;
  }

  @Override
  protected void move(GraphicsContext g2d, AnimationRectangle2D shape) {
    EyeOfGod eyeBall = (EyeOfGod) shape;

    g2d.setFill(eyeBall.getPaint());
    g2d.fillOval(shape.getMinX(), shape.getMinY(), shape.getWidth(), shape.getHeight());

    eyeBall.setPaint(getNewPaint(eyeBall));
  }

  public void maybeAddNewAnimationRectangle() {
    if (!hasAdded && getCount() == 0) {
      addBubble(getCenterOfEntrainerFrame(), null);
      hasAdded = true;
    }
  }

  @Override
  public String toString() {
    return "Eye of God!!!";
  }

  private void addBubble(Point2D newPosition, RadialGradient paint) {
    addBubble((EyeOfGod) getNewAnimationRectangle2D(newPosition), paint);
  }

  private void addBubble(EyeOfGod eyeBall, RadialGradient paint) {
    if (paint == null) {
      paint = getNewPaint(eyeBall);
    }

    eyeBall.setPaint(paint);
    add(eyeBall);
  }

  private RadialGradient getNewPaint(EyeOfGod bounds) {
    List<Color> colors = new ArrayList<Color>();

    colors.add(getRandomColourAndAlpha());
    for (int i = 0; i < NUM_REUSED_COLORS; i++) {
      colors
          .add(bounds.getPaint() == null ? getRandomColourAndAlpha() : bounds.getPaint().getStops().get(i).getColor());
    }

    double centerX = bounds.getCenterX();
    double centerY = bounds.getCenterY();
    double radiusX = bounds.getRadiusX();
    double radiusY = bounds.getRadiusY();
    double radius = radiusX < radiusY ? radiusX : radiusY;

    return new RadialGradient(bounds.getAngle(), 0.05, centerX, centerY, radius, false, CycleMethod.NO_CYCLE,
        getStops(colors));
  }

  private List<Stop> getStops(List<Color> colors) {
    List<Stop> stops = new ArrayList<Stop>();

    double size = colors.size();
    for (double i = 0; i < size; i++) {
      stops.add(new Stop(i / NUM_REUSED_COLORS, colors.get((int) i)));
    }

    return stops;
  }

  private class EyeOfGod extends AnimationRectangle2D {
    private RadialGradient paint;

    private int angle;

    public EyeOfGod(double x, double y, double w, double h) {
      super(x, y, w, h);
    }

    public RadialGradient getPaint() {
      return paint;
    }

    public void setPaint(RadialGradient paint) {
      this.paint = paint;
    }

    public int getAngle() {
      int current = angle;

      angle++;
      if (angle > 359) angle = 0;

      return current;
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
