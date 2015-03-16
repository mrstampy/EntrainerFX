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
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import net.sourceforge.entrainer.gui.jfx.JFXUtils;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class EntrainerBackground.
 */
public class EntrainerBackground {
	private static final double FLASH_OPACITY = 1.0;

	private static final double NORMAL_OPACITY = 0.5;

	private static final Logger log = LoggerFactory.getLogger(EntrainerBackground.class);

	private static String[] picSuffixes = { ".jpg", ".JPG", ".png", ".PNG", ".gif", ".GIF", ".jpeg", ".JPEG", ".bmp",
			".BMP" };

	private Sender sender = new SenderAdapter();

	private enum BackgroundMode {
		DYNAMIC, STATIC, NO_BACKGROUND;
	}

	private BackgroundMode mode;

	private ImageView current;
	private String currentFile;
	private ImageView old;
	private AnchorPane pane = new AnchorPane();

	private FadeTransition fadeIn = new FadeTransition();
	private FadeTransition fadeOut = new FadeTransition();

	private Image currentImage;

	private int fadeTime = 5;
	private int displayTime = 10;
	private String directoryName = "css";

	private ScheduledExecutorService switchSvc = Executors.newSingleThreadScheduledExecutor();

	private List<String> pictureNames = new ArrayList<>();

	private Random rand = new Random(System.nanoTime());

	private double width;
	private double height;

	private boolean flashBackground;

	private String backgroundPic;

	private ParallelTransition pt;

	private Rectangle rect;

	private javafx.scene.paint.Color backgroundColor;

	private boolean staticPictureLock;

	private AtomicInteger ai = new AtomicInteger(1);

	private Map<Integer, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();

	private boolean ptRunning;

	private boolean psychedelic;
	
	private Lock scaleLock = new ReentrantLock();

	/**
	 * Instantiates a new variable background.
	 */
	public EntrainerBackground() {
		initMediator();
	}

	/**
	 * Clear mediator objects.
	 */
	public void clearMediatorObjects() {
		EntrainerMediator.getInstance().removeReceiver(this);
		EntrainerMediator.getInstance().removeSender(sender);
	}

	private void init() {
		killCurrent();
		pictureNames.clear();
		loadFromDirectory();
		JFXUtils.runLater(() -> initContent());
	}

	private void initContent() {
		clearPictures();

		createCurrent();
		setFadeInImage();
		fadeIn();
		fadeIn.play();

		switchPictures();
	}

	private void loadFromDirectory() {
		loadFromDirectory(new File(getDirectoryName()));
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
		ptRunning = false;
		int key = ai.getAndIncrement();
		ScheduledFuture<?> sf = switchSvc.schedule(() -> fadeInOut(key), getDisplayTime(), TimeUnit.SECONDS);
		futures.put(key, sf);
	}

	private void fadeInOut(int key) {
		ptRunning = true;
		ScheduledFuture<?> sf = futures.remove(key);

		if (sf == null || sf.isCancelled()) return;

		if (!isDynamic()) {
			return;
		}

		old = current;

		createCurrent();

		setFadeInImage();

		fadeOut();
		fadeIn();

		pt = new ParallelTransition(fadeIn, fadeOut);

		pt.setOnFinished(e -> switchPictures());

		JFXUtils.runLater(() -> pt.play());
	}

	private void createCurrent() {
		current = new ImageView();
		current.setOpacity(0.01);
		current.setSmooth(true);
		current.setPreserveRatio(true);
		current.setScaleX(1);
		current.setScaleY(1);
	}

	private void killCurrent() {
		clearFutures();
		clearPictures();
	}

	private void setFadeInImage() {
		int idx = rand.nextInt(pictureNames.size());

		try {
			currentFile = pictureNames.get(idx);
			currentImage = new Image(new FileInputStream(currentFile));
			JFXUtils.runLater(() -> scaleImage());
		} catch (FileNotFoundException e) {
			log.error("Unexpected exception for picture {}", pictureNames.get(idx), e);
		}
	}

	private void scaleImage() {
		current.setImage(currentImage);

		scale();

		if (pane.getChildren().contains(current)) {
			System.out.println("ping");
			setFadeInImage();
		}

		pane.getChildren().add(current);
	}

	private void scale() {
		scaleLock.lock();
		try {
			scale(current);
			scale(old);
		} finally {
			scaleLock.unlock();
		}
	}
	
