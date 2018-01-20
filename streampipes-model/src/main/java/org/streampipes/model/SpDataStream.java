package org.streampipes.model;

import org.apache.commons.lang.RandomStringUtils;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.quality.EventStreamQualityDefinition;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.vocabulary.StreamPipes;

import java.util.List;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.DATA_STREAM)
@Entity
public class SpDataStream extends SpDataSequence {

	private static final long serialVersionUID = -5732549347563182863L;
	
	private static final String prefix = "urn:fzi.de:eventstream:";

	public SpDataStream(String uri, String name, String description, String iconUrl, List<EventStreamQualityDefinition> hasEventStreamQualities,
											EventGrounding eventGrounding,
											EventSchema eventSchema) {
		super(uri, name, description, iconUrl, hasEventStreamQualities, eventGrounding, eventSchema);
	}
	
	public SpDataStream(String uri, String name, String description, EventSchema eventSchema)
	{
		super(uri, name, description, eventSchema);
	}

	public SpDataStream() {
		super(prefix +RandomStringUtils.randomAlphabetic(6));
	}


	public SpDataStream(SpDataStream other) {
		super(other);
	}



}
