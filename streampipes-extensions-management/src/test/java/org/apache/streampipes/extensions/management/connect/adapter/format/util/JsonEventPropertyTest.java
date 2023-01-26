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

package org.apache.streampipes.extensions.management.connect.adapter.format.util;

import static org.junit.Assert.assertEquals;

import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.junit.Test;

public class JsonEventPropertyTest {

	@Test
	public void getEventPropertyForLongValue() {
		EventPropertyPrimitive p = (EventPropertyPrimitive)JsonEventProperty.getEventProperty("timestamp_in_ms", 1674637204330L);

		assertEquals("http://www.w3.org/2001/XMLSchema#long", p.getRuntimeType());
	}

	@Test
	public void getEventPropertyForDoubleValue() {
		EventPropertyPrimitive p = (EventPropertyPrimitive)JsonEventProperty.getEventProperty("double", 1674637204330.0D);

		assertEquals("http://www.w3.org/2001/XMLSchema#double", p.getRuntimeType());
	}

	@Test
	public void getEventPropertyForFloatValue() {
		EventPropertyPrimitive p = (EventPropertyPrimitive)JsonEventProperty.getEventProperty("float", 1674637204330.0f);

		assertEquals("http://www.w3.org/2001/XMLSchema#float", p.getRuntimeType());
	}

	@Test
	public void getEventPropertyForIntegerValue() {
		EventPropertyPrimitive p = (EventPropertyPrimitive)JsonEventProperty.getEventProperty("integer", 1674637204);

		assertEquals("http://www.w3.org/2001/XMLSchema#integer", p.getRuntimeType());
	}
}
