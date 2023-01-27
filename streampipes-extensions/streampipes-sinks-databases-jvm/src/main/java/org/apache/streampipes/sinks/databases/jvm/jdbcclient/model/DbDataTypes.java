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


public enum DbDataTypes {

  BOOLEAN("BOOLEAN"),
  TEXT("TEXT"),
  VAR_CHAR("VARCHAR255"),
  DOUBLE_PRECISION("DOUBLE PRECISION"),
  FLOAT("FLOAT"),
  REAL("REAL"),
  BIGINT("BIGINT"),
  TINYINT("TINYINT"),
  INT("INT"),
  INTEGER("INTEGER"),
  TIMESTAMP("TIMESTAMP"),
  DATE("DATE"),
  TIME("TIME"),
  DATETIME("DATETIME");

  private String sqlTerm;


  DbDataTypes(String sqlTerm) {
    this.sqlTerm = sqlTerm;
  }

  @Override
  public String toString() {
    return sqlTerm;
  }
}
