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
package net.sourceforge.entrainer.sound.jsyn;

import com.softsynth.jsyn.AddUnit;
import com.softsynth.jsyn.SynthInput;
import com.softsynth.jsyn.SynthOutput;

// TODO: Auto-generated Javadoc
/**
 * Utility class to ease JSyn object usage.
 * 
 * @author burton
 *
 */
public class JSynUtil {

	/** The Constant LEFT_CHANNEL. */
	public static final int LEFT_CHANNEL = 0;

	/** The Constant RIGHT_CHANNEL. */
	public static final int RIGHT_CHANNEL = 1;

	/**
	 * Returns an AddUnit object adding both specified outputs.
	 *
	 * @param outputA
	 *          the output a
	 * @param outputB
	 *          the output b
	 * @return the adds the unit
	 */
	public static AddUnit add(SynthOutput outputA, SynthOutput outputB) {
		AddUnit unit = new AddUnit();
		unit.inputA.connect(outputA);
		unit.inputB.connect(outputB);

		return unit;
	}

	/**
	 * Connects the output to the input.
	 *
	 * @param to
	 *          the to
	 * @param from
	 *          the from
	 */
	public static void connect(SynthInput to, SynthOutput from) {
		to.connect(from);
	}

	/**
	 * Connects the output to the left channel of the input.
	 *
	 * @param to
	 *          the to
	 * @param from
	 *          the from
	 */
	public static void connectToLeftChannel(SynthInput to, SynthOutput from) {
		connect(to, from, LEFT_CHANNEL);
	}

	/**
	 * Connects the output to the right channel of the input.
	 *
	 * @param to
	 *          the to
	 * @param from
	 *          the from
	 */
	public static void connectToRightChannel(SynthInput to, SynthOutput from) {
		connect(to, from, RIGHT_CHANNEL);
	}

	/**
	 * Connects the output to the specified channel of the input.
	 *
	 * @param to
	 *          the to
	 * @param from
	 *          the from
	 * @param channel
	 *          the channel
	 */
	public static void connect(SynthInput to, SynthOutput from, int channel) {
		to.connect(0, from, channel);
	}

	/**
	 * Connects the input to the output.
	 *
	 * @param to
	 *          the to
	 * @param from
	 *          the from
	 */
	public static void connect(SynthOutput to, SynthInput from) {
		to.connect(from);
	}

	/**
	 * Connects the input to the left channel of the output.
	 *
	 * @param to
	 *          the to
	 * @param from
	 *          the from
	 */
	public static void connectToLeftChannel(SynthOutput to, SynthInput from) {
		connect(to, from, LEFT_CHANNEL);
	}

	/**
	 * Connects the input to the right channel of the output.
	 *
	 * @param to
	 *          the to
	 * @param from
	 *          the from
	 */
	public static void connectToRightChannel(SynthOutput to, SynthInput from) {
		connect(to, from, RIGHT_CHANNEL);
	}

	/**
	 * Connects the input to the specified channel of the output.
	 *
	 * @param to
	 *          the to
	 * @param from
	 *          the from
	 * @param channel
	 *          the channel
	 */
	public static void connect(SynthOutput to, SynthInput from, int channel) {
		to.connect(0, from, channel);
	}

	/**
	 * Connects the 'from' SynthInput to the 'to' SynthInput.
	 *
	 * @param to
	 *          the to
	 * @param from
	 *          the from
	 */
	public static void connect(SynthInput to, SynthInput from) {
		to.connect(from);
	}

}
