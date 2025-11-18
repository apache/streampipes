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

import org.apache.streampipes.model.assets.SpAssetModel;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.List;

@RestController
@RequestMapping("/api/v2/assets")
public class AssetManagementResource extends AbstractAuthGuardedRestResource {

  private static final Logger LOG = LoggerFactory.getLogger(AssetManagementResource.class);

  private final CRUDStorage<SpAssetModel> assetStorage;

  public AssetManagementResource() {
    this.assetStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getAssetStorage();
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_READ_ASSETS_PRIVILEGE)
  public List<SpAssetModel> getAll() {
    return assetStorage.findAll();
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_WRITE_ASSETS_PRIVILEGE)
  public ResponseEntity<?> create(@RequestBody SpAssetModel asset) {
    assetStorage.persist(asset);
    return ok();
  }

  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_READ_ASSETS_PRIVILEGE)
  public ResponseEntity<SpAssetModel> getAsset(@PathVariable("id") String elementId) {
      var obj = assetStorage.getElementById(elementId);
      if (obj != null) {
        return ok(obj);
      } else {
        return null;
      }
  }

  @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_WRITE_ASSETS_PRIVILEGE)
  public ResponseEntity<SpAssetModel> update(@PathVariable("id") String assetId,
      @RequestBody SpAssetModel asset) {
    var obj = assetStorage.updateElement(asset);
    return ok(obj);
  }

  @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_WRITE_ASSETS_PRIVILEGE)
  public ResponseEntity<Void> delete(@PathVariable("id") String assetId) {
    var asset = assetStorage.getElementById(assetId);
    assetStorage.deleteElement(asset);
    return ok();
  }
}
