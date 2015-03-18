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
package net.sourceforge.entrainer.gui.flash;

import static net.sourceforge.entrainer.gui.flash.ColourAdjustState.DEFAULT_COLOUR_ADJUST;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Lighting;
import net.sourceforge.entrainer.gui.flash.effectable.BloomEffectable;
import net.sourceforge.entrainer.gui.flash.effectable.BoxBlurEffectable;
import net.sourceforge.entrainer.gui.flash.effectable.Effectable;
import net.sourceforge.entrainer.gui.flash.effectable.GaussianBlurEffectable;
import net.sourceforge.entrainer.gui.flash.effectable.GlowEffectable;
import net.sourceforge.entrainer.gui.flash.effectable.MotionBlurEffectable;
import net.sourceforge.entrainer.gui.flash.effectable.SepiaToneEffectable;
import net.sourceforge.entrainer.gui.flash.effectable.ShadowEffectable;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class FlashOptions.
 */
public class FlashOptions {
	private static final Logger log = LoggerFactory.getLogger(FlashOptions.class);

	private static FlashOptions options;

	private boolean flashBackground;

	/**
	 * Gets the instance.
	 *
	 * @return the instance
	 */
	public static synchronized FlashOptions getinstance() {
		if (options == null) options = new FlashOptions();
		return options;
	}

	private Map<FlashType, Boolean> flashTypes = new LinkedHashMap<>();

	private AtomicBoolean opacity = new AtomicBoolean(false);

	private ReadWriteLock lock = new ReentrantReadWriteLock();
	private Lock readLock = lock.readLock();
	private Lock writeLock = lock.writeLock();

	private ColourAdjustState colourAdjustState = new ColourAdjustState();
	private Effect effect = null;
	private Effect currentEffect = null;

	private boolean started = false;

	private AtomicBoolean flip = new AtomicBoolean(false);

	private FlashOptions() {
		populateMap();
		initMediator();
	}

