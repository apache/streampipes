package org.streampipes.model.quality;

import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

@RdfsClass(StreamPipes.EVENT_PROPERTY_QUALITY_DEFINITION)
@MappedSuperclass
@Entity
public abstract class EventPropertyQualityDefinition extends MeasurementProperty /*implements Comparable<EventPropertyQualityDefinition>*/{

	private static final long serialVersionUID = -3849772043514528797L;
	
	public EventPropertyQualityDefinition() {
		super();
	}
	
	public EventPropertyQualityDefinition(EventPropertyQualityDefinition other) {
		super(other);
	}
}
