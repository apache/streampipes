package de.fzi.cep.sepa.esper.collection;

public class DataRange {

	private int min;
	private int max;
	private int step;
	
	public DataRange(int min, int max, int step) {
		super();
		this.min = min;
		this.max = max;
		this.step = step;
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
	public int getStep() {
		return step;
	}
	public void setStep(int step) {
		this.step = step;
	}
	
	
}
