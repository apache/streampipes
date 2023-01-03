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

import org.apache.streampipes.model.dashboard.DashboardWidgetModel;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.storage.api.IDashboardWidgetStorage;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v2/dashboard/widgets")
public class DashboardWidget extends AbstractRestResource {

  @GET
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAllDashboardWidgets() {
    return ok(getDashboardWidgetStorage().getAllDashboardWidgets());
  }

  @GET
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{widgetId}")
  public Response getDashboardWidget(@PathParam("widgetId") String widgetId) {
    return ok(getDashboardWidgetStorage().getDashboardWidget(widgetId));
  }

  @PUT
  @JacksonSerialized
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{widgetId}")
  public Response modifyDashboardWidget(DashboardWidgetModel dashboardWidgetModel) {
    getDashboardWidgetStorage().updateDashboardWidget(dashboardWidgetModel);
    return ok();
  }

  @DELETE
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{widgetId}")
  public Response deleteDashboardWidget(@PathParam("widgetId") String widgetId) {
    getDashboardWidgetStorage().deleteDashboardWidget(widgetId);
    return ok();
  }

  @POST
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createDashboardWidget(DashboardWidgetModel dashboardWidgetModel) {
    String widgetId = getDashboardWidgetStorage().storeDashboardWidget(dashboardWidgetModel);
    return ok(getDashboardWidgetStorage().getDashboardWidget(widgetId));
  }

  private IDashboardWidgetStorage getDashboardWidgetStorage() {
    return getNoSqlStorage().getDashboardWidgetStorage();
  }
}
