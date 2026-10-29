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
import org.apache.streampipes.export.resolver.GenericStorageDocumentResolver;
import org.apache.streampipes.model.export.ExportConfiguration;
import org.apache.streampipes.model.export.ExportItem;
import org.apache.streampipes.model.export.StreamPipesApplicationPackage;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.system.IGenericStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GenericStorageCategoryExportTest {

  private static final String TYPE = "test:categorized-pipeline";
  private static final ResourceRegistration<Map<String, Object>> REGISTRATION = new ResourceRegistration<>(
      new PermissionDefaults(GenericStorageCategoryExportTest.class, false),
      "Test pipelines", NamedDocumentResolver::new);
  private final IGenericStorage storage = mock(IGenericStorage.class);
  private final SpResourceManager resources = mock(SpResourceManager.class, RETURNS_DEEP_STUBS);
  private final Map<String, Object> first = Map.of(
      "_id", "pipeline-1", "appDocType", TYPE, "name", "Named pipeline", "_rev", "1-revision");
  private final Map<String, Object> second = Map.of("_id", "pipeline-2", "appDocType", TYPE);

  @BeforeEach
  void setUp() throws IOException {
    GenericStorageResourceRegistry.INSTANCE.register(TYPE, REGISTRATION);
    when(resources.getGenericStorage()).thenReturn(storage);
    when(storage.getAllAppDocTypes()).thenReturn(List.of(TYPE, "other-documents"));
    when(storage.findAll(TYPE)).thenReturn(List.of(first, second));
    // Export sanitization removes _rev from the retrieved map.
    when(storage.findOne("pipeline-1")).thenAnswer(invocation -> new HashMap<>(first));
    when(storage.findOne("pipeline-2")).thenAnswer(invocation -> new HashMap<>(second));
  }

  @Test
  void shouldListNamedCategorySeparatelyAndUseIdWhenNameIsMissing() {
    var preview = preview();

    assertEquals(List.of("other-documents"), preview.getGenericStorageAppDocTypes().stream()
        .map(ExportItem::getResourceId).toList());
    assertEquals(List.of("Named pipeline", "pipeline-2"), preview.getGenericStorageDocuments().stream()
        .map(ExportItem::getLabel).toList());
    assertTrue(preview.getGenericStorageDocuments().stream().allMatch(item -> "Test pipelines".equals(item.getCategory())));
    assertTrue(preview.getGenericStorageDocuments().stream().noneMatch(ExportItem::isSelected));
  }

  @Test
  void shouldExportOnlySelectedDocumentsUsingExistingArchiveFormat() throws IOException {
    var preview = preview();
    preview.getGenericStorageDocuments().getFirst().setSelected(true);

    var files = new ZipFileExtractor(new ByteArrayInputStream(export(preview))).extractZipToMap();
    var manifest = JacksonSerializer.getObjectMapper().readValue(files.get("manifest"),
        StreamPipesApplicationPackage.class);

    assertEquals(Set.of("pipeline-1"), manifest.getGenericStorageDocuments());
    assertTrue(files.containsKey("pipeline-1"));
    assertFalse(files.containsKey("pipeline-2"));
  }

  @Test
  void shouldStillSupportBulkExportsFromOlderClients() throws IOException {
    var config = new ExportConfiguration();
    config.addGenericStorageAppDocType(new ExportItem(TYPE, TYPE, true));

    var files = new ZipFileExtractor(new ByteArrayInputStream(export(config))).extractZipToMap();
    var manifest = JacksonSerializer.getObjectMapper().readValue(files.get("manifest"),
        StreamPipesApplicationPackage.class);

    assertEquals(Set.of("pipeline-1", "pipeline-2"), manifest.getGenericStorageDocuments());
  }

  @Test
  void shouldCategorizeLegacyArchiveDocumentsDuringImportPreview() throws IOException {
    var config = new ExportConfiguration();
    config.addGenericStorageAppDocType(new ExportItem(TYPE, TYPE, true));

    var preview = new PreviewImportGenerator(null, resources, null)
        .generate(new ByteArrayInputStream(export(config)));

    assertEquals(2, preview.getGenericStorageDocuments().size());
    assertTrue(preview.getGenericStorageDocuments().stream().allMatch(item -> "Test pipelines".equals(item.getCategory())));
    assertTrue(preview.getGenericStorageDocuments().stream().allMatch(ExportItem::isSelected));
    assertTrue(preview.getGenericStorageDocuments().stream().anyMatch(item -> "Named pipeline".equals(item.getLabel())));
  }

  @Test
  void shouldKeepUnregisteredDocumentsUncategorized() throws IOException {
    when(storage.findAll("other-documents")).thenReturn(List.of(Map.of(
        "_id", "other-1", "appDocType", "other-documents", "name", "Other")));
    when(storage.findOne("other-1")).thenAnswer(invocation -> new HashMap<>(Map.of(
        "_id", "other-1", "appDocType", "other-documents", "name", "Other")));
    var config = new ExportConfiguration();
    config.addGenericStorageAppDocType(new ExportItem("other-documents", "Other", true));

    var preview = new PreviewImportGenerator(null, resources, null)
        .generate(new ByteArrayInputStream(export(config)));

    var item = preview.getGenericStorageDocuments().iterator().next();
    assertNull(item.getCategory());
    assertEquals("other-1", item.getLabel());
  }

  @Test
  void shouldFallBackToBulkSelectionWhenDocumentEnumerationFails() throws IOException {
    when(storage.findAll(TYPE)).thenThrow(new IOException("unavailable"));

    var preview = preview();

    assertTrue(preview.getGenericStorageDocuments().isEmpty());
    assertTrue(preview.getGenericStorageAppDocTypes().stream().anyMatch(item -> TYPE.equals(item.getResourceId())));
  }

  @Test
  void shouldNotLetUnselectedCategoryItemsSuppressAnExplicitBulkExport() throws IOException {
    var config = preview();
    config.addGenericStorageAppDocType(new ExportItem(TYPE, TYPE, true));

    var files = new ZipFileExtractor(new ByteArrayInputStream(export(config))).extractZipToMap();
    var manifest = JacksonSerializer.getObjectMapper().readValue(files.get("manifest"),
        StreamPipesApplicationPackage.class);

    assertEquals(Set.of("pipeline-1", "pipeline-2"), manifest.getGenericStorageDocuments());
  }

  @Test
  void shouldRespectIndividualSelectionsWhenImportingCategorizedDocuments() throws IOException {
    var config = new ExportConfiguration();
    config.addGenericStorageAppDocType(new ExportItem(TYPE, TYPE, true));
    var archive = export(config);
    var importConfig = new PreviewImportGenerator(null, resources, null)
        .generate(new ByteArrayInputStream(archive));
    importConfig.getGenericStorageDocuments().stream()
        .filter(item -> "pipeline-1".equals(item.getResourceId()))
        .forEach(item -> item.setSelected(false));

    new PerformImportGenerator(importConfig, "importer", null, resources, null)
        .generate(new ByteArrayInputStream(archive));

    var document = ArgumentCaptor.forClass(String.class);
    verify(storage).create(document.capture());
    assertEquals("pipeline-2", JacksonSerializer.getObjectMapper().readTree(document.getValue()).get("_id").asText());
  }

  private ExportConfiguration preview() {
    return ExportManager.getExportPreview(List.of(), null, resources, null);
  }

  private byte[] export(ExportConfiguration config) throws IOException {
    return ExportManager.getExportPackage(config, null, resources, null);
  }
  private static class NamedDocumentResolver extends GenericStorageDocumentResolver {

    NamedDocumentResolver(IGenericStorage storage) {
      super(storage);
    }

    @Override
    public ExportItem convert(Map<String, Object> document) {
      var id = document.get("_id").toString();
      var label = document.get("name");
      return new ExportItem(id, label instanceof String name && !name.isBlank() ? name : id, true);
    }
  }

}
