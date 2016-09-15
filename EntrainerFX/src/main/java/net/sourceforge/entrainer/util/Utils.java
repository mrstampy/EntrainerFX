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
package net.sourceforge.entrainer.util;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.entrainer.EntrainerResources;
import net.sourceforge.entrainer.guitools.GuiUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class Utils.
 */
public class Utils implements EntrainerResources {
  private static Logger log = LoggerFactory.getLogger(Utils.class);

  /**
   * Snooze.
   *
   * @param time
   *          the time
   */
  public static void snooze(long time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      log.warn("Woke up unexpectedly from a snooze", e);
    }
  }

  /**
   * Snooze.
   *
   * @param time
   *          the time
   * @param nanos
   *          the nanos
   */
  public static void snooze(long time, int nanos) {
    try {
      Thread.sleep(time, nanos);
    } catch (InterruptedException e) {
      log.debug("Woke up unexpectedly from a snooze", e);
    }
  }

  /**
   * List system properties.
   */
  public static void listSystemProperties() {
    Properties props = System.getProperties();

    Set<Entry<Object, Object>> set = props.entrySet();
    for (Entry<Object, Object> entry : set) {
      System.out.println(entry.getKey() + "=" + entry.getValue());
    }
  }

  /**
   * Open browser.
   *
   * @param address
   *          the address
   */
  public static void openBrowser(String address) {
    address = address.replace("\\", "/");
    address = address.replace(" ", "%20");
    try {
      openBrowser(URI.create(address));
    } catch (Throwable e) {
      GuiUtil.handleProblem(e);
    }
  }

  /**
   * Open browser.
   *
   * @param uri
   *          the uri
   */
  public static void openBrowser(URI uri) {
    try {
      Desktop.getDesktop().browse(uri);
    } catch (Throwable e) {
      GuiUtil.handleProblem(e);
    }
  }

  /**
   * Gets the settings file.
   *
   * @return the settings file
   */
  public static Optional<File> getSettingsFile() {
    return getFile(EFX_SETTINGS_DIR + "settings.xml", EFX_USER_HOME_SETTINGS_DIR + "settings.xml");
  }

  /**
   * Gets the license file.
   *
   * @return the license file
   */
  public static Optional<File> getLicenseFile() {
    return getFile(EFX_SETTINGS_DIR + "LICENSE.txt", EFX_USER_HOME_SETTINGS_DIR + "LICENSE.txt");
  }

  /**
   * Gets the entrainer program dir.
   *
   * @return the entrainer program dir
   */
  public static Optional<File> getEntrainerProgramDir() {
    return getFile(EFX_PROGRAM_DIR, EFX_USER_HOME_PROGRAM_DIR);
  }

  /**
   * Gets the recording dir.
   *
   * @return the recording dir
   */
  public static Optional<File> getRecordingDir() {
    return getFile(EFX_RECORDING_DIR, EFX_USER_HOME_RECORDING_DIR);
  }

  /**
   * Open local documentation.
   *
   * @param htmlPage
   *          the html page
   */
  public static void openLocalDocumentation(String htmlPage) {
    Optional<File> index = getLocalDocPage(htmlPage);

    if (index.isPresent() && index.get().exists()) openBrowser(index.get().toURI());
  }

  private static Optional<File> getLocalDocPage(String htmlPage) {
    return getFile(EFX_DOC_DIR + "/" + htmlPage, EFX_USER_HOME_DOC_DIR + "/" + htmlPage);
  }

  /**
   * Gets the animation dir.
   *
   * @return the animation dir
   */
  public static Optional<File> getAnimationDir() {
    return getFile(EFX_ANIMATION_DIR, EFX_USER_HOME_ANIMATION_DIR);
  }

  /**
   * Gets the esp dir.
   *
   * @return the esp dir
   */
  public static Optional<File> getEspDir() {
    return getFile(EFX_ESP_DIR, EFX_USER_HOME_ESP_DIR);
  }

  /**
   * Gets the css dir.
   *
   * @return the css dir
   */
  public static Optional<File> getCssDir() {
    return getFile(EFX_CSS_DIR, EFX_USER_HOME_CSS_DIR);
  }

  private static Optional<File> getFile(String override, String userHome) {
    File file = new File(override);
    if (!file.exists()) file = new File(userHome);

    return Optional.of(file);
  }

}
