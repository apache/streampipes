package de.fzi.cep.sepa.model.client.ontology;

public class QuantitativeValueRange extends Range {

	private static final String TITLE = "";
	private static final String DESCRIPTION = "";
	
	private int minValue;
	private int maxValue;
	
	private String unitCode;
	
	public QuantitativeValueRange(int minValue, int maxValue, String unitCode) {
		super(RangeType.QUANTITATIVE_VALUE, TITLE, DESCRIPTION);
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.unitCode = unitCode;
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

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}
	
	
}
