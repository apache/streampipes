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

import org.apache.streampipes.assetmodel.management.AssetModelManagement;
import org.apache.streampipes.model.assets.SpAssetModel;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v2/assets")
public class AssetManagementResource extends AbstractAuthGuardedRestResource {

  private static final Logger LOG = LoggerFactory.getLogger(AssetManagementResource.class);

  private final AssetModelManagement assetModelManagement;

  public AssetManagementResource() {
    var genericStorage = StorageDispatcher.INSTANCE.getNoSqlStore()
                                                   .getGenericStorage();
    assetModelManagement = new AssetModelManagement(genericStorage);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_READ_ASSETS_PRIVILEGE)
  public List<SpAssetModel> getAll() throws IOException {
    return assetModelManagement.findAll();
  }

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize(AuthConstants.HAS_WRITE_ASSETS_PRIVILEGE)
  public ResponseEntity<?> create(@RequestBody String assetModel) {
    try {
      var updatedAssetModel = assetModelManagement.create(assetModel);
      return ok(updatedAssetModel);
    } catch (IOException e) {
      LOG.error("Could not connect to storage", e);
      return fail();
    }
  }

  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_READ_ASSETS_PRIVILEGE)
  public ResponseEntity<SpAssetModel> getCategory(@PathVariable("id") String assetId) {
    try {
      var assetModel = assetModelManagement.findOne(assetId);
      return ok(assetModel);
    } catch (NoSuchElementException e) {
      LOG.error("Asset model not found", e);
      throw new SpMessageException(HttpStatus.NOT_FOUND, e);
    } catch (IOException e) {
      LOG.error("Could not connect to storage", e);
      throw new SpMessageException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @PutMapping(
      path = "/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_WRITE_ASSETS_PRIVILEGE)
  public ResponseEntity<SpAssetModel> update(
      @PathVariable("id") String assetId,
      @RequestBody String assetModel
  ) {
    try {
      var updatedAssetModel = assetModelManagement.update(assetId, assetModel);
      return ok(updatedAssetModel);
    } catch (IOException e) {
      LOG.error("Could not connect to storage", e);
      throw new SpMessageException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @DeleteMapping(path = "/{id}/{rev}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_WRITE_ASSETS_PRIVILEGE)
  public ResponseEntity<Void> delete(
      @PathVariable("id") String assetId,
      @PathVariable("rev") String rev
  ) {
    try {
      assetModelManagement.delete(assetId, rev);
      return ok();
    } catch (IOException e) {
      LOG.error("Could not connect to storage", e);
      throw new SpMessageException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

}
