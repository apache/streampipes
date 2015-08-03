package de.fzi.cep.sepa.manager.monitoring.runtime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fzi.cep.sepa.manager.monitoring.job.MonitoringUtils;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyList;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyNested;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;

public abstract class EventGenerator {

	protected EventSchema schema;
	private FormatGenerator formatGenerator;
	
	public EventGenerator(EventSchema schema, FormatGenerator formatGenerator)
	{
		this.schema = schema;
		this.formatGenerator = formatGenerator;
	}
	
	public Object nextEvent()
	{
		return formatGenerator.makeOutputFormat(makeEvent(new HashMap<String, Object>(), schema.getEventProperties()));
	}
	
	protected Map<String, Object> makeEvent(Map<String, Object> map, List<EventProperty> properties)
	{
		for(EventProperty p : properties)
		{
			if (p instanceof EventPropertyPrimitive) map.put(randomKey(), makePrimitiveProperty((EventPropertyPrimitive) p));
			else if (p instanceof EventPropertyNested) map.put(randomKey(), makeNestedProperty((EventPropertyNested) p));
			else if (p instanceof EventPropertyList) map.put(randomKey(), makeListProperty((EventPropertyList) p));
			else throw new IllegalArgumentException("Wrong type detected");
		}
		
		return map;
	}
	
	private String randomKey()
	{
		return MonitoringUtils.randomKey();
	}
	
	protected abstract Map<String, Object> makeNestedProperty(EventPropertyNested nested);
	
	protected abstract Object makePrimitiveProperty(EventPropertyPrimitive primitive);
	
	protected abstract List<?> makeListProperty(EventPropertyList list);
	
}
