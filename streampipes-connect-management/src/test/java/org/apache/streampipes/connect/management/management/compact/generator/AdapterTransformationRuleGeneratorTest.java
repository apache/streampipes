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

package org.apache.streampipes.connect.management.management.compact.generator;

import org.apache.streampipes.connect.management.compact.generator.AdapterTransformationRuleGenerator;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.compact.CompactAdapter;
import org.apache.streampipes.model.connect.adapter.compact.TransformationConfig;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdapterTransformationRuleGeneratorTest {

  @Test
  public void testCompactAdapterTransformationRuleRename() throws Exception {
    var compactAdapter = mock(CompactAdapter.class);
    var rename = Map.of("a", "b");
    var ep = new EventPropertyPrimitive();
    ep.setRuntimeName("a");
    var schema = new EventSchema(List.of(ep));
    var stream = new SpDataStream();
    stream.setEventSchema(schema);
    var adapterDescription = new AdapterDescription();
    adapterDescription.setDataStream(stream);
    when(compactAdapter.transform()).thenReturn(new TransformationConfig(rename, Map.of()));

    new AdapterTransformationRuleGenerator().apply(adapterDescription, compactAdapter);
    assertEquals(
        "b",
        adapterDescription.getDataStream().getEventSchema().getEventProperties().get(0).getRuntimeName()
    );
    assertEquals(1, adapterDescription.getRules().size());
  }

  @Test
  public void testCompactAdapterTransformationRuleMeasurementUnit() throws Exception {
    var compactAdapter = mock(CompactAdapter.class);
    var ep = new EventPropertyPrimitive();
    var measurementUnit = "https://streampipes.apache.org";
    var targetMeasurementUnit = "https://streampipes.apache.org/docs";
    ep.setRuntimeName("a");
    ep.setMeasurementUnit(URI.create(measurementUnit));
    var schema = new EventSchema(List.of(ep));
    var stream = new SpDataStream();
    stream.setEventSchema(schema);
    var adapterDescription = new AdapterDescription();
    adapterDescription.setDataStream(stream);
    when(compactAdapter.transform()).thenReturn(
        new TransformationConfig(
            Map.of(),
            Map.of("a", targetMeasurementUnit))
    );

    new AdapterTransformationRuleGenerator().apply(adapterDescription, compactAdapter);
    var transformedEp = adapterDescription.getDataStream().getEventSchema().getEventProperties().get(0);
    assertEquals(EventPropertyPrimitive.class, transformedEp.getClass());
    assertEquals(
        "a",
        transformedEp.getRuntimeName()
    );
    assertEquals(
        targetMeasurementUnit,
        ((EventPropertyPrimitive) transformedEp).getMeasurementUnit().toString()
    );
    assertEquals(1, adapterDescription.getRules().size());
  }
}
