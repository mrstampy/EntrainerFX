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
package net.sourceforge.entrainer.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.guitools.MigHelper;
import net.sourceforge.entrainer.guitools.trident.TimelineChain;

import org.pushingpixels.trident.Timeline.RepeatBehavior;

// TODO: Auto-generated Javadoc
/**
 * Splash window displayed on Entrainer startup.
 * 
 * @author burton
 */
public class EntrainerSplash extends JWindow {

	private static final long serialVersionUID = 1L;

	private static Color BACKGROUND = new Color(145, 140, 247);
	private static Color FOREGROUND = new Color(37, 144, 46);
	private static Color ANIM = new Color(135, 255, 151);

	private TimelineChain chain;

	/**
	 * Instantiates a new entrainer splash.
	 */
	public EntrainerSplash() {
		super();
		init();
		layoutComponents();
		pack();
		getContentPane().setBackground(BACKGROUND);
		GuiUtil.centerOnScreen(this);
		animateBackground();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Window#setVisible(boolean)
	 */
	public void setVisible(boolean b) {
		if (b) {
			super.setVisible(b);
		} else {
			GuiUtil.fadeOutAndDispose(this, 5000);
		}
	}

	private void init() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (chain != null) {
					chain.cancel();
				}
			}
		});
	}

	private void animateBackground() {
		GuiUtil.fadeIn(this, 3000);

		chain = new TimelineChain(getContentPane());

		chain.addTimeline(400, null, RepeatBehavior.REVERSE, 2);
		chain.addTransition("background", ANIM);

		chain.addTimeline(400);
		chain.addTransition("background", ANIM);

		chain.addTimeline(2500);
		chain.addTransition("background", BACKGROUND);

		chain.play();
	}

	private void layoutComponents() {
		((JPanel) getContentPane()).setBorder(new BevelBorder(BevelBorder.RAISED));
		MigHelper mh = new MigHelper(getContentPane());
		mh.setLayoutInsets(15, 5, 15, 5);

		JLabel label = new JLabel("Entrainer");
		label.setForeground(FOREGROUND);
		Font font = label.getFont();
		label.setFont(new Font(font.getName(), font.getStyle(), 30));

		JLabel pic = new JLabel(GuiUtil.getIcon("/brain.jpg"));
		pic.setBorder(new EtchedBorder(EtchedBorder.RAISED, FOREGROUND, FOREGROUND.brighter()));
		mh.addLast(label).addLast(pic);

		label = new JLabel("For the only journey that matters...");
		label.setForeground(FOREGROUND);
		label.setFont(new Font(font.getName(), font.getStyle(), 18));
		mh.add(label);
	}
}
