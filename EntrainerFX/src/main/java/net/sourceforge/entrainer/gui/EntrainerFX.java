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

import static net.sourceforge.entrainer.gui.EntrainerConstants.ABOUT_ENTRAINER_MENU_NAME;
import static net.sourceforge.entrainer.gui.EntrainerConstants.ANIMATION_WINDOW_NAME;
import static net.sourceforge.entrainer.gui.EntrainerConstants.FILE_MENU_NAME;
import static net.sourceforge.entrainer.gui.EntrainerConstants.HELP_MENU_NAME;
import static net.sourceforge.entrainer.gui.EntrainerConstants.NEW_XML_PROGRAM_MENU_NAME;
import static net.sourceforge.entrainer.gui.laf.LAFConstants.SKINNABLE_LAF_CLASS_NAME;
import static net.sourceforge.entrainer.mediator.MediatorConstants.MESSAGE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.START_ENTRAINMENT;
import static net.sourceforge.entrainer.mediator.MediatorConstants.START_FLASHING;
import static net.sourceforge.entrainer.util.Utils.openBrowser;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.entrainer.esp.EspConnectionRegister;
import net.sourceforge.entrainer.gui.jfx.AnimationPane;
import net.sourceforge.entrainer.gui.jfx.BackgroundPicturePane;
import net.sourceforge.entrainer.gui.jfx.EntrainerFXSplash;
import net.sourceforge.entrainer.gui.jfx.JFXUtils;
import net.sourceforge.entrainer.gui.jfx.PinkPanningPane;
import net.sourceforge.entrainer.gui.jfx.ShimmerOptionsPane;
import net.sourceforge.entrainer.gui.jfx.SliderControlPane;
import net.sourceforge.entrainer.gui.jfx.SoundControlPane;
import net.sourceforge.entrainer.gui.jfx.animation.JFXAnimationRegister;
import net.sourceforge.entrainer.gui.jfx.animation.JFXAnimationWindow;
import net.sourceforge.entrainer.gui.jfx.shimmer.AbstractShimmer;
import net.sourceforge.entrainer.gui.jfx.shimmer.LinearShimmerRectangle;
import net.sourceforge.entrainer.gui.jfx.shimmer.ShimmerRegister;
import net.sourceforge.entrainer.gui.popup.NotificationWindow;
import net.sourceforge.entrainer.gui.socket.EntrainerSocketConnector;
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
import net.sourceforge.entrainer.sound.tools.Panner;
import net.sourceforge.entrainer.xml.Settings;
import net.sourceforge.entrainer.xml.SleeperManager;
import net.sourceforge.entrainer.xml.SleeperManagerEvent;
import net.sourceforge.entrainer.xml.SleeperManagerListener;

import org.controlsfx.dialog.Dialogs;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
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
public class EntrainerFX extends JFrame {
	private static final int MIN_HEIGHT = 900;
	private static final long serialVersionUID = 1L;
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

	private Settings settings;

	private EntrainerSocketManager socket;

	private boolean entrainerProgramInitialized;

	private Sender sender = new SenderAdapter();

	private List<String> intervalCache = new ArrayList<String>();

	private JFXAnimationWindow animationWindow;

	private JCheckBoxMenuItem connect;

	private final ImageIcon icon = GuiUtil.getIcon("/Brain.png");

	private AbstractShimmer<?> shimmer = ShimmerRegister.getShimmer(LinearShimmerRectangle.NAME);

	private GridPane gp = new GridPane();
	private JFXPanel mainPanel;
	// private ImageView background = new ImageView();
	private EntrainerBackground background = new EntrainerBackground();
	private PinkPanningPane pinkPanningPane = new PinkPanningPane();
	private AnimationPane animations = new AnimationPane();
	private BackgroundPicturePane pictures = new BackgroundPicturePane();
	private Group group;

	private ShimmerOptionsPane shimmerOptions = new ShimmerOptionsPane();
	private Lab lab;
	private NeuralizerPane neuralizer = new NeuralizerPane();

	private MasterLevelController masterLevelController;

	private JMenu espDevices;
	private JMenuItem startEspDevice;
	private JMenuItem stopEspDevice;
	private JMenuItem showEspLab;
	private JMenuItem saveEspLab;
	private JMenuItem loadEspLab;
	private JMenuItem chooseChannel;

	private EspConnectionListener espConnectionListener = new EspConnectionListener();

	private ObjectMapper jsonMapper = new ObjectMapper();
	private JCheckBoxMenuItem splashOnStartup;

