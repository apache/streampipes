package org.streampipes.model.impl.quality;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;

import javax.persistence.Entity;

@RdfsClass("ssn:Precision")
@Entity
public class Precision extends EventPropertyQualityDefinition {

	private static final long serialVersionUID = -1090184880089982077L;
	
	@RdfProperty("sepa:hasQuantityValue")
	private float quantityValue;

	public Precision() {
		super();
	}
	
	public Precision(float quantityValue) {
		this.quantityValue = quantityValue;
	}
	
	public Precision(Precision other) {
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
