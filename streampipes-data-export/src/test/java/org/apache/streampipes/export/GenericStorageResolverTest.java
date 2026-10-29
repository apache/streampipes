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

import org.apache.streampipes.commons.zip.ZipFileExtractor;
import org.apache.streampipes.export.dataimport.PerformImportGenerator;
import org.apache.streampipes.export.dataimport.PreviewImportGenerator;
import org.apache.streampipes.export.resolver.AbstractResolver;
import org.apache.streampipes.export.resolver.GenericStorageDocumentResolver;
import org.apache.streampipes.model.export.AssetExportConfiguration;
import org.apache.streampipes.model.export.ExportConfiguration;
import org.apache.streampipes.model.export.ExportItem;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.system.IGenericStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GenericStorageResolverTest {

  private static final String TYPE = "test:custom-resolver";
  private static final Function<IGenericStorage, AbstractResolver<Map<String, Object>>> FACTORY = PipelineDocumentResolver::new;
  private final IGenericStorage storage = mock(IGenericStorage.class);
  private final SpResourceManager resources = mock(SpResourceManager.class, RETURNS_DEEP_STUBS);

  @BeforeEach
  void setUp() throws IOException {
    GenericStorageResourceRegistry.INSTANCE.register(TYPE,
        new ResourceRegistration<>(new PermissionDefaults(getClass(), false), "Custom pipelines", FACTORY));
    when(resources.getGenericStorage()).thenReturn(storage);
    var document = Map.<String, Object>of("_id", "custom-1", "_rev", "1-revision", "appDocType", TYPE,
        "title", "Custom name", "running", true, "selectedEndpointUrl", "http://source");
    when(storage.getAllAppDocTypes()).thenReturn(List.of(TYPE));
    when(storage.findAll(TYPE)).thenReturn(List.of(document));
    when(storage.findOne("custom-1")).thenAnswer(invocation -> new HashMap<>(document));
  }

  @Test
  void shouldUseCustomConversionInExportAndImportPreviews() throws IOException {
    var config = ExportManager.getExportPreview(List.of(), null, resources, null);
    assertEquals("Custom name", config.getGenericStorageDocuments().getFirst().getLabel());
    assertFalse(config.getGenericStorageDocuments().getFirst().isSelected());
    config.getGenericStorageDocuments().getFirst().setSelected(true);

    var preview = new PreviewImportGenerator(null, resources, null)
        .generate(new ByteArrayInputStream(ExportManager.getExportPackage(config, null, resources, null)));
    var item = preview.getGenericStorageDocuments().iterator().next();
    assertEquals("Custom name", item.getLabel());
    assertEquals("Custom pipelines", item.getCategory());
  }

  @Test
  void shouldSanitizeIndividualAndBulkExports() throws IOException {
    for (boolean bulk : List.of(false, true)) {
      var config = new ExportConfiguration();
      if (bulk) {
        config.addGenericStorageAppDocType(new ExportItem(TYPE, TYPE, true));
      } else {
        config.getGenericStorageDocuments().add(new ExportItem("custom-1", "Custom name", true));
      }
      var archive = ExportManager.getExportPackage(config, null, resources, null);
      var files = new ZipFileExtractor(new ByteArrayInputStream(archive)).extractZipToMap();
      var exported = JacksonSerializer.getObjectMapper().readTree(files.get("custom-1"));
      assertFalse(exported.get("running").asBoolean());
      assertNull(exported.get("selectedEndpointUrl"));
      assertNull(exported.get("_rev"));
    }
  }

  @Test
  void shouldUseRegisteredResolverForImportAndOverwrite() throws IOException {
    var registry = new GenericStorageResourceRegistry();
    var resolver = spy(new PipelineDocumentResolver(storage));
    registry.register(TYPE, new ResourceRegistration<>(new PermissionDefaults(getClass(), false),
        "Custom pipelines", ignored -> resolver));
    var config = new ExportConfiguration();
    config.addGenericStorageAppDocType(new ExportItem(TYPE, TYPE, true));
    var archive = ExportManager.getExportPackage(config, null, resources, null);
    var preview = new PreviewImportGenerator(null, resources, null).generate(new ByteArrayInputStream(archive));
    preview.setOverwriteExistingDocuments(true);

    new PerformImportGenerator(preview, "importer", null, resources, null, registry)
        .generate(new ByteArrayInputStream(archive));

    verify(resolver).deleteDocument(anyString(), any());
    verify(resolver).writeDocument(anyString(), any(AssetExportConfiguration.class));
  }

  private static class PipelineDocumentResolver extends GenericStorageDocumentResolver {

    PipelineDocumentResolver(IGenericStorage storage) {
      super(storage);
    }

    @Override
    public Map<String, Object> modifyDocumentForExport(Map<String, Object> document) {
      super.modifyDocumentForExport(document);
      document.put("running", false);
      document.remove("selectedEndpointUrl");
      return document;
    }

    @Override
    public ExportItem convert(Map<String, Object> document) {
      return new ExportItem(document.get("_id").toString(), document.get("title").toString(), true);
    }
  }
}
