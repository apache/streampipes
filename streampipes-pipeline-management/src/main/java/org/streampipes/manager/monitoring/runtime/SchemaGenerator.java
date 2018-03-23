/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.manager.monitoring.runtime;

import org.streampipes.manager.monitoring.job.MonitoringUtils;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;

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
