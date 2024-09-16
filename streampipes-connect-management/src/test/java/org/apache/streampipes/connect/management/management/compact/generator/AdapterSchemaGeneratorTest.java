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

import org.apache.streampipes.connect.management.compact.SchemaMetadataEnricher;
import org.apache.streampipes.connect.management.compact.generator.AdapterSchemaGenerator;
import org.apache.streampipes.connect.management.management.GuessManagement;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.compact.CompactAdapter;
import org.apache.streampipes.model.connect.adapter.compact.CompactEventProperty;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdapterSchemaGeneratorTest {

  @Test
  public void testAdapterSchemaGenerator() throws Exception {
    var compactAdapter = mock(CompactAdapter.class);
    var guessMgmt = mock(GuessManagement.class);

    var epDef = new CompactEventProperty(
        "label",
        "description",
        "dimension",
        "semanticType"

    );
    var ep = new EventPropertyPrimitive();
    var schema = new EventSchema(List.of(ep));
    var adapterDescription = new AdapterDescription();
    var guessedSchema = new GuessSchema();

    ep.setRuntimeName("a");
    guessedSchema.setEventSchema(schema);

    when(guessMgmt.guessSchema(adapterDescription)).thenReturn(guessedSchema);
    when(compactAdapter.schema()).thenReturn(Map.of("a", epDef));

    new AdapterSchemaGenerator(new SchemaMetadataEnricher(), guessMgmt).apply(adapterDescription, compactAdapter);
    var properties = adapterDescription.getEventSchema().getEventProperties();
    assertEquals(1, properties.size());
    assertEquals(
        "label",
        properties.get(0).getLabel()
    );
    assertEquals("description", properties.get(0).getDescription());
    assertEquals(PropertyScope.DIMENSION_PROPERTY.name(), properties.get(0).getPropertyScope());
    assertEquals("semanticType", properties.get(0).getDomainProperties().get(0).toString());
  }
}
