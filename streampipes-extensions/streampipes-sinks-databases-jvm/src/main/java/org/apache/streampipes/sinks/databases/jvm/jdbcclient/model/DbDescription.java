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

public class DbDescription {

  private final JdbcConnectionParameters connectionParameters;
  protected final SupportedDbEngines dbEngine;

  public DbDescription(JdbcConnectionParameters connectionParameters,
                       SupportedDbEngines dbEngine) {
    this.connectionParameters = connectionParameters;
    this.dbEngine = dbEngine;
  }


  public String getAllowedRegEx() {
    return dbEngine.getAllowedRegex();
  }

  public JdbcConnectionParameters getConnectionParameters() {
    return connectionParameters;
  }

  public SupportedDbEngines getEngine() {
    return dbEngine;
  }

  public String getDriverName() {
    return dbEngine.getDriverName();
  }

  public String getHost() {
    return connectionParameters.getDbHost();
  }

  public int getPort() {
    return connectionParameters.getDbPort();
  }

  public String getName() {
    return connectionParameters.getDbName();
  }

  public String getUsername() {
    return connectionParameters.getUsername();
  }

  public String getPassword() {
    return connectionParameters.getPassword();
  }

  public String getSslFactory() {
    return connectionParameters.getSslFactory();
  }

  public boolean isColumnNameQuoted() {
    return connectionParameters.isColumnNameQuoted();
  }

  public boolean isSslEnabled() {
    return connectionParameters.isSslEnabled();
  }
}
