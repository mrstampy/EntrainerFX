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
package net.sourceforge.entrainer.gui;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.converter.IntegerStringConverter;
import net.sourceforge.entrainer.util.Utils;
import net.sourceforge.entrainer.xml.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class SocketPortDialog.
 */
public class SocketPortDialog extends DialogPane {

	private CheckBox deltaSocketMessage = new CheckBox("Delta Messages");
	private TextField port = new TextField();
	private TextField ipAddress = new TextField();

	/**
	 * Instantiates a new socket port dialog.
	 *
	 * @throws UnknownHostException
	 *           the unknown host exception
	 */
	public SocketPortDialog() throws UnknownHostException {
		super();
		init();
		setId(getClass().getSimpleName());
	}

	private void init() throws UnknownHostException {
		port.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
		port.setMaxWidth(70);

		Integer sp = Settings.getInstance().getSocketPort();
		port.setText(sp == null ? "" : sp.toString());

		String address = Settings.getInstance().getSocketIPAddress();
		if (address == null || address.trim().length() == 0) address = initIPAddress();
		ipAddress.setText(address);
		deltaSocketMessage.setSelected(Settings.getInstance().isDeltaSocketMessage());
		initListener();
		initGui();
		setToolTips();

		setOnMouseClicked(e -> localDoc(e));
	}

	private String initIPAddress() throws UnknownHostException {
		String ipAddress = InetAddress.getLocalHost().getHostAddress();
		Settings.getInstance().setSocketIPAddress(ipAddress);

		return ipAddress;
	}

	private void localDoc(MouseEvent e) {
		if (!(e.isMetaDown() && e.getClickCount() == 1)) return;

		Utils.openLocalDocumentation("advanced.html");
	}

	private void setToolTips() {
		port.setTooltip(new Tooltip("Choose a free socket port for EntrainerFX (typically > 1000)"));
		ipAddress.setTooltip(new Tooltip("Set the hostname or ip address if known, leave blank otherwise"));
		deltaSocketMessage.setTooltip(new Tooltip(
				"Send Entrainer's entire state (unchecked) or just the delta change (checked) per message"));
	}

	private void initGui() {
		getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		GridPane gp = new GridPane();
		gp.setPadding(new Insets(10));

		Label pn = new Label("Port Number");
		Label ha = new Label("Host Address");

		int row = 0;
		int col = 0;

		setConstraints(pn, col++, row);
		setConstraints(port, col, row++);

		col = 0;

		setConstraints(ha, col++, row);
		setConstraints(ipAddress, col, row++);

		col = 0;

		setConstraints(deltaSocketMessage, col, row);

		gp.getChildren().addAll(pn, port, ha, ipAddress, deltaSocketMessage);

		setContent(gp);
	}

	private void setConstraints(Node node, int col, int row) {
		GridPane.setConstraints(node, col, row);
		GridPane.setMargin(node, new Insets(5));
	}

	private void initListener() {
		deltaSocketMessage.setOnAction(e -> Settings.getInstance().setDeltaSocketMessage(deltaSocketMessage.isSelected()));
	}

	/**
	 * Validate and save.
	 */
	public void validateAndSave() {
		int port = Integer.parseInt(this.port.getText());
		if (port <= 0) {
			showErrorDialog(port);
			return;
		}

		Settings.getInstance().setSocketPort(port);
		Settings.getInstance().setSocketIPAddress(ipAddress.getText());
	}

	private void showErrorDialog(int port) {
		Alert alert = new Alert(AlertType.ERROR, port + " is not a valid port number", ButtonType.OK);
		alert.setTitle("Invalid Port Number");
		alert.initOwner(EntrainerFX.getInstance().getStage());
		alert.showAndWait();
	}

}
