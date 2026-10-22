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

package org.apache.streampipes.resource.management;

import org.apache.streampipes.model.assets.SpAsset;
import org.apache.streampipes.model.assets.SpAssetModel;
import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.storage.api.system.IAssetStorage;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class AssetResourceManagerTest {

  private final IAssetStorage storage = mock(IAssetStorage.class);
  private final AssetResourceManager manager =
      new AssetResourceManager(
        storage, 
        mock(PermissionResourceManager.class),
        mock(ResourceDeletionManager.class)
        );

  @Test
  void removesOnlyTheSelectedNestedAssetsImage() {
    var root = new SpAssetModel();
    var parent = new SpAsset();
    var child = new SpAsset();
    child.setAssetId("child");
    root.getAssets().add(parent);
    parent.getAssets().add(child);
    root.getAssetImages().add(image("root-image"));
    parent.getAssetImages().add(image("parent-image"));
    child.getAssetImages().add(image("child-image"));

    assertTrue(manager.removeAssetImage(root, "child", "child-image"));
    assertTrue(child.getAssetImages().isEmpty());
    assertEquals(1, root.getAssetImages().size());
    assertEquals(1, parent.getAssetImages().size());
    verify(storage).updateElement(root);
  }

  @Test
  void removesRootImageWhenRootIsSelected() {
    var root = new SpAssetModel();
    root.setAssetId("root");
    root.getAssetImages().add(image("root-image"));
    assertTrue(manager.removeAssetImage(root, "root", "root-image"));
    assertTrue(root.getAssetImages().isEmpty());
    verify(storage).updateElement(root);
  }

  @Test
  void doesNotUpdateWhenAssetOrImageIsMissing() {
    var root = new SpAssetModel();
    root.setAssetId("root");
    root.getAssetImages().add(image("root-image"));
    assertFalse(manager.removeAssetImage(root, "missing", "root-image"));
    assertFalse(manager.removeAssetImage(root, "root", "missing"));
    assertEquals(1, root.getAssetImages().size());
    verifyNoInteractions(storage);
  }

  @Test
  void rejectsMultipleImagesOnCreate() {
    var root = new SpAssetModel();
    root.getAssetImages().add(image("first"));
    root.getAssetImages().add(image("second"));

    assertThrows(IllegalArgumentException.class, () -> manager.create(root, "user"));
    verifyNoInteractions(storage);
  }

  @Test
  void rejectsMultipleImagesAnywhereInTheHierarchyOnUpdate() {
    var root = new SpAssetModel();
    var child = new SpAsset();
    var grandchild = new SpAsset();
    root.getAssets().add(child);
    child.getAssets().add(grandchild);

    for (var asset : List.of(root, child, grandchild)) {
      asset.getAssetImages().add(image("first"));
      asset.getAssetImages().add(image("second"));
      assertThrows(IllegalArgumentException.class, () -> manager.update(root));
      asset.getAssetImages().clear();
    }
    verifyNoInteractions(storage);
  }

  @Test
  void acceptsOneImagePerAsset() {
    var root = new SpAssetModel();
    var child = new SpAsset();
    root.getAssets().add(child);
    root.getAssetImages().add(image("root-image"));
    child.getAssetImages().add(image("child-image"));

    manager.update(root);

    verify(storage).updateElement(root);
  }

  private FileMetadata image(String fileId) {
    var image = new FileMetadata();
    image.setFileId(fileId);
    return image;
  }
}
