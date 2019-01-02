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
import org.streampipes.connect.management.AdapterDeserializer;
import org.streampipes.connect.management.master.AdapterMasterManagement;
import org.streampipes.connect.management.master.Utils;
import org.streampipes.connect.rest.AbstractContainerResource;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.AdapterDescriptionList;
import org.streampipes.rest.shared.annotation.GsonWithIds;
import org.streampipes.rest.shared.annotation.JsonLdSerialized;
import org.streampipes.rest.shared.util.SpMediaType;
import org.streampipes.storage.couchdb.impl.AdapterStorageImpl;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/v1/{username}/master/adapters")
public class AdapterResource extends AbstractContainerResource {

    private Logger logger = LoggerFactory.getLogger(AdapterResource.class);

    private AdapterMasterManagement adapterMasterManagement;

    private String connectContainerEndpoint;

    public AdapterResource() {
        this.adapterMasterManagement = new AdapterMasterManagement();
        this.connectContainerEndpoint = ConnectContainerConfig.INSTANCE.getConnectContainerWorkerUrl();
    }

    public AdapterResource(String connectContainerEndpoint) {
        this.adapterMasterManagement = new AdapterMasterManagement();
        this.connectContainerEndpoint = connectContainerEndpoint;
    }

    @POST
//    @JsonLdSerialized
    @Path("/")
    @GsonWithIds
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAdapter(String s, @PathParam("username") String userName) {

        AdapterDescription adapterDescription = null;
        String adapterId;

        try {
            adapterDescription = AdapterDeserializer.getAdapterDescription(s);
        } catch (AdapterException e) {
            logger.error("Could not deserialize AdapterDescription: " + s, e);
            e.printStackTrace();
        }

        logger.info("User: " + userName + " starts adapter " + adapterDescription.getAdapterId());

        String newUrl = Utils.addUserNameToApi(connectContainerEndpoint, userName);

        try {
            adapterId = adapterMasterManagement.addAdapter(adapterDescription, newUrl, new
                    AdapterStorageImpl(), userName);
        } catch (AdapterException e) {
            logger.error("Error while starting adapter with id " + adapterDescription.getAppId(), e);
            return ok(Notifications.error(e.getMessage()));
        }

        logger.info("Stream adapter with id " + adapterId + " successfully added");
        return ok(Notifications.success(adapterId));
    }

    @GET
    @JsonLdSerialized
    @Path("/{id}")
    @Produces(SpMediaType.JSONLD)
    public Response getAdapter(@PathParam("id") String id, @PathParam("username") String userName) {

        try {
            AdapterDescription adapterDescription = adapterMasterManagement.getAdapter(id, new AdapterStorageImpl());

            return ok(adapterDescription);
        } catch (AdapterException e) {
            logger.error("Error while getting adapter with id " + id, e);
            return fail();
        }

    }

    @DELETE
    @JsonLdSerialized
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAdapter(@PathParam("id") String id, @PathParam("username") String userName) {

        try {

            String newUrl = Utils.addUserNameToApi(connectContainerEndpoint, userName);
            adapterMasterManagement.deleteAdapter(id, newUrl);
            return ok(true);
        } catch (AdapterException e) {
            logger.error("Error while deleting adapter with id " + id, e);
            return fail();
        }
    }

    @GET
    @JsonLdSerialized
    @Path("/")
    @Produces(SpMediaType.JSONLD)
    public Response getAllAdapters(String id, @PathParam("username") String userName) {
        try {
            List<AdapterDescription> allAdapterDescription = adapterMasterManagement.getAllAdapters(new AdapterStorageImpl());
            AdapterDescriptionList result = new AdapterDescriptionList();
            result.setList(allAdapterDescription);

            return ok(result);
        } catch (AdapterException e) {
            logger.error("Error while getting all adapters", e);
            return fail();
        }

    }

    public void setAdapterMasterManagement(AdapterMasterManagement adapterMasterManagement) {
        this.adapterMasterManagement = adapterMasterManagement;
    }

}
