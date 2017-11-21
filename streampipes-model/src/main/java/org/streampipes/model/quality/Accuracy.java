package org.streampipes.model.quality;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.SSN;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(SSN.ACCURACY)
@Entity
public class Accuracy extends EventPropertyQualityDefinition {
	
	private static final long serialVersionUID = -4368302218285302897L;

	public Accuracy() {
		super();
	}
	
	public Accuracy(float quantityValue) {
		this.quantityValue = quantityValue;
	}
	
	public Accuracy(Accuracy other) {
		super(other);
		this.quantityValue = other.getQuantityValue();
	}
	
	@RdfProperty(StreamPipes.HAS_QUANTITY_VALUE)
	private float quantityValue;

	public float getQuantityValue() {
		return quantityValue;
	}

	public void setQuantityValue(float quantityValue) {
		this.quantityValue = quantityValue;
	}

}
