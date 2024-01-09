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

package org.apache.streampipes.extensions.connectors.plc.adapter.migration;

import org.apache.streampipes.extensions.connectors.plc.adapter.migration.config.Plc4xModbusAdapterVersionedConfig;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Plc4xModbusAdapterMigrationTest {

  private Plc4xModbusAdapterMigrationV1 migrationV1;

  @Before
  public void setUp() {
    migrationV1 = new Plc4xModbusAdapterMigrationV1();
  }

  @Test
  public void testMigrationV1(){
    var v0 = Plc4xModbusAdapterVersionedConfig.getPlc4xModbusAdapterDescriptionV0();
    var v1 = Plc4xModbusAdapterVersionedConfig.getPlc4xModbusAdapterDescriptionV1();

    var migrationResult = migrationV1.migrate(v0, null);

    assertTrue(migrationResult.success());
    assertCollectionsEqual(v1.getConfig(), migrationResult.element().getConfig());
  }

  private <T> void assertCollectionsEqual(List<T> list1, List<T> list2) {
    assertEquals(list1.size(), list2.size());
    assertTrue(list1.containsAll(list2));
    assertTrue(list2.containsAll(list1));
  }
}