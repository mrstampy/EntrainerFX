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
package net.sourceforge.entrainer.sound.jsyn;

import static net.sourceforge.entrainer.sound.jsyn.JSynChannels.LINEOUT_LEFT;
import static net.sourceforge.entrainer.sound.jsyn.JSynChannels.LINEOUT_RIGHT;
import static net.sourceforge.entrainer.sound.jsyn.JSynChannels.RECORDING_LEFT;
import static net.sourceforge.entrainer.sound.jsyn.JSynChannels.RECORDING_RIGHT;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.sound.AbstractSoundControl;
import net.sourceforge.entrainer.util.Utils;

import com.softsynth.jsyn.LineOut;
import com.softsynth.jsyn.PanUnit;
import com.softsynth.jsyn.PinkNoise;
import com.softsynth.jsyn.SineOscillator;
import com.softsynth.jsyn.Synth;
import com.softsynth.jsyn.SynthMixer;
import com.softsynth.jsyn.SynthOscillator;
import com.softsynth.jsyn.SynthOutput;
import com.softsynth.jsyn.util.StreamRecorder;
import com.softsynth.jsyn.util.WAVFileWriter;

// TODO: Auto-generated Javadoc
/**
 * The JSyn sound control class.
 * 
 * @author burton
 */
public class JSynSoundControl extends AbstractSoundControl {
	
	/** The Constant J_SYN_SOUND_CONTROL_CLASS. */
	public static final String J_SYN_SOUND_CONTROL_CLASS = "net.sourceforge.entrainer.sound.jsyn.JSynSoundControl";

	private static int FRAMES_PER_BUFFER = 8 * 1024;
	private static int NUM_BUFFERS = 4;
	private static int NUM_REC_CHANNELS = 2;

	private static int IS_LEFT = 0;
	private static int IS_RIGHT = 1;
	private static int IS_BOTH = 2;

	private SynthOscillator leftChannel;
	private SynthOscillator rightChannel;
	private LineOut out;
	private PinkNoise pinkNoise;
	private PanUnit pinkPanLeft;
	private PanUnit pinkPanRight;

	private SynthMixer mixer;

	private StreamRecorder recorder;
	private BufferedOutputStream recordingOut;
	private RandomAccessFile raf;
	private WAVFileWriter wfw;

	private List<JSynInterval> intervals = new CopyOnWriteArrayList<JSynInterval>();

	/**
	 * Instantiates a new j syn sound control.
	 */
	public JSynSoundControl() {
		super();
		init();
	}

