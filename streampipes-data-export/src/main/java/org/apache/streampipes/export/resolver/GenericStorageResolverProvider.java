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

package org.apache.streampipes.export.resolver;

import org.apache.streampipes.commons.exceptions.ElementNotFoundException;
import org.apache.streampipes.export.GenericStorageResourceRegistry;
import org.apache.streampipes.export.ResourceRegistration;
import org.apache.streampipes.model.assets.AssetLink;
import org.apache.streampipes.model.export.ExportItem;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.system.IGenericStorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** Selects resource resolvers and bridges generic storage documents to their domain types. */
public class GenericStorageResolverProvider {

  private static final Logger LOG = LoggerFactory.getLogger(GenericStorageResolverProvider.class);

  private final GenericStorageResourceRegistry registry;
  private final IGenericStorage storage;
  private final GenericStorageDocumentResolver fallback;
  private final ObjectMapper mapper = JacksonSerializer.getObjectMapper();

  public GenericStorageResolverProvider(IGenericStorage storage) {
    this(storage, GenericStorageResourceRegistry.INSTANCE);
  }

  public GenericStorageResolverProvider(IGenericStorage storage, GenericStorageResourceRegistry registry) {
    this.storage = storage;
    this.registry = registry;
    this.fallback = new GenericStorageDocumentResolver(storage);
  }

  public Optional<ResourceRegistration<?>> registrationFor(Map<String, Object> document) {
    return document.get("appDocType") instanceof String type ? registry.find(type) : Optional.empty();
  }

  public AbstractResolver<?> resolverFor(Map<String, Object> document) {
    return registrationFor(document).<AbstractResolver<?>>map(entry -> entry.createResolver(storage)).orElse(fallback);
  }

  public ExportItem toExportItem(Map<String, Object> document, boolean selected) throws JsonProcessingException {
    var item = convert(resolverFor(document), mapper.writeValueAsString(document));
    item.setSelected(selected);
    registrationFor(document).ifPresent(entry -> item.setCategory(entry.category()));
    return item;
  }

  public Set<ExportItem> resolve(Set<AssetLink> links) {
    var items = new HashSet<ExportItem>();
    var resourceIds = links.stream().map(AssetLink::getResourceId).filter(Objects::nonNull).distinct().toList();
    for (var resourceId : resourceIds) {
      var document = fallback.findDocument(resourceId);
      if (document != null && registrationFor(document).filter(entry -> entry.category() != null).isPresent()) {
        try {
          items.add(toExportItem(document, true));
        } catch (JsonProcessingException e) {
          LOG.warn("Could not resolve linked generic storage document {}", resourceId, e);
        }
      }
    }
    return items;
  }

  public String getSerializedDocument(String resourceId) throws JsonProcessingException, ElementNotFoundException {
    var document = fallback.findDocument(resourceId);
    if (document == null) {
      throw new ElementNotFoundException("Could not find element with resource id " + resourceId);
    }
    return serializeForExport(resolverFor(document), mapper.writeValueAsString(document));
  }

  private <T> ExportItem convert(AbstractResolver<T> resolver, String document) throws JsonProcessingException {
    return resolver.convert(resolver.readDocument(document));
  }

  private <T> String serializeForExport(AbstractResolver<T> resolver, String document) throws JsonProcessingException {
    return resolver.serializeDocument(resolver.modifyDocumentForExport(resolver.readDocument(document)));
  }
}
