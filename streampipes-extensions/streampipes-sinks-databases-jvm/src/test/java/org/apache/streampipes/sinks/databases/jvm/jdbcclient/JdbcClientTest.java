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

package org.apache.streampipes.sinks.databases.jvm.jdbcclient;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JdbcClientTest {

  @Test
  public void testIsSqlStateClassMatchesClassPrefix() {
    assertTrue(TestJdbcClient.isSqlStateClass(new SQLException("missing relation", "42P01"), "42"));
  }

  @Test
  public void testIsSqlStateClassRejectsDifferentClass() {
    assertFalse(TestJdbcClient.isSqlStateClass(new SQLException("constraint violation", "23P01"), "42"));
  }

  @Test
  public void testIsSqlStateClassRejectsNullState() {
    assertFalse(TestJdbcClient.isSqlStateClass(new SQLException("missing state", (String) null), "42"));
  }

  @Test
  public void testIsSqlStateClassRejectsShortState() {
    assertFalse(TestJdbcClient.isSqlStateClass(new SQLException("short state", "4"), "42"));
  }

  private static class TestJdbcClient extends JdbcClient {

  }
}
