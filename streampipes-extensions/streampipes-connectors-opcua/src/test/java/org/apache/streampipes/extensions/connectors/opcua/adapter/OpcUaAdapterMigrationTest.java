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

package org.apache.streampipes.extensions.connectors.opcua.adapter;

import org.apache.streampipes.extensions.connectors.opcua.adapter.config.OpcUaAdapterVersionedConfig;
import org.apache.streampipes.extensions.connectors.opcua.migration.OpcUaAdapterMigrationV2;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OpcUaAdapterMigrationTest {

  private OpcUaAdapterMigrationV2 migrationV2;

  @Before
  public void setUp(){
    migrationV2 = new OpcUaAdapterMigrationV2();
  }

  @Test
  public void testMigrationV2(){
    var v1 = OpcUaAdapterVersionedConfig.getOpcUaAdapterDescriptionV1();
    var v2 = OpcUaAdapterVersionedConfig.getOpcUaAdapterDescriptionV2();

    var migrationResult = migrationV2.migrate(v1, null);

    assertTrue(migrationResult.success());
    assertCollectionsEqual(v2.getConfig(), migrationResult.element().getConfig());
  }

  private <T> void assertCollectionsEqual(List<T> list1, List<T> list2) {
    assertEquals(list1.size(), list2.size());
    assertTrue(list1.containsAll(list2));
    assertTrue(list2.containsAll(list1));
  }

}