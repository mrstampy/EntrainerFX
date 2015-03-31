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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * This class is the super class for all animations. Entrainer picks up new
 * animation classes dynamically - there is no need to recompile the app to run
 * new animations. Consequently, anyone can write new animations for Entrainer
 * and they can be deployed separately.<br>
 * <br>
 * {@link AbstractJFXAnimation} has available all current values ie. amplitude,
 * entrainment frequency etc. to its subclasses. Consequently animation can
 * respond to changes in these values.<br>
 * <br>
 * 
 * @author burton
 */
public abstract class AbstractJFXAnimation {

	private Dimension screenSize = GuiUtil.getWorkingVirtualScreenSize();

	private Random rand = new Random(Calendar.getInstance().getTimeInMillis());

	private Image customImage = null;
	private Color backgroundColour = null;
	private double amplitude;
	private double entrainmentFrequency;
	private double rightFrequency;
	private double leftFrequency;
	private double pinkNoiseAmplitude;
	private double pinkPanLeftAmplitude;
	private double pinkPanRightAmplitude;
	private double pinkPan;

	private List<AnimationInterval> intervals = new CopyOnWriteArrayList<AnimationInterval>();

	private boolean hideEntrainerFrame;

	/**
	 * Call <code>super();</code> in all subclass constructors.
	 */
	public AbstractJFXAnimation() {
		super();
		initMediator();
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				double delta;
				double entFreq;
				switch (e.getParm()) {
				case AMPLITUDE:
					setAmplitude(e.getDoubleValue());
					break;
				case FREQUENCY:
					entFreq = getEntrainmentFrequency();
					setLeftFrequency(e.getDoubleValue());
					setRightFrequency(e.getDoubleValue() + entFreq);
					break;
				case ENTRAINMENT_FREQUENCY:
					setRightFrequency(getLeftFrequency() + e.getDoubleValue());
					setEntrainmentFrequency(e.getDoubleValue());
					break;
				case PINK_NOISE_AMPLITUDE:
					setPinkNoiseAmplitude(e.getDoubleValue());
					break;
				case PINK_PAN_AMPLITUDE:
					setPinkPanAmplitude(e.getDoubleValue());
					break;
				case DELTA_AMPLITUDE:
					delta = getDelta(e, getAmplitude(), e.getEndValue());
					setAmplitude(getAmplitude() + delta);
					break;
				case DELTA_ENTRAINMENT_FREQUENCY:
					delta = getDelta(e, getEntrainmentFrequency(), e.getEndValue());
					entFreq = getEntrainmentFrequency() + delta;
					setRightFrequency(getLeftFrequency() + entFreq);
					break;
				case DELTA_FREQUENCY:
					delta = getDelta(e, getLeftFrequency(), e.getEndValue());
					entFreq = getEntrainmentFrequency();
					double freq = getLeftFrequency() + delta;
					setLeftFrequency(freq);
					setRightFrequency(freq + entFreq);
					break;
				case DELTA_PINK_NOISE_AMPLITUDE:
					delta = getDelta(e, getPinkNoiseAmplitude(), e.getEndValue());
					setPinkNoiseAmplitude(getPinkNoiseAmplitude() + delta);
					break;
				case DELTA_PINK_PAN_AMPLITUDE:
					delta = getDelta(e, getPinkPan(), e.getEndValue());
					setPinkPanAmplitude(getPinkPan() + delta);
					break;
				case INTERVAL_ADD:
					addIntervalFromEvent(e.getStringValue());
					break;
				case INTERVAL_REMOVE:
					removeIntervalFromEvent(e.getStringValue());
					break;
				default:
					break;
				}
			}

		});
	}

	/**
	 * Implement to decide how to move, change colours etc.
	 *
	 * @param gc
	 *          the gc
	 */
	protected abstract void animateImpl(GraphicsContext gc);

	/**
	 * Return true if your animation does not require any specific backgrounds,
	 * else false.
	 *
	 * @return true, if successful
	 */
	public abstract boolean useDesktopBackground();

	/**
	 * Return true if your animation does not require any background; the
	 * <code>setBackgroundColour(Color)</code> method must be called with a
	 * non-null value in the object's instantiation ie. from the constructor.
	 * Else, return false.
	 *
	 * @return true, if successful
	 */
	public abstract boolean useBackgroundColour();

	/**
	 * <code>toString();</code> has been made abstract to enforce its
	 * implementation. Return the name of the animation.
	 *
	 * @return the string
	 */
	public abstract String toString();

	/**
	 * Do not call this directly; it is invoked from {@link JFXAnimationWindow}.
	 * Iterates thru the added shapes and calls the
	 * <code>move(GraphicsContext, AnimationRectangle2D);</code> method for each.
	 *
	 * @param gc
	 *          the gc
	 */
	public synchronized void animate(GraphicsContext gc) {
		animateImpl(gc);
	}

	private void setPinkPanAmplitude(double d) {
		setPinkPan(d);
		double right = d >= 0.5 ? 1.0 : 1 - ((0.5 - d) * 2);
		double left = d <= 0.5 ? 1.0 : 1 - ((d - 0.5) * 2);

		right = 1 - right;
		left = 1 - left;

		setPinkPanLeftAmplitude(left);
		setPinkPanRightAmplitude(right);
	}

	/**
	 * Returns true if the shape has reached the screen edge.
	 *
	 * @param shape
	 *          the shape
	 * @return true, if is at screen edge
	 */
	protected boolean isAtScreenEdge(AnimationRectangle2D shape) {
		return isAtScreenHorizontalEdge(shape) || isAtScreenVerticalEdge(shape);
	}

	/**
	 * Returns true if the shape is at the top edge of the screen.
	 *
	 * @param shape
	 *          the shape
	 * @return true, if is at bottom
	 */
	protected boolean isAtBottom(AnimationRectangle2D shape) {
		return shape.getMinY() <= 0;
	}

	/**
	 * Returns true if the shape is at the bottom edge of the screen.
	 *
	 * @param shape
	 *          the shape
	 * @return true, if is at top
	 */
	protected boolean isAtTop(AnimationRectangle2D shape) {
		AnimationRectangle2D rect = shape;

		return rect.getMinY() + rect.getHeight() >= getScreenSize().getHeight();
	}

	/**
	 * Returns true if the shape is at the left or right edge of the screen.
	 *
	 * @param shape
	 *          the shape
	 * @return true, if is at screen horizontal edge
	 */
	protected boolean isAtScreenHorizontalEdge(AnimationRectangle2D shape) {
		return isAtRight(shape) || isAtLeft(shape);
	}

	/**
	 * Returns true if the shape is at the top or bottom edge of the screen.
	 *
	 * @param shape
	 *          the shape
	 * @return true, if is at screen vertical edge
	 */
	protected boolean isAtScreenVerticalEdge(AnimationRectangle2D shape) {
		return isAtTop(shape) || isAtBottom(shape);
	}

	/**
	 * Returns true if the shape is at the right edge of the screen.
	 *
	 * @param shape
	 *          the shape
	 * @return true, if is at right
	 */
	protected boolean isAtRight(AnimationRectangle2D shape) {
		AnimationRectangle2D rect = shape;

		return rect.getMinX() + rect.getWidth() >= getScreenSize().getWidth();
	}

	/**
	 * Returns true if the shape is at the left edge of the screen.
	 *
	 * @param shape
	 *          the shape
	 * @return true, if is at left
	 */
	protected boolean isAtLeft(AnimationRectangle2D shape) {
		return shape.getMinX() <= 0;
	}

	/**
	 * Returns true if the shape is off the screen.
	 *
	 * @param shape
	 *          the shape
	 * @return true, if is off screen
	 */
	protected boolean isOffScreen(AnimationRectangle2D shape) {
		return isOffScreenLeft(shape) || isOffScreenRight(shape) || isOffScreenTop(shape) || isOffScreenBottom(shape);
	}

	/**
	 * Returns true if the shape is off the right side of the screen.
	 *
	 * @param shape
	 *          the shape
	 * @return true, if is off screen right
	 */
	protected boolean isOffScreenRight(AnimationRectangle2D shape) {
		Dimension d = getScreenSize();
		return shape.getMinX() > d.getWidth() && shape.getMinX() + shape.getWidth() > d.getWidth();
	}

	/**
	 * Returns true if the shape is off the left side of the screen.
	 *
	 * @param shape
	 *          the shape
	 * @return true, if is off screen left
	 */
	protected boolean isOffScreenLeft(AnimationRectangle2D shape) {
		return shape.getMinX() < 0 && shape.getMinX() + shape.getWidth() < 0;
	}

	/**
	 * Returns true if the shape is off the bottom of the screen.
	 *
	 * @param shape
	 *          the shape
	 * @return true, if is off screen bottom
	 */
	protected boolean isOffScreenBottom(AnimationRectangle2D shape) {
		Dimension d = getScreenSize();
		return shape.getMinY() > d.getHeight() && shape.getMinY() + shape.getHeight() > d.getHeight();
	}

	/**
	 * Returns true if the shape is off the top of the screen.
	 *
	 * @param shape
	 *          the shape
	 * @return true, if is off screen top
	 */
	protected boolean isOffScreenTop(AnimationRectangle2D shape) {
		return shape.getMinY() < 0 && shape.getMinY() + shape.getHeight() < 0;
	}

	/**
	 * Returns a random colour with a random alpha (transparency) value.
	 *
	 * @return the random colour and alpha
	 */
	protected Color getRandomColourAndAlpha() {
		double r = getRandomPositiveDouble();
		double g = getRandomPositiveDouble();
		double b = getRandomPositiveDouble();
		double a = getRandomPositiveDouble();

		return new Color(r, g, b, a);
	}

	/**
	 * Returns a random solid colour.
	 *
	 * @return the random colour
	 */
	protected Color getRandomColour() {
		double r = getRandomPositiveDouble();
		double g = getRandomPositiveDouble();
		double b = getRandomPositiveDouble();

		return new Color(r, g, b, 1);
	}

	/**
	 * Returns true 50% of the time. ;>)
	 *
	 * @return the fifty fifty
	 */
	protected boolean getFiftyFifty() {
		return getRandomPositiveDouble() < 0.5;
	}

	/**
	 * Returns '1' 50% of the time, '-1' the other.
	 *
	 * @return the int
	 */
	protected int positiveOrNegative() {
		return getFiftyFifty() ? 1 : -1;
	}

	/**
	 * Returns the dimension representing the renderable area.
	 *
	 * @return the screen size
	 */
	public Dimension getScreenSize() {
		return screenSize;
	}

	/**
	 * Returns a rectangle representing the renderable area.
	 *
	 * @return the screen rectangle
	 */
	public AnimationRectangle2D getScreenRectangle() {
		return new AnimationRectangle2D(0, 0, getScreenSize().getWidth(), getScreenSize().getHeight());
	}

	/**
	 * Returns a random double value between 0 and 1.
	 *
	 * @return the random positive double
	 */
	public double getRandomPositiveDouble() {
		return rand.nextDouble();
	}

	/**
	 * Returns a random double value between -1 and 1.
	 *
	 * @return the random double
	 */
	public double getRandomDouble() {
		return getRandomPositiveDouble() * positiveOrNegative();
	}

	/**
	 * If the <code>useDesktopBackground();</code> implementation returns false,
	 * this returns the image the implementation sets in its initialization.
	 *
	 * @return the custom image
	 */
	public Image getCustomImage() {
		return customImage;
	}

	/**
	 * If the <code>useDesktopBackground();</code> implementation returns false,
	 * call this method in the subclass's initialization with a non-null value.
	 *
	 * @param customImage
	 *          the new custom image
	 */
	protected void setCustomImage(Image customImage) {
		this.customImage = customImage;
	}

	/**
	 * If the <code>useBackgroundColour();</code> implementation returns true,
	 * this method returns the Color the implementation sets in its
	 * initialization.
	 *
	 * @return the background colour
	 */
	public Color getBackgroundColour() {
		return backgroundColour;
	}

	/**
	 * If the <code>useBackgroundColour();</code> implementation returns true,
	 * call this method in the subclass's initialization with a non-null value.
	 *
	 * @param backgroundColour
	 *          the new background colour
	 */
	protected void setBackgroundColour(Color backgroundColour) {
		this.backgroundColour = backgroundColour;
	}

	/**
	 * Returns the current amplitude (volume) of the program's generated sine
	 * waves (between 0 and 1).
	 *
	 * @return the amplitude
	 */
	public double getAmplitude() {
		return amplitude;
	}

	private void setAmplitude(double amplitude) {
		this.amplitude = amplitude;
	}

	/**
	 * Returns the current entrainment frequency (Hz).
	 *
	 * @return the entrainment frequency
	 */
	public double getEntrainmentFrequency() {
		return entrainmentFrequency;
	}

	private void setEntrainmentFrequency(double entrainmentFrequency) {
		this.entrainmentFrequency = entrainmentFrequency;
	}

	/**
	 * Returns the current right channel frequency (Hz).
	 *
	 * @return the right frequency
	 */
	public double getRightFrequency() {
		return rightFrequency;
	}

	private void setRightFrequency(double rightFrequency) {
		this.rightFrequency = rightFrequency;
	}

	/**
	 * Returns the current left channel frequency (Hz).
	 *
	 * @return the left frequency
	 */
	public double getLeftFrequency() {
		return leftFrequency;
	}

	private void setLeftFrequency(double leftFrequency) {
		this.leftFrequency = leftFrequency;
	}

	private double getPinkNoiseAmplitude() {
		return pinkNoiseAmplitude;
	}

	private void setPinkNoiseAmplitude(double pinkNoiseAmplitude) {
		this.pinkNoiseAmplitude = pinkNoiseAmplitude;
	}

	/**
	 * Returns the current amplitude (volume) of the left channel of the pink
	 * noise (between 0 and 1).
	 *
	 * @return the pink pan left amplitude
	 */
	public double getPinkPanLeftAmplitude() {
		return pinkPanLeftAmplitude;
	}

	private void setPinkPanLeftAmplitude(double pinkPanLeftAmplitude) {
		this.pinkPanLeftAmplitude = pinkPanLeftAmplitude;
	}

	/**
	 * Returns the current amplitude (volume) of the right channel of the pink
	 * noise (between 0 and 1).
	 *
	 * @return the pink pan right amplitude
	 */
	public double getPinkPanRightAmplitude() {
		return pinkPanRightAmplitude;
	}

	private void setPinkPanRightAmplitude(double pinkPanRightAmplitude) {
		this.pinkPanRightAmplitude = pinkPanRightAmplitude;
	}

	/**
	 * Returns the amplitude of the panning for the pink noise (between 0 and 1).
	 * This is not the volume of the pink noise, rather how far left and right the
	 * pink noise pans.
	 *
	 * @return the pink pan
	 */
	public double getPinkPan() {
		return pinkPan;
	}

	/**
	 * Returns the list of intervals currently in use.
	 *
	 * @return the intervals
	 * @see AnimationInterval
	 */
	public List<AnimationInterval> getIntervals() {
		return new ArrayList<AnimationInterval>(intervals);
	}

	private void setPinkPan(double pinkPan) {
		this.pinkPan = pinkPan;
	}

	private void addIntervalFromEvent(final String interval) {
		Thread thread = new Thread() {
			public void run() {
				Utils.snooze(100);
				addIntervalControl(interval);
			}
		};

		thread.start();
	}

	private void removeIntervalFromEvent(final String interval) {
		Thread thread = new Thread() {
			public void run() {
				Utils.snooze(100);
				removeIntervalControl(interval);
			}
		};

		thread.start();
	}

	private void addIntervalControl(String displayString) {
		addIntervalControl(AnimationInterval.getNumerator(displayString), AnimationInterval.getDenominator(displayString));
	}

	private void removeIntervalControl(String displayString) {
		removeIntervalControl(AnimationInterval.getNumerator(displayString),
				AnimationInterval.getDenominator(displayString));
	}

	private void initInterval(AnimationInterval interval) {
		interval.setLeftFrequency(getLeftFrequency());
		interval.setLeftAmplitude(getAmplitude());
	}

	private synchronized void addIntervalControl(int intervalNumerator, int intervalDenominator) {
		if (!containsInterval(intervalNumerator, intervalDenominator)) {
			AnimationInterval interval = new AnimationInterval(intervalNumerator, intervalDenominator);
			initInterval(interval);
			intervals.add(interval);
		}
	}

	private void removeIntervalControl(int intervalNumerator, int intervalDenominator) {
		AnimationInterval interval = getInterval(intervalNumerator, intervalDenominator);
		if (interval != null) {
			interval.stop();
			intervals.remove(interval);
		}
	}

	private AnimationInterval getInterval(int num, int denom) {
		for (AnimationInterval interval : intervals) {
			if (interval.isInterval(num, denom)) {
				return interval;
			}
		}

		return null;
	}

	private boolean containsInterval(int num, int denom) {
		return getInterval(num, denom) != null;
	}

	/**
	 * Checks if is hide entrainer frame.
	 *
	 * @return true, if is hide entrainer frame
	 */
	public boolean isHideEntrainerFrame() {
		return hideEntrainerFrame;
	}

	/**
	 * Sets the hide entrainer frame.
	 *
	 * @param hideEntrainerFrame
	 *          the new hide entrainer frame
	 */
	protected void setHideEntrainerFrame(boolean hideEntrainerFrame) {
		this.hideEntrainerFrame = hideEntrainerFrame;
	}

}
