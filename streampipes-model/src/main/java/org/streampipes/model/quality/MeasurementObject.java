package org.streampipes.model.quality;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.StreamPipes;

import java.net.URI;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.MEASUREMENT_OBJECT)
@Entity
public class MeasurementObject extends UnnamedStreamPipesEntity {

	private static final long serialVersionUID = 4391097898611686930L;
	
	@RdfProperty(StreamPipes.MEASURES_OBJECT)
	private URI measuresObject;
	
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
