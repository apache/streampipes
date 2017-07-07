package de.fzi.cep.sepa.model.impl.quality;

import java.net.URI;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.UnnamedSEPAElement;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "ssn",   "http://purl.oclc.org/NET/ssnx/ssn#"})
@RdfsClass("sepa:MeasurementCapability")
@Entity
public class MeasurementCapability extends UnnamedSEPAElement {

	private static final long serialVersionUID = -7561544835976781403L;
	
	@RdfProperty("sepa:hasCapability")
	URI capability;
	
	public MeasurementCapability() {
		super();
	}
	
	public MeasurementCapability(MeasurementCapability other) {
		super(other);
		this.capability = other.getCapability();
	}
	
	public MeasurementCapability(URI capability) {
		super();
		this.capability = capability;
	}

	public URI getCapability() {
		return capability;
	}

	public void setCapability(URI capability) {
		this.capability = capability;
	}
	
	
}
