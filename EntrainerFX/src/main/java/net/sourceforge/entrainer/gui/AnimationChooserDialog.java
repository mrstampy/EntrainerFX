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
package net.sourceforge.entrainer.gui;

import static net.sourceforge.entrainer.gui.AnimationChooserDialogConstants.ACD_ANIMATIONS_COMBOBOX_NAME;
import static net.sourceforge.entrainer.gui.AnimationChooserDialogConstants.ACD_ANIMATION_BACKGROUND;
import static net.sourceforge.entrainer.gui.AnimationChooserDialogConstants.ACD_USE_DESKTOP_BACKGROUND_NAME;
import static net.sourceforge.entrainer.mediator.MediatorConstants.ANIMATION_BACKGROUND;
import static net.sourceforge.entrainer.mediator.MediatorConstants.ANIMATION_DESKTOP_BACKGROUND;
import static net.sourceforge.entrainer.mediator.MediatorConstants.ANIMATION_PROGRAM;
import static net.sourceforge.entrainer.util.Utils.openBrowser;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.entrainer.gui.animation.AnimationRegister;
import net.sourceforge.entrainer.gui.animation.EntrainerAnimation;
import net.sourceforge.entrainer.gui.jfx.AnimationPane;
import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.guitools.MigHelper;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;
import net.sourceforge.entrainer.xml.Settings;
import net.sourceforge.entrainer.xml.program.EntrainerProgram;

// TODO: Auto-generated Javadoc
/**
 * This class allows the user to choose which animation to display and provides
 * the option to change the animation background for animations which do not use
 * a colour background.
 *
 * @author burton
 * @deprecated now using {@link AnimationPane}
 */
