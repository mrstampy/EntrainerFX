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
package net.sourceforge.entrainer.gui.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.xml.bind.JAXBException;

import net.sourceforge.entrainer.gui.jfx.JFXUtils;
import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.socket.EntrainerStateMessage;
import net.sourceforge.entrainer.socket.EntrainerStateMessageMarshal;
import net.sourceforge.entrainer.socket.WebSocketHandler;
import net.sourceforge.entrainer.util.Utils;
import net.sourceforge.entrainer.xml.Settings;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

// TODO: Auto-generated Javadoc
/**
 * The Class EntrainerSocketConnector.
 */
public class EntrainerSocketConnector extends DialogPane {

	private TextArea output = new TextArea(); // 30, 50
	private ToggleButton connect = new ToggleButton("Connect");
	private Button clear = new Button("Clear");

	private RadioButton nioConnection = new RadioButton("NIO Connection");
	private RadioButton webSocketConnection = new RadioButton("Web Socket Connection");

	private ToggleGroup connectionTypes = new ToggleGroup();

	private Slider entrainmentFrequency = new Slider(0, 40, 10);
	private Slider frequency = new Slider(20, 500, 200);
	private Slider amplitude = new Slider(0, 1, 0.5);
	private Slider pinkNoise = new Slider(0, 1, 0);

	private DecimalFormat entrainmentFormat = new DecimalFormat("#0.00Hz");
	private DecimalFormat amplitudeFormat = new DecimalFormat("#0%");
	private DecimalFormat frequencyFormat = new DecimalFormat("##0Hz");

	private Label entrainmentValue = new Label();
	private Label frequencyValue = new Label();
	private Label amplitudeValue = new Label();
	private Label pinkNoiseValue = new Label();

	private GridPane pane = new GridPane();

	// booleans to prevent message reflection feedback
	private volatile boolean isEntrainerEntrainmentFrequencyMessage;
	private volatile boolean isEntrainerFrequencyMessage;
	private volatile boolean isEntrainerAmplitudeMessage;
	private volatile boolean isEntrainerPinkNoiseAmplitudeMessage;

	private Bootstrap bootstrap;
	private Channel channel;

	private int port;
	private String ipAddress;

	private Executor executor = Executors.newCachedThreadPool();

	private EntrainerStateMessageMarshal marshal = new EntrainerStateMessageMarshal();

	private ObjectMapper jsonMapper = new ObjectMapper();

	/**
	 * Instantiates a new entrainer socket connector.
	 *
	 * @param ipAddress
	 *          the ip address
	 * @param port
	 *          the port
	 * @throws UnknownHostException
	 *           the unknown host exception
	 */
	public EntrainerSocketConnector(String ipAddress, int port) throws UnknownHostException {
		jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, Boolean.TRUE);
		jsonMapper.setSerializationInclusion(Include.NON_NULL);

