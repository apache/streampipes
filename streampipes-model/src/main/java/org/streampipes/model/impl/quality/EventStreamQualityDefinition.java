package org.streampipes.model.impl.quality;

import org.streampipes.empire.annotations.RdfsClass;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

@RdfsClass("sepa:EventStreamQualityDefinition")
@MappedSuperclass
@Entity
public class EventStreamQualityDefinition extends MeasurementProperty {

	private static final long serialVersionUID = 6310763356941481868L;

	public EventStreamQualityDefinition() {
		super();
	}

	public EventStreamQualityDefinition(EventStreamQualityDefinition o) {
		super(o);
	}


}