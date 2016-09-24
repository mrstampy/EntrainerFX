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
package net.sourceforge.entrainer.sound.jsyn;

import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.UnitOscillator;

import net.sourceforge.entrainer.sound.AbstractSoundInterval;

// TODO: Auto-generated Javadoc
/**
 * Implementation of a sound interval for the JSyn libraries.
 * 
 * @author burton
 *
 */
public class JSynInterval extends AbstractSoundInterval {
  private UnitOscillator leftChannel;

  /**
   * Instantiates a new j syn interval.
   *
   * @param numerator
   *          the numerator
   * @param denominator
   *          the denominator
   */
  public JSynInterval(int numerator, int denominator) {
    super(numerator, denominator);
    init();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.sound.SoundSettings#getLeftAmplitude()
   */
  public double getLeftAmplitude() {
    return leftChannel.amplitude.get();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.sound.SoundSettings#getLeftFrequency()
   */
  public double getLeftFrequency() {
    return leftChannel.frequency.get();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.sound.SoundSettings#getPinkNoiseAmplitude()
   */
  public double getPinkNoiseAmplitude() {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.entrainer.sound.SoundSettings#getPinkPanLeftAmplitude()
   */
  public double getPinkPanLeftAmplitude() {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.entrainer.sound.SoundSettings#getPinkPanRightAmplitude()
   */
  public double getPinkPanRightAmplitude() {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.sound.SoundSettings#getRightAmplitude()
   */
  public double getRightAmplitude() {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.sound.SoundSettings#getRightFrequency()
   */
  public double getRightFrequency() {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.sound.SoundSettings#setLeftAmplitude(double)
   */
  public void setLeftAmplitude(double d) {
    leftChannel.amplitude.set(d);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.sound.SoundSettings#setLeftFrequency(double)
   */
  public void setLeftFrequency(double d) {
    double interval = d + (d * getInterval());
    leftChannel.frequency.set(interval);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.entrainer.sound.SoundSettings#setPinkNoiseAmplitude(double)
   */
  public void setPinkNoiseAmplitude(double d) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.sound.SoundSettings#setPinkPanLeftAmplitude(
   * double )
   */
  public void setPinkPanLeftAmplitude(double d) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.entrainer.sound.SoundSettings#setPinkPanRightAmplitude(
   * double)
   */
  public void setPinkPanRightAmplitude(double d) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.entrainer.sound.SoundSettings#setRightAmplitude(double)
   */
  public void setRightAmplitude(double d) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.entrainer.sound.SoundSettings#setRightFrequency(double)
   */
  public void setRightFrequency(double d) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.sound.SoundSettings#start()
   */
  public void start() {
    leftChannel.start();
  }

  public void start(LineOut out) {
    leftChannel.output.connect(out.input);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.sound.SoundSettings#stop()
   */
  public void stop() {
    leftChannel.stop();
  }

  public void stop(LineOut out) {
    leftChannel.output.disconnect(out.input);
    leftChannel.amplitude.set(0);
    stop();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.sound.SoundInterval#getInterval()
   */
  public double getInterval() {
    return ((double) getIntervalNumerator()) / ((double) getIntervalDenominator());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.sound.SoundInterval#isInterval(int, int)
   */
  public boolean isInterval(int num, int denom) {
    return num == getIntervalNumerator() && denom == getIntervalDenominator();
  }

  private void init() {
    leftChannel = new SineOscillator();
  }

  /**
   * Gets the left channel.
   *
   * @return the left channel
   */
  UnitOscillator getLeftChannel() {
    return leftChannel;
  }

}
