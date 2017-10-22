package org.streampipes.model.impl.quality;

import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import javax.persistence.Entity;

@RdfsClass("ssn:Accuracy")
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
	
	@RdfProperty("sepa:hasQuantityValue")
	float quantityValue;

	public float getQuantityValue() {
		return quantityValue;
	}

	public void setQuantityValue(float quantityValue) {
		this.quantityValue = quantityValue;
	}

	//@Override
	public int compareTo(EventPropertyQualityDefinition o) {
		Accuracy other = (Accuracy) o;
		if (other.getQuantityValue() == this.getQuantityValue()) {
			return 0;
			
		} else if ((other).getQuantityValue() > this.getQuantityValue()) {
			return -1;
		} else {
			return 1;
		}
	}


}
