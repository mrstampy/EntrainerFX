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

import static net.sourceforge.entrainer.gui.EntrainerConstants.HELP_MENU_NAME;
import static net.sourceforge.entrainer.gui.XmlEditorConstants.XEC_ABOUT_MENU_NAME;
import static net.sourceforge.entrainer.gui.XmlEditorConstants.XEC_ADD_UNIT_NAME;
import static net.sourceforge.entrainer.gui.XmlEditorConstants.XEC_CANCEL_NAME;
import static net.sourceforge.entrainer.gui.XmlEditorConstants.XEC_DIALOG_NAME;
import static net.sourceforge.entrainer.gui.XmlEditorConstants.XEC_REMOVE_UNIT_NAME;
import static net.sourceforge.entrainer.gui.XmlEditorConstants.XEC_SAVE_AS_NAME;
import static net.sourceforge.entrainer.gui.XmlEditorConstants.XEC_SAVE_NAME;
import static net.sourceforge.entrainer.gui.XmlEditorConstants.XEC_SHOW_CHART_NAME;
import static net.sourceforge.entrainer.gui.XmlEditorConstants.XEC_UNITS_NAME;
import static net.sourceforge.entrainer.mediator.MediatorConstants.ANIMATION_BACKGROUND;
import static net.sourceforge.entrainer.mediator.MediatorConstants.ANIMATION_PROGRAM;
import static net.sourceforge.entrainer.mediator.MediatorConstants.IS_ANIMATION;
import static net.sourceforge.entrainer.mediator.MediatorConstants.IS_FLASH;
import static net.sourceforge.entrainer.mediator.MediatorConstants.IS_PSYCHEDELIC;
import static net.sourceforge.entrainer.mediator.MediatorConstants.IS_SHIMMER;
import static net.sourceforge.entrainer.mediator.MediatorConstants.MESSAGE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_PAN;
import static net.sourceforge.entrainer.mediator.MediatorConstants.SHIMMER_RECTANGLE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.START_FLASHING;
import static net.sourceforge.entrainer.util.Utils.openBrowser;
import static net.sourceforge.entrainer.xml.program.EntrainerProgramUtil.marshal;
import static net.sourceforge.entrainer.xml.program.EntrainerProgramUtil.unmarshal;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
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
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.entrainer.gui.jfx.AnimationPane;
import net.sourceforge.entrainer.gui.jfx.FlashPane;
import net.sourceforge.entrainer.gui.jfx.JFXUtils;
import net.sourceforge.entrainer.gui.jfx.PinkPanningPane;
import net.sourceforge.entrainer.gui.jfx.ShimmerOptionsPane;
import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.guitools.MigHelper;
import net.sourceforge.entrainer.jfreechart.UnitChart;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;
import net.sourceforge.entrainer.xml.program.EntrainerProgram;
import net.sourceforge.entrainer.xml.program.EntrainerProgramInterval;
import net.sourceforge.entrainer.xml.program.EntrainerProgramUnit;
import net.sourceforge.entrainer.xml.program.UnitSetter;

import org.xml.sax.SAXException;

// TODO: Auto-generated Javadoc
/**
 * The editor dialog for entrainer xml files (Entrainer Programs).
 * 
 * @author burton
 */
public class XmlEditor extends JDialog {

	private static final long serialVersionUID = 1L;

	private EntrainerProgram xml;

	private JTabbedPane units = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

	private JButton save = new JButton(XEC_SAVE_NAME);
	private JButton saveAs = new JButton(XEC_SAVE_AS_NAME);
	private JButton cancel = new JButton(XEC_CANCEL_NAME);

	private JButton removeUnit = new JButton();
	private JButton addUnit = new JButton();
	private JButton showChart = new JButton();

	private UnitEditorPane visibleUnitEditorPane;
	private UnitEditorPane previousUnitEditorPane;

	private SimpleDateFormat titleFormat = new SimpleDateFormat("mm:ss");
	private Calendar titleGenerator = Calendar.getInstance();

	private AnimationPane animations = new AnimationPane();
	private FlashPane checkBoxPane = new FlashPane();
	private ShimmerOptionsPane shimmers = new ShimmerOptionsPane();
	private PinkPanningPane pinkPanning = new PinkPanningPane(false);

	private boolean cancelPressed = false;

