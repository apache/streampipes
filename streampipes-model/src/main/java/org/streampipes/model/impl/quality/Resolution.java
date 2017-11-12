package org.streampipes.model.impl.quality;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;

import javax.persistence.Entity;

@RdfsClass("ssn:Resolution")
@Entity
public class Resolution extends EventPropertyQualityDefinition {

	private static final long serialVersionUID = -8794648771727880619L;
	
	@RdfProperty("sepa:hasQuantityValue")
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
