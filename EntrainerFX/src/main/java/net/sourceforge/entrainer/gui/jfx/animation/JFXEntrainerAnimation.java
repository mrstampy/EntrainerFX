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

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import net.sourceforge.entrainer.gui.EntrainerBackground;

// TODO: Auto-generated Javadoc
/**
 * This class is the super class for all animations. Entrainer picks up new
 * animation classes dynamically - there is no need to recompile the app to run
 * new animations. Consequently, anyone can write new animations for Entrainer
 * and they can be deployed separately.<br>
 * <br>
 * {@link JFXAnimationWindow} calls the
 * <code>moveAnimationRectangle2D(GraphicsContext, AnimationRectangle2D);</code>
 * method twice for each Hz of the entrainment frequency. This is analogous to
 * how the {@link EntrainerBackground} flashes, and facilitates smoother
 * animation.<br>
 * <br>
 * {@link JFXEntrainerAnimation} has available all current values ie. amplitude,
 * entrainment frequency etc. to its subclasses. Consequently animation can
 * respond to changes in these values.<br>
 * <br>
 * The steps to create a new animation are as follows:<br>
 * <br>
 * - Install Entrainer with all optional packages (default install).<br>
 * <br>
 * - Create a Java project in your favourite IDE, including 'Entrainer.jar' in
 * the build path (from the Entrainer install directory). Currently Entrainer is
 * built using Java 5 (JDK 1.5).<br>
 * <br>
 * - Attach the source and javadoc in the Entrainer install directory to
 * Entrainer.jar in the project (optional, but very useful for development)<br>
 * <br>
 * - Extend this class, implementing the abstract methods. As the animations are
 * instantiated via reflection, ensure the subclass has a constructor with no
 * parameters, and ensure you call <code>super();</code><br>
 * <br>
 * - Implement your animation.<br>
 * <br>
 * - Jar your animation class (and any resources it requires ie. images),
 * calling the jar file anything you wish, and put the newly created jar file in
 * the 'animation' directory of the Entrainer install.<br>
 * <br>
 * - Restart Entrainer, and start debugging your animation!<br>
 * <br>
 * If you wish your custom animation(s) to be available with Entrainer, please
 * contact me at <b>burton@users.sourceforge.net</b>.
 * 
 * @author burton
 */
public abstract class JFXEntrainerAnimation extends AbstractJFXAnimation {

	private Rectangle2D entrainerFramePosition;
	private List<AnimationRectangle2D> shapes = new ArrayList<AnimationRectangle2D>();
	private List<AnimationRectangle2D> removables = new ArrayList<AnimationRectangle2D>();

	/**
	 * Call <code>super();</code> in all subclass constructors.
	 */
	public JFXEntrainerAnimation() {
		super();
		setHideEntrainerFrame(false);
	}

	/**
	 * Implement to return a new shape. Typically called by the
	 * <code>maybeAddNewAnimationRectangle2D();</code> implementation.
	 *
	 * @param position
	 *          the position
	 * @return the new animation rectangle2 d
	 */
	protected abstract AnimationRectangle2D getNewAnimationRectangle2D(Point2D position);

	/**
	 * Implement to decide how to move, change colours of, etc. the specified
	 * shape. The implementation must also draw the shape.
	 *
	 * @param gc
	 *          the gc
	 * @param shape
	 *          the shape
	 */
	protected abstract void move(GraphicsContext gc, AnimationRectangle2D shape);

	/**
	 * Implement to decide whether or not to add a new shape to the animation. To
	 * do so, call the <code>getNewAnimationRectangle2D(Point2D);</code> method,
	 * then <code>addAnimationRectangle2D(AnimationRectangle2D);</code>.
	 */
	public abstract void maybeAddNewAnimationRectangle();

	/**
	 * Call this method to add the specified shape to the animation.
	 *
	 * @param shape
	 *          the shape
	 */
	protected synchronized void add(AnimationRectangle2D shape) {
		shapes.add(shape);
	}

	/**
	 * Call this method to remove all shapes from the animation.
	 */
	public synchronized void clearAnimation() {
		shapes.clear();
	}

	/**
	 * Do not call this directly; it is invoked from {@link AnimationWindow}.
	 * Iterates thru the added shapes and calls the
	 * <code>moveAnimationRectangle2D(Graphics2D, AnimationRectangle2D);</code>
	 * method for each.
	 *
	 * @param gc
	 *          the gc
	 */
	public synchronized void animate(GraphicsContext gc) {
		maybeAddNewAnimationRectangle();

		super.animate(gc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.gui.jfx.animation.AbstractJFXAnimation#animateImpl
	 * (javafx.scene.canvas.GraphicsContext)
	 */
	@Override
	protected void animateImpl(GraphicsContext gc) {
		for (AnimationRectangle2D shape : getCopy()) {
			move(gc, shape);
		}

		removeRemovables();
	}

	/**
	 * Returns true if the shape is intersecting (touching) the Entrainer frame.
	 *
	 * @param shape
	 *          the shape
	 * @return true, if is intersecting with entrainer frame
	 */
	protected boolean isIntersectingWithEntrainerFrame(AnimationRectangle2D shape) {
		Rectangle2D r = getEntrainerFramePosition();
		return shape.intersects(r.getMinX(), r.getMinY(), r.getMaxX(), r.getMaxY());
	}

	/**
	 * Returns a copy of the list of animations currently being rendered.
	 *
	 * @return the copy
	 */
	protected List<AnimationRectangle2D> getCopy() {
		return new ArrayList<AnimationRectangle2D>(shapes);
	}

	/**
	 * Returns the number of shapes currently being rendered.
	 *
	 * @return the count
	 */
	protected int getCount() {
		return getCopy().size();
	}

	/**
	 * Returns the current center point of the Entrainer frame.
	 *
	 * @return the center of entrainer frame
	 */
	protected Point2D getCenterOfEntrainerFrame() {
		Rectangle2D r = getEntrainerFramePosition();
		
		double centerX = r.getMinX() + r.getWidth() / 2;
		double centerY = r.getMinY() + r.getHeight() / 2;
		
		return new Point2D(centerX, centerY);
	}

	private void removeRemovables() {
		for (AnimationRectangle2D shape : removables) {
			shapes.remove(shape);
		}

		removables.clear();
	}

	/**
	 * Returns a rectangle representing the current position of the Entrainer
	 * frame.
	 *
	 * @return the entrainer frame position
	 */
	public Rectangle2D getEntrainerFramePosition() {
		return entrainerFramePosition;
	}

	public void setEntrainerFramePosition(Rectangle2D entrainerFramePosition) {
		this.entrainerFramePosition = entrainerFramePosition;
	}

	/**
	 * Call this method to remove the specified shape from the animation.
	 *
	 * @param shape
	 *          the shape
	 */
	protected void removeFromAnimation(AnimationRectangle2D shape) {
		removables.add(shape);
	}

}
