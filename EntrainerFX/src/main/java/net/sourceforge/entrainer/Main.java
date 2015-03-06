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
package net.sourceforge.entrainer;

import java.awt.Component;
import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URISyntaxException;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.entrainer.gui.About;
import net.sourceforge.entrainer.gui.EntrainerFX;
import net.sourceforge.entrainer.gui.jfx.EntrainerFXSplash;
import net.sourceforge.entrainer.gui.jfx.trident.ColorPropertyInterpolator;
import net.sourceforge.entrainer.gui.jfx.trident.LinearGradientInterpolator;
import net.sourceforge.entrainer.gui.jfx.trident.RadialGradientInterpolator;
import net.sourceforge.entrainer.gui.popup.NotificationWindow;
import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.util.Utils;
import net.sourceforge.entrainer.xml.Settings;

import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.TridentConfig;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * Main class to instantiate the {@link EntrainerFX}. Will display the
 * {@link About} dialog should the JSyn libraries be missing.
 * 
 * @author burton
 */
public class Main {

	static {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				try {
					e.printStackTrace();
				} catch (Throwable f) {
					f.printStackTrace();
				}
			}
		});
	}

	private static Logger logger = LoggerFactory.getLogger(Main.class);

	/**
	 * The main method.
	 *
	 * @param args
	 *          the arguments
	 */
	public static void main(String[] args) {
		Thread.currentThread().setName("EntrainerFX");
		architectureCheck();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					TridentConfig.getInstance().addPropertyInterpolator(new ColorPropertyInterpolator());
					TridentConfig.getInstance().addPropertyInterpolator(new LinearGradientInterpolator());
					TridentConfig.getInstance().addPropertyInterpolator(new RadialGradientInterpolator());

					new JFXPanel(); // initializes JavaFX environment
					startApplication();
				} catch (Throwable e) {
					errorOnStartup(e);
				}
			}
		});
	}

	private static void startApplication() throws InstantiationException, IllegalAccessException, ClassNotFoundException,
			URISyntaxException, InterruptedException {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		Settings settings = Settings.getInstance();
		final EntrainerFXSplash splash = settings.isSplashOnStartup() ? new EntrainerFXSplash(true) : null;
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				checkForOldSubstanceJar();
				try {
					GuiUtil.loadAllLafs();
				} catch (Exception e) {
					errorOnStartup(e);
				}
				GuiUtil.changeLookAndFeel(settings.getLafClass(), settings.getLafThemePack(), new Component[] {});
				showGui(splash);
			}
		});
		thread.start();
	}

	private static void checkForOldSubstanceJar() {
		File oldSubstance = new File("./lafs/substance.jar");
		if (!oldSubstance.exists()) return;

		oldSubstance.renameTo(new File("./lafs/substance.bak"));
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new NotificationWindow("Old substance.jar renamed to substance.bak", null);
			}
		});
	}

	private static void architectureCheck() {
		try {
			if (!JavaVersionChecker.VERSION_OK) {
				StringBuilder builder = new StringBuilder();
				builder.append("Cannot run Entrainer.  Minimum version required is ");
				builder.append(JavaVersionChecker.MIN_VERSION);
				builder.append(".\nYou are running version ");
				builder.append(JavaVersionChecker.CURRENT);
				builder.append(".\nPlease visit http://java.oracle.com to get the latest Java Runtime Environment");
				GuiUtil.handleProblem(new RuntimeException(builder.toString()));
				System.exit(-1);
			}
		} catch (Throwable e) {
			logger.error("Cannot evaluate java version " + System.getProperty("java.version"), e);
		}
	}

	private static void showGui(final EntrainerFXSplash splash) {
		Thread thread = new Thread() {
			public void run() {
				EntrainerFX entrainer = EntrainerFX.getInstance();
				Utils.snooze(1000);
				GuiUtil.fadeIn(entrainer, 5000, new TimelineCallbackAdapter() {
					public void onTimelineStateChanged(TimelineState oldState, TimelineState newState, float durationFraction,
							float timelinePosition) {
						if (TimelineState.PLAYING_FORWARD.equals(newState) && splash != null) {
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									splash.toFront();
								}
							});
						}
					}
				});
			}
		};

		thread.start();
	}

	private static void errorOnStartup(Throwable e) {
		e.printStackTrace();
		logger.error("JSyn library must be in the Library Path: " + System.getProperty("java.library.path"), e);
		System.out.println("Library Path: " + System.getProperty("java.library.path"));
		About.showAboutDialog();
		System.exit(0);
	}

}
