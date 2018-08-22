/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.connect.rest.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.config.ConnectContainerConfig;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.management.master.SourcesManagement;
import org.streampipes.connect.rest.AbstractContainerResource;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.rest.shared.annotation.JsonLdSerialized;
import org.streampipes.rest.shared.util.SpMediaType;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/v1/{username}/master/sources")
public class SourcesResource extends AbstractContainerResource {

    private Logger logger = LoggerFactory.getLogger(SourcesResource.class);

    private String connectContainerBaseUrl;

    private SourcesManagement sourcesManagement;

    public SourcesResource() {
        this.connectContainerBaseUrl = ConnectContainerConfig.INSTANCE.getConnectContainerUrl();
        this.sourcesManagement = new SourcesManagement();
    }

    public SourcesResource(String connectContainerBaseUrl) {
        this.connectContainerBaseUrl = connectContainerBaseUrl;
    }

    @POST
    @JsonLdSerialized
    @Consumes(SpMediaType.JSONLD)
    @Path("/{streamId}/streams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAdapter(@PathParam("streamId") String elementId, SpDataSet dataSet) {

        String responseMessage = "Instance of data set " + dataSet.getUri() + " successfully started";

        try {
            this.sourcesManagement.addAdapter(elementId, this.connectContainerBaseUrl, dataSet);
        } catch (AdapterException e) {
            logger.error("Could not set data set instance: " + dataSet.getUri(), e);
            return ok(Notifications.error("Could not set data set instance: " + dataSet.getUri()));
        }


        return ok(Notifications.success(responseMessage));
    }

    @DELETE
    @Path("/{streamId}/streams/{runningInstanceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detach(@PathParam("streamId") String elementId, @PathParam("runningInstanceId") String runningInstanceId) {
        String responseMessage = "Instance of set id: " + elementId  + " with instance id: "+ runningInstanceId + " successfully started";

        try {
            this.sourcesManagement.detachAdapter(this.connectContainerBaseUrl, elementId, runningInstanceId);
        } catch (AdapterException e) {
            logger.error("Could not set set id "+ elementId  + " with instance id: "+ runningInstanceId, e);
            return fail();
        }


        return ok(Notifications.success(responseMessage));
    }

    public void setSourcesManagement(SourcesManagement sourcesManagement) {
        this.sourcesManagement = sourcesManagement;
    }
}
