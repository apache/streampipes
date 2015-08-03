package de.fzi.cep.sepa.manager.monitoring.runtime;

import de.fzi.cep.sepa.manager.monitoring.job.MonitoringUtils;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;

public class SchemaGenerator {

	public EventSchema generateSchema(EventSchema schemaRequirement, boolean minimumSchema)
	{
		EventSchema schema = new EventSchema();
		
		for(EventProperty requiredProperty : schemaRequirement.getEventProperties())
		{
			if (requiredProperty instanceof EventPropertyPrimitive)
				schema.addEventProperty(new EventPropertyPrimitive(((EventPropertyPrimitive) requiredProperty).getPropertyType(), MonitoringUtils.randomKey(), "", requiredProperty.getSubClassOf()));
			//else if (requiredProperty instanceof EventPropertyNested)
		}
		return schema;
	}
	
	private EventProperty addSampleProperty()
	{
		//TODO
		return null;
	}
}
