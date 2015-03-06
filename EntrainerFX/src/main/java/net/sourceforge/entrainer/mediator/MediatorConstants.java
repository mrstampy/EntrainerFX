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
package net.sourceforge.entrainer.mediator;

// TODO: Auto-generated Javadoc
/**
 * Constants for the {@link ReceiverChangeEvent} instances.
 *  
 * @author burton
 *
 */
public enum MediatorConstants  {
	
	/** The amplitude. */
	AMPLITUDE("Amplitude"),
	
	/** The entrainment frequency. */
	ENTRAINMENT_FREQUENCY("Entrainment Frequency"),
	
	/** The frequency. */
	FREQUENCY("Frequency"),
	
	/** The pink entrainer multiple. */
	PINK_ENTRAINER_MULTIPLE("Pink Entrainer Multiple"),
	
	/** The pink noise amplitude. */
	PINK_NOISE_AMPLITUDE("Pink Noise Amplitude"),
	
	/** The pink pan amplitude. */
	PINK_PAN_AMPLITUDE("Pink Pan Amplitude"),
	
	/** The pink pan value. */
	PINK_PAN_VALUE("Pink Pan Value"),
	
	/** The pink pan. */
	PINK_PAN("Pink Pan"),
	
	/** The delta amplitude. */
	DELTA_AMPLITUDE("Delta Amplitude"),
	
	/** The delta entrainment frequency. */
	DELTA_ENTRAINMENT_FREQUENCY("Delta Entrainment Frequency"),
	
	/** The delta frequency. */
	DELTA_FREQUENCY("Delta Frequency"),
	
	/** The delta pink entrainer multiple. */
	DELTA_PINK_ENTRAINER_MULTIPLE("Delta Pink Entrainer Multiple"),
	
	/** The delta pink noise amplitude. */
	DELTA_PINK_NOISE_AMPLITUDE("Delta Pink Noise Amplitude"),
	
	/** The delta pink pan amplitude. */
	DELTA_PINK_PAN_AMPLITUDE("Delta Pink Pan Amplitude"),
	
	/** The is animation. */
	IS_ANIMATION("Is Animation"),
	
	/** The animation program. */
	ANIMATION_PROGRAM("Animation Program"),
	
	/** The animation background. */
	ANIMATION_BACKGROUND("Animation Background"),
	
	/** The animation desktop background. */
	ANIMATION_DESKTOP_BACKGROUND("Animation Desktop Background"),
	
	/** The interval add. */
	INTERVAL_ADD("Interval Add"),
	
	/** The interval remove. */
	INTERVAL_REMOVE("Interval Remove"),
	
	/** The custom interval add. */
	CUSTOM_INTERVAL_ADD("Custom Interval Add"),
	
	/** The custom interval remove. */
	CUSTOM_INTERVAL_REMOVE("Custom Interval Remove"),
	
	/** The is flash. */
	IS_FLASH("Is Flash"),
	
	/** The is psychedelic. */
	IS_PSYCHEDELIC("Is Psychedelic"),
	
	/** The flash colour. */
	FLASH_COLOUR("Flash Colour"),
	
	/** The start entrainment. */
	START_ENTRAINMENT("Start Entrainment"),
	
	/** The start flashing. */
	START_FLASHING("Start Flashing"),
	
	/** The flash background. */
	FLASH_BACKGROUND("Flash Background"),
	
	/** The message. */
	MESSAGE("Message"),
	
	/** The is shimmer. */
	IS_SHIMMER("Is Shimmer"),
	
	/** The look and feel. */
	LOOK_AND_FEEL("Look And Feel"),
	
	/** The shimmer rectangle. */
	SHIMMER_RECTANGLE("Shimmer Rectangle Implementation"),
	
	/** The theme pack. */
	THEME_PACK("Theme Pack"),
	
	/** The esp start. */
	ESP_START("ESP Device Start"),
	
	/** The esp connections reloaded. */
	ESP_CONNECTIONS_RELOADED("ESP Connections Reloaded"),
	
	STATIC_BACKGROUND("Static background picture indicator"),
	
	DYNAMIC_BACKGROUND("Dynamic background picture indicator"),
	
	NO_BACKGROUND("No background picture indicator"),
	
	NO_BACKGROUND_COLOUR("Fill colour for no background picture"),
	
	BACKGROUND_PIC_DIR("Root directory for EntrainerFX background pics"),
	
	BACKGROUND_PIC("Static background picture path and filename"),
	
	BACKGROUND_TRANSITION_SECONDS("Seconds to transition between dynamic background pictures"),
	
	BACKGROUND_DURATION_SECONDS("Seconds to display a picture before transitioning to another");
	
	private String value;
	
	/**
	 * Instantiates a new mediator constants.
	 *
	 * @param value the value
	 */
	MediatorConstants(String value) {
		this.value = value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		return value;
	}
}
