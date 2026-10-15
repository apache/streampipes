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

import org.apache.streampipes.model.favorites.FavoriteResourceType;
import org.apache.streampipes.model.favorites.FavoriteSummary;
import org.apache.streampipes.resource.management.FavoriteResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v2/favorites")
public class FavoriteResource extends AbstractAuthGuardedRestResource {

  private final FavoriteResourceManager manager;

  public FavoriteResource(SpResourceManager resources) {
    this.manager = resources.manageFavorites();
  }

  @GetMapping
  @PreAuthorize(AuthConstants.IS_AUTHENTICATED)
  @Operation(summary = "List the current user's favorites", tags = {"Favorites"})
  public ResponseEntity<List<FavoriteSummary>> list() {
    return ok(manager.list(getAuthentication()));
  }

  @PutMapping
  @PreAuthorize(AuthConstants.IS_AUTHENTICATED)
  @Operation(summary = "Favorite an accessible resource", tags = {"Favorites"})
  public ResponseEntity<FavoriteSummary> add(@RequestParam FavoriteResourceType resourceType,
                                             @RequestParam String resourceId) {
    try {
      return ok(manager.add(getAuthentication(), resourceType, resourceId));
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    } catch (NoSuchElementException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @DeleteMapping
  @PreAuthorize(AuthConstants.IS_AUTHENTICATED)
  @Operation(summary = "Remove a current user's favorite, including unavailable resources", tags = {"Favorites"})
  public ResponseEntity<Void> remove(@RequestParam FavoriteResourceType resourceType,
                                    @RequestParam String resourceId) {
    try {
      manager.remove(getAuthentication(), resourceType, resourceId);
      return ok();
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }
}

