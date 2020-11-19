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
import org.apache.streampipes.connect.container.master.management.AdapterMasterManagement;
import org.apache.streampipes.connect.container.master.management.Utils;
import org.apache.streampipes.connect.rest.AbstractContainerResource;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterDescriptionList;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.storage.couchdb.impl.AdapterStorageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/connect/v1/{username}/master/adapters")
public class AdapterResource extends AbstractContainerResource {

    private Logger LOG = LoggerFactory.getLogger(AdapterResource.class);

    private AdapterMasterManagement adapterMasterManagement;


    public AdapterResource() {
        this.adapterMasterManagement = new AdapterMasterManagement();
    }

    @POST
    @JacksonSerialized
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAdapter(AdapterDescription adapterDescription, @PathParam("username") String userName) {

        String adapterId;
        LOG.info("User: " + userName + " starts adapter " + adapterDescription.getAdapterId());


        String workerUrl = new Utils().getWorkerUrl(adapterDescription);
        String newUrl = Utils.addUserNameToApi(workerUrl, userName);

        try {
            adapterId = adapterMasterManagement.addAdapter(adapterDescription, newUrl, new
                    AdapterStorageImpl(), userName);
        } catch (AdapterException e) {
            LOG.error("Error while starting adapter with id " + adapterDescription.getAppId(), e);
            return ok(Notifications.error(e.getMessage()));
        }

        LOG.info("Stream adapter with id " + adapterId + " successfully added");
        return ok(Notifications.success(adapterId));
    }

    @GET
    @JacksonSerialized
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdapter(@PathParam("id") String id, @PathParam("username") String userName) {

        try {
            AdapterDescription adapterDescription = adapterMasterManagement.getAdapter(id, new AdapterStorageImpl());

            return ok(adapterDescription);
        } catch (AdapterException e) {
            LOG.error("Error while getting adapter with id " + id, e);
            return fail();
        }

    }


    @DELETE
    @JacksonSerialized
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAdapter(@PathParam("id") String id, @PathParam("username") String userName) {

        try {

            AdapterDescription adapterDescription = adapterMasterManagement.getAdapter(id, new AdapterStorageImpl());

            String workerUrl = new Utils().getWorkerUrlById(adapterDescription.getAppId());
            String newUrl = Utils.addUserNameToApi(workerUrl, userName);
            adapterMasterManagement.deleteAdapter(id, newUrl);
            return ok(true);
        } catch (AdapterException e) {
            LOG.error("Error while deleting adapter with id " + id, e);
            return fail();
        }
    }

    @GET
    @JacksonSerialized
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAdapters(@PathParam("username") String userName) {
        try {
            List<AdapterDescription> allAdapterDescription = adapterMasterManagement.getAllAdapters(new AdapterStorageImpl());
            AdapterDescriptionList result = new AdapterDescriptionList();
            result.setList(allAdapterDescription);

            return ok(result);
        } catch (AdapterException e) {
            LOG.error("Error while getting all adapters", e);
            return fail();
        }

    }

    public void setAdapterMasterManagement(AdapterMasterManagement adapterMasterManagement) {
        this.adapterMasterManagement = adapterMasterManagement;
    }

}
