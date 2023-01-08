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

import org.apache.streampipes.model.client.user.Privilege;
import org.apache.streampipes.resource.management.AbstractDashboardResourceManager;

import org.springframework.stereotype.Component;

import jakarta.ws.rs.Path;

@Path("/v2/dashboard/dashboards")
@Component
public class Dashboard extends AbstractDashboardResource {

  @Override
  protected AbstractDashboardResourceManager getResourceManager() {
    return getSpResourceManager().manageDashboards();
  }

  @Override
  public boolean hasReadAuthority() {
    return isAdminOrHasAnyAuthority(Privilege.Constants.PRIVILEGE_READ_DASHBOARD_VALUE);
  }

  @Override
  public boolean hasWriteAuthority() {
    return isAdminOrHasAnyAuthority(Privilege.Constants.PRIVILEGE_WRITE_DASHBOARD_VALUE);
  }

  @Override
  public boolean hasDeleteAuthority() {
    return isAdminOrHasAnyAuthority(Privilege.Constants.PRIVILEGE_DELETE_DASHBOARD_VALUE);
  }
}
