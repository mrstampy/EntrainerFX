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

import static net.sourceforge.entrainer.mediator.MediatorConstants.MESSAGE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.START_ENTRAINMENT;
import static net.sourceforge.entrainer.util.Utils.openBrowser;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import net.sourceforge.entrainer.JavaVersionChecker;
import net.sourceforge.entrainer.esp.EspConnectionRegister;
import net.sourceforge.entrainer.gui.flash.CurrentEffect;
import net.sourceforge.entrainer.gui.flash.FlashOptions;
import net.sourceforge.entrainer.gui.jfx.AnimationPane;
import net.sourceforge.entrainer.gui.jfx.BackgroundPicturePane;
import net.sourceforge.entrainer.gui.jfx.EntrainerFXSplash;
import net.sourceforge.entrainer.gui.jfx.FlashOptionsPane;
import net.sourceforge.entrainer.gui.jfx.JFXUtils;
import net.sourceforge.entrainer.gui.jfx.MediaPlayerPane;
import net.sourceforge.entrainer.gui.jfx.ShimmerOptionsPane;
import net.sourceforge.entrainer.gui.jfx.SliderControlPane;
import net.sourceforge.entrainer.gui.jfx.SoundControlPane;
import net.sourceforge.entrainer.gui.jfx.animation.JFXAnimationRegister;
import net.sourceforge.entrainer.gui.jfx.animation.JFXAnimationWindow;
import net.sourceforge.entrainer.gui.jfx.animation.JFXEntrainerAnimation;
import net.sourceforge.entrainer.gui.jfx.shimmer.AbstractShimmer;
import net.sourceforge.entrainer.gui.jfx.shimmer.LinearShimmerRectangle;
import net.sourceforge.entrainer.gui.jfx.shimmer.ShimmerRegister;
import net.sourceforge.entrainer.gui.jfx.trident.ColorPropertyInterpolator;
import net.sourceforge.entrainer.gui.jfx.trident.LinearGradientInterpolator;
import net.sourceforge.entrainer.gui.jfx.trident.RadialGradientInterpolator;
import net.sourceforge.entrainer.gui.popup.NotificationWindow;
import net.sourceforge.entrainer.gui.socket.EntrainerSocketConnector;
import net.sourceforge.entrainer.guitools.GridPaneHelper;
import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;
import net.sourceforge.entrainer.neuroph.jfx.NeuralizerPane;
import net.sourceforge.entrainer.pausethread.EntrainerPausibleThread;
import net.sourceforge.entrainer.pausethread.PauseEvent;
import net.sourceforge.entrainer.socket.EntrainerSocketManager;
import net.sourceforge.entrainer.socket.InvalidPortNumberException;
import net.sourceforge.entrainer.sound.MasterLevelController;
import net.sourceforge.entrainer.sound.SoundControl;
import net.sourceforge.entrainer.sound.jsyn.JSynSoundControl;
import net.sourceforge.entrainer.sound.tools.EntrainmentFrequencyPulseNotifier;
import net.sourceforge.entrainer.sound.tools.Panner;
import net.sourceforge.entrainer.util.Utils;
import net.sourceforge.entrainer.xml.Settings;
import net.sourceforge.entrainer.xml.SleeperManager;
import net.sourceforge.entrainer.xml.SleeperManagerEvent;
import net.sourceforge.entrainer.xml.SleeperManagerListener;

import org.pushingpixels.trident.TridentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.mrstampy.esp.dsp.lab.DefaultLabValues;
import com.github.mrstampy.esp.dsp.lab.Lab;
import com.github.mrstampy.esp.dsp.lab.RawEspConnection;
import com.github.mrstampy.esp.multiconnectionsocket.ConnectionEvent;
import com.github.mrstampy.esp.multiconnectionsocket.ConnectionEventListener;
import com.github.mrstampy.esp.multiconnectionsocket.EspChannel;
import com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocketException;
import com.github.mrstampy.esplab.EspPowerLabWindow;

// TODO: Auto-generated Javadoc
/**
 * This class is the gui frontend to the Entrainer project.
 * 
 * @author burton
 */
public class EntrainerFX extends Application {
	private static final String MENU_CLEAR_ENTRAINER_FX_PROGRAM = "Clear EntrainerFX Program";
	private static final String MENU_LOAD_ENTRAINER_FX_PROGRAM = "Load EntrainerFX Program";
	private static final String MENU_EDIT_ENTRAINER_FX_PROGRAM = "Edit EntrainerFX Program";
	private static final String MENU_NEW_ENTRAINER_FX_PROGRAM = "New EntrainerFX Program";
	private static final int MIN_HEIGHT = 950;
	private static final Logger log = LoggerFactory.getLogger(EntrainerFX.class);

	// JSyn classes
	private SoundControl control;

	// Button controls
	private SoundControlPane soundControlPane = new SoundControlPane();
	private SliderControlPane sliderControlPane = new SliderControlPane();

	private IntervalMenu intervalMenu;

	private SleeperManager sleeperManager;

	private EntrainerPausibleThread sleeperManagerThread;

	private static EntrainerFX instance;

	private Settings settings = Settings.getInstance();

	private EntrainerSocketManager socket;

	private boolean entrainerProgramInitialized;

	private Sender sender = new SenderAdapter();

	private List<String> intervalCache = new ArrayList<String>();

	private JFXAnimationWindow animationWindow;

	private CheckMenuItem connect;

	private final ImageIcon icon = GuiUtil.getIcon("/Brain.png");

	private AbstractShimmer<?> shimmer = ShimmerRegister.getShimmer(LinearShimmerRectangle.NAME);

	private GridPane gp;

	private EntrainerBackground background = new EntrainerBackground();
	private AnimationPane animations;
	private BackgroundPicturePane pictures = new BackgroundPicturePane();

	private Group group;
	private Scene scene;
	private Stage stage;

	private ShimmerOptionsPane shimmerOptions = new ShimmerOptionsPane();
	private Lab lab;
	private NeuralizerPane neuralizer = new NeuralizerPane();
	private FlashOptionsPane flashOptions = new FlashOptionsPane();

