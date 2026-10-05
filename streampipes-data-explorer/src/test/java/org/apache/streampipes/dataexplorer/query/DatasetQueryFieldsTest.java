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

package org.apache.streampipes.dataexplorer.query;

import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatasetQueryFieldsTest {
  @Test
  void skipsDimensionsAndInvalidNamesAndKeepsSchemaOrder() {
    var dimension = field("machine");
    dimension.setPropertyScope(PropertyScope.DIMENSION_PROPERTY.name());
    var dataset = new DatasetMetadata();
    dataset.setEventSchema(new EventSchema(Arrays.asList(null, dimension, field(null), field(""), field("*"),
        field("temperature"), field("pressure"))));
    assertEquals(Optional.of("temperature"), DatasetQueryFields.firstCountableProperty(dataset));
  }

  @Test
  void absentOrDimensionOnlySchemaHasNoFallbackField() {
    var dataset = new DatasetMetadata();
    dataset.setEventSchema(null);
    assertEquals(Optional.empty(), DatasetQueryFields.firstCountableProperty(dataset));
    var dimension = field("machine");
    dimension.setPropertyScope(PropertyScope.DIMENSION_PROPERTY.name());
    dataset.setEventSchema(new EventSchema(List.of(dimension)));
    assertEquals(Optional.empty(), DatasetQueryFields.firstCountableProperty(dataset));
  }

  private EventPropertyPrimitive field(String name) {
    var field = new EventPropertyPrimitive();
    field.setRuntimeName(name);
    return field;
  }
}
