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
package net.sourceforge.entrainer.jfreechart;

import static net.sourceforge.entrainer.mediator.MediatorConstants.AMPLITUDE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.ENTRAINMENT_FREQUENCY;
import static net.sourceforge.entrainer.mediator.MediatorConstants.FREQUENCY;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_ENTRAINER_MULTIPLE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_NOISE_AMPLITUDE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.PINK_PAN_AMPLITUDE;
import static net.sourceforge.entrainer.util.Utils.openBrowser;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import net.sourceforge.entrainer.gui.popup.InfoTimer;
import net.sourceforge.entrainer.gui.popup.UnitValuePopup;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.xml.program.EntrainerProgramInterval;
import net.sourceforge.entrainer.xml.program.EntrainerProgramUnit;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

// TODO: Auto-generated Javadoc
/**
 * This class displays the chart for all the units over time.
 * 
 * @author burton
 */
public class UnitChart extends JDialog {

	private List<EntrainerProgramUnit> units;
	private JFreeChart chart;
	private ChartPanel chartPanel;
	private DefaultXYDataset dataset;
	private XYItemEntity currentEntity;
	private static final long serialVersionUID = 1L;
	private UnitValueTimer timer;
	private UnitChart instance;
	private List<EntrainerProgramInterval> intervals = new ArrayList<EntrainerProgramInterval>();

	/**
	 * Instantiates a new unit chart.
	 *
	 * @param owner
	 *          the owner
	 * @param units
	 *          the units
	 * @param name
	 *          the name
	 * @param intervals
	 *          the intervals
	 */
	public UnitChart(Frame owner, List<EntrainerProgramUnit> units, String name, List<EntrainerProgramInterval> intervals) {
		super(owner, "Unit Chart for " + name, true);
		setUnits(units);
		this.intervals = intervals;
		init();
		instance = this;
	}

	/**
	 * Instantiates a new unit chart.
	 *
	 * @param owner
	 *          the owner
	 * @param units
	 *          the units
	 * @param name
	 *          the name
	 * @param intervals
	 *          the intervals
	 */
	public UnitChart(Dialog owner, List<EntrainerProgramUnit> units, String name, List<EntrainerProgramInterval> intervals) {
		super(owner, "Unit Chart for " + name, true);
		setUnits(units);
		this.intervals = intervals;
		init();
		instance = this;
	}

