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

package org.apache.streampipes.service.core.migrations.v099;

import org.apache.streampipes.commons.constants.GenericDocTypes;
import org.apache.streampipes.storage.api.system.IGenericStorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RenameAssetLinkTypesMigrationTest {

  private IGenericStorage genericStorage;
  private RenameAssetLinkTypesMigration migration;

  @BeforeEach
  void setUp() {
    genericStorage = mock(IGenericStorage.class);
    migration = new RenameAssetLinkTypesMigration(genericStorage, new ObjectMapper());
  }

  @Test
  void shouldExecuteWhenLegacyLinkTypesExist() throws IOException {
    when(genericStorage.findAll(GenericDocTypes.DOC_ASSET_LINK_TYPE))
        .thenReturn(List.of(linkType("data-source", "Data Source")));
    when(genericStorage.findAll(GenericDocTypes.DOC_ASSET_MANAGEMENT)).thenReturn(List.of());

    assertTrue(migration.shouldExecute());
  }

  @Test
  void shouldNotExecuteWhenAllLinkTypesAreCurrent() throws IOException {
    when(genericStorage.findAll(GenericDocTypes.DOC_ASSET_LINK_TYPE))
        .thenReturn(List.of(linkType("data-stream", "Data Stream"), linkType("dataset", "Dataset")));
    when(genericStorage.findAll(GenericDocTypes.DOC_ASSET_MANAGEMENT)).thenReturn(List.of());

    assertFalse(migration.shouldExecute());
  }

  @Test
  void shouldMigrateLinkTypesAndNestedAssetLinks() throws IOException {
    Map<String, Object> dataSource = linkType("data-source", "Data Source");
    Map<String, Object> measurement = linkType("measurement", "Data Lake Storage");
    Map<String, Object> asset = assetWithLegacyLinks();
    when(genericStorage.findAll(GenericDocTypes.DOC_ASSET_LINK_TYPE))
        .thenReturn(List.of(dataSource, measurement, linkType("chart", "Chart")));
    when(genericStorage.findAll(GenericDocTypes.DOC_ASSET_MANAGEMENT)).thenReturn(List.of(asset));

    migration.executeMigration();

    assertEquals("data-stream", dataSource.get("linkType"));
    assertEquals("Data Stream", dataSource.get("linkLabel"));
    assertEquals("data-stream", dataSource.get("linkQueryHint"));
    assertEquals("dataset", measurement.get("linkType"));
    assertEquals("Dataset", measurement.get("linkLabel"));
    assertEquals("dataset", measurement.get("linkQueryHint"));

    Map<?, ?> rootLink = (Map<?, ?>) ((List<?>) asset.get("assetLinks")).get(0);
    Map<?, ?> nestedAsset = (Map<?, ?>) ((List<?>) asset.get("assets")).get(0);
    Map<?, ?> nestedLink = (Map<?, ?>) ((List<?>) nestedAsset.get("assetLinks")).get(0);
    assertEquals("data-stream", rootLink.get("linkType"));
    assertEquals("data-stream", rootLink.get("queryHint"));
    assertEquals("dataset", nestedLink.get("linkType"));
    assertEquals("dataset", nestedLink.get("queryHint"));
    verify(genericStorage).update(eq("data-source"), anyString());
    verify(genericStorage).update(eq("measurement"), anyString());
    verify(genericStorage).update(eq("asset"), anyString());
  }

  private Map<String, Object> linkType(String type, String label) {
    Map<String, Object> linkType = new HashMap<>();
    linkType.put("_id", type);
    linkType.put("linkType", type);
    linkType.put("linkLabel", label);
    return linkType;
  }

  private Map<String, Object> assetWithLegacyLinks() {
    Map<String, Object> rootLink = link("data-source");
    Map<String, Object> nestedLink = link("measurement");
    Map<String, Object> nestedAsset = new HashMap<>();
    nestedAsset.put("assetLinks", List.of(nestedLink));

    Map<String, Object> asset = new HashMap<>();
    asset.put("_id", "asset");
    asset.put("assetLinks", List.of(rootLink));
    asset.put("assets", List.of(nestedAsset));
    return asset;
  }

  private Map<String, Object> link(String type) {
    Map<String, Object> link = new HashMap<>();
    link.put("linkType", type);
    link.put("queryHint", type);
    return link;
  }
}