	@SuppressWarnings("unused")
	private MasterLevelController masterLevelController;

	private Menu espDevices;
	private MenuItem startEspDevice;
	private MenuItem stopEspDevice;
	private MenuItem showEspLab;
	private MenuItem saveEspLab;
	private MenuItem loadEspLab;
	private MenuItem chooseChannel;

	private EspConnectionListener espConnectionListener = new EspConnectionListener();

	private ObjectMapper jsonMapper = new ObjectMapper();
	private CheckMenuItem splashOnStartup;

	private MediaPlayerPane audioPlayerPane = new MediaPlayerPane();

	private boolean enableMediaEntrainment;
	private boolean flashEFX;
	private MenuBar bar;
	private EntrainerFXResizer resizer;
	private boolean exiting;

	/**
	 * Instantiates a new entrainer fx.
	 *
	 * @throws Exception
	 *           the exception
	 */
	public EntrainerFX() throws Exception {
		EntrainmentFrequencyPulseNotifier.start();
		FlashOptions.start();

		group = new Group();
		scene = new Scene(group, Color.BLACK);

		resizer = new EntrainerFXResizer(r -> resizeDimensions(r));

		jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);

		instance = this;
	}

	/**
	 * Gets the single instance of Entrainer.
	 *
	 * @return single instance of Entrainer
	 */
	public static synchronized EntrainerFX getInstance() {
		return instance;
	}

	/**
	 * Gets the bounds.
	 *
	 * @return the bounds
	 */
	public Rectangle2D getBounds() {
		return resizer.getSize();
	}

	/**
	 * Checks if is visible.
	 *
	 * @return true, if is visible
	 */
	public boolean isVisible() {
		return stage.isShowing();
	}

	/**
	 * To front.
	 */
	public void toFront() {
		stage.toFront();
	}

	/**
	 * To back.
	 */
	public void toBack() {
		stage.toBack();
	}

	/**
	 * Gets the min size.
	 *
	 * @return the min size
	 */
	public Rectangle2D getMinSize() {
		return new Rectangle2D(stage.getX(), stage.getY(), stage.getMinWidth(), stage.getMinHeight());
	}

	/**
	 * Gets the stage.
	 *
	 * @return the stage
	 */
	public Stage getStage() {
		return stage;
	}

	private void fireReceiverChangeEvent(boolean value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	private void displayComponents() {
		gp.setVisible(true);

		JFXUtils.runLater(() -> initAnimationWindow());
		initSocket();
		initSettings();
		SwingUtilities.invokeLater(() -> addSystemTrayIcon());
		setMessage("Started Entrainer");
	}

	private void initAnimationWindow() {
		animationWindow = new JFXAnimationWindow();
		animationWindow.initGui();
	}

	private void setShimmerSizes(Rectangle2D size) {
		for (AbstractShimmer<?> shimmer : ShimmerRegister.getShimmers()) {
			shimmer.setWidth(size.getWidth());
			shimmer.setHeight(size.getHeight());
		}
	}

	private void scaleSize() {
		Dimension screen = GuiUtil.getWorkingScreenSize();
		int height = MIN_HEIGHT > screen.getHeight() ? (int) (screen.getHeight() - 50) : MIN_HEIGHT;

		gp.setPrefHeight(height);
		stage.setHeight(height);

		Rectangle2D r = new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
		resizer.setSize(r);
	}

	private void unexpandTitledPanes() {
		unexpandeTitledPane(sliderControlPane);
		unexpandeTitledPane(animations);
		unexpandeTitledPane(shimmerOptions);
		unexpandeTitledPane(neuralizer);
		unexpandeTitledPane(pictures);
		unexpandeTitledPane(flashOptions);
		unexpandeTitledPane(audioPlayerPane);

		gp.setMinSize(scene.getWidth(), scene.getHeight() / 2);
	}

	private void setJFXSize(Rectangle2D size) {
		double width = size.getWidth();

		gp.setPrefSize(width, size.getHeight());

		setTitledPaneWidth(sliderControlPane, width);
		setTitledPaneWidth(animations, width);
		setTitledPaneWidth(shimmerOptions, width);
		setTitledPaneWidth(flashOptions, width);
		setTitledPaneWidth(neuralizer, width);
		setTitledPaneWidth(audioPlayerPane, width);
		setTitledPaneWidth(pictures, width);

		setShimmerSizes(size);

		setAnimationPosition(size);

		background.setDimension(size.getWidth(), size.getHeight());
	}

	private void setAnimationPosition(Rectangle2D size) {
		for (JFXEntrainerAnimation animation : JFXAnimationRegister.getEntrainerAnimations()) {
			animation.setEntrainerFramePosition(size);
		}
	}

	private void setTitledPaneWidth(TitledPane tp, double width) {
		tp.setPrefWidth(width);
	}

	private void unexpandeTitledPane(TitledPane tp) {
		tp.setExpanded(false);
		tp.setOpacity(0);
	}

	private void addSystemTrayIcon() {
		if (!SystemTray.isSupported()) return;

		TrayIcon icon = new TrayIcon(this.icon.getImage());
		icon.setPopupMenu(getTrayIconPopup());
		icon.setToolTip("EntrainerFX");

		try {
			SystemTray.getSystemTray().add(icon);
		} catch (AWTException e) {
			GuiUtil.handleProblem(e);
		}
	}

	private PopupMenu getTrayIconPopup() {
		PopupMenu pop = new PopupMenu("EntrainerFX");

		java.awt.MenuItem start = new java.awt.MenuItem("Start EntrainerFX");
		start.addActionListener(e -> trayStart(true));

		pop.add(start);

		java.awt.MenuItem stop = new java.awt.MenuItem("Stop EntrainerFX");
		stop.addActionListener(e -> trayStart(false));

		pop.add(stop);

		java.awt.MenuItem exit = new java.awt.MenuItem("Exit");
		exit.addActionListener(e -> JFXUtils.runLater(() -> exitPressed()));

		pop.add(exit);

		return pop;
	}

	private void trayStart(boolean b) {
		fireReceiverChangeEvent(b, START_ENTRAINMENT);

		if (b) {
			JFXUtils.runLater(() -> playPressed());
		} else {
			JFXUtils.runLater(() -> stopPressed());
		}
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addSender(sender);
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this, true) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case START_ENTRAINMENT:
					if (e.getSource() == soundControlPane) break;
					soundControlPane.setPlaying(e.getBooleanValue());
					if (e.getBooleanValue()) {
						playPressed();
					} else {
						stopPressed();
					}
					break;
				case MEDIA_ENTRAINMENT:
					enableMediaEntrainment = e.getBooleanValue();
					break;
				case SHIMMER_RECTANGLE:
					setShimmerRectangle(e.getStringValue());
					break;
				case ESP_CONNECTIONS_RELOADED:
					espDevices.getItems().clear();
					JFXUtils.runLater(() -> addEspDevices(espDevices));
					break;
				case SPLASH_ON_STARTUP:
					JFXUtils.runLater(() -> splashOnStartup.setSelected(e.getBooleanValue()));
					break;
				case APPLY_FLASH_TO_ENTRAINER_FX:
					applyFlashEvent(e.getBooleanValue());
					break;
				case FLASH_EFFECT:
					flashEFX(e.getEffect());
					break;
				default:
					break;

				}
			}

		});
	}

	private void flashEFX(CurrentEffect effect) {
		if (!flashEFX) return;

		JFXUtils.setEffect(gp, effect);
	}

	private void applyFlashEvent(boolean b) {
		flashEFX = b;
		if (!flashEFX) {
			JFXUtils.resetEffects(gp);
		}
	}

	private void setEspMenuItemsEnabled(boolean booleanValue) {
		espDevices.setDisable(booleanValue);
		startEspDevice.setDisable(booleanValue);
		stopEspDevice.setDisable(!booleanValue);
	}

	private void setShimmerRectangle(final String stringValue) {
		if (stringValue == null || stringValue.isEmpty()) return;

		final AbstractShimmer<?> shimmer = ShimmerRegister.getShimmer(stringValue);
		if (shimmer == null) return;

		JFXUtils.runLater(() -> swapShimmers(shimmer));
	}

	private void swapShimmers(final AbstractShimmer<?> shimmer) {
		if (group.getChildren().isEmpty()) return;
		AbstractShimmer<?> old = (AbstractShimmer<?>) group.getChildren().get(1);
		if (old == shimmer) return;
		try {
			group.getChildren().remove(old);
			old.stop();
			old.setInUse(false);
			shimmer.setInUse(true);
			group.getChildren().add(1, shimmer);
			if (control.isPlaying()) shimmer.start();
		} catch (Throwable e) {
			GuiUtil.handleProblem(e);
		}
	}

	private void initSettings() {
		if (settings.getXmlProgram() != null && !settings.getXmlProgram().isEmpty() && stage.isShowing()) {
			readXmlFile(settings.getXmlProgram());
		} else {
			enableControls(true);
		}
	}

	private void initSocket() {
		if (socket == null) {
			socket = new EntrainerSocketManager();

			if (settings.isSocketConnected()) {
				bindSocket();
				if (isSocketBound()) connect.setSelected(true);
			}
		}
	}

	private void addMenu() {
		bar = new MenuBar();

		Menu menu = new Menu("File");
		addMnemonic(menu, KeyCode.F);

		menu.getItems().add(getStartItem());
		menu.getItems().add(getStopItem());
		menu.getItems().add(new SeparatorMenuItem());
		menu.getItems().add(getLoadXmlItem());
		menu.getItems().add(getClearXmlItem());
		menu.getItems().add(getEditXmlItem());
		menu.getItems().add(getNewXmlItem());
		menu.getItems().add(new SeparatorMenuItem());
		menu.getItems().add(getExitItem());
		bar.getMenus().add(menu);

		bar.getMenus().add(intervalMenu);

		bar.getMenus().add(getEspMenu());

		bar.getMenus().add(getSocketMenu());

		Menu help = new Menu("Help");
		addMnemonic(help, KeyCode.H);

		help.getItems().add(getAboutItem());
		help.getItems().add(getLicenseItem());
		help.getItems().add(getLocalDocItem());
		help.getItems().add(getRemoteDocItem());
		help.getItems().add(getSplashItem());
		help.getItems().add(getSplashOnStartupItem());
		bar.getMenus().add(help);

		bar.setOnMouseEntered(e -> fade(true, bar));
		bar.setOnMouseExited(e -> fade(false, bar));
		bar.setOpacity(0);
	}

	private void fade(boolean b, Region c) {
		FadeTransition ft = new FadeTransition(Duration.millis(500), c);
		ft.setFromValue(c.getOpacity());
		ft.setToValue(b ? 0.75 : 0);
		ft.play();
	}

	private MenuItem getSplashOnStartupItem() {
		splashOnStartup = new CheckMenuItem("Splash on Startup");

		splashOnStartup.setOnAction(e -> enableSplashOnStartup(splashOnStartup.isSelected()));

		return splashOnStartup;
	}

	private void enableSplashOnStartup(boolean selected) {
		fireReceiverChangeEvent(selected, MediatorConstants.SPLASH_ON_STARTUP);
	}

	private MenuItem getRemoteDocItem() {
		MenuItem item = new MenuItem("Web Documentation");
		addMnemonic(item, KeyCode.W);
		item.setOnAction(e -> openBrowser("http://entrainer.sourceforge.net"));

		return item;
	}

	private MenuItem getLocalDocItem() {
		MenuItem item = new MenuItem("Local Documentation");
		addMnemonic(item, KeyCode.D);
		item.setOnAction(e -> Utils.openLocalDocumentation("index.html"));

		return item;
	}

	private MenuItem getLicenseItem() {
		MenuItem aboutItem = new MenuItem("License");
		addMnemonic(aboutItem, KeyCode.I);
		aboutItem.setOnAction(e -> showLicenseDialog());

		return aboutItem;
	}

	private void showLicenseDialog() {
		Dialog<ButtonType> d = new Dialog<ButtonType>();
		d.setDialogPane(new License());
		d.setTitle("EntrainerFX License");
		d.initOwner(stage);

		d.showAndWait();
	}

	private MenuItem getEditXmlItem() {
		MenuItem item = new MenuItem(MENU_EDIT_ENTRAINER_FX_PROGRAM);
		addMnemonic(item, KeyCode.E);
		item.setOnAction(e -> editXml());

		return item;
	}

	private MenuItem getNewXmlItem() {
		MenuItem item = new MenuItem(MENU_NEW_ENTRAINER_FX_PROGRAM);
		addMnemonic(item, KeyCode.N);
		item.setOnAction(e -> newXml());

		return item;
	}

	private MenuItem getLoadXmlItem() {
		MenuItem item = new MenuItem(MENU_LOAD_ENTRAINER_FX_PROGRAM);
		addMnemonic(item, KeyCode.L);
		item.setOnAction(e -> loadXml());

		return item;
	}

	private MenuItem getClearXmlItem() {
		MenuItem item = new MenuItem(MENU_CLEAR_ENTRAINER_FX_PROGRAM);
		addMnemonic(item, KeyCode.C);
		item.setOnAction(e -> clearXmlFile());

		return item;
	}

	private void addMnemonic(MenuItem item, KeyCode code) {
		item.setAccelerator(new KeyCodeCombination(code, KeyCodeCombination.META_DOWN));
	}

	private MenuItem getAboutItem() {
		MenuItem aboutItem = new MenuItem("About EntrainerFX");
		addMnemonic(aboutItem, KeyCode.B);
		aboutItem.setOnAction(e -> showAboutDialog());

		return aboutItem;
	}

	private void showAboutDialog() {
		Dialog<ButtonType> d = new Dialog<ButtonType>();
		d.setDialogPane(new About());
		d.setTitle("About EntrainerFX");
		d.initOwner(stage);

		d.showAndWait();
	}

	private MenuItem getSplashItem() {
		MenuItem splashItem = new MenuItem("Show Splash Screen");
		addMnemonic(splashItem, KeyCode.P);
		splashItem.setOnAction(e -> new EntrainerFXSplash());

		return splashItem;
	}

	private MenuItem getExitItem() {
		MenuItem exitItem = new MenuItem("Exit");
		addMnemonic(exitItem, KeyCode.X);
		exitItem.setOnAction(e -> exitPressed());

		return exitItem;
	}

	private MenuItem getStopItem() {
		MenuItem stopItem = new MenuItem("Stop");
		addMnemonic(stopItem, KeyCode.T);
		stopItem.setOnAction(e -> soundControlPane.getStop().fire());

		return stopItem;
	}

	private MenuItem getStartItem() {
		MenuItem startItem = new MenuItem("Start");
		addMnemonic(startItem, KeyCode.S);
		startItem.setOnAction(e -> soundControlPane.getPlay().fire());

		return startItem;
	}

	private Menu getSocketMenu() {
		Menu menu = new Menu("External Sockets");
		addMnemonic(menu, KeyCode.R);

		menu.getItems().add(getChangePortItem());
		menu.getItems().add(getConnectorGuiItem());
		menu.getItems().add(getConnectSocketItem());

		return menu;
	}

	private Menu getEspMenu() {
		Menu menu = new Menu("EEG Signal Processing");
		addMnemonic(menu, KeyCode.G);

		menu.getItems().add(getEspDeviceMenu());
		menu.getItems().add(new SeparatorMenuItem());
		menu.getItems().add(chooseChannelMenu());
		menu.getItems().add(new SeparatorMenuItem());
		menu.getItems().add(getStartEspMenu());
		menu.getItems().add(getStopEspMenu());
		menu.getItems().add(new SeparatorMenuItem());
		menu.getItems().add(loadLabMenu());
		menu.getItems().add(saveLabMenu());
		menu.getItems().add(new SeparatorMenuItem());
		menu.getItems().add(showEspLabMenu());

		setEspMenuItemsEnabled();

		return menu;
	}

	private MenuItem chooseChannelMenu() {
		chooseChannel = new MenuItem("Choose Channel");

		chooseChannel.setOnAction(e -> chooseChannel());

		return chooseChannel;
	}

	private void chooseChannel() {
		ChoiceDialog<EspChannel> cd = new ChoiceDialog<EspChannel>(null, lab.getConnection().getChannels());
		cd.setTitle("Choose Channel");
		cd.setHeaderText("Choose the channel for processing");
		cd.initOwner(stage);

		Optional<EspChannel> channel = cd.showAndWait();

		if (channel.isPresent()) lab.setChannel(channel.get().getChannelNumber());
	}

	private MenuItem loadLabMenu() {
		loadEspLab = new MenuItem("Load ESP Lab Settings");

		loadEspLab.setOnAction(e -> loadLabSettings());

		return loadEspLab;
	}

	private void loadLabSettings() {
		FileChooser chooser = getLabFileChooser("Load");

		File labfile = chooser.showOpenDialog(null);
		if (labfile != null) loadLab(labfile);
	}

	private void loadLab(File labfile) {
		try {
			DefaultLabValues values = jsonMapper.readValue(labfile, DefaultLabValues.class);
			lab.setLabValues(values);
		} catch (Exception e) {
			GuiUtil.handleProblem(e);
		}
	}

	private MenuItem saveLabMenu() {
		saveEspLab = new MenuItem("Save ESP Lab Settings");

		saveEspLab.setOnAction(e -> showSaveLabSettings());

		return saveEspLab;
	}

	private void showSaveLabSettings() {
		FileChooser chooser = getLabFileChooser("Save");

		File labfile = chooser.showSaveDialog(null);
		if (labfile != null) saveLab(labfile);
	}

	private FileChooser getLabFileChooser(String operation) {
		File labdir = getLabDir();
		FileChooser chooser = new FileChooser();

		chooser.setInitialDirectory(labdir);
		chooser.setTitle(operation + " ESP Lab Settings");
		chooser.getExtensionFilters().add(new ExtensionFilter("ESP Lab Files", "*.lab"));
		return chooser;
	}

	private File getLabDir() {
		File labdir = new File("esp.lab");
		if (!labdir.exists()) labdir.mkdir();
		return labdir;
	}

	private void saveLab(File labfile) {
		try {
			jsonMapper.writeValue(labfile, lab.getLabValues());
		} catch (Exception e) {
			GuiUtil.handleProblem(e);
		}
	}

	private void setEspMenuItemsEnabled() {
		boolean disable = lab == null || lab.getConnection() == null;
		startEspDevice.setDisable(disable);
		stopEspDevice.setDisable(false);
		showEspLab.setDisable(disable);
		saveEspLab.setDisable(disable);
		loadEspLab.setDisable(disable);
		chooseChannel.setDisable(!disable ? lab.getNumChannels() <= 1 : true);
	}

	private MenuItem showEspLabMenu() {
		showEspLab = new MenuItem("Show ESP Lab");

		showEspLab.setOnAction(e -> showEspLab());

		return showEspLab;
	}

	private void showEspLab() {
		if (!connectionCheck()) return;
		EspPowerLabWindow espLab = new EspPowerLabWindow(lab);
		espLab.addEventFilter(javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST, e -> setEspLabShowingEnabled(true));
		setEspLabShowingEnabled(false);
		espLab.show();
	}

	private void setEspLabShowingEnabled(boolean enabled) {
		espDevices.setDisable(!enabled);
		showEspLab.setDisable(!enabled);
	}

	private MenuItem getStopEspMenu() {
		stopEspDevice = new MenuItem("Stop ESP Device");

		stopEspDevice.setOnAction(e -> stopEspDevice());

		return stopEspDevice;
	}

	private void stopEspDevice() {
		if (!connectionCheck()) return;

		if (!lab.getConnection().isConnected()) return;

		JFXUtils.runLater(() -> lab.getConnection().stop());
	}

	private MenuItem getStartEspMenu() {
		startEspDevice = new MenuItem("Start ESP Device");

		startEspDevice.setOnAction(e -> startEspDevice());

		return startEspDevice;
	}

	private void startEspDevice() {
		if (!connectionCheck()) return;

		if (lab.getConnection().isConnected()) return;

		JFXUtils.runLater(() -> {
			try {
				lab.getConnection().start();
			} catch (MultiConnectionSocketException e) {
				GuiUtil.handleProblem(e);
			}
		});
	}
	
	private void setProgramItemsDisabled(boolean b) {
		boolean disable = b || soundControlPane.isPlayingEntrainerProgram() || soundControlPane.isRecordingEntrainerProgram();
		getFileMenuItem(MENU_NEW_ENTRAINER_FX_PROGRAM).setDisable(disable);
		getFileMenuItem(MENU_EDIT_ENTRAINER_FX_PROGRAM).setDisable(disable);
		getFileMenuItem(MENU_LOAD_ENTRAINER_FX_PROGRAM).setDisable(disable);
	}

	private boolean connectionCheck() {
		if (lab.getConnection() == null) {
			Alert alert = new Alert(AlertType.WARNING, "Choose an ESP device first", ButtonType.OK);
			alert.setHeaderText("No ESP Device Selected");
			alert.setTitle("No ESP Device Selected");
			alert.initOwner(stage);
			alert.showAndWait();
			return false;
		}

		return true;
	}

	private Menu getEspDeviceMenu() {
		espDevices = new Menu("Choose ESP Device");

		EspConnectionRegister.getEspConnections();

		return espDevices;
	}

	private void addEspDevices(Menu menu) {
		List<RawEspConnection> connections = EspConnectionRegister.getEspConnections();
		ToggleGroup bg = new ToggleGroup();
		connections.forEach(connection -> addEspDevice(menu, connection, bg));
	}

	private void addEspDevice(Menu menu, RawEspConnection connection, ToggleGroup bg) {
		RadioMenuItem item = new RadioMenuItem(connection.getName());

		item.setToggleGroup(bg);
		menu.getItems().add(item);

		item.setOnAction(e -> setConnection(connection));
	}

	private void setConnection(RawEspConnection connection) {
		EspConnectionRegister.getEspConnection(connection.getName());
		setLabFrom(connection);
		lab.setConnection(connection);
		setEspMenuItemsEnabled();
		setEspMenuItemText(connection.getName());
		connection.addConnectionEventListener(espConnectionListener);
	}

	private void setLabFrom(RawEspConnection connection) {
		Lab connectionLab = connection.getDefaultLab();

		connectionLab.setNumBands(41);

		lab = connectionLab;

		neuralizer.setLab(lab);
	}

	private void setEspMenuItemText(String name) {
		startEspDevice.setText("Start " + name);
		stopEspDevice.setText("Stop " + name);
	}

	private MenuItem getConnectorGuiItem() {
		MenuItem item = new MenuItem("Show Connector GUI");

		item.setOnAction(e -> showConnectorGui());

		return item;
	}

	private void showConnectorGui() {
		try {
			EntrainerSocketConnector esc = new EntrainerSocketConnector(settings.getSocketIPAddress(),
					settings.getSocketPort());
			Dialog<ButtonType> d = new Dialog<>();
			d.setDialogPane(esc);
			d.setTitle("EntrainerFX Socket Connector");
			d.initModality(Modality.NONE);
			d.initOwner(stage);
			d.setOnHiding(e -> esc.disconnectFromEntrainer());
			d.show();
		} catch (UnknownHostException e) {
			GuiUtil.handleProblem(e);
		}
	}

	private MenuItem getChangePortItem() {
		MenuItem item = new MenuItem("Choose Socket Host & Port");

		item.setOnAction(e -> showSocketPortDialog());

		return item;
	}

	private MenuItem getConnectSocketItem() {
		connect = new CheckMenuItem("Accept Connections");

		connect.setOnAction(e -> connectClicked());

		return connect;
	}

	private void connectClicked() {
		if (connect.isSelected()) {
			bindSocket();
		} else {
			unbindSocket();
		}
	}

	private boolean isSocketBound() {
		return socket.isBound();
	}

	private void unbindSocket() {
		int portNum = socket.getPortNumber();
		String hostName = socket.getHostName();
		socket.unbind();
		new NotificationWindow("EntrainerFX socket unbound from host " + hostName + " and port " + portNum);
		settings.setSocketConnected(false);
	}

	private void bindSocket() {
		if (settings.getSocketPort() <= 0) showSocketPortDialog();
		try {
			socket.bind();
			new NotificationWindow("EntrainerFX socket bound to host " + socket.getHostName() + " and port "
					+ socket.getPortNumber());
			settings.setSocketConnected(true);
		} catch (IOException e) {
			GuiUtil.handleProblem(e);
			connect.setSelected(false);
		} catch (InvalidPortNumberException e) {
			Alert alert = new Alert(AlertType.ERROR, "The port number " + e.getPort() + " is not valid", ButtonType.OK);
			alert.setHeaderText("Invalid Port Number");
			alert.setTitle("Invalid Port Number");
			alert.initOwner(stage);
			alert.showAndWait();
			connect.setSelected(false);
		}
	}

	private void showSocketPortDialog() {
		SocketPortDialog spd = null;
		try {
			spd = new SocketPortDialog();
		} catch (UnknownHostException e) {
			log.error("Unexpected exception", e);
			return;
		}

		Dialog<ButtonType> socker = new Dialog<ButtonType>();
		socker.setDialogPane(spd);
		socker.setTitle("Choose Host and Port");
		socker.initOwner(stage);
		Optional<ButtonType> bt = socker.showAndWait();

		if (bt.isPresent() && bt.get() == ButtonType.OK) {
			spd.validateAndSave();
		}
	}

	private void editXml() {
		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(Utils.getEntrainerProgramDir().get());
		fc.setSelectedExtensionFilter(new ExtensionFilter("EntrainerFX Programs", "xml"));
		fc.setTitle("EntrainerFX Programs");

		File f = fc.showOpenDialog(stage);
		if (f == null) return;

		showXmlEditor(f);
	}

	private void showXmlEditor(File f) {
		settings.setPreserveState(true);
		intervalCache = intervalMenu.removeAllIntervals();

		XmlEditor editor = new XmlEditor(stage, f);
		
		editor.addXmlFileSaveListener(e -> xmlFileSaved(e.getXmlFile()));

		editor.setOnHiding(e -> resetIntervalCache());
		editor.setOpacity(0);

		Timeline tl = new Timeline(new KeyFrame(Duration.seconds(1), new KeyValue(editor.opacityProperty(), 1)));

		tl.play();
		editor.show();

		settings.setPreserveState(false);
	}

	private void resetIntervalCache() {
		intervalMenu.loadIntervals(intervalCache);
		intervalMenu.loadCustomIntervals();
	}

	private void xmlFileSaved(File xmlFile) {
		if (sleeperManager != null && !soundControlPane.getPlay().isDisabled()
				&& sleeperManager.getXml().getFile().equals(xmlFile.getAbsoluteFile())) {
			readXmlFile(xmlFile.getName());
		}
	}

	private void newXml() {
		showXmlEditor(null);
	}

	private void wireButtons() {
		soundControlPane.getPlay().setOnAction(e -> playPressed());
		soundControlPane.getStop().setOnAction(e -> stopPressed());
		soundControlPane.getPause().setOnAction(e -> pausePressed());
		soundControlPane.getRecord().setOnAction(e -> recordClicked());
	}

	private void recordClicked() {
		if (soundControlPane.getRecord().isSelected()) {
			boolean recording = showWavFileChooser();
			recordClicked(recording);
		} else {
			control.setWavFile(null);
			setMessage("Recording cancelled");
			soundControlPane.setRecordingEntrainerProgram(false);
			control.setRecord(false);
		}
	}

	private void recordClicked(boolean recording) {
		soundControlPane.getRecord().setSelected(recording);
		control.setRecord(recording);
		if (control.getWavFile() != null) {
			String msg = "Prepared to record to " + control.getWavFile().getName();
			setMessage(msg);
			soundControlPane.setRecordingEntrainerProgram(true);
		}
	}

	private boolean showWavFileChooser() {
		TextInputDialog in = new TextInputDialog("EntrainerFX-recording.wav");

		in.setTitle("EntrainerFX Recording File Name");
		in.setHeaderText("Enter the name of the recording output file");
		in.initOwner(stage);

		Optional<String> name = in.showAndWait();

		if (name.isPresent()) {
			File f = processFile(new File(Utils.getRecordingDir().get(), name.get()));

			if (f.exists()) {
				Alert alert = new Alert(AlertType.CONFIRMATION, f.getAbsolutePath() + " already exists; overwrite?",
						ButtonType.OK, ButtonType.CANCEL);
				alert.setTitle("Recording File Exists");
				alert.initOwner(stage);
				Optional<ButtonType> button = alert.showAndWait();
				if (!button.isPresent() || button.get() != ButtonType.OK) {
					control.setWavFile(null);
					return false;
				}
			}

			if (!isValidFile(f)) {
				Alert alert = new Alert(AlertType.ERROR, f.getName()
						+ " is not a valid WAV file\n(It must end with a '.wav' extension')", ButtonType.OK);
				alert.setTitle("Invalid Recording File");
				alert.initOwner(stage);
				alert.showAndWait();
				control.setWavFile(null);
				return false;
			}

			control.setWavFile(f);
			return true;
		}

		control.setWavFile(null);
		return false;
	}

	private File processFile(File selected) {
		if (isUntypedFile(selected)) {
			return new File(selected.getAbsolutePath() + ".wav");
		}

		return selected;
	}

	private boolean isUntypedFile(File f) {
		return f.getName().indexOf(".") == -1;
	}

	private boolean isValidFile(File f) {
		return f.getName().indexOf(".wav") == f.getName().length() - 4;
	}

	private void stopPressed() {
		if (isPaused()) return;
		setMessage(control.isRecord() ? "Recording Stopped" : "Stopped");
		stopImpl();
	}

	private boolean isPaused() {
		return soundControlPane.getPause().isSelected();
	}

	private void playPressed() {
		if (animations.getRunAnimation().isSelected()) startAnimation();

		start();

		if (!animations.getRunAnimation().isSelected()) {
			setMessage(control.isRecord() ? "Recording Started" : "Entrainment Started");
		}

		if (enableMediaEntrainment) fireReceiverChangeEvent(true, MediatorConstants.MEDIA_PLAY);
	}

	private void startAnimation() {
		if (animationWindow.isShowing()) {
			return;
		}

		if (animationWindow.getEntrainerAnimation() == null) {
			animationWindow.setEntrainerAnimation(JFXAnimationRegister.getEntrainerAnimations().get(0));
		}
	}

	private void pausePressed() {
		pauseEventPerformed();
		if (soundControlPane.getPause().isSelected()) {
			pause();
			setMessage("Paused");
		} else {
			resume();
			setMessage("Resumed");
		}
	}

	private void pauseEventPerformed() {
		Thread pauseThread = new Thread() {
			public void run() {
				PauseEvent e = new PauseEvent(this, soundControlPane.getPause().isSelected() ? PauseEvent.PAUSE
						: PauseEvent.RESUME);
				sleeperManagerThread.pauseEventPerformed(e);
			}
		};

		pauseThread.start();
	}

	private void initializeControls() {
		if (!entrainerProgramInitialized) {
			sleeperManager.initGlobalSettings();
			entrainerProgramInitialized = true;
			soundControlPane.setPlayingEntrainerProgram(true);
		}
	}

	private void enableControls(final boolean enabled) {
		JFXUtils.runLater(() -> setPanesDisabled(!enabled));

		getFileMenuItem(MENU_NEW_ENTRAINER_FX_PROGRAM).setDisable(!enabled);
		getFileMenuItem(MENU_EDIT_ENTRAINER_FX_PROGRAM).setDisable(!enabled);
		getFileMenuItem(MENU_CLEAR_ENTRAINER_FX_PROGRAM).setDisable(enabled);
		getFileMenuItem(MENU_LOAD_ENTRAINER_FX_PROGRAM).setDisable(!enabled);
	}

	private void setPanesDisabled(boolean b) {
		sliderControlPane.setControlsDisabled(b);
		audioPlayerPane.setControlsDisabled(b);
	}

	private MenuItem getFileMenuItem(String text) {
		Menu file = bar.getMenus().get(0);

		List<MenuItem> comps = file.getItems();
		for (MenuItem jmi : comps) {
			if (jmi instanceof MenuItem) {
				if (text.equals(jmi.getText())) {
					return jmi;
				}
			}
		}

		return null;
	}

	private void exitPressed() {
		if (exiting) return;

		exiting = true;
		stopPressed();

		Alert alert = new Alert(AlertType.CONFIRMATION, "Exiting: Confirm?", ButtonType.OK, ButtonType.CANCEL);
		alert.setTitle("Exiting EntrainerFX");
		alert.setHeaderText("Exit Confirmation");
		alert.initOwner(stage);

		Optional<ButtonType> button = alert.showAndWait();
		if (button.isPresent() && button.get() == ButtonType.OK) {
			control.exit();

			JFXUtils.runLater(() -> shutdownAnimations());

			exitApplication();
		} else {
			exiting = false;
		}
	}

	private void shutdownAnimations() {
		if (animationWindow.isShowing()) animationWindow.setVisible(false);
	}

	private void exitApplication() {
		Timeline tl = new Timeline(new KeyFrame(Duration.millis(1500), new KeyValue(stage.opacityProperty(), 0)));
		tl.setOnFinished(e -> System.exit(0));
		tl.play();
	}

	private void start() {
		if (isXmlProgram()) {
			initializeControls();
			sleeperManagerThread = sleeperManager.start();
		}
		setProgramItemsDisabled(true);
	}

	private void resume() {
		control.resume();
	}

	private boolean isXmlProgram() {
		return sleeperManager != null;
	}

	private void createPanner() {
		Panner.getInstance();
	}

	private void stopImpl() {
		if (isXmlProgram()) {
			sleeperManager.stop();
		}
		entrainerProgramInitialized = false;
		setProgramItemsDisabled(false);
	}

	private void pause() {
		control.pause();
	}

	private void resizeDimensions(Rectangle2D r) {
		if (stage.getX() != r.getMinX()) {
			stage.setX(r.getMinX());
		}

		if (stage.getY() != r.getMinY()) {
			stage.setY(r.getMinY());
		}

		if (stage.getWidth() != r.getWidth()) {
			stage.setWidth(r.getWidth());
		}

		if (stage.getHeight() != r.getHeight()) {
			stage.setHeight(r.getHeight());
		}

		setJFXSize(r);
	}

	private void setMessage(String s) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, s, MESSAGE));
	}

	private void loadXml() {
		settings.setPreserveState(true);
		intervalMenu.removeAllIntervals();

		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(Utils.getEntrainerProgramDir().get());
		fc.setSelectedExtensionFilter(new ExtensionFilter("EntrainerFX Programs", "xml"));
		fc.setTitle("EntrainerFX Programs");

		File f = fc.showOpenDialog(stage);
		if (f == null) return;

		try {
			clearXmlFile();
			readXmlFile(f.getAbsolutePath());
			soundControlPane.setPlayingEntrainerProgram(true);
		} catch (Exception e) {
			GuiUtil.handleProblem(e);
			soundControlPane.setPlayingEntrainerProgram(false);
		}
	}

	private void readXmlFile(String fileName) {
		if (fileName == null || fileName.isEmpty()) return;
		sleeperManager = new SleeperManager(fileName);

		sleeperManager.addSleeperManagerListener(new SleeperManagerListener() {
			public void sleeperManagerEventPerformed(SleeperManagerEvent e) {
				if (e.isStopped()) {
					fireReceiverChangeEvent(false, START_ENTRAINMENT);
					stopPressed();
					soundControlPane.setPlaying(false);
				}
			}
		});

		enableControls(false);

		initializeControls();

		setMessage("Loaded " + fileName);

		settings.setXmlProgram(fileName);
	}

	private void clearXmlFile() {
		if (sleeperManager != null) {
			sleeperManager.clearMediatorObjects();
			sleeperManager = null;
			sleeperManagerThread = null;
			setMessage("Cleared EntrainerFX Program");
			entrainerProgramInitialized = false;
			settings.setXmlProgram("");
			enableControls(true);
			soundControlPane.getPause().setDisable(true);
			soundControlPane.setPlayingEntrainerProgram(false);
			settings.setPreserveState(false);
		}
	}

	private class EspConnectionListener implements ConnectionEventListener {

		@Override
		public void connectionEventPerformed(ConnectionEvent e) {
			switch (e.getState()) {
			case STARTED:
				fireReceiverChangeEvent(true, MediatorConstants.ESP_START);
				setEspMenuItemsEnabled(true);
				break;
			case ERROR_UNBOUND:
			case ERROR_STOPPED:
			case STOPPED:
				fireReceiverChangeEvent(false, MediatorConstants.ESP_START);
				setEspMenuItemsEnabled(false);
				break;
			default:
				break;
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#init()
	 */
	public void init() {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread arg0, Throwable arg1) {
				GuiUtil.handleProblem(arg1);
			}
		});

		intervalMenu = new IntervalMenu();
		control = new JSynSoundControl();
		masterLevelController = new MasterLevelController(control);
		initMediator();
		wireButtons();

		soundControlPane.setOnMouseEntered(e -> fade(true, soundControlPane));
		soundControlPane.setOnMouseExited(e -> fade(false, soundControlPane));
		soundControlPane.setOpacity(0);

		addMenu();

		createPanner();
		initSettings();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.stage = new Stage(StageStyle.TRANSPARENT);

		stage.initOwner(primaryStage);
		primaryStage.setTitle("EntrainerFX");
		stage.setTitle("EntrainerFX");

		stage.setScene(scene);

		stage.addEventHandler(javafx.stage.WindowEvent.WINDOW_SHOWN, e -> startup());
		stage.addEventHandler(javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST, e -> exitPressed());

		animations = new AnimationPane();

		GridPaneHelper gph = new GridPaneHelper();

		//@formatter:off
		gph
			.addLast(bar)
			.addLast(soundControlPane)
			.addLast(sliderControlPane)
			.addLast(audioPlayerPane)
			.addLast(pictures)
			.addLast(shimmerOptions)
			.addLast(animations)
			.addLast(flashOptions)
			.addLast(neuralizer);
		//@formatter:on

		gp = gph.getPane();

		unexpandTitledPanes();

		URI css = JFXUtils.getEntrainerCSS();

		shimmer.setInUse(true);
		group.getChildren().addAll(background.getPane(), shimmer, gp);
		if (css != null) scene.getStylesheets().add(css.toString());

		gp.setCache(true);
		gp.setCacheHint(CacheHint.SPEED);

		gp.setId(getClass().getSimpleName() + "-layout");

		stage.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> resizer.onDrag(e));
		stage.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> resizer.onRelease(e));
		stage.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> resizer.onClick(e));

		Image brainz = new Image(getClass().getResourceAsStream("/Brain.png"));

		stage.getIcons().add(brainz);
		primaryStage.getIcons().add(brainz);

		displayComponents();

		settings.initState();

		if (settings.isSplashOnStartup()) {
			EntrainerFXSplash splash = new EntrainerFXSplash();
			splash.onClose(e -> show());
		} else {
			show();
		}
	}

	private void show() {
		scaleSize();
		stage.centerOnScreen();
		stage.setOpacity(0);
		stage.show();
		setShimmerSizes(resizer.getSize());
		Timeline tl = new Timeline(new KeyFrame(Duration.millis(500), new KeyValue(stage.opacityProperty(), 1)));
		tl.play();
	}

	private void startup() {
		settings.setAcceptUpdates(true);
		Rectangle2D size = new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
		resizer.setSize(size);
		background.setDimension(stage.getWidth(), stage.getHeight());
		resizeDimensions(size);
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *          the arguments
	 * @throws Exception
	 *           the exception
	 */
	public static void main(String[] args) throws Exception {
		initTridentInterpolators();
		architectureCheck();

		launch(args);
	}

	private static void initTridentInterpolators() {
		TridentConfig.getInstance().addPropertyInterpolator(new ColorPropertyInterpolator());
		TridentConfig.getInstance().addPropertyInterpolator(new LinearGradientInterpolator());
		TridentConfig.getInstance().addPropertyInterpolator(new RadialGradientInterpolator());
	}

	private static void architectureCheck() {
		try {
			if (!JavaVersionChecker.VERSION_OK) {
				StringBuilder builder = new StringBuilder();
				builder.append("Cannot run EntrainerFX.  Minimum version required is ");
				builder.append(JavaVersionChecker.MIN_VERSION);
				builder.append(".\nYou are running version ");
				builder.append(JavaVersionChecker.CURRENT);
				builder.append(".\nPlease visit http://java.oracle.com to get the latest Java Runtime Environment");
				GuiUtil.handleProblem(new RuntimeException(builder.toString()), true);
				System.exit(-1);
			}
		} catch (Throwable e) {
			log.error("Cannot evaluate java version {}", System.getProperty("java.version"), e);
		}
	}
}
