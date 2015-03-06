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
package net.sourceforge.entrainer.gui;

import static net.sourceforge.entrainer.gui.PinkPanningConstants.PPC_AMPLITUDE_SLIDER_NAME;
import static net.sourceforge.entrainer.gui.PinkPanningConstants.PPC_AMPLITUDE_VALUE_NAME;
import static net.sourceforge.entrainer.gui.PinkPanningConstants.PPC_MULTIPLE_SLIDER_NAME;
import static net.sourceforge.entrainer.gui.PinkPanningConstants.PPC_MULTIPLE_VALUE_NAME;
import static net.sourceforge.entrainer.gui.PinkPanningConstants.PPC_OK_BUTTON;
import static net.sourceforge.entrainer.gui.PinkPanningConstants.PPC_PAN_CHECKBOX_NAME;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_ENTRAINER_MULTIPLE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_PAN;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_PAN_AMPLITUDE;
import static net.sourceforge.entrainer.util.Utils.openBrowser;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.guitools.MigHelper;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;

// TODO: Auto-generated Javadoc
/**
 * Dialog which contains controls for pink noise panning.
 * 
 * @author burton
 */
public class PinkPanning extends JDialog {

	private static final long serialVersionUID = 1L;

	private JSlider amplitudeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
	private JSlider multipleSlider = new JSlider(JSlider.HORIZONTAL, 1, 512, 32);

	private DecimalFormat amplitudeFormat = new DecimalFormat("#0%");
	private DecimalFormat multipleFormat = new DecimalFormat("#");

	private JLabel amplitudeValue = new JLabel("100");
	private JLabel multipleValue = new JLabel("1.00");

	private JCheckBox panCheck = new JCheckBox();
	private JButton ok = new JButton(PPC_OK_BUTTON);

	private Sender sender = new SenderAdapter();

	/**
	 * Instantiates a new pink panning.
	 *
	 * @param owner
	 *          the owner
	 */
	public PinkPanning(Frame owner) {
		super(owner, "Pink Noise Options", true);
		setResizable(false);
		init();
	}

	/**
	 * Overridden to prevent resizing bug.
	 */
	public void pack() {
		setAmplitudeValue(1);
		setMultipleValue(128);
		super.pack();
		amplitudeValue.setPreferredSize(amplitudeValue.getSize());
		multipleValue.setPreferredSize(multipleValue.getSize());

		setAmplitudeValue(getAmplitudeFromSlider());
		setMultipleValue(multipleSlider.getValue());
	}

	private void init() {
		addListeners();
		layoutComponents();
		initMediator();
		setComponentNames();

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.isControlDown() && e.getClickCount() == 1) {
					openBrowser(getLocalDocAddress());
				}
			}
		});
	}

	private String getLocalDocAddress() {
		File file = new File(".");

		String path = file.getAbsolutePath();

		path = path.substring(0, path.lastIndexOf("."));

		return "file://" + path + "doc/running.html";
	}

	private void setComponentNames() {
		amplitudeSlider.setName(PPC_AMPLITUDE_SLIDER_NAME);
		multipleSlider.setName(PPC_MULTIPLE_SLIDER_NAME);
		amplitudeValue.setName(PPC_AMPLITUDE_VALUE_NAME);
		multipleValue.setName(PPC_MULTIPLE_VALUE_NAME);
		panCheck.setName(PPC_PAN_CHECKBOX_NAME);
		ok.setName(PPC_OK_BUTTON);
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addSender(sender);
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case PINK_PAN_AMPLITUDE:
					setAmplitude(e.getDoubleValue());
					if (!panCheck.isSelected()) panCheck.doClick();
					break;
				case PINK_ENTRAINER_MULTIPLE:
					setMultiple((int) e.getDoubleValue());
					break;
				case PINK_PAN:
					setPan(e.getBooleanValue());
					break;
				default:
					break;
				}
			}

		});
	}

	private void addListeners() {
		ActionListener okListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GuiUtil.fadeOutVisibleFalse(PinkPanning.this, 500);
			}
		};

		ok.addActionListener(okListener);

		amplitudeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double d = getAmplitudeFromSlider();
				setAmplitudeValue(d);
				fireReceiverChangeEvent(d, PINK_PAN_AMPLITUDE);
			}
		});

		multipleSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double d = multipleSlider.getValue();
				setMultipleValue((int) d);
				fireReceiverChangeEvent(d, PINK_ENTRAINER_MULTIPLE);
			}
		});

		panCheck.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				fireReceiverChangeEvent(panCheck.isSelected());
			}

		});
	}

	private void fireReceiverChangeEvent(boolean isPan) {
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, isPan, PINK_PAN);
		sender.fireReceiverChangeEvent(e);
	}

	private void fireReceiverChangeEvent(double d, MediatorConstants parm) {
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, d, parm);
		sender.fireReceiverChangeEvent(e);
	}

	private void layoutComponents() {
		MigHelper mh = new MigHelper(getContentPane());
		mh.setLayoutFillX(true).setLayoutInsets(0, 2, 0, 2);

		mh.growX(100).addLast(getControlPanel()).growX(100).add(getButtonPanel());
	}

	private Container getControlPanel() {
		JPanel jp = new JPanel();
		jp.setBorder(new BevelBorder(BevelBorder.LOWERED));
		MigHelper mh = new MigHelper(jp);

		mh.alignEast().add("Pan").alignWest().addLast(panCheck).alignCenter();

		addComponent(mh, amplitudeSlider, "Pan Amplitude", amplitudeValue);
		addComponent(mh, multipleSlider, "Entrainment Multiplier", multipleValue);

		return mh.getContainer();
	}

	private Container getButtonPanel() {
		JPanel jp = new JPanel();
		jp.setBorder(new BevelBorder(BevelBorder.RAISED));
		MigHelper mh = new MigHelper(jp);
		mh.add(ok);

		return mh.getContainer();
	}

	private void addComponent(MigHelper mh, JComponent component, String label, JLabel value) {
		mh.alignEast().add(label).alignCenter().add(component).alignWest().addLast(value);
	}

	/**
	 * Gets the amplitude.
	 *
	 * @return the amplitude
	 */
	public double getAmplitude() {
		return getAmplitudeFromSlider();
	}

	private void setAmplitude(double amplitude) {
		setAmplitudeToSlider(amplitude);
		setAmplitudeValue(amplitude);
	}

	/**
	 * Gets the multiple.
	 *
	 * @return the multiple
	 */
	public int getMultiple() {
		return multipleSlider.getValue();
	}

	private void setMultiple(int multiple) {
		multipleSlider.setValue(multiple);
		setMultipleValue(multiple);
	}

	/**
	 * Checks if is pan.
	 *
	 * @return true, if is pan
	 */
	public boolean isPan() {
		return panCheck.isSelected();
	}

	private void setPan(boolean pan) {
		panCheck.setSelected(pan);
	}

	private void setMultipleValue(int i) {
		multipleValue.setText(multipleFormat.format(i));
	}

	private double getAmplitudeFromSlider() {
		return ((double) amplitudeSlider.getValue()) / 100;
	}

	private void setAmplitudeValue(double d) {
		amplitudeValue.setText(amplitudeFormat.format(d));
	}

	private void setAmplitudeToSlider(double d) {
		amplitudeSlider.setValue((int) (d * 100));
	}

}
