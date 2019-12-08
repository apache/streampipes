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

package org.streampipes.connect.container.master.rest;

import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.container.master.management.WorkerAdministrationManagement;
import org.streampipes.connect.container.master.management.WorkerRestClient;
import org.streampipes.connect.rest.AbstractContainerResource;
import org.streampipes.model.runtime.RuntimeOptionsResponse;
import org.streampipes.rest.shared.annotation.JsonLdSerialized;
import org.streampipes.rest.shared.util.SpMediaType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/api/v1/{username}/master/resolvable")
public class RuntimeResolvableResource extends AbstractContainerResource {

    private static final String SP_NS =  "https://streampipes.org/vocabulary/v1/";
    private WorkerAdministrationManagement workerAdministrationManagement;

    public RuntimeResolvableResource() {
        this.workerAdministrationManagement = new WorkerAdministrationManagement();
    }

    @POST
    @Path("{id}/configurations")
    @JsonLdSerialized
    @Produces(SpMediaType.JSONLD)
    @Consumes(SpMediaType.JSONLD)
    public Response fetchConfigurations(@PathParam("id") String elementId,
                                        @PathParam("username") String username,
                                        String payload) {

        // TODO add solution for formats
//        ResolvesContainerProvidedOptions runtimeResolvableOptions = RuntimeResovable.getRuntimeResolvableFormat(elementId);

        String id = elementId.replaceAll("sp:", SP_NS);
        String workerEndpoint = this.workerAdministrationManagement.getWorkerUrl(id);

        try {

            RuntimeOptionsResponse result = WorkerRestClient.getConfiguration(workerEndpoint, elementId, username, payload);

            return ok(result);
        } catch (AdapterException e) {
            e.printStackTrace();
            return fail();
        }

    }

}