	/**
	 * Gets the effect.
	 *
	 * @return the effect
	 */
	public Effect getEffect() {
		readLock.lock();
		try {
			return effect;
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Checks for effect.
	 *
	 * @return true, if successful
	 */
	public boolean hasEffect() {
		return currentEffect != null;
	}

	/**
	 * Default effect.
	 *
	 * @return the effect
	 */
	public Effect defaultEffect() {
		return DEFAULT_COLOUR_ADJUST;
	}

	/**
	 * Checks if is opacity.
	 *
	 * @return true, if is opacity
	 */
	public boolean isOpacity() {
		return opacity.get();
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case FLASH_BACKGROUND:
					flashBackground = e.getBooleanValue();
					break;
				case FLASH_TYPE:
					evaluate(((FlashType) e.getOption()), e.getBooleanValue());
					break;
				case ENTRAINMENT_FREQUENCY_PULSE:
					evaluateForPulse(e.getBooleanValue());
					break;
				case START_ENTRAINMENT:
					evaluateStart(e.getBooleanValue());
					break;
				default:
					break;
				}
			}
		});
	}

	private void evaluateStart(boolean b) {
		if (!flashBackground) return;

		started = b;
		writeLock.lock();
		try {
			if (b) {
				createEffect();
			} else {
				setEffectDefault();
				currentEffect = null;
			}
		} finally {
			writeLock.unlock();
		}
	}

	private void createEffect() {
		List<Effectable> effectables = getEffectables();

		Effect last = getNonEffectable();

		createEffect(effectables, last);
	}

	private void createEffect(List<Effectable> effectables, Effect last) {
		if (effectables.isEmpty() && last == null) {
			log.info("No effect");
			return;
		}

		for (Effectable effectable : effectables) {
			addEffect((Effect) effectable);
		}

		if (last != null) addEffect(last);
	}

	private Effect getNonEffectable() {
		Effect e = null;
		Effect tmp = e;

		for (Entry<FlashType, Boolean> entry : flashTypes.entrySet()) {
			if (entry.getValue() && isNonEffectable(entry.getKey())) {
				tmp = createNonEffectable(entry.getKey());

				if (e == null) {
					e = tmp;
				} else {
					log.error("Cannot use {}, already created {}", tmp.getClass(), e.getClass());
				}
			}
		}

		return e;
	}

	private Effect createNonEffectable(FlashType key) {
		switch (key) {
		case COLOUR_ADJUST:
			return colourAdjustState.getColorAdjust();
		case LIGHTING:
			return new Lighting();
		default:
			log.error("should not be here: createNonEffectable");
			return null;
		}
	}

	private boolean isNonEffectable(FlashType key) {
		switch (key) {
		case COLOUR_ADJUST:
		case LIGHTING:
			return true;
		default:
			break;
		}

		return false;
	}

	private List<Effectable> getEffectables() {
		List<Effectable> effectables = new ArrayList<>();

		for (Entry<FlashType, Boolean> entry : flashTypes.entrySet()) {
			if (entry.getValue()) addEffectable(entry.getKey(), effectables);
		}

		return effectables;
	}

	private void addEffectable(FlashType key, List<Effectable> effectables) {
		switch (key) {
		case BLOOM:
			effectables.add(new BloomEffectable());
			break;
		case BOX_BLUR:
			effectables.add(new BoxBlurEffectable());
			break;
		case GAUSSIAN_BLUR:
			effectables.add(new GaussianBlurEffectable());
			break;
		case GLOW:
			effectables.add(new GlowEffectable());
			break;
		case MOTION_BLUR:
			effectables.add(new MotionBlurEffectable());
			break;
		case SEPIA_TONE:
			effectables.add(new SepiaToneEffectable());
			break;
		case SHADOW:
			effectables.add(new ShadowEffectable());
			break;
		default:
			break;

		}
	}

	private void evaluateForPulse(boolean b) {
		writeLock.lock();
		try {
			evalForPulse(b);
		} finally {
			writeLock.unlock();
		}
	}

	private void evalForPulse(boolean b) {
		if (effect == null && currentEffect == null && !colourAdjustState.isColourAdjusting()) return;

		if (!flashBackground) b = false;

		colourAdjustState.evaluateForPulse(b);
		ColorAdjust ca = null;
		if (colourAdjustState.isColourAdjusting()) {
			ca = setColourAdjust();
			if (currentEffect instanceof ColorAdjust) return;
		}

		if (b) {
			boolean rev = flip.get();

			setEffect(rev ? currentEffect : colourAdjustState.isColourAdjusting() ? ca : null);

			flip.set(!rev);
		} else {
			setEffectDefault();
			flip.set(false);
		}
	}

	private void setEffectDefault() {
		setEffect(colourAdjustState.isColourAdjusting() ? DEFAULT_COLOUR_ADJUST : null);
	}

	private ColorAdjust setColourAdjust() {
		if (currentEffect == null || currentEffect instanceof ColorAdjust) {
			ColorAdjust colourAdjust = getColourAdjust(currentEffect);
			setEffect(colourAdjust);
			currentEffect = effect;
			return colourAdjust;
		}

		if (!(currentEffect instanceof Effectable)) {
			log.error("Cannot add colour adjust to {}", currentEffect.getClass());
			return null;
		}

		Effectable ef = (Effectable) currentEffect;
		Effect sub = ef.getInput();

		return setColourAdjust(ef, sub);
	}

	private ColorAdjust setColourAdjust(Effectable ef, Effect sub) {
		if (sub == null || sub instanceof ColorAdjust) {
			ColorAdjust colourAdjust = getColourAdjust(sub);
			ef.setInput(colourAdjust);
			return colourAdjust;
		}

		if (!(sub instanceof Effectable)) {
			log.error("Cannot add colour adjust to {}", sub.getClass());
			return null;
		}

		Effectable ef2 = (Effectable) sub;
		Effect sub2 = ef2.getInput();

		return setColourAdjust(ef2, sub2);
	}

	private ColorAdjust getColourAdjust(Effect ef) {
		return ef == DEFAULT_COLOUR_ADJUST ? colourAdjustState.getColorAdjust() : DEFAULT_COLOUR_ADJUST;
	}

	/**
	 * Evaluate.
	 *
	 * @param flashType
	 *          the flash type
	 * @param b
	 *          the b
	 */
	protected void evaluate(FlashType flashType, boolean b) {
		flashTypes.put(flashType, b);

		switch (flashType) {
		case BLOOM:
			evaluateBloom(b);
			break;
		case BOX_BLUR:
			evaluateBoxBlur(b);
			break;
		case COLOUR_ADJUST:
			evaluateColourAdjust(b);
			break;
		case GAUSSIAN_BLUR:
			evaluateGaussianBlur(b);
			break;
		case GLOW:
			evaluateGlow(b);
			break;
		case LIGHTING:
			evaluateLighting(b);
			break;
		case MOTION_BLUR:
			evaluateMotionBlur(b);
			break;
		case OPACITY:
			opacity.set(b);
			break;
		case SEPIA_TONE:
			evaluateSepiaTone(b);
			break;
		case SHADOW:
			evaluateShadow(b);
			break;
		default:
			log.warn("Unknown flash type {}", flashType);
			break;

		}
	}

	private void evaluateShadow(boolean b) {
		evaluateEffect(b, ShadowEffectable.class);
	}

	private void evaluateSepiaTone(boolean b) {
		evaluateEffect(b, SepiaToneEffectable.class);
	}

	private void evaluateMotionBlur(boolean b) {
		evaluateEffect(b, MotionBlurEffectable.class);
	}

	private void evaluateLighting(boolean b) {
		evaluateEffect(b, Lighting.class);
	}

	private void evaluateGlow(boolean b) {
		evaluateEffect(b, GlowEffectable.class);
	}

	private void evaluateGaussianBlur(boolean b) {
		evaluateEffect(b, GaussianBlurEffectable.class);
	}

	private void evaluateColourAdjust(boolean b) {
		colourAdjustState.setColourAdjusting(b);
		evaluateEffect(b, ColorAdjust.class);
	}

	private void evaluateBoxBlur(boolean b) {
		evaluateEffect(b, BoxBlurEffectable.class);
	}

	private void evaluateBloom(boolean b) {
		evaluateEffect(b, BloomEffectable.class);
	}

	private void evaluateEffect(boolean b, Class<? extends Effect> clz) {
		if (!started) return;

		if (b) {
			try {
				addEffect(clz.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				log.error("Unexpected exception", e);
			}
		} else {
			removeEffect(clz);
		}
	}

	private void addEffect(Effect add) {
		writeLock.lock();
		try {
			addEffectImpl(add);
		} finally {
			writeLock.unlock();
		}
	}

	private void addEffectImpl(Effect add) {
		log.debug("Adding effect {}", add);

		if (currentEffect == null && effect == null) {
			log.debug("Effect was null");
			currentEffect = add;
			setEffect(add);
			return;
		}

		if (!(currentEffect instanceof Effectable)) {
			log.debug("Not effectable: {}", currentEffect);
			currentEffect = add;
			setEffect(add);
			return;
		}

		Effectable ef = (Effectable) currentEffect;
		Effectable last = ef;

		Effect sub = ef.getInput();

		while (sub != null && sub instanceof Effectable) {
			last = (Effectable) sub;
			sub = last.getInput();
		}

		log.debug("Adding effect if last not null: {}", last);

		if (last != null) last.setInput(add);

		setEffect(currentEffect);
	}

	private void removeEffect(Class<? extends Effect> clz) {
		writeLock.lock();
		try {
			removeEffectImpl(clz);
		} finally {
			writeLock.unlock();
		}
	}

	private void removeEffectImpl(Class<? extends Effect> clz) {
		log.debug("Removing class {}", clz.getSimpleName());
		if (currentEffect == null) return;

		if (!(currentEffect instanceof Effectable)) {
			if (currentEffect.getClass().equals(clz)) {
				log.debug("Removing root non effectable");
				setEffect(null);
				currentEffect = null;
			} else {
				log.warn("Cannot remove effect {} using class {}", effect.getClass(), clz);
			}
			return;
		}

		Effectable ef = (Effectable) currentEffect;
		Effect sub = ef.getInput();

		removeEffect(null, ef, sub, clz);
	}

	private void removeEffect(Effectable supr, Effectable ef, Effect sub, Class<? extends Effect> clz) {
		if (ef.getClass().equals(clz)) {
			if (supr == null && sub == null) {
				log.debug("No effect left");
				currentEffect = null;
				setEffect(null);
			} else if (supr == null) {
				log.debug("supr null, effect = {}", sub);
				currentEffect = sub;
				setEffect(sub);
			} else {
				log.debug("Setting supr {}'s input to {}", sub);
				supr.setInput(sub);
			}
			return;
		}

		if (sub == null) {
			log.warn("sub is null, {} is not part of the current effect", clz);
			return;
		}

		if (sub.getClass().equals(clz)) {
			if (sub instanceof Effectable) {
				log.debug("Setting {}'s input to sub {}'s input", ef, sub);
				ef.setInput(((Effectable) sub).getInput());
			} else {
				log.debug("setting {}'s input to null", ef);
				ef.setInput(null);
			}
			return;
		}

		if (!(sub instanceof Effectable)) {
			log.warn("{} is not part of the current effect", clz);
			return;
		}

		Effectable supr2 = ef;
		Effectable ef2 = (Effectable) sub;
		Effect sub2 = ef2.getInput();

		removeEffect(supr2, ef2, sub2, clz);
	}

	private void populateMap() {
		for (FlashType type : FlashType.values()) {
			flashTypes.put(type, false);
		}
	}

	private void setEffect(Effect effect) {
		this.effect = effect;
	}

}
