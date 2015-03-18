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
package net.sourceforge.entrainer.neuroph.jfx;

import static net.sourceforge.entrainer.gui.jfx.AbstractTitledPane.COLLAPSED_OPACITY;
import static net.sourceforge.entrainer.gui.jfx.AbstractTitledPane.EXPANDED_OPACITY;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javafx.animation.FadeTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import net.sourceforge.entrainer.gui.jfx.JFXUtils;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.neuroph.Neuralizer;
import net.sourceforge.entrainer.neuroph.SignalDataAggregator;
import net.sourceforge.entrainer.util.Utils;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Subscription;
import rx.schedulers.Schedulers;

import com.github.mrstampy.esp.dsp.lab.Lab;

// TODO: Auto-generated Javadoc
/**
 * The Class NeuralizerPane.
 */
public class NeuralizerPane extends TitledPane {
	private static final Logger log = LoggerFactory.getLogger(NeuralizerPane.class);

	private static final String NETWORK_DIR = "networks";
	private static final String SELECTIONS_EXT = ".selections";

	private NeuronGrid grid;
	private Neuralizer neuralizer;

	private Lab lab;

	private VBox layout = new VBox();

	private static final int NUM_NEURONS = 144;

	private Random rand = new Random(System.nanoTime());

	private volatile boolean connected = false;

	private SignalDataAggregator aggregator = new SignalDataAggregator();

	private Subscription subscription;

	/**
	 * Instantiates a new neuralizer pane.
	 */
	public NeuralizerPane() {
		super();
		init();
	}

