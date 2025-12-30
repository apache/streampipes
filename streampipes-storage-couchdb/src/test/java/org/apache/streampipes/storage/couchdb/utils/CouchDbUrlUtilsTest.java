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

package org.apache.streampipes.storage.couchdb.utils;

import org.apache.streampipes.commons.environment.DefaultEnvironment;
import org.apache.streampipes.commons.environment.Environment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CouchDbUrlUtilsTest {

  @Test
  void testEscapePathSegment() {
    assertEquals("my%20db", CouchDbUrlUtils.escapePathSegment("my db"));
    assertEquals("a%2Fb", CouchDbUrlUtils.escapePathSegment("a/b"));
    assertEquals("simple", CouchDbUrlUtils.escapePathSegment("simple"));
  }

  @Test
  void testBuildDatabaseRouteWithDefaultEnvironment() {
    Environment env = new DefaultEnvironment();

    String route = CouchDbUrlUtils.buildDatabaseRoute(env, "testdb");

    // Verify the route is constructed correctly using the default environment values
    assertNotNull(route);
    // The route should end with the database name
    assertEquals(
        env.getCouchDbProtocol().getValueOrDefault()
            + "://" + env.getCouchDbHost().getValueOrDefault()
            + ":" + env.getCouchDbPort().getValueOrDefault()
            + "/testdb",
        route
    );
  }

  @Test
  void testBuildDatabaseRouteFormat() {
    Environment env = new DefaultEnvironment();

    String route = CouchDbUrlUtils.buildDatabaseRoute(env, "my-database");

    // Verify the route contains expected URL components
    assertNotNull(route);
    assertTrue(route.contains("://"));
    assertTrue(route.endsWith("/my-database"));
  }
}