	private void scale(ImageView view) {
		if(view == null || view.getOpacity() == 0 || view.getImage() == null) return;
		
		double pw = view.getImage().getWidth();
		double ph = view.getImage().getHeight();
		System.out.println("Image: " + pw + ", " + ph);
		
		double vw = getWidth();
		double vh = getHeight();
		System.out.println("Background: " + vw + ", " + vh);

		double xr = Math.abs(1 - (pw - vw) / pw);
		double yr = Math.abs(1 - (ph - vh) / ph);
		
		System.out.println("xr: " + xr + ", yr: " + yr);

		double yDiff = (ph * vw / pw);
		double xDiff = (pw * vh / ph);

		if (xr >= yr) {
			view.setFitWidth(vw);
		} else {
			view.setFitHeight(vh);
		}
		
		System.out.println("xDiff: " + xDiff + ", yDiff: " + yDiff);
		
		if (xDiff > vw) {
			view.setX(0 - (xDiff - vw) / 2);
		} else {
			view.setX(0);
		}

		if (yDiff > vh) {
			view.setY(0 - (yDiff - vh) / 2);
		} else {
			view.setY(0);
		}
	}

	private void fadeIn() {
		fadeIn = new FadeTransition(Duration.seconds(getFadeTime()), current);
		fadeIn.setFromValue(current.getOpacity());
		fadeIn.setToValue(NORMAL_OPACITY);
		fadeIn.setInterpolator(Interpolator.LINEAR);
	}

	private void fadeOut() {
		fadeOut = new FadeTransition(Duration.seconds(getFadeTime()), old);
		fadeOut.setFromValue(old.getOpacity());
		fadeOut.setToValue(0.0);
		fadeOut.setInterpolator(Interpolator.LINEAR);
		fadeOut.setOnFinished(e -> pane.getChildren().remove(old));
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case FLASH_BACKGROUND:
					flashBackground = e.getBooleanValue();
					break;
				case STATIC_BACKGROUND:
					if (isStatic()) return;
					mode = BackgroundMode.STATIC;
					JFXUtils.runLater(() -> evaluateStaticBackground(true));
					break;
				case DYNAMIC_BACKGROUND:
					if (isDynamic()) return;
					mode = BackgroundMode.DYNAMIC;
					JFXUtils.runLater(() -> init());
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
					backgroundPic = e.getStringValue();
					JFXUtils.runLater(() -> evaluateStaticBackground(false));
					break;
				case BACKGROUND_PIC_DIR:
					directoryName = e.getStringValue();
					if (isDynamic()) {
						JFXUtils.runLater(() -> init());
					} else {
						pictureNames.clear();
						loadFromDirectory();
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
				case IS_PSYCHEDELIC:
					psychedelic = e.getBooleanValue();
					break;
				case ENTRAINMENT_FREQUENCY_PULSE:
					transition(isNoBackground() ? rect : current, e.getBooleanValue());
					break;
				default:
					break;
				}
			}

		});
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
		return psychedelic || backgroundColor == null ? randomColour() : backgroundColor;
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
		current.setOpacity(NORMAL_OPACITY);

		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, backgroundPic, MediatorConstants.BACKGROUND_PIC));
	}

	private void clearPictures() {
		//@formatter:off
		for (Node node : pane.getChildren()) node.setOpacity(0);
		//@formatter:on

		pane.getChildren().clear();
	}

	private void transition(Node background, boolean b) {
		if (b) {
			if (canRun(background) && !ptRunning) JFXUtils.runLater(() -> invert(background));
		} else {
			if (background.getOpacity() > 0) JFXUtils.runLater(() -> reset(background));
		}
	}

	private boolean canRun(Node background) {
		return shouldRun() && background.getOpacity() > 0;
	}

	private void invert(Node background) {
		if (flashBackground) {
			setBackgroundOpacity(background);
		}

		if (psychedelic && background instanceof Rectangle) {
			((Rectangle) background).setFill(randomColour());
		}
	}

	private void setBackgroundOpacity(Node background) {
		double o = background.getOpacity() == NORMAL_OPACITY ? FLASH_OPACITY : NORMAL_OPACITY;

		background.setOpacity(o);
	}

	private void reset(Node background) {
		background.setOpacity(NORMAL_OPACITY);
	}

	private boolean shouldRun() {
		return flashBackground || pane.getChildren().contains(rect);
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
		return current.getImage();
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
}
