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
package net.sourceforge.entrainer.gui.socket;

import static net.sourceforge.entrainer.util.Utils.openBrowser;
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

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBException;

import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.guitools.MigHelper;
import net.sourceforge.entrainer.socket.EntrainerStateMessage;
import net.sourceforge.entrainer.socket.EntrainerStateMessageMarshal;
import net.sourceforge.entrainer.socket.WebSocketHandler;
import net.sourceforge.entrainer.xml.Settings;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

// TODO: Auto-generated Javadoc
/**
 * The Class EntrainerSocketConnector.
 */
@SuppressWarnings("serial")
public class EntrainerSocketConnector extends JFrame {

	private JTextArea output = new JTextArea(30, 50);
	private JToggleButton connect = new JToggleButton("Connect");
	private JButton cancel = new JButton("Cancel");
	private JButton clear = new JButton("Clear");

	private JRadioButton nioConnection = new JRadioButton("NIO Connection");
	private JRadioButton webSocketConnection = new JRadioButton("Web Socket Connection");

	private ButtonGroup connectionTypes = new ButtonGroup();

	private JSlider entrainmentFrequency = new JSlider(JSlider.HORIZONTAL, 0, 4000, 1000);
	private JSlider frequency = new JSlider(JSlider.HORIZONTAL, 20, 500, 200);
	private JSlider amplitude = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
	private JSlider pinkNoise = new JSlider(JSlider.HORIZONTAL, 0, 1000, 0);

	private DecimalFormat entrainmentFormat = new DecimalFormat("#0.00Hz");
	private DecimalFormat amplitudeFormat = new DecimalFormat("#0%");
	private DecimalFormat frequencyFormat = new DecimalFormat("##0Hz");

	private JLabel entrainmentValue = new JLabel();
	private JLabel frequencyValue = new JLabel();
	private JLabel amplitudeValue = new JLabel();
	private JLabel pinkNoiseValue = new JLabel();

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
		super("Entrainer Socket Connector");
		setIconImage(GuiUtil.getIcon("/Brain.png").getImage());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

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

