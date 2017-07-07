package de.fzi.cep.sepa.esper.distribution;

public class DistributionRow {

	private String key;
	private int value;
	
	public DistributionRow(String key, int value) {
		super();
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
	
}
