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
import org.apache.streampipes.export.generator.ZipFileBuilder;
import org.apache.streampipes.export.resolver.GenericStorageDocumentResolver;
import org.apache.streampipes.model.export.AssetExportConfiguration;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

  @Test
  void shouldRoundTripDocumentsWithSanitizedArchiveIds() throws IOException {
    when(resources.managePermissions().findForObjectId(anyString())).thenReturn(List.of());
    var archive = exportDocumentsWithSpecialIds();
    var importConfig = new PreviewImportGenerator(null, resources, null)
        .generate(new ByteArrayInputStream(archive));

    new PerformImportGenerator(importConfig, "importer", null, resources, null)
        .generate(new ByteArrayInputStream(archive));

    var document = ArgumentCaptor.forClass(String.class);
    verify(storage, times(3)).create(document.capture());
    var importedIds = new HashSet<String>();
    for (var payload : document.getAllValues()) {
      importedIds.add(JacksonSerializer.getObjectMapper().readTree(payload).get("_id").asText());
    }
    assertEquals(Set.of("pipeline:1", "pipeline.2", "pipeline:3.4"), importedIds);
    for (var id : importedIds) {
      verify(resources.managePermissions()).createDefault(id, getClass(), "importer", false);
    }
  }

  @Test
  void shouldRespectDeselectionForSanitizedArchiveIds() throws IOException {
    var archive = exportDocumentsWithSpecialIds();
    var importConfig = new PreviewImportGenerator(null, resources, null)
        .generate(new ByteArrayInputStream(archive));
    importConfig.getGenericStorageDocuments().forEach(item -> item.setSelected(false));

    new PerformImportGenerator(importConfig, "importer", null, resources, null)
        .generate(new ByteArrayInputStream(archive));

    verify(storage, never()).create(anyString());
  }

  @Test
  void shouldRejectDocumentIdsThatDoNotMatchArchiveEntries() throws IOException {
    var mapper = JacksonSerializer.getObjectMapper();
    var manifest = new StreamPipesApplicationPackage();
    manifest.addGenericStorageDocument("different-id");
    var archive = ZipFileBuilder.create()
        .addManifest(mapper.writeValueAsString(manifest))
        .addText("different-id", mapper.writeValueAsString(Map.of("_id", "pipeline:1", "appDocType", TYPE)))
        .buildZip();
    var generator = new PerformImportGenerator(new AssetExportConfiguration(), "importer", null, resources, null);

    assertThrows(IllegalArgumentException.class, () -> generator.generate(new ByteArrayInputStream(archive)));
    verify(storage, never()).create(anyString());
  }

  private byte[] exportDocumentsWithSpecialIds() throws IOException {
    var config = new ExportConfiguration();
    for (var id : List.of("pipeline:1", "pipeline.2", "pipeline:3.4")) {
      when(storage.findOne(id)).thenReturn(Map.of("_id", id, "appDocType", TYPE));
      config.getGenericStorageDocuments().add(new ExportItem(id, id, true));
    }
    return export(config);
  }

  @Test
  void shouldOnlyPreviewDocumentsLinkedToSelectedAssetsAndTheirChildren() throws IOException {
    when(storage.findAll(TYPE)).thenReturn(List.of(first, second,
        Map.of("_id", "unrelated", "appDocType", TYPE)));
    when(storage.findOne("asset-1")).thenReturn(Map.of(
        "assetName", "First asset",
        "assetLinks", List.of(Map.of("resourceId", "pipeline-1", "queryHint", "ai-pipeline")),
        "assets", List.of()));
    when(storage.findOne("asset-2")).thenReturn(Map.of(
        "assetName", "Second asset",
        "assetLinks", List.of(Map.of("resourceId", "pipeline-1", "queryHint", "ai-pipeline")),
        "assets", List.of(Map.of("assetLinks", List.of(
            Map.of("resourceId", "pipeline-2", "queryHint", "ai-notebook"))))));

    var firstPreview = ExportManager.getExportPreview(List.of("asset-1"), null, resources, null);
    assertTrue(firstPreview.getGenericStorageDocuments().isEmpty());
    assertEquals(List.of("pipeline-1"), firstPreview.getAssetExportConfiguration().getFirst()
        .getGenericStorageDocuments().stream()
        .map(ExportItem::getResourceId).toList());

    var bothPreview = ExportManager.getExportPreview(List.of("asset-1", "asset-2"), null, resources, null);
    assertTrue(bothPreview.getGenericStorageDocuments().isEmpty());
    var firstAsset = bothPreview.getAssetExportConfiguration().getFirst();
    var secondAsset = bothPreview.getAssetExportConfiguration().get(1);
    assertEquals("First asset", firstAsset.getAssetName());
    assertEquals("Second asset", secondAsset.getAssetName());
    assertEquals(Set.of("pipeline-1", "pipeline-2"), new HashSet<>(secondAsset.getGenericStorageDocuments().stream()
        .map(ExportItem::getResourceId).toList()));
    firstAsset.getGenericStorageDocuments().forEach(item -> item.setSelected(false));
    assertTrue(secondAsset.getGenericStorageDocuments().stream().allMatch(ExportItem::isSelected));
    verify(storage, never()).findAll(TYPE);
  }

  @Test
  void shouldNotPreviewRegisteredDocumentsForAssetsWithoutLinks() throws IOException {
    when(storage.findOne("asset-1")).thenReturn(Map.of("assetName", "Empty asset"));

    var config = ExportManager.getExportPreview(List.of("asset-1"), null, resources, null);

    assertTrue(config.getGenericStorageDocuments().isEmpty());
    assertTrue(config.getGenericStorageAppDocTypes().stream().noneMatch(item -> TYPE.equals(item.getResourceId())));
  }

  @Test
  void shouldSkipMissingLinksWithoutOfferingBulkFallback() throws IOException {
    when(storage.findOne("asset-1")).thenReturn(Map.of(
        "assetName", "Test asset",
        "assetLinks", List.of(Map.of("resourceId", "missing", "queryHint", "ai-pipeline"),
            Map.of("resourceId", "pipeline-1", "queryHint", "ai-pipeline"))));
    when(storage.findOne("missing")).thenThrow(new IOException("not found"));

    var config = ExportManager.getExportPreview(List.of("asset-1"), null, resources, null);
    assertEquals(List.of("pipeline-1"), config.getAssetExportConfiguration().getFirst()
        .getGenericStorageDocuments().stream().map(ExportItem::getResourceId).toList());
    verify(storage, never()).findAll(TYPE);


    assertTrue(config.getGenericStorageDocuments().isEmpty());
    assertTrue(config.getGenericStorageAppDocTypes().stream().noneMatch(item -> TYPE.equals(item.getResourceId())));
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
