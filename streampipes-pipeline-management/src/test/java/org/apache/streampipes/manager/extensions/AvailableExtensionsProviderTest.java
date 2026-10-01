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
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.ExtensionItemDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceStatus;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineElementDescriptionStorage;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AvailableExtensionsProviderTest {

  @Test
  void mergesInstalledDescriptionsWithHealthyServicesWithoutDuplicates() {
    var services = mock(IExtensionsServiceStorage.class);
    var adapters = mock(IAdapterStorage.class);
    var descriptions = mock(IPipelineElementDescriptionStorage.class);
    var adapter = new AdapterDescription();
    adapter.setElementId("adapter");
    var processor = new DataProcessorDescription();
    processor.setElementId("processor");
    var sink = new DataSinkDescription();
    sink.setElementId("sink");
    var stream = new SpDataStream();
    stream.setElementId("stream");
    var managedStream = new SpDataStream();
    managedStream.setElementId("managed");
    managedStream.setInternallyManaged(true);
    when(adapters.findAll()).thenReturn(List.of(adapter));
    when(descriptions.getAllDataProcessors()).thenReturn(List.of(processor));
    when(descriptions.getAllDataSinks()).thenReturn(List.of(sink));
    when(descriptions.getAllDataStreams()).thenReturn(List.of(stream, managedStream));

    var healthy = new SpServiceRegistration();
    healthy.setStatus(SpServiceStatus.HEALTHY);
    healthy.setProvidedExtensions(Set.of(extension("processor"), extension("available")));
    var unavailable = new SpServiceRegistration();
    unavailable.setProvidedExtensions(Set.of(extension("unavailable")));
    var anotherHealthyService = new SpServiceRegistration();
    anotherHealthyService.setStatus(SpServiceStatus.HEALTHY);
    anotherHealthyService.setProvidedExtensions(Set.of(extension("available")));
    when(services.findAll()).thenReturn(List.of(healthy, anotherHealthyService, unavailable));

    var result = new AvailableExtensionsProvider(services, adapters, descriptions).getExtensionItemDescriptions();

    assertEquals(5, result.size());
    assertEquals(Set.of("adapter", "processor", "sink", "stream", "available"),
        result.stream().map(ExtensionItemDescription::getElementId).collect(Collectors.toSet()));
    var installedProcessor = result.stream().filter(e -> e.getElementId().equals("processor")).findFirst().orElseThrow();
    assertTrue(installedProcessor.isInstalled());
    assertTrue(installedProcessor.isAvailable());
    var installedSink = result.stream().filter(e -> e.getElementId().equals("sink")).findFirst().orElseThrow();
    assertTrue(installedSink.isInstalled());
    assertFalse(installedSink.isAvailable());
  }

  private ExtensionItemDescription extension(String id) {
    var extension = new ExtensionItemDescription();
    extension.setElementId(id);
    return extension;
  }
}
