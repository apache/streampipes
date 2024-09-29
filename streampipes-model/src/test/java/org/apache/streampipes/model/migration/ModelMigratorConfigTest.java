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
package org.apache.streampipes.model.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;

import org.junit.jupiter.api.Test;

public class ModelMigratorConfigTest {

  static ModelMigratorConfig config = new ModelMigratorConfig("app-id", SpServiceTagPrefix.ADAPTER, 0, 1);

  @Test
  public void compareToWithNullPointer() {
    assertThrows(NullPointerException.class, () -> config.compareTo(null));
  }

  @Test
  public void compareToWithClassCastException() {
    assertThrows(ClassCastException.class, () -> config.compareTo(5));
  }

  @Test
  public void compareTo() {
    assertEquals(0, config.compareTo(new ModelMigratorConfig("other-app-id", SpServiceTagPrefix.ADAPTER, 0, 1)));
    assertEquals(0, config.compareTo(new ModelMigratorConfig("app-id", SpServiceTagPrefix.ADAPTER, 0, 1)));
    assertEquals(-1, config.compareTo(new ModelMigratorConfig("app-id", SpServiceTagPrefix.ADAPTER, 1, 2)));
    assertEquals(0, config.compareTo(new ModelMigratorConfig("app-id", SpServiceTagPrefix.ADAPTER, 0, 3)));
  }
}
