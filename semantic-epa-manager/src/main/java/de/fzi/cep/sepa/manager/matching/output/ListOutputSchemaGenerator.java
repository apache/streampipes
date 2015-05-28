package de.fzi.cep.sepa.manager.matching.output;

import java.net.URI;
import java.util.List;

import com.clarkparsia.empire.SupportsRdfId.URIKey;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyList;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;

public class ListOutputSchemaGenerator implements OutputSchemaGenerator {

	private String propertyName;
	
	public ListOutputSchemaGenerator(String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public EventSchema buildFromOneStream(EventStream stream) {
		return makeList(stream.getEventSchema().getEventProperties());
	}

	@Override
	public EventSchema buildFromTwoStreams(EventStream stream1,
			EventStream stream2) {
		return buildFromOneStream(stream1);
	}
	
	private EventSchema makeList(List<EventProperty> schemaProperties)
	{
		EventPropertyList list = new EventPropertyList();
		list.setEventProperties(schemaProperties);
		list.setRuntimeName(propertyName);
		list.setRdfId(new URIKey(URI.create(schemaProperties.get(0).getRdfId()+"-list")));
		EventSchema schema = new EventSchema();
		schema.setEventProperties(Utils.createList(list));
		return schema;
	}

}
