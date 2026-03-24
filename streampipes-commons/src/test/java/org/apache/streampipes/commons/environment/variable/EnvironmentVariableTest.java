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

package org.apache.streampipes.commons.environment.variable;

import org.apache.streampipes.commons.constants.Envs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvironmentVariableTest {

  @Test
  void getValueOrResolveUsesExistingValue() {
    var variable = new TestEnvironmentVariable(true, "configured-value");

    var result = variable.getValueOrResolve(() -> "resolved-value");

    assertEquals("configured-value", result);
  }

  @Test
  void getValueOrResolveUsesResolverWhenValueMissing() {
    var variable = new TestEnvironmentVariable(false, "configured-value");

    var result = variable.getValueOrResolve(() -> "resolved-value");

    assertEquals("resolved-value", result);
  }

  private static final class TestEnvironmentVariable extends EnvironmentVariable<String> {

    private final boolean exists;
    private final String value;

    private TestEnvironmentVariable(boolean exists, String value) {
      super(Envs.SP_NATS_HOST);
      this.exists = exists;
      this.value = value;
    }

    @Override
    public String getValue() {
      return value;
    }

    @Override
    public boolean exists() {
      return exists;
    }

    @Override
    public String parse(String value) {
      return value;
    }
  }
}
