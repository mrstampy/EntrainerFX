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

import static net.sourceforge.entrainer.util.Utils.openBrowser;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.guitools.MigHelper;
import net.sourceforge.entrainer.widgets.IntegerTextField;
import net.sourceforge.entrainer.xml.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class SocketPortDialog.
 */
@SuppressWarnings("serial")
public class SocketPortDialog extends JDialog {

	private JButton ok = new JButton("Ok");
	private JButton cancel = new JButton("Cancel");
	private JCheckBox deltaSocketMessage = new JCheckBox("Delta Messages");
	private IntegerTextField port = new IntegerTextField(6);
	private JTextField ipAddress = new JTextField(10);

	/**
	 * Instantiates a new socket port dialog.
	 *
	 * @throws UnknownHostException
	 *           the unknown host exception
	 */
	public SocketPortDialog() throws UnknownHostException {
		super((Frame)null, "Choose Socket Port");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		init();
	}

	private void init() throws UnknownHostException {
		port.setNumber(Settings.getInstance().getSocketPort());
		String address = Settings.getInstance().getSocketIPAddress();
		if (address == null || address.trim().length() == 0) address = initIPAddress();
		ipAddress.setText(address);
		deltaSocketMessage.setSelected(Settings.getInstance().isDeltaSocketMessage());
		initListener();
		initGui();
		setToolTips();

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.isControlDown() && e.getClickCount() == 1) {
					openBrowser(getLocalDocAddress());
				}
			}
		});
	}

	private String initIPAddress() throws UnknownHostException {
		String ipAddress = InetAddress.getLocalHost().getHostAddress();
		Settings.getInstance().setSocketIPAddress(ipAddress);

		return ipAddress;
	}

	private String getLocalDocAddress() {
		File file = new File(".");

		String path = file.getAbsolutePath();

		path = path.substring(0, path.lastIndexOf("."));

		return "file://" + path + "doc/sockets.html";
	}

	private void setToolTips() {
		port.setToolTipText("Choose a free socket port for Entrainer (typically > 1000)");
		ipAddress.setToolTipText("Set the hostname or ip address if known, leave blank otherwise");
		deltaSocketMessage
				.setToolTipText("Send Entrainer's entire state (unchecked) or just the delta change (checked) per message");
	}

	private void initGui() {
		MigHelper mh = new MigHelper(getContentPane());
		mh.addLast(getPortPanel()).addLast(deltaSocketMessage).add(getButtonPanel());
	}

	private Component getPortPanel() {
		MigHelper mh = new MigHelper();

		mh.add("Port Number").alignWest().addLast(port);
		mh.add("Host Address").alignWest().addLast(ipAddress);

		return mh.getContainer();
	}

	private Component getButtonPanel() {
		MigHelper mh = new MigHelper();

		mh.add(ok).add(cancel);

		return mh.getContainer();
	}

	private void initListener() {
		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				validateAndSavePort();
			}
		});

		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				close();
			}
		});

		deltaSocketMessage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Settings.getInstance().setDeltaSocketMessage(deltaSocketMessage.isSelected());
			}
		});
	}

	private void close() {
		GuiUtil.fadeOutAndDispose(this, 500);
	}

	/**
	 * Validate and save port.
	 */
	protected void validateAndSavePort() {
		if (port.getNumber() <= 0) {
			showErrorDialog();
			return;
		}

		Settings.getInstance().setSocketPort((int) port.getNumber());
		Settings.getInstance().setSocketIPAddress(ipAddress.getText());

		close();
	}

	private void showErrorDialog() {
		JOptionPane.showMessageDialog(this,
				port.getNumber() + " is not a valid port number",
				"Invalid Port Number",
				JOptionPane.ERROR_MESSAGE);
	}

}
