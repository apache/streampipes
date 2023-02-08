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

package org.apache.streampipes.dataexplorer.commons.influx;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

public class InfluxClientProvider {

  /**
   * Create a new InfluxDB client from environment variables
   *
   * @return InfluxDB
   */
  public static InfluxDB getInfluxDBClient() {
    var env = getEnvironment();
    return getInfluxDBClient(InfluxConnectionSettings.from(env));
  }

  /**
   * Create a new InfluxDB client from provided settings
   *
   * @param settings Connection settings
   * @return InfluxDB
   */
  public static InfluxDB getInfluxDBClient(InfluxConnectionSettings settings) {
    if (settings.getAuthMode() == InfluxAuthMode.TOKEN) {
      var okHttpClientBuilder = InfluxClientUtils.getHttpClientBuilder(settings.getToken());

      return InfluxDBFactory.connect(settings.getConnectionUrl(), okHttpClientBuilder);
    } else {
      var okHttpClientBuilder = InfluxClientUtils.getHttpClientBuilder();
      return InfluxDBFactory.connect(
          settings.getConnectionUrl(),
          settings.getUsername(),
          settings.getPassword(),
          okHttpClientBuilder
      );
    }
  }

  private static Environment getEnvironment() {
    return Environments.getEnvironment();
  }
}
