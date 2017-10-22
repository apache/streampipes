package org.streampipes.model.impl.quality;

import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import javax.persistence.Entity;

@RdfsClass("ssn:Frequency")
@Entity
public class Frequency extends EventStreamQualityDefinition {

	private static final long serialVersionUID = 8196363710990038633L;

	public Frequency() {
		super();
	}

	public Frequency(float quantityValue) {
		super();
		this.quantityValue = quantityValue;
	}
	
	public Frequency(Frequency other) {
		super(other);
		this.quantityValue = other.getQuantityValue();
	}
	
	/**
	 * The unit of qualityValue is Hertz [Hz]
	 */
	@RdfProperty("sepa:hasQuantityValue")
	float quantityValue;

	public float getQuantityValue() {
		return quantityValue;
	}

	public void setQuantityValue(float quantityValue) {
		this.quantityValue = quantityValue;
	}
	
	
}
