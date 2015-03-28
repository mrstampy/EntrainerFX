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
package net.sourceforge.entrainer.xml;

import static net.sourceforge.entrainer.mediator.MediatorConstants.AMPLITUDE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.ENTRAINMENT_FREQUENCY;
import static net.sourceforge.entrainer.mediator.MediatorConstants.FREQUENCY;
import static net.sourceforge.entrainer.mediator.MediatorConstants.INTERVAL_ADD;
import static net.sourceforge.entrainer.mediator.MediatorConstants.IS_ANIMATION;
import static net.sourceforge.entrainer.mediator.MediatorConstants.IS_SHIMMER;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_ENTRAINER_MULTIPLE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_NOISE_AMPLITUDE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_PAN;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_PAN_AMPLITUDE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.SHIMMER_RECTANGLE;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sourceforge.entrainer.gui.flash.FlashType;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;
import net.sourceforge.entrainer.util.Utils;
import net.sourceforge.entrainer.xml.program.EntrainerProgramInterval;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * This class loads the settings saved from previous sessions.
 * 
 * @author burton
 */
@XmlRootElement(name = "entrainer.settings")
@XmlAccessorType(XmlAccessType.FIELD)
public class Settings {
	private static final Logger log = LoggerFactory.getLogger(Settings.class);

	@XmlElement
	private Double entrainmentFrequency;

	@XmlElement
	private Double frequency;

	@XmlElement
	private Double amplitude;

	@XmlElement
	private Double pinkNoise;

	@XmlElement
	private Boolean pinkNoisePan;

	@XmlElement
	private Double pinkNoiseAmplitude;

	@XmlElement
	private Double pinkNoiseEntrainmentMultiple;

	@XmlElement
	private String xmlProgram;

	@XmlElement
	private String lafClass;

	@XmlElement
	private String lafThemePack;

	@XmlElement
	private Boolean isAnimation;

	@XmlElement
	private Boolean isShimmer;

	@XmlElement
	private String animationProgram;

	@XmlElement
	private String animationBackground;

	@XmlElement
	private Boolean colourBackground;

	@XmlElement
	private Integer socketPort;

	@XmlElement
	private String socketIPAddress;

	@XmlElement
	private Boolean socketConnected;

	@XmlElement
	private Boolean deltaSocketMessage;

	@XmlTransient
	private Sender sender;

	@XmlElement(name = "Integererval")
	private List<EntrainerProgramInterval> Integerervals = new ArrayList<EntrainerProgramInterval>();

	@XmlElement(name = "customInterval")
	private List<EntrainerProgramInterval> customIntervals = new ArrayList<EntrainerProgramInterval>();

	@XmlElement(name = "shimmer.rectangle.implementation")
	private String shimmerRectangle;

	@XmlElement(name = "min.width")
	private Integer minWidth;

	@XmlElement(name = "min.height")
	private Integer minHeight;

	@XmlElement(name = "flash.background")
	private Boolean flashBackground;

	private static Unmarshaller unmarshal;
	private static Marshaller marshal;

	private static Settings instance;

	@XmlElement(name = "dynamic.background")
	private Boolean dynamicPicture;

	@XmlElement(name = "static.background")
	private Boolean staticPicture;

	@XmlElement(name = "static.background.picture")
	private String staticPictureFile;

	@XmlElement(name = "static.picture.lock")
	private Boolean staticPictureLock;

	@XmlElement(name = "no.background")
	private Boolean noPicture;

	@XmlElement(name = "picture.directory")
	private String pictureDirectory;

	@XmlElement(name = "background.red")
	private Integer backgroundRed;

	@XmlElement(name = "background.green")
	private Integer backgroundGreen;

	@XmlElement(name = "background.blue")
	private Integer backgroundBlue;

	@XmlElement(name = "dynamic.duration")
	private Integer dynamicDuration;

	@XmlElement(name = "dynamic.transition")
	private Integer dynamicTransition;

	@XmlElement(name = "splash.on.startup")
	private Boolean splashOnStartup;

	private static Lock lock = new ReentrantLock();

	@XmlTransient
	private Boolean acceptUpdates = false;

	@XmlElement(name = "opacity.flash")
	private Boolean opacity;

	@XmlElement(name = "bloom.flash")
	private Boolean bloom;

	@XmlElement(name = "boxBlur.flash")
	private Boolean boxBlur;

	@XmlElement(name = "gaussianBlur.flash")
	private Boolean gaussianBlur;

	@XmlElement(name = "glow.flash")
	private Boolean glow;

	@XmlElement(name = "motionBlur.flash")
	private Boolean motionBlur;

	@XmlElement(name = "sepiaTone.flash")
	private Boolean sepiaTone;