	private void init() {
		setStyle("-fx-background-color: black");
		initMediator();
		initNeuralizer();
		initGrid();
		expandedProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable arg0) {
				setOpacity(isExpanded() ? EXPANDED_OPACITY : COLLAPSED_OPACITY);
			}
		});

		setText("Neuralizer");

		//@formatter:off
		//
		// Primary (left) mouse button (available when connected to the ESP device):
		//		- single click + shift - randomize network weights
		//		- double click - record start/stop
		//
		// Secondary (right) mouse button (single click):
		//		- shift down ? show network load : show network save
		//
		//@formatter:on
		addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			private boolean recording = false;

			@Override
			public void handle(MouseEvent e) {
				if (e.getButton() == MouseButton.SECONDARY && e.getClickCount() == 1) {
					if (e.isShiftDown()) {
						if (!connected) showLoad();
					} else {
						showSave();
					}
					e.consume();
				}

				if (!connected || e.isConsumed()) return;

				if (e.getButton() == MouseButton.PRIMARY) {
					switch (e.getClickCount()) {
					case 1:
						if (e.isShiftDown()) randomizeWeights();
						break;
					case 2:
						recordControl();
						break;
					}
					e.consume();
				}
			}

			private void recordControl() {
				if (recording) {
					stopRecording();
					recording = false;
					log.debug("Recording stopped");
				} else {
					startRecording();
					recording = true;
					log.debug("Recording");
				}
			}
		});

		//@formatter:off
		//
		// handler to change the opacity of the expanded NeuralizerPane
		//
		// click-drag, then click to release
		//
		// if abs(x distance) > 100
		//
		//   if distance is positive 
		//   then opacity = 1
		//   else opacity = 0.25
		//
		//@formatter:on
		addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {

			private boolean dragging = false;
			private double startx = -1;

			@Override
			public void handle(MouseEvent e) {
				switch (e.getEventType().toString()) {
				case "MOUSE_DRAGGED":
					checkInitDrag(e);
					break;
				case "MOUSE_RELEASED":
					checkEndDrag(e);
					break;
				default:
					break;
				}
			}

			private void checkEndDrag(MouseEvent e) {
				if (!dragging) return;

				dragging = false;
				double dist = e.getX() - startx;
				startx = -1;

				if (Math.abs(dist) < 100) return;
				if (getOpacity() == 1 && dist > 0) return;
				if (getOpacity() == 0.25 && dist < 0) return;

				FadeTransition ft = new FadeTransition(Duration.millis(1000));
				ft.setNode(NeuralizerPane.this);
				ft.setToValue(dist > 0 ? 1 : 0.25);
				ft.play();
			}

			private void checkInitDrag(MouseEvent e) {
				if (dragging) return;

				dragging = true;
				startx = e.getX();
			}

		});

		initVbox();
		layout.setAlignment(Pos.CENTER);
		setContent(layout);

		setNeuronColor(Color.FIREBRICK);

		setOnMouseEntered(e -> determineOpacity());

		setOnMouseExited(e -> mouseExited());

		setOpacity(0);
	}

	private void mouseExited() {
		if (isExpanded()) return;

		FadeTransition ft = new FadeTransition(Duration.millis(250), this);

		ft.setFromValue(getOpacity());
		ft.setToValue(0);

		ft.play();
	}

	private void determineOpacity() {
		if (isExpanded()) return;

		FadeTransition ft = new FadeTransition(Duration.millis(250), this);

		ft.setFromValue(getOpacity());
		ft.setToValue(COLLAPSED_OPACITY);

		ft.play();
	}

	private void showLoad() {
		FileChooser chooser = getFileChooser();
		chooser.setTitle("Load Network");
		File f = chooser.showOpenDialog(null);

		if (f != null) {
			Color dflt = getDefaultColor();
			setNeuronColor(Color.DARKGOLDENROD);
			loadNetwork(f);
			resetNeuronColor(dflt);
		}
	}

	private Color getDefaultColor() {
		Color dflt = grid.getFlattened()[0].getDefaultColor();
		return dflt;
	}

	private void loadNetwork(File f) {
		try {
			NeuralNetwork<?> network = NeuralNetwork.load(new FileInputStream(f));
			neuralizer.setNetwork(network);
			loadSelections(f);
		} catch (Exception e) {
			log.error("Could not load network {}", f.getAbsolutePath());
		}
	}

	private void showSave() {
		FileChooser chooser = getFileChooser();
		chooser.setTitle("Save Network");

		File f = chooser.showSaveDialog(null);

		if (f != null) {
			neuralizer.getNetworkContainer().save(f.getAbsolutePath());
			saveSelections(f);
		}
	}

	private void loadSelections(File f) {
		String name = f.getName() + SELECTIONS_EXT;
		File sel = new File(NETWORK_DIR, name);

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(sel));
			String selections = reader.readLine();
			setSelections(selections);
		} catch (Exception e) {
			log.error("Could not load selections {}", sel.getAbsolutePath(), e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					log.error("Could not close writer for {}", sel.getAbsolutePath(), e);
				}
			}
		}
	}

	private void setSelections(String selections) {
		String[] s = selections.split(",");

		for (int i = 0; i < grid.getFlattened().length; i++) {
			boolean selected = Boolean.parseBoolean(s[i]);
			grid.getFlattened()[i].setSelected(selected);
		}
	}

	private void saveSelections(File f) {
		String name = f.getName() + SELECTIONS_EXT;

		String selections = getSelections();

		File sel = new File(NETWORK_DIR, name);

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(sel));
			writer.write(selections);
			writer.flush();
		} catch (Exception e) {
			log.error("Cannot write selections for {}", sel.getAbsolutePath(), e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					log.error("Could not close writer for {}", sel.getAbsolutePath(), e);
				}
			}
		}
	}

	private String getSelections() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (NeuronFx nfx : grid.getFlattened()) {
			if (!first) sb.append(",");
			sb.append(nfx.isSelected());
			first = false;
		}

		return sb.toString();
	}

	private FileChooser getFileChooser() {
		FileChooser chooser = new FileChooser();

		File netdir = new File(NETWORK_DIR);
		if (!netdir.exists()) netdir.mkdir();

		chooser.setInitialDirectory(netdir);
		return chooser;
	}

	private void randomizeWeights() {
		Color dflt = getDefaultColor();
		setNeuronColor(Color.DARKGOLDENROD);
		log.debug("Randomizing");
		neuralizer.randomizeSignalWeights();
		for (NeuronFx nfx : grid.getFlattened()) {
			nfx.resetMaxMin();
		}
		resetNeuronColor(dflt);
	}

	private void resetNeuronColor(final Color dflt) {
		Thread thread = new Thread() {
			public void run() {
				Utils.snooze(1000);
				JFXUtils.runLater(new Runnable() {

					@Override
					public void run() {
						setNeuronColor(dflt);
					}
				});
			}
		};

		thread.start();
	}

	private void initVbox() {
		layout.setStyle("-fx-background-color: black");
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(5));
		layout.getChildren().addAll(grid.getGridPane());
	}

	/**
	 * Start training.
	 */
	protected void startTraining() {
		double[][] inputs = aggregator.getSamples();
		if (inputs == null) return;

		double[] output = calculateOutput();

		NeuralNetwork<?> network = neuralizer.getNetwork();

		DataSet ds = neuralizer.getNetworkContainer().createDataSet(inputs, output);

		network.learnInNewThread(ds);
	}

	private double[] calculateOutput() {
		double[] expected = new double[NUM_NEURONS];

		for (int i = 0; i < expected.length; i++) {
			NeuronFx nfx = grid.getFlattened()[i];
			expected[i] = nfx.isSelected() ? 1.0 : getUnselected();
		}

		return expected;
	}

	private double getUnselected() {
		double d = rand.nextDouble();
		while (d > 0.9) {
			d = rand.nextDouble();
		}

		return d;
	}

	/**
	 * Stop recording.
	 */
	protected void stopRecording() {
		getLab().removeSignalProcessedListener(aggregator);
		startTraining();

		setNeuronColor(Color.ROYALBLUE);
	}

	/**
	 * Start recording.
	 */
	protected void startRecording() {
		aggregator.clear();

		getLab().addSignalProcessedListener(aggregator);

		setNeuronColor(Color.FORESTGREEN);
	}

	private void setNeuronColor(Color c) {
		for (NeuronFx nfx : grid.getFlattened()) {
			nfx.setNeuronFill(nfx.isSelected() ? c.invert() : c);
			nfx.setDefaultColor(c);
		}
	}

	private void initGrid() {
		grid = new NeuronGrid(12, neuralizer);
		for (int i = 0; i < grid.getFlattened().length; i++) {
			NeuronFx neuronFx = grid.getFlattened()[i];
			initConstraints(neuronFx, i);
		}
	}

	private void initConstraints(NeuronFx neuronFx, int i) {
		int marge = 3;
		GridPane.setMargin(neuronFx, new Insets(marge));
	}

	private void initNeuralizer() {
		neuralizer = new Neuralizer(NUM_NEURONS);
	}

	private void initMediator() {
		EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

			@Override
			protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
				switch (e.getParm()) {
				case START_ENTRAINMENT:
					if (!e.getBooleanValue()) setNeuronsOpaque();
					break;
				case ESP_START:
					if (e.getBooleanValue()) {
						start();
					} else {
						stop();
					}
				default:
					break;
				}
			}

		});
	}

	private void setNeuronsOpaque() {
		for (NeuronFx nfx : grid.getFlattened()) {
			nfx.setNeuronOpacity(1.0);
		}
	}

	/**
	 * Gets the neuralizer.
	 *
	 * @return the neuralizer
	 */
	public Neuralizer getNeuralizer() {
		return neuralizer;
	}

	/**
	 * Gets the lab.
	 *
	 * @return the lab
	 */
	public Lab getLab() {
		return lab;
	}

	/**
	 * Sets the lab.
	 *
	 * @param lab
	 *          the new lab
	 */
	public void setLab(Lab lab) {
		this.lab = lab;

		lab.addSignalProcessedListener(getNeuralizer());
	}

	private void start() {
		setNeuronColor(Color.ROYALBLUE);
		connected = true;

		subscription = Schedulers.computation().schedulePeriodically(t1 -> sample(), 0, 1, TimeUnit.SECONDS);
	}

	private void sample() {
		getLab().triggerProcessing();
	}

	private void stop() {
		for (NeuronFx nfx : grid.getFlattened()) {
			nfx.setNeuronOpacity(1.0);
		}

		if (subscription != null) subscription.unsubscribe();

		setNeuronColor(Color.FIREBRICK);
		connected = false;
	}

}
