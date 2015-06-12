package de.fzi.cep.sepa.model.impl.quality;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "ssn",   "http://purl.oclc.org/NET/ssnx/ssn#"})
@RdfsClass("ssn:Precision")
@Entity
public class Precision extends EventPropertyQuality {

	@RdfProperty("sepa:hasQuantityValue")
	float quantityValue;

	public Precision() {
		super();
	}
	
	public Precision(float quantityValue) {
		this.quantityValue = quantityValue;
	}
	
	public float getQuantityValue() {
		return quantityValue;
	}

	public void setQuantityValue(float quantityValue) {
		this.quantityValue = quantityValue;
	}
}
