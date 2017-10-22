package org.streampipes.model.impl.quality;

import com.clarkparsia.empire.annotation.RdfsClass;
import org.streampipes.model.UnnamedSEPAElement;

import javax.persistence.Entity;

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
