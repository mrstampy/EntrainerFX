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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Ellipse;
import javafx.util.Duration;
import net.sourceforge.entrainer.neuroph.NeurophUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class NeuronFx.
 */
public class NeuronFx extends VBox {
	private double radius = 16;

	private Ellipse indicator = new Ellipse(radius, radius);
	private Ellipse invisible = new Ellipse();

	private FadeTransition fader = new FadeTransition(Duration.millis(1000));
	private FillTransition filler;

	private double value;
	private double maxValue = Double.MIN_VALUE;
	private double minValue = Double.MAX_VALUE;

	private String tooltip;

	private List<Double> wma = new ArrayList<Double>();

	private int wmaSize = 10;

	private Color defaultColor = Color.ROYALBLUE;
	private Color reflectionColor = Color.CORNSILK;

	private volatile boolean selected;

	/**
	 * Instantiates a new neuron fx.
	 */
	public NeuronFx() {
		super();

		init();
	}

	/**
	 * Sets the neuron opacity.
	 *
	 * @param o
	 *          the new neuron opacity
	 */
	public void setNeuronOpacity(double o) {
		fader.stop();

		fader.setFromValue(indicator.getOpacity());
		fader.setToValue(o);

		fader.playFromStart();
	}

	/**
	 * Sets the neuron fill.
	 *
	 * @param c
	 *          the new neuron fill
	 */
	public void setNeuronFill(Color c) {
		filler.stop();

		filler.setFromValue((Color) invisible.getFill());
		filler.setToValue(c);

		filler.playFromStart();
	}

	/**
	 * Gets the neuron fill.
	 *
	 * @return the neuron fill
	 */
	public Color getNeuronFill() {
		return (Color) invisible.getFill();
	}

	private void init() {
		invisible.setFill(getDefaultColor());
		indicator.setFill(getGradient(getDefaultColor()));

		getChildren().add(indicator);

		filler = new FillTransition(Duration.millis(1000), invisible);
		fader.setNode(indicator);

		invisible.fillProperty().addListener(new ChangeListener<Paint>() {

			@Override
			public void changed(ObservableValue<? extends Paint> observable, Paint oldValue, Paint newValue) {
				indicator.setFill(getGradient((Color) newValue));
			}
		});

		addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
					setSelected(!isSelected());
				}
			}
		});
	}

	private Paint getGradient(Color c) {
		return new RadialGradient(0d, 0d, -2, -4, radius, false, CycleMethod.REFLECT, getStops(c));
	}

	private Stop[] getStops(Color c) {
		return new Stop[] { new Stop(0.05, getReflectionColor()), new Stop(0.95, c) };
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value
	 *          the new value
	 */
	public void setValue(double value) {
		this.value = value;

		setMinAndMaxValues();
	}

	private void setMinAndMaxValues() {
		maxValue = Double.MIN_VALUE;
		minValue = Double.MAX_VALUE;

		for (Double d : wma) {
			maxValue = Math.max(maxValue, d.doubleValue());
			minValue = Math.min(minValue, d.doubleValue());
		}
	}

	/**
	 * Calculate neuron opacity.
	 */
	public void calculateNeuronOpacity() {
		BigDecimal wma = getWeightedMovingAverage();

		if (wma.doubleValue() > 0 && wma.doubleValue() < 1) {
			setNeuronOpacity(wma.doubleValue());
			return;
		}

		BigDecimal numerator = wma.subtract(new BigDecimal(getMinValue()));

		BigDecimal denominator = new BigDecimal(getMaxValue() - getMinValue());

		if (denominator.equals(BigDecimal.ZERO)) {
			setNeuronOpacity(1.0);
		} else {
			setNeuronOpacity(numerator.divide(denominator, 3, RoundingMode.HALF_UP).doubleValue());
		}
	}

	private BigDecimal getWeightedMovingAverage() {
		double d = NeurophUtil.weightedMovingAverage(wma, getValue(), getWMASize());

		return new BigDecimal(d);
	}

	/**
	 * Gets the default color.
	 *
	 * @return the default color
	 */
	public Color getDefaultColor() {
		return defaultColor;
	}

	/**
	 * Sets the default color.
	 *
	 * @param defaultColor
	 *          the new default color
	 */
	public void setDefaultColor(Color defaultColor) {
		this.defaultColor = defaultColor;
	}

	/**
	 * Gets the entered color.
	 *
	 * @return the entered color
	 */
	public Color getEnteredColor() {
		return getDefaultColor().invert();
	}

	/**
	 * Gets the reflection color.
	 *
	 * @return the reflection color
	 */
	public Color getReflectionColor() {
		return reflectionColor;
	}

	/**
	 * Sets the reflection color.
	 *
	 * @param reflectionColor
	 *          the new reflection color
	 */
	public void setReflectionColor(Color reflectionColor) {
		this.reflectionColor = reflectionColor;
	}

	/**
	 * Gets the WMA size.
	 *
	 * @return the WMA size
	 */
	public int getWMASize() {
		return wmaSize;
	}

	/**
	 * Sets the WMA size.
	 *
	 * @param wmaSize
	 *          the new WMA size
	 */
	public void setWMASize(int wmaSize) {
		assert wmaSize > 1;
		this.wmaSize = wmaSize;
	}

	/**
	 * Gets the indicator.
	 *
	 * @return the indicator
	 */
	public Ellipse getIndicator() {
		return indicator;
	}

	/**
	 * Gets the tooltip.
	 *
	 * @return the tooltip
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * Sets the tooltip.
	 *
	 * @param tooltip
	 *          the new tooltip
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	/**
	 * Checks if is selected.
	 *
	 * @return true, if is selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Sets the selected.
	 *
	 * @param selected
	 *          the new selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		setNeuronFill(selected ? getEnteredColor() : getDefaultColor());
	}

	/**
	 * Gets the wma.
	 *
	 * @return the wma
	 */
	public List<Double> getWma() {
		return wma;
	}

	/**
	 * Gets the max value.
	 *
	 * @return the max value
	 */
	public double getMaxValue() {
		return maxValue;
	}

	/**
	 * Gets the min value.
	 *
	 * @return the min value
	 */
	public double getMinValue() {
		return minValue;
	}

	/**
	 * Reset max min.
	 */
	public void resetMaxMin() {
		maxValue = Double.MIN_VALUE;
		minValue = Double.MAX_VALUE;
	}

}
