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

package org.apache.streampipes.extensions.management.connect.adapter.transform.value;

import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.transform.value.UnitTransformationRule;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.transform.value.ValueEventTransformer;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.transform.value.ValueTransformationRule;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class ValueEventTransformerTest {

  @Test
  public void transform() {
    EventSchema eventSchema = new EventSchema();
    EventProperty eventPropertyf = new EventPropertyPrimitive();
    eventPropertyf.setLabel("a");
    eventPropertyf.setRuntimeName("a");
    eventSchema.addEventProperty(eventPropertyf);

    Map<String, Object> event = new HashMap<>();
    event.put("a", 273.15);

    List<String> keys = new ArrayList<>();
    keys.add("a");

    List<ValueTransformationRule> rules = new ArrayList<>();
    rules.add(new UnitTransformationRule(keys,
        "http://qudt.org/vocab/unit#Kelvin", "http://qudt.org/vocab/unit#DegreeCelsius"));

    ValueEventTransformer eventTransformer = new ValueEventTransformer(rules);
    Map<String, Object> result = eventTransformer.transform(event);

    assertEquals(0.0, result.get(eventPropertyf.getRuntimeName()));

  }

}
