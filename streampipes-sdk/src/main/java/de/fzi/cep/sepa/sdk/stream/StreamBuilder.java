package de.fzi.cep.sepa.sdk.stream;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;

import java.net.URI;

public class StreamBuilder {

	private EventStream stream;
	private EventSchema schema;

	private StreamBuilder(String name, String description, String uri)
	{
		stream = new EventStream();
		stream.setName(name);
		stream.setDescription(description);
		stream.setUri(uri);
	}
	
	private StreamBuilder(String uri)
	{
		stream = new EventStream();
		stream.setUri(uri);
		schema = new EventSchema();
	}
	
	public static StreamBuilder createStream(String name, String description, String uri)
	{
		return new StreamBuilder(name, description, uri);
	}
	
	public static StreamBuilder createStreamRestriction(String uri)
	{
		return new StreamBuilder(uri);
	}
	
	public StreamBuilder property(String propertyName, URI propertyType, URI subclassOf)
	{
		schema.addEventProperty(new EventPropertyPrimitive(propertyType.toString(), propertyName, "", Utils.createURI(subclassOf.toString())));
		return this;
	}
	
	public StreamBuilder icon(String iconUrl)
	{
		stream.setIconUrl(iconUrl);
		return this;
	}
	
	public StreamBuilder schema(EventSchema eventSchema)
	{
		stream.setEventSchema(eventSchema);
		return this;
	}
	
	public StreamBuilder grounding(EventGrounding eventGrounding)
	{
		stream.setEventGrounding(eventGrounding);
		return this;
	}
	
	public EventStream build()
	{
		return stream;
	}

}
