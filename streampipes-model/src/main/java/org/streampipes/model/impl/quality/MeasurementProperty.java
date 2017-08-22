package org.streampipes.model.impl.quality;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfsClass;

import org.streampipes.model.UnnamedSEPAElement;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "ssn",   "http://purl.oclc.org/NET/ssnx/ssn#"})
@RdfsClass("ssn:MeasurementProperty")
@Entity
public class MeasurementProperty extends UnnamedSEPAElement {

	private static final long serialVersionUID = 8527800469513813552L;

	public MeasurementProperty() {
		super();
	}
	
	public MeasurementProperty(MeasurementProperty other) {
		super(other);
	}

}