	private List<XmlFileSaveListener> fileSaveListeners = new ArrayList<XmlFileSaveListener>();

	private IntervalMenu intervalMenu = new IntervalMenu();

	private Sender sender = new SenderAdapter();
	private JFXPanel panel = new JFXPanel();
	private ImageView background = new ImageView();

	private volatile boolean closing = false;

	/**
	 * Instantiates a new xml editor.
	 *
	 * @param owner
	 *          the owner
	 * @param file
	 *          the file
	 */
	public XmlEditor(Frame owner, File file) {
		super(owner, XEC_DIALOG_NAME, true);
		init(file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Window#pack()
	 */
	public void pack() {
		units.setPreferredSize(units.getSize());
		panel.setPreferredSize(new Dimension(panel.getWidth() + 10, 400));
		super.pack();
		if (background.getImage() != null) scaleBackground();
		animations.setMinWidth(getWidth() - 10);
		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				animations.setExpanded(false);
				checkBoxPane.setExpanded(false);
				shimmers.setExpanded(false);
				pinkPanning.setExpanded(false);
			}
		});
	}

	private void scaleBackground() {
		double backWidth = background.getImage().getWidth();
		double backHeight = background.getImage().getHeight();
		final double ratio = backWidth / backHeight;

		final double newWidth = panel.getWidth() + 10;

		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				background.setPreserveRatio(true);
				background.setFitWidth(ratio < 1 ? newWidth : newWidth * ratio);
			}
		});
	}

	/**
	 * Adds the xml file save listener.
	 *
	 * @param l
	 *          the l
	 */
	public void addXmlFileSaveListener(XmlFileSaveListener l) {
		fileSaveListeners.add(l);
	}

	/**
	 * Fire xml file save event.
	 *
	 * @param xmlFile
	 *          the xml file
	 */
	protected void fireXmlFileSaveEvent(File xmlFile) {
		XmlFileSaveEvent e = new XmlFileSaveEvent(this, xmlFile);

		for (XmlFileSaveListener l : fileSaveListeners) {
			l.xmlFileSaveEventPerformed(e);
		}
	}

	private void init(File f) {
		initMediator();
		addMenuBar();
		xml = unmarshal(f);
		setResizable(false);
		initFields();
		createTabs();
		addListeners();
		layoutComponents();
		if (f != null) {
			setMessage("Loaded " + f.getName());
		}
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

		return "file://" + path + "doc/editing.html";
	}

	private void setComponentNames() {
		save.setName(XEC_SAVE_NAME);
		cancel.setName(XEC_CANCEL_NAME);
		removeUnit.setName(XEC_REMOVE_UNIT_NAME);
		saveAs.setName(XEC_SAVE_AS_NAME);
		showChart.setName(XEC_SHOW_CHART_NAME);
		units.setName(XEC_UNITS_NAME);
		addUnit.setName(XEC_ADD_UNIT_NAME);
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addSender(sender);
	}

	private void addMenuBar() {
		JMenuBar bar = new JMenuBar();

		bar.add(intervalMenu);

		JMenu help = new JMenu(HELP_MENU_NAME);
		addMnemonic(help, KeyEvent.VK_H);
		help.add(getAboutItem());

		bar.add(help);

		setJMenuBar(bar);
	}

	private JMenuItem getAboutItem() {
		JMenuItem item = new JMenuItem(XEC_ABOUT_MENU_NAME);
		addMnemonic(item, KeyEvent.VK_A);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				XmlEditorAbout.showDialog();
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

	private void addListeners() {
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				savePressed(true);
			}
		});

		save.setToolTipText("Save the Entrainer Program");

		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				savePressed(false);
			}
		});

		saveAs.setToolTipText("Save Entrainer Program As...");

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});

		cancel.setToolTipText("Cancel Entrainer Program Changes");

		removeUnit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeUnitPressed();
			}
		});

		removeUnit.setIcon(GuiUtil.getIcon("/delete.png"));
		removeUnit.setRolloverIcon(GuiUtil.getIcon("/delete-Hot.png"));
		GuiUtil.initIconButton(removeUnit, "Remove the Current Unit");

		addUnit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addUnitPressed();
			}
		});

		addUnit.setIcon(GuiUtil.getIcon("/add.png"));
		addUnit.setRolloverIcon(GuiUtil.getIcon("/add-Hot.png"));
		GuiUtil.initIconButton(addUnit, "Add a Unit");

		showChart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showChartPressed();
			}
		});

		showChart.setIcon(GuiUtil.getIcon("/Column-Chart-Normal.png"));
		showChart.setRolloverIcon(GuiUtil.getIcon("/Column-Chart-Hot.png"));
		GuiUtil.initIconButton(showChart, "Show Chart of the Entrainer Program Settings");

		units.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				previousUnitEditorPane = visibleUnitEditorPane;
				visibleUnitEditorPane = getUnitEditorPane(units.getSelectedIndex());
			}
		});
	}

	private void showChartPressed() {
		UnitChart chart = new UnitChart(this, getUnits(), xml.getFile() != null ? xml.getFile().getName()
				: "New Entrainer Program", intervalMenu.getLoadedIntervals());
		GuiUtil.showDialog(chart);
	}

	private void savePressed(boolean isSave) {
		if (units.getTabCount() == 0) {
			JOptionPane.showMessageDialog(this, "No units specified", "Missing Units", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (validatePane(getUnitEditorPane(units.getSelectedIndex()))) {
			try {
				saveXmlFile(isSave);
			} catch (Exception e) {
				GuiUtil.handleProblem(e);
			}
		}
	}

	private void cancelPressed() {
		cancelPressed = true;
		clearMediatorObjects();
		intervalMenu.removeAllIntervals();
	}

	private void clearMediatorObjects() {
		for (int i = 0; i < units.getTabCount(); i++) {
			getUnitEditorPane(i).clearMediatorObjects();
		}
		xml.clearMediatorObjects();
		intervalMenu.clearMediatorObjects();
		animations.clearMediatorObjects();
		checkBoxPane.clearMediatorObjects();
		pinkPanning.clearMediatorObjects();
		shimmers.clearMediatorObjects();
	}

	private void saveXmlFile(boolean isSave) throws ParserConfigurationException, SAXException, IOException {
		if (xml.getFile() == null || !isSave) {
			JFileChooser xmlChooser = getXmlFileChooser();
			int result = xmlChooser.showSaveDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File f = processFile(xmlChooser.getSelectedFile());
				if (!isValidFile(f)) {
					JOptionPane.showMessageDialog(this,
							"The file " + f.getName() + " is invalid",
							"Invalid File Name",
							JOptionPane.WARNING_MESSAGE);
					return;
				} else {
					xml.setFile(f);
				}
			} else {
				return;
			}
		}
		if (xml.getFile().exists()) {
			int option = JOptionPane.showConfirmDialog(this,
					"File " + xml.getFile().getName() + " already exists. Continue?",
					"File exists",
					JOptionPane.OK_CANCEL_OPTION);
			if (option != JOptionPane.OK_OPTION) {
				return;
			}
		}
		xml.setUnits(getUnits());
		xml.setFlash(checkBoxPane.getFlash().isSelected());
		xml.setPsychedelic(checkBoxPane.getPsychedelic().isSelected());
		xml.setAnimation(animations.getAnimation().isSelected());
		xml.setPinkPan(pinkPanning.getPanCheck().isSelected());
		xml.setShimmer(shimmers.getShimmer().isSelected());
		xml.setAnimationBackground(animations.getAnimationBackgroundPicture());
		xml.setAnimationProgram(animations.getSelectedAnimationName());
		xml.setColour(JFXUtils.fromJFXColor((Color) checkBoxPane.getColourChooser().getTextFill()));
		xml.setIntervals(intervalMenu.getLoadedIntervals());
		xml.setShimmerName(shimmers.getShimmers().getValue());
		// xml.setFlashBackground(checkBoxPane.getFlashBackground().isSelected());

		try {
			marshal(xml, xml.getFile().getName());
		} catch (Exception e) {
			GuiUtil.handleProblem(e);
		}

		setMessage("Saved " + xml.getFile().getName());

		fireXmlFileSaveEvent(xml.getFile());
	}

	private File processFile(File selected) {
		if (isUntypedFile(selected)) {
			return new File(selected.getAbsolutePath() + ".xml");
		}

		return selected;
	}

	private boolean isUntypedFile(File f) {
		return f.getName().indexOf(".") == -1;
	}

	private boolean isValidFile(File f) {
		return f.getName().indexOf(".xml") == f.getName().length() - 4;
	}

	private List<EntrainerProgramUnit> getUnits() {
		List<EntrainerProgramUnit> list = new LinkedList<EntrainerProgramUnit>();

		for (int i = 0; i < units.getTabCount(); i++) {
			list.add(getUnitEditorPane(i).getUnit());
		}

		return list;
	}

	private void removeUnitPressed() {
		if (units.getTabCount() > 0) {
			int option = JOptionPane.showConfirmDialog(this,
					"About to remove this unit.  Continue?",
					"Remove Unit",
					JOptionPane.OK_CANCEL_OPTION);
			if (option == JOptionPane.OK_OPTION) {
				int idx = units.getSelectedIndex();
				UnitEditorPane pane = getUnitEditorPane(idx);
				pane.removeListener();
				pane.clearMediatorObjects();
				units.remove(idx);
				for (int i = idx; i < units.getTabCount(); i++) {
					setTabTitle(i);
				}

				if (idx < units.getTabCount()) {
					UnitEditorPane replacement = getUnitEditorPane(idx);
					replacement.fireAllChanged();
				}
			}
		}
	}

	private void addUnitPressed() {
		EntrainerProgramUnit unit = new EntrainerProgramUnit();
		if (units.getTabCount() > 0) {
			setLastFromPrevious(unit);
		}
		addTab(unit);

		units.setSelectedIndex(units.getTabCount() - 1);
	}

	private void setLastFromPrevious(EntrainerProgramUnit unit) {
		UnitEditorPane pane = getUnitEditorPane(units.getTabCount() - 1);
		EntrainerProgramUnit last = pane.getUnit();
		setUnitSetter(unit.getStartUnitSetter(), last);
		setUnitSetter(unit.getEndUnitSetter(), last);
	}

	private void setUnitSetter(UnitSetter us, EntrainerProgramUnit last) {
		us.setAmplitude(last.getEndAmplitude());
		us.setEntrainmentFrequency(last.getEndEntrainmentFrequency());
		us.setFrequency(last.getEndFrequency());
		us.setPinkEntrainerMultiple(last.getEndPinkEntrainerMultiple());
		us.setPinkNoise(last.getEndPinkNoise());
		us.setPinkPanAmplitude(last.getEndPinkPan());
	}

	private UnitEditorPane getUnitEditorPane(int idx) {
		if (idx < 0) {
			idx = 0;
		}
		return (UnitEditorPane) units.getComponentAt(idx);
	}

	private void initFields() {
		checkBoxPane.getFlash().setSelected(xml.isFlash());
		checkBoxPane.getPsychedelic().setSelected(xml.isPsychedelic());
		// checkBoxPane.getFlashBackground().setSelected(xml.isFlashBackground());
		fireReceiverChangeEvent(checkBoxPane.getFlash().isSelected(), IS_FLASH);
		fireReceiverChangeEvent(checkBoxPane.getPsychedelic().isSelected(), IS_PSYCHEDELIC);

		Color c = JFXUtils.toJFXColor(xml.getColour());
		if (c != null) fireReceiverChangeEvent(c);

		animations.getAnimation().setSelected(xml.isAnimation());
		if (xml.getAnimationProgram() != null) {
			animations.refreshAnimations();
			fireReceiverChangeEvent(xml.getAnimationProgram(), ANIMATION_PROGRAM);
		}
		pinkPanning.getPanCheck().setSelected(xml.isPinkPan());
		shimmers.getShimmer().setSelected(xml.isShimmer());
		if (xml.getAnimationBackground() != null) {
			fireReceiverChangeEvent(xml.getAnimationBackground(), ANIMATION_BACKGROUND);
		}
		if (!xml.getIntervals().isEmpty()) intervalMenu.loadIntervals(getIntervals(xml.getIntervals()));
		if (xml.getShimmerName() != null) shimmers.getShimmers().setValue(xml.getShimmerName());
	}

	private List<String> getIntervals(List<EntrainerProgramInterval> intervals) {
		List<String> list = new ArrayList<String>();

		for (EntrainerProgramInterval epi : intervals) {
			list.add(epi.getValue());
		}

		return list;
	}

	private void fireReceiverChangeEvent(Color value) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, JFXUtils.fromJFXColor(value)));
	}

	private void createTabs() {
		for (EntrainerProgramUnit unit : xml.getUnits()) {
			addTab(unit);
		}

		if (units.getTabCount() == 0) {
			addTab(new EntrainerProgramUnit());
		}
	}

	private boolean validatePane(UnitEditorPane pane) {
		String s = pane.validateFields();
		if (s.trim().length() > 0) {
			try {
				units.setSelectedComponent(pane);
				JOptionPane.showMessageDialog(this, s, "Errors", JOptionPane.ERROR_MESSAGE);
			} catch (IllegalArgumentException e) {
				// pane has been removed
				pane = null;
			}
			return false;
		}

		return true;
	}

	private String getTabTitle(int idx) {
		return getTabTitle(idx, getUnitEditorPane(idx));
	}

	private String getTabTitle(int idx, UnitEditorPane pane) {
		long millis = getPreviousTimeInMillis(idx);

		String from = getFormattedTime(millis);

		EntrainerProgramUnit unit = pane.getUnit();

		long toMillis = (unit.getTime().getSeconds() + (unit.getTime().getMinutes() * 60)) * 1000;
		String to;
		if (toMillis == 0) {
			to = "";
		} else {
			millis += toMillis;
			to = getFormattedTime(millis);
		}

		return from + " -> " + to;
	}

	private String getFormattedTime(long millis) {
		long oneHour = 60 * 60 * 1000;

		long hours = 0;
		if (millis >= oneHour) {
			hours = millis / oneHour;
			millis = millis % oneHour;
		}

		titleGenerator.setTimeInMillis(millis);

		String time = titleFormat.format(titleGenerator.getTime());
		if (hours > 0) {
			time = hours + ":" + time;
		}

		return time;
	}

	private void setTabTitle(int idx) {
		units.setTitleAt(idx, getTabTitle(idx));
		units.getComponentAt(idx).setName(XEC_UNITS_NAME + (idx + 1));
	}

	private long getPreviousTimeInMillis(int idx) {
		int seconds = 0;
		EntrainerProgramUnit unit;
		for (int i = 0; i < idx; i++) {
			unit = getUnitEditorPane(i).getUnit();
			seconds += unit.getTime().getSeconds() + (unit.getTime().getMinutes() * 60);
		}

		return seconds * 1000;
	}

	private void enableControls(boolean enabled) {
		cancel.setEnabled(enabled);
		save.setEnabled(enabled);
		saveAs.setEnabled(enabled);
		units.setEnabled(enabled);
		removeUnit.setEnabled(enabled);
		addUnit.setEnabled(enabled);
		showChart.setEnabled(enabled);
	}

	private void fireReceiverChangeEvent(boolean b, MediatorConstants parm) {
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, b, parm);
		sender.fireReceiverChangeEvent(e);
	}

	private void fireReceiverChangeEvent(String s, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, s, parm));
	}

	private void setMessage(boolean isTerminalStart, boolean isStart) {
		if (isStart) {
			if (isTerminalStart) {
				setMessage("Testing start parameters");
			} else {
				setMessage("Testing end parameters");
			}
		} else {
			setMessage("Testing stopped");
		}
	}

	private void addTab(EntrainerProgramUnit unit) {
		final UnitEditorPane pane = new UnitEditorPane(unit);

		units.addTab(getTabTitle(units.getTabCount(), pane), pane);

		pane.addTimeChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				setTabTitles(units.indexOfComponent(pane));
			}
		});

		pane.addTestUnitListener(new TestUnitListener() {
			public void testUnitEventPerformed(TestUnitEvent e) {
				enableControls(e.isActionStop());

				fireReceiverChangeEvent(checkBoxPane.getFlash().isSelected(), IS_FLASH);
				fireReceiverChangeEvent(checkBoxPane.getPsychedelic().isSelected(), IS_PSYCHEDELIC);
				fireReceiverChangeEvent(animations.getAnimation().isSelected(), IS_ANIMATION);
				fireReceiverChangeEvent(pinkPanning.getPanCheck().isSelected(), PINK_PAN);
				fireReceiverChangeEvent(shimmers.getShimmer().isSelected(), IS_SHIMMER);
				String shimmer = shimmers.getShimmers().getValue();
				if (shimmer != null) fireReceiverChangeEvent(shimmer, SHIMMER_RECTANGLE);
				fireReceiverChangeEvent(e.isActionStart(), START_FLASHING);
				setMessage(e.isTerminalStart(), e.isActionStart());
			}
		});

		pane.addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) {
			}

			public void ancestorMoved(AncestorEvent event) {
			}

			public void ancestorRemoved(AncestorEvent event) {
				if (previousUnitEditorPane == pane && !cancelPressed) {
					if (validatePane(pane)) {
						setTabTitles(0);
					}
				}
			}
		});

		pane.setName(XEC_UNITS_NAME + units.getTabCount());

		if (units.getTabCount() == 1) {
			visibleUnitEditorPane = pane;
			previousUnitEditorPane = pane;
		}
	}

	private void setTabTitles(int idx) {
		setTabTitle(idx);
		for (int i = units.getSelectedIndex(); i < units.getTabCount(); i++) {
			setTabTitle(i);
		}
	}

	private void layoutComponents() {
		MigHelper mh = new MigHelper(getContentPane());
		mh.setLayoutFillX(true).setLayoutInsets(0, 0, 0, 0);

		mh.growX(100).addLast(getEntrainerAttributeWithChartPanel()).growX(100).addLast(getUnitTabPanel());

		mh.growX(100).addLast(getButtonPanel()).growX(100).add(getMessagePanel());
	}

	private Container getUnitTabPanel() {
		JPanel jp = new JPanel();
		jp.setBorder(new BevelBorder(BevelBorder.LOWERED));
		MigHelper mh = new MigHelper(jp);

		mh.setLayoutFillX(true).setLayoutInsets(0, 0, 0, 0);

		mh.add(units);

		return mh.getContainer();
	}

	private Container getUnitTabButtonPanel() {
		MigHelper mh = new MigHelper();
		mh.setLayoutFillX(true);
		mh.setLayoutInsets(5, 30, 5, 30).setLayoutGridGap(30, 0);

		mh.add(addUnit).add(removeUnit);

		return mh.getContainer();
	}

	private Container getEntrainerAttributeWithChartPanel() {
		JPanel jp = new JPanel();
		jp.setBorder(new BevelBorder(BevelBorder.RAISED));
		MigHelper mh = new MigHelper(jp);
		mh.setLayoutInsets(5, 5, 5, 50);
		mh.alignEast().add(getUnitTabButtonPanel()).add(showChart);

		return mh.getContainer();
	}

	private Container getButtonPanel() {
		JPanel jp = new JPanel();
		jp.setBorder(new BevelBorder(BevelBorder.RAISED));
		MigHelper mh = new MigHelper(jp);
		mh.setLayoutInsets(5, 15, 5, 15).setLayoutGridGap(15, 0);

		mh.alignEast().add(save);
		if (xml.getFile() != null) {
			mh.alignCenter().add(saveAs);
		}
		mh.alignWest().add(cancel);

		return mh.getContainer();
	}

	private Container getMessagePanel() {
		final GridPane gp = new GridPane();
		gp.setPadding(new Insets(10, 0, 10, 0));
		GridPane.setConstraints(checkBoxPane, 0, 0);
		GridPane.setConstraints(pinkPanning, 0, 1);
		GridPane.setConstraints(animations, 0, 2);
		GridPane.setConstraints(shimmers, 0, 3);
		gp.getChildren().addAll(checkBoxPane, pinkPanning, animations, shimmers);

		final URI css = JFXUtils.getEntrainerCSS();

		background.setImage(EntrainerFX.getInstance().getBackgroundImage());
		background.setOpacity(0.25);

		JFXUtils.runLater(new Runnable() {

			@Override
			public void run() {
				Group group = new Group();

				group.getChildren().addAll(background, gp);
				Scene scene = new Scene(group);
				if (css != null) scene.getStylesheets().add(css.toString());
				panel.setScene(scene);
			}
		});

		return panel;
	}

	private void setMessage(String s) {
		fireReceiverChangeEvent(s, MESSAGE);
	}

	/**
	 * Gets the xml file chooser.
	 *
	 * @return the xml file chooser
	 */
	public static JFileChooser getXmlFileChooser() {
		JFileChooser chooser = new JFileChooser(System.getProperty("user.dir") + "/xml");

		chooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().indexOf(".xml") > 0;
			}

			@Override
			public String getDescription() {
				return "XML files";
			}
		});
		return chooser;
	}

	private void exit() {
		if (closing) return;
		closing = true;
		cancelPressed();
		GuiUtil.fadeOutAndDispose(this, 750);
	}

}
