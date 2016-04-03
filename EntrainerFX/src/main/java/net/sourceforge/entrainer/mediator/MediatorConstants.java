/*
 *      ______      __             _                 _______  __
 *     / ____/___  / /__________ _(_)___  ___  _____/ ____/ |/ /
 *    / __/ / __ \/ __/ ___/ __ `/ / __ \/ _ \/ ___/ /_   |   / 
 *   / /___/ / / / /_/ /  / /_/ / / / / /  __/ /  / __/  /   |  
 *  /_____/_/ /_/\__/_/   \__,_/_/_/ /_/\___/_/  /_/    /_/|_|  
 *                                                          
 *
 * Copyright (C) 2008 - 2016 Burton Alexander
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
public enum MediatorConstants {

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
	ANIMATION_COLOR_BACKGROUND("Animation Colour Background"),

	/** The interval add. */
	INTERVAL_ADD("Interval Add"),

	/** The interval remove. */
	INTERVAL_REMOVE("Interval Remove"),

	/** The custom interval add. */
	CUSTOM_INTERVAL_ADD("Custom Interval Add"),

	/** The custom interval remove. */
	CUSTOM_INTERVAL_REMOVE("Custom Interval Remove"),

	/** The start entrainment. */
	START_ENTRAINMENT("Start Entrainment"),

	/** The message. */
	MESSAGE("Message"),

	/** The is shimmer. */
	IS_SHIMMER("Is Shimmer"),

	/** The shimmer rectangle. */
	SHIMMER_RECTANGLE("Shimmer Rectangle Implementation"),

	/** The esp start. */
	ESP_START("ESP Device Start"),

	/** The esp connections reloaded. */
	ESP_CONNECTIONS_RELOADED("ESP Connections Reloaded"),

	/** The static background. */
	STATIC_BACKGROUND("Static background picture indicator"),

	/** The dynamic background. */
	DYNAMIC_BACKGROUND("Dynamic background picture indicator"),

	/** The no background. */
	NO_BACKGROUND("No background picture indicator"),

	/** The no background colour. */
	NO_BACKGROUND_COLOUR("Fill colour for no background picture"),

	/** The background pic dir. */
	BACKGROUND_PIC_DIR("Root directory for EntrainerFX background pics"),

	/** The background pic. */
	BACKGROUND_PIC("Static background picture path and filename"),

	/** The background transition seconds. */
	BACKGROUND_TRANSITION_SECONDS("Seconds to transition between dynamic background pictures"),

	/** The background duration seconds. */
	BACKGROUND_DURATION_SECONDS("Seconds to display a picture before transitioning to another"),

	/** The static picture lock. */
	STATIC_PICTURE_LOCK("If true will prevent chosen picture from being overwritten switching to & from dynamic"),

	/** The entrainment frequency pulse. */
	ENTRAINMENT_FREQUENCY_PULSE("Notification of entrainment frequency"),

	/** The flash type. */
	FLASH_TYPE("The type of flashing to apply to the background image"),

	/** The media amplitude. */
	MEDIA_AMPLITUDE("Media volume"),

	/** The delta media amplitude. */
	DELTA_MEDIA_AMPLITUDE("Delta media volume"),

	/** The delta media entrainment strength. */
	DELTA_MEDIA_ENTRAINMENT_STRENGTH("Delta media entrainment strength"),

	/** The media entrainment strength. */
	MEDIA_ENTRAINMENT_STRENGTH("Media entrainment strength"),

	/** The media play. */
	MEDIA_PLAY("If true play, else stop"),

	/** The media pause. */
	MEDIA_PAUSE("Pause/resume media"),

	/** The media uri. */
	MEDIA_URI("Media URI"),

	/** The media entrainment. */
	MEDIA_ENTRAINMENT("Enable/disable media entrainment"),

	/** The media loop. */
	MEDIA_LOOP("Loop media on end if selected"),

	/** The media time. */
	MEDIA_TIME("Media time remaining"),

	/** The apply flash to background. */
	APPLY_FLASH_TO_BACKGROUND("Enables/disables the flash effect on the background"),

	/** The apply flash to shimmer. */
	APPLY_FLASH_TO_SHIMMER("Enables/disables the flash effect on the shimmers"),

	/** The apply flash to animation. */
	APPLY_FLASH_TO_ANIMATION("Enables/disables the flash effect on the animations"),

	/** The apply flash to media. */
	APPLY_FLASH_TO_MEDIA("Enables/disables the flash effect on the media"),

	/** The apply flash to EntrainerFX. */
	APPLY_FLASH_TO_ENTRAINER_FX("Enables/disables the flash effect on the main window"),

	/** The flash effect. */
	FLASH_EFFECT("The effect to use for flashing"),

	/** The splash on startup. */
	SPLASH_ON_STARTUP("If true will enable the splash screen on startup");

	private String value;

	/**
	 * Instantiates a new mediator constants.
	 *
	 * @param value
	 *          the value
	 */
	MediatorConstants(String value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		return value;
	}
}
