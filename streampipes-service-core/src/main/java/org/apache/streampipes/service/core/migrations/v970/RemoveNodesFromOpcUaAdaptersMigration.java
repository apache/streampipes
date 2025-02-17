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
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class RemoveNodesFromOpcUaAdaptersMigration implements Migration {

  private static final Logger LOG = LoggerFactory.getLogger(RemoveNodesFromOpcUaAdaptersMigration.class);

  private final IAdapterStorage adapterStorage;

  public RemoveNodesFromOpcUaAdaptersMigration() {
    adapterStorage = StorageDispatcher.INSTANCE.getNoSqlStore()
                                               .getAdapterInstanceStorage();
  }

  public RemoveNodesFromOpcUaAdaptersMigration(IAdapterStorage adapterStorage) {
    this.adapterStorage = adapterStorage;
  }

  @Override
  public boolean shouldExecute() {
    return true;
  }

  @Override
  public String getDescription() {
    return "Remove latestFetchedNodes and nodes from OPC UA adapter instances and updates the adapters in CouchDB.";
  }

  @Override
  public void executeMigration() throws IOException {
    var adapters = adapterStorage.findAll();

    var opcUaAdapters = filterForOpcUaAdapters(adapters);

    opcUaAdapters.forEach(adapter -> {
      removeTreeNodesFromConfigurationAndUpdateAdapter(adapter);
      LOG.info("The nodes and latest fetched nodes of the adapter {} have been removed.", adapter.getElementId());
    });

  }

  private void removeTreeNodesFromConfigurationAndUpdateAdapter(AdapterDescription adapter) {
    removeTreeNodesFromConfiguration(adapter);
    adapterStorage.updateElement(adapter);
  }

  /**
   * This method sets the nodes and latest fetched nodes of the adapter configuration to an empty list.
   */
  private static void removeTreeNodesFromConfiguration(AdapterDescription adapter) {
    adapter.getConfig()
           .stream()
           .filter(RuntimeResolvableTreeInputStaticProperty.class::isInstance)
           .map(RuntimeResolvableTreeInputStaticProperty.class::cast)
           .forEach(treeInputProperty -> {
             treeInputProperty.setNodes(Collections.emptyList());
             treeInputProperty.setLatestFetchedNodes(Collections.emptyList());
           });
  }

  private static List<AdapterDescription> filterForOpcUaAdapters(List<AdapterDescription> adapters) {
    return adapters.stream()
                   .filter(adapter -> adapter.getAppId()
                                             .equals("org.apache.streampipes.connect.iiot.adapters.opcua"))
                   .toList();
  }

}
