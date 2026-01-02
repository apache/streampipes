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

package org.apache.streampipes.service.core.migrations.v099;

import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription;
import org.apache.streampipes.storage.api.IAdapterStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MigrateAdaptersToUseScriptTest {

  private IAdapterStorage mockStorage;
  private MigrateAdaptersToUseScript migration;

  @BeforeEach
  void setUp() {
    mockStorage = mock(IAdapterStorage.class);
    migration = new MigrateAdaptersToUseScript(mockStorage);
  }

  @Test
  void shouldExecute_ReturnsTrue_WhenAdapterHasRules() {
    // Arrange
    AdapterDescription adapter = new AdapterDescription();
    adapter.setRules(Collections.singletonList(new RenameRuleDescription("old", "new")));

    when(mockStorage.findAll()).thenReturn(Collections.singletonList(adapter));

    boolean result = migration.shouldExecute();

    assertTrue(result);
  }

  @Test
  void executeMigration_TransformsRenameRuleToScript() throws IOException {
    // Arrange
    AdapterDescription adapter = new AdapterDescription();
    RenameRuleDescription rename = new RenameRuleDescription("old", "new");
    adapter.setRules(Collections.singletonList(rename));

    when(mockStorage.findAll()).thenReturn(List.of(adapter));

    // Act
    migration.executeMigration();

    // Assert
    assertNotNull(adapter.getTransformationConfig());
    String script = adapter.getTransformationConfig().getScript();
    assertTrue(script.contains("event['new'] = event['old'];"));
    assertTrue(adapter.getRules().isEmpty()); // Ensure old rules are cleared

    // Verify the storage was actually updated
    verify(mockStorage).updateElement(adapter);
  }
}