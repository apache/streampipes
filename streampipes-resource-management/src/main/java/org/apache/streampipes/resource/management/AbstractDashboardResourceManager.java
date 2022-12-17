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
package org.apache.streampipes.resource.management;

import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.model.util.ElementIdGenerator;
import org.apache.streampipes.storage.api.IDashboardStorage;

import java.util.List;

public abstract class AbstractDashboardResourceManager extends AbstractResourceManager<IDashboardStorage> {

  public AbstractDashboardResourceManager(IDashboardStorage db) {
    super(db);
  }

  public List<DashboardModel> findAll() {
    return db.getAllDashboards();
  }

  public DashboardModel find(String dashboardId) {
    return db.getDashboard(dashboardId);
  }

  public void delete(String dashboardId) {
    db.deleteDashboard(dashboardId);
    deletePermissions(dashboardId);
  }

  public void create(DashboardModel dashboardModel, String principalSid) {
    if (dashboardModel.getCouchDbId() == null) {
      dashboardModel.setCouchDbId(ElementIdGenerator.makeElementId(DashboardModel.class));
    }
    db.storeDashboard(dashboardModel);
    new PermissionResourceManager().createDefault(dashboardModel.getCouchDbId(), DashboardModel.class, principalSid,
        false);
  }

  public void update(DashboardModel dashboardModel) {
    db.updateDashboard(dashboardModel);
  }

  private void deletePermissions(String dashboardId) {
    PermissionResourceManager manager = new PermissionResourceManager();
    List<Permission> permissions = manager.findForObjectId(dashboardId);
    permissions.forEach(manager::delete);
  }
}
