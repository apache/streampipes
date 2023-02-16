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

public class InfluxConnectionSettings {

  private final Integer influxDbPort;

  private final String influxDbProtocol;
  private final String influxDbHost;
  private final String databaseName;

  private String username;
  private String password;
  private String token;

  private InfluxAuthMode authMode;


  private InfluxConnectionSettings(String influxDbProtocol,
                                   String influxDbHost,
                                   Integer influxDbPort,
                                   String databaseName) {
    this.influxDbProtocol = influxDbProtocol;
    this.influxDbHost = influxDbHost;
    this.influxDbPort = influxDbPort;
    this.databaseName = databaseName;
  }

  private InfluxConnectionSettings(String influxDbProtocol,
                                   String influxDbHost,
                                   Integer influxDbPort,
                                   String databaseName,
                                   String token) {
    this(influxDbProtocol, influxDbHost, influxDbPort, databaseName);
    this.token = token;
    this.authMode = InfluxAuthMode.TOKEN;
  }

  private InfluxConnectionSettings(String influxDbProtocol,
                                   String influxDbHost,
                                   Integer influxDbPort,
                                   String databaseName,
                                   String username,
                                   String password) {
    this(influxDbProtocol, influxDbHost, influxDbPort, databaseName);
    this.username = username;
    this.password = password;
    this.authMode = InfluxAuthMode.USERNAME_PASSWORD;
  }

  public static InfluxConnectionSettings from(String influxDbProtocol,
                                              String influxDbHost,
                                              int influxDbPort,
                                              String databaseName,
                                              String username,
                                              String password) {
    return new InfluxConnectionSettings(influxDbProtocol, influxDbHost, influxDbPort, databaseName, username, password);
  }

  public static InfluxConnectionSettings from(String influxDbProtocol,
                                              String influxDbHost,
                                              int influxDbPort,
                                              String databaseName,
                                              String token) {
    return new InfluxConnectionSettings(influxDbProtocol, influxDbHost, influxDbPort, databaseName, token);
  }

  public static InfluxConnectionSettings from(Environment environment) {

    return new InfluxConnectionSettings(
        environment.getTsStorageProtocol().getValueOrDefault(),
        environment.getTsStorageHost().getValueOrDefault(),
        environment.getTsStoragePort().getValueOrDefault(),
        environment.getTsStorageBucket().getValueOrDefault(),
        environment.getTsStorageToken().getValueOrDefault());
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

  public String getToken() {
    return token;
  }

  public InfluxAuthMode getAuthMode() {
    return authMode;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getConnectionUrl() {
    return  influxDbProtocol + "://" + influxDbHost + ":" + influxDbPort;
  }
}
