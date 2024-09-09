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

package org.apache.streampipes.service.core.migrations.v970;

import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.TreeInputNode;
import org.apache.streampipes.storage.api.IAdapterStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class RemoveNodesFromOpcUaAdaptersMigrationTest {

  private RemoveNodesFromOpcUaAdaptersMigration migration;
  private IAdapterStorage adapterStorage;

  @BeforeEach
  void setUp() {
    adapterStorage = mock(IAdapterStorage.class);
    migration = new RemoveNodesFromOpcUaAdaptersMigration(adapterStorage);
  }

  @Test
  void executeMigration_RemoveConfigsFromOpcUaAdapter() throws IOException {
    var opcUaAdapter = prepareOpcUaAdapterWithNodesAndLatestFetchedNodes();
    when(adapterStorage.findAll()).thenReturn(List.of(opcUaAdapter));

    migration.executeMigration();

    // Ensure that the nodes and latestFetchedNodes are removed from the adapter description
    var captor = ArgumentCaptor.forClass(AdapterDescription.class);
    verify(adapterStorage).updateElement(captor.capture());
    var updatedAdapter = captor.getValue();
    var treeProperty = getFirstTreeInputProperty(updatedAdapter);
    assertTrue(treeProperty.getNodes().isEmpty());
    assertTrue(treeProperty.getLatestFetchedNodes()
                           .isEmpty());
  }

  private RuntimeResolvableTreeInputStaticProperty getFirstTreeInputProperty(AdapterDescription adapterDescription) {
    return adapterDescription.getConfig()
                             .stream()
                             .filter(config -> config instanceof RuntimeResolvableTreeInputStaticProperty)
                             .map(config -> (RuntimeResolvableTreeInputStaticProperty) config)
                             .findFirst()
                             .orElse(null);
  }

  private AdapterDescription prepareOpcUaAdapterWithNodesAndLatestFetchedNodes() {
    var opcUaAdapter = new AdapterDescription();
    opcUaAdapter.setAppId("org.apache.streampipes.connect.iiot.adapters.opcua");
    var property = new RuntimeResolvableTreeInputStaticProperty();
    property.setNodes(List.of(new TreeInputNode()));
    property.setLatestFetchedNodes(List.of(new TreeInputNode()));
    opcUaAdapter.setConfig(List.of(property));
    return opcUaAdapter;
  }
}