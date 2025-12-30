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

import org.apache.streampipes.commons.constants.Envs;
import org.apache.streampipes.commons.environment.DefaultEnvironment;
import org.apache.streampipes.commons.environment.variable.StringEnvironmentVariable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CouchDbAuthUtilsTest {

  @Test
  void testGetBasicAuthHeaderValue() {
    TestEnvironment environment = new TestEnvironment("admin", "secret");

    CouchDbAuthUtils authUtils = new CouchDbAuthUtils(environment);

    String encodedAuth = authUtils.getBasicAuthHeaderValue();

    assertEquals("YWRtaW46c2VjcmV0", encodedAuth);
  }

  private static class TestEnvironment extends DefaultEnvironment {
    private final String username;
    private final String password;

    TestEnvironment(String username, String password) {
      this.username = username;
      this.password = password;
    }

    @Override
    public StringEnvironmentVariable getCouchDbUsername() {
      return new StringEnvironmentVariable(Envs.SP_COUCHDB_USER) {
        @Override
        public String getValueOrDefault() {
          return username;
        }
      };
    }

    @Override
    public StringEnvironmentVariable getCouchDbPassword() {
      return new StringEnvironmentVariable(Envs.SP_COUCHDB_PASSWORD) {
        @Override
        public String getValueOrDefault() {
          return password;
        }
      };
    }
  }
}