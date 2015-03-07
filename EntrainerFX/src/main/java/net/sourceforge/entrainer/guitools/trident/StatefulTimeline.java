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
package net.sourceforge.entrainer.guitools.trident;

/*
 * Copyright (C) 2008, 2009, 2010 Burton Alexander
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

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.TimelineScenario;

// TODO: Auto-generated Javadoc
/**
 * This subclass of {@link Timeline} allows its behaviour to be specified prior
 * to any call to its 'play' method. Used by {@link TimelineChain}, and could be
 * used in {@link TimelineScenario}
 * 
 * @author burton
 *
 */
public class StatefulTimeline extends Timeline {

	private RepeatBehavior repeatBehavior = null;
	private int loopCount = -1;
	private long msToSkip = -1;
	private PlayDirection playDirection = PlayDirection.FORWARD;

	/**
	 * The Enum PlayDirection.
	 */
	public enum PlayDirection {

		/** The forward. */
		FORWARD,
		/** The reverse. */
		REVERSE;
	}

	/**
	 * Instantiate with the object to animate.
	 *
	 * @param mainTimelineObject
	 *          the main timeline object
	 */
	public StatefulTimeline(Object mainTimelineObject) {
		super(mainTimelineObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.trident.Timeline#play()
	 */
	public void play() {
		if (getRepeatBehavior() != null) {
			evaluatePlayRepeat();
		} else if (isReverse()) {
			evaluatePlayReverse();
		} else if (isSkipping()) {
			playSkipping(getMsToSkip());
		} else {
			super.play();
		}
	}

	private void evaluatePlayReverse() {
		if (isSkipping()) {
			playReverseSkipping(getMsToSkip());
		} else {
			playReverse();
		}
	}

	private void evaluatePlayRepeat() {
		if (isSkipping()) {
			if (isLooping()) {
				playLoopSkipping(getLoopCount(), getRepeatBehavior(), getMsToSkip());
			} else {
				playLoopSkipping(getRepeatBehavior(), getMsToSkip());
			}
		} else {
			if (isLooping()) {
				playLoop(getLoopCount(), getRepeatBehavior());
			} else {
				playLoop(getRepeatBehavior());
			}
		}
	}

	private boolean isLooping() {
		return getLoopCount() > -1;
	}

	private boolean isSkipping() {
		return getMsToSkip() > -1;
	}

	private boolean isReverse() {
		return PlayDirection.REVERSE.equals(getPlayDirection());
	}

	/**
	 * Gets the repeat behavior.
	 *
	 * @return the repeat behavior
	 */
	public RepeatBehavior getRepeatBehavior() {
		return repeatBehavior;
	}

	/**
	 * Set the repeat behaviour for this timeline.
	 *
	 * @param repeatBehavior
	 *          the new repeat behavior
	 */
	public void setRepeatBehavior(RepeatBehavior repeatBehavior) {
		this.repeatBehavior = repeatBehavior;
	}

	/**
	 * Gets the loop count.
	 *
	 * @return the loop count
	 */
	public int getLoopCount() {
		return loopCount;
	}

	/**
	 * Set the number of loops this timeline will execute.
	 *
	 * @param loopCount
	 *          the new loop count
	 */
	public void setLoopCount(int loopCount) {
		this.loopCount = loopCount;
	}

	/**
	 * Gets the ms to skip.
	 *
	 * @return the ms to skip
	 */
	public long getMsToSkip() {
		return msToSkip;
	}

	/**
	 * Set the number of milliseconds to skip prior to starting.
	 *
	 * @param msToSkip
	 *          the new ms to skip
	 */
	public void setMsToSkip(long msToSkip) {
		this.msToSkip = msToSkip;
	}

	/**
	 * Gets the play direction.
	 *
	 * @return the play direction
	 */
	public PlayDirection getPlayDirection() {
		return playDirection;
	}

	/**
	 * Set to play the timeline either forward or reverse.
	 *
	 * @param playDirection
	 *          the new play direction
	 */
	public void setPlayDirection(PlayDirection playDirection) {
		this.playDirection = playDirection;
	}

}
