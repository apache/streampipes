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

import org.apache.streampipes.connect.api.exception.AdapterException;
import org.apache.streampipes.connect.container.master.management.AdapterTemplateMasterManagement;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterDescriptionList;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Deprecated
@Path("/v2/connect/master/adapters/template")
public class AdapterTemplateResource extends AbstractAdapterResource<AdapterTemplateMasterManagement> {

    private static final Logger LOG = LoggerFactory.getLogger(AdapterTemplateResource.class);

    public AdapterTemplateResource() {
        super(AdapterTemplateMasterManagement::new);
    }

    @POST
    @JacksonSerialized
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAdapterTemplate(AdapterDescription adapterDescription) {
        try {
            String adapterTemplateId = managementService.addAdapterTemplate(adapterDescription);
            LOG.info("User: " + getAuthenticatedUsername() + " added adapter as adapter template");

            return ok(Notifications.success(adapterTemplateId));
        } catch (AdapterException e) {
            LOG.error("Error while storing the adapter template", e);
            return ok(Notifications.error(e.getMessage()));
        }
    }

    @GET
    @JacksonSerialized
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAdapterTemplates() {
        try {
            AdapterDescriptionList result = managementService.getAllAdapterTemplates();

            return ok(result);
        } catch (AdapterException e) {
            LOG.error("Error while getting all adapter templates", e);
            return ok(Notifications.error(e.getMessage()));
        }

    }

    @DELETE
    @JacksonSerialized
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAdapter(@PathParam("id") String id) {

        try {
            managementService.deleteAdapterTemplates(id);
            return ok(true);
        } catch (AdapterException e) {
            LOG.error("Error while deleting adapter with id " + id, e);
            return fail();
        }
    }
}