		this.port = port;
		if (ipAddress == null || ipAddress.trim().length() == 0) ipAddress = initIPAddress();
		this.ipAddress = ipAddress;
		if (ipAddress == null || ipAddress.trim().length() == 0) throw new RuntimeException("IP address has not been set");
		init();
	}

	private String initIPAddress() throws UnknownHostException {
		String ipAddress = InetAddress.getLocalHost().getHostAddress();
		Settings.getInstance().setSocketIPAddress(ipAddress);

		return ipAddress;
	}

	private void init() {
		initConnector();
		initListeners();
		wireSliders();
		initGui();
		setTooltips();

		output.setPrefColumnCount(50);
		output.setPrefRowCount(30);

		getButtonTypes().add(ButtonType.CANCEL);

		setOnMouseClicked(e -> getLocalDocAddress(e));
	}

	private void setTooltips() {
		nioConnection.setTooltip(new Tooltip("Connect to EntrainerFX using Java NIO Sockets"));
		webSocketConnection.setTooltip(new Tooltip("Connect to EntrainerFX using Web Sockets"));
		connect.setTooltip(new Tooltip("Connects to Entrainer's external socket"));
		clear.setTooltip(new Tooltip("Clears the messages"));
		output.setTooltip(new Tooltip("Displays messages received from Entrainer"));
	}

	private void getLocalDocAddress(MouseEvent e) {
		if (!(e.isMetaDown() && e.getClickCount() == 1)) return;

		Utils.openLocalDocumentation("sockets.html");
	}

	private void initGui() {
		GridPane gp = new GridPane();
		gp.setPadding(new Insets(10));
		gp.setHgap(10);
		gp.setVgap(10);

		GridPane.setConstraints(output, 0, 0);
		GridPane.setConstraints(pane, 1, 0);

		Node buttonPanel = getButtonPanel();

		GridPane.setConstraints(buttonPanel, 0, 1);

		gp.getChildren().addAll(output, pane, buttonPanel);

		setContent(gp);
	}

	private Node getButtonPanel() {
		HBox box = new HBox(10, getConnectionTypePanel(), connect, clear);
		box.setAlignment(Pos.CENTER);

		return box;
	}

	private Node getConnectionTypePanel() {
		nioConnection.setToggleGroup(connectionTypes);
		webSocketConnection.setToggleGroup(connectionTypes);

		nioConnection.setSelected(true);

		VBox box = new VBox(10, nioConnection, webSocketConnection);

		return box;
	}

	private void initListeners() {
		output.setEditable(false);

		connect.setOnAction(e -> onConnect());
		clear.setOnAction(e -> output.setText(""));
	}

	private void onConnect() {
		if (connect.isSelected()) {
			try {
				if (connectToEntrainer()) {
					connect.setText("Disconnect");
					enableConnectionTypes(false);
				} else {
					connectionFailed();
				}
			} catch (Throwable f) {
				GuiUtil.handleProblem(f);
				connectionFailed();
			}
		} else {
			disconnectFromEntrainer();
			enableConnectionTypes(true);
			connect.setText("Connect");
		}
	}

	private void connectionFailed() {
		connect.setSelected(false);
		enableConnectionTypes(true);
	}

	private void enableConnectionTypes(boolean enable) {
		nioConnection.setDisable(!enable);
		webSocketConnection.setDisable(!enable);
	}

	private boolean isActiveConnection() {
		return bootstrap != null && channel != null && channel.isActive();
	}

	private void wireSliders() {
		initSlider(amplitude, amplitudeValue, amplitudeFormat);
		initSlider(entrainmentFrequency, entrainmentValue, entrainmentFormat);
		initSlider(frequency, frequencyValue, frequencyFormat);
		initSlider(pinkNoise, pinkNoiseValue, amplitudeFormat);

		pinkNoise.valueProperty().addListener(e -> sendPinkNoiseChange());
		frequency.valueProperty().addListener(e -> sendFrequencyChange());
		amplitude.valueProperty().addListener(e -> sendAmplitudeChange());
		entrainmentFrequency.valueProperty().addListener(e -> sendEntrainmentFrequencyChange());

		pane.setHgap(10);
		pane.setVgap(20);
		pane.setPadding(new Insets(10));
		pane.setAlignment(Pos.CENTER);

		int row = 0;
		addSlider("Entrainment Frequency", entrainmentFrequency, entrainmentValue, row++);
		addSlider("Frequency", frequency, frequencyValue, row++);
		addSlider("Volume", amplitude, amplitudeValue, row++);
		addSlider("Pink Noise", pinkNoise, pinkNoiseValue, row++);
	}

	private void addSlider(String label, Slider slider, Label value, int row) {
		slider.setId(label);
		Label title = new Label(label);

		pane.add(title, 0, row);

		pane.add(slider, 1, row);
		pane.add(value, 2, row);
	}

	private void initSlider(Slider slider, Label label, DecimalFormat format) {
		slider.setEffect(new InnerShadow());

		slider.setMinWidth(350);

		slider.valueProperty().addListener(e -> onSliderChange(slider, label, format));

		label.setText(format.format(slider.getValue()));
	}

	private void onSliderChange(Slider slider, Label label, DecimalFormat format) {
		double value = slider.getValue();
		JFXUtils.runLater(() -> label.setText(format.format(value)));
	}

	private void sendAmplitudeChange() {
		if (isActiveConnection() && !isEntrainerAmplitudeMessage) {
			EntrainerStateMessage esm = new EntrainerStateMessage();
			esm.setAmplitude(getAmplitudeFromSlider());
			sendMessage(esm);
		}

		isEntrainerAmplitudeMessage = false;
	}

	private void sendPinkNoiseChange() {
		if (isActiveConnection() && !isEntrainerPinkNoiseAmplitudeMessage) {
			EntrainerStateMessage esm = new EntrainerStateMessage();
			esm.setPinkNoiseAmplitude(getPinkNoiseValue());
			sendMessage(esm);
		}

		isEntrainerPinkNoiseAmplitudeMessage = false;
	}

	private void sendFrequencyChange() {
		if (isActiveConnection() && !isEntrainerFrequencyMessage) {
			EntrainerStateMessage esm = new EntrainerStateMessage();
			esm.setFrequency(getFrequencyValue());
			sendMessage(esm);
		}

		isEntrainerFrequencyMessage = false;
	}

	private void sendEntrainmentFrequencyChange() {
		if (isActiveConnection() && !isEntrainerEntrainmentFrequencyMessage) {
			EntrainerStateMessage esm = new EntrainerStateMessage();
			esm.setEntrainmentFrequency(getEntrainmentFrequencyFromSlider());
			sendMessage(esm);
		}

		isEntrainerEntrainmentFrequencyMessage = false;
	}

	private double getAmplitudeFromSlider() {
		return ((double) amplitude.getValue());
	}

	private double getEntrainmentFrequencyFromSlider() {
		return ((double) entrainmentFrequency.getValue());
	}

	private double getPinkNoiseValue() {
		return ((double) pinkNoise.getValue());
	}

	private double getFrequencyValue() {
		return (double) frequency.getValue();
	}

	private void initConnector() {
		bootstrap = new Bootstrap();
		bootstrap.group(new NioEventLoopGroup());
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();

				if (webSocketConnection.isSelected()) {
					pipeline.addLast("http-codec", new HttpClientCodec());
					pipeline.addLast("aggregator", new HttpObjectAggregator(8192));
					pipeline.addLast(getWebSocketHandler());
				} else {
					pipeline.addLast(new LengthFieldBasedFrameDecoder(10000, 0, 4, 0, 4));
					pipeline.addLast(new StringDecoder());
					pipeline.addLast(new LengthFieldPrepender(4));
					pipeline.addLast(new StringEncoder());
					pipeline.addLast(getJavaHandler());
				}
			}
		});
	}

	private ChannelHandler getWebSocketHandler() {
		try {
			return new SimpleChannelInboundHandler<Object>() {

				private URI uri = new URI("ws://" + ipAddress + ":" + port + WebSocketHandler.WEBSOCKET_PATH);
				HttpHeaders customHeaders = new DefaultHttpHeaders();

				private final WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(uri,
						WebSocketVersion.V13,
						null,
						false,
						customHeaders);
				private ChannelPromise handshakeFuture;

				@Override
				public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
					handshakeFuture = ctx.newPromise();
				}

				@Override
				public void channelActive(final ChannelHandlerContext ctx) throws Exception {
					handshaker.handshake(ctx.channel());
				}

				public void channelInactive(ChannelHandlerContext ctx) throws Exception {
					JFXUtils.runLater(() -> remoteDisconnection());
				}

				@Override
				public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
					Channel ch = ctx.channel();
					if (!handshaker.isHandshakeComplete()) {
						handshaker.finishHandshake(ch, (FullHttpResponse) msg);
						handshakeFuture.setSuccess();

						requestState(ctx);
						return;
					}

					if (msg instanceof FullHttpResponse) {
						FullHttpResponse response = (FullHttpResponse) msg;
						throw new Exception("Unexpected FullHttpResponse (getStatus=" + response.getStatus() + ", content="
								+ response.content().toString(CharsetUtil.UTF_8) + ')');
					}

					WebSocketFrame frame = (WebSocketFrame) msg;
					if (frame instanceof TextWebSocketFrame) {
						TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
						executeJsonMessage(textFrame.text());
					} else if (frame instanceof PongWebSocketFrame) {
						System.out.println("WebSocket Client received pong");
					} else if (frame instanceof CloseWebSocketFrame) {
						ch.close();
					}
				}

				@Override
				public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
					cause.printStackTrace();

					if (!handshakeFuture.isDone()) {
						handshakeFuture.setFailure(cause);
					}

					ctx.close();
				}
			};
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private void requestState(ChannelHandlerContext ctx) throws Exception {
		EntrainerStateMessage esm = new EntrainerStateMessage();
		esm.setRequestState(Boolean.TRUE);
		ctx.channel().writeAndFlush(getMessage(esm));
	}

	private void remoteDisconnection() {
		if (connect.isSelected()) {
			connect.setSelected(false);
			setOutputText("Disconnected");
			connect.setText("Connect");
			enableConnectionTypes(true);
		}
	}

	private ChannelHandler getJavaHandler() {
		return new SimpleChannelInboundHandler<String>() {

			public void channelActive(ChannelHandlerContext ctx) throws Exception {
				requestState(ctx);
			}

			public void channelInactive(ChannelHandlerContext ctx) throws Exception {
				JFXUtils.runLater(() -> remoteDisconnection());
			}

			@Override
			protected void channelRead0(ChannelHandlerContext ctx, final String msg) throws Exception {
				executeXmlMessage(msg);
			}
		};
	}

	/**
	 * Process message.
	 *
	 * @param esm
	 *          the esm
	 */
	protected void processMessage(EntrainerStateMessage esm) {
		if (esm.getAmplitude() != null && !amplitude.isValueChanging()) {
			setAutoAmplitude(esm.getAmplitude());
		}

		if (esm.getFrequency() != null && !frequency.isValueChanging()) {
			setAutoFrequency(esm.getFrequency());
		}

		if (esm.getEntrainmentFrequency() != null && !entrainmentFrequency.isValueChanging()) {
			setAutoEntrainment(esm.getEntrainmentFrequency());
		}

		if (esm.getPinkNoiseAmplitude() != null && !pinkNoise.isValueChanging()) {
			setAutoPinkNoise(esm.getPinkNoiseAmplitude());
		}
	}

	private void setAutoAmplitude(double value) {
		if (amplitude.getValue() == value) return;
		isEntrainerAmplitudeMessage = true;
		amplitude.setValue(value);
	}

	private void setAutoPinkNoise(double value) {
		if (pinkNoise.getValue() == value) return;
		isEntrainerPinkNoiseAmplitudeMessage = true;
		pinkNoise.setValue(value);
	}

	private void setAutoFrequency(double value) {
		if (frequency.getValue() == value) return;
		isEntrainerFrequencyMessage = true;
		frequency.setValue(value);
	}

	private void setAutoEntrainment(double value) {
		if (entrainmentFrequency.getValue() == value) return;
		isEntrainerEntrainmentFrequencyMessage = true;
		entrainmentFrequency.setValue(value);
	}

	private void sendMessage(EntrainerStateMessage esm) {
		try {
			channel.writeAndFlush(getMessage(esm));
		} catch (Exception e) {
			GuiUtil.handleProblem(e);
		}
	}

	private Object getMessage(EntrainerStateMessage esm) throws Exception {
		if (webSocketConnection.isSelected()) {
			StringWriter writer = new StringWriter();

			jsonMapper.writeValue(writer, esm);

			return new TextWebSocketFrame(writer.toString());
		}

		return marshal.marshalMessage(esm);
	}

	private boolean connectToEntrainer() throws InterruptedException {
		ChannelFuture cf = bootstrap.connect(ipAddress, port).sync();

		setOutputText(cf.isSuccess() ? "Connected to Entrainer on host " + ipAddress + " and port " + port
				: "Cannot connect to Entrainer on host " + ipAddress + " and port " + port);

		if (cf.isSuccess()) {
			channel = cf.channel();
		}

		return cf.isSuccess();
	}

	public void disconnectFromEntrainer() {
		bootstrap.group().shutdownGracefully();
		if (channel != null) channel.close();
		channel = null;
		initConnector();
		setOutputText("Disconnected");
	}

	private void setOutputText(String text) {
		synchronized (output) {
			String oldText = output.getText();
			String newText = text + "\n" + oldText;
			output.setText(newText.length() > 10000 ? newText.substring(0, 10000) : newText);
			output.home();
		}
	}

	private void executeXmlMessage(final String msg) {
		Runnable processor = new Runnable() {

			@Override
			public void run() {
				setOutputText(msg);

				try {
					EntrainerStateMessage esm = marshal.unmarshal(msg);
					processMessage(esm);
				} catch (JAXBException e) {
					GuiUtil.handleProblem(e);
				}
			}
		};

		executor.execute(processor);
	}

	private void executeJsonMessage(final String msg) {
		Runnable processor = new Runnable() {

			@Override
			public void run() {
				setOutputText(msg);

				try {
					StringReader reader = new StringReader(msg);
					EntrainerStateMessage esm = jsonMapper.readValue(reader, EntrainerStateMessage.class);
					processMessage(esm);
				} catch (Exception e) {
					GuiUtil.handleProblem(e);
				}
			}
		};

		executor.execute(processor);
	}

}
