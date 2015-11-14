package de.fzi.cep.sepa.util;

import java.util.HashMap;
import java.util.Map;

import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.SimpleEvent;
import eu.proasense.internal.VariableType;

public class TestThriftDataFormat {

	public static void main(String[] args)
	{
		SimpleEvent simpleEvent = new SimpleEvent();
		Map<String, ComplexValue> complexValues = new HashMap<>();
		ComplexValue timestamp = new ComplexValue();
		timestamp.setType(VariableType.LONG);
		timestamp.setValue("123");
		complexValues.put("timestamp", timestamp);
		
		simpleEvent.setEventProperties(complexValues);
		
		//Object map = new ThriftDataFormat().toMap(simpleEvent);
		//SimpleEvent simpleEvent2 = new ThriftDataFormat().toSimpleEvent(map);
		//System.out.println(simpleEvent2.getEventProperties().get("timestamp").toString());
		
	}
}
