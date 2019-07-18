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
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.management.AdapterDeserializer;
import org.streampipes.connect.management.master.AdapterTemplateMasterManagement;
import org.streampipes.connect.rest.AbstractContainerResource;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.AdapterDescriptionList;
import org.streampipes.rest.shared.annotation.GsonWithIds;
import org.streampipes.rest.shared.annotation.JsonLdSerialized;
import org.streampipes.rest.shared.util.SpMediaType;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/v1/{username}/master/adapters/template")
public class AdapterTemplateResource extends AbstractContainerResource {

    private Logger logger = LoggerFactory.getLogger(AdapterTemplateResource.class);

    private AdapterTemplateMasterManagement adapterTemplateMasterManagement;

    private String connectContainerEndpoint;

    public AdapterTemplateResource() {
        this.adapterTemplateMasterManagement = new AdapterTemplateMasterManagement();
    }

    @POST
//    @JsonLdSerialized
    @Path("/")
    @GsonWithIds
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAdapterTemplate(String s, @PathParam("username") String userName) {

        AdapterDescription adapterDescription = null;

        try {
            adapterDescription = AdapterDeserializer.getAdapterDescription(s);
        } catch (AdapterException e) {
            logger.error("Could not deserialize AdapterDescription: " + s, e);
            e.printStackTrace();
        }

        try {
            String adapterTemplateId = adapterTemplateMasterManagement.addAdapterTemplate(adapterDescription);
            logger.info("User: " + userName + " added adapter as adapter template");

            return ok(Notifications.success(adapterTemplateId));
        } catch (AdapterException e) {
            logger.error("Error while storing the adapter template", e);
            return ok(Notifications.error(e.getMessage()));
        }


    }

    @GET
    @JsonLdSerialized
    @Path("/all")
    @Produces(SpMediaType.JSONLD)
    public Response getAllAdapterTemplates(String id, @PathParam("username") String userName) {
        try {
            AdapterDescriptionList result = adapterTemplateMasterManagement.getAllAdapterTemplates();

            return ok(result);
        } catch (AdapterException e) {
            logger.error("Error while getting all adapter templates", e);
            return ok(Notifications.error(e.getMessage()));
        }

    }


    @DELETE
    @JsonLdSerialized
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAdapter(@PathParam("id") String id, @PathParam("username") String userName) {

        try {
            adapterTemplateMasterManagement.deleteAdapterTemplates(id);
            return ok(true);
        } catch (AdapterException e) {
            logger.error("Error while deleting adapter with id " + id, e);
            return fail();
        }
    }

    public void setAdapterTemplateMasterManagement(AdapterTemplateMasterManagement adapterTemplateMasterManagement) {
        this.adapterTemplateMasterManagement = adapterTemplateMasterManagement;
    }
}
