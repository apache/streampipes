package org.streampipes.model.quality;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.SSN;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(SSN.RESOLUTION)
@Entity
public class Resolution extends EventPropertyQualityDefinition {

	private static final long serialVersionUID = -8794648771727880619L;
	
	@RdfProperty(StreamPipes.HAS_QUANTITY_VALUE)
	private float quantityValue;

	public Resolution() {
		super();
	}
	
	public Resolution(float quantityValue) {
		this.quantityValue = quantityValue;
	}
	
	public Resolution(Resolution other) {
		super(other);
		this.quantityValue = other.getQuantityValue();
	}
	
	public float getQuantityValue() {
		return quantityValue;
	}

	public void setQuantityValue(float quantityValue) {
		this.quantityValue = quantityValue;
	}

}
