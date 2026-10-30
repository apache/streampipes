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

package org.apache.streampipes.export;

import org.apache.streampipes.commons.exceptions.ElementNotFoundException;
import org.apache.streampipes.export.resolver.GenericStorageDocumentResolver;
import org.apache.streampipes.export.resolver.GenericStorageResolverProvider;
import org.apache.streampipes.storage.api.system.IGenericStorage;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GenericStorageResourceRegistryTest {

  @Test
  void shouldRequireResolverFactoriesForResourceRegistrations() {
    assertThrows(NullPointerException.class,
        () -> new ResourceRegistration<>(new PermissionDefaults(getClass(), false), "Resources", null));
  }

  @Test
  void shouldRejectConflictingRegistrationsAndAllowRepeatedRegistration() {
    var registry = new GenericStorageResourceRegistry();
    var registration = new ResourceRegistration<>(new PermissionDefaults(getClass(), false),
        "Resources", GenericStorageDocumentResolver::new);
    registry.register("test:resource", registration);
    registry.register("test:resource", registration);

    assertSame(registration, registry.find("test:resource").orElseThrow());
    assertThrows(IllegalArgumentException.class, () -> registry.register("test:resource",
        new ResourceRegistration<>(new PermissionDefaults(getClass(), true),
            "Resources", GenericStorageDocumentResolver::new)));
  }

  @Test
  void shouldExportWithRegisteredSerializationWithoutReloadingDocument() throws Exception {
    var storage = mock(IGenericStorage.class);
    var registry = new GenericStorageResourceRegistry();
    registry.register("test:resource", new ResourceRegistration<>(new PermissionDefaults(getClass(), false),
        "Resources", CustomSerializationResolver::new));
    when(storage.findOne("resource-1")).thenReturn(Map.of(
        "_id", "resource-1", "_rev", "1-revision", "appDocType", "test:resource"));

    var exported = new GenericStorageResolverProvider(storage, registry).getSerializedDocument("resource-1");

    assertEquals("custom serialization", exported);
    verify(storage, times(1)).findOne("resource-1");
  }

  @Test
  void shouldPreserveGenericFallbackForUnregisteredTypes() throws Exception {
    var storage = mock(IGenericStorage.class);
    when(storage.findOne("unknown-1")).thenReturn(Map.of(
        "_id", "unknown-1", "_rev", "1-revision", "appDocType", "unknown"));
    var provider = new GenericStorageResolverProvider(storage, new GenericStorageResourceRegistry());

    assertFalse(provider.getSerializedDocument("unknown-1").contains("_rev"));
    assertEquals("unknown-1", provider.toExportItem(Map.of("_id", "unknown-1", "name", "Other"), true).getLabel());
  }

  @Test
  void shouldReportMissingDocuments() throws IOException {
    var storage = mock(IGenericStorage.class);
    when(storage.findOne("missing")).thenThrow(new IOException("not found"));
    var provider = new GenericStorageResolverProvider(storage, new GenericStorageResourceRegistry());

    assertThrows(ElementNotFoundException.class, () -> provider.getSerializedDocument("missing"));
  }

  private static class CustomSerializationResolver extends GenericStorageDocumentResolver {

    CustomSerializationResolver(IGenericStorage storage) {
      super(storage);
    }

    @Override
    public String serializeDocument(Map<String, Object> document) {
      assertFalse(document.containsKey("_rev"));
      return "custom serialization";
    }
  }
}
