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

public abstract class AbstractTitledPane extends TitledPane {

	public static final double COLLAPSED_OPACITY = 0.5;

	public static final double EXPANDED_OPACITY = 0.75;

	protected static final Color TEXT_FILL = Color.CORNSILK;

	private Sender sender = new SenderAdapter();

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

	protected abstract Node getContentPane();

	protected void setTextFill(Labeled lb) {
		lb.setTextFill(TEXT_FILL);
	}

	protected void fireReceiverChangeEvent(double value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	protected void fireReceiverChangeEvent(boolean value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	protected void fireReceiverChangeEvent(String value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	protected void fireReceiverChangeEvent(int value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	protected void fireReceiverChangeEvent(java.awt.Color value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

}
