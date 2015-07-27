package de.fzi.cep.sepa.model.impl.quality;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "ssn",   "http://purl.oclc.org/NET/ssnx/ssn#"})
@RdfsClass("ssn:MeasurementRange")
@Entity
public class MeasurementRange extends EventPropertyQualityDefinition {
	@RdfProperty("sepa:hasMeasurementPropertyMinValue")
	float minValue;

	@RdfProperty("sepa:hasMeasurementPropertyMaxValue")
	float maxValue;
	
	public MeasurementRange() {
		super();
	}

	public MeasurementRange(float minValue, float maxValue) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
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
	

	@Override
	public int compareTo(EventPropertyQualityDefinition o) {
		MeasurementRange other = (MeasurementRange) o;
		
		//TODO not sure if this is correct
		if (this.minValue <= other.minValue && this.maxValue >= other.maxValue) {
			return 0;
		} else {
			return 1;
		}

	}


}
