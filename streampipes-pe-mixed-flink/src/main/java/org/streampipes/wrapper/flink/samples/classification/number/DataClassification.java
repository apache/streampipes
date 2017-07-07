package org.streampipes.wrapper.flink.samples.classification.number;

import java.io.Serializable;

public class DataClassification implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double minValue;
	private double maxValue;
	private String label;

	public DataClassification(double minValue, double maxValue, String label) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.label = label;
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}