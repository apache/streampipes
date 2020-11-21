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

import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.connect.container.master.management.WorkerAdministrationManagement;
import org.apache.streampipes.connect.container.master.management.WorkerRestClient;
import org.apache.streampipes.connect.rest.AbstractContainerResource;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.model.runtime.RuntimeOptionsResponse;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/connect/{username}/master/resolvable")
public class RuntimeResolvableResource extends AbstractContainerResource {

    private static final String SP_NS =  "https://streampipes.org/vocabulary/v1/";
    private WorkerAdministrationManagement workerAdministrationManagement;

    public RuntimeResolvableResource() {
        this.workerAdministrationManagement = new WorkerAdministrationManagement();
    }

    @POST
    @Path("{id}/configurations")
    @JacksonSerialized
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response fetchConfigurations(@PathParam("id") String elementId,
                                        @PathParam("username") String username,
                                        RuntimeOptionsRequest runtimeOptionsRequest) {

        // TODO add solution for formats
//        ResolvesContainerProvidedOptions runtimeResolvableOptions = RuntimeResovable.getRuntimeResolvableFormat(elementId);

        String id = elementId.replaceAll("sp:", SP_NS);
        String workerEndpoint = this.workerAdministrationManagement.getWorkerUrl(id);

        try {

            RuntimeOptionsResponse result = WorkerRestClient.getConfiguration(workerEndpoint, elementId, username, runtimeOptionsRequest);

            return ok(result);
        } catch (AdapterException e) {
            e.printStackTrace();
            return fail();
        }

    }

}
