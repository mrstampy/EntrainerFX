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
package net.sourceforge.entrainer.gui.jfx.animation;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * This class dynamically loads the jar'd animations located in the 'animation'
 * directory of the Entrainer install. Refer to {@link JFXEntrainerAnimation}
 * for more information.
 * 
 * @author burton
 */
public final class JFXAnimationRegister {
	private static JFXAnimationLoader animationLoader = new JFXAnimationLoader();

	static {
		animationLoader.loadAllAnimations();
	}

	private JFXAnimationRegister() {
		super();
	}

	/**
	 * Returns the list of {@link JFXEntrainerAnimation} implementations that have
	 * been loaded on startup.
	 *
	 * @return the entrainer animations
	 */
	public static List<JFXEntrainerAnimation> getEntrainerAnimations() {
		return new ArrayList<JFXEntrainerAnimation>(animationLoader.getEntrainerAnimations());
	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public static boolean isEmpty() {
		return animationLoader.isEmpty();
	}

	/**
	 * Returns the {@link JFXEntrainerAnimation} specified by its
	 * <code>toString();</code> implementation.
	 *
	 * @param stringRep
	 *          the string rep
	 * @return the entrainer animation
	 */
	public static JFXEntrainerAnimation getEntrainerAnimation(String stringRep) {
		for (JFXEntrainerAnimation animation : getEntrainerAnimations()) {
			if (stringRep.equals(animation.toString())) {
				return animation;
			}
		}

		return null;
	}

}
