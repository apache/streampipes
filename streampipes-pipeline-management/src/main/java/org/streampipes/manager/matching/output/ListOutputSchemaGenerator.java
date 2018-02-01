package org.streampipes.manager.matching.output;

import org.streampipes.commons.Utils;
import org.streampipes.empire.core.empire.SupportsRdfId;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.output.ListOutputStrategy;

import java.net.URI;
import java.util.List;

public class ListOutputSchemaGenerator implements OutputSchemaGenerator<ListOutputStrategy> {

	private String propertyName;
	
	public ListOutputSchemaGenerator(String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public EventSchema buildFromOneStream(SpDataStream stream) {
		return makeList(stream.getEventSchema().getEventProperties());
	}

	@Override
	public EventSchema buildFromTwoStreams(SpDataStream stream1,
			SpDataStream stream2) {
		return buildFromOneStream(stream1);
	}
	
	private EventSchema makeList(List<EventProperty> schemaProperties)
	{
		EventPropertyList list = new EventPropertyList();
		list.setEventProperties(schemaProperties);
		list.setRuntimeName(propertyName);
		list.setRdfId(new SupportsRdfId.URIKey(URI.create(schemaProperties.get(0).getRdfId()+"-list")));
		EventSchema schema = new EventSchema();
		schema.setEventProperties(Utils.createList(list));
		return schema;
	}

	@Override
	public ListOutputStrategy getModifiedOutputStrategy(
			ListOutputStrategy strategy) {
		return strategy;
	}

}
