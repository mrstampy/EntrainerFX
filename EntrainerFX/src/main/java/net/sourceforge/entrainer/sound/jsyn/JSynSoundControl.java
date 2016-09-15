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

import static net.sourceforge.entrainer.sound.jsyn.JSynChannels.LINEOUT_LEFT;
import static net.sourceforge.entrainer.sound.jsyn.JSynChannels.LINEOUT_RIGHT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.Pan;
import com.jsyn.unitgen.PinkNoise;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.UnitOscillator;
import com.jsyn.util.WaveRecorder;

import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.sound.AbstractSoundControl;
import net.sourceforge.entrainer.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * The JSyn sound control class.
 * 
 * @author burton
 */
public class JSynSoundControl extends AbstractSoundControl {

  /** The Constant J_SYN_SOUND_CONTROL_CLASS. */
  public static final String J_SYN_SOUND_CONTROL_CLASS = "net.sourceforge.entrainer.sound.jsyn.JSynSoundControl";

  private static int IS_LEFT = 0;
  private static int IS_RIGHT = 1;
  private static int IS_BOTH = 2;

  private UnitOscillator leftChannel;
  private UnitOscillator rightChannel;
  private LineOut out;
  private PinkNoise pinkNoise;
  private Pan pinkPanLeft;
  private Pan pinkPanRight;

  private Synthesizer synth;

  private WaveRecorder recorder;

  private List<JSynInterval> intervals = new CopyOnWriteArrayList<JSynInterval>();

