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

package org.apache.streampipes.dataexplorer.influx;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataExplorerInfluxQueryExecutorTest {

  @Test
  public void makeLatestTimestampQueryGroupsMeasurementsByField() {
    var measurementFields = new LinkedHashMap<String, String>();
    measurementFields.put("measure-1", "value");
    measurementFields.put("measure-2", "value");
    measurementFields.put("measure-3", "temperature");

    var query = new DataExplorerInfluxQueryExecutor().makeLatestTimestampQuery(measurementFields);

    assertEquals("SELECT LAST(\"temperature\") FROM /^(measure-3)$/;"
        + "SELECT LAST(\"value\") FROM /^(measure-1|measure-2)$/", query.getCommand());
  }
}
