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
package net.sourceforge.entrainer.sound;

import java.io.File;

import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;

// TODO: Auto-generated Javadoc
/**
 * Superclass for sound control classes, implementing common methods.
 * 
 * @author burton
 */
public abstract class AbstractSoundControl extends AbstractSoundSettings implements SoundControl {

	private File wavFile;
	private boolean isRecord;

	/**
	 * Instantiates a new abstract sound control.
	 */
	protected AbstractSoundControl() {
		super();
		initMediator();
	}

	/**
	 * Inits the mediator.
	 */
	protected void initMediator() {
		EntrainerMediator.getInstance().addFirstReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case START_ENTRAINMENT:
					if (e.getBooleanValue()) {
						start();
					} else {
						stop();
					}
					break;
				default:
					break;
				}
			}

		});
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundControl#getWavFile()
	 */
	public File getWavFile() {
		return wavFile;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundControl#setWavFile(java.io.File)
	 */
	public void setWavFile(File wavFile) {
		this.wavFile = wavFile;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundControl#isRecord()
	 */
	public boolean isRecord() {
		return isRecord;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.entrainer.sound.SoundControl#setRecord(boolean)
	 */
	public void setRecord(boolean isRecord) {
		this.isRecord = isRecord;
	}

}
