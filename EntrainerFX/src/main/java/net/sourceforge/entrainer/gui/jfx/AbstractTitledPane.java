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

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.TitledPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractTitledPane.
 */
public abstract class AbstractTitledPane extends TitledPane {

	/** The Constant COLLAPSED_OPACITY. */
	public static final double COLLAPSED_OPACITY = 0.5;

	/** The Constant EXPANDED_OPACITY. */
	public static final double EXPANDED_OPACITY = 0.75;

	/** The Constant TEXT_FILL. */
	protected static final Color TEXT_FILL = Color.CORNSILK;

	private Sender sender = new SenderAdapter();

	/**
	 * Instantiates a new abstract titled pane.
	 *
	 * @param title
	 *          the title
	 */
	public AbstractTitledPane(String title) {
		super();
		setText(title);
		setStyle("-fx-background-color: black");
		EntrainerMediator.getInstance().addSender(sender);
	}

	/**
	 * Clear mediator objects.
	 */
	public void clearMediatorObjects() {
		EntrainerMediator.getInstance().removeReceiver(this);
		EntrainerMediator.getInstance().removeSender(sender);
	}

	/**
	 * Inits the.
	 */
	protected void init() {
		setStyle("-fx-background-color: black");
		Node contentPane = getContentPane();
		contentPane.setStyle("-fx-background-color: black");
		setContent(contentPane);

		expandedProperty().addListener(e -> setOpacity(isExpanded() ? EXPANDED_OPACITY : COLLAPSED_OPACITY));

		setOnMouseEntered(e -> determineOpacity());

		setOnMouseExited(e -> mouseExited());

		setOpacity(0);
	}

	private void mouseExited() {
		if (isExpanded()) return;

		FadeTransition ft = new FadeTransition(Duration.millis(250), this);

		ft.setFromValue(getOpacity());
		ft.setToValue(0);

		ft.play();
	}

	private void determineOpacity() {
		if (isExpanded()) return;

		FadeTransition ft = new FadeTransition(Duration.millis(250), this);

		ft.setFromValue(getOpacity());
		ft.setToValue(COLLAPSED_OPACITY);

		ft.play();
	}

	/**
	 * Gets the content pane.
	 *
	 * @return the content pane
	 */
	protected abstract Node getContentPane();

	/**
	 * Sets the text fill.
	 *
	 * @param lb
	 *          the new text fill
	 */
	protected void setTextFill(Labeled lb) {
		lb.setTextFill(TEXT_FILL);
	}

	/**
	 * Fire receiver change event.
	 *
	 * @param value
	 *          the value
	 * @param parm
	 *          the parm
	 */
	protected void fireReceiverChangeEvent(double value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	/**
	 * Fire receiver change event.
	 *
	 * @param value
	 *          the value
	 * @param parm
	 *          the parm
	 */
	protected void fireReceiverChangeEvent(boolean value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	/**
	 * Fire receiver change event.
	 *
	 * @param value
	 *          the value
	 * @param parm
	 *          the parm
	 */
	protected void fireReceiverChangeEvent(String value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	/**
	 * Fire receiver change event.
	 *
	 * @param value
	 *          the value
	 * @param parm
	 *          the parm
	 */
	protected void fireReceiverChangeEvent(int value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	/**
	 * Fire receiver change event.
	 *
	 * @param value
	 *          the value
	 * @param parm
	 *          the parm
	 */
	protected void fireReceiverChangeEvent(java.awt.Color value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	/**
	 * Fire receiver change event.
	 *
	 * @param e
	 *          the e
	 * @param b
	 *          the b
	 * @param parm
	 *          the parm
	 */
	protected void fireReceiverChangeEvent(Enum<?> e, boolean b, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, e, b, parm));
	}

}
