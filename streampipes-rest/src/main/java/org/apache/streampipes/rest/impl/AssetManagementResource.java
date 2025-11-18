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
import org.apache.streampipes.model.client.user.DefaultPrivilege;
import org.apache.streampipes.resource.management.CrudResourceManager;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v2/assets")
public class AssetManagementResource extends AbstractAuthGuardedRestResource {

  private final CrudResourceManager<SpAssetModel> resourceManager;

  public AssetManagementResource() {
    CRUDStorage<SpAssetModel> assetStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getAssetStorage();
    this.resourceManager = new CrudResourceManager<>(assetStorage, SpAssetModel.class);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_READ_ASSETS_PRIVILEGE)
  @PostFilter("hasPermission(filterObject.elementId, 'READ')")
  public List<SpAssetModel> getAll() {
    return resourceManager.findAll();
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_WRITE_ASSETS_PRIVILEGE)
  public ResponseEntity<?> create(@RequestBody SpAssetModel asset) {
    resourceManager.create(asset, getAuthenticatedUserSid());
    return ok();
  }

  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasReadAuthority() and hasPermission(#elementId, 'READ')")
  public ResponseEntity<SpAssetModel> getAsset(@PathVariable("id") String elementId) {
      var obj = resourceManager.find(elementId);
      if (obj != null) {
        return ok(obj);
      } else {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      }
  }

  @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_WRITE_ASSETS_PRIVILEGE + " and hasPermission(#elementId, 'WRITE')")
  public ResponseEntity<SpAssetModel> update(@PathVariable("id") String elementId,
      @RequestBody SpAssetModel asset) {
    if (elementId.equals(asset.getElementId())) {
      resourceManager.update(asset);
      return ok(resourceManager.find(elementId));
    } else {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
  }

  @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_WRITE_ASSETS_PRIVILEGE + " and hasPermission(#elementId, 'WRITE')")
  public ResponseEntity<Void> delete(@PathVariable("id") String elementId) {
    resourceManager.delete(elementId);
    return ok();
  }

  /**
   * required by Spring expression
   */
  public boolean hasReadAuthority() {
    return isAdminOrHasAnyAuthority(DefaultPrivilege.Constants.PRIVILEGE_READ_ASSETS_VALUE);
  }

  /**
   * required by Spring expression
   */
  public boolean hasWriteAuthority() {
    return isAdminOrHasAnyAuthority(DefaultPrivilege.Constants.PRIVILEGE_WRITE_ASSETS_VALUE);
  }
}
