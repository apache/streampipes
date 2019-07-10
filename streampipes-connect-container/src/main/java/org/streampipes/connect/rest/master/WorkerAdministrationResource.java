/*
 * Copyright 2019 FZI Forschungszentrum Informatik
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
import org.streampipes.connect.management.master.WorkerAdministrationManagement;
import org.streampipes.connect.rest.AbstractContainerResource;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.streampipes.rest.shared.annotation.GsonWithIds;
import org.streampipes.rest.shared.util.JsonLdUtils;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/v1/{username}/master/workercontainer")
public class WorkerAdministrationResource extends AbstractContainerResource {

    private Logger LOG = LoggerFactory.getLogger(WorkerAdministrationResource.class);

    private WorkerAdministrationManagement workerAdministrationManagement;

    public WorkerAdministrationResource() {
        this.workerAdministrationManagement = new WorkerAdministrationManagement();
    }

    @POST
    @GsonWithIds
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAdapter(String connectWorkerContainerString) {
        ConnectWorkerContainer connectWorkerContainer = JsonLdUtils.fromJsonLd(connectWorkerContainerString, ConnectWorkerContainer.class);
        this.workerAdministrationManagement.register(connectWorkerContainer);

        LOG.info("Worker container: " + connectWorkerContainer.getEndpointUrl() + " was registered");

        return ok(Notifications.success("Worker Container sucessfully added"));
    }

}
