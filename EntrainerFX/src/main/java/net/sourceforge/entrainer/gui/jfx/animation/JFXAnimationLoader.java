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
package net.sourceforge.entrainer.gui.jfx.animation;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import net.sourceforge.entrainer.util.EntrainerRegister;
import net.sourceforge.entrainer.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class JFXAnimationLoader.
 */
public class JFXAnimationLoader {
  private static final Logger log = Logger.getLogger(JFXAnimationLoader.class);

  private List<JFXEntrainerAnimation> animations = Collections.synchronizedList(new ArrayList<JFXEntrainerAnimation>());
  private List<Path> jarFiles = Collections.synchronizedList(new ArrayList<Path>());
  private Timer timer = new Timer("Animation Watch Timer", true);

  /**
   * Returns the list of {@link JFXEntrainerAnimation} implementations that have
   * been loaded on startup.
   *
   * @return the entrainer animations
   */
  public List<JFXEntrainerAnimation> getEntrainerAnimations() {
    return animations;
  }

  /**
   * Checks if is empty.
   *
   * @return true, if is empty
   */
  public boolean isEmpty() {
    return animations.isEmpty();
  }

  /**
   * Load all animations.
   */
  void loadAllAnimations() {
    log.info("Loading animations");

    Optional<File> animDir = Utils.getAnimationDir();

    timer.schedule(getTimerTask(Paths.get(animDir.get().toURI())), 0);

    List<Path> jarPaths = EntrainerRegister.getJarFilesInDirectory(animDir.get().getAbsolutePath());

    jarPaths = getNewJarPaths(jarPaths);

    if (jarPaths.isEmpty()) {
      log.info("No new animations to load");
      return;
    }

    List<URL> jarUrls = new ArrayList<>();

    try {
      for (Path path : jarPaths) {
        log.info("Loading " + path);
        jarUrls.add(path.toUri().toURL());
      }

      EntrainerRegister.loadClasses(jarUrls, JFXEntrainerAnimation.class, animations);
    } catch (Exception e) {
      log.error("Unexpected exception", e);
    }
  }

  private WatchService getWatchService() throws IOException {
    FileSystem system = FileSystems.getDefault();
    return system.newWatchService();
  }

  private TimerTask getTimerTask(final Path directory) {
    TimerTask task = new TimerTask() {

      @Override
      public void run() {
        try {
          WatchKey key = directory.register(getWatchService(),
              StandardWatchEventKinds.ENTRY_CREATE,
              StandardWatchEventKinds.ENTRY_DELETE,
              StandardWatchEventKinds.ENTRY_MODIFY);

          List<WatchEvent<?>> events = key.pollEvents();
          while (events.isEmpty()) {
            log.debug("No changes to animation directory");
            Utils.snooze(5000);
            events = key.pollEvents();
          }

          boolean shouldLoad = false;
          for (WatchEvent<?> event : events) {
            if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())
                || StandardWatchEventKinds.ENTRY_MODIFY.equals(event.kind())) {
              shouldLoad = true;
            }
            if (StandardWatchEventKinds.ENTRY_DELETE.equals(event.kind())) {
              Path jarFile = (Path) event.context();
              fireAnimationsRemoved(jarFile);
            }
          }

          if (shouldLoad) loadAllAnimations();
        } catch (Throwable e) {
          log.error("Unexpected watch problem: ", e);
        }
      }
    };

    return task;
  }

  private void fireAnimationsRemoved(Path jarFile) {
    // TODO Auto-generated method stub
  }

  @SuppressWarnings("unchecked")
  private List<Path> getNewJarPaths(List<Path> jarPaths) {
    if (jarPaths.isEmpty()) return Collections.EMPTY_LIST;
    List<Path> paths = new ArrayList<Path>();

    if (jarFiles.isEmpty()) {
      jarFiles.addAll(jarPaths);
      paths.addAll(jarPaths);
    } else {
      for (Path path : jarPaths) {
        if (!jarFiles.contains(path)) {
          jarFiles.add(path);

          paths.add(path);
        }
      }
    }

    return paths;
  }

}
