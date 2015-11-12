package de.fzi.cep.sepa.esper.classification.number;

public class DataClassification {
	private int minValue;
	private int maxValue;
	private String label;
	
	public DataClassification(int minValue, int maxValue, String label) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.label = label;
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	
	

}
