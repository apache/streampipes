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
package org.apache.streampipes.sdk.builder;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.sdk.utils.Datatypes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NestedPropertyBuilderTest {

  @Test
  public void buildNestedProperty() {
    EventPropertyNested result = NestedPropertyBuilder.create("TestProperty")
            .withEventProperty(PrimitivePropertyBuilder.create(Datatypes.String, "SubProperty1").build())
            .withEventProperty(PrimitivePropertyBuilder.create(Datatypes.String, "SubProperty2").build()).build();

    Assertions.assertNotNull(result);
    Assertions.assertEquals("TestProperty", result.getRuntimeName());
    Assertions.assertEquals(2, result.getEventProperties().size());
  }

  @Test
  public void buildNestedPropertyWithoutProperties() {
    EventPropertyNested result = NestedPropertyBuilder.create("TestProperty").build();

    Assertions.assertNotNull(result);
    Assertions.assertEquals("TestProperty", result.getRuntimeName());
    Assertions.assertTrue(result.getEventProperties().isEmpty());
  }

  @Test
  public void buildNestedPropertyWithArray() {
    EventPropertyNested result = NestedPropertyBuilder.create("TestProperty")
            .withEventProperties(PrimitivePropertyBuilder.create(Datatypes.String, "SubProperty1").build(),
                    PrimitivePropertyBuilder.create(Datatypes.String, "SubProperty2").build())
            .build();

    Assertions.assertNotNull(result);
    Assertions.assertEquals("TestProperty", result.getRuntimeName());
    Assertions.assertEquals(2, result.getEventProperties().size());
  }

  @Test
  public void createBuilderWithNullName() {
    assertThrows(IllegalArgumentException.class, () -> NestedPropertyBuilder.create(null));
  }

  @Test
  public void createBuilderWithEmptyName() {
    assertThrows(IllegalArgumentException.class, () -> NestedPropertyBuilder.create(""));
  }
}
