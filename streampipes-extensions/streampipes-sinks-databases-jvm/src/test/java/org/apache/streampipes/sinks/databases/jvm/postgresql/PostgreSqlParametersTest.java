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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the implementation of the {@link PostgreSqlParameters} class.
 */
class PostgreSqlParametersTest {

  private static final String HOST = "localhost";
  private static final String DB_NAME = "streampipes";
  private static final String TABLE_NAME = "sensor_data";
  private static final String USER = "user";
  private static final String PASSWORD = "secret";
  private static final int PORT = 5432;
  private static final boolean SSL_ENABLED = false;

  PostgreSqlParameters newParameters(Boolean allowNewTableCreation, Integer batchSize) {
    return new PostgreSqlParameters(
        null, HOST, PORT, DB_NAME, TABLE_NAME, USER, PASSWORD, SSL_ENABLED,
        allowNewTableCreation, batchSize);
  }

  @Test
  void testAllowsNewTableCreation_isMissing_returnsTrue() {
    PostgreSqlParameters param = newParameters(null, 1);
    assertTrue(param.allowsNewTableCreation());
  }

  @Test
  void testAllowsNewTableCreation_isOn_returnsTrue() {
    PostgreSqlParameters param = newParameters(true, 1);
    assertTrue(param.allowsNewTableCreation());
  }

  @Test
  void testAllowsNewTableCreation_isOff_returnsFalse() {
    PostgreSqlParameters param = newParameters(false, 1);
    assertFalse(param.allowsNewTableCreation());
  }

  @Test
  void testGetBatchSize_isMissing_returnsOne() {
    PostgreSqlParameters param = newParameters(true, null);
    assertEquals(1, param.getBatchSize());
  }

  @Test
  void testGetBatchSize_isZero_returnsOne() {
    PostgreSqlParameters param = newParameters(true, 0);
    assertEquals(1, param.getBatchSize());
  }

  @Test
  void testGetBatchSize_isNegative_returnsOne() {
    PostgreSqlParameters param = newParameters(true, -5);
    assertEquals(1, param.getBatchSize());
  }

  @Test
  void testGetBatchSize_isPositive_returnsPositive() {
    PostgreSqlParameters param = newParameters(true, 5);
    assertEquals(5, param.getBatchSize());
  }
}
