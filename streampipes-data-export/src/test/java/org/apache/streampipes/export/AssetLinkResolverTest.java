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

import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.resource.management.SpResourceManager;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AssetLinkResolverTest {

  @Test
  void shouldIgnoreLinksWithoutQueryHintsAndResolveSupportedLinks() throws IOException {
    var resources = mock(SpResourceManager.class, RETURNS_DEEP_STUBS);
    when(resources.getGenericStorage().findOne("asset-1")).thenReturn(Map.of(
        "assetName", "Test asset",
        "assets", List.of(),
        "assetLinks", List.of(
            Map.of("resourceId", "external-link"),
            Map.of("resourceId", "file-1", "queryHint", "file"))));
    var file = new FileMetadata();
    file.setFileId("file-1");
    file.setFilename("test.csv");
    when(resources.getFileMetadataStorage().getElementById("file-1")).thenReturn(file);

    var result = new AssetLinkResolver("asset-1", null, resources, null).resolveResources();

    assertEquals("asset-1", result.getAssetId());
    assertEquals("Test asset", result.getAssetName());
    assertEquals(1, result.getFiles().size());
    assertEquals("file-1", result.getFiles().iterator().next().getResourceId());
    assertTrue(result.getAdapters().isEmpty());
    assertTrue(result.getDataViews().isEmpty());
    assertTrue(result.getDashboards().isEmpty());
    assertTrue(result.getDataSources().isEmpty());
    assertTrue(result.getPipelines().isEmpty());
    assertTrue(result.getDataLakeMeasures().isEmpty());
  }
}
