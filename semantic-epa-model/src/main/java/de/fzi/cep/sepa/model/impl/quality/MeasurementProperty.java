package de.fzi.cep.sepa.model.impl.quality;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.UnnamedSEPAElement;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "ssn",   "http://purl.oclc.org/NET/ssnx/ssn#"})
@RdfsClass("ssn:MeasurementProperty")
@Entity
public class MeasurementProperty extends UnnamedSEPAElement {
	public MeasurementProperty() {
		super();
	}

}
