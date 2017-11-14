package org.streampipes.model.quality;

import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

@RdfsClass(StreamPipes.EVENT_STREAM_QUALITY_DEFINITION)
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