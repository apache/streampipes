package org.streampipes.model.impl.quality;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.UnnamedSEPAElement;

import java.net.URI;

import javax.persistence.Entity;

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
