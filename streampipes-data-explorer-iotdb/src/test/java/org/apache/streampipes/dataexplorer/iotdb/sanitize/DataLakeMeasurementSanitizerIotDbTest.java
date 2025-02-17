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

package org.apache.streampipes.dataexplorer.iotdb.sanitize;

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.test.generator.EventPropertyPrimitiveTestBuilder;
import org.apache.streampipes.test.generator.EventSchemaTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class DataLakeMeasurementSanitizerIotDbTest {

  private IStreamPipesClient clientMock;

  @BeforeEach
  public void setUp() {
    this.clientMock = mock(IStreamPipesClient.class);
  }

  @Test
  public void cleanDataLakeMeasure() {
    var eventSchema = EventSchemaTestBuilder.create()
        .withEventProperty(
            EventPropertyPrimitiveTestBuilder
                .create()
                .withRuntimeName("timestamp")
                .build())
        .withEventProperty(
            EventPropertyPrimitiveTestBuilder
                .create()
                .withRuntimeName("all")
                .build())
        .withEventProperty(
            EventPropertyPrimitiveTestBuilder
                .create()
                .withRuntimeName("pressure")
                .build())
        .build();
    var measure = new DataLakeMeasure(
        "invalid.Measure",
        "s0::%s".formatted("timestamp"),
        eventSchema
    );
    var sanitizer = new DataLakeMeasurementSanitizerIotDb(clientMock, measure);

    sanitizer.cleanDataLakeMeasure();
    var result = sanitizer.getMeasure();

    assertEquals("invalid_Measure", result.getMeasureName());
    assertEquals(3, result.getEventSchema().getEventProperties().size());
    assertEquals("timestamp_", result.getEventSchema().getEventProperties().get(0).getRuntimeName());
    assertEquals("all_", result.getEventSchema().getEventProperties().get(1).getRuntimeName());
    assertEquals("pressure", result.getEventSchema().getEventProperties().get(2).getRuntimeName());
  }
}
