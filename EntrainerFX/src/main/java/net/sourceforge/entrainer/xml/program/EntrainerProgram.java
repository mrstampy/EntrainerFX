/*
 * Copyright (C) 2008 - 2014 Burton Alexander
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
package net.sourceforge.entrainer.xml.program;

import static net.sourceforge.entrainer.mediator.MediatorConstants.ANIMATION_BACKGROUND;
import static net.sourceforge.entrainer.mediator.MediatorConstants.ANIMATION_PROGRAM;
import static net.sourceforge.entrainer.mediator.MediatorConstants.FLASH_BACKGROUND;
import static net.sourceforge.entrainer.mediator.MediatorConstants.IS_ANIMATION;
import static net.sourceforge.entrainer.mediator.MediatorConstants.IS_FLASH;
import static net.sourceforge.entrainer.mediator.MediatorConstants.IS_PSYCHEDELIC;
import static net.sourceforge.entrainer.mediator.MediatorConstants.IS_SHIMMER;
import static net.sourceforge.entrainer.mediator.MediatorConstants.SHIMMER_RECTANGLE;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;

// TODO: Auto-generated Javadoc
/**
 * The Class EntrainerProgram.
 */
@XmlRootElement(name = "entrainer")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntrainerProgram {

	@XmlAttribute
	@XmlJavaTypeAdapter(ColourAdapter.class)
	private Color colour;

	@XmlAttribute
	private boolean flash;

	@XmlAttribute
	private boolean psychedelic;

	@XmlAttribute
	private boolean animation;

	@XmlAttribute
	private String animationBackground;

	@XmlAttribute
	private String animationProgram;

	@XmlAttribute
	private boolean shimmer;

	@XmlAttribute
	private String shimmerName;

	@XmlAttribute
	private boolean pinkPan;

	@XmlAttribute
	private boolean flashBackground;

	@XmlElement(name = "interval")
	private List<EntrainerProgramInterval> intervals = new ArrayList<EntrainerProgramInterval>();

	@XmlElement(name = "unit")
	private List<EntrainerProgramUnit> units = new ArrayList<EntrainerProgramUnit>();

	@XmlTransient
	private Sender sender;

	@XmlTransient
	private File file;

	/**
	 * Instantiates a new entrainer program.
	 */
	public EntrainerProgram() {
		sender = new SenderAdapter();
		EntrainerMediator.getInstance().addSender(sender);
	}

	/**
	 * Gets the colour.
	 *
	 * @return the colour
	 */
	public Color getColour() {
		return colour;
	}

	/**
	 * Sets the colour.
	 *
	 * @param colour
	 *          the new colour
	 */
	public void setColour(Color colour) {
		this.colour = colour;
	}

	/**
	 * Checks if is flash.
	 *
	 * @return true, if is flash
	 */
	public boolean isFlash() {
		return flash;
	}

	/**
	 * Sets the flash.
	 *
	 * @param flash
	 *          the new flash
	 */
	public void setFlash(boolean flash) {
		this.flash = flash;
	}

	/**
	 * Checks if is psychedelic.
	 *
	 * @return true, if is psychedelic
	 */
	public boolean isPsychedelic() {
		return psychedelic;
	}

	/**
	 * Sets the psychedelic.
	 *
	 * @param psychedelic
	 *          the new psychedelic
	 */
	public void setPsychedelic(boolean psychedelic) {
		this.psychedelic = psychedelic;
	}

	/**
	 * Gets the intervals.
	 *
	 * @return the intervals
	 */
	public List<EntrainerProgramInterval> getIntervals() {
		return intervals;
	}

	/**
	 * Sets the intervals.
	 *
	 * @param intervals
	 *          the new intervals
	 */
	public void setIntervals(List<EntrainerProgramInterval> intervals) {
		this.intervals = intervals;
	}

	/**
	 * Gets the units.
	 *
	 * @return the units
	 */
	public List<EntrainerProgramUnit> getUnits() {
		return units;
	}

	/**
	 * Sets the units.
	 *
	 * @param units
	 *          the new units
	 */
	public void setUnits(List<EntrainerProgramUnit> units) {
		this.units = units;
	}

	/**
	 * Checks if is animation.
	 *
	 * @return true, if is animation
	 */
	public boolean isAnimation() {
		return animation;
	}

	/**
	 * Sets the animation.
	 *
	 * @param animation
	 *          the new animation
	 */
	public void setAnimation(boolean animation) {
		this.animation = animation;
	}

	/**
	 * Gets the animation background.
	 *
	 * @return the animation background
	 */
	public String getAnimationBackground() {
		return animationBackground;
	}

	/**
	 * Sets the animation background.
	 *
	 * @param animationBackground
	 *          the new animation background
	 */
	public void setAnimationBackground(String animationBackground) {
		this.animationBackground = animationBackground;
	}

	/**
	 * Gets the animation program.
	 *
	 * @return the animation program
	 */
	public String getAnimationProgram() {
		return animationProgram;
	}

	/**
	 * Sets the animation program.
	 *
	 * @param animationProgram
	 *          the new animation program
	 */
	public void setAnimationProgram(String animationProgram) {
		this.animationProgram = animationProgram;
	}

	/**
	 * Checks if is shimmer.
	 *
	 * @return true, if is shimmer
	 */
	public boolean isShimmer() {
		return shimmer;
	}

	/**
	 * Sets the shimmer.
	 *
	 * @param shimmer
	 *          the new shimmer
	 */
	public void setShimmer(boolean shimmer) {
		this.shimmer = shimmer;
	}

	/**
	 * Checks if is pink pan.
	 *
	 * @return true, if is pink pan
	 */
	public boolean isPinkPan() {
		return pinkPan;
	}

	/**
	 * Sets the pink pan.
	 *
	 * @param pinkPan
	 *          the new pink pan
	 */
	public void setPinkPan(boolean pinkPan) {
		this.pinkPan = pinkPan;
	}

	/**
	 * Inits the global settings.
	 */
	public void initGlobalSettings() {
		fireReceiverChangeEvent(isFlash(), IS_FLASH);
		fireReceiverChangeEvent(isPsychedelic(), IS_PSYCHEDELIC);
		fireReceiverChangeEvent(isShimmer(), IS_SHIMMER);
		fireReceiverChangeEvent(isFlashBackground(), FLASH_BACKGROUND);
		fireReceiverChangeEvent(getColour());
		fireReceiverChangeEvent(isAnimation(), IS_ANIMATION);
		fireReceiverChangeEvent(getAnimationBackground(), ANIMATION_BACKGROUND);
		if (getAnimationProgram() != null && getAnimationProgram().trim().length() > 0) {
			fireReceiverChangeEvent(getAnimationProgram(), ANIMATION_PROGRAM);
		}
		if (getShimmerName() != null && isShimmer()) {
			fireReceiverChangeEvent(getShimmerName(), SHIMMER_RECTANGLE);
		}
	}

	/**
	 * Clear mediator objects.
	 */
	public void clearMediatorObjects() {
		EntrainerMediator.getInstance().removeSender(sender);
	}

	private void fireReceiverChangeEvent(Color c) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, c));
	}

	private void fireReceiverChangeEvent(String value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	private void fireReceiverChangeEvent(boolean value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Sets the file.
	 *
	 * @param file
	 *          the new file
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Gets the shimmer name.
	 *
	 * @return the shimmer name
	 */
	public String getShimmerName() {
		return shimmerName;
	}

	/**
	 * Sets the shimmer name.
	 *
	 * @param shimmerName
	 *          the new shimmer name
	 */
	public void setShimmerName(String shimmerName) {
		this.shimmerName = shimmerName;
	}

	/**
	 * Checks if is flash background.
	 *
	 * @return true, if is flash background
	 */
	public boolean isFlashBackground() {
		return flashBackground;
	}

	/**
	 * Sets the flash background.
	 *
	 * @param flashBackground
	 *          the new flash background
	 */
	public void setFlashBackground(boolean flashBackground) {
		this.flashBackground = flashBackground;
	}

}
