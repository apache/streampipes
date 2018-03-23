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
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		return formatGenerator.makeOutputFormat(makeEvent(new HashMap<>(), schema.getEventProperties()));
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
