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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Calendar;
import java.util.Random;

import javax.swing.JComponent;

import net.sourceforge.entrainer.gui.jfx.shimmer.AbstractShimmer;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallback;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
import org.pushingpixels.trident.swing.SwingRepaintTimeline;

// TODO: Auto-generated Javadoc
/**
 * This subclass of JPanel is meant to be used as a glass pane object. It
 * creates a shimmer effect over top of the application.
 *
 * @author burton
 * @deprecated now using {@link AbstractShimmer} subclasses.
 */
public class ShimmerPane extends JComponent {
	private static final long serialVersionUID = 1L;
	private boolean canShimmer = true;
	private boolean isStarted = false;
	private Random rand = new Random(Calendar.getInstance().getTimeInMillis());
	private SwingRepaintTimeline repaintThread;
	private ShimmerRectangle rect;
	private Color c1;
	private Color c2;
	private Color c3;
	private Color c4;
	private Float op1;
	private Float op2;

	/** The is first. */
	boolean isFirst = true;
	private Timeline tl;

	/**
	 * Creates a translucent shimmer pane.
	 */
	public ShimmerPane() {
		super();
		setOpaque(false);
		setDoubleBuffered(true);
		initMediator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		if (!isCanShimmer() || !isStarted()) {
			return;
		}
		if (getRect() == null) {
			init();
		}
		getRect().paint((Graphics2D) g);
	}

	private void initVars() {
		c1 = null;
		c2 = null;
		c3 = null;
		c4 = null;
		op1 = null;
		op2 = null;
		isFirst = true;
		tl = null;
	}

	private void init() {
		setRect(new ShimmerRectangle(getSize()));
		initVars();
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

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
				default:
					break;
				}
			}

		});
	}

	private void checkStarted() {
		if (!isCanShimmer() || !isStarted()) {
			return;
		}

		if (getRect() == null) {
			init();
		}

		repaintThread = getRepaintThread();
		repaintThread.playLoop(RepeatBehavior.LOOP);

		if (tl == null || tl.isDone()) {
			Thread thread = new Thread() {
				public void run() {
					runTimelines();
				}
			};

			thread.start();
		}
	}

	private SwingRepaintTimeline getRepaintThread() {
		if (repaintThread == null) {
			repaintThread = new SwingRepaintTimeline(this);
			repaintThread.addCallback(new TimelineCallbackAdapter() {

				@Override
				public void onTimelinePulse(float arg0, float arg1) {
					if (!isCanShimmer() || !isStarted()) {
						repaintThread.cancel();
					}
				}
			});
		}

		return repaintThread;
	}

	private void runTimelines() {
		tl = new Timeline(getRect());

		tl.setDuration(1000);
		tl.addPropertyToInterpolate("color1", getC1(), getC2());
		tl.addPropertyToInterpolate("color2", getC3(), getC4());
		tl.addPropertyToInterpolate("opacity", getOp1(), getOp2());
		tl.addCallback(new TimelineCallback() {

			@Override
			public void onTimelineStateChanged(TimelineState arg0, TimelineState arg1, float arg2, float arg3) {
				if (arg1.equals(TimelineState.DONE)) {
					if (isCanShimmer() && isStarted()) {
						System.gc();
						runTimelines();
					} else {
						reinit();
					}
				}
			}

			@Override
			public void onTimelinePulse(float arg0, float arg1) {
				if (!isCanShimmer() || !isStarted()) {
					if (tl != null) tl.cancel();
					reinit();
				}
			}

			private void reinit() {
				repaint();
				initVars();
			}
		});

		tl.play();
		isFirst = false;
	}

	private boolean isCanShimmer() {
		return canShimmer;
	}

	private void setCanShimmer(boolean canShimmer) {
		this.canShimmer = canShimmer;
	}

	private boolean isStarted() {
		return isStarted;
	}

	private void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	private ShimmerRectangle getRect() {
		return rect;
	}

	private void setRect(ShimmerRectangle rect) {
		this.rect = rect;
	}

	private Color getC1() {
		c1 = isFirst ? getRandomColour() : c2;
		return c1;
	}

	private Color getC2() {
		c2 = isFirst ? getRandomColour() : c3;
		return c2;
	}

	private Color getC3() {
		c3 = isFirst ? getRandomColour() : c4;
		return c3;
	}

	private Color getC4() {
		c4 = getRandomColour();
		return c4;
	}

	private Color getRandomColour() {
		return new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
	}

	private float getOp1() {
		op1 = isFirst ? 0 : op2;
		return op1;
	}

	private float getOp2() {
		op2 = rand.nextFloat();
		return op2;
	}

}
