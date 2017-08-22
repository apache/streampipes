package org.streampipes.manager.monitoring.runtime;

import org.streampipes.manager.monitoring.job.MonitoringUtils;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;

public class SchemaGenerator {

	public EventSchema generateSchema(EventSchema schemaRequirement, boolean minimumSchema)
	{
		EventSchema schema = new EventSchema();
		
		for(EventProperty requiredProperty : schemaRequirement.getEventProperties())
		{
			if (requiredProperty instanceof EventPropertyPrimitive)
				schema.addEventProperty(new EventPropertyPrimitive(((EventPropertyPrimitive) requiredProperty).getRuntimeType(), MonitoringUtils.randomKey(), "", requiredProperty.getDomainProperties()));
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
