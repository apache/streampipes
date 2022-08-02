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

import org.apache.streampipes.dataexplorer.commons.configs.DataExplorerEnvKeys;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.svcdiscovery.api.SpConfig;

public class InfluxConnectionSettings {

  private final Integer influxDbPort;
  private final String influxDbHost;
  private final String databaseName;
  private final String user;
  private final String password;

  public static InfluxConnectionSettings from(SpConfig configStore) {

    return new InfluxConnectionSettings(
            configStore.getString(DataExplorerEnvKeys.DATA_LAKE_PROTOCOL) + "://" + configStore.getString(DataExplorerEnvKeys.DATA_LAKE_HOST),
            configStore.getInteger(DataExplorerEnvKeys.DATA_LAKE_PORT),
            configStore.getString(DataExplorerEnvKeys.DATA_LAKE_DATABASE_NAME),
            configStore.getString(DataExplorerEnvKeys.DATA_LAKE_USERNAME),
            configStore.getString(DataExplorerEnvKeys.DATA_LAKE_PASSWORD));
  }


  private InfluxConnectionSettings(String influxDbHost,
                                   Integer influxDbPort,
                                   String databaseName,
                                   String user,
                                   String password) {
    this.influxDbHost = influxDbHost;
    this.influxDbPort = influxDbPort;
    this.databaseName = databaseName;
    this.user = user;
    this.password = password;
  }

  public Integer getInfluxDbPort() {
    return influxDbPort;
  }

  public String getInfluxDbHost() {
    return influxDbHost;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }
}
