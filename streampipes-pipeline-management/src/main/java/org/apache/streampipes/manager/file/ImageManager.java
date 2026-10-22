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

import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.sdk.helpers.Filetypes;
import org.apache.streampipes.storage.api.system.IAssetStorage;
import org.apache.streampipes.storage.api.system.IImageMetadataStorage;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;

public class ImageManager {

  private final IImageMetadataStorage imageStorage;
  private final IAssetStorage assetStorage;
  private final FileHandler imageHandler;

  public ImageManager(ISpCoreConfigurationStorage configurationStorage,
                      IImageMetadataStorage imageStorage,
                      IAssetStorage assetStorage) {
    this.imageStorage = imageStorage;
    this.assetStorage = assetStorage;
    this.imageHandler = new FileHandler(configurationStorage, "images");
  }

  public List<FileMetadata> getAllImages() {
    return imageStorage.findAll();
  }

  public FileMetadata storeImage(String user, String filename, InputStream content) throws IOException {
    var extension = filename == null ? "" : filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    if (Stream.of(Filetypes.JPG, Filetypes.PNG)
        .noneMatch(type -> type.getFileExtensions().contains(extension))) {
      throw new IllegalArgumentException("Only JPEG, JPG and PNG images are supported");
    }

    var metadata = new FileMetadata();
    metadata.setFileId(UUID.randomUUID().toString());
    metadata.setFilename(filename.replaceAll("[^a-zA-Z0-9.\\-]", "_"));
    metadata.setFiletype(extension);
    metadata.setCreatedAt(System.currentTimeMillis());
    metadata.setCreatedByUser(user);

    imageHandler.storeFile(metadata.getFileId(), content);
    try {
      persist(metadata);
      return imageStorage.getElementById(metadata.getFileId());
    } catch (RuntimeException e) {
      Files.deleteIfExists(imageHandler.getFile(metadata.getFileId()).toPath());
      throw e;
    }
  }

  public File getImage(String imageId) throws IOException {
    var metadata = imageStorage.getElementById(imageId);
    if (metadata == null) {
      throw new java.io.FileNotFoundException("Image not found");
    }
    return imageHandler.getFile(metadata.getFileId());
  }

  public boolean deleteImageIfUnused(String imageId) throws IOException {
    if (isImageInUse(imageId)) {
      return false;
    }
    var metadata = imageStorage.getElementById(imageId);
    if (metadata != null) {
      Files.deleteIfExists(imageHandler.getFile(metadata.getFileId()).toPath());
      imageStorage.deleteElementById(imageId);
    }
    return true;
  }

  public boolean isImageInUse(String imageId) {
    return assetStorage.isImageReferenced(imageId);
  }

  private void persist(FileMetadata metadata) {
    var result = imageStorage.persist(metadata);
    if (!result.k) {
      throw new IllegalStateException("Could not store image metadata");
    }
  }
}
