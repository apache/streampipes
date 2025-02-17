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

package org.apache.streampipes.manager.extensions;

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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Provides available extensions by taking into account service availability and installed elements
 * Extensions are filtered by their elementId so that no duplicates are returned.
 */
public class AvailableExtensionsProvider {

  private final INoSqlStorage storage;

  public AvailableExtensionsProvider(INoSqlStorage storage) {
    this.storage = storage;
  }

  public List<ExtensionItemDescription> getExtensionItemDescriptions() {
    var availableExtensions = getUniqueAvailableExtensions();
    var installedExtensions = getAllInstalledExtensions(availableExtensions);
    var allExtensions = new ArrayList<>(installedExtensions);

    availableExtensions.values()
        .stream()
        .filter(extension -> installedExtensions
            .stream()
            .noneMatch(e -> extension.getElementId().equals(e.getElementId())))
        .forEach(allExtensions::add);

    return allExtensions;
  }

  /**
   * Fetched available extensions from all registered extension services.
   *
   * @return Map of extension item descriptions keyed by their elementId
   */
  private Map<String, ExtensionItemDescription> getUniqueAvailableExtensions() {
    return storage.getExtensionsServiceStorage().findAll()
        .stream()
        .filter(service -> service.getStatus() == SpServiceStatus.HEALTHY)
        .flatMap(service -> service.getProvidedExtensions().stream())
        .collect(Collectors.toMap(
            ExtensionItemDescription::getElementId,
            Function.identity(),
            (existing, replacement) -> existing)
        );
  }

  /**
   * Fetches all already installed extensions and determines if the installed extension
   * is also currently available (.i.e., an extension service supporting this extension is registered)
   *
   * @param availableExtensions Map of unique available extensions
   * @return list of installed extensions
   */
  private List<ExtensionItemDescription> getAllInstalledExtensions(
      Map<String, ExtensionItemDescription> availableExtensions
  ) {
    List<NamedStreamPipesEntity> elements = new ArrayList<>();
    elements.addAll(getAllAdapters());
    elements.addAll(getAllDataStreams());
    elements.addAll(getAllDataProcessors());
    elements.addAll(getAllDataSinks());
    return elements
        .stream()
        .map(e -> e.toExtensionDescription(
            true,
            !e.isInternallyManaged(),
            isExtensionAvailable(availableExtensions, e.getElementId())))
        .toList();
  }

  private boolean isExtensionAvailable(Map<String, ExtensionItemDescription> availableExtensions,
                                       String elementId) {
    return availableExtensions.containsKey(elementId);
  }

  private List<AdapterDescription> getAllAdapters() {
    return storage.getAdapterDescriptionStorage().findAll();
  }

  private List<SpDataStream> getAllDataStreams() {
    return storage.getDataStreamStorage().findAll()
        .stream()
        .filter(stream -> !stream.isInternallyManaged())
        .toList();
  }

  private List<DataProcessorDescription> getAllDataProcessors() {
    return storage.getDataProcessorStorage().findAll();
  }

  private List<DataSinkDescription> getAllDataSinks() {
    return storage.getDataSinkStorage().findAll();
  }

}
