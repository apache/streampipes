package de.fzi.cep.sepa.storm.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import backtype.storm.spout.Scheme;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;

public class JsonScheme implements Scheme {
	private List<String> fields;

	public JsonScheme(EventStream eventStream) {
		EventSchema eventSchema = eventStream.getEventSchema();

		fields = new ArrayList<String>();
		
		for (EventProperty property : eventSchema.getEventProperties()) {
			// TODO add support for Nested and List Properties
			fields.add(property.getRuntimeName());
		}
	}

	@Override
	public List<Object> deserialize(byte[] ser) {
		String l = new String(ser);
		JSONObject j = new JSONObject(l);
		Object[] list = new Object[fields.size()];
	
		for (int i = 0; i < fields.size(); i++) {
			list[i]= (j.get(fields.get(i)));
		}
		
		
		
		return new Values(list);
	}

	@Override
	public Fields getOutputFields() {
		return new Fields(fields);
	}

}