	@XmlElement(name = "shadow.flash")
	private Boolean shadow;

	@XmlElement(name = "colourAdjust.flash")
	private Boolean colourAdjust;

	@XmlElement(name = "lighting.flash")
	private Boolean lighting;

	@XmlElement(name = "media.amplitude")
	private Double mediaAmplitude;

	@XmlElement(name = "media.entrainment.strength")
	private Double mediaEntrainmentStrength;

	@XmlElement(name = "media.loop")
	private Boolean mediaLoop;

	@XmlElement(name = "media.entrainment")
	private Boolean mediaEntrainment;

	@XmlElement(name = "media.uri")
	private String mediaUri;

	@XmlElement(name = "flash.animation")
	private Boolean flashAnimation;

	@XmlElement(name = "flash.shimmer")
	private Boolean flashShimmer;

	@XmlElement(name = "flash.entrainerfx")
	private Boolean flashEntrainerFX;

	@XmlElement(name = "flash.media")
	private Boolean flashMedia;

	@XmlTransient
	private boolean preserveState = false;

	/**
	 * Sets the accept updates.
	 *
	 * @param acceptUpdates
	 *          the new accept updates
	 */
	public void setAcceptUpdates(Boolean acceptUpdates) {
		this.acceptUpdates = acceptUpdates;
	}

