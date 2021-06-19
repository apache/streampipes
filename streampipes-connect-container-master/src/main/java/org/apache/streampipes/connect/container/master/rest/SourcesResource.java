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

package org.apache.streampipes.connect.container.master.rest;

import org.apache.streampipes.connect.api.exception.AdapterException;
import org.apache.streampipes.connect.container.master.management.SourcesManagement;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.shared.annotation.GsonWithIds;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/connect/{username}/master/sources")
public class SourcesResource extends AbstractAdapterResource<SourcesManagement> {

    private static final Logger LOG = LoggerFactory.getLogger(SourcesResource.class);

    public SourcesResource() {
        super(SourcesManagement::new);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    public Response getAllAdaptersInstallDescription(@PathParam("username") String username) {

        try {
            String resultingJson = managementService.getAllAdaptersInstallDescription(username);
            return ok(resultingJson);
        } catch (AdapterException e) {
            LOG.error("Error while getting all adapter descriptions", e);
            return fail();
        }
    }

    @GET
    @Path("/{id}")
    @JacksonSerialized
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdapterDataSource(@PathParam("id") String id) {
        try {
            return ok(managementService.getAdapterDataStream(id));
        } catch (AdapterException e) {
            LOG.error("Error while retrieving DataSourceDescription with id: " + id);
            return fail();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{streamId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAdapter(@PathParam("streamId") String elementId,
                               @PathParam("username") String username,
                               SpDataSet dataSet) {

        String responseMessage = "Instance of data set " + dataSet.getUri() + " successfully started";

        try {
            managementService.addAdapter(elementId,  dataSet, username);
        } catch (AdapterException e) {
            LOG.error("Could not set data set instance: " + dataSet.getUri(), e);
            return ok(Notifications.error("Could not set data set instance: " + dataSet.getUri()));
        }

        return ok(Notifications.success(responseMessage));
    }

    @DELETE
    @Path("/{streamId}/{runningInstanceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detach(@PathParam("streamId") String elementId, @PathParam("runningInstanceId") String runningInstanceId, @PathParam("username") String username) {
        String responseMessage = "Instance of set id: " + elementId  + " with instance id: "+ runningInstanceId + " successfully started";

//        String workerUrl = new Utils().getWorkerUrlById(elementId);
//        String newUrl = Utils.addUserNameToApi(workerUrl, username);

        try {
            managementService.detachAdapter(elementId, runningInstanceId, username);
        } catch (AdapterException e) {
            LOG.error("Could not set set id "+ elementId  + " with instance id: "+ runningInstanceId, e);
            return fail();
        }

        return ok(Notifications.success(responseMessage));
    }
}
