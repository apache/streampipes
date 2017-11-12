package org.streampipes.model.impl.quality;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;

import javax.persistence.Entity;

@RdfsClass("ssn:MeasurementRange")
@Entity
public class MeasurementRange extends EventPropertyQualityDefinition {

	private static final long serialVersionUID = 4853190183770515968L;

	@RdfProperty("sepa:hasMeasurementPropertyMinValue")
	private float minValue;

	@RdfProperty("sepa:hasMeasurementPropertyMaxValue")
	private float maxValue;
	
	public MeasurementRange() {
		super();
	}

	public MeasurementRange(float minValue, float maxValue) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public MeasurementRange(MeasurementRange other) {
		super(other);
		this.minValue = other.getMinValue();
		this.maxValue = other.getMaxValue();
	}

	public float getMinValue() {
		return minValue;
	}

	public void setMinValue(float minValue) {
		this.minValue = minValue;
	}

	public float getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}

}
