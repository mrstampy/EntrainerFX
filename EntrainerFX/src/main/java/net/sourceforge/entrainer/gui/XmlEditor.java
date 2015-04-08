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

import static net.sourceforge.entrainer.mediator.MediatorConstants.ANIMATION_BACKGROUND;
import static net.sourceforge.entrainer.mediator.MediatorConstants.ANIMATION_PROGRAM;
import static net.sourceforge.entrainer.mediator.MediatorConstants.IS_ANIMATION;
import static net.sourceforge.entrainer.mediator.MediatorConstants.IS_SHIMMER;
import static net.sourceforge.entrainer.mediator.MediatorConstants.MESSAGE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_PAN;
import static net.sourceforge.entrainer.mediator.MediatorConstants.SHIMMER_RECTANGLE;
import static net.sourceforge.entrainer.xml.program.EntrainerProgramUtil.marshal;
import static net.sourceforge.entrainer.xml.program.EntrainerProgramUtil.unmarshal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.entrainer.gui.flash.FlashType;
import net.sourceforge.entrainer.gui.jfx.AnimationPane;
import net.sourceforge.entrainer.gui.jfx.BackgroundPicturePane;
import net.sourceforge.entrainer.gui.jfx.ControlButtonFactory;
import net.sourceforge.entrainer.gui.jfx.FlashOptionsPane;
import net.sourceforge.entrainer.gui.jfx.JFXUtils;
import net.sourceforge.entrainer.gui.jfx.MediaPlayerPane;
import net.sourceforge.entrainer.gui.jfx.ShimmerOptionsPane;
import net.sourceforge.entrainer.gui.jfx.SliderControlPane;
import net.sourceforge.entrainer.guitools.GridPaneHelper;
import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;
import net.sourceforge.entrainer.util.Utils;
import net.sourceforge.entrainer.xml.program.EntrainerProgram;
import net.sourceforge.entrainer.xml.program.EntrainerProgramInterval;
import net.sourceforge.entrainer.xml.program.EntrainerProgramUnit;
import net.sourceforge.entrainer.xml.program.UnitSetter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

// TODO: Auto-generated Javadoc
/**
 * The editor dialog for entrainer xml files (Entrainer Programs).
 * 
 * @author burton
 */
public class XmlEditor extends Stage {

	private static final Logger log = LoggerFactory.getLogger(XmlEditor.class);

	private EntrainerProgram xml;

	private TabPane units = new TabPane();

	private Button save = new Button("Save");
	private Button saveAs = new Button("Save As");
	private Button cancel = new Button("Cancel");

	private Button removeUnit = new Button();
	private Button addUnit = new Button();

	private UnitEditorPane visibleUnitEditorPane;
	private UnitEditorPane previousUnitEditorPane;

	private SimpleDateFormat titleFormat = new SimpleDateFormat("mm:ss");
	private Calendar titleGenerator = Calendar.getInstance();

	private AnimationPane animations = new AnimationPane();
	private ShimmerOptionsPane shimmers = new ShimmerOptionsPane();
	private SliderControlPane pinkPan = new SliderControlPane(false);
	private BackgroundPicturePane pics = new BackgroundPicturePane();
	private FlashOptionsPane flashOptions = new FlashOptionsPane();
	private MediaPlayerPane mediaOptions = new MediaPlayerPane();

	private boolean cancelPressed = false;

	private List<XmlFileSaveListener> fileSaveListeners = new ArrayList<XmlFileSaveListener>();

	private IntervalMenu intervalMenu = new IntervalMenu();

	private Sender sender = new SenderAdapter();
	private EntrainerBackground background = new EntrainerBackground();

	private volatile boolean closing = false;
	private GridPane gp;

	private EntrainerFXResizer resizer;
	private MenuBar bar;

	/**
	 * Instantiates a new xml editor.
	 *
	 * @param owner
	 *          the owner
	 * @param file
	 *          the file
	 */
	public XmlEditor(Stage owner, File file) {
		super(StageStyle.TRANSPARENT);
		initOwner(owner);
		init(file);
		unexpandTitledPanes();
	}

	private void unexpandTitledPanes() {
		unexpandeTitledPane(animations);
		unexpandeTitledPane(shimmers);
		unexpandeTitledPane(pinkPan);
		unexpandeTitledPane(pics);
		unexpandeTitledPane(flashOptions);
		unexpandeTitledPane(mediaOptions);
	}

