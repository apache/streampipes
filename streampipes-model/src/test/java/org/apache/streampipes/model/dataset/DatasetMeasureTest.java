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

package org.apache.streampipes.model.dataset;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DatasetMeasureTest {

  private static final String TIMESTAMP_FIELD = "testTimestamp";
  private static final String STREAM_PREFIX = "s0::";
  private static final String TIMESTAMP_FIELD_NAME = STREAM_PREFIX + TIMESTAMP_FIELD;

  private DatasetMeasure datasetMeasure = new DatasetMeasure();

  @Test
  public void setTimestampFieldSuccess() {
    datasetMeasure.setTimestampField(TIMESTAMP_FIELD_NAME);

    var result = datasetMeasure.getTimestampField();

    Assertions.assertEquals(TIMESTAMP_FIELD_NAME, result);
  }

  @Test
  public void setTimestampFieldAssertionError() {
    try {
      datasetMeasure.setTimestampField(TIMESTAMP_FIELD);
    } catch (AssertionError assertionError) {
      Assertions.assertEquals(DatasetMeasure.ASSERTION_ERROR_MESSAGE, assertionError.getMessage());
    }
  }

  @Test
  public void getTimestampFieldName() {
    datasetMeasure.setTimestampField(TIMESTAMP_FIELD_NAME);

    var result = datasetMeasure.getTimestampFieldName();

    Assertions.assertEquals(TIMESTAMP_FIELD, result);
  }

}
