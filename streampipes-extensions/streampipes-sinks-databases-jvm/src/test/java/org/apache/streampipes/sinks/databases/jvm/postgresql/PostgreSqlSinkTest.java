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

package org.apache.streampipes.sinks.databases.jvm.postgresql;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the implementation of the {@link PostgreSqlSink} class.
 */
class PostgreSqlSinkTest {

  private PostgreSqlSink sink;

  /**
   * Set up the values a user entered in the sink configuration.
   * Only the batch size is filled in.
   *
   * @param batchSize the batch size the user entered.
   * @return the parameters the sink receives when a pipeline is started.
   */
  IDataSinkParameters newParameters(Integer batchSize) {
    IDataSinkParameterExtractor extractor = mock(IDataSinkParameterExtractor.class);
    when(extractor.singleValueParameter(PostgreSqlSink.BATCH_SIZE_KEY, Integer.class)).thenReturn(batchSize);

    IDataSinkParameters parameters = mock(IDataSinkParameters.class);
    when(parameters.extractor()).thenReturn(extractor);
    return parameters;
  }

  @BeforeEach
  void setUp() {
    sink = new PostgreSqlSink();
  }

  @Test
  void testOnPipelineStarted_batchSizeIsMissing_throwsException() {
    IDataSinkParameters parameters = newParameters(null);

    try {
      sink.onPipelineStarted(parameters, null);
      fail("No exception on #onPipelineStarted");
    } catch (SpRuntimeException e) {
      String expected = "Batch size must be at least 1, but was 'null'. "
          + "Use 1 to write each event immediately, or a higher value to write events in batches.";
      assertEquals(expected, e.getMessage());
    }
  }

  @Test
  void testOnPipelineStarted_batchSizeIsZero_throwsException() {
    int batchSize = 0;

    IDataSinkParameters parameters = newParameters(batchSize);

    try {
      sink.onPipelineStarted(parameters, null);
      fail("No exception on #onPipelineStarted");
    } catch (SpRuntimeException e) {
      String expected = "Batch size must be at least 1, but was '" + batchSize + "'. "
          + "Use 1 to write each event immediately, or a higher value to write events in batches.";
      assertEquals(expected, e.getMessage());
    }
  }

  @Test
  void testOnPipelineStarted_batchSizeIsNegative_throwsException() {
    int batchSize = -5;

    IDataSinkParameters parameters = newParameters(batchSize);

    try {
      sink.onPipelineStarted(parameters, null);
      fail("No exception on #onPipelineStarted");
    } catch (SpRuntimeException e) {
      String expected = "Batch size must be at least 1, but was '" + batchSize + "'. "
          + "Use 1 to write each event immediately, or a higher value to write events in batches.";
      assertEquals(expected, e.getMessage());
    }
  }
}
