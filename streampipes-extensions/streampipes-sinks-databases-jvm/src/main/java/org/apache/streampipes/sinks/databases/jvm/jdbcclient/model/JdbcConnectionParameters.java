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

import org.apache.streampipes.model.graph.DataSinkInvocation;

public class JdbcConnectionParameters {

  private DataSinkInvocation graph;

  private String dbHost;
  private Integer dbPort;
  private String dbName;
  private String username;
  private String password;
  private String dbTable;
  private boolean sslEnabled;
  private String sslFactory;
  private boolean columnNameQuoted;

  public JdbcConnectionParameters(DataSinkInvocation graph,
                                  String dbHost,
                                  Integer dbPort,
                                  String dbName,
                                  String username,
                                  String password,
                                  String dbTable,
                                  boolean sslEnabled,
                                  String sslFactory,
                                  boolean quotedColumnNames) {
    this.graph = graph;
    this.dbHost = dbHost;
    this.dbPort = dbPort;
    this.dbName = dbName;
    this.username = username;
    this.password = password;
    this.dbTable = dbTable;
    this.sslEnabled = sslEnabled;
    this.sslFactory = sslFactory;
    this.columnNameQuoted = quotedColumnNames;
  }


  public String getDbHost() {
    return dbHost;
  }

  public Integer getDbPort() {
    return dbPort;
  }

  public String getDbName() {
    return dbName;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getDbTable() {
    return dbTable;
  }

  public boolean isSslEnabled() {
    return sslEnabled;
  }

  public String getSslFactory() {
    return sslFactory;
  }

  public boolean isColumnNameQuoted() {
    return columnNameQuoted;
  }

  public DataSinkInvocation getGraph() {
    return graph;
  }
}
