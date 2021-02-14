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
import org.apache.streampipes.rest.impl.AbstractRestResource;
import org.apache.streampipes.storage.api.IDashboardStorage;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public abstract class AbstractDashboardResource extends AbstractRestResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAllDashboards() {
    return ok(getDashboardStorage().getAllDashboards());
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{dashboardId}")
  public Response getDashboard(@PathParam("dashboardId") String dashboardId) {
    return ok(getDashboardStorage().getDashboard(dashboardId));
  }

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{dashboardId}")
  public Response modifyDashboard(DashboardModel dashboardModel) {
    getDashboardStorage().updateDashboard(dashboardModel);
    return ok(getDashboardStorage().getDashboard(dashboardModel.getCouchDbId()));
  }

  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{dashboardId}")
  public Response deleteDashboard(@PathParam("dashboardId") String dashboardId) {
    getDashboardStorage().deleteDashboard(dashboardId);
    return ok();
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response createDashboard(DashboardModel dashboardModel) {
    getDashboardStorage().storeDashboard(dashboardModel);
    return ok();
  }

  protected abstract IDashboardStorage getDashboardStorage();

}
