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

package org.apache.streampipes.dataexplorer.influx.sanitize;

import org.apache.streampipes.client.api.IDatasetMeasureApi;
import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.dataset.DatasetMeasure;
import org.apache.streampipes.test.generator.EventPropertyPrimitiveTestBuilder;
import org.apache.streampipes.test.generator.EventSchemaTestBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DatasetMeasurementSanitizerInfluxTest {

  private IStreamPipesClient clientMock;

  @BeforeEach
  public void setUp() {
    clientMock = mock(IStreamPipesClient.class);

    var apiMock = mock(IDatasetMeasureApi.class);
    when(clientMock.datasetMeasureApi()).thenReturn(apiMock);

  }

  @Test
  public void cleanDatasetMeasure() {
    var measure = new DatasetMeasure(
      "test?Measurement",
      "timestamp",
      EventSchemaTestBuilder.create()
                            .withEventProperties(List.of(
                              EventPropertyPrimitiveTestBuilder.create()
                                                               .withRuntimeName("timestamp")
                                                               .build(),
                              EventPropertyPrimitiveTestBuilder.create()
                                                               .withRuntimeName("value")
                                                               .build(),
                              EventPropertyPrimitiveTestBuilder.create()
                                                               .withRuntimeName("all")
                                                               .build()
                            ))
                            .build()
    );

    var result = new DatasetMeasurementSanitizerInflux(clientMock, measure).sanitizeAndRegister();

    assertEquals("test_Measurement", result.getMeasureName());
    assertEquals(2, result.getEventSchema().getEventProperties().size());
    assertEquals("value", result.getEventSchema().getEventProperties().get(0).getRuntimeName());
    assertEquals("all_", result.getEventSchema().getEventProperties().get(1).getRuntimeName());
  }

  @Test
  public void cleanDatasetMeasureNoTimestampField() {
    var measure = new DatasetMeasure("test", EventSchemaTestBuilder.create()
                                                                    .withEventProperties(List.of(
                                                                      EventPropertyPrimitiveTestBuilder.create()
                                                                                                       .withRuntimeName(
                                                                                                         "value")
                                                                                                       .build()
                                                                    ))
                                                                    .build());

    assertThrows(SpRuntimeException.class,
                 () -> new DatasetMeasurementSanitizerInflux(clientMock, measure).sanitizeAndRegister());
  }
}
