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
package net.sourceforge.entrainer.gui.jfx.shimmer;

import javafx.scene.paint.Paint;
import net.sourceforge.entrainer.gui.jfx.JFXUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractFlashingShimmer.
 *
 * @param <P>
 *          the generic type
 */
public abstract class AbstractFlashingShimmer<P extends Paint> extends AbstractShimmer<P> {

	private double opacity = 1.0;
	private double halfOpacity = 0.5;

	/**
	 * Instantiates a new abstract flashing shimmer.
	 */
	protected AbstractFlashingShimmer() {
		super();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.gui.jfx.shimmer.AbstractShimmer#pulse(boolean)
	 */
	@Override
	protected void pulse(boolean b) {
		setShimmerOpacity(determineOpacity(b));
	}

	private double determineOpacity(boolean b) {
		return b ? getOpacity() == opacity ? halfOpacity : opacity : opacity;
	}

	private void setShimmerOpacity(final double o) {
		JFXUtils.runLater(() -> setOpacity(o));
	}

}
