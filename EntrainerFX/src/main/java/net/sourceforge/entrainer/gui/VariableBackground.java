package net.sourceforge.entrainer.gui;

import java.awt.Color;
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
import net.sourceforge.entrainer.sound.tools.FrequencyToHalfTimeCycle;
import net.sourceforge.entrainer.util.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VariableBackground {
	private static final Logger log = LoggerFactory.getLogger(VariableBackground.class);

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

	private ScheduledExecutorService switchSvc = Executors.newScheduledThreadPool(3);

	private List<String> pictureNames = new ArrayList<>();

	private Random rand = new Random(System.nanoTime());

	private double width;
	private double height;

	private boolean flashBackground;

	private boolean running;

	private FrequencyToHalfTimeCycle calculator = new FrequencyToHalfTimeCycle();

	private String backgroundPic;

	private ParallelTransition pt;

	private Rectangle rect;

	private javafx.scene.paint.Color backgroundColor;

	public VariableBackground() {
		initMediator();
	}

	private void init() {
		pictureNames.clear();
		loadFromDirectory();
		if(Platform.isFxApplicationThread()) {
			initContent();
		} else {
			Platform.runLater(() -> initContent());
		}
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
		switchSvc.schedule(() -> fadeInOut(), getDisplayTime(), TimeUnit.SECONDS);
	}

	private void fadeInOut() {
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

		Platform.runLater(() -> pt.play());
	}

	private void createCurrent() {
		current = new ImageView();
		current.setOpacity(0.01);
		current.setSmooth(true);
		current.setPreserveRatio(true);
		current.setScaleX(1);
		current.setScaleY(1);
	}

	private void setFadeInImage() {
		int idx = rand.nextInt(pictureNames.size());

		try {
			currentFile = pictureNames.get(idx);
			currentImage = new Image(new FileInputStream(currentFile));
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

		double xr = 1 - (pw - vw) / pw;
		double yr = 1 - (ph - vh) / ph;

		double yDiff = 0;
		double xDiff = 0;

		if (xr >= yr) {
			current.setFitWidth(vw);
			yDiff = (ph * vw / pw) - vh;
		} else {
			current.setFitHeight(vh);
			xDiff = (pw * vh / ph) - vw;
		}

		pane.getChildren().add(current);

		if (xDiff > 0) current.setX(0 - xDiff / 2);

		if (yDiff > 0) current.setY(0 - yDiff / 2);

		if (shouldRun()) startTransition();
	}

	private void fadeIn() {
		fadeIn = new FadeTransition(Duration.seconds(getFadeTime()), current);
		fadeIn.setFromValue(current.getOpacity());
		fadeIn.setToValue(0.25);
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
					if (shouldRun()) startTransition();
					break;
				case START_ENTRAINMENT:
					running = e.getBooleanValue();
					if (shouldRun()) startTransition();
					break;
				case STATIC_BACKGROUND:
					if(isStatic()) return;
					mode = BackgroundMode.STATIC;
					Platform.runLater(() -> evaluateStaticBackground(true));
					break;
				case DYNAMIC_BACKGROUND:
					if(isDynamic()) return;
					mode = BackgroundMode.DYNAMIC;
					Platform.runLater(() -> init());
					break;
				case NO_BACKGROUND:
					if(isNoBackground()) return;
					mode = BackgroundMode.NO_BACKGROUND;
					Platform.runLater(() -> clearBackground());
					break;
				case NO_BACKGROUND_COLOUR:
					Platform.runLater(() -> setBackgroundColor(e.getColourValue()));
					break;
				case BACKGROUND_PIC:
					backgroundPic = e.getStringValue();
					Platform.runLater(() -> evaluateStaticBackground(false));
					break;
				case BACKGROUND_PIC_DIR:
					directoryName = e.getStringValue();
					pictureNames.clear();
					loadFromDirectory();
					break;
				case BACKGROUND_DURATION_SECONDS:
					setDisplayTime((int) e.getDoubleValue());
					break;
				case BACKGROUND_TRANSITION_SECONDS:
					setFadeTime((int) e.getDoubleValue());
					break;
				default:
					break;
				}
			}

		});
	}

	private void setBackgroundColor(Color colourValue) {
		backgroundColor = JFXUtils.toJFXColor(colourValue);
		
		if(isShowBackgroundFill()) showBackgroundFill();
	}

	private void clearBackground() {
		if(isShowBackgroundFill()) showBackgroundFill();
	}
	
	private void showBackgroundFill() {
		clearPictures();
		
		rect = new Rectangle(pane.getWidth(), pane.getHeight(), backgroundColor);
		
		pane.getChildren().add(rect);
		
		if(shouldRun()) startTransition();
	}
	
	private boolean isShowBackgroundFill() {
		return isNoBackground() && backgroundColor != null;
	}

	private void evaluateStaticBackground(boolean useCurrent) {
		if(!isStatic()) return;
		
		if(useCurrent && currentFile != null) {
			backgroundPic = currentFile;
		}
		
		if(backgroundPic == null) return;
		
		try {
			currentImage = new Image(new FileInputStream(backgroundPic));
		} catch (FileNotFoundException e) {
			log.error("Unexpected exception", e);
			return;
		}
		
		clearPictures();

		createCurrent();
		scaleImage();
		current.setOpacity(0.25);

		sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, backgroundPic, MediatorConstants.BACKGROUND_PIC));
	}

	private void clearPictures() {
		//@formatter:off
		for (Node node : pane.getChildren()) node.setOpacity(0);
		//@formatter:on

		pane.getChildren().clear();
	}

	private void startTransition() {
		Runnable thread = new Runnable() {
			private Node background = isNoBackground() ? rect : current;

			public void run() {
				while (shouldRun() && background.getOpacity() > 0) {
					Utils.snooze(getMillis(), calculator.getNanos());

					invert(background);
				}

				reset(background);
			}

			private long getMillis() {
				long millis = calculator.getMillis();
				return millis > 5000 ? 5000l : millis;
			}
		};

		switchSvc.execute(thread);
	}

	private void invert(Node background) {
		if(background.getOpacity() == 0) return;
		
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				double o = 0;
				if(background instanceof ImageView) {
					o = background.getOpacity() == 0.25 ? 0.60 : 0.25;
				} else {
					o = background.getOpacity() == 1.0 ? 0.50 : 1.0;
				}
				background.setOpacity(o);
			}
		});
	}

	private void reset(Node background) {
		if(background.getOpacity() == 0) return;
		
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if(background instanceof ImageView) {
					background.setOpacity(0.25);
				} else {
					background.setOpacity(1);
				}
			}
		});
	}

	private boolean shouldRun() {
		return running && flashBackground;
	}
	
	public BackgroundMode getMode() {
		return mode;
	}
	
	public boolean isDynamic() {
		return isMode(BackgroundMode.DYNAMIC);
	}
	
	public boolean isStatic() {
		return isMode(BackgroundMode.STATIC);
	}
	
	public boolean isNoBackground() {
		return isMode(BackgroundMode.NO_BACKGROUND);
	}

	private boolean isMode(BackgroundMode mode) {
		return this.mode == mode;
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
		return pane;
	}

	public Image getCurrentImage() {
		return current.getImage();
	}

	public void setDimension(double width, double height) {
		setWidth(width);
		setHeight(height);
		
		if(rect != null) {
			rect.setWidth(width);
			rect.setHeight(height);
		}
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
