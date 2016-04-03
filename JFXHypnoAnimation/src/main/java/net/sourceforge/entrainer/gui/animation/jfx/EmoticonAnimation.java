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
import javafx.scene.image.Image;
import net.sourceforge.entrainer.gui.jfx.animation.AnimationRectangle2D;
import net.sourceforge.entrainer.gui.jfx.animation.JFXEntrainerAnimation;

/**
 * This animation draws emoticons on the screen.  Each instance
 * contains two random emoticons which are switched in time with the entrainment
 * frequency.
 *
 * http://openiconlibrary.sourceforge.net/
 * 
 * @author burton
 */
public class EmoticonAnimation extends JFXEntrainerAnimation {

  private EmoticonLoader emoticonLoader;

  public EmoticonAnimation() {
    super();
    try {
      emoticonLoader = new EmoticonLoader();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected AnimationRectangle2D getNewAnimationRectangle2D(Point2D position) {
    double sizeFactor = getRandomPositiveDouble();

    double diameter = sizeFactor * 200;
    if (diameter < 5) diameter = 5;

    Image image = emoticonLoader.getRandomEmoticon();
    Image image2 = emoticonLoader.getRandomEmoticon();

    Emoticon emoticon = new Emoticon(position.getX(), position.getY(), image, image2);

    emoticon.setXSpeedFactor(getRandomDouble());
    emoticon.setYSpeedFactor(getRandomDouble());

    return emoticon;
  }

  @Override
  protected void move(GraphicsContext g2d, AnimationRectangle2D shape) {
    Emoticon emoticon = (Emoticon) shape;

    g2d.drawImage(emoticon.getEmoticon(), emoticon.getMinX(), emoticon.getMinY());

    modifyPosition(emoticon);

    if (isAtScreenEdge(emoticon)) {
      removeFromAnimation(emoticon);
    }
  }

  public void maybeAddNewAnimationRectangle() {
    double f = getRandomPositiveDouble();
    if (f > 0.95) {
      addBubble(getCenterOfEntrainerFrame());
    }
  }

  @Override
  public String toString() {
    return "Emoticons!!!";
  }

  private void modifyPosition(Emoticon emoticon) {
    double speedX = emoticon.getXSpeedFactor() * 5;
    double speedY = emoticon.getYSpeedFactor() * 5;

    emoticon.setCenterX(emoticon.getCenterX() + speedX);
    emoticon.setCenterY(emoticon.getCenterY() + speedY);
  }

  private void addBubble(Point2D newPosition) {
    add((Emoticon) getNewAnimationRectangle2D(newPosition));
  }

  private class Emoticon extends AnimationRectangle2D {
    private double xSpeedFactor;
    private double ySpeedFactor;
    private Image emoticon1;
    private Image emoticon2;

    private boolean one;

    public Emoticon(double x, double y, Image emoticon1, Image emoticon2) {
      super(x, y, emoticon1.getWidth(), emoticon1.getHeight());
      setEmoticon1(emoticon1);
      setEmoticon2(emoticon2);
    }

    public double getXSpeedFactor() {
      return xSpeedFactor;
    }

    public void setXSpeedFactor(double speedFactor) {
      this.xSpeedFactor = speedFactor;
    }

    public double getYSpeedFactor() {
      return ySpeedFactor;
    }

    public void setYSpeedFactor(double speedFactor) {
      ySpeedFactor = speedFactor;
    }

    public Image getEmoticon() {
      try {
        if (one) {
          return emoticon1;
        } else {
          return emoticon2;
        }
      } finally {
        one = !one;
      }
    }

    public void setEmoticon1(Image emoticon) {
      this.emoticon1 = emoticon;
    }

    public void setEmoticon2(Image emoticon) {
      this.emoticon2 = emoticon;
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
