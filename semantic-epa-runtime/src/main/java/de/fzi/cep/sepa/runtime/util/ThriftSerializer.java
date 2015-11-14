package de.fzi.cep.sepa.runtime.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.SimpleEvent;
import eu.proasense.internal.VariableType;

public class ThriftSerializer {
	
	public ThriftSerializer()
	{
		
	}
	
	public SimpleEvent toSimpleEvent(Object graph) {
		@SuppressWarnings("unchecked")
		Map<String, Object> mapValue = (Map<String, Object>) graph;
		
		SimpleEvent simpleEvent = new SimpleEvent();
		simpleEvent.setSensorId("CEP");
		Map<String, ComplexValue> eventProperties = new HashMap<>();
		for(String propertyName : mapValue.keySet())
		{
			if (!propertyName.equals("time"))
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
		else if (object instanceof java.lang.Boolean) return VariableType.BOOLEAN;
		else return VariableType.STRING;
	}
	
	public Object toMap(SimpleEvent simpleEvent) {
		Map<String, Object> map = new HashMap<>();
		map.put("time", simpleEvent.getTimestamp());
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
