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

package org.apache.streampipes.model.assets;

import org.apache.streampipes.model.file.FileMetadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpAssetImagesTest {

  @Test
  void roundTripsIndependentImagesAtEveryHierarchyLevel() throws Exception {
    var root = new SpAssetModel();
    var child = new SpAsset();
    var grandchild = new SpAsset();
    root.getAssets().add(child);
    child.getAssets().add(grandchild);
    root.getAssetImages().add(image("root"));
    child.getAssetImages().add(image("child"));
    grandchild.getAssetImages().add(image("grandchild"));

    var mapper = new ObjectMapper();
    var fromRest = mapper.readValue(mapper.writeValueAsString(root), SpAssetModel.class);
    var gson = new Gson();
    var fromStorage = gson.fromJson(gson.toJson(fromRest), SpAssetModel.class);

    assertEquals("root", fromStorage.getAssetImages().getFirst().getFileId());
    assertEquals("child", fromStorage.getAssets().getFirst().getAssetImages().getFirst().getFileId());
    var restoredGrandchild = fromStorage.getAssets().getFirst().getAssets().getFirst();
    assertEquals("grandchild", restoredGrandchild.getAssetImages().getFirst().getFileId());
    restoredGrandchild.getAssetImages().clear();
    assertEquals(1, fromStorage.getAssetImages().size());
    assertEquals(1, fromStorage.getAssets().getFirst().getAssetImages().size());
  }

  @Test
  void keepsLegacyRootImagesOnRootAndDefaultsChildrenToEmpty() throws Exception {
    var mapper = new ObjectMapper();
    var root = mapper.readValue("""
        {"assetImages": [{"fileId": "legacy"}], "assets": [{"assetId": "child"}]}
        """, SpAssetModel.class);
    assertEquals("legacy", root.getAssetImages().getFirst().getFileId());
    assertTrue(root.getAssets().getFirst().getAssetImages().isEmpty());
    assertTrue(new SpAsset().getAssetImages().isEmpty());
  }

  private FileMetadata image(String fileId) {
    var image = new FileMetadata();
    image.setFileId(fileId);
    return image;
  }

  @Test
  void readsLegacyImageMetadataAndPreservesGenericFileSerialization() throws Exception {
    var gson = new Gson();
    var image = gson.fromJson("""
        {"_id":"legacy","_rev":"1-old","filename":"factory.jpeg","filetype":"jpeg","createdAt":123}
        """, FileMetadata.class);
    assertEquals("legacy", image.getFileId());
    assertEquals("jpeg", image.getFiletype());
    var mapper = new ObjectMapper();
    var restored = mapper.readValue(mapper.writeValueAsString(image), FileMetadata.class);
    assertEquals("legacy", restored.getFileId());
    assertEquals(123, restored.getCreatedAt());

    var file = new FileMetadata();
    file.setFileId("file");
    file.setFiletype("csv");
    assertEquals("file", gson.fromJson(gson.toJson(file),
        FileMetadata.class).getFileId());
    assertTrue(gson.toJson(file).contains("\"_id\":\"file\""));
    assertTrue(mapper.writeValueAsString(file).contains("\"fileId\":\"file\""));
  }
}
