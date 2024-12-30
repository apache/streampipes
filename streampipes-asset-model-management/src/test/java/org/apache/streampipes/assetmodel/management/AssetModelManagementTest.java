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

package org.apache.streampipes.assetmodel.management;

import org.apache.streampipes.model.assets.SpAssetModel;
import org.apache.streampipes.storage.api.IGenericStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AssetModelManagementTest {

  private static final String SAMPLE_ASSET_MODEL_ID = "1";
  private static final String SAMPLE_ASSET_MODEL_NAME = "Asset1";
  private static final Map<String, Object> SAMPLE_ASSET_MODEL_AS_MAP = Map.of(
      "_id",
      SAMPLE_ASSET_MODEL_ID,
      "assetName",
      SAMPLE_ASSET_MODEL_NAME
  );
  private static final String SAMPLE_ASSET_MODEL_AS_JSON = """
                                                           {
                                                           "_id": "SAMPLE_ASSET_MODEL_ID",
                                                           "assetName": "SAMPLE_ASSET_MODEL_NAME"
                                                           }
                                                           """;

  private static final String REV = "1";

  private IGenericStorage genericStorage;
  private AssetModelManagement assetModelManagement;

  @BeforeEach
  void setUp() {
    genericStorage = Mockito.mock(IGenericStorage.class);
    assetModelManagement = new AssetModelManagement(genericStorage);
  }

  @Test
  void findAll_ReturnsListOfAssetModels() throws IOException {
    when(genericStorage.findAll(SpAssetModel.APP_DOC_TYPE)).thenReturn(List.of(SAMPLE_ASSET_MODEL_AS_MAP));

    var result = assetModelManagement.findAll();

    assertEquals(1, result.size());
    assertEquals(
        SAMPLE_ASSET_MODEL_ID,
        result.get(0)
              .getId()
    );
    assertEquals(
        SAMPLE_ASSET_MODEL_NAME,
        result.get(0)
              .getAssetName()
    );
  }

  @Test
  void findAll_ReturnsEmptyListWhenNoData() throws IOException {
    when(genericStorage.findAll(SpAssetModel.APP_DOC_TYPE)).thenReturn(List.of());

    var result = assetModelManagement.findAll();

    assertTrue(result.isEmpty());
  }

  @Test
  void findAll_ThrowsIOException() throws IOException {
    when(genericStorage.findAll(SpAssetModel.APP_DOC_TYPE)).thenThrow(new IOException());

    assertThrows(IOException.class, () -> assetModelManagement.findAll());
  }


  @Test
  void findOne_ReturnsAssetModel() throws IOException {
    when(genericStorage.findOne(SAMPLE_ASSET_MODEL_ID)).thenReturn(SAMPLE_ASSET_MODEL_AS_MAP);

    var result = assetModelManagement.findOne(SAMPLE_ASSET_MODEL_ID);

    assertEquals(SAMPLE_ASSET_MODEL_ID, result.getId());
    assertEquals(SAMPLE_ASSET_MODEL_NAME, result.getAssetName());
  }

  @Test
  void findOne_ThrowsIOException() throws IOException {
    when(genericStorage.findOne(SAMPLE_ASSET_MODEL_ID)).thenThrow(new IOException());

    assertThrows(IOException.class, () -> assetModelManagement.findOne(SAMPLE_ASSET_MODEL_ID));
  }

  @Test
  void findOne_ReturnsNoSuchElementExceptionWhenNotFound() throws IOException {
    when(genericStorage.findOne(SAMPLE_ASSET_MODEL_ID)).thenReturn(null);

    assertThrows(NoSuchElementException.class, () -> assetModelManagement.findOne(SAMPLE_ASSET_MODEL_ID));
  }

  @Test
  void create_ReturnsCreatedAssetModel() throws IOException {
    when(genericStorage.create(SAMPLE_ASSET_MODEL_NAME)).thenReturn(SAMPLE_ASSET_MODEL_AS_MAP);

    var result = assetModelManagement.create(SAMPLE_ASSET_MODEL_NAME);

    assertEquals(SAMPLE_ASSET_MODEL_ID, result.getId());
    assertEquals(SAMPLE_ASSET_MODEL_NAME, result.getAssetName());
  }

  @Test
  void create_ThrowsIOException() throws IOException {
    when(genericStorage.create(SAMPLE_ASSET_MODEL_AS_JSON)).thenThrow(new IOException());

    assertThrows(IOException.class, () -> assetModelManagement.create(SAMPLE_ASSET_MODEL_AS_JSON));
  }


  @Test
  void update_ReturnsUpdatedAssetModel() throws IOException {
    var assetModelToUpdate = new SpAssetModel();
    assetModelToUpdate.setId(SAMPLE_ASSET_MODEL_ID);
    assetModelToUpdate.setAssetName(SAMPLE_ASSET_MODEL_NAME);

    when(genericStorage.update(any(), any())).thenReturn(SAMPLE_ASSET_MODEL_AS_MAP);

    var result = assetModelManagement.update(SAMPLE_ASSET_MODEL_ID, assetModelToUpdate);

    assertEquals(SAMPLE_ASSET_MODEL_ID, result.getId());
    assertEquals(SAMPLE_ASSET_MODEL_NAME, result.getAssetName());
  }

  @Test
  void update_GenericStorageThrowsIOException() throws IOException {
    when(genericStorage.update(any(), any())).thenThrow(new IOException());

    assertThrows(IOException.class, () -> assetModelManagement.update(SAMPLE_ASSET_MODEL_ID, new SpAssetModel()));
  }

  @Test
  void update_ReturnsUpdatedAssetModelFromJson() throws IOException {
    when(genericStorage.update(any(), any())).thenReturn(SAMPLE_ASSET_MODEL_AS_MAP);

    var result = assetModelManagement.update(SAMPLE_ASSET_MODEL_ID, SAMPLE_ASSET_MODEL_AS_JSON);

    assertEquals(SAMPLE_ASSET_MODEL_ID, result.getId());
    assertEquals(SAMPLE_ASSET_MODEL_NAME, result.getAssetName());
  }

  @Test
  void update_ThrowsIOExceptionWhenUpdatingFromJson() throws IOException {
    when(genericStorage.update(any(), any())).thenThrow(new IOException());

    assertThrows(
        IOException.class,
        () -> assetModelManagement.update(SAMPLE_ASSET_MODEL_ID, SAMPLE_ASSET_MODEL_AS_JSON)
    );
  }

  @Test
  void delete_RemovesAssetModel() throws IOException {
    doNothing().when(genericStorage)
               .delete(SAMPLE_ASSET_MODEL_ID, REV);

    assetModelManagement.delete(SAMPLE_ASSET_MODEL_ID, REV);

    verify(genericStorage, times(1)).delete(SAMPLE_ASSET_MODEL_ID, REV);
  }

  @Test
  void delete_ThrowsIOException() throws IOException {
    doThrow(new IOException()).when(genericStorage)
                              .delete(SAMPLE_ASSET_MODEL_ID, REV);

    assertThrows(IOException.class, () -> assetModelManagement.delete(SAMPLE_ASSET_MODEL_ID, REV));
  }


}