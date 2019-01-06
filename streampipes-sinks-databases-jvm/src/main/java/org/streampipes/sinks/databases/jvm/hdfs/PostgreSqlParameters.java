/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.streampipes.sinks.databases.jvm.hdfs;

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class PostgreSqlParameters extends EventSinkBindingParams {

  private String PostgreSqlHost;
  private Integer PostgreSqlPort;
  private String databaseName;
  private String tableName;
  private String user;
  private String password;

  public PostgreSqlParameters(DataSinkInvocation graph, String PostgreSqlHost, Integer PostgreSqlPort, String databaseName, String tableName, String user, String password) {
    super(graph);
    this.PostgreSqlHost = PostgreSqlHost;
    this.PostgreSqlPort = PostgreSqlPort;
    this.databaseName = databaseName;
    this.tableName = tableName;
    this.user = user;
    this.password = password;
  }

  public String getPostgreSqlHost() {
    return PostgreSqlHost;
  }

  public Integer getPostgreSqlPort() {
    return PostgreSqlPort;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public String getTableName() {
    return tableName;
  }

  public String getUsername() {
    return user;
  }

  public String getPassword() {
    return password;
  }
}
