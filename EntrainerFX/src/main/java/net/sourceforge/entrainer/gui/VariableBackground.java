package net.sourceforge.entrainer.gui;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VariableBackground {
	private static final Logger log = LoggerFactory.getLogger(VariableBackground.class);

	private ImageView current;
	private ImageView old;
	private AnchorPane stackPane = new AnchorPane();

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

	public void start() {
		Platform.runLater(() -> init());
	}

	public void stop() {

	}

	private void init() {
		File dir = new File(getDirectoryName());

		pictureNames.clear();
		loadFromDirectory(dir);
		
		createCurrent();
		setFadeInImage();
		fadeIn();
		fadeIn.play();
		
		switchPictures();
	}

	private void loadFromDirectory(File dir) {
		File[] pics = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				//@formatter:off
				return name.endsWith(".jpg")
					|| name.endsWith(".JPG")
					|| name.endsWith(".gif")
					|| name.endsWith(".GIF")
					|| name.endsWith(".png")
					|| name.endsWith(".PNG")
					|| name.endsWith(".jpeg")
					|| name.endsWith(".JPEG");
				//@formatter:on
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
		switchSvc.schedule(() -> fadeInOut(), getDisplayTime(), TimeUnit.SECONDS);
	}

	private void fadeInOut() {
		old = current;
		
		createCurrent();
		
		setFadeInImage();

		fadeOut();
		fadeIn();

		ParallelTransition pt = new ParallelTransition(fadeIn, fadeOut);

		pt.setOnFinished(e -> switchPictures());

		Platform.runLater(() -> pt.play());
	}

	private void createCurrent() {
		current = new ImageView();
		current.setOpacity(0);
		current.setSmooth(true);
		current.setPreserveRatio(true);
		current.setScaleX(1);
		current.setScaleY(1);
	}

	private void setFadeInImage() {
		int idx = rand.nextInt(pictureNames.size());

		try {
			currentImage = new Image(new FileInputStream(pictureNames.get(idx)));
			Platform.runLater(() -> scaleImage());
		} catch (FileNotFoundException e) {
			log.error("Unexpected exception for picture {}", pictureNames.get(idx), e);
		}
	}

	private void scaleImage() {
		current.setImage(currentImage);
		
		double pw = currentImage.getWidth();
		double ph = currentImage.getHeight();
		double vw = getWidth();
		double vh = getHeight();
		
		System.out.println("Window: " + vw + ":" + vh);
		System.out.println("Picture: " + pw + ":" + ph);
		
		double xr = 1 - (pw - vw) / pw;
		double yr = 1 - (ph - vh) / ph;
		
		System.out.println("xr: " + xr);
		System.out.println("yr: " + yr);

		if (xr >= yr) {
			current.setFitWidth(vw);
		} else {
			current.setFitHeight(vh);
		}

		double xDiff = current.getFitWidth() - vw;
		if (xDiff > 0) {
			double offset = 0 - xDiff / 2;
			current.setX(offset);
			AnchorPane.setRightAnchor(current, offset);
		}

		double yDiff = current.getFitHeight() - vh;
		if (yDiff > 0) {
			double offset = 0 - yDiff / 2;
			current.setY(offset);
			AnchorPane.setTopAnchor(current, offset);
		}
		
		stackPane.getChildren().add(current);
	}

	private void fadeIn() {
		fadeIn.setNode(current);
		fadeIn.setFromValue(current.getOpacity());
		fadeIn.setToValue(0.25);
		fadeIn.setDuration(Duration.seconds(getFadeTime()));
		fadeIn.setInterpolator(Interpolator.LINEAR);
	}

	private void fadeOut() {
		fadeOut.setNode(old);
		fadeOut.setFromValue(old.getOpacity());
		fadeOut.setToValue(0.0);
		fadeOut.setDuration(Duration.seconds(getFadeTime()));
		fadeOut.setInterpolator(Interpolator.LINEAR);
		fadeOut.setOnFinished(e -> stackPane.getChildren().remove(old));
	}

	public int getFadeTime() {
		return fadeTime;
	}

	public void setFadeTime(int fadeTime) {
		this.fadeTime = fadeTime;
	}

	public int getDisplayTime() {
		return displayTime;
	}

	public void setDisplayTime(int displayTime) {
		this.displayTime = displayTime;
	}

	public String getDirectoryName() {
		return directoryName;
	}

	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}

	public Pane getPane() {
		return stackPane;
	}

	public Image getCurrentImage() {
		return current.getImage();
	}
	
	public void setDimension(double width, double height) {
		setWidth(width);
		setHeight(height);
		stackPane.setMaxSize(width, height);
		stackPane.setPrefSize(width, height);
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}
}
