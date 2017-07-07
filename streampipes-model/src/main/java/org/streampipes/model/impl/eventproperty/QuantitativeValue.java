package org.streampipes.model.impl.eventproperty;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import javax.persistence.Entity;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/", "rdfs", "http://www.w3.org/2000/01/rdf-schema#", "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", "so", "http://schema.org/"})
@RdfsClass("so:QuantitativeValue")
@Entity
public class QuantitativeValue extends ValueSpecification {

	private static final long serialVersionUID = 1L;

	@RdfProperty("so:minValue")
	private Float minValue;
	
	@RdfProperty("so:maxValue")
	private Float maxValue;

	@RdfProperty("so:step")
	private Float step;
	
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
