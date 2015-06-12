package de.fzi.cep.sepa.model.impl.quality;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "ssn",   "http://purl.oclc.org/NET/ssnx/ssn#"})
@RdfsClass("ssn:Frequency")
@Entity
public class Frequency extends EventStreamQuality {

	public Frequency() {
		super();
	}

	public Frequency(float quantityValue) {
		super();
		this.quantityValue = quantityValue;
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
