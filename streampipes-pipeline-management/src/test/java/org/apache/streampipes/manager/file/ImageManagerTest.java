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
package org.apache.streampipes.manager.file;

import org.apache.streampipes.model.assets.SpAsset;
import org.apache.streampipes.model.assets.SpAssetModel;
import org.apache.streampipes.model.configuration.SpCoreConfiguration;
import org.apache.streampipes.storage.api.system.IAssetStorage;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ImageManagerTest {

  @TempDir
  Path directory;

  private ImageManager manager;
  private IAssetStorage assets;
  private SpAssetModel root;
  private SpAsset child;

  @BeforeEach
  void setUp() {
    var configuration = new SpCoreConfiguration();
    configuration.setFilesDir(directory.toString());
    var configurationStorage = mock(ISpCoreConfigurationStorage.class);
    when(configurationStorage.get()).thenReturn(configuration);
    assets = mock(IAssetStorage.class);
    root = new SpAssetModel();
    root.setAssetId("root");
    child = new SpAsset();
    child.setAssetId("child");
    root.getAssets().add(child);
    when(assets.getElementById("document")).thenReturn(root);
    manager = new ImageManager(configurationStorage, assets);
  }

  @Test
  void storesGeneratedIdsAndTypesOnTheSelectedAssetOnly() throws Exception {
    Files.writeString(directory.resolve("factory.PNG"), "generic file");
    manager.storeImage("document", "root", "factory.PNG", content("root image"));
    manager.storeImage("document", "child", "factory.PNG", content("child image"));

    UUID.fromString(root.getImageId());
    UUID.fromString(child.getImageId());
    assertNotEquals(root.getImageId(), child.getImageId());
    assertEquals("png", child.getFiletype());
    assertEquals("root image", Files.readString(manager.getImage("document", "root", root.getImageId()).toPath()));
    assertEquals("child image", Files.readString(manager.getImage("document", "child", child.getImageId()).toPath()));
    assertEquals("generic file", Files.readString(directory.resolve("factory.PNG")));
  }

  @Test
  void rejectsUnrelatedImagesAndAssetsForBothReadingAndDeleting() throws Exception {
    manager.storeImage("document", "child", "factory.jpg", content("image"));
    var imageId = child.getImageId();
    assertThrows(FileNotFoundException.class, () -> manager.getImage("document", "root", imageId));
    assertThrows(FileNotFoundException.class, () -> manager.deleteImage("document", "root", imageId));
    assertThrows(FileNotFoundException.class, () -> manager.getImage("other", "child", imageId));
    assertThrows(FileNotFoundException.class, () -> manager.deleteImage("document", "missing", imageId));
    assertTrue(manager.getImage("document", "child", imageId).isFile());
  }

  @Test
  void rejectsNonImageExtensionsBeforeWriting() {
    assertThrows(IllegalArgumentException.class,
        () -> manager.storeImage("document", "child", "data.csv", content("data")));
    assertThrows(IllegalArgumentException.class,
        () -> manager.storeImage("document", "child", null, content("data")));
    assertNull(child.getImageId());
    assertFalse(Files.exists(directory.resolve("images")));
  }

  @Test
  void removesUploadedContentWhenAssetPersistenceFails() throws Exception {
    doThrow(new IllegalStateException("Conflict")).when(assets).updateElement(any());
    assertThrows(IllegalStateException.class,
        () -> manager.storeImage("document", "child", "factory.png", content("image")));
    try (var files = Files.list(directory.resolve("images"))) {
      assertEquals(0, files.count());
    }
  }

  @Test
  void explicitlyDeletesTheImageAndReferenceBeforeReplacement() throws Exception {
    manager.storeImage("document", "child", "factory.jpg", content("old"));
    var previousId = child.getImageId();
    manager.deleteImage("document", "child", previousId);
    assertNull(child.getImageId());
    assertNull(child.getFiletype());
    assertFalse(Files.exists(directory.resolve("images").resolve(previousId)));
    manager.storeImage("document", "child", "factory.png", content("new"));
    assertNotEquals(previousId, child.getImageId());
  }

  @Test
  void canRemoveAReferenceWhenItsPhysicalFileIsMissing() throws Exception {
    child.setImageId(UUID.randomUUID().toString());
    child.setFiletype("png");
    manager.deleteImage("document", "child", child.getImageId());
    assertNull(child.getImageId());
    verify(assets).updateElement(root);
  }

  @Test
  void deletesImagesThroughoutTheDeletedAssetHierarchy() throws Exception {
    manager.storeImage("document", "root", "root.png", content("root"));
    manager.storeImage("document", "child", "child.png", content("child"));
    manager.deleteAssetImages(root);
    assertFalse(Files.exists(directory.resolve("images").resolve(root.getImageId())));
    assertFalse(Files.exists(directory.resolve("images").resolve(child.getImageId())));
  }

  @Test
  void movingAnAssetKeepsItsImageAvailableInTheDestinationDocument() throws Exception {
    manager.storeImage("document", "child", "child.png", content("child"));
    var destination = new SpAssetModel();
    destination.getAssets().add(child);
    root.getAssets().clear();
    when(assets.getElementById("destination")).thenReturn(destination);
    assertThrows(FileNotFoundException.class, () -> manager.getImage("document", "child", child.getImageId()));
    assertTrue(manager.getImage("destination", "child", child.getImageId()).isFile());
  }

  private ByteArrayInputStream content(String value) {
    return new ByteArrayInputStream(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
  }
}