	static {
		try {
			JAXBContext context = JAXBContext.newInstance(Settings.class);
			unmarshal = context.createUnmarshaller();
			marshal = context.createMarshaller();
			marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Save settings.
	 */
	protected void saveSettings() {
		lock.lock();
		try {
			marshal.marshal(instance, Utils.getSettingsFile().get());
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Gets the single instance of Settings.
	 *
	 * @return single instance of Settings
	 */
	public static Settings getInstance() {
		if (instance == null) {
			try {
				unmarshalSettings();
			} catch (JAXBException e) {
				throw new RuntimeException(e);
			}
		}

		return instance;
	}

	private static void unmarshalSettings() throws JAXBException {
		Optional<File> file = Utils.getSettingsFile();

		instance = file.isPresent() ? (Settings) unmarshal.unmarshal(file.get()) : new Settings();

		instance.init();
	}

	/**
	 * Kludge to circumvent Integerermittent, variable, strange L&F change errors.
	 *
	 * @return the settings
	 */
	public static Settings reload() {
		try {
			if (instance != null) instance.unload();
			unmarshalSettings();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}

		return instance;
	}

	/**
	 * Instantiates a new settings.
	 */
	Settings() {
	}

	private void removeInterval(String val) {
		ListIterator<EntrainerProgramInterval> li = Integerervals.listIterator();
		while (li.hasNext()) {
			if (li.next().getValue().equals(val)) {
				li.remove();
			}
		}
	}

	/**
	 * Unload.
	 */
	void unload() {
		EntrainerMediator.getInstance().removeSender(sender);
		EntrainerMediator.getInstance().removeReceiver(this);
	}

	@XmlTransient
	public void setPreserveState(boolean b) {
		log.debug("preserving state {}", b);
		this.preserveState = b;
		if (!b) initState();
	}

	/**
	 * Inits the.
	 */
	protected void init() {
		sender = new SenderAdapter();
		EntrainerMediator.getInstance().addSender(sender);

		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				if (preserveState || !acceptUpdates) return;

				Boolean save = true;
				switch (e.getParm()) {
				case AMPLITUDE:
					log.debug("settings: amplitude {} from {}", e.getDoubleValue(), e.getSource());
					setAmplitude(e.getDoubleValue());
					break;
				case ENTRAINMENT_FREQUENCY:
					setEntrainmentFrequency(e.getDoubleValue());
					break;
				case FREQUENCY:
					setFrequency(e.getDoubleValue());
					break;
				case PINK_NOISE_AMPLITUDE:
					setPinkNoiseAmplitude(e.getDoubleValue());
					break;
				case PINK_ENTRAINER_MULTIPLE:
					setPinkNoiseEntrainmentMultiple(e.getDoubleValue());
					break;
				case PINK_PAN:
					setPinkNoisePan(e.getBooleanValue());
					break;
				case PINK_PAN_AMPLITUDE:
					setPinkNoisePanAmplitude(e.getDoubleValue());
					break;
				case INTERVAL_ADD:
					if (!hasInterval(e.getStringValue())) {
						EntrainerProgramInterval i = new EntrainerProgramInterval(e.getStringValue());
						Integerervals.add(i);
					}
					break;
				case INTERVAL_REMOVE:
					removeInterval(e.getStringValue());
					break;
				case CUSTOM_INTERVAL_ADD:
					addCustomInterval(e.getStringValue());
					break;
				case CUSTOM_INTERVAL_REMOVE:
					removeCustomInterval(e.getStringValue());
					break;
				case IS_ANIMATION:
					setAnimation(e.getBooleanValue());
					break;
				case ANIMATION_PROGRAM:
					setAnimationProgram(e.getStringValue());
					break;
				case ANIMATION_BACKGROUND:
					setAnimationBackground(e.getStringValue());
					break;
				case ANIMATION_COLOR_BACKGROUND:
					setColourBackground(e.getBooleanValue());
					break;
				case IS_SHIMMER:
					setShimmer(e.getBooleanValue());
					break;
				case LOOK_AND_FEEL:
					setLafClass(e.getStringValue());
					break;
				case THEME_PACK:
					setLafThemePack(e.getStringValue());
					break;
				case SHIMMER_RECTANGLE:
					setShimmerRectangle(e.getStringValue());
					break;
				case APPLY_FLASH_TO_BACKGROUND:
					setFlashBackground(e.getBooleanValue());
					break;
				case APPLY_FLASH_TO_ANIMATION:
					setFlashAnimation(e.getBooleanValue());
					break;
				case APPLY_FLASH_TO_ENTRAINER_FX:
					setFlashEntrainerFX(e.getBooleanValue());
					break;
				case APPLY_FLASH_TO_MEDIA:
					setFlashMedia(e.getBooleanValue());
					break;
				case APPLY_FLASH_TO_SHIMMER:
					setFlashShimmer(e.getBooleanValue());
					break;
				case DYNAMIC_BACKGROUND:
					setDynamicPicture(true);
					setStaticPicture(false);
					setNoPicture(false);
					break;
				case STATIC_BACKGROUND:
					setDynamicPicture(false);
					setStaticPicture(true);
					setNoPicture(false);
					break;
				case NO_BACKGROUND:
					setDynamicPicture(false);
					setStaticPicture(false);
					setNoPicture(true);
					break;
				case NO_BACKGROUND_COLOUR:
					Color cb = e.getColourValue();
					if (cb != null) {
						setBackgroundRed(cb.getRed());
						setBackgroundGreen(cb.getGreen());
						setBackgroundBlue(cb.getBlue());
					}
					break;
				case BACKGROUND_PIC:
					setStaticPictureFile(e.getStringValue());
					break;
				case BACKGROUND_PIC_DIR:
					setPictureDirectory(e.getStringValue());
					break;
				case BACKGROUND_DURATION_SECONDS:
					setDynamicDuration((int) e.getDoubleValue());
					break;
				case BACKGROUND_TRANSITION_SECONDS:
					setDynamicTransition((int) e.getDoubleValue());
					break;
				case STATIC_PICTURE_LOCK:
					setStaticPictureLock(e.getBooleanValue());
					break;
				case SPLASH_ON_STARTUP:
					setSplashOnStartup(e.getBooleanValue());
					break;
				case FLASH_TYPE:
					evaluateFlashType(((FlashType) e.getOption()), e.getBooleanValue());
					break;
				case MEDIA_AMPLITUDE:
					setMediaAmplitude(e.getDoubleValue());
					break;
				case MEDIA_ENTRAINMENT:
					setMediaEntrainment(e.getBooleanValue());
					break;
				case MEDIA_ENTRAINMENT_STRENGTH:
					setMediaEntrainmentStrength(e.getDoubleValue());
					break;
				case MEDIA_LOOP:
					setMediaLoop(e.getBooleanValue());
					break;
				case MEDIA_URI:
					setMediaUri(e.getStringValue());
					break;
				default:
					save = false;
					break;
				}

				if (save) saveSettings();
			}

		});
	}

	public void initState() {
		fireReceiverChangeEvent(getEntrainmentFrequency(), ENTRAINMENT_FREQUENCY);
		fireReceiverChangeEvent(getFrequency(), FREQUENCY);
		fireReceiverChangeEvent(getAmplitude(), AMPLITUDE);
		fireReceiverChangeEvent(getPinkNoiseAmplitude(), PINK_NOISE_AMPLITUDE);
		fireReceiverChangeEvent(getPinkNoisePanAmplitude(), PINK_PAN_AMPLITUDE);
		fireReceiverChangeEvent(getPinkNoiseEntrainmentMultiple(), PINK_ENTRAINER_MULTIPLE);
		fireReceiverChangeEvent(isPinkNoisePan(), PINK_PAN);
		fireReceiverChangeEvent(isShimmer(), IS_SHIMMER);
		fireReceiverChangeEvent(getShimmerRectangle(), SHIMMER_RECTANGLE);
		fireReceiverChangeEvent(isFlashBackground(), MediatorConstants.APPLY_FLASH_TO_BACKGROUND);
		fireReceiverChangeEvent(isFlashEntrainerFX(), MediatorConstants.APPLY_FLASH_TO_ENTRAINER_FX);
		fireReceiverChangeEvent(isFlashMedia(), MediatorConstants.APPLY_FLASH_TO_MEDIA);
		fireReceiverChangeEvent(isFlashShimmer(), MediatorConstants.APPLY_FLASH_TO_SHIMMER);

		if (getStaticPictureFile() != null) {
			fireReceiverChangeEvent(getStaticPictureFile(), MediatorConstants.BACKGROUND_PIC);
		}

		if (getPictureDirectory() != null) {
			fireReceiverChangeEvent(getPictureDirectory(), MediatorConstants.BACKGROUND_PIC_DIR);
		}

		if (isDynamicPicture()) fireReceiverChangeEvent(true, MediatorConstants.DYNAMIC_BACKGROUND);
		if (isStaticPicture()) fireReceiverChangeEvent(true, MediatorConstants.STATIC_BACKGROUND);
		if (isNoPicture()) fireReceiverChangeEvent(true, MediatorConstants.NO_BACKGROUND);

		ReceiverChangeEvent e = new ReceiverChangeEvent(this, new Color(getBackgroundRed(), getBackgroundGreen(),
				getBackgroundBlue()), MediatorConstants.NO_BACKGROUND_COLOUR);
		sender.fireReceiverChangeEvent(e);

		fireReceiverChangeEvent(getDynamicDuration(), MediatorConstants.BACKGROUND_DURATION_SECONDS);
		fireReceiverChangeEvent(getDynamicTransition(), MediatorConstants.BACKGROUND_TRANSITION_SECONDS);
		fireReceiverChangeEvent(isStaticPictureLock(), MediatorConstants.STATIC_PICTURE_LOCK);
		fireReceiverChangeEvent(isSplashOnStartup(), MediatorConstants.SPLASH_ON_STARTUP);

		for (EntrainerProgramInterval interval : Integerervals) {
			fireReceiverChangeEvent(interval.getValue());
		}

		fireFlashOptions();

		fireMediaOptions();
		
		initAnimation();
	}
	
	public void initAnimation() {
		fireReceiverChangeEvent(isFlashAnimation(), MediatorConstants.APPLY_FLASH_TO_ANIMATION);
		fireReceiverChangeEvent(isAnimation(), IS_ANIMATION);
		fireReceiverChangeEvent(getAnimationBackground(), MediatorConstants.ANIMATION_BACKGROUND);
		fireReceiverChangeEvent(getAnimationProgram(), MediatorConstants.ANIMATION_PROGRAM);
		fireReceiverChangeEvent(isColourBackground(), MediatorConstants.ANIMATION_COLOR_BACKGROUND);
	}

	private void fireMediaOptions() {
		fireReceiverChangeEvent(isMediaEntrainment(), MediatorConstants.MEDIA_ENTRAINMENT);
		fireReceiverChangeEvent(isMediaLoop(), MediatorConstants.MEDIA_LOOP);
		fireReceiverChangeEvent(getMediaAmplitude(), MediatorConstants.MEDIA_AMPLITUDE);
		fireReceiverChangeEvent(getMediaEntrainmentStrength(), MediatorConstants.MEDIA_ENTRAINMENT_STRENGTH);
		fireReceiverChangeEvent(getMediaUri(), MediatorConstants.MEDIA_URI);
	}

	private void fireFlashOptions() {
		fireReceiverChangeEvent(FlashType.BLOOM, isBloom());
		fireReceiverChangeEvent(FlashType.BOX_BLUR, isBoxBlur());
		fireReceiverChangeEvent(FlashType.COLOUR_ADJUST, isColourAdjust());
		fireReceiverChangeEvent(FlashType.GAUSSIAN_BLUR, isGaussianBlur());
		fireReceiverChangeEvent(FlashType.GLOW, isGlow());
		fireReceiverChangeEvent(FlashType.LIGHTING, isLighting());
		fireReceiverChangeEvent(FlashType.MOTION_BLUR, isMotionBlur());
		fireReceiverChangeEvent(FlashType.OPACITY, isOpacity());
		fireReceiverChangeEvent(FlashType.SEPIA_TONE, isSepiaTone());
		fireReceiverChangeEvent(FlashType.SHADOW, isShadow());
	}

	private void evaluateFlashType(FlashType flashType, Boolean b) {
		switch (flashType) {
		case BLOOM:
			setBloom(b);
			break;
		case BOX_BLUR:
			setBoxBlur(b);
			break;
		case COLOUR_ADJUST:
			setColourAdjust(b);
			break;
		case GAUSSIAN_BLUR:
			setGaussianBlur(b);
			break;
		case GLOW:
			setGlow(b);
			break;
		case LIGHTING:
			setLighting(b);
			break;
		case MOTION_BLUR:
			setMotionBlur(b);
			break;
		case OPACITY:
			setOpacity(b);
			break;
		case SEPIA_TONE:
			setSepiaTone(b);
			break;
		case SHADOW:
			setShadow(b);
			break;
		default:
			break;

		}
	}

	/**
	 * Checks for Integererval.
	 *
	 * @param s
	 *          the s
	 * @return true, if successful
	 */
	public Boolean hasInterval(String s) {
		for (EntrainerProgramInterval Integererval : Integerervals) {
			if (Integererval.getValue().equals(s)) {
				return true;
			}
		}

		return false;
	}

	private void fireReceiverChangeEvent(FlashType type, Boolean b) {
		if (b == null) return;
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, type, b, MediatorConstants.FLASH_TYPE));
	}

	private void fireReceiverChangeEvent(String interval) {
		if (interval == null) return;
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, interval, INTERVAL_ADD);
		sender.fireReceiverChangeEvent(e);
	}

	private void fireReceiverChangeEvent(String s, MediatorConstants parm) {
		if (s == null) return;
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, s, parm);
		sender.fireReceiverChangeEvent(e);
	}

	private void fireReceiverChangeEvent(Integer s, MediatorConstants parm) {
		if (s == null) return;
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, s, parm);
		sender.fireReceiverChangeEvent(e);
	}

	private void fireReceiverChangeEvent(Boolean b, MediatorConstants parm) {
		if (b == null) return;
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, b, parm);
		sender.fireReceiverChangeEvent(e);
	}

	private void fireReceiverChangeEvent(Double value, MediatorConstants name) {
		if (value == null) return;
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, value, name);
		sender.fireReceiverChangeEvent(e);
	}

	/**
	 * Gets the entrainment frequency.
	 *
	 * @return the entrainment frequency
	 */
	public Double getEntrainmentFrequency() {
		return entrainmentFrequency;
	}

	private void setEntrainmentFrequency(Double entrainmentFrequency) {
		this.entrainmentFrequency = entrainmentFrequency;
	}

	/**
	 * Gets the frequency.
	 *
	 * @return the frequency
	 */
	public Double getFrequency() {
		return frequency;
	}

	private void setFrequency(Double frequency) {
		this.frequency = frequency;
	}

	/**
	 * Gets the amplitude.
	 *
	 * @return the amplitude
	 */
	public Double getAmplitude() {
		return amplitude;
	}

	private void setAmplitude(Double amplitude) {
		this.amplitude = amplitude;
	}

	/**
	 * Checks if is pink noise pan.
	 *
	 * @return true, if is pink noise pan
	 */
	public Boolean isPinkNoisePan() {
		return pinkNoisePan;
	}

	private void setPinkNoisePan(Boolean pinkNoisePan) {
		this.pinkNoisePan = pinkNoisePan;
	}

	/**
	 * Gets the pink noise entrainment multiple.
	 *
	 * @return the pink noise entrainment multiple
	 */
	public Double getPinkNoiseEntrainmentMultiple() {
		return pinkNoiseEntrainmentMultiple;
	}

	private void setPinkNoiseEntrainmentMultiple(Double pinkNoiseEntrainmentMultiple) {
		this.pinkNoiseEntrainmentMultiple = pinkNoiseEntrainmentMultiple;
	}

	/**
	 * Gets the xml program.
	 *
	 * @return the xml program
	 */
	public String getXmlProgram() {
		return xmlProgram;
	}

	/**
	 * Sets the xml program.
	 *
	 * @param xmlProgram
	 *          the new xml program
	 */
	public void setXmlProgram(String xmlProgram) {
		this.xmlProgram = xmlProgram;
		saveSettings();
	}

	/**
	 * Gets the laf class.
	 *
	 * @return the laf class
	 */
	public String getLafClass() {
		return lafClass;
	}

	/**
	 * Sets the laf class.
	 *
	 * @param lafClass
	 *          the new laf class
	 */
	public void setLafClass(String lafClass) {
		this.lafClass = lafClass;
	}

	/**
	 * Gets the laf theme pack.
	 *
	 * @return the laf theme pack
	 */
	public String getLafThemePack() {
		return lafThemePack;
	}

	/**
	 * Sets the laf theme pack.
	 *
	 * @param lafThemePack
	 *          the new laf theme pack
	 */
	public void setLafThemePack(String lafThemePack) {
		this.lafThemePack = lafThemePack;
	}

	/**
	 * Gets the pink noise pan amplitude.
	 *
	 * @return the pink noise pan amplitude
	 */
	public Double getPinkNoisePanAmplitude() {
		return pinkNoiseAmplitude;
	}

	private void setPinkNoisePanAmplitude(Double pinkNoiseAmplitude) {
		this.pinkNoiseAmplitude = pinkNoiseAmplitude;
	}

	/**
	 * Gets the pink noise amplitude.
	 *
	 * @return the pink noise amplitude
	 */
	public Double getPinkNoiseAmplitude() {
		return pinkNoise;
	}

	private void setPinkNoiseAmplitude(Double pinkNoise) {
		this.pinkNoise = pinkNoise;
	}

	/**
	 * Gets the custom Integerervals.
	 *
	 * @return the custom Integerervals
	 */
	public List<EntrainerProgramInterval> getCustomIntervals() {
		return new ArrayList<EntrainerProgramInterval>(customIntervals);
	}

	private void removeCustomInterval(String s) {
		customIntervals.remove(new EntrainerProgramInterval(s));
	}

	private void addCustomInterval(String s) {
		if (!hasCustomInterval(s)) {
			EntrainerProgramInterval i = new EntrainerProgramInterval(s);
			customIntervals.add(i);
		}
	}

	private Boolean hasCustomInterval(String s) {
		for (EntrainerProgramInterval Integererval : customIntervals) {
			if (Integererval.getValue().equals(s)) {
				return true;
			}
		}

		return false;
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
	 * Checks if is animation.
	 *
	 * @return true, if is animation
	 */
	public Boolean isAnimation() {
		return isAnimation;
	}

	/**
	 * Sets the animation.
	 *
	 * @param isAnimation
	 *          the new animation
	 */
	public void setAnimation(Boolean isAnimation) {
		this.isAnimation = isAnimation;
	}

	/**
	 * Checks if is shimmer.
	 *
	 * @return true, if is shimmer
	 */
	public Boolean isShimmer() {
		return isShimmer;
	}

	/**
	 * Sets the shimmer.
	 *
	 * @param isShimmer
	 *          the new shimmer
	 */
	public void setShimmer(Boolean isShimmer) {
		this.isShimmer = isShimmer;
	}

	/**
	 * Checks if is desktop background.
	 *
	 * @return true, if is desktop background
	 */
	public Boolean isColourBackground() {
		return colourBackground;
	}

	/**
	 * Sets the desktop background.
	 *
	 * @param isDesktopBackground
	 *          the new desktop background
	 */
	public void setColourBackground(Boolean isDesktopBackground) {
		this.colourBackground = isDesktopBackground;
	}

	/**
	 * Gets the socket port.
	 *
	 * @return the socket port
	 */
	public Integer getSocketPort() {
		return socketPort;
	}

	/**
	 * Sets the socket port.
	 *
	 * @param socketPort
	 *          the new socket port
	 */
	public void setSocketPort(Integer socketPort) {
		this.socketPort = socketPort;
		saveSettings();
	}

	/**
	 * Checks if is delta socket message.
	 *
	 * @return true, if is delta socket message
	 */
	public Boolean isDeltaSocketMessage() {
		return deltaSocketMessage;
	}

	/**
	 * Sets the delta socket message.
	 *
	 * @param fullSocketMessage
	 *          the new delta socket message
	 */
	public void setDeltaSocketMessage(Boolean fullSocketMessage) {
		this.deltaSocketMessage = fullSocketMessage;
		saveSettings();
	}

	/**
	 * Checks if is socket connected.
	 *
	 * @return true, if is socket connected
	 */
	public Boolean isSocketConnected() {
		return socketConnected;
	}

	/**
	 * Sets the socket connected.
	 *
	 * @param socketConnected
	 *          the new socket connected
	 */
	public void setSocketConnected(Boolean socketConnected) {
		this.socketConnected = socketConnected;
		saveSettings();
	}

	/**
	 * Gets the socket ip address.
	 *
	 * @return the socket ip address
	 */
	public String getSocketIPAddress() {
		return socketIPAddress;
	}

	/**
	 * Sets the socket ip address.
	 *
	 * @param socketIPAddress
	 *          the new socket ip address
	 */
	public void setSocketIPAddress(String socketIPAddress) {
		this.socketIPAddress = socketIPAddress;
		saveSettings();
	}

	/**
	 * Gets the shimmer rectangle.
	 *
	 * @return the shimmer rectangle
	 */
	public String getShimmerRectangle() {
		return shimmerRectangle;
	}

	/**
	 * Sets the shimmer rectangle.
	 *
	 * @param shimmerRectangle
	 *          the new shimmer rectangle
	 */
	public void setShimmerRectangle(String shimmerRectangle) {
		this.shimmerRectangle = shimmerRectangle;
	}

	/**
	 * Gets the min width.
	 *
	 * @return the min width
	 */
	public Integer getMinWidth() {
		return minWidth;
	}

	/**
	 * Sets the min width.
	 *
	 * @param minWidth
	 *          the new min width
	 */
	public void setMinWidth(Integer minWidth) {
		this.minWidth = minWidth;
	}

	/**
	 * Gets the min height.
	 *
	 * @return the min height
	 */
	public Integer getMinHeight() {
		return minHeight;
	}

	/**
	 * Sets the min height.
	 *
	 * @param minHeight
	 *          the new min height
	 */
	public void setMinHeight(Integer minHeight) {
		this.minHeight = minHeight;
	}

	/**
	 * Gets the min size.
	 *
	 * @return the min size
	 */
	public Dimension getMinSize() {
		if (getMinWidth() > 0 && getMinHeight() > 0) return new Dimension(getMinWidth(), getMinHeight());

		return new Dimension(674, 674);
	}

	/**
	 * Checks if is flash background.
	 *
	 * @return true, if is flash background
	 */
	public Boolean isFlashBackground() {
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
	 * @return true, if is dynamic picture
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
	 * @return true, if is static picture
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
	 * @return true, if is no picture
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
	 * Gets the background red.
	 *
	 * @return the background red
	 */
	public Integer getBackgroundRed() {
		return backgroundRed;
	}

	/**
	 * Sets the background red.
	 *
	 * @param backgroundRed
	 *          the new background red
	 */
	public void setBackgroundRed(Integer backgroundRed) {
		this.backgroundRed = backgroundRed;
	}

	/**
	 * Gets the background green.
	 *
	 * @return the background green
	 */
	public Integer getBackgroundGreen() {
		return backgroundGreen;
	}

	/**
	 * Sets the background green.
	 *
	 * @param backgroundGreen
	 *          the new background green
	 */
	public void setBackgroundGreen(Integer backgroundGreen) {
		this.backgroundGreen = backgroundGreen;
	}

	/**
	 * Gets the background blue.
	 *
	 * @return the background blue
	 */
	public Integer getBackgroundBlue() {
		return backgroundBlue;
	}

	/**
	 * Sets the background blue.
	 *
	 * @param backgroundBlue
	 *          the new background blue
	 */
	public void setBackgroundBlue(Integer backgroundBlue) {
		this.backgroundBlue = backgroundBlue;
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
	 * Checks if is static picture lock.
	 *
	 * @return true, if is static picture lock
	 */
	public Boolean isStaticPictureLock() {
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

	/**
	 * Checks if is splash on startup.
	 *
	 * @return true, if is splash on startup
	 */
	public Boolean isSplashOnStartup() {
		return splashOnStartup;
	}

	/**
	 * Sets the splash on startup.
	 *
	 * @param splashOnStartup
	 *          the new splash on startup
	 */
	public void setSplashOnStartup(Boolean splashOnStartup) {
		this.splashOnStartup = splashOnStartup;
	}

	/**
	 * Checks if is opacity.
	 *
	 * @return true, if is opacity
	 */
	public Boolean isOpacity() {
		return opacity;
	}

	/**
	 * Sets the opacity.
	 *
	 * @param isOpacity
	 *          the new opacity
	 */
	public void setOpacity(Boolean isOpacity) {
		this.opacity = isOpacity;
	}

	/**
	 * Checks if is bloom.
	 *
	 * @return true, if is bloom
	 */
	public Boolean isBloom() {
		return bloom;
	}

	/**
	 * Sets the bloom.
	 *
	 * @param isBloom
	 *          the new bloom
	 */
	public void setBloom(Boolean isBloom) {
		this.bloom = isBloom;
	}

	/**
	 * Checks if is box blur.
	 *
	 * @return true, if is box blur
	 */
	public Boolean isBoxBlur() {
		return boxBlur;
	}

	/**
	 * Sets the box blur.
	 *
	 * @param isBoxBlur
	 *          the new box blur
	 */
	public void setBoxBlur(Boolean isBoxBlur) {
		this.boxBlur = isBoxBlur;
	}

	/**
	 * Checks if is gaussian blur.
	 *
	 * @return true, if is gaussian blur
	 */
	public Boolean isGaussianBlur() {
		return gaussianBlur;
	}

	/**
	 * Sets the gaussian blur.
	 *
	 * @param isGaussianBlur
	 *          the new gaussian blur
	 */
	public void setGaussianBlur(Boolean isGaussianBlur) {
		this.gaussianBlur = isGaussianBlur;
	}

	/**
	 * Checks if is glow.
	 *
	 * @return true, if is glow
	 */
	public Boolean isGlow() {
		return glow;
	}

	/**
	 * Sets the glow.
	 *
	 * @param isGlow
	 *          the new glow
	 */
	public void setGlow(Boolean isGlow) {
		this.glow = isGlow;
	}

	/**
	 * Checks if is motion blur.
	 *
	 * @return true, if is motion blur
	 */
	public Boolean isMotionBlur() {
		return motionBlur;
	}

	/**
	 * Sets the motion blur.
	 *
	 * @param isMotionBlur
	 *          the new motion blur
	 */
	public void setMotionBlur(Boolean isMotionBlur) {
		this.motionBlur = isMotionBlur;
	}

	/**
	 * Checks if is sepia tone.
	 *
	 * @return true, if is sepia tone
	 */
	public Boolean isSepiaTone() {
		return sepiaTone;
	}

	/**
	 * Sets the sepia tone.
	 *
	 * @param isSepiaTone
	 *          the new sepia tone
	 */
	public void setSepiaTone(Boolean isSepiaTone) {
		this.sepiaTone = isSepiaTone;
	}

	/**
	 * Checks if is shadow.
	 *
	 * @return true, if is shadow
	 */
	public Boolean isShadow() {
		return shadow;
	}

	/**
	 * Sets the shadow.
	 *
	 * @param isShadow
	 *          the new shadow
	 */
	public void setShadow(Boolean isShadow) {
		this.shadow = isShadow;
	}

	/**
	 * Checks if is colour adjust.
	 *
	 * @return true, if is colour adjust
	 */
	public Boolean isColourAdjust() {
		return colourAdjust;
	}

	/**
	 * Sets the colour adjust.
	 *
	 * @param isRandomColourAdjust
	 *          the new colour adjust
	 */
	public void setColourAdjust(Boolean isRandomColourAdjust) {
		this.colourAdjust = isRandomColourAdjust;
	}

	/**
	 * Checks if is lighting.
	 *
	 * @return true, if is lighting
	 */
	public Boolean isLighting() {
		return lighting;
	}

	/**
	 * Sets the lighting.
	 *
	 * @param isLighting
	 *          the new lighting
	 */
	public void setLighting(Boolean isLighting) {
		this.lighting = isLighting;
	}

	/**
	 * Gets the media amplitude.
	 *
	 * @return the media amplitude
	 */
	public Double getMediaAmplitude() {
		return mediaAmplitude;
	}

	/**
	 * Sets the media amplitude.
	 *
	 * @param mediaAmplitude
	 *          the new media amplitude
	 */
	public void setMediaAmplitude(Double mediaAmplitude) {
		this.mediaAmplitude = mediaAmplitude;
	}

	/**
	 * Gets the media entrainment strength.
	 *
	 * @return the media entrainment strength
	 */
	public Double getMediaEntrainmentStrength() {
		return mediaEntrainmentStrength;
	}

	/**
	 * Sets the media entrainment strength.
	 *
	 * @param mediaEntrainmentStrength
	 *          the new media entrainment strength
	 */
	public void setMediaEntrainmentStrength(Double mediaEntrainmentStrength) {
		this.mediaEntrainmentStrength = mediaEntrainmentStrength;
	}

	/**
	 * Checks if is media loop.
	 *
	 * @return true, if is media loop
	 */
	public Boolean isMediaLoop() {
		return mediaLoop;
	}

	/**
	 * Sets the media loop.
	 *
	 * @param mediaLoop
	 *          the new media loop
	 */
	public void setMediaLoop(Boolean mediaLoop) {
		this.mediaLoop = mediaLoop;
	}

	/**
	 * Checks if is media entrainment.
	 *
	 * @return true, if is media entrainment
	 */
	public Boolean isMediaEntrainment() {
		return mediaEntrainment;
	}

	/**
	 * Sets the media entrainment.
	 *
	 * @param mediaEntrainment
	 *          the new media entrainment
	 */
	public void setMediaEntrainment(Boolean mediaEntrainment) {
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

	public Boolean isFlashAnimation() {
		return flashAnimation;
	}

	public void setFlashAnimation(Boolean flashAnimation) {
		this.flashAnimation = flashAnimation;
	}

	public Boolean isFlashShimmer() {
		return flashShimmer;
	}

	public void setFlashShimmer(Boolean flashShimmer) {
		this.flashShimmer = flashShimmer;
	}

	public Boolean isFlashEntrainerFX() {
		return flashEntrainerFX;
	}

	public void setFlashEntrainerFX(Boolean flashEntrainerFX) {
		this.flashEntrainerFX = flashEntrainerFX;
	}

	public Boolean isFlashMedia() {
		return flashMedia;
	}

	public void setFlashMedia(Boolean flashMedia) {
		this.flashMedia = flashMedia;
	}

}
