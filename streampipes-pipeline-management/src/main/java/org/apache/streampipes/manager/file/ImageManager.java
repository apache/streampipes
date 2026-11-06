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
import org.apache.streampipes.sdk.helpers.Filetypes;
import org.apache.streampipes.storage.api.system.IAssetStorage;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;

public class ImageManager {

  private final IAssetStorage assetStorage;
  private final FileHandler imageHandler;

  public ImageManager(ISpCoreConfigurationStorage configurationStorage, IAssetStorage assetStorage) {
    this.assetStorage = assetStorage;
    this.imageHandler = new FileHandler(configurationStorage, "images");
  }

  public SpAssetModel storeImage(String elementId, String assetId, String filename, InputStream content)
      throws IOException {
    var extension = filename == null ? "" : filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    if (Stream.of(Filetypes.JPG, Filetypes.PNG)
        .noneMatch(type -> type.getFileExtensions().contains(extension))) {
      throw new IllegalArgumentException("Only JPEG, JPG and PNG images are supported");
    }
    var model = requireModel(elementId);
    var asset = requireAsset(model, assetId);
    if (asset.getImageId() != null) {
      throw new IllegalArgumentException("Delete the current asset image before uploading its replacement");
    }
    var imageId = UUID.randomUUID().toString();
    imageHandler.storeFile(imageId, content);
    asset.setImageId(imageId);
    asset.setFiletype(extension);
    try {
      assetStorage.updateElement(model);
    } catch (RuntimeException e) {
      Files.deleteIfExists(imageHandler.getFile(imageId).toPath());
      throw e;
    }
    return requireModel(elementId);
  }

  public File getImage(String elementId, String assetId, String imageId) throws IOException {
    requireImage(requireModel(elementId), assetId, imageId);
    return imageHandler.getFile(imageId);
  }

  public SpAssetModel deleteImage(String elementId, String assetId, String imageId) throws IOException {
    var model = requireModel(elementId);
    var asset = requireImage(model, assetId, imageId);
    asset.setImageId(null);
    asset.setFiletype(null);
    assetStorage.updateElement(model);
    Files.deleteIfExists(imageHandler.getFile(imageId).toPath());
    return requireModel(elementId);
  }

  public void deleteAssetImages(SpAsset asset) throws IOException {
    if (asset == null) {
      return;
    }
    if (asset.getImageId() != null) {
      UUID.fromString(asset.getImageId());
      Files.deleteIfExists(imageHandler.getFile(asset.getImageId()).toPath());
    }
    if (asset.getAssets() != null) {
      for (var child : asset.getAssets()) {
        deleteAssetImages(child);
      }
    }
  }

  private SpAssetModel requireModel(String elementId) throws FileNotFoundException {
    var model = assetStorage.getElementById(elementId);
    if (model == null) {
      throw new FileNotFoundException("Asset not found");
    }
    return model;
  }

  private SpAsset requireAsset(SpAsset model, String assetId) throws FileNotFoundException {
    var asset = findAsset(model, assetId);
    if (asset == null) {
      throw new FileNotFoundException("Asset not found");
    }
    return asset;
  }

  private SpAsset findAsset(SpAsset asset, String assetId) {
    if (assetId != null && assetId.equals(asset.getAssetId())) {
      return asset;
    }
    if (asset.getAssets() != null) {
      for (var child : asset.getAssets()) {
        var match = findAsset(child, assetId);
        if (match != null) {
          return match;
        }
      }
    }
    return null;
  }

  private SpAsset requireImage(SpAssetModel model, String assetId, String imageId) throws FileNotFoundException {
    var asset = requireAsset(model, assetId);
    if (imageId == null || !imageId.equals(asset.getImageId())) {
      throw new FileNotFoundException("Image not found");
    }
    UUID.fromString(imageId);
    return asset;
  }
}
