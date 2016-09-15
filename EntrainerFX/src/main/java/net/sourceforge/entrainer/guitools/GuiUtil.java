/*
 *      ______      __             _                 _______  __
 *     / ____/___  / /__________ _(_)___  ___  _____/ ____/ |/ /
 *    / __/ / __ \/ __/ ___/ __ `/ / __ \/ _ \/ ___/ /_   |   / 
 *   / /___/ / / / /_/ /  / /_/ / / / / /  __/ /  / __/  /   |  
 *  /_____/_/ /_/\__/_/   \__,_/_/_/ /_/\___/_/  /_/    /_/|_|  
 *                                                          
 *
 * Copyright (C) 2008 - 2016 Burton Alexander
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

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import javax.swing.ImageIcon;

import net.sourceforge.entrainer.gui.EntrainerFX;
import net.sourceforge.entrainer.gui.jfx.JFXUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * Convenience gui methods class.
 * 
 * @author burton
 */
public class GuiUtil {

  private static Logger logger = LoggerFactory.getLogger(GuiUtil.class);

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
   * Handle problem.
   *
   * @param e
   *          the e
   * @param useLatch
   *          the use latch
   */
  public static void handleProblem(Throwable e, boolean useLatch) {
    if (e instanceof UnsupportedOperationException) {
      logger.debug("Expected exception (if one-off...)", e);
      return;
    }

    e.printStackTrace();

    final String msg = e.getMessage();

    logger.error(msg, e);

    CountDownLatch cdl = useLatch ? new CountDownLatch(1) : null;
    JFXUtils.runLater(() -> showAlert(msg, cdl));

    if (useLatch) try {
      cdl.await();
    } catch (InterruptedException e1) {
      logger.error("Unexpected exception", e1);
    }
  }

  private static void showAlert(String msg, CountDownLatch cdl) {
    if (msg == null || msg.equals("null") || msg.isEmpty()) msg = "An unknown error has occurred";

    Alert alert = new Alert(AlertType.ERROR, msg, ButtonType.OK);
    alert.setHeaderText("Please see the ~/EntrainerFX-Settings/entrainer.log file for details");
    alert.setTitle("Unexpected Exception");
    EntrainerFX efx = EntrainerFX.getInstance();
    alert.initOwner(efx == null ? null : efx.getStage());
    if (cdl == null) {
      alert.show();
    } else {
      alert.showAndWait();
      cdl.countDown();
    }
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

}