	private EntrainerFX() {
		super("Entrainer FX");
		init();

		jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
		setIconImage(icon.getImage());

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent e) {
				settings.setAcceptUpdates(true);
			}
		});
	}

	/**
	 * Gets the single instance of Entrainer.
	 *
	 * @return single instance of Entrainer
	 */
	public static synchronized EntrainerFX getInstance() {
		if (instance == null) {
			final CountDownLatch cdl = new CountDownLatch(1);
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					try {
						if (instance == null) instance = new EntrainerFX();
					} finally {
						cdl.countDown();
					}
				}
			});
			try {
				cdl.await();
			} catch (InterruptedException e) {
			}
			instance.setLocationRelativeTo(null);
		}

		return instance;
	}

	/**
	 * Gets the background image.
	 *
	 * @return the background image
	 */
	Image getBackgroundImage() {
		// TODO fix me
		return background.getCurrentImage();
	}

	private void fireReceiverChangeEvent(boolean value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Window#pack()
	 */
	public void pack() {
		setMinimumSize(settings.getMinSize());
		super.pack();
	}

	/**
	 * Overridden to set visible property to true always. Circumvents bug when
	 * window is closing and exit is canceled.
	 *
	 * @param b
	 *          the new visible
	 */
	public void setVisible(boolean b) {
		if (b) displayComponents();

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				EntrainerFX.super.setVisible(true);
			}
		});
	}

	private void displayComponents() {
		mainPanel.setVisible(true);
		gp.setVisible(true);

		scaleBackground();
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				animationWindow = new JFXAnimationWindow();
				initSocket();
				initSettings();
				setComponentNames();
				addSystemTrayIcon();
				setMessage("Started Entrainer");
			}
		});

		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				setShimmerSizes();
			}
		});
	}

	private void setShimmerSizes() {
		for (AbstractShimmer<?> shimmer : ShimmerRegister.getShimmers()) {
			shimmer.setWidth(getWidth());
			shimmer.setHeight(getHeight());
		}
	}

	private void scaleBackground() {
		setPreferredSize(new Dimension((int) gp.getWidth(), MIN_HEIGHT));
		setSize(getPreferredSize());
		background.setDimension(gp.getWidth(), MIN_HEIGHT);
		GuiUtil.centerOnScreen(EntrainerFX.this);
		unexpandTitledPanes();
		return;
	}

	private void unexpandTitledPanes() {
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				pinkPanningPane.setExpanded(false);
				animations.setExpanded(false);
				shimmerOptions.setExpanded(false);
				neuralizer.setExpanded(false);
				pictures.setExpanded(false);
			}
		});
	}

	private void addSystemTrayIcon() {
		if (!SystemTray.isSupported()) return;

		TrayIcon icon = new TrayIcon(this.icon.getImage());
		icon.setPopupMenu(getTrayIconPopup());
		icon.setToolTip("Entrainer");

		try {
			SystemTray.getSystemTray().add(icon);
		} catch (AWTException e) {
			GuiUtil.handleProblem(e);
		}
	}

	private PopupMenu getTrayIconPopup() {
		PopupMenu pop = new PopupMenu("Entrainer");

		MenuItem start = new MenuItem("Start Entrainer");
		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				fireReceiverChangeEvent(true, START_FLASHING);
				fireReceiverChangeEvent(true, START_ENTRAINMENT);
				playPressed();
			}
		});

		pop.add(start);

		MenuItem stop = new MenuItem("Stop Entrainer");
		stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				fireReceiverChangeEvent(false, START_FLASHING);
				fireReceiverChangeEvent(false, START_ENTRAINMENT);
				stopPressed();
			}
		});

		pop.add(stop);

		MenuItem exit = new MenuItem("Exit");
		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				exitPressed();
			}
		});

		pop.add(exit);

		return pop;
	}

	private void init() {
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
		setResizable(false);
		wireButtons();
		layoutComponents();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exitPressed();
			}
		});

		initButtonIcons();
		createPanner();
		addMenu();
		initSettings();

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.isControlDown() && e.getClickCount() == 1) {
					openBrowser(getLocalDocAddress());
				}
			}
		});
	}

	private void setComponentNames() {
		animationWindow.setName(ANIMATION_WINDOW_NAME);
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addSender(sender);
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case DELTA_AMPLITUDE:
					setAutoAmplitude(masterLevelController.getAmplitude());
					break;
				case DELTA_ENTRAINMENT_FREQUENCY:
					setAutoEntrainment(masterLevelController.getEntrainmentFrequency());
					break;
				case DELTA_FREQUENCY:
					setAutoFrequency(masterLevelController.getFrequency());
					break;
				case DELTA_PINK_NOISE_AMPLITUDE:
					setAutoPinkNoise(masterLevelController.getPinkNoiseAmplitude());
					break;
				case START_ENTRAINMENT:
					if (e.getSource() == soundControlPane) break;
					soundControlPane.setPlaying(e.getBooleanValue());
					if (e.getBooleanValue()) {
						playPressed();
					} else {
						stopPressed();
					}
					break;
				case SHIMMER_RECTANGLE:
					setShimmerRectangle(e.getStringValue());
					break;
				case ESP_CONNECTIONS_RELOADED:
					espDevices.removeAll();
					SwingUtilities.invokeLater(() -> addEspDevices(espDevices));
					break;
				case SPLASH_ON_STARTUP:
					SwingUtilities.invokeLater(() -> splashOnStartup.setSelected(e.getBooleanValue()));
					break;
				default:
					break;

				}
			}

		});
	}

	private void setEspMenuItemsEnabled(boolean booleanValue) {
		espDevices.setEnabled(!booleanValue);
		startEspDevice.setEnabled(!booleanValue);
		stopEspDevice.setEnabled(booleanValue);
	}

	private void setShimmerRectangle(final String stringValue) {
		if (stringValue == null || stringValue.isEmpty()) return;

		final AbstractShimmer<?> shimmer = ShimmerRegister.getShimmer(stringValue);
		if (shimmer == null) return;

		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				swapShimmers(shimmer);
			}
		});
	}

	private void swapShimmers(final AbstractShimmer<?> shimmer) {
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

	private void initButtonIcons() {
		soundControlPane.setPlayToolTip("Start Entrainment");
		soundControlPane.setRecordToolTip("Flag/Clear Recording to '.wav' File");
		soundControlPane.setStopToolTip("Stop Entrainment");
		soundControlPane.setPauseToolTip("Pause/Resume an Entrainer Program");

		animations.setAnimationToolTip("Run Animation During Entrainment Session");
		shimmerOptions.setShimmerToolTip("Adds a shimmer effect to the application");
	}

	private void initSettings() {
		settings = Settings.reload();
		if (settings.getXmlProgram() != null && !settings.getXmlProgram().isEmpty() && isVisible()) {
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
		JMenuBar bar = new JMenuBar();

		JMenu menu = new JMenu(FILE_MENU_NAME);
		addMnemonic(menu, KeyEvent.VK_F);

		menu.add(getStartItem());
		menu.add(getStopItem());
		menu.add(new JSeparator());
		menu.add(getLoadXmlItem());
		menu.add(getClearXmlItem());
		menu.add(getEditXmlItem());
		menu.add(getNewXmlItem());
		menu.add(new JSeparator());
		menu.add(getExitItem());
		bar.add(menu);

		bar.add(getLookAndFeels());

		bar.add(intervalMenu);

		bar.add(getEspMenu());

		bar.add(getSocketMenu());

		JMenu help = new JMenu(HELP_MENU_NAME);
		addMnemonic(help, KeyEvent.VK_H);

		help.add(getAboutItem());
		help.add(getLicenseItem());
		help.add(getLocalDocItem());
		help.add(getRemoteDocItem());
		help.add(getSplashItem());
		help.add(getSplashOnStartupItem());
		bar.add(help);

		setJMenuBar(bar);
	}

	private JMenuItem getSplashOnStartupItem() {
		splashOnStartup = new JCheckBoxMenuItem("Splash on Startup");

		splashOnStartup.setToolTipText("Enables/Disables splash screen on startup");

		splashOnStartup.addActionListener(e -> enableSplashOnStartup(splashOnStartup.isSelected()));

		return splashOnStartup;
	}

	private void enableSplashOnStartup(boolean selected) {
		fireReceiverChangeEvent(selected, MediatorConstants.SPLASH_ON_STARTUP);
	}

	private JMenuItem getRemoteDocItem() {
		JMenuItem item = new JMenuItem("Web Documentation");
		addMnemonic(item, KeyEvent.VK_W);
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openBrowser("http://entrainer.sourceforge.net");
			}
		});

		return item;
	}

	private JMenuItem getLocalDocItem() {
		JMenuItem item = new JMenuItem("Local Documentation");
		addMnemonic(item, KeyEvent.VK_D);
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openBrowser(getLocalDocAddress());
			}
		});

		return item;
	}

	private URI getLocalDocAddress() {
		return new File("doc/index.html").toURI();
	}

	private JMenuItem getLicenseItem() {
		JMenuItem aboutItem = new JMenuItem("License");
		addMnemonic(aboutItem, KeyEvent.VK_I);
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				License.showLicenseDialog();
			}
		});

		return aboutItem;
	}

	private JMenuItem getEditXmlItem() {
		JMenuItem item = new JMenuItem("Edit Entrainer Program");
		addMnemonic(item, KeyEvent.VK_E);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editXml();
			}
		});

		return item;
	}

	private JMenuItem getNewXmlItem() {
		JMenuItem item = new JMenuItem(NEW_XML_PROGRAM_MENU_NAME);
		addMnemonic(item, KeyEvent.VK_N);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newXml();
			}
		});

		return item;
	}

	private JMenuItem getLoadXmlItem() {
		JMenuItem item = new JMenuItem("Load Entrainer Program");
		addMnemonic(item, KeyEvent.VK_L);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadXml();
			}
		});

		return item;
	}

	private JMenuItem getClearXmlItem() {
		JMenuItem item = new JMenuItem("Clear Entrainer Program");
		addMnemonic(item, KeyEvent.VK_C);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearXmlFile();
			}
		});

		return item;
	}

	private void addMnemonic(JMenuItem item, int charKey) {
		item.setMnemonic(charKey);
		if (!(item instanceof JMenu)) {
			item.setAccelerator(KeyStroke.getKeyStroke(charKey, InputEvent.CTRL_DOWN_MASK));
		}
	}

	private JMenuItem getAboutItem() {
		JMenuItem aboutItem = new JMenuItem(ABOUT_ENTRAINER_MENU_NAME);
		addMnemonic(aboutItem, KeyEvent.VK_B);
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				About.showAboutDialog();
			}
		});

		return aboutItem;
	}

	private JMenuItem getSplashItem() {
		JMenuItem splashItem = new JMenuItem("Show Splash Screen");
		addMnemonic(splashItem, KeyEvent.VK_P);
		splashItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new EntrainerFXSplash(true);
			}
		});

		return splashItem;
	}

	private JMenuItem getExitItem() {
		JMenuItem exitItem = new JMenuItem("Exit");
		addMnemonic(exitItem, KeyEvent.VK_X);
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitPressed();
			}
		});

		return exitItem;
	}

	private JMenuItem getStopItem() {
		JMenuItem stopItem = new JMenuItem("Stop");
		addMnemonic(stopItem, KeyEvent.VK_T);
		stopItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				soundControlPane.getStop().fire();
			}
		});

		return stopItem;
	}

	private JMenuItem getStartItem() {
		JMenuItem startItem = new JMenuItem("Start");
		addMnemonic(startItem, KeyEvent.VK_S);
		startItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				soundControlPane.getPlay().fire();
			}
		});

		return startItem;
	}

	private JMenu getLookAndFeels() {
		JMenu menu = new JMenu("Look and Feel");
		addMnemonic(menu, KeyEvent.VK_K);
		LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
		Map<String, List<LookAndFeelInfo>> sortedInfos = sortInfos(infos);
		for (String key : sortedInfos.keySet()) {
			List<LookAndFeelInfo> sorted = sortedInfos.get(key);
			JMenu lafMenu = new JMenu(key);
			for (LookAndFeelInfo info : sorted) {
				JMenuItem item = addLookAndFeel(info);
				lafMenu.add(item);
			}
			if (!sorted.isEmpty()) {
				menu.add(lafMenu);
			}
		}

		return menu;
	}

	private Map<String, List<LookAndFeelInfo>> sortInfos(LookAndFeelInfo[] infos) {
		Map<String, List<LookAndFeelInfo>> sorted = new LinkedHashMap<String, List<LookAndFeelInfo>>();

		String[] known = { "substance", "jgoodies", "jtattoo" };

		sorted.put("Substance", getLAFInfo("substance", infos));
		sorted.put("JGoodies", getLAFInfo("jgoodies", infos));
		sorted.put("JTattoo", getLAFInfo("jtattoo", infos));
		sorted.put("Other", getOtherLAFInfo(known, infos));

		return sorted;
	}

	private List<LookAndFeelInfo> getOtherLAFInfo(String[] known, LookAndFeelInfo[] infos) {
		List<LookAndFeelInfo> list = new ArrayList<LookAndFeelInfo>();

		for (LookAndFeelInfo info : infos) {
			if (!isKnownLAFInfo(info, known)) {
				list.add(info);
			}
		}

		return list;
	}

	private boolean isKnownLAFInfo(LookAndFeelInfo info, String[] known) {
		for (String s : known) {
			if (info.getClassName().contains(s)) {
				return true;
			}
		}
		return false;
	}

	private List<LookAndFeelInfo> getLAFInfo(String name, LookAndFeelInfo[] infos) {
		List<LookAndFeelInfo> list = new ArrayList<LookAndFeelInfo>();

		for (LookAndFeelInfo info : infos) {
			if (info.getClassName().contains(name)) {
				list.add(info);
			}
		}

		return list;
	}

	private JMenuItem addLookAndFeel(final LookAndFeelInfo info) {
		if (info.getClassName().equals(SKINNABLE_LAF_CLASS_NAME)) {
			return getSkinnableLookAndFeelMenu(info);
		}

		JMenuItem item = new JMenuItem(info.getName());
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeLookAndFeel(info.getClassName(), null);
			}
		});

		return item;
	}

	private JMenu getSkinnableLookAndFeelMenu(LookAndFeelInfo info) {
		JMenu menu = new JMenu(info.getName());

		File themePacks = new File("lafs/skins");
		File[] packs = themePacks.listFiles(new java.io.FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".zip");
			}
		});

		for (int i = 0; i < packs.length; i++) {
			menu.add(getThemePackItem(info, packs[i]));
		}

		return menu;
	}

	private JMenuItem getThemePackItem(final LookAndFeelInfo info, final File pack) {
		JMenuItem item = new JMenuItem(getPackName(pack));

		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeLookAndFeel(info.getClassName(), pack.getAbsolutePath());
			}
		});

		return item;
	}

	private void changeLookAndFeel(String className, String themePack) {
		GuiUtil.changeLookAndFeel(className, themePack, this);
		fireReceiverChangeEvent(className, MediatorConstants.LOOK_AND_FEEL);
		fireReceiverChangeEvent(themePack, MediatorConstants.THEME_PACK);
		pack();
	}

	private void fireReceiverChangeEvent(String value, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, value, parm));
	}

	private JMenu getSocketMenu() {
		JMenu menu = new JMenu("External Sockets");
		addMnemonic(menu, KeyEvent.VK_R);

		menu.add(getChangePortItem());
		menu.add(getConnectorGuiItem());
		menu.add(getConnectSocketItem());

		return menu;
	}

	private JMenu getEspMenu() {
		JMenu menu = new JMenu("EEG Signal Processing");
		addMnemonic(menu, KeyEvent.VK_G);

		menu.add(getEspDeviceMenu());
		menu.add(new JSeparator());
		menu.add(chooseChannelMenu());
		menu.add(new JSeparator());
		menu.add(getStartEspMenu());
		menu.add(getStopEspMenu());
		menu.add(new JSeparator());
		menu.add(loadLabMenu());
		menu.add(saveLabMenu());
		menu.add(new JSeparator());
		menu.add(showEspLabMenu());

		setEspMenuItemsEnabled();

		return menu;
	}

	private JMenuItem chooseChannelMenu() {
		chooseChannel = new JMenuItem("Choose Channel");

		chooseChannel.addActionListener(e -> JFXUtils.runLater(() -> chooseChannel()));

		return chooseChannel;
	}

	private void chooseChannel() {
		//@formatter:off
		EspChannel channel = Dialogs
				.create()
				.title("Choose Channel")
				.message("Choose the channel for processing")
				.showChoices(lab.getConnection().getChannels());
		//@formatter:on

		if (channel != null) lab.setChannel(channel.getChannelNumber());
	}

	private JMenuItem loadLabMenu() {
		loadEspLab = new JMenuItem("Load ESP Lab Settings");

		loadEspLab.addActionListener(e -> JFXUtils.runLater(() -> loadLabSettings()));

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

	private JMenuItem saveLabMenu() {
		saveEspLab = new JMenuItem("Save ESP Lab Settings");

		saveEspLab.addActionListener(e -> JFXUtils.runLater(() -> showSaveLabSettings()));

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
		boolean enable = lab != null && lab.getConnection() != null;
		startEspDevice.setEnabled(enable);
		stopEspDevice.setEnabled(false);
		showEspLab.setEnabled(enable);
		saveEspLab.setEnabled(enable);
		loadEspLab.setEnabled(enable);
		chooseChannel.setEnabled(enable ? lab.getNumChannels() > 1 : false);
	}

	private JMenuItem showEspLabMenu() {
		showEspLab = new JMenuItem("Show ESP Lab");

		showEspLab.addActionListener(e -> JFXUtils.runLater(() -> showEspLab()));

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
		espDevices.setEnabled(enabled);
		showEspLab.setEnabled(enabled);
	}

	private JMenuItem getStopEspMenu() {
		stopEspDevice = new JMenuItem("Stop ESP Device");

		stopEspDevice.addActionListener(e -> stopEspDevice());

		return stopEspDevice;
	}

	private void stopEspDevice() {
		if (!connectionCheck()) return;

		if (!lab.getConnection().isConnected()) return;

		JFXUtils.runLater(() -> lab.getConnection().stop());
	}

	private JMenuItem getStartEspMenu() {
		startEspDevice = new JMenuItem("Start ESP Device");

		startEspDevice.addActionListener(e -> startEspDevice());

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

	private boolean connectionCheck() {
		if (lab.getConnection() == null) {
			if (Platform.isFxApplicationThread()) {
				Dialogs.create().title("No ESP Device Selected").message("Choose an ESP device first").showWarning();
			} else {
				JOptionPane.showMessageDialog(this,
						"Choose an ESP device first",
						"No ESP Device Selected",
						JOptionPane.ERROR_MESSAGE);
			}
			return false;
		}

		return true;
	}

	private JMenu getEspDeviceMenu() {
		espDevices = new JMenu("Choose ESP Device");

		EspConnectionRegister.getEspConnections();

		return espDevices;
	}

	private void addEspDevices(JMenu menu) {
		List<RawEspConnection> connections = EspConnectionRegister.getEspConnections();
		ButtonGroup bg = new ButtonGroup();
		connections.forEach(connection -> addEspDevice(menu, connection, bg));
	}

	private void addEspDevice(JMenu menu, RawEspConnection connection, ButtonGroup bg) {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(connection.getName());

		bg.add(item);
		menu.add(item);

		item.addActionListener(e -> setConnection(connection));
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

	private JMenuItem getConnectorGuiItem() {
		JMenuItem item = new JMenuItem("Show Connector GUI");

		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				showConnectorGui();
			}
		});

		item.setToolTipText("Show the Connector GUI, allowing sending and receiving of Entrainer state messages");

		return item;
	}

	private void showConnectorGui() {
		try {
			EntrainerSocketConnector esc = new EntrainerSocketConnector(settings.getSocketIPAddress(),
					settings.getSocketPort());
			esc.pack();
			GuiUtil.centerOnScreen(esc);
			GuiUtil.addFadeOutInvisibleListener(esc, 500);
			GuiUtil.fadeIn(esc, 500);
		} catch (UnknownHostException e) {
			GuiUtil.handleProblem(e);
		}
	}

	private JMenuItem getChangePortItem() {
		JMenuItem item = new JMenuItem("Choose Socket Host & Port");

		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				showSocketPortDialog();
			}
		});

		item.setToolTipText("Choose the host and port on which Entrainer sends and receives state messages");

		return item;
	}

	private JMenuItem getConnectSocketItem() {
		connect = new JCheckBoxMenuItem("Accept Connections");

		connect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (connect.isSelected()) {
					bindSocket();
				} else {
					unbindSocket();
				}
			}
		});

		connect.setToolTipText("Bind / unbind the socket on which Entrainer sends and receives state messages");

		return connect;
	}

	private boolean isSocketBound() {
		return socket.isBound();
	}

	private void unbindSocket() {
		int portNum = socket.getPortNumber();
		String hostName = socket.getHostName();
		socket.unbind();
		new NotificationWindow("Entrainer socket unbound from host " + hostName + " and port " + portNum, this);
		settings.setSocketConnected(false);
	}

	private void bindSocket() {
		if (Settings.getInstance().getSocketPort() <= 0) showSocketPortDialog();
		try {
			socket.bind();
			new NotificationWindow("Entrainer socket bound to host " + socket.getHostName() + " and port "
					+ socket.getPortNumber(), this);
			settings.setSocketConnected(true);
		} catch (IOException e) {
			GuiUtil.handleProblem(e);
			connect.setSelected(false);
		} catch (InvalidPortNumberException e) {
			JOptionPane.showMessageDialog(EntrainerFX.this,
					"The port number " + e.getPort() + " is not valid",
					"Invalid Port Number",
					JOptionPane.ERROR_MESSAGE);
			connect.setSelected(false);
		}
	}

	private void showSocketPortDialog() {
		try {
			GuiUtil.showDialog(new SocketPortDialog());
		} catch (UnknownHostException e) {
			GuiUtil.handleProblem(e);
		}
	}

	private String getPackName(File pack) {
		String name = pack.getName();

		name = name.substring(0, name.indexOf("themepack.zip"));

		return name;
	}

	private void editXml() {
		JFileChooser xmlChooser = XmlEditor.getXmlFileChooser();
		int val = xmlChooser.showOpenDialog(this);
		if (val == JFileChooser.APPROVE_OPTION) {
			showXmlEditor(xmlChooser.getSelectedFile());
		}
	}

	private void showXmlEditor(File f) {
		intervalCache = intervalMenu.removeAllIntervals();
		final XmlEditor editor = new XmlEditor(this, f);
		editor.addXmlFileSaveListener(new XmlFileSaveListener() {
			public void xmlFileSaveEventPerformed(XmlFileSaveEvent e) {
				xmlFileSaved(e.getXmlFile());
			}
		});

		editor.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				intervalMenu.loadIntervals(intervalCache);
				intervalMenu.loadCustomIntervals();
			}
		});

		GuiUtil.showDialog(editor);
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
		soundControlPane.getPlay().addEventHandler(javafx.event.ActionEvent.ACTION,
				new EventHandler<javafx.event.ActionEvent>() {

					@Override
					public void handle(javafx.event.ActionEvent arg0) {
						playPressed();
					}
				});

		soundControlPane.getStop().addEventHandler(javafx.event.ActionEvent.ACTION,
				new EventHandler<javafx.event.ActionEvent>() {

					@Override
					public void handle(javafx.event.ActionEvent arg0) {
						stopPressed();
					}
				});

		soundControlPane.getPause().addEventHandler(javafx.event.ActionEvent.ACTION,
				new EventHandler<javafx.event.ActionEvent>() {

					@Override
					public void handle(javafx.event.ActionEvent arg0) {
						pausePressed();
					}
				});

		soundControlPane.getRecord().addEventHandler(javafx.event.ActionEvent.ACTION,
				new EventHandler<javafx.event.ActionEvent>() {

					@Override
					public void handle(javafx.event.ActionEvent arg0) {
						recordClicked();
					}
				});
	}

	private void recordClicked() {
		if (soundControlPane.getRecord().isSelected()) {

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					boolean recording = showWavFileChooser();
					JFXUtils.runLater(() -> recordClicked(recording));
				}
			});
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
		JFileChooser wavChooser = getWavFileChooser();

		int val = wavChooser.showDialog(this, "Ok");
		if (val == JFileChooser.APPROVE_OPTION) {
			File wavFile = processFile(wavChooser.getSelectedFile());
			if (!isValidFile(wavFile)) {
				JOptionPane.showMessageDialog(this,
						wavFile.getName() + " is not a valid WAV file name\n(it must end with a '.wav' extension)",
						"Invalid WAV File Name",
						JOptionPane.WARNING_MESSAGE);
				return false;
			}
			control.setWavFile(wavFile);
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

	private JFileChooser getWavFileChooser() {
		JFileChooser wavChooser = new JFileChooser(new File(System.getProperty("user.dir") + "/wav"));
		wavChooser.setDialogTitle("WAV File Chooser, select or enter WAV file for recording");

		wavChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().indexOf(".wav") > 0;
			}

			@Override
			public String getDescription() {
				return "WAV files";
			}
		});

		return wavChooser;
	}

	private void stopPressed() {
		if (isPaused()) return;
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				setMessage(control.isRecord() ? "Recording Stopped" : "Stopped");
				if (animationWindow != null) {
					animationWindow.setVisible(false);
				}
				stop();
			}
		});
	}

	private boolean isPaused() {
		return soundControlPane.getPause().isSelected();
	}

	private void playPressed() {
		if (isUnpaused()) return;
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				if (animations.getAnimation().isSelected()) {
					startAnimation();
				}
				start();
				if (!animations.getAnimation().isSelected()) {
					setMessage(control.isRecord() ? "Recording Started" : "Entrainment Started");
				}
			}
		});
	}

	private boolean isUnpaused() {
		return !soundControlPane.getPause().isDisabled() && control.isPlaying();
	}

	private void startAnimation() {
		if (animationWindow.isVisible()) {
			return;
		}

		if (animationWindow.getEntrainerAnimation() == null) {
			animationWindow.setEntrainerAnimation(JFXAnimationRegister.getEntrainerAnimations().get(0));
		}

		animationWindow.setVisible(true);
		toFront();
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

	private void setAutoAmplitude(double value) {
		if (sliderControlPane.getAmplitude().getValue() == value) return;

		sliderControlPane.setAmplitudeValue(value);
	}

	private void setAutoPinkNoise(double value) {
		if (sliderControlPane.getPinkNoise().getValue() == value) return;

		sliderControlPane.setPinkNoiseValue(value);
	}

	private void setAutoFrequency(double value) {
		if (sliderControlPane.getFrequency().getValue() == value) return;

		sliderControlPane.setFrequencyValue(value);
	}

	private void setAutoEntrainment(double value) {
		sliderControlPane.setEntrainmentFrequencyValue(value);
	}

	private void enableControls(final boolean enabled) {
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				sliderControlPane.setDisable(!enabled);
				pinkPanningPane.setEnabled(enabled);
			}
		});

		getFileMenuItem("New Entrainer Program").setEnabled(enabled);
		getFileMenuItem("Edit Entrainer Program").setEnabled(enabled);
		getFileMenuItem("Clear Entrainer Program").setEnabled(!enabled);
		getFileMenuItem("Load Entrainer Program").setEnabled(enabled);
	}

	private JMenuItem getFileMenuItem(String text) {
		JMenu file = getJMenuBar().getMenu(0);

		Component[] comps = file.getMenuComponents();
		JMenuItem jmi;
		for (Component c : comps) {
			if (c instanceof JMenuItem) {
				jmi = (JMenuItem) c;
				if (jmi.getText().equals(text)) {
					return jmi;
				}
			}
		}

		return null;
	}

	private void exitPressed() {
		stopPressed();
		int option = JOptionPane.showConfirmDialog(this, "Exiting: Confirm?", "Exit Entrainer", JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			new NotificationWindow("Exiting Entrainer", this);

			control.exit();

			exitApplication();
		}
	}

	private void exitApplication() {
		TimelineCallbackAdapter tca = new TimelineCallbackAdapter() {

			public void onTimelineStateChanged(TimelineState oldState, TimelineState newState, float durationFraction,
					float timelinePosition) {
				if (TimelineState.DONE == newState || TimelineState.CANCELLED == newState) {
					System.exit(0);
				}
			}
		};

		GuiUtil.fadeOut(this, 5000, tca);
	}

	private void start() {
		if (isXmlProgram()) {
			initializeControls();
		}

		if (isXmlProgram()) {
			sleeperManagerThread = sleeperManager.start();
		}
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

	private void stop() {
		if (isXmlProgram()) {
			sleeperManager.stop();
		}
		entrainerProgramInitialized = false;
	}

	private void pause() {
		control.pause();
	}

	private void layoutComponents() {
		mainPanel = new JFXPanel();

		gp.setAlignment(Pos.BASELINE_CENTER);
		GridPane.setConstraints(soundControlPane, 0, 0);
		GridPane.setConstraints(sliderControlPane, 1, 0);
		GridPane.setValignment(sliderControlPane, VPos.BOTTOM);

		int v = 1;
		GridPane.setConstraints(pictures, 0, v++, 2, 1);
		GridPane.setConstraints(pinkPanningPane, 0, v++, 2, 1);
		GridPane.setConstraints(animations, 0, v++, 2, 1);
		GridPane.setConstraints(shimmerOptions, 0, v++, 2, 1);
		GridPane.setConstraints(neuralizer, 0, v++, 2, 1);

		gp.setPadding(new Insets(5));
		gp.getChildren().addAll(soundControlPane,
				sliderControlPane,
				pinkPanningPane,
				animations,
				shimmerOptions,
				pictures,
				neuralizer);

		final URI css = JFXUtils.getEntrainerCSS();

		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				group = new Group();

				group.getChildren().add(background.getPane());
				// new BackgroundFlasher(background);
				// gp.setMinSize(getBackgroundImage().getWidth(),
				// getBackgroundImage().getHeight());
				shimmer.setInUse(true);
				group.getChildren().add(shimmer);
				group.getChildren().add(gp);
				Scene scene = new Scene(group);
				if (css != null) scene.getStylesheets().add(css.toString());
				mainPanel.setScene(scene);
			}
		});

		getContentPane().add(mainPanel);
	}

	private void setMessage(String s) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, s, MESSAGE));
	}

	private void loadXml() {
		intervalMenu.removeAllIntervals();
		JFileChooser chooser = XmlEditor.getXmlFileChooser();

		int val = chooser.showOpenDialog(this);
		if (val == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();

			if (f != null) {
				try {
					clearXmlFile();
					readXmlFile(f.getName());
					soundControlPane.setPlayingEntrainerProgram(true);
				} catch (Exception e) {
					GuiUtil.handleProblem(e);
					soundControlPane.setPlayingEntrainerProgram(false);
				}
			}
		}
	}

	private void readXmlFile(String fileName) {
		sleeperManager = new SleeperManager(fileName);

		sleeperManager.addSleeperManagerListener(new SleeperManagerListener() {
			public void sleeperManagerEventPerformed(SleeperManagerEvent e) {
				if (e.isStopped()) {
					fireReceiverChangeEvent(false, START_FLASHING);
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
			setMessage("Cleared Entrainer Program");
			entrainerProgramInitialized = false;
			settings.setXmlProgram("");
			enableControls(true);
			soundControlPane.getPause().setDisable(true);
			soundControlPane.setPlayingEntrainerProgram(false);
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
}
