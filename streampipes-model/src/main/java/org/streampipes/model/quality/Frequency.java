package org.streampipes.model.quality;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.SSN;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(SSN.FREQUENCY)
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
	@RdfProperty(StreamPipes.HAS_QUANTITY_VALUE)
	float quantityValue;

	public float getQuantityValue() {
		return quantityValue;
	}

	public void setQuantityValue(float quantityValue) {
		this.quantityValue = quantityValue;
	}
	
	
}
