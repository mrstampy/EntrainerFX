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
package net.sourceforge.entrainer.guitools;

/*
 * Copyright (C) 2008, 2009 Burton Alexander
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.IllegalComponentStateException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.sourceforge.entrainer.gui.jfx.JFXUtils;
import net.sourceforge.entrainer.gui.laf.LAFRegister;
import net.sourceforge.entrainer.util.Utils;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallback;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.awt.AWTUtilities;

// TODO: Auto-generated Javadoc
/**
 * Convenience gui methods class.
 * 
 * @author burton
 */
public class GuiUtil {

	private static Logger logger = LoggerFactory.getLogger(GuiUtil.class);

	/**
	 * Centers the given {@link Window} on the screen.
	 *
	 * @param w
	 *          the w
	 */
	public static void centerOnScreen(Window w) {
		Dimension screenSize = getScreenSize();
		Dimension size = w.getSize();

		w.setLocation((screenSize.width - size.width) / 2, (screenSize.height - size.height) / 2);
	}

	/**
	 * Returns a dimension representing the full size of the screen.
	 *
	 * @return the screen size
	 */
	public static Dimension getScreenSize() {
		Toolkit kit = Toolkit.getDefaultToolkit();

		return kit.getScreenSize();
	}

	/**
	 * Returns a dimension representing the working area of the screen.
	 *
	 * @return the working screen size
	 */
	public static Dimension getWorkingScreenSize() {
		Dimension full = getScreenSize();

		if (isMac()) {
			return new Dimension(full.width, full.height - 22);
		}

		return full;
	}

	private static boolean isMac() {
		return System.getProperty("os.name").contains("Mac");
	}

