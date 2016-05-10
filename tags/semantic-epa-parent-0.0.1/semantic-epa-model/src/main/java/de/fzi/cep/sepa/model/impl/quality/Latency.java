package de.fzi.cep.sepa.model.impl.quality;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "ssn",   "http://purl.oclc.org/NET/ssnx/ssn#"})
@RdfsClass("ssn:Latency")
@Entity
public class Latency extends EventPropertyQualityDefinition {

	private static final long serialVersionUID = -9211064635743833555L;
	
	@RdfProperty("sepa:hasQuantityValue")
	float quantityValue;

	public Latency() {
		super();
	}
	
	public Latency(float quantityValue) {
		this.quantityValue = quantityValue;
	}
	
	public float getQuantityValue() {
		return quantityValue;
	}

	public void setQuantityValue(float quantityValue) {
		this.quantityValue = quantityValue;
	}
	

	//@Override
	public int compareTo(EventPropertyQualityDefinition o) {
		Latency other = (Latency) o;
		if (other.getQuantityValue() == this.getQuantityValue()) {
			return 0;
			
		} else if ((other).getQuantityValue() > this.getQuantityValue()) {
			return -1;
		} else {
			return 1;
		}
	}

	
}
