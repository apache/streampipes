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

package org.apache.streampipes.sinks.internal.jvm.datalake;

public class InfluxDbConnectionSettings {

  private final Integer influxDbPort;
  private final String influxDbHost;
  private final String databaseName;
  private final String measureName;
  private final String user;
  private final String password;

  public static InfluxDbConnectionSettings from(String influxDbHost,
                                                Integer influxDbPort,
                                                String databaseName,
                                                String measureName,
                                                String user,
                                                String password) {
    return new InfluxDbConnectionSettings(
            influxDbHost,
            influxDbPort,
            databaseName,
            measureName,
            user,
            password);
  }

  private InfluxDbConnectionSettings(String influxDbHost,
                                     Integer influxDbPort,
                                     String databaseName,
                                     String measureName,
                                     String user,
                                     String password) {
    this.influxDbHost = influxDbHost;
    this.influxDbPort = influxDbPort;
    this.databaseName = databaseName;
    this.measureName = measureName;
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

  public String getMeasureName() {
    return measureName;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }
}
