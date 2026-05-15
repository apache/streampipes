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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DbDataTypesTest {

  @Test
  public void testFromSqlTypeDoublePrecision() throws SpRuntimeException {
    assertEquals(DbDataTypes.DOUBLE_PRECISION, DbDataTypes.fromSqlType("double precision"));
  }

  @Test
  public void testFromSqlTypeCharacterVarying() throws SpRuntimeException {
    assertEquals(DbDataTypes.VAR_CHAR, DbDataTypes.fromSqlType("character varying"));
  }

  @Test
  public void testFromSqlTypeTimestampWithoutTimeZone() throws SpRuntimeException {
    assertEquals(DbDataTypes.TIMESTAMP, DbDataTypes.fromSqlType("timestamp without time zone"));
  }

  @Test
  public void testFromSqlTypeTimestampWithTimeZone() throws SpRuntimeException {
    assertEquals(DbDataTypes.TIMESTAMP, DbDataTypes.fromSqlType("timestamp with time zone"));
  }

  @Test
  public void testFromSqlTypeTimeWithoutTimeZone() throws SpRuntimeException {
    assertEquals(DbDataTypes.TIME, DbDataTypes.fromSqlType("time without time zone"));
  }

  @Test
  public void testFromSqlTypeBigint() throws SpRuntimeException {
    assertEquals(DbDataTypes.BIGINT, DbDataTypes.fromSqlType("bigint"));
  }

  @Test
  public void testFromSqlTypeInteger() throws SpRuntimeException {
    assertEquals(DbDataTypes.INTEGER, DbDataTypes.fromSqlType("integer"));
  }

  @Test
  public void testFromSqlTypeBoolean() throws SpRuntimeException {
    assertEquals(DbDataTypes.BOOLEAN, DbDataTypes.fromSqlType("boolean"));
  }

  @Test
  public void testFromSqlTypeText() throws SpRuntimeException {
    assertEquals(DbDataTypes.TEXT, DbDataTypes.fromSqlType("text"));
  }

  @Test
  public void testFromSqlTypeReal() throws SpRuntimeException {
    assertEquals(DbDataTypes.REAL, DbDataTypes.fromSqlType("real"));
  }

  @Test
  public void testFromSqlTypeCaseInsensitive() throws SpRuntimeException {
    assertEquals(DbDataTypes.BIGINT, DbDataTypes.fromSqlType("BIGINT"));
  }

  @Test
  public void testFromSqlTypeNull() {
    assertThrows(SpRuntimeException.class, () -> DbDataTypes.fromSqlType(null));
  }

  @Test
  public void testFromSqlTypeUnknown() {
    assertThrows(SpRuntimeException.class, () -> DbDataTypes.fromSqlType("unknown_type"));
  }
}
