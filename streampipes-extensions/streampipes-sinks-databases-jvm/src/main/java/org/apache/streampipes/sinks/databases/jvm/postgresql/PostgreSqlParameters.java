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

package org.apache.streampipes.sinks.databases.jvm.postgresql;

import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.JdbcConnectionParameters;

public class PostgreSqlParameters extends JdbcConnectionParameters {

  private final boolean appendToExisting;
  private final int batchSize;

  public PostgreSqlParameters(DataSinkInvocation graph, String postgreSqlHost, Integer postgreSqlPort,
                              String databaseName, String tableName, String user, String password, Boolean sslEnabled,
                              Boolean appendToExisting, Integer batchSize) {
    super(
        graph,
        postgreSqlHost,
        postgreSqlPort,
        databaseName,
        user,
        password,
        tableName,
        sslEnabled,
        "org.postgresql.ssl.NonValidatingFactory",
        true);

    this.appendToExisting = appendToExisting != null && appendToExisting;
    this.batchSize = (batchSize == null || batchSize < 1) ? 1 : batchSize;
  }

  public boolean isAppendToExisting() {
    return appendToExisting;
  }

  public int getBatchSize() {
    return batchSize;
  }
}
