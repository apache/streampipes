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
import org.apache.streampipes.commons.environment.variable.IntEnvironmentVariable;
import org.apache.streampipes.commons.environment.variable.StringEnvironmentVariable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CouchDbClientFactoryTest {

  private static class TestEnvironment extends DefaultEnvironment {

    @Override
    public StringEnvironmentVariable getCouchDbProtocol() {
      return new StringEnvironmentVariable(Envs.SP_COUCHDB_PROTOCOL) {
        @Override
        public String getValueOrDefault() {
          return "http";
        }
      };
    }

    @Override
    public StringEnvironmentVariable getCouchDbHost() {
      return new StringEnvironmentVariable(Envs.SP_COUCHDB_HOST) {
        @Override
        public String getValueOrDefault() {
          return "localhost";
        }
      };
    }

    @Override
    public IntEnvironmentVariable getCouchDbPort() {
      return new IntEnvironmentVariable(Envs.SP_COUCHDB_PORT) {
        @Override
        public Integer getValueOrDefault() {
          return 5984;
        }
      };
    }

    @Override
    public StringEnvironmentVariable getCouchDbUsername() {
      return new StringEnvironmentVariable(Envs.SP_COUCHDB_USER) {
        @Override
        public String getValueOrDefault() {
          return "user";
        }
      };
    }

    @Override
    public StringEnvironmentVariable getCouchDbPassword() {
      return new StringEnvironmentVariable(Envs.SP_COUCHDB_PASSWORD) {
        @Override
        public String getValueOrDefault() {
          return "password";
        }
      };
    }
  }

  @Test
  void testFactoryCreation() {
    CouchDbPropertiesFactory propertiesFactory = new CouchDbPropertiesFactory(new TestEnvironment());
    CouchDbClientFactory factory = new CouchDbClientFactory(propertiesFactory);

    assertNotNull(factory);
    assertNotNull(propertiesFactory.create("test-db"));
  }
}
