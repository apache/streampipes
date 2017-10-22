package org.streampipes.model.impl.quality;

import com.clarkparsia.empire.annotation.RdfsClass;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

@RdfsClass("sepa:EventPropertyQualityDefinition")
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
