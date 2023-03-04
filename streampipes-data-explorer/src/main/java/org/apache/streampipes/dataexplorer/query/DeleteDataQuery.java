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

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.dataexplorer.commons.influx.InfluxClientProvider;
import org.apache.streampipes.model.datalake.DataLakeMeasure;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

public class DeleteDataQuery {

  private final DataLakeMeasure measure;

  public DeleteDataQuery(DataLakeMeasure measure) {
    this.measure = measure;
  }

  private String getQuery() {
    return "DROP MEASUREMENT \"" + measure.getMeasureName() + "\"";
  }

  public QueryResult executeQuery() throws RuntimeException {
    try (final InfluxDB influxDB = InfluxClientProvider.getInfluxDBClient()) {
      var databaseName = getEnvironment().getTsStorageBucket().getValueOrDefault();

      var query = new Query(getQuery(), databaseName);
      return influxDB.query(query);
    }
  }

  private Environment getEnvironment() {
    return Environments.getEnvironment();
  }
}
