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
package net.sourceforge.entrainer.gui.jfx;

import static net.sourceforge.entrainer.mediator.MediatorConstants.START_ENTRAINMENT;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;

// TODO: Auto-generated Javadoc
/**
 * The Class SoundControlPane.
 */
public class SoundControlPane extends HBox {

	/** The Constant CSS_ID. */
	public static final String CSS_ID = "sound-control-pane";
	private ButtonBase play;
	private ButtonBase stop;
	private ButtonBase record;
	private ButtonBase pause;

	private boolean playingEntrainerProgram;
	private boolean recordingEntrainerProgram;

	private Sender sender = new SenderAdapter();

	/**
	 * Instantiates a new sound control pane.
	 */
	public SoundControlPane() {
		super();
		initButtons();
	}

	private void fireReceiverChangeEvent(boolean value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	private void initButtons() {
		initReceiver();
		setId(CSS_ID);
		setAlignment(Pos.CENTER);
		EntrainerMediator.getInstance().addSender(sender);
		play = ControlButtonFactory.createButton("Play");
		stop = ControlButtonFactory.createButton("Stop");
		record = ControlButtonFactory.createButton("Record");
		pause = ControlButtonFactory.createButton("Pause");

		stop.setDisable(true);
		pause.setDisable(true);

		play.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				fireReceiverChangeEvent(true, START_ENTRAINMENT);

				setPlaying(true);
			}
		});

		stop.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				fireReceiverChangeEvent(false, START_ENTRAINMENT);

				setPlaying(false);
			}
		});

		getChildren().add(record);
		getChildren().add(pause);
		getChildren().add(play);
		getChildren().add(stop);
	}

	private void initReceiver() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(final ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case START_ENTRAINMENT:
					setPlaying(e.getBooleanValue());
					break;
				default:
					break;
				}
			}
		});
	}

	/**
	 * Checks if is playing entrainer program.
	 *
	 * @return true, if is playing entrainer program
	 */
	public boolean isPlayingEntrainerProgram() {
		return playingEntrainerProgram;
	}

	/**
	 * Sets the playing entrainer program.
	 *
	 * @param playingEntrainerProgram
	 *          the new playing entrainer program
	 */
	public void setPlayingEntrainerProgram(boolean playingEntrainerProgram) {
		this.playingEntrainerProgram = playingEntrainerProgram;
	}

	/**
	 * Sets the play tool tip.
	 *
	 * @param toolTip
	 *          the new play tool tip
	 */
	public void setPlayToolTip(String toolTip) {
		setToolTip(getPlay(), toolTip);
	}

	/**
	 * Sets the record tool tip.
	 *
	 * @param toolTip
	 *          the new record tool tip
	 */
	public void setRecordToolTip(String toolTip) {
		setToolTip(getRecord(), toolTip);
	}

	/**
	 * Sets the pause tool tip.
	 *
	 * @param toolTip
	 *          the new pause tool tip
	 */
	public void setPauseToolTip(String toolTip) {
		setToolTip(getPause(), toolTip);
	}

	/**
	 * Sets the stop tool tip.
	 *
	 * @param toolTip
	 *          the new stop tool tip
	 */
	public void setStopToolTip(String toolTip) {
		setToolTip(getStop(), toolTip);
	}

	private void setToolTip(final ButtonBase button, final String toolTip) {
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				button.setTooltip(new Tooltip(toolTip));
			}
		});
	}

	/**
	 * Sets the playing.
	 *
	 * @param playing
	 *          the new playing
	 */
	public void setPlaying(final boolean playing) {
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				play.setDisable(playing);
				stop.setDisable(!playing);
				record.setDisable(playing);
				pause.setDisable(!isPlayingEntrainerProgram() || !playing);
				if (!playing) getRecord().setSelected(false);
			}
		});
	}

	/**
	 * Gets the play.
	 *
	 * @return the play
	 */
	public Button getPlay() {
		return (Button) play;
	}

	/**
	 * Gets the stop.
	 *
	 * @return the stop
	 */
	public Button getStop() {
		return (Button) stop;
	}

	/**
	 * Gets the record.
	 *
	 * @return the record
	 */
	public ToggleButton getRecord() {
		return (ToggleButton) record;
	}

	/**
	 * Gets the pause.
	 *
	 * @return the pause
	 */
	public ToggleButton getPause() {
		return (ToggleButton) pause;
	}

	/**
	 * Checks if is recording entrainer program.
	 *
	 * @return true, if is recording entrainer program
	 */
	public boolean isRecordingEntrainerProgram() {
		return recordingEntrainerProgram;
	}

	/**
	 * Sets the recording entrainer program.
	 *
	 * @param recordingEntrainerProgram
	 *          the new recording entrainer program
	 */
	public void setRecordingEntrainerProgram(boolean recordingEntrainerProgram) {
		this.recordingEntrainerProgram = recordingEntrainerProgram;
	}

}
