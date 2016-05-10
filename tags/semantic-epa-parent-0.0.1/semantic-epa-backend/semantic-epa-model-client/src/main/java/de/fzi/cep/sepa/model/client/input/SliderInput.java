package de.fzi.cep.sepa.model.client.input;

public class SliderInput extends FormInput {

	private double minValue;
	private double maxValue;
	private double step;
	
	private double value;
	
	public SliderInput() {
		super(ElementType.SLIDER);
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

	public double getStep() {
		return step;
	}

	public void setStep(double step) {
		this.step = step;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
}
