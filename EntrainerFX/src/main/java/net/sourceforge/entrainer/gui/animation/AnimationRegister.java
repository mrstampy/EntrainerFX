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
package net.sourceforge.entrainer.gui.animation;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.entrainer.gui.jfx.animation.JFXAnimationRegister;
import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.util.EntrainerRegister;

// TODO: Auto-generated Javadoc
/**
 * This class dynamically loads the jar'd animations located in the 'animation'
 * directory of the Entrainer install. Refer to {@link EntrainerAnimation} for
 * more information.
 *
 * @author burton
 * @deprecated now using {@link JFXAnimationRegister}
 */
public final class AnimationRegister {
	private static List<EntrainerAnimation> animations = new ArrayList<EntrainerAnimation>();

	private AnimationRegister() {
		super();
	}

	/**
	 * Returns the list of {@link EntrainerAnimation} implementations that have
	 * been loaded on startup.
	 *
	 * @return the entrainer animations
	 */
	public static List<EntrainerAnimation> getEntrainerAnimations() {
		if (animations.isEmpty()) {
			try {
				loadAllAnimations();
			} catch (Exception e) {
				GuiUtil.handleProblem(e);
			}
		}

		return animations;
	}

	/**
	 * Returns the {@link EntrainerAnimation} specified by its
	 * <code>toString();</code> implementation.
	 *
	 * @param stringRep the string rep
	 * @return the entrainer animation
	 */
	public static EntrainerAnimation getEntrainerAnimation(String stringRep) {
		for (EntrainerAnimation animation : getEntrainerAnimations()) {
			if (stringRep.equals(animation.toString())) {
				return animation;
			}
		}

		return null;
	}

	private static void loadAllAnimations() throws URISyntaxException {
		List<URL> jarUrls = EntrainerRegister.getPackageUrls("animation");

		EntrainerRegister.loadClasses(jarUrls, EntrainerAnimation.class, animations);
	}

}