		return "file://" + path + "doc/sockets.html";
	}

	private void initGui() {
		MigHelper mh = new MigHelper(getContentPane());

		mh.add(new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER))
				.addLast(getSliderPanel());
		mh.add(getButtonPanel());
	}

	private Container getSliderPanel() {
		MigHelper mh = new MigHelper();

		mh.setColumnGrowWeights(0, 0, 1).setColumnShrinkWeights(0, 0, 1);
		mh.setColumnGrowWeights(100, 2).setColumnShrinkWeights(100, 2).setLayoutFill(true);

		mh.gapY(10, 10).alignEast().add("Entrainment Frequency").alignCenter().add(entrainmentFrequency).alignWest()
				.setWidth(60).addLast(entrainmentValue);

		mh.gapY(10, 10).alignEast().add("Frequency").alignCenter().add(frequency).alignWest().addLast(frequencyValue);

		mh.gapY(10, 10).alignEast().add("Amplitude").alignCenter().add(amplitude).alignWest().addLast(amplitudeValue);

		mh.gapY(0, 0).alignEast().add("Pink Noise").alignCenter().add(pinkNoise).alignWest().addLast(pinkNoiseValue);

		return mh.getContainer();
	}

	private Component getButtonPanel() {
		MigHelper mh = new MigHelper();

		mh.add(getConnectionTypePanel()).add(connect).add(clear).add(cancel);

		return mh.getContainer();
	}

	private Component getConnectionTypePanel() {
		MigHelper mh = new MigHelper();

		connectionTypes.add(nioConnection);
		connectionTypes.add(webSocketConnection);

		nioConnection.setToolTipText("Connect to EntrainerFX using Java NIO Sockets");
		webSocketConnection.setToolTipText("Connect to EntrainerFX using Web Sockets");

		mh.alignWest().addLast(nioConnection).alignWest().add(webSocketConnection);
		nioConnection.setSelected(true);

		return mh.getContainer();
	}

	private void initListeners() {
		output.setEditable(false);
		output.setToolTipText("Displays messages received from Entrainer");

		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				GuiUtil.fadeOutAndDispose(EntrainerSocketConnector.this, 500);
			}
		});
		cancel.setToolTipText("Closes this Entrainer Socket Connector");

		connect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
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
		});

		connect.setToolTipText("Connects to Entrainer's external socket");

		clear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				output.setText("");
			}
		});
		clear.setToolTipText("Clears the messages");
	}

	private void connectionFailed() {
		connect.setSelected(false);
		enableConnectionTypes(true);
	}

	private void enableConnectionTypes(boolean enable) {
		nioConnection.setEnabled(enable);
		webSocketConnection.setEnabled(enable);
	}

	private boolean isActiveConnection() {
		return bootstrap != null && channel != null && channel.isActive();
	}

	private void wireSliders() {
		entrainmentFrequency.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				setEntrainmentFrequencyValue();
				if (isActiveConnection() && !isEntrainerEntrainmentFrequencyMessage) sendEntrainmentFrequencyChange();
				isEntrainerEntrainmentFrequencyMessage = false;
			}
		});
		setEntrainmentFrequencyValue();

		amplitude.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				setAmplitudeValue();
				if (isActiveConnection() && !isEntrainerAmplitudeMessage) sendAmplitudeChange();
				isEntrainerAmplitudeMessage = false;
			}
		});
		setAmplitudeValue();

		frequency.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				setFrequencyValue();
				if (isActiveConnection() && !isEntrainerFrequencyMessage) sendFrequencyChange();
				isEntrainerFrequencyMessage = false;
			}
		});
		setFrequencyValue();

		pinkNoise.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				setPinkNoiseValue();
				if (isActiveConnection() && !isEntrainerPinkNoiseAmplitudeMessage) sendPinkNoiseChange();
				isEntrainerPinkNoiseAmplitudeMessage = false;
			}
		});
		setPinkNoiseValue();
	}

	private void setFrequencyValue() {
		frequencyValue.setText(frequencyFormat.format(getFrequencyValue()));
	}

	private void setPinkNoiseValue() {
		pinkNoiseValue.setText(amplitudeFormat.format(getPinkNoiseValue()));
	}

	private void setAmplitudeValue() {
		amplitudeValue.setText(amplitudeFormat.format(getAmplitudeFromSlider()));
	}

	private void setEntrainmentFrequencyValue() {
		entrainmentValue.setText(entrainmentFormat.format(getEntrainmentFrequencyFromSlider()));
	}

	private void sendAmplitudeChange() {
		EntrainerStateMessage esm = new EntrainerStateMessage();
		esm.setAmplitude(getAmplitudeFromSlider());
		sendMessage(esm);
	}

	private void sendPinkNoiseChange() {
		EntrainerStateMessage esm = new EntrainerStateMessage();
		esm.setPinkNoiseAmplitude(getPinkNoiseValue());
		sendMessage(esm);
	}

	private void sendFrequencyChange() {
		EntrainerStateMessage esm = new EntrainerStateMessage();
		esm.setFrequency(getFrequencyValue());
		sendMessage(esm);
	}

	private void sendEntrainmentFrequencyChange() {
		EntrainerStateMessage esm = new EntrainerStateMessage();
		esm.setEntrainmentFrequency(getEntrainmentFrequencyFromSlider());
		sendMessage(esm);
	}

	private double getAmplitudeFromSlider() {
		return ((double) amplitude.getValue()) / 100;
	}

	private double getEntrainmentFrequencyFromSlider() {
		return ((double) entrainmentFrequency.getValue()) / 100;
	}

	private double getPinkNoiseValue() {
		return ((double) pinkNoise.getValue()) / 1000;
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
						WebSocketVersion.V13, null, false, customHeaders);
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
					remoteDisconnection();
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
				remoteDisconnection();
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
		if (esm.getAmplitude() != null && !amplitude.getValueIsAdjusting()) {
			setAutoAmplitude(esm.getAmplitude());
		}

		if (esm.getFrequency() != null && !frequency.getValueIsAdjusting()) {
			setAutoFrequency(esm.getFrequency());
		}

		if (esm.getEntrainmentFrequency() != null && !entrainmentFrequency.getValueIsAdjusting()) {
			setAutoEntrainment(esm.getEntrainmentFrequency());
		}

		if (esm.getPinkNoiseAmplitude() != null && !pinkNoise.getValueIsAdjusting()) {
			setAutoPinkNoise(esm.getPinkNoiseAmplitude());
		}
	}

	private void setAutoAmplitude(double value) {
		int val = new BigDecimal(value * 100).divide(BigDecimal.ONE, 0, RoundingMode.HALF_UP).intValue();
		if (amplitude.getValue() == value) return;
		isEntrainerAmplitudeMessage = true;
		amplitude.setValue(val);
	}

	private void setAutoPinkNoise(double value) {
		int val = new BigDecimal(value * 1000).divide(BigDecimal.ONE, 0, RoundingMode.HALF_UP).intValue();
		if (pinkNoise.getValue() == val) return;
		isEntrainerPinkNoiseAmplitudeMessage = true;
		pinkNoise.setValue(val);
	}

	private void setAutoFrequency(double value) {
		int val = new BigDecimal(value).divide(BigDecimal.ONE, 0, RoundingMode.HALF_UP).intValue();
		if (frequency.getValue() == val) return;
		isEntrainerFrequencyMessage = true;
		frequency.setValue(val);
	}

	private void setAutoEntrainment(double value) {
		double val = new BigDecimal(value * 100).divide(BigDecimal.ONE, 0, RoundingMode.HALF_UP).doubleValue();
		if (entrainmentFrequency.getValue() == val) return;
		isEntrainerEntrainmentFrequencyMessage = true;
		entrainmentFrequency.setValue((int) val);
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

	private void disconnectFromEntrainer() {
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
			output.setCaretPosition(0);
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
