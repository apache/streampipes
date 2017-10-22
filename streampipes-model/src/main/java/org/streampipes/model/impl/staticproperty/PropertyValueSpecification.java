package org.streampipes.model.impl.staticproperty;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.UnnamedSEPAElement;

import javax.persistence.Entity;

@RdfsClass("so:PropertyValueSpecification")
@Entity
public class PropertyValueSpecification extends UnnamedSEPAElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@RdfProperty("so:minValue")
	private double minValue;
	
	@RdfProperty("so:maxValue")
	private double maxValue;
	
	@RdfProperty("so:step")
	private double step;

	public PropertyValueSpecification(double minValue, double maxValue,
			double step) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.step = step;
	}
	
	public PropertyValueSpecification(PropertyValueSpecification other)
	{
		super();
		this.minValue = other.getMinValue();
		this.maxValue = other.getMaxValue();
		this.step = other.getStep();
	}
	
	public PropertyValueSpecification()
	{
		super();
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
	
}