	private void unexpandeTitledPane(TitledPane tp) {
		tp.setExpanded(false);
		tp.setOpacity(0);
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
		initUi();
		if (f != null) {
			setMessage("Loaded " + f.getName());
		}

		resizer = new EntrainerFXResizer(e -> resizeDimensions(e));

		addEventHandler(MouseEvent.MOUSE_CLICKED, e -> showDoc(e));

		addEventHandler(MouseEvent.MOUSE_CLICKED, e -> resizer.onClick(e));
		addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> resizer.onDrag(e));
		addEventHandler(MouseEvent.MOUSE_RELEASED, e -> resizer.onRelease(e));

		setOnShown(e -> setResizer());
	}

	private void setResizer() {
		Rectangle2D size = new Rectangle2D(getX(), getY(), getWidth(), getHeight());
		resizer.setSize(size);
		background.setDimension(size.getWidth(), size.getHeight());
	}

	private void resizeDimensions(Rectangle2D r) {
		if (getX() != r.getMinX()) {
			setX(r.getMinX());
		}

		if (getY() != r.getMinY()) {
			setY(r.getMinY());
		}

		if (getWidth() != r.getWidth()) {
			setWidth(r.getWidth());
		}

		if (getHeight() != r.getHeight()) {
			setHeight(r.getHeight());
		}

		setJFXSize(r);
	}

	private void setJFXSize(Rectangle2D size) {
		double width = size.getWidth();

		gp.setPrefSize(width, size.getHeight());

		setTitledPaneWidth(pics, width);
		setTitledPaneWidth(animations, width);
		setTitledPaneWidth(pinkPan, width);
		setTitledPaneWidth(flashOptions, width);
		setTitledPaneWidth(shimmers, width);

		background.setDimension(size.getWidth(), size.getHeight());
	}

	private void setTitledPaneWidth(TitledPane tp, double width) {
		tp.setPrefWidth(width);
	}

	private void showDoc(javafx.scene.input.MouseEvent e) {
		if (!(e.isMetaDown() && e.getClickCount() == 1)) return;

		Utils.openLocalDocumentation("editing.html");
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addSender(sender);
	}

	private void addMenuBar() {
		bar = new MenuBar();
		bar.getMenus().add(intervalMenu);

		Menu help = new Menu("Help");

		MenuItem doc = new MenuItem("Documentation");
		doc.setOnAction(e -> Utils.openLocalDocumentation("editing.html"));
		help.getItems().add(doc);

		bar.getMenus().add(help);
		bar.setOpacity(0.75);
	}

	private void addListeners() {
		save.setOnAction(e -> savePressed(true));
		save.setTooltip(new Tooltip("Save the Entrainer Program"));

		saveAs.setOnAction(e -> savePressed(false));
		saveAs.setTooltip(new Tooltip("Save Entrainer Program As..."));

		cancel.setOnAction(e -> fadeOut());
		cancel.setTooltip(new Tooltip("Cancel Entrainer Program Changes"));

		setOnHiding(e -> exit());

		removeUnit.setOnAction(e -> removeUnitPressed());
		removeUnit.setTooltip(new Tooltip("Remove the Current Unit"));
		ControlButtonFactory.decorateButton(removeUnit, "/delete.png", "/delete-Hot.png");

		addUnit.setOnAction(e -> addUnitPressed());
		addUnit.setTooltip(new Tooltip("Add a Unit"));
		ControlButtonFactory.decorateButton(addUnit, "/add.png", "/add-Hot.png");

		units.getSelectionModel().selectedIndexProperty().addListener(e -> switchEditors());
	}

	private void fadeOut() {
		cancelPressed();
		Timeline tl = new Timeline(new KeyFrame(Duration.millis(500), new KeyValue(opacityProperty(), 0)));
		tl.setOnFinished(e -> hide());
		tl.play();
	}

	private void switchEditors() {
		previousUnitEditorPane = visibleUnitEditorPane;
		visibleUnitEditorPane = getUnitEditorPane(units.getSelectionModel().getSelectedIndex());
	}

	private void savePressed(boolean isSave) {
		if (units.getTabs().isEmpty()) {
			showError("No units specified", "Missing Units");
			return;
		}

		if (validatePane(getUnitEditorPane(units.getSelectionModel().getSelectedIndex()))) {
			try {
				saveXmlFile(isSave);
			} catch (Exception e) {
				GuiUtil.handleProblem(e);
			}
		}
	}

	private void showError(String content, String title) {
		Alert alert = new Alert(AlertType.ERROR, content, ButtonType.OK);
		alert.initOwner(this);
		alert.setTitle(title);

		alert.showAndWait();
	}

	private void cancelPressed() {
		cancelPressed = true;
		clearMediatorObjects();
		intervalMenu.removeAllIntervals();
	}

	private void clearMediatorObjects() {
		for (int i = 0; i < units.getTabs().size(); i++) {
			getUnitEditorPane(i).clearMediatorObjects();
		}
		xml.clearMediatorObjects();
		intervalMenu.clearMediatorObjects();
		animations.clearMediatorObjects();
		pinkPan.clearMediatorObjects();
		shimmers.clearMediatorObjects();
		mediaOptions.clearMediatorObjects();
		pics.clearMediatorObjects();
		flashOptions.clearMediatorObjects();
		background.stop();
	}

	private void saveXmlFile(boolean isSave) throws ParserConfigurationException, SAXException, IOException {
		if (xml.getFile() == null || !isSave) {
			FileChooser fc = new FileChooser();
			fc.setInitialDirectory(Utils.getEntrainerProgramDir().get());
			fc.setSelectedExtensionFilter(new ExtensionFilter("EntrainerFX Programs", "xml"));
			fc.setTitle("EntrainerFX Programs");

			File f = fc.showOpenDialog(this);
			if (f == null) return;

			f = processFile(f);
			if (!isValidFile(f)) {
				showError("The file " + f.getName() + " is invalid", "Invalid File Name");
				return;
			} else {
				xml.setFile(f);
			}
		}

		if (xml.getFile().exists()) {
			Alert alert = new Alert(AlertType.CONFIRMATION, "File " + xml.getFile().getName() + " already exists. Continue?",
					ButtonType.OK, ButtonType.CANCEL);
			alert.initOwner(this);
			alert.setTitle("File Exists");

			Optional<ButtonType> bt = alert.showAndWait();

			if (!bt.isPresent() || bt.get() != ButtonType.OK) return;
		}

		xml.setUnits(getUnits());
		xml.setAnimation(animations.getRunAnimation().isSelected());
		xml.setPinkPan(pinkPan.getPanCheck().isSelected());
		xml.setShimmer(shimmers.getShimmer().isSelected());
		xml.setFlashAnimation(animations.isFlashAnimation());
		xml.setFlashEntrainerFX(flashOptions.isFlashEntrainerFX());
		xml.setFlashMedia(mediaOptions.isFlashMedia());
		xml.setFlashShimmer(shimmers.isFlashShimmer());
		xml.setAnimationBackground(animations.getAnimationBackgroundPicture());
		xml.setAnimationProgram(animations.getSelectedAnimationName());
		xml.setUseColourAsBackground(animations.getUseColourAsBackground().isSelected());
		xml.setIntervals(intervalMenu.getLoadedIntervals());
		xml.setShimmerName(shimmers.getShimmers().getValue());

		xml.setMediaEntrainment(mediaOptions.isMediaEntrainment());
		xml.setMediaLoop(mediaOptions.isMediaLoop());
		xml.setMediaUri(mediaOptions.getMediaURI());

		xml.setBackgroundColour(JFXUtils.fromJFXColor(pics.getBackgroundColour()));
		xml.setDynamicDuration(pics.getDuration());
		xml.setDynamicPicture(pics.isDynamic());
		xml.setDynamicTransition(pics.getTransition());
		xml.setFlashBackground(pics.isFlashBackground());
		xml.setNoPicture(pics.isNoBackground());
		xml.setPictureDirectory(pics.getPictureDirectory());
		xml.setStaticPicture(pics.isStatic());
		xml.setStaticPictureFile(pics.getStaticPicture());
		xml.setStaticPictureLock(pics.isPictureLock());

		xml.setOpacity(flashOptions.isOpacity());
		xml.setBloom(flashOptions.isBloom());
		xml.setBoxBlur(flashOptions.isBoxBlur());
		xml.setGaussianBlur(flashOptions.isGaussianBlur());
		xml.setGlow(flashOptions.isGlow());
		xml.setMotionBlur(flashOptions.isMotionBlur());
		xml.setSepiaTone(flashOptions.isSepiaTone());
		xml.setShadow(flashOptions.isShadow());
		xml.setLighting(flashOptions.isLighting());
		xml.setColourAdjust(flashOptions.isColourAdjust());

		try {
			marshal(xml, xml.getFile().getAbsolutePath());
		} catch (Exception e) {
			GuiUtil.handleProblem(e);
		}

		setMessage("Saved " + xml.getFile().getName());

		fireXmlFileSaveEvent(xml.getFile());
	}

	private File processFile(File selected) {
		return isUntypedFile(selected) ? new File(selected.getAbsolutePath() + ".xml") : selected;
	}

	private boolean isUntypedFile(File f) {
		return f.getName().indexOf(".") == -1;
	}

	private boolean isValidFile(File f) {
		return f.getName().indexOf(".xml") == f.getName().length() - 4;
	}

	private List<EntrainerProgramUnit> getUnits() {
		List<EntrainerProgramUnit> list = new LinkedList<EntrainerProgramUnit>();

		for (int i = 0; i < units.getTabs().size(); i++) {
			list.add(getUnitEditorPane(i).getUnit());
		}

		return list;
	}

	private void removeUnitPressed() {
		if (units.getTabs().size() > 0) {
			Alert alert = new Alert(AlertType.CONFIRMATION, "About to remove this unit.  Continue?", ButtonType.OK,
					ButtonType.CANCEL);
			alert.setTitle("Remove Unit");
			alert.initOwner(this);
			Optional<ButtonType> bt = alert.showAndWait();
			if (bt.isPresent() && bt.get() == ButtonType.OK) {
				int idx = units.getSelectionModel().getSelectedIndex();
				UnitEditorPane pane = getUnitEditorPane(idx);
				pane.removeListener();
				pane.clearMediatorObjects();
				units.getTabs().remove(idx);
				for (int i = idx; i < units.getTabs().size(); i++) {
					setTabTitle(i);
				}

				if (idx < units.getTabs().size()) {
					UnitEditorPane replacement = getUnitEditorPane(idx);
					replacement.fireAllChanged();
				}
			}
		}
	}

	private void addUnitPressed() {
		EntrainerProgramUnit unit = new EntrainerProgramUnit();
		if (units.getTabs().size() > 0) {
			setLastFromPrevious(unit);
		}
		addTab(unit);

		units.getSelectionModel().select(units.getTabs().size() - 1);
	}

	private void setLastFromPrevious(EntrainerProgramUnit unit) {
		UnitEditorPane pane = getUnitEditorPane(units.getTabs().size() - 1);
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
		return (UnitEditorPane) units.getTabs().get(idx);
	}

	private void initFields() {
		animations.getRunAnimation().setSelected(xml.isAnimation());
		if (xml.getAnimationProgram() != null) {
			animations.refreshAnimations();
			fireReceiverChangeEvent(xml.getAnimationProgram(), ANIMATION_PROGRAM);
		}
		animations.getUseColourAsBackground().setSelected(xml.isUseColourAsBackground());

		pinkPan.getPanCheck().setSelected(xml.isPinkPan());
		shimmers.getShimmer().setSelected(xml.isShimmer());
		if (xml.getAnimationBackground() != null) {
			fireReceiverChangeEvent(xml.getAnimationBackground(), ANIMATION_BACKGROUND);
		}
		if (!xml.getIntervals().isEmpty()) intervalMenu.loadIntervals(getIntervals(xml.getIntervals()));
		if (xml.getShimmerName() != null) shimmers.getShimmers().setValue(xml.getShimmerName());

		pics.setFlashBackground(xml.isFlashBackground());
		fireReceiverChangeEvent(pics.isFlashBackground(), MediatorConstants.APPLY_FLASH_TO_BACKGROUND);

		animations.setFlashAnimation(xml.isFlashAnimation());
		fireReceiverChangeEvent(animations.isFlashAnimation(), MediatorConstants.APPLY_FLASH_TO_ANIMATION);

		shimmers.setFlashShimmer(xml.isFlashShimmer());
		fireReceiverChangeEvent(shimmers.isFlashShimmer(), MediatorConstants.APPLY_FLASH_TO_SHIMMER);

		flashOptions.setFlashEntrainerFX(xml.isFlashEntrainerFX());
		fireReceiverChangeEvent(flashOptions.isFlashEntrainerFX(), MediatorConstants.APPLY_FLASH_TO_ENTRAINER_FX);

		pics.setDuration(xml.getDynamicDuration());
		fireReceiverChangeEvent(pics.getDuration(), MediatorConstants.BACKGROUND_DURATION_SECONDS);

		pics.setTransition(xml.getDynamicTransition());
		fireReceiverChangeEvent(pics.getTransition(), MediatorConstants.BACKGROUND_TRANSITION_SECONDS);

		pics.setBackgroundColor(JFXUtils.toJFXColor(xml.getBackgroundColour()));
		fireReceiverChangeEvent(xml.getBackgroundColour(), MediatorConstants.NO_BACKGROUND_COLOUR);

		pics.setPictureDirectory(xml.getPictureDirectory());
		fireReceiverChangeEvent(pics.getPictureDirectory(), MediatorConstants.BACKGROUND_PIC_DIR);

		if (xml.getStaticPictureFile() != null) {
			pics.setStaticPicture(xml.getStaticPictureFile());
			fireReceiverChangeEvent(pics.getStaticPicture(), MediatorConstants.BACKGROUND_PIC);
		}

		mediaOptions.setFlashMedia(xml.isFlashMedia());
		fireReceiverChangeEvent(xml.isFlashMedia(), MediatorConstants.APPLY_FLASH_TO_MEDIA);

		mediaOptions.setMediaEntrainment(xml.isMediaEntrainment());
		fireReceiverChangeEvent(xml.isMediaEntrainment(), MediatorConstants.MEDIA_ENTRAINMENT);

		mediaOptions.setMediaURI(xml.getMediaUri());
		fireReceiverChangeEvent(xml.getMediaUri(), MediatorConstants.MEDIA_URI);

		mediaOptions.setMediaLoop(xml.isMediaLoop());

		pics.setPictureLock(xml.isStaticPictureLock());
		fireReceiverChangeEvent(pics.isPictureLock(), MediatorConstants.STATIC_PICTURE_LOCK);

		pics.setDynamic(xml.isDynamicPicture());
		if (pics.isDynamic()) fireReceiverChangeEvent(true, MediatorConstants.DYNAMIC_BACKGROUND);

		pics.setStatic(xml.isStaticPicture());
		if (pics.isStatic()) fireReceiverChangeEvent(true, MediatorConstants.STATIC_BACKGROUND);

		pics.setNoBackground(xml.isNoPicture());
		if (pics.isNoBackground()) fireReceiverChangeEvent(true, MediatorConstants.NO_BACKGROUND);

		initFlashOptions();
	}

	private void initFlashOptions() {
		flashOptions.setOpacity(xml.isOpacity());
		fireFlashOptionEvent(FlashType.OPACITY, xml.isOpacity());

		flashOptions.setBloom(xml.isBloom());
		fireFlashOptionEvent(FlashType.BLOOM, xml.isBloom());

		flashOptions.setBoxBlur(xml.isBoxBlur());
		fireFlashOptionEvent(FlashType.BOX_BLUR, xml.isBoxBlur());

		flashOptions.setGaussianBlur(xml.isGaussianBlur());
		fireFlashOptionEvent(FlashType.GAUSSIAN_BLUR, xml.isGaussianBlur());

		flashOptions.setGlow(xml.isGlow());
		fireFlashOptionEvent(FlashType.GLOW, xml.isGlow());

		flashOptions.setMotionBlur(xml.isMotionBlur());
		fireFlashOptionEvent(FlashType.MOTION_BLUR, xml.isMotionBlur());

		flashOptions.setSepiaTone(xml.isSepiaTone());
		fireFlashOptionEvent(FlashType.SEPIA_TONE, xml.isSepiaTone());

		flashOptions.setShadow(xml.isShadow());
		fireFlashOptionEvent(FlashType.SHADOW, xml.isShadow());

		flashOptions.setLighting(xml.isLighting());
		fireFlashOptionEvent(FlashType.LIGHTING, xml.isLighting());

		flashOptions.setColourAdjust(xml.isColourAdjust());
		fireFlashOptionEvent(FlashType.COLOUR_ADJUST, xml.isColourAdjust());
	}

	private void fireFlashOptionEvent(FlashType type, boolean b) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, type, b, MediatorConstants.FLASH_TYPE));
	}

	private List<String> getIntervals(List<EntrainerProgramInterval> intervals) {
		List<String> list = new ArrayList<String>();

		for (EntrainerProgramInterval epi : intervals) {
			list.add(epi.getValue());
		}

		return list;
	}

	private void createTabs() {
		for (EntrainerProgramUnit unit : xml.getUnits()) {
			addTab(unit);
		}

		if (units.getTabs().isEmpty()) {
			addTab(new EntrainerProgramUnit());
		}
	}

	private boolean validatePane(UnitEditorPane pane) {
		String s = pane.validateFields();
		if (s.trim().length() > 0) {
			try {
				units.getSelectionModel().select(pane);
				showError(s, "Errors");
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
		Tab selected = units.getTabs().get(idx);
		selected.setText(getTabTitle(idx));
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

	private void disableControls(boolean b) {
		cancel.setDisable(b);
		save.setDisable(b);
		saveAs.setDisable(b);
		units.setDisable(b);
		removeUnit.setDisable(b);
		addUnit.setDisable(b);
	}

	private void fireReceiverChangeEvent(boolean b, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, b, parm));
	}

	private void fireReceiverChangeEvent(String s, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, s, parm));
	}

	private void fireReceiverChangeEvent(int i, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, i, parm));
	}

	private void fireReceiverChangeEvent(java.awt.Color c, MediatorConstants parm) {
		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, c, parm));
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

		String tabTitle = getTabTitle(units.getTabs().size(), pane);
		pane.setText(tabTitle);
		units.getTabs().add(pane);

		pane.addTimeChangeListener(e -> setTabTitles(units.getTabs().indexOf(pane)));

		pane.addTestUnitListener(new TestUnitListener() {
			public void testUnitEventPerformed(TestUnitEvent e) {
				disableControls(!e.isActionStop());

				fireReceiverChangeEvent(animations.getRunAnimation().isSelected(), IS_ANIMATION);
				fireReceiverChangeEvent(pinkPan.getPanCheck().isSelected(), PINK_PAN);
				fireReceiverChangeEvent(shimmers.getShimmer().isSelected(), IS_SHIMMER);
				String shimmer = shimmers.getShimmers().getValue();
				if (shimmer != null) fireReceiverChangeEvent(shimmer, SHIMMER_RECTANGLE);
				setMessage(e.isTerminalStart(), e.isActionStart());
			}
		});

		units.getTabs().addListener(new ListChangeListener<Tab>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Tab> c) {
				try {
					if (c.wasRemoved() && previousUnitEditorPane == pane && !cancelPressed) {
						if (validatePane(pane)) {
							setTabTitles(0);
						}
					}
				} catch (IllegalStateException e) {
					// e.printStackTrace();
				}
			}

		});

		if (units.getTabs().size() == 1) {
			visibleUnitEditorPane = pane;
			previousUnitEditorPane = pane;
		}
	}

	private void setTabTitles(int idx) {
		setTabTitle(idx);
		for (int i = units.getSelectionModel().getSelectedIndex(); i < units.getTabs().size(); i++) {
			setTabTitle(i);
		}
	}

	private Node getTopButtons() {
		HBox box = new HBox(10, addUnit, removeUnit);
		box.setAlignment(Pos.CENTER);
		return box;
	}

	private Node getBottomButtons() {
		HBox box = new HBox(10, save, saveAs, cancel);
		box.setAlignment(Pos.CENTER);
		return box;
	}

	private void initUi() {
		Node topButtons = getTopButtons();
		Node bottomButtons = getBottomButtons();

		GridPaneHelper gph = new GridPaneHelper();
		//@formatter:off
		gph
			.addLast(bar)
			.addLast(topButtons)
			.addLast(units)
			.addLast(bottomButtons)
			.addLast(pinkPan)
			.addLast(mediaOptions)
			.addLast(pics)
			.addLast(shimmers)
			.addLast(animations)
			.addLast(flashOptions)
			.alignment(Pos.TOP_CENTER)
			.padding(new Insets(0, 0, 10, 0));
		//@formatter:on

		gp = gph.getPane();

		final URI css = JFXUtils.getEntrainerCSS();
		Group group = new Group();

		group.getChildren().addAll(background.getPane(), gp);
		Scene scene = new Scene(group, Color.BLACK);
		if (css != null) scene.getStylesheets().add(css.toString());

		setScene(scene);
		setHeight(950);
	}

	private void setMessage(String s) {
		fireReceiverChangeEvent(s, MESSAGE);
	}

	private void exit() {
		if (closing) return;
		closing = true;
		cancelPressed();
	}

}
