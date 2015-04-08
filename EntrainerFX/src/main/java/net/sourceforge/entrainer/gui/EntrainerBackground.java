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

import java.awt.Color;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.geometry.Dimension2D;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import net.sourceforge.entrainer.gui.flash.CurrentEffect;
import net.sourceforge.entrainer.gui.jfx.JFXUtils;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;
import net.sourceforge.entrainer.util.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class EntrainerBackground.
 */
public class EntrainerBackground {
	private static final Logger log = LoggerFactory.getLogger(EntrainerBackground.class);

	private static String[] picSuffixes = { ".jpg", ".JPG", ".png", ".PNG", ".gif", ".GIF", ".jpeg", ".JPEG", ".bmp",
			".BMP" };

	private Sender sender = new SenderAdapter();

	private enum BackgroundMode {
		DYNAMIC, STATIC, NO_BACKGROUND;
	}

	private BackgroundMode mode;

	private AtomicReference<ImageView> current = new AtomicReference<>();
	private String currentFile;
	private AtomicReference<ImageView> old = new AtomicReference<>();
	private AnchorPane pane = new AnchorPane();

	private Image currentImage;

	private int fadeTime = 5;
	private int displayTime = 10;
	private String directoryName = null;

	private ScheduledExecutorService switchSvc = Executors.newSingleThreadScheduledExecutor();

	private List<String> pictureNames = new ArrayList<>();

	private Random rand = new Random(System.nanoTime());

	private double width;
	private double height;

	private boolean flashBackground;

	private String backgroundPic;

	private AtomicReference<ParallelTransition> pt = new AtomicReference<>(new ParallelTransition());

	private Rectangle rect;

	private javafx.scene.paint.Color backgroundColor;

	private boolean staticPictureLock;

	private AtomicInteger ai = new AtomicInteger(1);

	private Map<Integer, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();

	private ExecutorService loadSvc = Executors.newSingleThreadExecutor();

	private AtomicBoolean backgroundChanging = new AtomicBoolean(false);

	/**
	 * Instantiates a new variable background.
	 */
	public EntrainerBackground() {
		initMediator();
		pane.setCache(true);
		pane.setCacheHint(CacheHint.SPEED);
		initDirectoryName();
	}

	private void initDirectoryName() {
		Optional<File> cssDir = Utils.getCssDir();
		setDirectoryName(cssDir.get().getAbsolutePath());
	}

	/**
	 * Clear mediator objects.
	 */
	public void stop() {
		log.debug("Stopping background");
		clearFutures();
		loadSvc.shutdownNow();
		EntrainerMediator.getInstance().removeReceiver(this);
		EntrainerMediator.getInstance().removeSender(sender);
	}

	private void init() {
		getPt().stop();
		killCurrent();
		pictureNames.clear();
		loadFromDirectory();
		JFXUtils.runLater(() -> initContent());
	}

	private void initContent() {
		getPt().stop();
		createCurrent();
		setFadeInImage();
		fadeIn().play();

		switchPictures();
		backgroundChanging.set(false);
	}

	private void loadFromDirectory() {
		getPt().stop();
		loadFromDirectory(new File(getDirectoryName()));
		backgroundChanging.set(false);
	}

