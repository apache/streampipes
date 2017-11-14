package org.streampipes.model.quality;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.StreamPipes;

import java.net.URI;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.MEASUREMENT_CAPABILITY)
@Entity
public class MeasurementCapability extends UnnamedStreamPipesEntity {

	private static final long serialVersionUID = -7561544835976781403L;
	
	@RdfProperty(StreamPipes.HAS_CAPABILTIY)
	private URI capability;
	
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
