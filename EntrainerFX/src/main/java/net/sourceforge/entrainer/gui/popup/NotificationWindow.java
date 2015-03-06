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
package net.sourceforge.entrainer.gui.popup;

import java.awt.Color;
import java.awt.Container;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.BevelBorder;

import net.sourceforge.entrainer.guitools.GuiUtil;

import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;

// TODO: Auto-generated Javadoc
/**
 * The Class NotificationWindow.
 */
@SuppressWarnings("serial")
public class NotificationWindow extends JWindow {

	private JPanel panel = new JPanel();

	private Color background = new Color(145, 140, 247);

	/**
	 * Instantiates a new notification window.
	 *
	 * @param message
	 *          the message
	 * @param container
	 *          the container
	 */
	public NotificationWindow(String message, Container container) {
		initGui(message, container);
		pack();
		execute();
	}

	private void initGui(String message, Container container) {
		panel.setBackground(background);
		panel.setBorder(new BevelBorder(BevelBorder.RAISED));
		panel.add(new JLabel(message));

		add(panel);

		setLocationRelativeTo(container);
	}

	private void execute() {
		TimelineCallbackAdapter tca = new TimelineCallbackAdapter() {

			public void onTimelineStateChanged(TimelineState oldState, TimelineState newState, float durationFraction,
					float timelinePosition) {
				toFront();

				if (TimelineState.DONE == newState) {
					fadeOut();
				}
			}
		};

		GuiUtil.fadeIn(this, 500, tca);
	}

	private void fadeOut() {
		GuiUtil.fadeOutAndDispose(this, 4000);
	}

}
