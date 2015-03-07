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
import static net.sourceforge.entrainer.mediator.MediatorConstants.ANIMATION_DESKTOP_BACKGROUND;
import static net.sourceforge.entrainer.mediator.MediatorConstants.ENTRAINMENT_FREQUENCY;
import static net.sourceforge.entrainer.mediator.MediatorConstants.FLASH_BACKGROUND;
import static net.sourceforge.entrainer.mediator.MediatorConstants.FREQUENCY;
import static net.sourceforge.entrainer.mediator.MediatorConstants.INTERVAL_ADD;
import static net.sourceforge.entrainer.mediator.MediatorConstants.IS_ANIMATION;
import static net.sourceforge.entrainer.mediator.MediatorConstants.IS_FLASH;
import static net.sourceforge.entrainer.mediator.MediatorConstants.IS_PSYCHEDELIC;
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

import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;
import net.sourceforge.entrainer.xml.program.EntrainerProgramInterval;

// TODO: Auto-generated Javadoc
/**
 * This class loads the settings saved from previous sessions.
 * 
 * @author burton
 */
@XmlRootElement(name = "entrainer.settings")
@XmlAccessorType(XmlAccessType.FIELD)
public class Settings {

	@XmlElement
	private double entrainmentFrequency;

	@XmlElement
	private double frequency;

	@XmlElement
	private double amplitude;

	@XmlElement
	private double pinkNoise;

	@XmlElement
	private boolean pinkNoisePan;

	@XmlElement
	private double pinkNoiseAmplitude;

	@XmlElement
	private double pinkNoiseEntrainmentMultiple;

	@XmlElement
	private String xmlProgram;

	@XmlElement
	private String lafClass;

	@XmlElement
	private String lafThemePack;

	@XmlElement
	private boolean isAnimation;

	@XmlElement
	private boolean isFlash;

	@XmlElement
	private boolean isPsychedelic;

	@XmlElement
	private boolean isShimmer;

	@XmlElement
	private String animationProgram;

	@XmlElement
	private String animationBackground;

	@XmlElement
	private boolean isDesktopBackground;

	@XmlElement(name = "jsyn.native.jar")
	private String jsynNativeJar;

	@XmlElement(name = "jsyn.java.jar")
	private String jsynJavaJar;

	@XmlElement
	private boolean isNativeJsyn;

	@XmlElement
	private int socketPort;

	@XmlElement
	private String socketIPAddress;

	@XmlElement
	private boolean socketConnected;

	@XmlElement
	private boolean deltaSocketMessage;

	@XmlTransient
	private Sender sender;

	@XmlElement(name = "interval")
	private List<EntrainerProgramInterval> intervals = new ArrayList<EntrainerProgramInterval>();

	@XmlElement(name = "customInterval")
	private List<EntrainerProgramInterval> customIntervals = new ArrayList<EntrainerProgramInterval>();

	@XmlElement(name = "shimmer.rectangle.implementation")
	private String shimmerRectangle;

	@XmlElement(name = "min.width")
	private int minWidth;

	@XmlElement(name = "min.height")
	private int minHeight;

	@XmlElement(name = "flash.background")
	private boolean flashBackground;

	private static Unmarshaller unmarshal;
	private static Marshaller marshal;

	private static Settings instance;

	@XmlElement(name = "flash.red")
	private int flashRed;

	@XmlElement(name = "flash.green")
	private int flashGreen;

	@XmlElement(name = "flash.blue")
	private int flashBlue;

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

	@XmlElement(name = "background.red")
	private int backgroundRed;

	@XmlElement(name = "background.green")
	private int backgroundGreen;

	@XmlElement(name = "background.blue")
	private int backgroundBlue;

	@XmlElement(name = "dynamic.duration")
	private int dynamicDuration;

	@XmlElement(name = "dynamic.transition")
	private int dynamicTransition;

	@XmlElement(name = "splash.on.startup")
	private boolean splashOnStartup;

	private static Lock lock = new ReentrantLock();
	
	private boolean acceptUpdates = false;

