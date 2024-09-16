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

package org.apache.streampipes.rest.impl.dashboard;

import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.resource.management.AbstractCRUDResourceManager;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;

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

import java.util.List;

public abstract class AbstractDashboardResource extends AbstractAuthGuardedRestResource {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasReadAuthority()")
  @PostFilter("hasPermission(filterObject.couchDbId, 'READ')")
  public List<DashboardModel> getAllDashboards() {
    return getResourceManager().findAll();
  }

  @GetMapping(path = "/{dashboardId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasReadAuthority() and hasPermission(#dashboardId, 'READ')")
  public DashboardModel getDashboard(@PathVariable("dashboardId") String dashboardId) {
    return getResourceManager().find(dashboardId);
  }

  @PutMapping(path = "/{dashboardId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasWriteAuthority() and hasPermission(#dashboardModel.couchDbId, 'WRITE')")
  public ResponseEntity<DashboardModel> modifyDashboard(@RequestBody DashboardModel dashboardModel) {
    getResourceManager().update(dashboardModel);
    return ok(getResourceManager().find(dashboardModel.getElementId()));
  }

  @DeleteMapping(path = "/{dashboardId}")
  @PreAuthorize("this.hasWriteAuthority() and hasPermission(#dashboardId, 'WRITE')")
  public ResponseEntity<Void> deleteDashboard(@PathVariable("dashboardId") String dashboardId) {
    getResourceManager().delete(dashboardId);
    return ok();
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasWriteAuthority()")
  public ResponseEntity<Void> createDashboard(@RequestBody DashboardModel dashboardModel) {
    getResourceManager().create(dashboardModel, getAuthenticatedUserSid());
    return ok();
  }

  protected abstract AbstractCRUDResourceManager<DashboardModel> getResourceManager();

  /**
   * Do not delete these abstract methods below - required by Spring SPEL (see above)
   */

  public abstract boolean hasReadAuthority();

  public abstract boolean hasWriteAuthority();

}