public class AnimationChooserDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JComboBox animations;
	private JCheckBox useDesktopAsBackground;
	private JTextField animationBackground;

	private Sender sender = new SenderAdapter();

	/**
	 * Instantiates a new animation chooser dialog.
	 *
	 * @param frame the frame
	 */
	public AnimationChooserDialog(Frame frame) {
		super(frame, "Animation Chooser", true);

		setResizable(false);

		initGui();
		initMediator();
		initEntrainerAnimation();
	}

	/**
	 * Instantiates a new animation chooser dialog.
	 *
	 * @param dialog the dialog
	 * @param xml the xml
	 */
	public AnimationChooserDialog(Dialog dialog, EntrainerProgram xml) {
		super(dialog, "Animation Chooser", true);

		setResizable(false);

		initGui();
		initMediator();
		initXmlAnimation(xml);
	}

	private void initXmlAnimation(EntrainerProgram xml) {
		if (xml.getAnimationProgram() != null && xml.getAnimationProgram().trim().length() > 0) {
			initEntrainerAnimation(xml.getAnimationProgram());
		}

		if (xml.getAnimationBackground() != null && xml.getAnimationBackground().trim().length() > 0) {
			initAnimationBackground(xml.getAnimationBackground());
		}
	}

	private void initEntrainerAnimation() {
		initEntrainerAnimation(Settings.getInstance().getAnimationProgram());
		initAnimationBackground(Settings.getInstance().getAnimationBackground());
	}

	private void initGui() {
		useDesktopAsBackground = new JCheckBox("Use Desktop Background", true);
		animationBackground = new JTextField(15);
		animationBackground.setEditable(false);

		animationBackground.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				showImageChooser();
			}

		});

		animationBackground.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					showImageChooser();
				}
			}

		});

		ComboBoxModel model = new DefaultComboBoxModel(AnimationRegister.getEntrainerAnimations().toArray());
		animations = new JComboBox(model);

		animations.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				fireAnimationSelection(e.getItem().toString());
				setBackgroundFields((EntrainerAnimation) e.getItem());
			}

		});

		useDesktopAsBackground.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				fireDesktopBackground();
				if (useDesktopAsBackground.isSelected()) {
					fireBackgroundSelection(null);
					animationBackground.setText("");
				} else {
					showImageChooser();
				}
			}

		});

		MigHelper mh = new MigHelper(getContentPane());

		mh.add(getAnimationSelectionContainer()).add(getBackgroundImageContainer());
		setComponentNames();
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.isControlDown() && e.getClickCount() == 1) {
					openBrowser(getLocalDocAddress());
				}
			}
		});
	}

	private String getLocalDocAddress() {
		File file = new File(".");

		String path = file.getAbsolutePath();

		path = path.substring(0, path.lastIndexOf("."));

		return "file://" + path + "doc/animations.html";
	}

	private void setComponentNames() {
		animationBackground.setName(ACD_ANIMATION_BACKGROUND);
		animations.setName(ACD_ANIMATIONS_COMBOBOX_NAME);
		useDesktopAsBackground.setName(ACD_USE_DESKTOP_BACKGROUND_NAME);
	}

	/**
	 * Show image chooser.
	 */
	@SuppressWarnings("deprecation")
	protected void showImageChooser() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Choose Background Image for Animation");
		chooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				String fileName = f.getName().toLowerCase();
				return f.isDirectory() || fileName.endsWith("jpg") || fileName.endsWith("jpeg") || fileName.endsWith(".gif")
						|| fileName.endsWith("bmp") || fileName.endsWith("png");
			}

			@Override
			public String getDescription() {
				return "Image Files";
			}

		});

		int val = chooser.showDialog(this, "Ok");
		if (val == JFileChooser.APPROVE_OPTION) {
			try {
				URL imageFile = chooser.getSelectedFile().toURL();
				String externalForm = imageFile.toExternalForm();
				fireBackgroundSelection(externalForm);
				initAnimationBackground(externalForm);
			} catch (MalformedURLException e) {
				GuiUtil.handleProblem(e);
			}
		} else {
			useDesktopAsBackground.setSelected(true);
		}
	}

	private Container getAnimationSelectionContainer() {
		MigHelper mh = new MigHelper();

		mh.add("Animations").addLast(animations);

		return mh.getContainer();
	}

	private Container getBackgroundImageContainer() {
		MigHelper mh = new MigHelper();

		mh.addLast(useDesktopAsBackground).add(animationBackground);

		return mh.getContainer();
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addSender(sender);
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {

				case ANIMATION_BACKGROUND:
					initAnimationBackground(e.getStringValue());
					break;
				case ANIMATION_DESKTOP_BACKGROUND:
					useDesktopAsBackground.setSelected(e.getBooleanValue());
					break;
				case ANIMATION_PROGRAM:
					initEntrainerAnimation(e.getStringValue());
					break;
				default:
					break;

				}

			}
		});
	}

	private void initEntrainerAnimation(String animationProgram) {
		if (animationProgram != null && animationProgram.trim().length() > 0) {
			animations.setSelectedItem(AnimationRegister.getEntrainerAnimation(animationProgram));
			setBackgroundFields((EntrainerAnimation) animations.getSelectedItem());
		}
	}

	private void setBackgroundFields(EntrainerAnimation selectedItem) {
		if (selectedItem.useBackgroundColour()) {
			useDesktopAsBackground.setSelected(false);
			useDesktopAsBackground.setEnabled(false);
			animationBackground.setEnabled(false);
			return;
		}

		if (selectedItem.useDesktopBackground()) {
			useDesktopAsBackground.setSelected(animationBackground.getText().equals(""));
			useDesktopAsBackground.setEnabled(true);
			animationBackground.setEnabled(true);
		} else {
			useDesktopAsBackground.setSelected(false);
			useDesktopAsBackground.setEnabled(true);
			animationBackground.setEnabled(false);
		}
	}

	private void initAnimationBackground(String s) {
		useDesktopAsBackground.setSelected(null == s);
		if (!useDesktopAsBackground.isSelected()) {
			animationBackground.setText(s);
		}
	}

	private void fireDesktopBackground() {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, useDesktopAsBackground.isSelected(),
				ANIMATION_DESKTOP_BACKGROUND));
	}

	private void fireAnimationSelection(String name) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, name, ANIMATION_PROGRAM));
	}

	private void fireBackgroundSelection(String name) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, name, ANIMATION_BACKGROUND));
	}

}
