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

package org.apache.streampipes.manager.endpoint;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.ExtensionItemDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceStatus;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.storage.api.INoSqlStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AvailableExtensionsProvider {

  private final INoSqlStorage storage;

  public AvailableExtensionsProvider(INoSqlStorage storage) {
    this.storage = storage;
  }

  public List<ExtensionItemDescription> getExtensions() {
    var installedExtensions = getAllInstalledExtensions();
    var availableExtensions = getAvailableExtensions().stream()
        .collect(Collectors.toMap(
            ExtensionItemDescription::getElementId,
            Function.identity(),
            (existing, replacement) -> existing)
        );

    var processedInstalledExtensions = installedExtensions
        .stream()
        .peek(extension -> {
          if (!availableExtensions.containsKey(extension.getElementId())) {
            extension.setAvailable(false);
          }
        })
        .toList();

    var allExtensions = new ArrayList<>(processedInstalledExtensions);
    availableExtensions.values()
        .stream()
        .filter(extension -> processedInstalledExtensions
            .stream()
            .noneMatch(e -> extension.getElementId().equals(e.getElementId())))
        .forEach(allExtensions::add);

    return allExtensions;
  }

  private List<ExtensionItemDescription> getAvailableExtensions() {
    return storage.getExtensionsServiceStorage().getAll()
        .stream()
        .filter(service -> service.getStatus() == SpServiceStatus.HEALTHY)
        .flatMap(service -> service.getProvidedExtensions().stream())
        .toList();
  }

  private List<ExtensionItemDescription> getAllInstalledExtensions() {
    List<NamedStreamPipesEntity> elements = new ArrayList<>();
    elements.addAll(getAllAdapters());
    elements.addAll(getAllDataStreams());
    elements.addAll(getAllDataProcessors());
    elements.addAll(getAllDataSinks());
    return elements.stream().map(e -> e.toExtensionDescription(true, !e.isInternallyManaged(), true)).toList();
  }

  private List<AdapterDescription> getAllAdapters() {
    return storage.getAdapterDescriptionStorage().getAllAdapters();
  }

  private List<SpDataStream> getAllDataStreams() {
    return storage.getDataStreamStorage().getAll()
        .stream()
        .filter(stream -> !stream.isInternallyManaged())
        .toList();
  }

  private List<DataProcessorDescription> getAllDataProcessors() {
    return storage.getDataProcessorStorage().getAll();
  }

  private List<DataSinkDescription> getAllDataSinks() {
    return storage.getDataSinkStorage().getAll();
  }

}
