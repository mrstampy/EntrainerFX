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

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating EntrainerFXThread objects.
 */
public class EntrainerFXThreadFactory implements ThreadFactory {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final UncaughtExceptionHandler UEH = new UncaughtExceptionHandler() {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
      log.error("Unexpected exception in thread {}", t.getName(), e);
    }
  };

  private String name;

  private boolean daemon;

  private ThreadGroup threadGroup;

  /** The count. */
  protected AtomicInteger count = new AtomicInteger(0);

  /**
   * Instantiates a new entrainer fx thread factory.
   *
   * @param name
   *          the name
   */
  public EntrainerFXThreadFactory(String name) {
    this(name, true);
  }

  /**
   * Instantiates a new entrainer fx thread factory.
   *
   * @param name
   *          the name
   * @param daemon
   *          the daemon
   */
  public EntrainerFXThreadFactory(String name, boolean daemon) {
    this(name, daemon, new ThreadGroup(name));
  }

  /**
   * Instantiates a new entrainer fx thread factory.
   *
   * @param name
   *          the name
   * @param daemon
   *          the daemon
   * @param threadGroup
   *          the thread group
   */
  public EntrainerFXThreadFactory(String name, boolean daemon, ThreadGroup threadGroup) {
    this.name = name;
    this.daemon = daemon;
    this.threadGroup = threadGroup;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
   */
  @Override
  public Thread newThread(Runnable r) {
    Thread thread = new Thread(threadGroup, r, createThreadName());
    thread.setDaemon(isDaemon());
    thread.setUncaughtExceptionHandler(UEH);

    return thread;
  }

  /**
   * Creates a new EntrainerFXThread object.
   *
   * @return the string
   */
  protected String createThreadName() {
    int i = count.incrementAndGet();
    return getName() + (i == 1 ? "" : "-" + i);
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Checks if is daemon.
   *
   * @return true, if is daemon
   */
  public boolean isDaemon() {
    return daemon;
  }

  /**
   * Gets the thread group.
   *
   * @return the thread group
   */
  public ThreadGroup getThreadGroup() {
    return threadGroup;
  }

}
