package de.fzi.cep.sepa.model.impl.eventproperty;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/", "rdfs", "http://www.w3.org/2000/01/rdf-schema#", "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", "so", "http://schema.org/"})
@RdfsClass("so:QuantitativeValue")
@Entity
public class QuantitativeValue extends ValueSpecification {

	private static final long serialVersionUID = 1L;

	@RdfProperty("so:minValue")
	private int minValue;
	
	@RdfProperty("so:maxValue")
	private int maxValue;
	
	@RdfProperty("so:unitCode")
	private String unitCode;
	
	@RdfProperty("so:step")
	private double step;
	
	public QuantitativeValue(int minValue, int maxValue, String unitCode, double step) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.unitCode = unitCode;
		this.step = step;
	}
	
	public QuantitativeValue(QuantitativeValue other) {
		super(other);
		this.minValue = other.getMinValue();
		this.maxValue = other.getMaxValue();
		this.unitCode = other.getUnitCode();
		this.step = other.getStep();
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

	public double getStep() {
		return step;
	}

	public void setStep(double step) {
		this.step = step;
	}
		
}
