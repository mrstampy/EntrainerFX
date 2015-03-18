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
package net.sourceforge.entrainer.gui.flash.effectable;

import javafx.scene.effect.Glow;

// TODO: Auto-generated Javadoc
/**
 * The Class GlowEffectable.
 */
public class GlowEffectable extends Glow implements Effectable {

	/**
	 * Instantiates a new glow effectable.
	 */
	public GlowEffectable() {
		super();
	}

	/**
	 * Instantiates a new glow effectable.
	 *
	 * @param level
	 *          the level
	 */
	public GlowEffectable(double level) {
		super(level);
	}

}