	public void setAcceptUpdates(boolean acceptUpdates) {
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
	public static void saveSettings() {
		lock.lock();
		try {
			File file = new File("settings.xml");

			marshal.marshal(instance, file);
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
		File file = new File("settings.xml");
		if (file.exists()) {
			instance = (Settings) unmarshal.unmarshal(file);
		} else {
			instance = new Settings();
		}

		instance.init();
	}

	/**
	 * Kludge to circumvent intermittent, variable, strange L&F change errors.
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
		ListIterator<EntrainerProgramInterval> li = intervals.listIterator();
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

	/**
	 * Inits the.
	 */
	protected void init() {
		sender = new SenderAdapter();
		EntrainerMediator.getInstance().addSender(sender);

		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				boolean save = true;
				switch (e.getParm()) {
				case AMPLITUDE:
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
						intervals.add(i);
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
				case ANIMATION_DESKTOP_BACKGROUND:
					setDesktopBackground(e.getBooleanValue());
					break;
				case IS_FLASH:
					setFlash(e.getBooleanValue());
					break;
				case IS_PSYCHEDELIC:
					setPsychedelic(e.getBooleanValue());
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
				case FLASH_COLOUR:
					Color c = e.getColourValue();
					setFlashRed(c.getRed());
					setFlashGreen(c.getGreen());
					setFlashBlue(c.getBlue());
					break;
				case FLASH_BACKGROUND:
					setFlashBackground(e.getBooleanValue());
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
					setBackgroundRed(cb.getRed());
					setBackgroundGreen(cb.getGreen());
					setBackgroundBlue(cb.getBlue());
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
				default:
					save = false;
					break;
				}

				if (save && acceptUpdates) saveSettings();
			}

		});

		fireReceiverChangeEvent(getEntrainmentFrequency(), ENTRAINMENT_FREQUENCY);
		fireReceiverChangeEvent(getFrequency(), FREQUENCY);
		fireReceiverChangeEvent(getAmplitude(), AMPLITUDE);
		fireReceiverChangeEvent(getPinkNoiseAmplitude(), PINK_NOISE_AMPLITUDE);
		fireReceiverChangeEvent(getPinkNoisePanAmplitude(), PINK_PAN_AMPLITUDE);
		fireReceiverChangeEvent(getPinkNoiseEntrainmentMultiple(), PINK_ENTRAINER_MULTIPLE);
		fireReceiverChangeEvent(isPinkNoisePan(), PINK_PAN);
		fireReceiverChangeEvent(isDesktopBackground(), ANIMATION_DESKTOP_BACKGROUND);
		fireReceiverChangeEvent(isFlash(), IS_FLASH);
		fireReceiverChangeEvent(isAnimation(), IS_ANIMATION);
		fireReceiverChangeEvent(isPsychedelic(), IS_PSYCHEDELIC);
		fireReceiverChangeEvent(isShimmer(), IS_SHIMMER);
		fireReceiverChangeEvent(getShimmerRectangle(), SHIMMER_RECTANGLE);
		fireReceiverChangeEvent(new Color(getFlashRed(), getFlashGreen(), getFlashBlue()));
		fireReceiverChangeEvent(isFlashBackground(), FLASH_BACKGROUND);

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

