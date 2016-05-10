package de.fzi.cep.sepa.actions.samples.gauge;

import de.fzi.cep.sepa.actions.samples.ActionParameters;

public class GaugeParameters extends ActionParameters {

	private String variableName;
	private int min;
	private int max;
	
	public GaugeParameters(String topic, String url, String variableName, int min, int max) {
		super(topic, url);
		this.variableName = variableName;
		this.min = min;
		this.max = max;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}
	
}