	private void loadFromDirectory(File dir) {
		File[] pics = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				for (String sfx : picSuffixes) {
					if (name.endsWith(sfx)) return true;
				}

				return false;
			}
		});

		for (File pic : pics) {
			pictureNames.add(pic.getAbsolutePath());
		}

		loadFromSubDirectories(dir);
	}

	private void loadFromSubDirectories(File dir) {
		File[] directories = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

		if (directories == null || directories.length == 0) return;

		for (File f : directories) {
			loadFromDirectory(f);
		}
	}

	private void switchPictures() {
		int key = ai.getAndIncrement();
		ScheduledFuture<?> sf = switchSvc.schedule(() -> fadeInOut(key), getDisplayTime(), TimeUnit.SECONDS);
		futures.put(key, sf);
	}

	private void fadeInOut(int key) {
		ScheduledFuture<?> sf = futures.remove(key);

		if (sf == null || sf.isCancelled()) return;

		if (!isDynamic()) return;

		setOld(getCurrent());

		createCurrent();

		setFadeInImage();

		ParallelTransition pt = new ParallelTransition(fadeIn(), fadeOut());

		pt.setOnFinished(e -> switchPictures());

		this.pt.set(pt);

		JFXUtils.runLater(() -> pt.play());
	}

	private void createCurrent() {
		ImageView current = new ImageView();

		current.setOpacity(0.01);
		current.setSmooth(true);
		current.setPreserveRatio(true);
		current.setScaleX(1);
		current.setScaleY(1);
		current.setCache(true);
		current.setCacheHint(CacheHint.SPEED);

		setCurrent(current);
	}

	private void killCurrent() {
		clearFutures();
		JFXUtils.runLater(() -> clearPictures());
	}

	private void setFadeInImage() {
		currentImage = getRandomImage();
		JFXUtils.runLater(() -> scaleImage());
	}

	private Image getRandomImage() {
		int idx = rand.nextInt(pictureNames.size());

		try {
			currentFile = pictureNames.get(idx);
			log.debug("Random image {}", currentFile);
			return new Image(new FileInputStream(currentFile));
		} catch (FileNotFoundException e) {
			log.error("Unexpected exception for picture {}", pictureNames.get(idx), e);
		}

		return null;
	}

	private void scaleImage() {
		getCurrent().setImage(currentImage);

		scale();

		pane.getChildren().add(getCurrent());
	}

	private void scale() {
		Dimension2D area = new Dimension2D(getWidth(), getHeight());

		JFXUtils.scale(getCurrent(), area);
		JFXUtils.scale(getOld(), area);
	}

	private FadeTransition fadeIn() {
		ImageView im = getCurrent();
		FadeTransition fadeIn = new FadeTransition(Duration.seconds(getFadeTime()), im);

		fadeIn.setFromValue(im.getOpacity());
		fadeIn.setToValue(1);
		fadeIn.setInterpolator(Interpolator.LINEAR);

		return fadeIn;
	}

	private FadeTransition fadeOut() {
		ImageView im = getOld();
		FadeTransition fadeOut = new FadeTransition(Duration.seconds(getFadeTime()), im);

		fadeOut.setFromValue(im.getOpacity());
		fadeOut.setToValue(0.0);
		fadeOut.setInterpolator(Interpolator.LINEAR);
		fadeOut.setOnFinished(e -> pane.getChildren().remove(im));

		return fadeOut;
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this, true) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				try {
					switch (e.getParm()) {
					case APPLY_FLASH_TO_BACKGROUND:
						applyFlashEvent(e.getBooleanValue());
						break;
					case STATIC_BACKGROUND:
						if (isStatic()) return;
						mode = BackgroundMode.STATIC;
						JFXUtils.runLater(() -> evaluateStaticBackground(true));
						break;
					case DYNAMIC_BACKGROUND:
						if (isDynamic()) return;
						mode = BackgroundMode.DYNAMIC;
						loadSvc.execute(() -> init());
						break;
					case NO_BACKGROUND:
						if (isNoBackground()) return;
						mode = BackgroundMode.NO_BACKGROUND;
						JFXUtils.runLater(() -> clearBackground());
						break;
					case NO_BACKGROUND_COLOUR:
						JFXUtils.runLater(() -> setBackgroundColor(e.getColourValue()));
						break;
					case BACKGROUND_PIC:
						if (backgroundPic != null && backgroundPic.equals(e.getStringValue())) break;
						backgroundPic = e.getStringValue();
						JFXUtils.runLater(() -> evaluateStaticBackground(false));
						break;
					case BACKGROUND_PIC_DIR:
						if (backgroundChanging.get() || directoryName != null && directoryName.equals(e.getStringValue())) break;
						backgroundChanging.set(true);
						directoryName = e.getStringValue();
						if (isDynamic()) {
							loadSvc.execute(() -> init());
						} else {
							loadSvc.execute(() -> loadPictures());
						}
						break;
					case BACKGROUND_DURATION_SECONDS:
						setDisplayTime((int) e.getDoubleValue());
						break;
					case BACKGROUND_TRANSITION_SECONDS:
						setFadeTime((int) e.getDoubleValue());
						break;
					case STATIC_PICTURE_LOCK:
						staticPictureLock = e.getBooleanValue();
						break;
					case FLASH_EFFECT:
						transition(e.getEffect());
						break;
					default:
						break;
					}
				} catch (Exception f) {
					log.error("Unexpected exception ", f);
				}
			}

		});
	}

	private void loadPictures() {
		pictureNames.clear();
		loadFromDirectory();
	}

	private void applyFlashEvent(boolean b) {
		flashBackground = b;
		if (!flashBackground) {
			JFXUtils.resetEffects(pane);
		}
	}

	private void clearFutures() {
		Set<Integer> set = futures.keySet();

		for (Integer i : set) {
			ScheduledFuture<?> sf = futures.remove(i);
			if (sf != null) sf.cancel(true);
		}
	}

	private void setBackgroundColor(Color colourValue) {
		backgroundColor = JFXUtils.toJFXColor(colourValue);

		if (rect != null && rect.getFill().equals(colourValue)) return;

		if (isShowBackgroundFill()) showBackgroundFill();
	}

	private void clearBackground() {
		if (isShowBackgroundFill()) showBackgroundFill();
	}

	private void showBackgroundFill() {
		killCurrent();

		rect = new Rectangle(pane.getWidth(), pane.getHeight(), getInitialColour());

		pane.getChildren().add(rect);
	}

	private javafx.scene.paint.Color getInitialColour() {
		return backgroundColor == null ? randomColour() : backgroundColor;
	}

	private javafx.scene.paint.Color randomColour() {
		double r = rand.nextDouble();
		double g = rand.nextDouble();
		double b = rand.nextDouble();

		return new javafx.scene.paint.Color(r, g, b, 1);
	}

	private boolean isShowBackgroundFill() {
		return isNoBackground() && backgroundColor != null;
	}

	private void evaluateStaticBackground(boolean useCurrent) {
		if (!isStatic() || backgroundPic == currentFile) return;

		if (useCurrent && !staticPictureLock && currentFile != null) {
			backgroundPic = currentFile;
		}

		if (backgroundPic == null) return;

		try {
			currentImage = new Image(new FileInputStream(backgroundPic));
		} catch (FileNotFoundException e) {
			log.error("Unexpected exception", e);
			return;
		}

		killCurrent();

		clearPictures();

		createCurrent();
		scaleImage();
		fadeIn().play();

		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, backgroundPic, MediatorConstants.BACKGROUND_PIC));
	}

	private void clearPictures() {
		//@formatter:off
		for (Node node : pane.getChildren()) node.setOpacity(0);
		//@formatter:on

		pane.getChildren().clear();
	}

	private void transition(CurrentEffect effect) {
		if (flashBackground) JFXUtils.setEffect(pane, effect);
	}

	/**
	 * Gets the mode.
	 *
	 * @return the mode
	 */
	public BackgroundMode getMode() {
		return mode;
	}

	/**
	 * Checks if is dynamic.
	 *
	 * @return true, if is dynamic
	 */
	public boolean isDynamic() {
		return isMode(BackgroundMode.DYNAMIC);
	}

	/**
	 * Checks if is static.
	 *
	 * @return true, if is static
	 */
	public boolean isStatic() {
		return isMode(BackgroundMode.STATIC);
	}

	/**
	 * Checks if is no background.
	 *
	 * @return true, if is no background
	 */
	public boolean isNoBackground() {
		return isMode(BackgroundMode.NO_BACKGROUND);
	}

	private boolean isMode(BackgroundMode mode) {
		return this.mode == mode;
	}

	/**
	 * Gets the fade time.
	 *
	 * @return the fade time
	 */
	public int getFadeTime() {
		return fadeTime;
	}

	/**
	 * Sets the fade time.
	 *
	 * @param fadeTime
	 *          the new fade time
	 */
	public void setFadeTime(int fadeTime) {
		this.fadeTime = fadeTime;
	}

	/**
	 * Gets the display time.
	 *
	 * @return the display time
	 */
	public int getDisplayTime() {
		return displayTime;
	}

	/**
	 * Sets the display time.
	 *
	 * @param displayTime
	 *          the new display time
	 */
	public void setDisplayTime(int displayTime) {
		this.displayTime = displayTime;
	}

	/**
	 * Gets the directory name.
	 *
	 * @return the directory name
	 */
	public String getDirectoryName() {
		return directoryName;
	}

	/**
	 * Sets the directory name.
	 *
	 * @param directoryName
	 *          the new directory name
	 */
	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}

	/**
	 * Gets the pane.
	 *
	 * @return the pane
	 */
	public Pane getPane() {
		return pane;
	}

	/**
	 * Gets the current image.
	 *
	 * @return the current image
	 */
	public Image getCurrentImage() {
		return getCurrent().getImage();
	}

	/**
	 * Sets the dimension.
	 *
	 * @param width
	 *          the width
	 * @param height
	 *          the height
	 */
	public void setDimension(double width, double height) {
		setWidth(width);
		setHeight(height);

		if (rect != null) {
			rect.setWidth(width);
			rect.setHeight(height);
		}

		scale();
	}

	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Sets the width.
	 *
	 * @param width
	 *          the new width
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Sets the height.
	 *
	 * @param height
	 *          the new height
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	private ImageView getCurrent() {
		return current.get();
	}

	private ImageView getOld() {
		return old.get();
	}

	private void setCurrent(ImageView current) {
		this.current.set(current);
	}

	private void setOld(ImageView old) {
		this.old.set(old);
	}

	private ParallelTransition getPt() {
		return pt.get();
	}
}
