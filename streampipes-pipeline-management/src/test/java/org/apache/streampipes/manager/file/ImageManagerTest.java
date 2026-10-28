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

import org.apache.streampipes.model.Tuple2;
import org.apache.streampipes.model.configuration.SpCoreConfiguration;
import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.storage.api.system.IAssetStorage;
import org.apache.streampipes.storage.api.system.IImageMetadataStorage;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ImageManagerTest {

  @TempDir
  Path directory;

  private ImageManager manager;
  private IImageMetadataStorage storage;
  private IAssetStorage assets;
  private final Map<String, FileMetadata> metadata = new HashMap<>();

  @BeforeEach
  void setUp() {
    var configuration = new SpCoreConfiguration();
    configuration.setFilesDir(directory.toString());
    var configurationStorage = mock(ISpCoreConfigurationStorage.class);
    when(configurationStorage.get()).thenReturn(configuration);
    storage = mock(IImageMetadataStorage.class);
    assets = mock(IAssetStorage.class);
    when(storage.getElementById(anyString())).thenAnswer(call -> metadata.get(call.getArgument(0)));
    when(storage.persist(any())).thenAnswer(call -> {
      FileMetadata image = call.getArgument(0);
      image.setRev("1-image");
      metadata.put(image.getFileId(), image);
      return new Tuple2<>(true, image.getFileId());
    });
    manager = new ImageManager(configurationStorage, storage, assets);
  }

  @Test
  void keepsSameNamedImagesIndependentAndSeparateFromGenericFiles() throws Exception {
    Files.writeString(directory.resolve("factory.PNG"), "generic file");
    var first = manager.storeImage("user", "factory.PNG", content("first image"));
    var second = manager.storeImage("user", "factory.PNG", content("second image"));

    assertNotEquals(first.getFileId(), second.getFileId());
    assertEquals("png", first.getFiletype());
    assertEquals("1-image", first.getRev());
    assertEquals("first image", Files.readString(manager.getImage(first.getFileId()).toPath()));
    assertEquals("second image", Files.readString(manager.getImage(second.getFileId()).toPath()));
    assertEquals("generic file", Files.readString(directory.resolve("factory.PNG")));
  }

  @Test
  void rejectsNonImageExtensionsBeforeWriting() {
    assertThrows(IllegalArgumentException.class, () -> manager.storeImage("user", "data.csv", content("data")));
    assertThrows(IllegalArgumentException.class, () -> manager.storeImage("user", null, content("data")));
    assertTrue(metadata.isEmpty());
    assertFalse(Files.exists(directory.resolve("images")));
  }

  @Test
  void removesUploadedContentWhenMetadataPersistenceFails() throws Exception {
    doReturn(new Tuple2<>(false, "failed")).when(storage).persist(any());
    assertThrows(IllegalStateException.class, () -> manager.storeImage("user", "factory.png", content("image")));
    try (var files = Files.list(directory.resolve("images"))) {
      assertEquals(0, files.count());
    }
  }

  @Test
  void usesTheReferenceIndexAndRetainsLinkedImagesUntilUnlinked() throws Exception {
    var image = manager.storeImage("user", "factory.jpg", content("image"));
    when(assets.isImageReferenced(image.getFileId())).thenReturn(true, false);

    assertFalse(manager.deleteImageIfUnused(image.getFileId()));
    assertTrue(manager.getImage(image.getFileId()).isFile());
    assertTrue(manager.deleteImageIfUnused(image.getFileId()));
    assertFalse(Files.exists(directory.resolve("images").resolve(image.getFileId())));
    verify(assets, never()).findAll();
  }

  @Test
  void leavesImagesUntouchedIfTheIndexLookupFails() throws Exception {
    var image = manager.storeImage("user", "factory.jpg", content("image"));
    when(assets.isImageReferenced(image.getFileId())).thenThrow(new IllegalStateException("Index unavailable"));
    assertThrows(IllegalStateException.class, () -> manager.deleteImageIfUnused(image.getFileId()));
    assertTrue(manager.getImage(image.getFileId()).isFile());
    verify(storage, never()).deleteElementById(anyString());
  }

  private ByteArrayInputStream content(String value) {
    return new ByteArrayInputStream(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
  }
}
