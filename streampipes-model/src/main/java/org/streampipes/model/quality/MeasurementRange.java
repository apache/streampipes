package org.streampipes.model.quality;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.SSN;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(SSN.MEASUREMENT_RANGE)
@Entity
public class MeasurementRange extends EventPropertyQualityDefinition {

	private static final long serialVersionUID = 4853190183770515968L;

	@RdfProperty(StreamPipes.HAS_MEASUREMENT_PROPERTY_MIN_VALUE)
	private float minValue;

	@RdfProperty(StreamPipes.HAS_MEASUREMENT_PROPERTY_MAX_VALUE)
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
