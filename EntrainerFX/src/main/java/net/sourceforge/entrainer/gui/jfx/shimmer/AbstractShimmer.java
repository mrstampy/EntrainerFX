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
package net.sourceforge.entrainer.gui.jfx.shimmer;

import java.util.Random;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import net.sourceforge.entrainer.gui.EntrainerFX;
import net.sourceforge.entrainer.gui.flash.CurrentEffect;
import net.sourceforge.entrainer.gui.jfx.JFXUtils;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallback;

// TODO: Auto-generated Javadoc
/**
 * Abstract superclass for all shimmer effects.
 *
 * @author burton
 * @param <P>
 *          the {@link Paint} subclass
 */
public abstract class AbstractShimmer<P extends Paint> extends Rectangle {

	private P p1;
	private P p2;

	private boolean canShimmer = true;
	private boolean isStarted = false;
	private boolean inUse = false;

	/** The rand. */
	protected Random rand = new Random(System.currentTimeMillis());

	private Timeline timeLine;

	/** The flash shimmer. */
	protected boolean flashShimmer;
	private boolean flip;

	/**
	 * Instantiates a new abstract shimmer.
	 */
	protected AbstractShimmer() {
		super();
		preInit();

		initMediator();

		setP1(createNewPaint(0));
		resetProps();

		setFill(null);
	}

	/**
	 * Pre init.
	 */
	protected void preInit() {

	}

	/**
	 * Implement to return the name of the shimmer effect.
	 *
	 * @return the string
	 */
	public abstract String toString();

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this, true) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case START_ENTRAINMENT:
					if (isStarted() == e.getBooleanValue()) return;
					setStarted(e.getBooleanValue());
					checkStarted();
					break;
				case IS_SHIMMER:
					if (isCanShimmer() == e.getBooleanValue()) return;
					setCanShimmer(e.getBooleanValue());
					checkStarted();
					break;
				case FLASH_EFFECT:
					JFXUtils.runLater(() -> pulse(e.getEffect()));
					break;
				case APPLY_FLASH_TO_SHIMMER:
					evaluateFlashShimmer(e.getBooleanValue());
					break;
				default:
					break;
				}
			}

		});
	}

	private void evaluateFlashShimmer(boolean b) {
		flashShimmer = b;
		if (!flashShimmer) JFXUtils.resetEffects(this);
	}

	private void setFlashOpacity(boolean b) {
		if (b) {
			if (isCanShimmer()) {
				setOpacity(flip ? 0.5 : 1);
				flip = !flip;
			}
		} else if (getOpacity() != 1) {
			setOpacity(1);
		}
	}

	private void pulse(CurrentEffect currentEffect) {
		if (!flashShimmer) return;

		if (currentEffect.isOpacity()) setFlashOpacity(currentEffect.isPulse());

		setEffect(currentEffect.getEffect());
	}

	private void checkStarted() {
		if (!isCanShimmer() || !isStarted() || !isInUse()) {
			return;
		}

		start();
	}

	/**
	 * Starts the animation. Should not be called by any class other than
	 * {@link EntrainerFX}.
	 */
	public void start() {
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				fadeIn();
			}
		});
	}

	private void fadeIn() {
		timeLine = new Timeline(this);

		timeLine.addPropertyToInterpolate("p1", getP1(), getP2());

		timeLine.addCallback(new TimelineCallback() {

			@Override
			public void onTimelineStateChanged(TimelineState arg0, TimelineState arg1, float arg2, float arg3) {
				if (arg1.equals(TimelineState.DONE)) {
					resetProps();
					initTimeLine();
				}
			}

			@Override
			public void onTimelinePulse(float arg0, float arg1) {
				if (!isCanShimmer() || !isStarted()) {
					stop();
				} else {
					fill();
				}
			}
		});

		timeLine.setDuration(1000);
		timeLine.play();
	}

	private void fadeOut() {
		timeLine = new Timeline(this);

		setP2(createNewPaint(0));

		timeLine.addPropertyToInterpolate("p1", getP1(), getP2());

		timeLine.addCallback(new TimelineCallback() {

			@Override
			public void onTimelineStateChanged(TimelineState arg0, TimelineState arg1, float arg2, float arg3) {
				if (arg1.equals(TimelineState.DONE)) {
					JFXUtils.runLater(new Runnable() {

						@Override
						public void run() {
							setFill(null);
						}
					});
				}
			}

			@Override
			public void onTimelinePulse(float arg0, float arg1) {
				fill();
			}
		});

		timeLine.setDuration(1000);
		timeLine.play();
	}

	/**
	 * Inits the time line.
	 */
	protected void initTimeLine() {
		timeLine = new Timeline(this);

		timeLine.addPropertyToInterpolate("p1", getP1(), getP2());

		timeLine.addCallback(new TimelineCallback() {

			@Override
			public void onTimelineStateChanged(TimelineState arg0, TimelineState arg1, float arg2, float arg3) {
				if (arg1.equals(TimelineState.DONE)) {
					resetProps();
					initTimeLine();
				} else if (arg1.equals(TimelineState.CANCELLED)) {
					fadeOut();
				}
			}

			@Override
			public void onTimelinePulse(float arg0, float arg1) {
				if (!isCanShimmer() || !isStarted()) {
					stop();
				} else {
					fill();
				}
			}
		});

		timeLine.setDuration(1000);
		modifyTimeline(timeLine);

		timeLine.play();
	}

	/**
	 * Modify timeline.
	 *
	 * @param timeLine
	 *          the time line
	 */
	protected void modifyTimeline(Timeline timeLine) {

	}

	private void fill() {
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				setFill(getP1());
			}
		});
	}

	private void resetProps() {
		setP2(createNewPaint());
	}

	/**
	 * Stops the shimmer animation.
	 */
	public void stop() {
		if (timeLine != null) timeLine.cancel();
	}

	/**
	 * Checks if is can shimmer.
	 *
	 * @return true, if is can shimmer
	 */
	protected boolean isCanShimmer() {
		return canShimmer;
	}

	private void setCanShimmer(boolean canShimmer) {
		this.canShimmer = canShimmer;
	}

	/**
	 * Checks if is started.
	 *
	 * @return true, if is started
	 */
	protected boolean isStarted() {
		return isStarted;
	}

	private void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	/**
	 * Gets the p1.
	 *
	 * @return the p1
	 */
	public P getP1() {
		return p1;
	}

	/**
	 * Sets the p1.
	 *
	 * @param p1
	 *          the new p1
	 */
	public void setP1(P p1) {
		this.p1 = p1;
	}

	/**
	 * Gets the p2.
	 *
	 * @return the p2
	 */
	public P getP2() {
		return p2;
	}

	/**
	 * Sets the p2.
	 *
	 * @param p2
	 *          the new p2
	 */
	public void setP2(P p2) {
		this.p2 = p2;
	}

	/**
	 * Creates the new paint.
	 *
	 * @return the p
	 */
	protected P createNewPaint() {
		return createNewPaint(rand.nextDouble());
	}

	/**
	 * Creates the new paint.
	 *
	 * @param opacity
	 *          the opacity
	 * @return the p
	 */
	protected abstract P createNewPaint(double opacity);

	/**
	 * Returns true if this shimmer implementation is currently in use.
	 *
	 * @return true, if is in use
	 */
	public boolean isInUse() {
		return inUse;
	}

	/**
	 * Sets the in use.
	 *
	 * @param inUse
	 *          the new in use
	 */
	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}

}