	/* (non-Javadoc)
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

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundControl#teardown()
	 */
	public void teardown() {
		EntrainerMediator.getInstance().removeReceiver(this);
		intervals.clear();
		Synth.stopEngine();
		leftChannel = null;
		rightChannel = null;
		out = null;
		pinkNoise = null;
		pinkPanLeft = null;
		pinkPanRight = null;
		mixer = null;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.jsyn.SoundControl#start()
	 */
	public void start() {
		if (!isPlaying()) {
			setPlaying(true);
			setMixerGains();
			startPlaying();

			if (isRecording()) {
				try {
					initRecording();
					connectRecording();
					startRecording();
				} catch (Exception e) {
					GuiUtil.handleProblem(e);
				}
			}
		}
	}

	private void startPlaying() {
		out.start();
		rightChannel.start();
		leftChannel.start();

		pinkNoise.start();
		pinkPanLeft.start();
		pinkPanRight.start();

		mixer.start();
		startIntervals();
	}

	private void startIntervals() {
		for (JSynInterval interval : intervals) {
			interval.start();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.jsyn.SoundControl#stop()
	 */
	public void stop() {
		if (isPlaying()) {
			setPlaying(false);
			stopPlaying();

			if (isRecording()) {
				try {
					stopRecording();
					finalizeRecording();
					setRecord(false);
					setWavFile(null);
				} catch (Exception e) {
					GuiUtil.handleProblem(e);
				}
			}
		}
	}

	/* (non-Javadoc)
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

	/* (non-Javadoc)
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
		out.stop();
		rightChannel.stop();
		leftChannel.stop();

		pinkNoise.stop();
		pinkPanLeft.stop();
		pinkPanRight.stop();
		stopIntervals();
		mixer.stop();
	}

	private void stopIntervals() {
		for (JSynInterval interval : intervals) {
			interval.stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.jsyn.SoundControl#setLeftFrequency(double)
	 */
	public void setLeftFrequency(double d) {
		leftChannel.frequency.set(d);
		setIntervalFrequencies(d);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.jsyn.SoundControl#getLeftFrequency()
	 */
	public double getLeftFrequency() {
		return leftChannel.frequency.get();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.jsyn.SoundControl#setRightFrequency(double)
	 */
	public void setRightFrequency(double d) {
		rightChannel.frequency.set(d);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.jsyn.SoundControl#getRightFrequency()
	 */
	public double getRightFrequency() {
		return rightChannel.frequency.get();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.jsyn.SoundControl#setPinkAmplitude(double)
	 */
	public void setPinkNoiseAmplitude(double d) {
		pinkNoise.amplitude.set(d);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.jsyn.SoundControl#getPinkAmplitude()
	 */
	public double getPinkNoiseAmplitude() {
		return pinkNoise.amplitude.get();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.jsyn.SoundControl#setLeftAmplitude(double)
	 */
	public void setLeftAmplitude(double d) {
		leftChannel.amplitude.set(d);
		setIntervalAmplitudes(d);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.jsyn.SoundControl#getLeftAmplitude()
	 */
	public double getLeftAmplitude() {
		return leftChannel.amplitude.get();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.jsyn.SoundControl#setRightAmplitude(double)
	 */
	public void setRightAmplitude(double d) {
		rightChannel.amplitude.set(d);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.jsyn.SoundControl#getRightAmplitude()
	 */
	public double getRightAmplitude() {
		return rightChannel.amplitude.get();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.jsyn.SoundControl#setPinkPanLeft(double)
	 */
	public void setPinkPanLeftAmplitude(double d) {
		pinkPanLeft.pan.set(d);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.jsyn.SoundControl#setPinkPanRight(double)
	 */
	public void setPinkPanRightAmplitude(double d) {
		pinkPanRight.pan.set(d);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.jsyn.SoundControl#getPinkPanLeft()
	 */
	public double getPinkPanLeftAmplitude() {
		return pinkPanLeft.pan.get();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sourceforge.jsyn.SoundControl#getPinkPanRight()
	 */
	public double getPinkPanRightAmplitude() {
		return pinkPanRight.pan.get();
	}

	private void init() {
		Synth.startEngine(0);
		leftChannel = new SineOscillator();
		rightChannel = new SineOscillator();
		out = new LineOut();
		pinkNoise = new PinkNoise();
		pinkPanLeft = new PanUnit();
		pinkPanRight = new PanUnit();

		mixer = new SynthMixer(20, 4);

		connectPinkNoisePan(pinkPanLeft, pinkNoise);
		connectPinkNoisePan(pinkPanRight, pinkNoise);

		connectMixer(leftChannel.output, IS_LEFT, 0);
		connectMixer(rightChannel.output, IS_RIGHT, 1);
		connectMixer(pinkPanLeft.output, IS_LEFT, 2);
		connectMixer(pinkPanRight.output, IS_RIGHT, 3);

		mixer.connectOutput(LINEOUT_LEFT.getChannel(), out.input, JSynUtil.LEFT_CHANNEL);
		mixer.connectOutput(LINEOUT_RIGHT.getChannel(), out.input, JSynUtil.RIGHT_CHANNEL);
	}

	private void setMixerGains() {
		int numChannels = 4 + intervals.size();
		double d = 1.0 / numChannels;

		setGain(IS_LEFT, 0, d);
		setGain(IS_RIGHT, 1, d);
		setGain(IS_LEFT, 2, d);
		setGain(IS_RIGHT, 3, d);

		int i = 4;
		ListIterator<JSynInterval> li = intervals.listIterator();
		while (li.hasNext()) {
			JSynInterval interval = li.next();
			connectMixer(interval.getLeftChannel().output, IS_BOTH, i);
			setGain(IS_BOTH, i, d);
			i++;
		}
	}

	private void connectMixer(SynthOutput output, int gain, int idx) {
		mixer.connectInput(idx, output, 0);
	}

	private void setGain(int gain, int idx, double gainValue) {
		double leftGain = gain == IS_BOTH || gain == IS_LEFT ? gainValue : 0;
		double rightGain = gain == IS_BOTH || gain == IS_RIGHT ? gainValue : 0;

		mixer.setGain(idx, LINEOUT_LEFT.getChannel(), leftGain);
		mixer.setGain(idx, LINEOUT_RIGHT.getChannel(), rightGain);

		mixer.setGain(idx, RECORDING_LEFT.getChannel(), leftGain);
		mixer.setGain(idx, RECORDING_RIGHT.getChannel(), rightGain);
	}

	private void connectPinkNoisePan(PanUnit pan, PinkNoise pink) {
		JSynUtil.connect(pan.input, pink.output);
		JSynUtil.connect(pan.pan, pink.amplitude);
	}

	private void initRecording() throws IOException {
		getWavFile().delete();
		raf = new RandomAccessFile(getWavFile(), "rw");
		wfw = new WAVFileWriter(raf);
		recordingOut = new BufferedOutputStream(wfw);

		recorder = new StreamRecorder(recordingOut, FRAMES_PER_BUFFER, NUM_BUFFERS, NUM_REC_CHANNELS);
	}

	private void connectRecording() throws IOException {
		mixer.connectOutput(RECORDING_LEFT.getChannel(), recorder.input, JSynUtil.LEFT_CHANNEL);
		mixer.connectOutput(RECORDING_RIGHT.getChannel(), recorder.input, JSynUtil.RIGHT_CHANNEL);
		wfw.writeHeader(NUM_REC_CHANNELS, 44100);
	}

	private void startRecording() {
		int time = Synth.getTickCount() + 20;
		recorder.start(time);
	}

	private void stopRecording() {
		int time = Synth.getTickCount() + 20;
		recorder.stop(time);
	}

	private void finalizeRecording() throws IOException {
		recordingOut.flush();
		wfw.fixSizes();
		recordingOut.close();
		raf.close();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundControl#addIntervalControl(int, int)
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

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundControl#removeIntervalControl(int, int)
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
	 * @param num the num
	 * @param denom the denom
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

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundControl#addIntervalControl(java.lang.String)
	 */
	public void addIntervalControl(String displayString) {
		addIntervalControl(JSynInterval.getNumerator(displayString), JSynInterval.getDenominator(displayString));
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundControl#removeIntervalControl(java.lang.String)
	 */
	public void removeIntervalControl(String displayString) {
		removeIntervalControl(JSynInterval.getNumerator(displayString), JSynInterval.getDenominator(displayString));
	}

	/* (non-Javadoc)
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

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundControl#exit()
	 */
	public void exit() {
		Synth.stop();
	}

}
