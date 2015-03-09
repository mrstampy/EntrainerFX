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
package net.sourceforge.entrainer.socket;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sourceforge.entrainer.xml.Settings;

import com.fasterxml.jackson.annotation.JsonIgnore;

// TODO: Auto-generated Javadoc
/**
 * Used by {@link EntrainerSocketManager} as a representation of valid messages
 * to send/receive via the socket specified in {@link Settings#getSocketPort()}. <br>
 * <br>
 * The schema: <br>
 * 
 * <pre>
 * {@code
 * 
 * <?xml version="1.0" standalone="yes"?>
 * <xs:schema version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
 * 	
 *  <xs:element name="entrainerStateMessage" type="entrainerStateMessage"/>
 * 	
 *  <xs:complexType name="entrainerStateMessage">
 *    <xs:sequence>
 *      <xs:element name="amplitude" type="xs:double" minOccurs="0" minInclusive="0" maxInclusive="1"/>
 *      <xs:element name="entrainmentFrequency" type="xs:double" minOccurs="0" minInclusive="0" maxInclusive="40"/>
 *      <xs:element name="frequency" type="xs:double" minOccurs="0" minInclusive="20" maxInclusive="500"/>
 *      <xs:element name="pinkNoiseMultiple" type="xs:double" minOccurs="0" minInclusive="1" maxInclusive="512"/>
 *      <xs:element name="pinkNoiseAmplitude" type="xs:double" minOccurs="0" minInclusive="0" maxInclusive="1"/>
 *      <xs:element name="pinkPanAmplitude" type="xs:double" minOccurs="0" minInclusive="0" maxInclusive="1"/>
 *      <xs:element name="pinkPan" type="xs:boolean" minOccurs="0"/>
 *      <xs:element name="deltaAmplitude" type="xs:double" minOccurs="0"/>
 *      <xs:element name="deltaEntrainmentFrequency" type="xs:double" minOccurs="0"/>
 *      <xs:element name="deltaFrequency" type="xs:double" minOccurs="0"/>
 *      <xs:element name="deltaPinkEntrainerMultiple" type="xs:double" minOccurs="0"/>
 *      <xs:element name="deltaPinkNoiseAmplitude" type="xs:double" minOccurs="0"/>
 *      <xs:element name="deltaPinkPanAmplitude" type="xs:double" minOccurs="0"/>
 *      <xs:element name="intervalAdd" type="xs:string" minOccurs="0"/>
 *      <xs:element name="intervalRemove" type="xs:string" minOccurs="0"/>
 *      <xs:element name="startEntrainment" type="xs:boolean" minOccurs="0"/>
 *      <xs:element name="startFlashing" type="xs:boolean" minOccurs="0"/>
 *      <xs:element name="flash" type="xs:boolean" minOccurs="0"/>
 *      <xs:element name="psychedelic" type="xs:boolean" minOccurs="0"/>
 *      <xs:element name="animation" type="xs:boolean" minOccurs="0"/>
 *      <xs:element name="shimmer" type="xs:boolean" minOccurs="0"/>
 *      <xs:element name="flashColour" type="flashColour" minOccurs="0"/>
 *    </xs:sequence>
 *  </xs:complexType>
 * 
 *  <xs:complexType name="flashColour">
 *    <xs:sequence>
 *      <xs:element name="red" type="xs:int" minInclusive="0" maxInclusive="255"/>
 *      <xs:element name="green" type="xs:int" minInclusive="0" maxInclusive="255"/>
 *      <xs:element name="blue" type="xs:int" minInclusive="0" maxInclusive="255"/>
 *      <xs:element name="alpha" type="xs:int" minInclusive="0" maxInclusive="255"/>
 *    </xs:sequence>
 *  </xs:complexType>
 * </xs:schema>
 * 
 * Example message:
 * 
 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
 * <entrainerStateMessage>
 *    <amplitude>0.5</amplitude>
 *    <entrainmentFrequency>7.83</entrainmentFrequency>
 *    <frequency>100.0</frequency>
 *    <pinkNoiseMultiple>45.0</pinkNoiseMultiple>
 *    <pinkNoiseAmplitude>0.5</pinkNoiseAmplitude>
 *    <pinkPanAmplitude>0.5</pinkPanAmplitude>
 *    <pinkPan>true</pinkPan>
 *    <deltaAmplitude>3.4E-4</deltaAmplitude>
 *    <deltaEntrainmentFrequency>3.4E-4</deltaEntrainmentFrequency>
 *    <deltaFrequency>3.4E-4</deltaFrequency>
 *    <deltaPinkEntrainerMultiple>3.4E-4</deltaPinkEntrainerMultiple>
 *    <deltaPinkNoiseAmplitude>3.4E-4</deltaPinkNoiseAmplitude>
 *    <deltaPinkPanAmplitude>3.4E-5</deltaPinkPanAmplitude>
 *    <intervalAdd>1/12</intervalAdd>
 *    <intervalRemove>3/12</intervalRemove>
 *    <startEntrainment>true</startEntrainment>
 *    <startFlashing>false</startFlashing>
 *    <flash>false</flash>
 *    <psychedelic>true</psychedelic>
 *    <animation>false</animation>
 *    <shimmer>true</shimmer>
 *    <flashColour>
 *        <red>255</red>
 *        <green>0</green>
 *        <blue>255</blue>
 *        <alpha>255</alpha>
 *    </flashColour>
 * </entrainerStateMessage>
 * 
 * }
 * </pre>
 * 
 * Entrainer uses the Apache MINA project to create sockets, and its
 * PrefixedStringCodec. This prepends each message with 4 bytes indicating how
 * many bytes there are in the message. The following code snippet will help
 * should the connecting socket not be implemented with Java/MINA:
 * 
 * <pre>
 * {@code
 *           
 * private byte[] getMessagePrefixForLength(int length) {
 *   byte[] prefix = new byte[4];
 * 
 *   for (int i = 3; i >= 0; i--) {
 *     int val = length % 256;
 *     length /= 256;
 *     prefix[i] = new Integer(val).byteValue();
 *   }
 * 
 *   return prefix;
 * }
 * 
 * private int getMessageLengthForPrefix(byte[] prefix) {
 *   int size = 0;
 *   for (int i = 0; i < prefix.length; i++) {
 *     int j = prefix[i];
 *     if (j != 0) {
 *       if (size > 0) size *= 256;
 *       size += j > 0 ? j : 256 + j;
 *     }
 *   }
 * 
 *   return size;
 * }
 * 
 * }
 * </pre>
 * 
 * @author burton
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EntrainerStateMessage {

	@XmlElement
	private Double amplitude;

	@XmlElement
	private Double entrainmentFrequency;

	@XmlElement
	private Double frequency;

	@XmlElement
	private Double pinkNoiseMultiple;

	@XmlElement
	private Double pinkNoiseAmplitude;

	@XmlElement
	private Double pinkPanAmplitude;

	@XmlElement
	private Boolean pinkPan;

	@XmlElement
	private Double deltaAmplitude;

	@XmlElement
	private Double deltaEntrainmentFrequency;

	@XmlElement
	private Double deltaFrequency;

	@XmlElement
	private Double deltaPinkEntrainerMultiple;

	@XmlElement
	private Double deltaPinkNoiseAmplitude;

	@XmlElement
	private Double deltaPinkPanAmplitude;

	@XmlElement
	private String intervalAdd;

	@XmlElement
	private String intervalRemove;

	@XmlElement
	private Boolean startEntrainment;

	@XmlElement
	private Boolean psychedelic;

	@XmlElement
	private Boolean animation;

	@XmlElement
	private Boolean shimmer;

	@XmlElement
	private Boolean requestState;

	@XmlElement
	private Boolean flashBackground;

	@XmlElement
	private Boolean dynamicPicture;

	@XmlElement
	private Boolean staticPicture;

	@XmlElement
	private String staticPictureFile;

	@XmlElement
	private Boolean staticPictureLock;

	@XmlElement
	private Boolean noPicture;

	@XmlElement
	private String pictureDirectory;

	@XmlElement
	private FlashColour backgroundColour;

	@XmlElement
	private Integer dynamicDuration;

	@XmlElement
	private Integer dynamicTransition;

	/**
	 * The volume of Entrainer. Valid values are between 0 and 1.
	 *
	 * @return the amplitude
	 */
	public Double getAmplitude() {
		return amplitude;
	}

	/**
	 * Sets the amplitude.
	 *
	 * @param amplitude
	 *          the new amplitude
	 */
	public void setAmplitude(Double amplitude) {
		doubleCheck(amplitude, 0, 1);
		this.amplitude = amplitude;
	}

	/**
	 * The entrainment frequency. Valid values are between 0 and 40 Hz.
	 *
	 * @return the entrainment frequency
	 */
	public Double getEntrainmentFrequency() {
		return entrainmentFrequency;
	}

	/**
	 * Sets the entrainment frequency.
	 *
	 * @param entrainmentFrequency
	 *          the new entrainment frequency
	 */
	public void setEntrainmentFrequency(Double entrainmentFrequency) {
		doubleCheck(entrainmentFrequency, 0, 40);
		this.entrainmentFrequency = entrainmentFrequency;
	}

	/**
	 * The base frequency. Valid values are between 20 and 500 Hz.
	 *
	 * @return the frequency
	 */
	public Double getFrequency() {
		return frequency;
	}

	/**
	 * Sets the frequency.
	 *
	 * @param frequency
	 *          the new frequency
	 */
	public void setFrequency(Double frequency) {
		doubleCheck(frequency, 20, 500);
		this.frequency = frequency;
	}

	/**
	 * A multiplier to control the speed of the pink noise panning. Valid values
	 * are between 1 and 512.
	 *
	 * @return the pink noise multiple
	 */
	public Double getPinkNoiseMultiple() {
		return pinkNoiseMultiple;
	}

	/**
	 * Sets the pink noise multiple.
	 *
	 * @param pinkNoiseMultiple
	 *          the new pink noise multiple
	 */
	public void setPinkNoiseMultiple(Double pinkNoiseMultiple) {
		doubleCheck(pinkNoiseMultiple, 1, 512);
		this.pinkNoiseMultiple = pinkNoiseMultiple;
	}

	/**
	 * The volume of the pink noise. Valid values are between 0 and 1.
	 *
	 * @return the pink noise amplitude
	 */
	public Double getPinkNoiseAmplitude() {
		return pinkNoiseAmplitude;
	}

	/**
	 * Sets the pink noise amplitude.
	 *
	 * @param pinkNoiseAmplitude
	 *          the new pink noise amplitude
	 */
	public void setPinkNoiseAmplitude(Double pinkNoiseAmplitude) {
		doubleCheck(pinkNoiseAmplitude, 0, 1);
		this.pinkNoiseAmplitude = pinkNoiseAmplitude;
	}

	/**
	 * The pan amplitude (between the left and right ear) for the pink noise.
	 * Valid values are between 0 and 1.
	 *
	 * @return the pink pan amplitude
	 */
	public Double getPinkPanAmplitude() {
		return pinkPanAmplitude;
	}

	/**
	 * Sets the pink pan amplitude.
	 *
	 * @param pinkPanAmplitude
	 *          the new pink pan amplitude
	 */
	public void setPinkPanAmplitude(Double pinkPanAmplitude) {
		doubleCheck(pinkPanAmplitude, 0, 1);
		this.pinkPanAmplitude = pinkPanAmplitude;
	}

	/**
	 * Boolean to control whether pink noise panning is enabled.
	 *
	 * @return the pink pan
	 */
	public Boolean getPinkPan() {
		return pinkPan;
	}

	/**
	 * Sets the pink pan.
	 *
	 * @param pinkPan
	 *          the new pink pan
	 */
	public void setPinkPan(Boolean pinkPan) {
		this.pinkPan = pinkPan;
	}

	/**
	 * The change in amplitude during the execution of an Entrainer program.
	 * {@link #getAmplitude()} will contain the actual value.
	 *
	 * @return the delta amplitude
	 */
	public Double getDeltaAmplitude() {
		return deltaAmplitude;
	}

	/**
	 * Sets the delta amplitude.
	 *
	 * @param deltaAmplitude
	 *          the new delta amplitude
	 */
	public void setDeltaAmplitude(Double deltaAmplitude) {
		this.deltaAmplitude = deltaAmplitude;
	}

	/**
	 * The change in entrainment frequency during the execution of an Entrainer
	 * program. {@link #getEntrainmentFrequency()} will contain the actual value.
	 *
	 * @return the delta entrainment frequency
	 */
	public Double getDeltaEntrainmentFrequency() {
		return deltaEntrainmentFrequency;
	}

	/**
	 * Sets the delta entrainment frequency.
	 *
	 * @param deltaEntrainmentFrequency
	 *          the new delta entrainment frequency
	 */
	public void setDeltaEntrainmentFrequency(Double deltaEntrainmentFrequency) {
		this.deltaEntrainmentFrequency = deltaEntrainmentFrequency;
	}

	/**
	 * The change in frequency during the execution of an Entrainer program.
	 * {@link #getFrequency()} will contain the actual value.
	 *
	 * @return the delta frequency
	 */
	public Double getDeltaFrequency() {
		return deltaFrequency;
	}

	/**
	 * Sets the delta frequency.
	 *
	 * @param deltaFrequency
	 *          the new delta frequency
	 */
	public void setDeltaFrequency(Double deltaFrequency) {
		this.deltaFrequency = deltaFrequency;
	}

	/**
	 * The change in pink noise multiple during the execution of an Entrainer
	 * program. {@link #getPinkNoiseMultiple()} will contain the actual value.
	 *
	 * @return the delta pink entrainer multiple
	 */
	public Double getDeltaPinkEntrainerMultiple() {
		return deltaPinkEntrainerMultiple;
	}

	/**
	 * Sets the delta pink entrainer multiple.
	 *
	 * @param deltaPinkEntrainerMultiple
	 *          the new delta pink entrainer multiple
	 */
	public void setDeltaPinkEntrainerMultiple(Double deltaPinkEntrainerMultiple) {
		this.deltaPinkEntrainerMultiple = deltaPinkEntrainerMultiple;
	}

	/**
	 * The change in pink noise volume during the execution of an Entrainer
	 * program. {@link #getPinkNoiseAmplitude()} will contain the actual value.
	 *
	 * @return the delta pink noise amplitude
	 */
	public Double getDeltaPinkNoiseAmplitude() {
		return deltaPinkNoiseAmplitude;
	}

	/**
	 * Sets the delta pink noise amplitude.
	 *
	 * @param deltaPinkNoiseAmplitude
	 *          the new delta pink noise amplitude
	 */
	public void setDeltaPinkNoiseAmplitude(Double deltaPinkNoiseAmplitude) {
		this.deltaPinkNoiseAmplitude = deltaPinkNoiseAmplitude;
	}

	/**
	 * The change in pink pan amplitude during the execution of an Entrainer
	 * program. {@link #getPinkPanAmplitude()} will contain the actual value.
	 *
	 * @return the delta pink pan amplitude
	 */
	public Double getDeltaPinkPanAmplitude() {
		return deltaPinkPanAmplitude;
	}

	/**
	 * Sets the delta pink pan amplitude.
	 *
	 * @param deltaPinkPanAmplitude
	 *          the new delta pink pan amplitude
	 */
	public void setDeltaPinkPanAmplitude(Double deltaPinkPanAmplitude) {
		this.deltaPinkPanAmplitude = deltaPinkPanAmplitude;
	}

	/**
	 * An interval added to the base {@link #getFrequency()}. String
	 * representation of a fraction ie. 1/12, included as interval defaults in the
	 * Entrainer program.
	 *
	 * @return the interval add
	 */
	public String getIntervalAdd() {
		return intervalAdd;
	}

	/**
	 * Sets the interval add.
	 *
	 * @param intervalAdd
	 *          the new interval add
	 */
	public void setIntervalAdd(String intervalAdd) {
		intervalCheck(intervalAdd);
		this.intervalAdd = intervalAdd;
	}

	/**
	 * An interval removed from the base {@link #getFrequency()}. String
	 * representation of a fraction ie. 1/12
	 *
	 * @return the interval remove
	 */
	public String getIntervalRemove() {
		return intervalRemove;
	}

	/**
	 * Sets the interval remove.
	 *
	 * @param intervalRemove
	 *          the new interval remove
	 */
	public void setIntervalRemove(String intervalRemove) {
		intervalCheck(intervalRemove);
		this.intervalRemove = intervalRemove;
	}

	/**
	 * Boolean to indicate/control the start of an entrainment session ie. the
	 * play button clicked.
	 *
	 * @return the start entrainment
	 */
	public Boolean getStartEntrainment() {
		return startEntrainment;
	}

	/**
	 * Sets the start entrainment.
	 *
	 * @param startEntrainment
	 *          the new start entrainment
	 */
	public void setStartEntrainment(Boolean startEntrainment) {
		this.startEntrainment = startEntrainment;
	}

	/**
	 * Boolean to indicate/control the use of Animation in Entrainer.
	 *
	 * @return the animation
	 */
	public Boolean getAnimation() {
		return animation;
	}

	/**
	 * Sets the animation.
	 *
	 * @param animation
	 *          the new animation
	 */
	public void setAnimation(Boolean animation) {
		this.animation = animation;
	}

	/**
	 * Boolean used to control whether random colours are used to flash the
	 * message panel. If true then any value of {@link #getFlashColour()} is
	 * ignored.
	 *
	 * @return the psychedelic
	 */
	public Boolean getPsychedelic() {
		return psychedelic;
	}

	/**
	 * Sets the psychedelic.
	 *
	 * @param psychedelic
	 *          the new psychedelic
	 */
	public void setPsychedelic(Boolean psychedelic) {
		this.psychedelic = psychedelic;
	}

	/**
	 * Boolean used to indicate/control the shimmer effect on Entrainer.
	 *
	 * @return the shimmer
	 */
	public Boolean getShimmer() {
		return shimmer;
	}

	/**
	 * Sets the shimmer.
	 *
	 * @param shimmer
	 *          the new shimmer
	 */
	public void setShimmer(Boolean shimmer) {
		this.shimmer = shimmer;
	}

	private void doubleCheck(Double value, double from, double to) {
		if (value == null) return;

		if (value < from || value > to) {
			throw new IllegalArgumentException("Value " + value + " is outside the range " + from + " to " + to);
		}
	}

	private void intervalCheck(String interval) {
		if (interval == null) return;

		String[] parts = interval.split("/");
		if (parts.length != 2) throw new IllegalArgumentException(interval + " is not a valid interval");

		for (String s : parts) {
			Integer.parseInt(s);
		}
	}

	/**
	 * Gets the request state.
	 *
	 * @return the request state
	 */
	public Boolean getRequestState() {
		return requestState;
	}

	/**
	 * Sets the request state.
	 *
	 * @param requestState
	 *          the new request state
	 */
	public void setRequestState(Boolean requestState) {
		this.requestState = requestState;
	}

	/**
	 * Gets the flash background.
	 *
	 * @return the flash background
	 */
	public Boolean getFlashBackground() {
		return flashBackground;
	}

	/**
	 * Sets the flash background.
	 *
	 * @param flashBackground
	 *          the new flash background
	 */
	public void setFlashBackground(Boolean flashBackground) {
		this.flashBackground = flashBackground;
	}

	/**
	 * Checks if is dynamic picture.
	 *
	 * @return the boolean
	 */
	public Boolean isDynamicPicture() {
		return dynamicPicture;
	}

	/**
	 * Sets the dynamic picture.
	 *
	 * @param dynamicPicture
	 *          the new dynamic picture
	 */
	public void setDynamicPicture(Boolean dynamicPicture) {
		this.dynamicPicture = dynamicPicture;
	}

	/**
	 * Checks if is static picture.
	 *
	 * @return the boolean
	 */
	public Boolean isStaticPicture() {
		return staticPicture;
	}

	/**
	 * Sets the static picture.
	 *
	 * @param staticPicture
	 *          the new static picture
	 */
	public void setStaticPicture(Boolean staticPicture) {
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
	 * Checks if is no picture.
	 *
	 * @return the boolean
	 */
	public Boolean isNoPicture() {
		return noPicture;
	}

	/**
	 * Sets the no picture.
	 *
	 * @param noPicture
	 *          the new no picture
	 */
	public void setNoPicture(Boolean noPicture) {
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
	 * Gets the background colour.
	 *
	 * @return the background colour
	 */
	public FlashColour getBackgroundColour() {
		return backgroundColour;
	}

	/**
	 * Sets the background colour.
	 *
	 * @param backgroundColour
	 *          the new background colour
	 */
	public void setBackgroundColour(FlashColour backgroundColour) {
		this.backgroundColour = backgroundColour;
	}

	/**
	 * Sets the no background color.
	 *
	 * @param c
	 *          the new no background color
	 */
	@JsonIgnore
	public void setNoBackgroundColor(Color c) {
		FlashColour fc = new FlashColour();
		fc.setColor(c);
		setBackgroundColour(fc);
	}

	/**
	 * Gets the no background color.
	 *
	 * @return the no background color
	 */
	@JsonIgnore
	public Color getNoBackgroundColor() {
		return getBackgroundColour() == null ? null : getBackgroundColour().getColor();
	}

	/**
	 * Gets the dynamic duration.
	 *
	 * @return the dynamic duration
	 */
	public Integer getDynamicDuration() {
		return dynamicDuration;
	}

	/**
	 * Sets the dynamic duration.
	 *
	 * @param dynamicDuration
	 *          the new dynamic duration
	 */
	public void setDynamicDuration(Integer dynamicDuration) {
		this.dynamicDuration = dynamicDuration;
	}

	/**
	 * Gets the dynamic transition.
	 *
	 * @return the dynamic transition
	 */
	public Integer getDynamicTransition() {
		return dynamicTransition;
	}

	/**
	 * Sets the dynamic transition.
	 *
	 * @param dynamicTransition
	 *          the new dynamic transition
	 */
	public void setDynamicTransition(Integer dynamicTransition) {
		this.dynamicTransition = dynamicTransition;
	}

	/**
	 * Gets the static picture lock.
	 *
	 * @return the static picture lock
	 */
	public Boolean getStaticPictureLock() {
		return staticPictureLock;
	}

	/**
	 * Sets the static picture lock.
	 *
	 * @param staticPictureLock
	 *          the new static picture lock
	 */
	public void setStaticPictureLock(Boolean staticPictureLock) {
		this.staticPictureLock = staticPictureLock;
	}

}
