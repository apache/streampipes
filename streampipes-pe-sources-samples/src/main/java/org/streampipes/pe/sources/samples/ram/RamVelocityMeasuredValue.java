package org.streampipes.pe.sources.samples.ram;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.vocabulary.MessageFormat;
import org.streampipes.vocabulary.MhWirth;
import org.streampipes.vocabulary.XSD;
import org.streampipes.pe.sources.samples.config.AkerVariables;
import org.streampipes.pe.sources.samples.config.ProaSenseSettings;
import org.streampipes.commons.Utils;

public class RamVelocityMeasuredValue implements DataStreamDeclarer {

	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {
		SpDataStream stream = new SpDataStream();
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "variable_type", "", Utils.createURI("http://schema.org/Text")));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "variable_timestamp", "", Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "value", "", Utils.createURI(MhWirth.RamVelMeasured)));
		
		
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(AkerVariables.RamVelocityMeasuredValue.topic()));
		grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));
		
		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(AkerVariables.RamVelocityMeasuredValue.eventName());
		stream.setDescription(AkerVariables.RamVelocityMeasuredValue.description());
		stream.setUri(sep.getUri() + "/ramVelocityMeasuredValue");
		
		return stream;
	}

	@Override
	public void executeStream() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isExecutable() {
		// TODO Auto-generated method stub
		return false;
	}

}
