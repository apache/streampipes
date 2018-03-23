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

import java.util.List;
import java.util.Map;

import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventPropertyPrimitive;

public class RandomEventGenerator extends EventGenerator {

	private RandomDataGenerator dataGenerator;
	
	public RandomEventGenerator(EventSchema schema,
			FormatGenerator formatGenerator) {
		super(schema, formatGenerator);
		this.dataGenerator = new RandomDataGenerator();
	}

	@Override
	protected Map<String, Object> makeNestedProperty(EventPropertyNested nested) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object makePrimitiveProperty(EventPropertyPrimitive primitive) {
		return dataGenerator.getValue(primitive);
	}

	@Override
	protected List<?> makeListProperty(EventPropertyList list) {
		// TODO Auto-generated method stub
		return null;
	}

}