	/**
	 * Packs and shows the specified {@link JDialog} in the center of the screen.
	 *
	 * @param jd
	 *          the jd
	 */
	public static void showDialog(final JDialog jd) {
		addFadeOutInvisibleListener(jd, 500);
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				jd.pack();
				centerOnScreen(jd);
				fadeIn(jd, 500);
			}

		});
	}

	/**
	 * Returns the parent dialog of the given {@link Container}.
	 *
	 * @param c
	 *          the c
	 * @return the parent dialog
	 */
	public static JDialog getParentDialog(Container c) {
		Container parent = c.getParent();
		if (parent instanceof JDialog) {
			return (JDialog) parent;
		} else if (parent == null) {
			return null;
		} else {
			return getParentDialog(parent);
		}
	}

	/**
	 * Returns an {@link ImageIcon} object from the given resource (representing
	 * the image) name.
	 *
	 * @param name
	 *          the name
	 * @return the icon
	 */
	public static ImageIcon getIcon(String name) {
		URL url = GuiUtil.class.getResource(name);
		return new ImageIcon(url);
	}

	/**
	 * Returns an image specified by its URL.
	 *
	 * @param urlName
	 *          the url name
	 * @return the image
	 */
	public static Image getImage(String urlName) {
		URL url;
		try {
			url = new URL(urlName);
		} catch (MalformedURLException e) {
			handleProblem(e);
			return null;
		}
		return new ImageIcon(url).getImage();
	}

	/**
	 * Scales the specified image to the specified size.
	 *
	 * @param img
	 *          the img
	 * @param size
	 *          the size
	 * @return the image
	 */
	public static Image scaleImage(Image img, Dimension size) {
		BufferedImage newImage = getGraphicsConfiguration().createCompatibleImage(size.width, size.height);

		newImage.getGraphics().drawImage(img, 0, 0, size.width, size.height, null);

		return newImage;
	}

	private static GraphicsConfiguration getGraphicsConfiguration() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	}

	/**
	 * Handle problem.
	 *
	 * @param e
	 *          the e
	 * @param useLatch
	 *          the use latch
	 */
	public static void handleProblem(Throwable e, boolean useLatch) {
		e.printStackTrace();

		final String msg = e.getMessage();
		
		logger.error(msg, e);

		JFXUtils.runLater(() -> showAlert(msg));
	}

	private static void showAlert(String msg) {
		if(msg == null || msg.equals("null") || msg.isEmpty()) msg = "An unknown error has occurred";
		
		Alert alert = new Alert(AlertType.ERROR, msg, ButtonType.OK);
		alert.setHeaderText("Please see the ~/EntrainerFX-Settings/entrainer.log file for details");
		alert.setTitle("Unexpected Exception");
		alert.show();
	}

	/**
	 * Displays a message pane for the user containing the error, prints the stack
	 * trace and logs the error in the entrainer.log file.
	 *
	 * @param e
	 *          the e
	 */
	public static void handleProblem(Throwable e) {
		handleProblem(e, false);
	}

	/**
	 * Unused currently.
	 *
	 * @param e
	 *          the e
	 * @return the string
	 */
	protected static String stackTraceToString(Throwable e) {
		StringWriter writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		e.printStackTrace(pw);

		return writer.toString();
	}

	/**
	 * Sets the button icons, from
	 * http://www.iconarchive.com/category/application/
	 * play-stop-pause-icons-by-icons-land.html
	 *
	 * @param prefix
	 *          the prefix
	 * @param button
	 *          the button
	 * @param toolTip
	 *          the tool tip
	 */
	public static void initButton(String prefix, AbstractButton button, String toolTip) {
		button.setIcon(getIcon("/" + prefix + "-Normal.png"));
		button.setPressedIcon(getIcon("/" + prefix + "-Pressed.png"));
		button.setDisabledIcon(getIcon("/" + prefix + "-Disabled.png"));
		button.setRolloverIcon(getIcon("/" + prefix + "-Hot.png"));
		initIconButton(button, toolTip);
	}

	/**
	 * Inits the icon button.
	 *
	 * @param button
	 *          the button
	 * @param toolTip
	 *          the tool tip
	 */
	public static void initIconButton(AbstractButton button, String toolTip) {
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setToolTipText(toolTip);
	}

	/**
	 * Change look and feel.
	 *
	 * @param className
	 *          the class name
	 * @param themePackName
	 *          the theme pack name
	 * @param components
	 *          the components
	 */
	public static void changeLookAndFeel(String className, String themePackName, Component... components) {
		try {
			LAFRegister.setLookAndFeel(className, themePackName, components);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				LAFRegister.setLookAndFeel(UIManager.getSystemLookAndFeelClassName(), components);
			} catch (Exception f) {
				GuiUtil.handleProblem(f);
				System.exit(-1);
			}
		}

		System.gc();
	}

	/**
	 * Load all lafs.
	 *
	 * @throws URISyntaxException
	 *           the URI syntax exception
	 * @throws InstantiationException
	 *           the instantiation exception
	 * @throws IllegalAccessException
	 *           the illegal access exception
	 * @throws ClassNotFoundException
	 *           the class not found exception
	 */
	public static void loadAllLafs() throws URISyntaxException, InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		LAFRegister.loadAllLafs();
	}

	/**
	 * Gets the virtual screen size.
	 *
	 * @return the virtual screen size
	 */
	public static Dimension getVirtualScreenSize() {
		Rectangle virtualBounds = new Rectangle();

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = ge.getScreenDevices();

		for (GraphicsDevice device : devices) {
			GraphicsConfiguration[] gc = device.getConfigurations();
			for (GraphicsConfiguration config : gc) {
				virtualBounds = virtualBounds.union(config.getBounds());
			}
		}

		return virtualBounds.getSize();
	}

	/**
	 * Gets the working virtual screen size.
	 *
	 * @return the working virtual screen size
	 */
	public static Dimension getWorkingVirtualScreenSize() {
		Dimension virtual = getVirtualScreenSize();

		if (isMac()) {
			virtual = new Dimension(virtual.width, virtual.height - 22);
		}

		return virtual.getSize();
	}

	/**
	 * Fade in.
	 *
	 * @param window
	 *          the window
	 * @param millis
	 *          the millis
	 * @param callbacks
	 *          the callbacks
	 */
	public static void fadeIn(final Window window, int millis, TimelineCallback... callbacks) {
		try {
			AWTUtilities.setWindowOpacity(window, 0);
		} catch (IllegalComponentStateException e) {
			logger.error("Unexpected exception", e);
			if (e.getMessage().contains("The frame is decorated")) {
				JFXUtils.runLater(() -> showAlert(getSpacesInPathMessage()));
				System.exit(-1);
			}

			throw e;
		}

		// For modal dialogs...
		Thread thread = new Thread() {
			public void run() {
				window.setVisible(true);
			}
		};

		thread.start();

		final Timeline tl = new Timeline();
		final Float factor = new Float(40.0 / millis);

		for (TimelineCallback tc : callbacks) {
			tl.addCallback(tc);
		}

		tl.addCallback(new TimelineCallbackAdapter() {
			float amount = AWTUtilities.getWindowOpacity(window);

			@Override
			public void onTimelinePulse(float arg0, float arg1) {
				amount += factor;
				if (amount <= 1.0) {
					AWTUtilities.setWindowOpacity(window, amount);
				} else {
					AWTUtilities.setWindowOpacity(window, 1.0f);
					tl.end();
				}
			}
		});

		tl.playLoop(RepeatBehavior.LOOP);
	}

	private static String getSpacesInPathMessage() {
		StringBuilder builder = new StringBuilder();

		builder.append("It appears that EntrainerFX has been installed\n");
		builder.append("in a path which has spaces in the directory name(s).\n");
		builder.append("There is a bug in the latest Java 7 releases which\n");
		builder.append("prevents EntrainerFX from functioning normally if\n");
		builder.append("there are spaces in the directory name(s).\n\n");
		builder.append("Please copy the EntrainerFX directory to a directory\n");
		builder.append("structure without spaces in the directory names ie.\n\n");
		builder.append("on Windows: C:\\my\\new\\path\\to\\EntrainerFX\n");
		builder.append("on Unix: /home/user/EntrainerFX");

		return builder.toString();
	}

	/**
	 * Fade out.
	 *
	 * @param window
	 *          the window
	 * @param millis
	 *          the millis
	 * @param callbacks
	 *          the callbacks
	 */
	public static void fadeOut(final Window window, int millis, TimelineCallback... callbacks) {
		final Timeline tl = new Timeline();
		final Float factor = new Float(40.0 / millis);

		for (TimelineCallback tc : callbacks) {
			tl.addCallback(tc);
		}

		tl.addCallback(new TimelineCallbackAdapter() {
			float amount = AWTUtilities.getWindowOpacity(window);

			@Override
			public void onTimelinePulse(float arg0, float arg1) {
				amount -= factor;
				if (amount >= 0.0) {
					AWTUtilities.setWindowOpacity(window, amount);
				} else {
					AWTUtilities.setWindowOpacity(window, 0.0f);
					tl.end();
				}
			}
		});

		tl.playLoop(RepeatBehavior.LOOP);
	}

	/**
	 * Fade out and dispose.
	 *
	 * @param window
	 *          the window
	 * @param millis
	 *          the millis
	 */
	public static void fadeOutAndDispose(final Window window, int millis) {
		TimelineCallbackAdapter tca = new TimelineCallbackAdapter() {

			public void onTimelineStateChanged(TimelineState oldState, TimelineState newState, float durationFraction,
					float timelinePosition) {
				if (TimelineState.DONE == newState || TimelineState.CANCELLED == newState) {
					window.dispose();
				}
			}
		};

		fadeOut(window, millis, tca);
	}

	/**
	 * Adds the fade out invisible listener.
	 *
	 * @param window
	 *          the window
	 * @param millis
	 *          the millis
	 */
	public static void addFadeOutInvisibleListener(final Window window, final int millis) {
		WindowAdapter wa = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				fadeOutVisibleFalse(window, millis);
				while (window.isVisible())
					Utils.snooze(10);
			}
		};

		window.addWindowListener(wa);
	}

	/**
	 * Fade out visible false.
	 *
	 * @param window
	 *          the window
	 * @param millis
	 *          the millis
	 */
	public static void fadeOutVisibleFalse(final Window window, final int millis) {
		TimelineCallbackAdapter tca = new TimelineCallbackAdapter() {

			public void onTimelineStateChanged(TimelineState oldState, TimelineState newState, float durationFraction,
					float timelinePosition) {
				if (TimelineState.DONE == newState || TimelineState.CANCELLED == newState) {
					window.setVisible(false);
				}
			}
		};

		fadeOut(window, millis, tca);
	}

}