  /**
   * Instantiates a new j syn sound control.
   */
  public JSynSoundControl() {
    super();
    init();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.sound.AbstractSoundControl#initMediator()
   */
  @Override
  protected void initMediator() {
    super.initMediator();

    EntrainerMediator.getInstance().addFirstReceiver(new ReceiverAdapter(this) {

      @Override
      protected synchronized void processReceiverChangeEvent(ReceiverChangeEvent e) {
        synchronized (this) {
          switch (e.getParm()) {
          case INTERVAL_ADD:
            addIntervalFromEvent(e.getStringValue());
            break;
          case INTERVAL_REMOVE:
            removeIntervalFromEvent(e.getStringValue());
            break;
          default:
            break;
          }
        }
      }

    });
  }

  private void addIntervalFromEvent(final String interval) {
    Thread thread = new Thread() {
      public void run() {
        Utils.snooze(100);
        addIntervalControl(interval);
      }
    };

    thread.start();
  }

  private void removeIntervalFromEvent(final String interval) {
    Thread thread = new Thread() {
      public void run() {
        Utils.snooze(100);
        removeIntervalControl(interval);
      }
    };

    thread.start();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.sound.SoundControl#teardown()
   */
  public void teardown() {
    EntrainerMediator.getInstance().removeReceiver(this);
    intervals.clear();
    leftChannel = null;
    rightChannel = null;
    out = null;
    pinkNoise = null;
    pinkPanLeft = null;
    pinkPanRight = null;
    synth = null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jsyn.SoundControl#start()
   */
  public void start() {
    if (isPlaying()) return;

    setPlaying(true);
    setMixerGains();
    startPlaying();
    beginRecording();
  }

  private void beginRecording() {
    if (!isRecording()) return;

    try {
      initRecording();
      startRecording();
    } catch (Exception e) {
      GuiUtil.handleProblem(e);
    }
  }

  private void startPlaying() {
    synth.start();
    out.start();
    startIntervals();
  }

  private void startIntervals() {
    for (JSynInterval interval : intervals) {
      interval.start();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jsyn.SoundControl#stop()
   */
  public void stop() {
    if (!isPlaying()) return;

    setPlaying(false);
    stopPlaying();
    endRecording();
  }

  private void endRecording() {
    if (!isRecording()) return;

    try {
      stopRecording();
      setRecord(false);
      setWavFile(null);
    } catch (Exception e) {
      GuiUtil.handleProblem(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.sound.SoundSettings#pause()
   */
  public void pause() {
    if (isPlaying()) {
      setPaused(true);
      setPlaying(false);
      stopPlaying();

      if (isRecording()) {
        stopRecording();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.sound.SoundSettings#resume()
   */
  public void resume() {
    if (isPaused()) {
      setPaused(false);
      setPlaying(true);
      startPlaying();

      if (isRecording()) {
        startRecording();
      }
    }
  }

  private boolean isRecording() {
    return isRecord() && getWavFile() != null;
  }

  private void stopPlaying() {
    stopIntervals();
    synth.stop();
    out.stop();
  }

  private void stopIntervals() {
    for (JSynInterval interval : intervals) {
      interval.stop();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jsyn.SoundControl#setLeftFrequency(double)
   */
  public void setLeftFrequency(double d) {
    leftChannel.frequency.set(d);
    setIntervalFrequencies(d);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jsyn.SoundControl#getLeftFrequency()
   */
  public double getLeftFrequency() {
    return leftChannel.frequency.get();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jsyn.SoundControl#setRightFrequency(double)
   */
  public void setRightFrequency(double d) {
    rightChannel.frequency.set(d);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jsyn.SoundControl#getRightFrequency()
   */
  public double getRightFrequency() {
    return rightChannel.frequency.get();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jsyn.SoundControl#setPinkAmplitude(double)
   */
  public void setPinkNoiseAmplitude(double d) {
    pinkNoise.amplitude.set(d);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jsyn.SoundControl#getPinkAmplitude()
   */
  public double getPinkNoiseAmplitude() {
    return pinkNoise.amplitude.get();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jsyn.SoundControl#setLeftAmplitude(double)
   */
  public void setLeftAmplitude(double d) {
    leftChannel.amplitude.set(d);
    setIntervalAmplitudes(d);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jsyn.SoundControl#getLeftAmplitude()
   */
  public double getLeftAmplitude() {
    return leftChannel.amplitude.get();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jsyn.SoundControl#setRightAmplitude(double)
   */
  public void setRightAmplitude(double d) {
    rightChannel.amplitude.set(d);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jsyn.SoundControl#getRightAmplitude()
   */
  public double getRightAmplitude() {
    return rightChannel.amplitude.get();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jsyn.SoundControl#setPinkPanLeft(double)
   */
  public void setPinkPanLeftAmplitude(double d) {
    pinkPanLeft.pan.set(d);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jsyn.SoundControl#setPinkPanRight(double)
   */
  public void setPinkPanRightAmplitude(double d) {
    pinkPanRight.pan.set(d);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jsyn.SoundControl#getPinkPanLeft()
   */
  public double getPinkPanLeftAmplitude() {
    return pinkPanLeft.pan.get();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jsyn.SoundControl#getPinkPanRight()
   */
  public double getPinkPanRightAmplitude() {
    return pinkPanRight.pan.get();
  }

  private void init() {
    synth = JSyn.createSynthesizer();
    
    initSineOscillators();
    initPinkNoise();
    
    connect(out.input);
  }

  private void initPinkNoise() {
    synth.add(pinkNoise = new PinkNoise());
    synth.add(pinkPanLeft = new Pan());
    synth.add(pinkPanRight = new Pan());
    
    pinkPanRight.input.connect(pinkNoise.output);
    pinkPanLeft.input.connect(pinkNoise.output);
  }

  private void initSineOscillators() {
    synth.add(leftChannel = new SineOscillator());
    synth.add(rightChannel = new SineOscillator());
    synth.add(out = new LineOut());
  }

  private void setMixerGains() {
    int numChannels = 4 + intervals.size();
    double d = 1.0 / numChannels;
    
    pinkNoise.amplitude.set(d);
    leftChannel.amplitude.set(d);
    rightChannel.amplitude.set(d);

    int i = 4;
    ListIterator<JSynInterval> li = intervals.listIterator();
    while (li.hasNext()) {
      JSynInterval interval = li.next();
      connectMixer(interval.getLeftChannel(), IS_BOTH, i);
      setGain(IS_BOTH, i, d);
      i++;
    }
  }

  private void connectMixer(UnitOscillator output, int gain, int idx) {
    synth.add(output);
    
    output.output.connect(0, out.input, IS_LEFT);
    output.output.connect(0, out.input, IS_RIGHT);
  }

  private void setGain(int gain, int idx, double gainValue) {
    double leftGain = gain == IS_BOTH || gain == IS_LEFT ? gainValue : 0;
    double rightGain = gain == IS_BOTH || gain == IS_RIGHT ? gainValue : 0;

    out.input.set(LINEOUT_LEFT.getChannel(), leftGain);
    out.input.set(LINEOUT_RIGHT.getChannel(), rightGain);
  }

  private void initRecording() throws IOException {
    getWavFile().delete();
    recorder = new WaveRecorder(synth, getWavFile(), 2);
    
    connect(recorder.getInput());
    
    intervals.stream().forEach(ji -> attachInterval(ji, recorder.getInput()));
  }
  
  private void attachInterval(JSynInterval ji, UnitInputPort input) {
    ji.getLeftChannel().output.connect(0, input, IS_LEFT);
    ji.getLeftChannel().output.connect(0, input, IS_RIGHT);
  }

  private void startRecording() {
    recorder.start();
  }

  private void stopRecording() {
    recorder.stop();
    
    disconnect(recorder.getInput());
    
    intervals.stream().forEach(ji -> detachInterval(ji, recorder.getInput()));
  }
  
  private void detachInterval(JSynInterval ji, UnitInputPort input) {
    ji.getLeftChannel().output.disconnect(input);
  }

  private void connect(UnitInputPort input) {
    leftChannel.output.connect(0, input, IS_LEFT);
    rightChannel.output.connect(0, input, IS_RIGHT);
    pinkPanRight.output.connect(0, input, IS_RIGHT);
    pinkPanLeft.output.connect(0, input, IS_LEFT);
  }
  
  private void disconnect(UnitInputPort input) {
    leftChannel.output.disconnect(input);
    rightChannel.output.disconnect(input);
    pinkPanRight.output.disconnect(input);
    pinkPanLeft.output.disconnect(input);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.sound.SoundControl#addIntervalControl(int,
   * int)
   */
  public synchronized void addIntervalControl(int intervalNumerator, int intervalDenominator) {
    if (!containsInterval(intervalNumerator, intervalDenominator)) {
      JSynInterval interval = new JSynInterval(intervalNumerator, intervalDenominator);
      initInterval(interval);
      intervals.add(interval);
      setMixerGains();
      if (isPlaying()) {
        interval.start();
      }
    }
  }

  private void initInterval(JSynInterval interval) {
    interval.setLeftFrequency(getLeftFrequency());
    interval.setLeftAmplitude(getLeftAmplitude());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.entrainer.sound.SoundControl#removeIntervalControl(int,
   * int)
   */
  public void removeIntervalControl(int intervalNumerator, int intervalDenominator) {
    JSynInterval interval = getInterval(intervalNumerator, intervalDenominator);
    if (interval != null) {
      interval.stop();
      intervals.remove(interval);
      setMixerGains();
    }
  }

  /**
   * Gets the interval.
   *
   * @param num
   *          the num
   * @param denom
   *          the denom
   * @return the interval
   */
  public JSynInterval getInterval(int num, int denom) {
    for (JSynInterval interval : intervals) {
      if (interval.isInterval(num, denom)) {
        return interval;
      }
    }

    return null;
  }

  private boolean containsInterval(int num, int denom) {
    return getInterval(num, denom) != null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.entrainer.sound.SoundControl#addIntervalControl(java.lang
   * .String)
   */
  public void addIntervalControl(String displayString) {
    addIntervalControl(JSynInterval.getNumerator(displayString), JSynInterval.getDenominator(displayString));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.entrainer.sound.SoundControl#removeIntervalControl(java
   * .lang.String)
   */
  public void removeIntervalControl(String displayString) {
    removeIntervalControl(JSynInterval.getNumerator(displayString), JSynInterval.getDenominator(displayString));
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.sound.SoundControl#getIntervals()
   */
  public List<String> getIntervals() {
    List<String> displayStrings = new ArrayList<String>();
    for (JSynInterval interval : intervals) {
      displayStrings.add(interval.getDisplayString());
    }

    return displayStrings;
  }

  private void setIntervalFrequencies(double d) {
    for (JSynInterval interval : intervals) {
      interval.setLeftFrequency(d);
    }
  }

  private void setIntervalAmplitudes(double d) {
    for (JSynInterval interval : intervals) {
      interval.setLeftAmplitude(d);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.sound.SoundControl#exit()
   */
  public void exit() {
    stop();
    teardown();
  }

}
