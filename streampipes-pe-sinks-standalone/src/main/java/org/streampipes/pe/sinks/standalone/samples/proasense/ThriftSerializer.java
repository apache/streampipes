package org.streampipes.pe.sinks.standalone.samples.proasense;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.SimpleEvent;
import eu.proasense.internal.VariableType;

public class ThriftSerializer {

	private TDeserializer deserializer;
	private TSerializer serializer;
	
	public ThriftSerializer()
	{
		deserializer = new TDeserializer(new TBinaryProtocol.Factory());
		serializer = new TSerializer(new TBinaryProtocol.Factory());
	}
	
	public SimpleEvent toSimpleEvent(Object graph) {
		@SuppressWarnings("unchecked")
		Map<String, Object> mapValue = (Map<String, Object>) graph;
		
		SimpleEvent simpleEvent = new SimpleEvent();
		simpleEvent.setSensorId("CEP");
		Map<String, ComplexValue> eventProperties = new HashMap<>();
		for(String propertyName : mapValue.keySet())
		{
			if (!propertyName.equals("timestamp"))
			{
				ComplexValue value = new ComplexValue();
				value.setValue(String.valueOf(mapValue.get(propertyName)));
				value.setType(getType(mapValue.get(propertyName)));
				eventProperties.put(propertyName, value);
			}
			else simpleEvent.setTimestamp(Long.parseLong(String.valueOf(mapValue.get(propertyName))));
		}
		simpleEvent.setEventProperties(eventProperties);
		return simpleEvent;
	}

	private VariableType getType(Object object) {
		if (object instanceof java.lang.Double) return VariableType.DOUBLE;
		else if (object instanceof java.lang.Integer) return VariableType.LONG;
		else if (object instanceof java.lang.Long) return VariableType.LONG;
		else return VariableType.STRING;
	}
	
	public Object toMap(SimpleEvent simpleEvent) {
		Map<String, Object> map = new HashMap<>();
		map.put("timestamp", simpleEvent.getTimestamp());
		Map<String, ComplexValue> eventProperties = simpleEvent.getEventProperties();
		for(String propertyName : eventProperties.keySet())
		{
			Object value = getValue(eventProperties.get(propertyName).getValue());
			map.put(propertyName, value);
		}
		return map;
	}
	
	private Object getValue(String value) {
		if (isNumber(value) && value.contains(".")) return Double.parseDouble(value);
		else if (isNumber(value)) return Integer.parseInt(value);
		else return value;
	}
	
	private boolean isNumber(String value) {
		return NumberUtils.isNumber(value);
	}
}