		for (EntrainerProgramInterval interval : intervals) {
			fireReceiverChangeEvent(interval.getValue());
		}
	}

	/**
	 * Checks for interval.
	 *
	 * @param s
	 *          the s
	 * @return true, if successful
	 */
	public boolean hasInterval(String s) {
		for (EntrainerProgramInterval interval : intervals) {
			if (interval.getValue().equals(s)) {
				return true;
			}
		}

		return false;
	}

	private void fireReceiverChangeEvent(Color c) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, c));
	}

	private void fireReceiverChangeEvent(String interval) {
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, interval, INTERVAL_ADD);
		sender.fireReceiverChangeEvent(e);
	}

	private void fireReceiverChangeEvent(String s, MediatorConstants parm) {
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, s, parm);
		sender.fireReceiverChangeEvent(e);
	}

	private void fireReceiverChangeEvent(boolean b, MediatorConstants parm) {
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, b, parm);
		sender.fireReceiverChangeEvent(e);
	}

	private void fireReceiverChangeEvent(double value, MediatorConstants name) {
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, value, name);
		sender.fireReceiverChangeEvent(e);
	}

	/**
	 * Gets the entrainment frequency.
	 *
	 * @return the entrainment frequency
	 */
	public double getEntrainmentFrequency() {
		return entrainmentFrequency;
	}

	private void setEntrainmentFrequency(double entrainmentFrequency) {
		this.entrainmentFrequency = entrainmentFrequency;
	}

	/**
	 * Gets the frequency.
	 *
	 * @return the frequency
	 */
	public double getFrequency() {
		return frequency;
	}

	private void setFrequency(double frequency) {
		this.frequency = frequency;
	}

	/**
	 * Gets the amplitude.
	 *
	 * @return the amplitude
	 */
	public double getAmplitude() {
		return amplitude;
	}

	private void setAmplitude(double amplitude) {
		this.amplitude = amplitude;
	}

	/**
	 * Checks if is pink noise pan.
	 *
	 * @return true, if is pink noise pan
	 */
	public boolean isPinkNoisePan() {
		return pinkNoisePan;
	}

	private void setPinkNoisePan(boolean pinkNoisePan) {
		this.pinkNoisePan = pinkNoisePan;
	}

	/**
	 * Gets the pink noise entrainment multiple.
	 *
	 * @return the pink noise entrainment multiple
	 */
	public double getPinkNoiseEntrainmentMultiple() {
		return pinkNoiseEntrainmentMultiple;
	}

	private void setPinkNoiseEntrainmentMultiple(double pinkNoiseEntrainmentMultiple) {
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
	public double getPinkNoisePanAmplitude() {
		return pinkNoiseAmplitude;
	}

	private void setPinkNoisePanAmplitude(double pinkNoiseAmplitude) {
		this.pinkNoiseAmplitude = pinkNoiseAmplitude;
	}

	/**
	 * Gets the pink noise amplitude.
	 *
	 * @return the pink noise amplitude
	 */
	public double getPinkNoiseAmplitude() {
		return pinkNoise;
	}

	private void setPinkNoiseAmplitude(double pinkNoise) {
		this.pinkNoise = pinkNoise;
	}

	/**
	 * Gets the custom intervals.
	 *
	 * @return the custom intervals
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

	private boolean hasCustomInterval(String s) {
		for (EntrainerProgramInterval interval : customIntervals) {
			if (interval.getValue().equals(s)) {
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
	public boolean isAnimation() {
		return isAnimation;
	}

	/**
	 * Sets the animation.
	 *
	 * @param isAnimation
	 *          the new animation
	 */
	public void setAnimation(boolean isAnimation) {
		this.isAnimation = isAnimation;
	}

	/**
	 * Checks if is flash.
	 *
	 * @return true, if is flash
	 */
	public boolean isFlash() {
		return isFlash;
	}

	/**
	 * Sets the flash.
	 *
	 * @param isFlash
	 *          the new flash
	 */
	public void setFlash(boolean isFlash) {
		this.isFlash = isFlash;
	}

	/**
	 * Checks if is psychedelic.
	 *
	 * @return true, if is psychedelic
	 */
	public boolean isPsychedelic() {
		return isPsychedelic;
	}

	/**
	 * Sets the psychedelic.
	 *
	 * @param isPsychedelic
	 *          the new psychedelic
	 */
	public void setPsychedelic(boolean isPsychedelic) {
		this.isPsychedelic = isPsychedelic;
	}

	/**
	 * Checks if is shimmer.
	 *
	 * @return true, if is shimmer
	 */
	public boolean isShimmer() {
		return isShimmer;
	}

	/**
	 * Sets the shimmer.
	 *
	 * @param isShimmer
	 *          the new shimmer
	 */
	public void setShimmer(boolean isShimmer) {
		this.isShimmer = isShimmer;
	}

	/**
	 * Checks if is desktop background.
	 *
	 * @return true, if is desktop background
	 */
	public boolean isDesktopBackground() {
		return isDesktopBackground;
	}

	/**
	 * Sets the desktop background.
	 *
	 * @param isDesktopBackground
	 *          the new desktop background
	 */
	public void setDesktopBackground(boolean isDesktopBackground) {
		this.isDesktopBackground = isDesktopBackground;
	}

	/**
	 * Gets the jsyn native jar.
	 *
	 * @return the jsyn native jar
	 */
	public String getJsynNativeJar() {
		return jsynNativeJar;
	}

	/**
	 * Sets the jsyn native jar.
	 *
	 * @param jsynNativeJar
	 *          the new jsyn native jar
	 */
	public void setJsynNativeJar(String jsynNativeJar) {
		this.jsynNativeJar = jsynNativeJar;
	}

	/**
	 * Gets the jsyn java jar.
	 *
	 * @return the jsyn java jar
	 */
	public String getJsynJavaJar() {
		return jsynJavaJar;
	}

	/**
	 * Sets the jsyn java jar.
	 *
	 * @param jsynJavaJar
	 *          the new jsyn java jar
	 */
	public void setJsynJavaJar(String jsynJavaJar) {
		this.jsynJavaJar = jsynJavaJar;
	}

	/**
	 * Checks if is native jsyn.
	 *
	 * @return true, if is native jsyn
	 */
	public boolean isNativeJsyn() {
		return isNativeJsyn;
	}

	/**
	 * Sets the native jsyn.
	 *
	 * @param isNativeJsyn
	 *          the new native jsyn
	 */
	public void setNativeJsyn(boolean isNativeJsyn) {
		this.isNativeJsyn = isNativeJsyn;
	}

	/**
	 * Gets the socket port.
	 *
	 * @return the socket port
	 */
	public int getSocketPort() {
		return socketPort;
	}

	/**
	 * Sets the socket port.
	 *
	 * @param socketPort
	 *          the new socket port
	 */
	public void setSocketPort(int socketPort) {
		this.socketPort = socketPort;
	}

	/**
	 * Checks if is delta socket message.
	 *
	 * @return true, if is delta socket message
	 */
	public boolean isDeltaSocketMessage() {
		return deltaSocketMessage;
	}

	/**
	 * Sets the delta socket message.
	 *
	 * @param fullSocketMessage
	 *          the new delta socket message
	 */
	public void setDeltaSocketMessage(boolean fullSocketMessage) {
		this.deltaSocketMessage = fullSocketMessage;
	}

	/**
	 * Checks if is socket connected.
	 *
	 * @return true, if is socket connected
	 */
	public boolean isSocketConnected() {
		return socketConnected;
	}

	/**
	 * Sets the socket connected.
	 *
	 * @param socketConnected
	 *          the new socket connected
	 */
	public void setSocketConnected(boolean socketConnected) {
		this.socketConnected = socketConnected;
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
	public int getMinWidth() {
		return minWidth;
	}

	/**
	 * Sets the min width.
	 *
	 * @param minWidth
	 *          the new min width
	 */
	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	/**
	 * Gets the min height.
	 *
	 * @return the min height
	 */
	public int getMinHeight() {
		return minHeight;
	}

	/**
	 * Sets the min height.
	 *
	 * @param minHeight
	 *          the new min height
	 */
	public void setMinHeight(int minHeight) {
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
	 * Gets the flash red.
	 *
	 * @return the flash red
	 */
	public int getFlashRed() {
		return flashRed;
	}

	/**
	 * Sets the flash red.
	 *
	 * @param flashRed
	 *          the new flash red
	 */
	public void setFlashRed(int flashRed) {
		this.flashRed = flashRed;
	}

	/**
	 * Gets the flash green.
	 *
	 * @return the flash green
	 */
	public int getFlashGreen() {
		return flashGreen;
	}

	/**
	 * Sets the flash green.
	 *
	 * @param flashGreen
	 *          the new flash green
	 */
	public void setFlashGreen(int flashGreen) {
		this.flashGreen = flashGreen;
	}

	/**
	 * Gets the flash blue.
	 *
	 * @return the flash blue
	 */
	public int getFlashBlue() {
		return flashBlue;
	}

	/**
	 * Sets the flash blue.
	 *
	 * @param flashBlue
	 *          the new flash blue
	 */
	public void setFlashBlue(int flashBlue) {
		this.flashBlue = flashBlue;
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
	 * Gets the background red.
	 *
	 * @return the background red
	 */
	public int getBackgroundRed() {
		return backgroundRed;
	}

	/**
	 * Sets the background red.
	 *
	 * @param backgroundRed
	 *          the new background red
	 */
	public void setBackgroundRed(int backgroundRed) {
		this.backgroundRed = backgroundRed;
	}

	/**
	 * Gets the background green.
	 *
	 * @return the background green
	 */
	public int getBackgroundGreen() {
		return backgroundGreen;
	}

	/**
	 * Sets the background green.
	 *
	 * @param backgroundGreen
	 *          the new background green
	 */
	public void setBackgroundGreen(int backgroundGreen) {
		this.backgroundGreen = backgroundGreen;
	}

	/**
	 * Gets the background blue.
	 *
	 * @return the background blue
	 */
	public int getBackgroundBlue() {
		return backgroundBlue;
	}

	/**
	 * Sets the background blue.
	 *
	 * @param backgroundBlue
	 *          the new background blue
	 */
	public void setBackgroundBlue(int backgroundBlue) {
		this.backgroundBlue = backgroundBlue;
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
	 * Checks if is splash on startup.
	 *
	 * @return true, if is splash on startup
	 */
	public boolean isSplashOnStartup() {
		return splashOnStartup;
	}

	/**
	 * Sets the splash on startup.
	 *
	 * @param splashOnStartup
	 *          the new splash on startup
	 */
	public void setSplashOnStartup(boolean splashOnStartup) {
		this.splashOnStartup = splashOnStartup;
	}

}
