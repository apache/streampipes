package org.streampipes.pe.sources.hella.streams;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.commons.Utils;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.pe.sources.hella.config.HellaVariables;
import org.streampipes.pe.sources.hella.main.AbstractHellaStream;

public class RawMaterialCertificateStream extends AbstractHellaStream {

	@Override
	public EventStream declareModel(SepDescription sep) {
		
		EventStream stream = prepareStream(HellaVariables.RawMaterialCertificate.topic());
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "variable_type", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "variable_timestamp", "", Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "materialId", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "orderNumber", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "mvrMethod", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "mvrAverage", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "mvrMin", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "mvrMax", "", Utils.createURI(SO.Number)));
		
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(HellaVariables.RawMaterialCertificate.eventName());
		stream.setDescription(HellaVariables.RawMaterialCertificate.description());
		stream.setUri(sep.getUri() + "/rawMaterialCertificate");
		
		return stream;
	}

}
