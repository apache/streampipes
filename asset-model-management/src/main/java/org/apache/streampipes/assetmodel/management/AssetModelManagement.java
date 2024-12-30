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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class provides convinience methods to work with asset models
 */
public class AssetModelManagement {

  private final IGenericStorage genericStorage;
  private final ObjectMapper objectMapper;

  public AssetModelManagement(IGenericStorage genericStorage) {
    this.genericStorage = genericStorage;
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Retrieves all asset models from generic storage and converts them to a list of asset models.
   *
   * @return a list of asset models
   */
  public List<SpAssetModel> findAll() throws IOException {

    try {
      return genericStorage.findAll(SpAssetModel.APP_DOC_TYPE)
                           .stream()
                           .map(this::convertMapToAssetModel)
                           .toList();
    } catch (IOException e) {
      throw new IOException("Error while fetching all asset models from generic storage.", e);
    }
  }


  /**
   * Retrieves a single asset model by its ID.
   *
   * @param assetId the ID of the asset model to retrieve
   * @return the asset model
   * @throws IOException if an I/O error occurs
   */
  public SpAssetModel findOne(String assetId) throws NoSuchElementException, IOException {
    var assetModelData = genericStorage.findOne(assetId);

    if (assetModelData == null) {
      throw new NoSuchElementException("Asset model with ID " + assetId + " not found.");
    }

    return this.convertMapToAssetModel(assetModelData);
  }

  /**
   * Creates a new asset model.
   *
   * @param asset the asset model to create
   * @return the created asset model
   * @throws IOException if an I/O error occurs
   */
  public SpAssetModel create(String asset) throws IOException {
    var assetModelInstanceInDatabase = genericStorage.create(asset);

    return this.convertMapToAssetModel(assetModelInstanceInDatabase);
  }

  /**
   *
   * @param assetId    the ID of the asset model to update
   * @param assetModel the updated asset model
   * @return the updated asset model
   * @throws IOException if an I/O error occurs
   */
  public SpAssetModel update(String assetId, SpAssetModel assetModel) throws IOException {
    var assetModelAsJson =  this.convertAssetModelToJson(assetModel);
    return update(assetId, assetModelAsJson);
  }

  /**
   * Updates an existing asset model.
   *
   * @param assetId    the ID of the asset model to update
   * @param assetModelJson the updated asset model as a JSON string
   * @return the updated asset model
   * @throws IOException if an I/O error occurs
   */
  public SpAssetModel update(String assetId, String assetModelJson) throws IOException {
    var updatedAssetModelAsMap = genericStorage.update(assetId, assetModelJson);
    return this.convertMapToAssetModel(updatedAssetModelAsMap);
  }

  /**
   * Deletes an asset model by its ID and revision.
   *
   * @param assetId the ID of the asset model to delete
   * @param rev     the revision of the asset model to delete
   * @throws IOException if an I/O error occurs
   */
  public void delete(String assetId, String rev) throws IOException {
    genericStorage.delete(assetId, rev);
  }

  private SpAssetModel convertMapToAssetModel(Map<String, Object> assetModelMap) {
    return objectMapper.convertValue(assetModelMap, SpAssetModel.class);
  }

  private String convertAssetModelToJson(SpAssetModel assetModel) throws JsonProcessingException {
    return objectMapper.writeValueAsString(assetModel);
  }

}
