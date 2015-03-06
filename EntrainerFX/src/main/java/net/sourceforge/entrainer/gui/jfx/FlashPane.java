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
package net.sourceforge.entrainer.gui.jfx;

import java.awt.Color;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.swing.JColorChooser;
import javax.swing.SwingUtilities;

import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;

// TODO: Auto-generated Javadoc
/**
 * The Class FlashPane.
 */
public class FlashPane extends TitledPane {

	/** The Constant CSS_ID. */
	public static final String CSS_ID = "checkbox-pane";
	private CheckBox flash = new CheckBox("Flash Colours");
	private CheckBox psychedelic = new CheckBox("Psychedelic");
	private Button colourChooser = new Button("Choose Entrainment Colour");

	private Sender sender = new SenderAdapter();

	private long id = System.currentTimeMillis();

	/**
	 * Instantiates a new flash pane.
	 */
	public FlashPane() {
		super();
		init();
	}

	/**
	 * Clear mediator objects.
	 */
	public void clearMediatorObjects() {
		EntrainerMediator.getInstance().removeReceiver(this);
		EntrainerMediator.getInstance().removeSender(sender);
	}

	private void init() {
		setId(CSS_ID);
		setText("Flash Panel Options");

		EntrainerMediator.getInstance().addSender(sender);
		initMediator();
		initCheckBox(flash, MediatorConstants.IS_FLASH);
		initCheckBox(psychedelic, MediatorConstants.IS_PSYCHEDELIC);

		psychedelic.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				colourChooser.setDisable(psychedelic.isSelected());
			}
		});

		HBox hbox = new HBox();
		hbox.setPadding(new Insets(5, 5, 5, 20));
		
		HBox.setMargin(flash, new Insets(5));
		HBox.setMargin(psychedelic, new Insets(5));
		HBox.setMargin(colourChooser, new Insets(5));

		VBox vBox = new VBox(5);
		vBox.getChildren().addAll(flash, psychedelic);
		hbox.getChildren().add(vBox);
		hbox.getChildren().add(colourChooser);

		setContent(hbox);

		expandedProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable arg0) {
				setOpacity(isExpanded() ? 1 : 0.25);
			}
		});

		colourChooser.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				chooseColour();
			}

		});
	}

	private void chooseColour() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				Color c = JColorChooser.showDialog(null, "Entrainment Colour",
						JFXUtils.fromJFXColor((javafx.scene.paint.Color) colourChooser.getTextFill()));

				if (c != null) {
					fireReceiverChangeEvent(c);
				}
			}
		});
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			public void receiverChangeEventPerformed(final ReceiverChangeEvent e) {
				JFXUtils.runLater(new Runnable() {

					@Override
					public void run() {
						processReceiverChangeEvent(e);
					}
				});
			}

			@Override
			protected void processReceiverChangeEvent(final ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case IS_FLASH:
					if (flash.isSelected() == e.getBooleanValue()) return;
					setFlashSelected(e.getBooleanValue());
					break;
				case IS_PSYCHEDELIC:
					if (psychedelic.isSelected() == e.getBooleanValue()) return;
					setPsychedelicSelected(e.getBooleanValue());
					colourChooser.setDisable(!psychedelic.isSelected());
					break;
				case FLASH_COLOUR:
					javafx.scene.paint.Color c = JFXUtils.toJFXColor(e.getColourValue());
					if (c.equals(colourChooser.getTextFill())) return;
					colourChooser.setTextFill(c);
					setColourChooserEffect();
					break;
				default:
					break;

				}
			}

		});

	}

	private void fireReceiverChangeEvent(final Color value) {
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				colourChooser.setTextFill(JFXUtils.toJFXColor(value));
				setColourChooserEffect();
			}
		});
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value));
	}

	private void setColourChooserEffect() {
		DropShadow is = new DropShadow();
		is.setOffsetX(2);
		is.setOffsetY(2);
		is.setColor(((javafx.scene.paint.Color) colourChooser.getTextFill()).invert());

		colourChooser.setEffect(is);
	}

	private void initCheckBox(final CheckBox checkBox, final MediatorConstants parm) {
		checkBox.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				fireReceiverChangeEvent(checkBox.isSelected(), parm);
			}
		});
	}

	private void fireReceiverChangeEvent(boolean value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	/**
	 * Sets the flash selected.
	 *
	 * @param selected the new flash selected
	 */
	public void setFlashSelected(boolean selected) {
		setSelected(selected, flash);
	}

	/**
	 * Sets the psychedelic selected.
	 *
	 * @param selected the new psychedelic selected
	 */
	public void setPsychedelicSelected(boolean selected) {
		setSelected(selected, psychedelic);
	}

	/**
	 * Sets the flash tool tip.
	 *
	 * @param toolTip the new flash tool tip
	 */
	public void setFlashToolTip(String toolTip) {
		setToolTip(toolTip, flash);
	}

	/**
	 * Sets the psychedelic tool tip.
	 *
	 * @param toolTip the new psychedelic tool tip
	 */
	public void setPsychedelicToolTip(String toolTip) {
		setToolTip(toolTip, psychedelic);
	}

	private void setSelected(final boolean selected, final CheckBox checkBox) {
		if (checkBox.isSelected() == selected) return;
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				checkBox.setSelected(selected);
			}
		});
	}

	private void setToolTip(final String toolTip, final Control node) {
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				node.setTooltip(new Tooltip(toolTip));
			}
		});
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (!(o instanceof FlashPane)) return false;
		FlashPane fp = (FlashPane) o;
		return fp.id == id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (colourChooser.hashCode() + flash.hashCode() + psychedelic.hashCode()) * 17;
	}

	/**
	 * Gets the flash.
	 *
	 * @return the flash
	 */
	public CheckBox getFlash() {
		return flash;
	}

	/**
	 * Gets the psychedelic.
	 *
	 * @return the psychedelic
	 */
	public CheckBox getPsychedelic() {
		return psychedelic;
	}

	/**
	 * Gets the colour chooser.
	 *
	 * @return the colour chooser
	 */
	public Button getColourChooser() {
		return colourChooser;
	}

}