	private void init() {
		chart = new JFreeChart(getXYPlot());
		chartPanel = new ChartPanel(chart);

		chartPanel.addChartMouseListener(new ChartMouseListener() {
			public void chartMouseClicked(ChartMouseEvent event) {
			}

			public void chartMouseMoved(ChartMouseEvent event) {
				if (event.getEntity() != null && (currentEntity == null || !currentEntity.equals(event.getEntity()))) {
					if (event.getEntity() instanceof XYItemEntity) {
						showPopup((XYItemEntity) event.getEntity());
					}
				} else if (event.getEntity() == null && timer != null) {
					timer.dismiss();
					timer = null;
					currentEntity = null;
				}
			}
		});

		getContentPane().add(chartPanel);

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

	private void showPopup(XYItemEntity entity) {
		currentEntity = entity;
		MediatorConstants name = (MediatorConstants) dataset.getSeriesKey(entity.getSeriesIndex());
		double value = dataset.getYValue(entity.getSeriesIndex(), entity.getItem()) * getMultiple(name);
		double time = dataset.getXValue(entity.getSeriesIndex(), entity.getItem());

		if (timer != null) {
			timer.dismiss();
		}

		timer = new UnitValueTimer(name, value, time);

		timer.start();
	}

	private XYPlot getXYPlot() {
		XYPlot plot = new XYPlot(getDataSet(), getDomainAxis(), getRangeAxis(), getRenderer());

		return plot;
	}

	private int getMultiple(MediatorConstants parameter) {
		if (FREQUENCY.equals(parameter)) {
			return 500;
		} else if (PINK_ENTRAINER_MULTIPLE.equals(parameter)) {
			return 512;
		} else if (ENTRAINMENT_FREQUENCY.equals(parameter)) {
			return 40;
		}

		return 1;
	}

	private String getUnits(MediatorConstants parameter) {
		if (FREQUENCY.equals(parameter)) {
			return "Hz";
		} else if (PINK_ENTRAINER_MULTIPLE.equals(parameter)) {
			return "X";
		} else if (ENTRAINMENT_FREQUENCY.equals(parameter)) {
			return "Hz";
		}

		return "";
	}

	private XYDataset getDataSet() {
		dataset = new DefaultXYDataset();

		dataset.addSeries(ENTRAINMENT_FREQUENCY, getEntrainmentFrequencyData());
		dataset.addSeries(FREQUENCY, getFrequencyData());
		dataset.addSeries(AMPLITUDE, getAmplitudeData());
		dataset.addSeries(PINK_NOISE_AMPLITUDE, getPinkNoiseData());
		dataset.addSeries(PINK_PAN_AMPLITUDE, getPinkPanData());
		dataset.addSeries(PINK_ENTRAINER_MULTIPLE, getPinkEntrainerMultipleData());

		return dataset;
	}

	private ValueAxis getDomainAxis() {
		return new NumberAxis("Time (seconds)");
	}

	private ValueAxis getRangeAxis() {
		NumberAxis axis = new NumberAxis("Range");

		axis.setAutoTickUnitSelection(false);

		return axis;
	}

	private XYItemRenderer getRenderer() {
		return new XYLineAndShapeRenderer(true, true);
	}

	private double[][] getAmplitudeData() {
		long time = 0;
		long endTime = 0;
		double[][] values = new double[2][units.size() * 2];

		int i = 0;
		for (EntrainerProgramUnit unit : units) {
			endTime = unit.getTimeInMillis() / 1000 + time;

			values[0][i] = time;
			values[0][i + 1] = endTime;
			values[1][i] = unit.getStartAmplitude();
			values[1][i + 1] = unit.getEndAmplitude();

			time = endTime;
			i += 2;
		}

		return values;
	}

	private double[][] getFrequencyData() {
		long time = 0;
		long endTime = 0;
		int multiple = getMultiple(FREQUENCY);
		double[][] values = new double[2][units.size() * 2];

		int i = 0;
		for (EntrainerProgramUnit unit : units) {
			endTime = unit.getTimeInMillis() / 1000 + time;

			values[0][i] = time;
			values[0][i + 1] = endTime;
			values[1][i] = unit.getStartFrequency() / multiple;
			values[1][i + 1] = unit.getEndFrequency() / multiple;

			time = endTime;
			i += 2;
		}

		return values;
	}

	private double[][] getEntrainmentFrequencyData() {
		long time = 0;
		long endTime = 0;
		int multiple = getMultiple(ENTRAINMENT_FREQUENCY);
		double[][] values = new double[2][units.size() * 2];

		int i = 0;
		for (EntrainerProgramUnit unit : units) {
			endTime = unit.getTimeInMillis() / 1000 + time;

			values[0][i] = time;
			values[0][i + 1] = endTime;
			values[1][i] = unit.getStartEntrainmentFrequency() / multiple;
			values[1][i + 1] = unit.getEndEntrainmentFrequency() / multiple;

			time = endTime;
			i += 2;
		}

		return values;
	}

	private double[][] getPinkNoiseData() {
		long time = 0;
		long endTime = 0;
		double[][] values = new double[2][units.size() * 2];

		int i = 0;
		for (EntrainerProgramUnit unit : units) {
			endTime = unit.getTimeInMillis() / 1000 + time;

			values[0][i] = time;
			values[0][i + 1] = endTime;
			values[1][i] = unit.getStartPinkNoise();
			values[1][i + 1] = unit.getEndPinkNoise();

			time = endTime;
			i += 2;
		}

		return values;
	}

	private double[][] getPinkPanData() {
		long time = 0;
		long endTime = 0;
		double[][] values = new double[2][units.size() * 2];

		int i = 0;
		for (EntrainerProgramUnit unit : units) {
			endTime = unit.getTimeInMillis() / 1000 + time;

			values[0][i] = time;
			values[0][i + 1] = endTime;
			values[1][i] = unit.getStartPinkPan();
			values[1][i + 1] = unit.getEndPinkPan();

			time = endTime;
			i += 2;
		}

		return values;
	}

	private double[][] getPinkEntrainerMultipleData() {
		long time = 0;
		long endTime = 0;
		int multiple = getMultiple(PINK_ENTRAINER_MULTIPLE);
		double[][] values = new double[2][units.size() * 2];

		int i = 0;
		for (EntrainerProgramUnit unit : units) {
			endTime = unit.getTimeInMillis() / 1000 + time;

			values[0][i] = time;
			values[0][i + 1] = endTime;
			values[1][i] = unit.getStartPinkEntrainerMultiple() / multiple;
			values[1][i + 1] = unit.getEndPinkEntrainerMultiple() / multiple;

			time = endTime;
			i += 2;
		}

		return values;
	}

	/**
	 * Gets the unit.
	 *
	 * @return the unit
	 */
	public List<EntrainerProgramUnit> getUnit() {
		return units;
	}

	/**
	 * Sets the units.
	 *
	 * @param units
	 *          the new units
	 */
	public void setUnits(List<EntrainerProgramUnit> units) {
		this.units = units;
	}

	private boolean isFrequency(MediatorConstants mc) {
		return FREQUENCY.equals(mc);
	}

	/**
	 * The Class UnitValueTimer.
	 */
	class UnitValueTimer extends InfoTimer {
		private MediatorConstants parameterName;
		private double value;
		private double time;

		/**
		 * Instantiates a new unit value timer.
		 *
		 * @param parameterName
		 *          the parameter name
		 * @param value
		 *          the value
		 * @param time
		 *          the time
		 */
		public UnitValueTimer(MediatorConstants parameterName, double value, double time) {
			super();
			this.parameterName = parameterName;
			this.value = value;
			this.time = time;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see net.sourceforge.entrainer.gui.popup.InfoTimer#createNewInfo()
		 */
		@Override
		protected void createNewInfo() {
			if (isFrequency(parameterName)) {
				setInfo(new UnitValuePopup(instance, parameterName, value, time, getUnits(parameterName), intervals));
			} else {
				setInfo(new UnitValuePopup(instance, parameterName, value, time, getUnits(parameterName)));
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see net.sourceforge.entrainer.gui.popup.InfoTimer#shouldShowInfo()
		 */
		@Override
		protected boolean shouldShowInfo() {
			return !isShouldStop();
		}
	}

}
