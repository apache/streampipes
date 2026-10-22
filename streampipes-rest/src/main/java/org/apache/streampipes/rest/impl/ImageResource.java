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
package org.apache.streampipes.rest.impl;

import org.apache.streampipes.manager.file.ImageManager;
import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.storage.api.system.IAssetStorage;
import org.apache.streampipes.storage.api.system.IImageMetadataStorage;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v2/images")
public class ImageResource extends AbstractAuthGuardedRestResource {

  private final ImageManager imageManager;

  public ImageResource(ISpCoreConfigurationStorage configurationStorage,
                       IImageMetadataStorage imageStorage,
                       IAssetStorage assetStorage) {
    this.imageManager = new ImageManager(configurationStorage, imageStorage, assetStorage);
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Upload a JPEG or PNG image to the image storage.", tags = {"Core", "Images"})
  @PreAuthorize(AuthConstants.HAS_WRITE_ASSETS_PRIVILEGE)
  public ResponseEntity<?> uploadImage(@RequestPart("image_upload") MultipartFile image) throws IOException {
    try (var content = image.getInputStream()) {
      return ok(imageManager.storeImage(getAuthenticatedUsername(), image.getOriginalFilename(), content));
    } catch (IllegalArgumentException e) {
      return badRequest(Notifications.error(e.getMessage()));
    }
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "List uploaded image metadata.", tags = {"Core", "Images"})
  @PreAuthorize(AuthConstants.HAS_READ_ASSETS_PRIVILEGE)
  public ResponseEntity<List<FileMetadata>> getImages() {
    return ok(imageManager.getAllImages());
  }

  @GetMapping(path = "/{imageId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  @Operation(summary = "Get image content by its ID.", tags = {"Core", "Images"})
  @PreAuthorize(AuthConstants.HAS_READ_ASSETS_PRIVILEGE)
  public ResponseEntity<byte[]> getImage(@PathVariable String imageId) {
    try {
      return ok(Files.readAllBytes(imageManager.getImage(imageId).toPath()));
    } catch (IOException e) {
      throw new ResponseStatusException(NOT_FOUND, "Image not found", e);
    }
  }

  @DeleteMapping(path = "/{imageId}")
  @Operation(summary = "Delete an image that is no longer linked to an asset.", tags = {"Core", "Images"})
  @PreAuthorize(AuthConstants.HAS_WRITE_ASSETS_PRIVILEGE)
  public ResponseEntity<Void> deleteImage(@PathVariable String imageId) throws IOException {
    if (!imageManager.deleteImageIfUnused(imageId)) {
      throw new ResponseStatusException(CONFLICT, "Image is still linked to an asset");
    }
    return ok();
  }

  @GetMapping(path = "/{imageId}/in-use", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Check whether an asset references an image.", tags = {"Core", "Images"})
  @PreAuthorize(AuthConstants.HAS_READ_ASSETS_PRIVILEGE)
  public ResponseEntity<Boolean> isImageInUse(@PathVariable String imageId) {
    return ok(imageManager.isImageInUse(imageId));
  }
}
