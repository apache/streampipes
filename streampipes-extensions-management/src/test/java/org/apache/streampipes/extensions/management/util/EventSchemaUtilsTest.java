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

package org.apache.streampipes.extensions.management.util;

import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.test.generator.EventPropertyNestedTestBuilder;
import org.apache.streampipes.test.generator.EventPropertyPrimitiveTestBuilder;
import org.apache.streampipes.test.generator.EventSchemaTestBuilder;
import org.apache.streampipes.vocabulary.SO;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventSchemaUtilsTest {

  EventProperty timestampProperty = EventPropertyPrimitiveTestBuilder.create()
      .withSemanticType(SO.DATE_TIME)
      .withRuntimeName("timestamp")
      .build();

  @Test
  public void noTimestampPropery() {

    var eventSchema = EventSchemaTestBuilder.create()
        .withEventProperty(
            EventPropertyPrimitiveTestBuilder.create().build())
        .build();

    var result = EventSchemaUtils.getTimestampProperty(eventSchema);

    assertFalse(result.isPresent());
  }

  @Test
  public void getTimestampProperty() {
    var eventSchema = EventSchemaTestBuilder.create()
        .withEventProperty(timestampProperty)
        .build();

    var result = EventSchemaUtils.getTimestampProperty(eventSchema);

    assertTrue(result.isPresent());
    assertEquals(result.get(), timestampProperty);
  }

  @Test
  public void getNestedTimestampProperty() {

    var eventSchema = EventSchemaTestBuilder.create()
        .withEventProperty(
            EventPropertyNestedTestBuilder.create()
                .withEventProperty(timestampProperty)
                .build())
        .build();

    var result = EventSchemaUtils.getTimestampProperty(eventSchema);

    assertTrue(result.isPresent());
    assertEquals(result.get(), timestampProperty);
  }
}