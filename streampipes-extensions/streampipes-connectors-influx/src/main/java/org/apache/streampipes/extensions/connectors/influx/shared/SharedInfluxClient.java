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

package org.apache.streampipes.extensions.connectors.influx.shared;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataexplorer.commons.influx.InfluxClientProvider;
import org.apache.streampipes.dataexplorer.commons.influx.InfluxConnectionSettings;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Pong;

public abstract class SharedInfluxClient {

  protected InfluxConnectionSettings connectionSettings;

  protected String measureName;

  protected InfluxDB influxDb = null;

  public SharedInfluxClient(InfluxConnectionSettings connectionSettings,
                            String measureName) {
    this.connectionSettings = connectionSettings;
    this.measureName = measureName;
  }



  protected void initClient() throws SpRuntimeException {
    this.influxDb = InfluxClientProvider.getInfluxDBClient(connectionSettings);

    // Checking, if server is available
    Pong response = influxDb.ping();
    if (response.getVersion().equalsIgnoreCase("unknown")) {
      throw new SpRuntimeException("Could not connect to InfluxDb Server: " + connectionSettings.getConnectionUrl());
    }
  }
}
