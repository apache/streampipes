package de.fzi.cep.sepa.model.impl.quality;

import java.net.URI;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.UnnamedSEPAElement;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "ssn",   "http://purl.oclc.org/NET/ssnx/ssn#"})
@RdfsClass("sepa:MeasurementObject")
@Entity
public class MeasurementObject extends UnnamedSEPAElement{

	private static final long serialVersionUID = 4391097898611686930L;
	
	@RdfProperty("sepa:measuresObject")
	URI measuresObject;
	
	public MeasurementObject() {
		super();
	}
	
	public MeasurementObject(MeasurementObject other) {
		super(other);
		this.measuresObject = other.getMeasuresObject();
	}
	
	public MeasurementObject(URI measurementObject) {
		super();
		this.measuresObject = measurementObject;
	}

	public URI getMeasuresObject() {
		return measuresObject;
	}

	public void setMeasuresObject(URI measurementObject) {
		this.measuresObject = measurementObject;
	}
	
	
}
