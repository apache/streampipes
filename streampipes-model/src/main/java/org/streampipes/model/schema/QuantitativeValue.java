package org.streampipes.model.schema;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.SO;

import javax.persistence.Entity;

@RdfsClass(SO.QuantitativeValue)
@Entity
public class QuantitativeValue extends ValueSpecification {

	private static final long serialVersionUID = 1L;

	@RdfProperty(SO.MinValue)
	private Float minValue;
	
	@RdfProperty(SO.MaxValue)
	private Float maxValue;

	@RdfProperty(SO.Step)
	private Float step;

	public QuantitativeValue() {
		super();
	}

	public QuantitativeValue(Float minValue, Float maxValue, Float step) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.step = step;
	}
	
	public QuantitativeValue(QuantitativeValue other) {
		super(other);
		this.minValue = other.getMinValue();
		this.maxValue = other.getMaxValue();
		this.step = other.getStep();
	}

	public Float getMinValue() {
		return minValue;
	}

	public void setMinValue(Float minValue) {
		this.minValue = minValue;
	}

	public Float getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Float maxValue) {
		this.maxValue = maxValue;
	}

	public Float getStep() {
		return step;
	}

	public void setStep(Float step) {
		this.step = step;
	}
		
}
