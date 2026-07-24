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

package org.apache.streampipes.dataexplorer.api;

import org.apache.streampipes.dataexplorer.export.ConfiguredOutputWriterFactory;
import org.apache.streampipes.dataexplorer.export.OutputFormat;
import org.apache.streampipes.model.datalake.SpQueryResult;
import org.apache.streampipes.model.datalake.param.ProvidedRestQueryParams;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.streampipes.model.datalake.param.SupportedRestQueryParams.QP_END_DATE;
import static org.apache.streampipes.model.datalake.param.SupportedRestQueryParams.QP_LIMIT;
import static org.apache.streampipes.model.datalake.param.SupportedRestQueryParams.QP_MISSING_VALUE_BEHAVIOUR;
import static org.apache.streampipes.model.datalake.param.SupportedRestQueryParams.QP_ORDER;
import static org.apache.streampipes.model.datalake.param.SupportedRestQueryParams.QP_START_DATE;

public interface IDataExplorerQueryManagement {

  SpQueryResult getData(
    ProvidedRestQueryParams queryParams,
    boolean ignoreMissingData) throws IllegalArgumentException;

  void getDataAsStream(ProvidedRestQueryParams params,
                       OutputFormat format,
                       ConfiguredOutputWriterFactory outputWriterFactory,
                       boolean ignoreMissingValues,
                       OutputStream outputStream) throws IOException;

  boolean deleteData(String measurementID);

  boolean deleteData(String measurementName, Long startDate, Long endDate);

  boolean deleteAllData();

  Map<String, Object> getTagValues(String measurementId,
                                   String fields);

  default Map<String, Long> getLatestTimestamps(List<String> measurementNames) {
    Map<String, Long> latestTimestamps = new HashMap<>();
    measurementNames.forEach(measurementName -> latestTimestamps.put(measurementName, getLatestTimestamp(
        measurementName)));
    return latestTimestamps;
  }

  private Long getLatestTimestamp(String measurementName) {
    Map<String, String> queryParams = Map.of(
        QP_START_DATE, "0",
        QP_END_DATE, String.valueOf(System.currentTimeMillis()),
        QP_LIMIT, "1",
        QP_ORDER, "DESC",
        QP_MISSING_VALUE_BEHAVIOUR, "empty"
    );

    try {
      return getData(new ProvidedRestQueryParams(measurementName, queryParams), true).getLastTimestamp();
    } catch (RuntimeException e) {
      return 0L;
    }
  }
}
