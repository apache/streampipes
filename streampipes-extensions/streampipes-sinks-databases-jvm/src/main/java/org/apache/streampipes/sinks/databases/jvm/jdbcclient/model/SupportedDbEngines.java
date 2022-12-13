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

package org.apache.streampipes.sinks.databases.jvm.jdbcclient.model;

public enum SupportedDbEngines {
  MY_SQL("mysql", "com.mysql.cj.jdbc.Driver", ".*"),
  IOT_DB("iotdb", "org.apache.iotdb.jdbc.IoTDBDriver", ".*"),
  POSTGRESQL("postgresql", "org.postgresql.Driver", "^[a-zA-Z_][a-zA-Z0-9_]*$");

  private final String urlName;
  private final String driverName;
  private final String allowedRegex;

  SupportedDbEngines(String urlName, String driverName, String allowedRegex) {
    this.urlName = urlName;
    this.driverName = driverName;
    this.allowedRegex = allowedRegex;
  }

  public String getUrlName() {
    return urlName;
  }

  public String getDriverName() {
    return driverName;
  }

  public String getAllowedRegex() {
    return allowedRegex;
  }
}
