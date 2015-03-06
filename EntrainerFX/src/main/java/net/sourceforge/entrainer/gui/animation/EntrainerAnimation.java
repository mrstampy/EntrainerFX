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
package net.sourceforge.entrainer.gui.animation;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.entrainer.gui.AnimationWindow;
import net.sourceforge.entrainer.gui.EntrainerFX;
import net.sourceforge.entrainer.gui.FlashPanel;
import net.sourceforge.entrainer.gui.jfx.animation.JFXEntrainerAnimation;
import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.guitools.MigHelper;

// TODO: Auto-generated Javadoc
/**
 * This class is the super class for all animations. Entrainer picks up new
 * animation classes dynamically - there is no need to recompile the app to run
 * new animations. Consequently, anyone can write new animations for Entrainer
 * and they can be deployed separately.<br>
 * <br>
 * 
 * {@link AnimationWindow} calls the <code>moveShape(Graphics2D, Shape);</code>
 * method twice for each Hz of the entrainment frequency. This is analogous to
 * how the {@link FlashPanel} flashes, and facilitates smoother animation.<br>
 * <br>
 * 
 * {@link EntrainerAnimation} has available all current values ie. amplitude,
 * entrainment frequency etc. to its subclasses. Consequently animation can
 * respond to changes in these values.<br>
 * <br>
 * 
 * The steps to create a new animation are as follows:<br>
 * <br>
 * 
 * - Install Entrainer with all optional packages (default install).<br>
 * <br>
 * 
 * - Create a Java project in your favourite IDE, including 'Entrainer.jar' in
 * the build path (from the Entrainer install directory). Currently Entrainer is
 * built using Java 5 (JDK 1.5).<br>
 * <br>
 * 
 * - Attach the source and javadoc in the Entrainer install directory to
 * Entrainer.jar in the project (optional, but very useful for development)<br>
 * <br>
 * 
 * - Extend this class, implementing the abstract methods. As the animations are
 * instantiated via reflection, ensure the subclass has a constructor with no
 * parameters, and ensure you call <code>super();</code><br>
 * <br>
 * 
 * - Implement your animation.<br>
 * <br>
 * 
 * - Jar your animation class (and any resources it requires ie. images),
 * calling the jar file anything you wish, and put the newly created jar file in
 * the 'animation' directory of the Entrainer install.<br>
 * <br>
 * 
 * - Restart Entrainer, and start debugging your animation!<br>
 * <br>
 * 
 * When creating your animation, you will have access to other Entrainer classes
 * which you may find useful ie. {@link MigHelper} and {@link GuiUtil}. A good
 * working knowledge of the Java2D API is advantageous; developing animations
 * for Entrainer is an excellent way to gain such experience.<br>
 * <br>
 * 
 * If you wish your custom animation(s) to be available with Entrainer, please
 * contact me at <b>burton@users.sourceforge.net</b>.
 *
 * @author burton
 * @deprecated now using {@link JFXEntrainerAnimation}
 */
public abstract class EntrainerAnimation extends AbstractEntrainerAnimation {

	private Rectangle entrainerFramePosition;
	private List<Shape> shapes = new ArrayList<Shape>();
	private List<Shape> removables = new ArrayList<Shape>();

	/**
	 * Call <code>super();</code> in all subclass constructors.
	 */
	public EntrainerAnimation() {
		super();
		initEntrainerFrame();
		setHideEntrainerFrame(false);
	}

	private void initEntrainerFrame() {
		EntrainerFX frame = EntrainerFX.getInstance();
		frame.addComponentListener(getComponentListener());
		setEntrainerFramePosition(frame.getBounds());
	}

	private ComponentListener getComponentListener() {
		return new ComponentAdapter() {

			@Override
			public void componentMoved(ComponentEvent e) {
				setEntrainerFramePosition(e.getComponent().getBounds());
			}

			@Override
			public void componentShown(ComponentEvent e) {
				setEntrainerFramePosition(e.getComponent().getBounds());
			}

		};
	}

	/**
	 * Implement to return a new shape. Typically called by the
	 * <code>maybeAddNewShape();</code> implementation.
	 *
	 * @param position
	 *          the position
	 * @return the new shape
	 */
	protected abstract Shape getNewShape(Point position);

	/**
	 * Implement to decide how to move, change colours of, etc. the specified
	 * shape. The implementation must also draw the shape.
	 *
	 * @param g2d
	 *          the g2d
	 * @param shape
	 *          the shape
	 */
	protected abstract void moveShape(Graphics2D g2d, Shape shape);

	/**
	 * Implement to decide whether or not to add a new shape to the animation. To
	 * do so, call the <code>getNewShape(Point);</code> method, then
	 * <code>addShape(Shape);</code>.
	 */
	public abstract void maybeAddNewShape();

	/**
	 * Call this method to add the specified shape to the animation.
	 *
	 * @param shape
	 *          the shape
	 */
	protected synchronized void addShape(Shape shape) {
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
	 * <code>moveShape(Graphics2D, Shape);</code> method for each.
	 *
	 * @param g2d
	 *          the g2d
	 */
	public synchronized void animate(Graphics2D g2d) {
		maybeAddNewShape();

		super.animate(g2d);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.entrainer.gui.animation.AbstractEntrainerAnimation#animateImpl
	 * (java.awt.Graphics2D)
	 */
	@Override
	protected void animateImpl(Graphics2D g2d) {
		for (Shape shape : getShapesCopy()) {
			moveShape(g2d, shape);
		}

		removeRemovables();
	}

	/**
	 * Returns true if the shape is intersecting (touching) the Entrainer frame.
	 *
	 * @param shape
	 *          the shape
	 * @return true, if is shape intersecting with entrainer frame
	 */
	protected boolean isShapeIntersectingWithEntrainerFrame(Shape shape) {
		return shape.intersects(getEntrainerFramePosition());
	}

	/**
	 * Returns a copy of the list of animations currently being rendered.
	 *
	 * @return the shapes copy
	 */
	protected List<Shape> getShapesCopy() {
		return new ArrayList<Shape>(shapes);
	}

	/**
	 * Returns the number of shapes currently being rendered.
	 *
	 * @return the shapes count
	 */
	protected int getShapesCount() {
		return getShapesCopy().size();
	}

	/**
	 * Returns the current center point of the Entrainer frame.
	 *
	 * @return the center of entrainer frame
	 */
	protected Point getCenterOfEntrainerFrame() {
		return new Point((int) getEntrainerFramePosition().getCenterX(), (int) getEntrainerFramePosition().getCenterY());
	}

	private void removeRemovables() {
		for (Shape shape : removables) {
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
	public Rectangle getEntrainerFramePosition() {
		return entrainerFramePosition;
	}

	private void setEntrainerFramePosition(Rectangle entrainerFramePosition) {
		this.entrainerFramePosition = entrainerFramePosition;
	}

	/**
	 * Call this method to remove the specified shape from the animation.
	 *
	 * @param shape
	 *          the shape
	 */
	protected void removeShapeFromAnimation(Shape shape) {
		removables.add(shape);
	}

}
