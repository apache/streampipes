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

package org.apache.streampipes.rest.impl.connect;

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.connect.api.exception.AdapterException;
import org.apache.streampipes.connect.container.master.management.SourcesManagement;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.message.Notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/connect/master/sources")
public class SourcesResource extends AbstractAdapterResource<SourcesManagement> {

  private static final Logger LOG = LoggerFactory.getLogger(SourcesResource.class);

  public SourcesResource() {
    super(SourcesManagement::new);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response addSetAdapter(SpDataSet dataSet) {

    String responseMessage = "Instance of data set " + dataSet.getElementId() + " successfully started";

    try {
      managementService.addSetAdapter(dataSet);
    } catch (AdapterException | NoServiceEndpointsAvailableException e) {
      LOG.error("Could not set data set instance: " + dataSet.getElementId(), e);
      return ok(Notifications.error("Could not set data set instance: " + dataSet.getElementId()));
    }

    return ok(Notifications.success(responseMessage));
  }

  @DELETE
  @Path("{adapterId}/{runningInstanceId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response detach(@PathParam("adapterId") String elementId,
                         @PathParam("runningInstanceId") String runningInstanceId) {
    String responseMessage = "Instance id: " + runningInstanceId + " successfully started";

    try {
      managementService.detachAdapter(elementId, runningInstanceId);
    } catch (AdapterException | NoServiceEndpointsAvailableException e) {
      LOG.error("Could not detach instance id: " + runningInstanceId, e);
      return fail();
    }

    return ok(Notifications.success(responseMessage));
  }
}
