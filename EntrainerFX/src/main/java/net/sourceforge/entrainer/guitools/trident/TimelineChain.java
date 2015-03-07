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
 * Copyright (C) 2008, 2009 Burton Alexander
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
import org.pushingpixels.trident.ease.TimelineEase;

// TODO: Auto-generated Javadoc
/**
 * This class allows chaining of Timeline animations, effectively using the 'to'
 * value from the last as the 'from' value of the first. This class currently
 * only works with the 'addPropertyToInterpolateTo(String, Object)' method of
 * Timeline
 * 
 * @author burton
 * @see Timeline
 */
public class TimelineChain {
	private Object mainObject;

	private List<Timeline> transitions = new LinkedList<Timeline>();

	private Iterator<Timeline> iterator;

	private Timeline currentTimeline;

	private boolean looping = false;

	/**
	 * Instantiates a new timeline chain.
	 *
	 * @param mainObject
	 *          the main object
	 */
	public TimelineChain(Object mainObject) {
		setMainObject(mainObject);
	}

	/**
	 * Adds a new timeline to the chain which will execute with the specified
	 * duration.
	 *
	 * @param time
	 *          the time
	 */
	public void addTimeline(long time) {
		addTimeline(time, null);
	}

	/**
	 * Adds a new timeline to the chain which will execute with the specified
	 * duration and ease.
	 *
	 * @param time
	 *          the time
	 * @param ease
	 *          the ease
	 * @see TimelineEase
	 */
	public void addTimeline(long time, TimelineEase ease) {
		addTimeline(time, ease, null);
	}

	/**
	 * Adds a new timeline to the chain which will execute with the specified
	 * duration, ease and repeat behaviour (infinite loop). Note that any
	 * timelines added after will never be executed - this should be the last
	 * timeline in the chain.
	 *
	 * @param time
	 *          the time
	 * @param ease
	 *          the ease
	 * @param repeatBehavior
	 *          the repeat behavior
	 * @see TimelineEase
	 * @see RepeatBehavior
	 */
	public void addTimeline(long time, TimelineEase ease, RepeatBehavior repeatBehavior) {
		addTimeline(time, ease, repeatBehavior, -1);
	}

	/**
	 * Adds a new timeline to the chain which will execute with the specified
	 * duration, ease and repeat behaviour for the number of loop counts.
	 *
	 * @param time
	 *          the time
	 * @param ease
	 *          the ease
	 * @param repeatBehavior
	 *          the repeat behavior
	 * @param loopCount
	 *          the loop count
	 * @see TimelineEase
	 * @see RepeatBehavior
	 */
	public void addTimeline(long time, TimelineEase ease, RepeatBehavior repeatBehavior, int loopCount) {
		StatefulTimeline currentTimeline = new StatefulTimeline(getMainObject());

		if (ease != null) {
			currentTimeline.setEase(ease);
		}
		currentTimeline.setDuration(time);
		currentTimeline.setRepeatBehavior(repeatBehavior);
		currentTimeline.setLoopCount(loopCount);

		addTimeline(currentTimeline);
	}

	/**
	 * Adds the timeline.
	 *
	 * @param tl
	 *          the tl
	 */
	public void addTimeline(Timeline tl) {
		if (tl != null) {
			transitions.add(tl);
			currentTimeline = tl;
			// Once complete, will play the next in the chain
			tl.addCallback(new TimelineCallbackAdapter() {
				public void onTimelineStateChanged(TimelineState oldState, TimelineState newState, float durationFraction,
						float timelinePosition) {
					if (newState.equals(TimelineState.DONE)) {
						playIterator();
					}
				}
			});
		}
	}

	/**
	 * Appends the specified chain to this chain.
	 *
	 * @param chain
	 *          the chain
	 * @return the timeline chain
	 */
	public TimelineChain addChain(TimelineChain chain) {
		if (chain == null) {
			throw new IllegalArgumentException("Chain must not be null");
		}
		transitions.addAll(chain.transitions);

		return this;
	}

	/**
	 * Add a transition property to the current timeline. The 'from' value is
	 * implicitly the state of the property in the main object at the start of the
	 * animation.
	 *
	 * @param <T>
	 *          the generic type
	 * @param property
	 *          the property
	 * @param to
	 *          the to
	 */
	public <T> void addTransition(String property, T to) {
		if (currentTimeline == null) {
			throw new IllegalArgumentException(
					"One of the 'addTimeline' methods must be called prior to adding a transition property");
		}
		currentTimeline.addPropertyToInterpolate(Timeline.<T> property(property).fromCurrent().to(to));
	}

	/**
	 * Play the timeline chain.
	 */
	public void play() {
		synchronized (getMainObject()) {
			setLooping(false);
			initIterator();
		}
	}

	/**
	 * Play loop.
	 */
	public void playLoop() {
		synchronized (getMainObject()) {
			setLooping(true);
			initIterator();
		}
	}

	private void playIterator() {
		if (iterator != null) {
			if (iterator.hasNext()) {
				play(iterator.next());
			} else if (isLooping()) {
				initIterator();
			}
		}
	}

	private void initIterator() {
		iterator = transitions.iterator();
		playIterator();
	}

	/**
	 * Cancel.
	 */
	public void cancel() {
		synchronized (getMainObject()) {
			if (getCurrentPlaying() != null) {
				getCurrentPlaying().cancel();
			}
			iterator = null;
		}
	}

	/**
	 * Pause.
	 */
	public void pause() {
		synchronized (getMainObject()) {
			if (getCurrentPlaying() != null
					&& (getCurrentPlaying().getState().equals(TimelineState.PLAYING_FORWARD) || getCurrentPlaying().getState()
							.equals(TimelineState.PLAYING_REVERSE))) {
				getCurrentPlaying().suspend();
			}
		}
	}

	/**
	 * Resume.
	 */
	public void resume() {
		synchronized (getMainObject()) {
			if (getCurrentPlaying() != null && getCurrentPlaying().getState().equals(TimelineState.SUSPENDED)) {
				getCurrentPlaying().resume();
			}
		}
	}

	private void play(Timeline tl) {
		tl.play();

		setCurrentPlaying(tl);
	}

	/**
	 * Gets the main object.
	 *
	 * @return the main object
	 */
	public Object getMainObject() {
		return mainObject;
	}

	/**
	 * Sets the main object.
	 *
	 * @param mainObject
	 *          the new main object
	 */
	protected void setMainObject(Object mainObject) {
		this.mainObject = mainObject;
	}

	/**
	 * Gets the current playing.
	 *
	 * @return the current playing
	 */
	public Timeline getCurrentPlaying() {
		return currentTimeline;
	}

	/**
	 * Sets the current playing.
	 *
	 * @param currentPlaying
	 *          the new current playing
	 */
	protected void setCurrentPlaying(Timeline currentPlaying) {
		this.currentTimeline = currentPlaying;
	}

	/**
	 * Checks if is looping.
	 *
	 * @return true, if is looping
	 */
	public boolean isLooping() {
		return looping;
	}

	private void setLooping(boolean looping) {
		this.looping = looping;
	}
}
