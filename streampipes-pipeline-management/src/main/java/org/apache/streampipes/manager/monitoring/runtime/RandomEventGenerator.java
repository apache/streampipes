/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.manager.monitoring.runtime;

import java.util.List;
import java.util.Map;

import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.EventPropertyList;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;

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
