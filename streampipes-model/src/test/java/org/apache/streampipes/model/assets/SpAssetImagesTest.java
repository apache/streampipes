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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SpAssetImagesTest {

  @Test
  void roundTripsIndependentImagesAtEveryHierarchyLevel() throws Exception {
    var root = new SpAssetModel();
    var child = new SpAsset();
    var grandchild = new SpAsset();
    root.getAssets().add(child);
    child.getAssets().add(grandchild);
    root.setImageId("root-image");
    root.setFiletype("png");
    child.setImageId("child-image");
    child.setFiletype("jpeg");
    grandchild.setImageId("grandchild-image");
    grandchild.setFiletype("jpg");

    var mapper = new ObjectMapper();
    var fromRest = mapper.readValue(mapper.writeValueAsString(root), SpAssetModel.class);
    var gson = new Gson();
    var fromStorage = gson.fromJson(gson.toJson(fromRest), SpAssetModel.class);

    assertEquals("root-image", fromStorage.getImageId());
    assertEquals("png", fromStorage.getFiletype());
    var restoredChild = fromStorage.getAssets().getFirst();
    assertEquals("child-image", restoredChild.getImageId());
    assertEquals("jpeg", restoredChild.getFiletype());
    assertEquals("grandchild-image", restoredChild.getAssets().getFirst().getImageId());
    assertEquals("jpg", restoredChild.getAssets().getFirst().getFiletype());
    restoredChild.setImageId(null);
    assertEquals("root-image", fromStorage.getImageId());
    assertNull(new SpAsset().getImageId());
  }
}
