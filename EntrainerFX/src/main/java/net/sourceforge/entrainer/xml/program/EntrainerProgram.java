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
package net.sourceforge.entrainer.xml.program;

import static net.sourceforge.entrainer.mediator.MediatorConstants.ANIMATION_BACKGROUND;
import static net.sourceforge.entrainer.mediator.MediatorConstants.ANIMATION_PROGRAM;
import static net.sourceforge.entrainer.mediator.MediatorConstants.IS_ANIMATION;
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

	@XmlElement(name = "dynamic.background")
	private boolean dynamicPicture;

	@XmlElement(name = "static.background")
	private boolean staticPicture;

	@XmlElement(name = "static.background.picture")
	private String staticPictureFile;

	@XmlElement(name = "static.picture.lock")
	private boolean staticPictureLock;

	@XmlElement(name = "no.background")
	private boolean noPicture;

	@XmlElement(name = "picture.directory")
	private String pictureDirectory;

	@XmlElement(name = "background.colour")
	@XmlJavaTypeAdapter(ColourAdapter.class)
	private Color backgroundColour;

	@XmlElement(name = "dynamic.duration")
	private int dynamicDuration;

	@XmlElement(name = "dynamic.transition")
	private int dynamicTransition;

	@XmlElement
	private boolean useDesktopAsBackground;

	@XmlElement(name = "opacity.flash")
	private boolean opacity;

	@XmlElement(name = "bloom.flash")
	private boolean bloom;

	@XmlElement(name = "boxBlur.flash")
	private boolean boxBlur;

	@XmlElement(name = "gaussianBlur.flash")
	private boolean gaussianBlur;

	@XmlElement(name = "glow.flash")
	private boolean glow;

	@XmlElement(name = "motionBlur.flash")
	private boolean motionBlur;

	@XmlElement(name = "sepiaTone.flash")
	private boolean sepiaTone;

	@XmlElement(name = "shadow.flash")
	private boolean shadow;

	@XmlElement(name = "colourAdjust.flash")
	private boolean colourAdjust;

	@XmlElement(name = "lighting.flash")
	private boolean lighting;

	@XmlElement(name = "media.loop")
	private boolean mediaLoop;

	@XmlElement(name = "media.entrainment")
	private boolean mediaEntrainment;

	@XmlElement(name = "media.uri")
	private String mediaUri;

	/**
	 * Instantiates a new entrainer program.
	 */
	public EntrainerProgram() {
		sender = new SenderAdapter();
		EntrainerMediator.getInstance().addSender(sender);
		dynamicPicture = true;
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
		fireReceiverChangeEvent(isPsychedelic(), IS_PSYCHEDELIC);
		fireReceiverChangeEvent(isShimmer(), IS_SHIMMER);
		fireReceiverChangeEvent(isFlashBackground(), MediatorConstants.APPLY_FLASH_TO_BACKGROUND);
		fireReceiverChangeEvent(isAnimation(), IS_ANIMATION);
		fireReceiverChangeEvent(getAnimationBackground(), ANIMATION_BACKGROUND);
		if (getAnimationProgram() != null && getAnimationProgram().trim().length() > 0) {
			fireReceiverChangeEvent(getAnimationProgram(), ANIMATION_PROGRAM);
		}
		fireReceiverChangeEvent(isUseDesktopAsBackground(), MediatorConstants.ANIMATION_DESKTOP_BACKGROUND);
		if (getShimmerName() != null && isShimmer()) {
			fireReceiverChangeEvent(getShimmerName(), SHIMMER_RECTANGLE);
		}

		if (getPictureDirectory() != null) {
			fireReceiverChangeEvent(getPictureDirectory(), MediatorConstants.BACKGROUND_PIC_DIR);
		}

		if (isDynamicPicture()) {
			fireReceiverChangeEvent(true, MediatorConstants.DYNAMIC_BACKGROUND);
		}

		if (isStaticPicture()) {
			fireReceiverChangeEvent(true, MediatorConstants.STATIC_BACKGROUND);
			fireReceiverChangeEvent(getStaticPictureFile(), MediatorConstants.BACKGROUND_PIC);
		}

		if (isNoPicture()) {
			fireReceiverChangeEvent(true, MediatorConstants.NO_BACKGROUND);
			fireReceiverChangeEvent(getBackgroundColour(), MediatorConstants.NO_BACKGROUND_COLOUR);
		}

		fireReceiverChangeEvent(isStaticPictureLock(), MediatorConstants.STATIC_PICTURE_LOCK);
		fireReceiverChangeEvent(getDynamicDuration(), MediatorConstants.BACKGROUND_DURATION_SECONDS);
		fireReceiverChangeEvent(getDynamicTransition(), MediatorConstants.BACKGROUND_TRANSITION_SECONDS);

		fireReceiverChangeEvent(isFlashBackground(), MediatorConstants.APPLY_FLASH_TO_BACKGROUND);
	}

	/**
	 * Clear mediator objects.
	 */
	public void clearMediatorObjects() {
		EntrainerMediator.getInstance().removeSender(sender);
	}

	private void fireReceiverChangeEvent(Color c, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, c, parm));
	}

	private void fireReceiverChangeEvent(String value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	private void fireReceiverChangeEvent(boolean value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	private void fireReceiverChangeEvent(int value, MediatorConstants parm) {
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

	/**
	 * Gets the sender.
	 *
	 * @return the sender
	 */
	public Sender getSender() {
		return sender;
	}

	/**
	 * Sets the sender.
	 *
	 * @param sender
	 *          the new sender
	 */
	public void setSender(Sender sender) {
		this.sender = sender;
	}

	/**
	 * Checks if is dynamic picture.
	 *
	 * @return true, if is dynamic picture
	 */
	public boolean isDynamicPicture() {
		return dynamicPicture;
	}

	/**
	 * Sets the dynamic picture.
	 *
	 * @param dynamicPicture
	 *          the new dynamic picture
	 */
	public void setDynamicPicture(boolean dynamicPicture) {
		this.dynamicPicture = dynamicPicture;
	}

	/**
	 * Checks if is static picture.
	 *
	 * @return true, if is static picture
	 */
	public boolean isStaticPicture() {
		return staticPicture;
	}

	/**
	 * Sets the static picture.
	 *
	 * @param staticPicture
	 *          the new static picture
	 */
	public void setStaticPicture(boolean staticPicture) {
		this.staticPicture = staticPicture;
	}

	/**
	 * Gets the static picture file.
	 *
	 * @return the static picture file
	 */
	public String getStaticPictureFile() {
		return staticPictureFile;
	}

	/**
	 * Sets the static picture file.
	 *
	 * @param staticPictureFile
	 *          the new static picture file
	 */
	public void setStaticPictureFile(String staticPictureFile) {
		this.staticPictureFile = staticPictureFile;
	}

	/**
	 * Checks if is static picture lock.
	 *
	 * @return true, if is static picture lock
	 */
	public boolean isStaticPictureLock() {
		return staticPictureLock;
	}

	/**
	 * Sets the static picture lock.
	 *
	 * @param staticPictureLock
	 *          the new static picture lock
	 */
	public void setStaticPictureLock(boolean staticPictureLock) {
		this.staticPictureLock = staticPictureLock;
	}

	/**
	 * Checks if is no picture.
	 *
	 * @return true, if is no picture
	 */
	public boolean isNoPicture() {
		return noPicture;
	}

	/**
	 * Sets the no picture.
	 *
	 * @param noPicture
	 *          the new no picture
	 */
	public void setNoPicture(boolean noPicture) {
		this.noPicture = noPicture;
	}

	/**
	 * Gets the picture directory.
	 *
	 * @return the picture directory
	 */
	public String getPictureDirectory() {
		return pictureDirectory;
	}

	/**
	 * Sets the picture directory.
	 *
	 * @param pictureDirectory
	 *          the new picture directory
	 */
	public void setPictureDirectory(String pictureDirectory) {
		this.pictureDirectory = pictureDirectory;
	}

	/**
	 * Gets the dynamic duration.
	 *
	 * @return the dynamic duration
	 */
	public int getDynamicDuration() {
		return dynamicDuration;
	}

	/**
	 * Sets the dynamic duration.
	 *
	 * @param dynamicDuration
	 *          the new dynamic duration
	 */
	public void setDynamicDuration(int dynamicDuration) {
		this.dynamicDuration = dynamicDuration;
	}

	/**
	 * Gets the dynamic transition.
	 *
	 * @return the dynamic transition
	 */
	public int getDynamicTransition() {
		return dynamicTransition;
	}

	/**
	 * Sets the dynamic transition.
	 *
	 * @param dynamicTransition
	 *          the new dynamic transition
	 */
	public void setDynamicTransition(int dynamicTransition) {
		this.dynamicTransition = dynamicTransition;
	}

	/**
	 * Gets the background colour.
	 *
	 * @return the background colour
	 */
	public Color getBackgroundColour() {
		return backgroundColour;
	}

	/**
	 * Sets the background colour.
	 *
	 * @param backgroundColour
	 *          the new background colour
	 */
	public void setBackgroundColour(Color backgroundColour) {
		this.backgroundColour = backgroundColour;
	}

	/**
	 * Checks if is use desktop as background.
	 *
	 * @return true, if is use desktop as background
	 */
	public boolean isUseDesktopAsBackground() {
		return useDesktopAsBackground;
	}

	/**
	 * Sets the use desktop as background.
	 *
	 * @param useDesktopAsBackground
	 *          the new use desktop as background
	 */
	public void setUseDesktopAsBackground(boolean useDesktopAsBackground) {
		this.useDesktopAsBackground = useDesktopAsBackground;
	}

	/**
	 * Checks if is opacity.
	 *
	 * @return true, if is opacity
	 */
	public boolean isOpacity() {
		return opacity;
	}

	/**
	 * Sets the opacity.
	 *
	 * @param opacity
	 *          the new opacity
	 */
	public void setOpacity(boolean opacity) {
		this.opacity = opacity;
	}

	/**
	 * Checks if is bloom.
	 *
	 * @return true, if is bloom
	 */
	public boolean isBloom() {
		return bloom;
	}

	/**
	 * Sets the bloom.
	 *
	 * @param bloom
	 *          the new bloom
	 */
	public void setBloom(boolean bloom) {
		this.bloom = bloom;
	}

	/**
	 * Checks if is box blur.
	 *
	 * @return true, if is box blur
	 */
	public boolean isBoxBlur() {
		return boxBlur;
	}

	/**
	 * Sets the box blur.
	 *
	 * @param boxBlur
	 *          the new box blur
	 */
	public void setBoxBlur(boolean boxBlur) {
		this.boxBlur = boxBlur;
	}

	/**
	 * Checks if is gaussian blur.
	 *
	 * @return true, if is gaussian blur
	 */
	public boolean isGaussianBlur() {
		return gaussianBlur;
	}

	/**
	 * Sets the gaussian blur.
	 *
	 * @param gaussianBlur
	 *          the new gaussian blur
	 */
	public void setGaussianBlur(boolean gaussianBlur) {
		this.gaussianBlur = gaussianBlur;
	}

	/**
	 * Checks if is glow.
	 *
	 * @return true, if is glow
	 */
	public boolean isGlow() {
		return glow;
	}

	/**
	 * Sets the glow.
	 *
	 * @param glow
	 *          the new glow
	 */
	public void setGlow(boolean glow) {
		this.glow = glow;
	}

	/**
	 * Checks if is motion blur.
	 *
	 * @return true, if is motion blur
	 */
	public boolean isMotionBlur() {
		return motionBlur;
	}

	/**
	 * Sets the motion blur.
	 *
	 * @param motionBlur
	 *          the new motion blur
	 */
	public void setMotionBlur(boolean motionBlur) {
		this.motionBlur = motionBlur;
	}

	/**
	 * Checks if is sepia tone.
	 *
	 * @return true, if is sepia tone
	 */
	public boolean isSepiaTone() {
		return sepiaTone;
	}

	/**
	 * Sets the sepia tone.
	 *
	 * @param sepiaTone
	 *          the new sepia tone
	 */
	public void setSepiaTone(boolean sepiaTone) {
		this.sepiaTone = sepiaTone;
	}

	/**
	 * Checks if is shadow.
	 *
	 * @return true, if is shadow
	 */
	public boolean isShadow() {
		return shadow;
	}

	/**
	 * Sets the shadow.
	 *
	 * @param shadow
	 *          the new shadow
	 */
	public void setShadow(boolean shadow) {
		this.shadow = shadow;
	}

	/**
	 * Checks if is colour adjust.
	 *
	 * @return true, if is colour adjust
	 */
	public boolean isColourAdjust() {
		return colourAdjust;
	}

	/**
	 * Sets the colour adjust.
	 *
	 * @param colourAdjust
	 *          the new colour adjust
	 */
	public void setColourAdjust(boolean colourAdjust) {
		this.colourAdjust = colourAdjust;
	}

	/**
	 * Checks if is lighting.
	 *
	 * @return true, if is lighting
	 */
	public boolean isLighting() {
		return lighting;
	}

	/**
	 * Sets the lighting.
	 *
	 * @param lighting
	 *          the new lighting
	 */
	public void setLighting(boolean lighting) {
		this.lighting = lighting;
	}

	/**
	 * Checks if is media loop.
	 *
	 * @return true, if is media loop
	 */
	public boolean isMediaLoop() {
		return mediaLoop;
	}

	/**
	 * Sets the media loop.
	 *
	 * @param mediaLoop
	 *          the new media loop
	 */
	public void setMediaLoop(boolean mediaLoop) {
		this.mediaLoop = mediaLoop;
	}

	/**
	 * Checks if is media entrainment.
	 *
	 * @return true, if is media entrainment
	 */
	public boolean isMediaEntrainment() {
		return mediaEntrainment;
	}

	/**
	 * Sets the media entrainment.
	 *
	 * @param mediaEntrainment
	 *          the new media entrainment
	 */
	public void setMediaEntrainment(boolean mediaEntrainment) {
		this.mediaEntrainment = mediaEntrainment;
	}

	/**
	 * Gets the media uri.
	 *
	 * @return the media uri
	 */
	public String getMediaUri() {
		return mediaUri;
	}

	/**
	 * Sets the media uri.
	 *
	 * @param mediaUri
	 *          the new media uri
	 */
	public void setMediaUri(String mediaUri) {
		this.mediaUri = mediaUri;
	}

}
