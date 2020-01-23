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
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.streampipes.connect.container.master.management.Utils;
import org.apache.streampipes.connect.container.master.management.DescriptionManagement;
import org.apache.streampipes.connect.rest.AbstractContainerResource;
import org.apache.streampipes.model.connect.adapter.AdapterDescriptionList;
import org.apache.streampipes.model.connect.grounding.FormatDescriptionList;
import org.apache.streampipes.model.connect.grounding.ProtocolDescriptionList;
import org.apache.streampipes.rest.shared.annotation.JsonLdSerialized;
import org.apache.streampipes.rest.shared.util.SpMediaType;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/v1/{username}/master/description")
public class DescriptionResource extends AbstractContainerResource {

    private Logger logger = LoggerFactory.getLogger(DescriptionResource.class);

    private DescriptionManagement descriptionManagement;

    public DescriptionResource() {
        descriptionManagement = new DescriptionManagement();
    }

    public DescriptionResource(DescriptionManagement descriptionManagement) {
        this.descriptionManagement = descriptionManagement;
    }

    @GET
    @JsonLdSerialized
    @Path("/formats")
    @Produces(SpMediaType.JSONLD)
    public Response getFormats() {
        FormatDescriptionList result = descriptionManagement.getFormats();

        return ok(result);
    }

    @GET
    @JsonLdSerialized
    @Path("/protocols")
    @Produces(SpMediaType.JSONLD)
    public Response getProtocols() {
        ProtocolDescriptionList result = descriptionManagement.getProtocols();

        return ok(result);
    }

    @GET
    @JsonLdSerialized
    @Path("/adapters")
    @Produces(SpMediaType.JSONLD)
    public Response getAdapters() {
        AdapterDescriptionList result = descriptionManagement.getAdapters();

        return ok(result);
    }

    public void setDescriptionManagement(DescriptionManagement descriptionManagement) {
        this.descriptionManagement = descriptionManagement;
    }

    @GET
    @Path("/{id}/assets")
    @Produces("application/zip")
    public Response getAdapterAssets(@PathParam("id") String id, @PathParam("username") String userName) {
        try {
            AdapterDescription adapterDescription = descriptionManagement.getAdapter(id);

            String workerUrl = new Utils().getWorkerUrl(adapterDescription);
            String newUrl = Utils.addUserNameToApi(workerUrl, userName);

            String result = "";
            if (adapterDescription instanceof GenericAdapterDescription) {
                result = descriptionManagement.getProtocolAssets(adapterDescription, newUrl);
            } else {
                result = descriptionManagement.getAdapterAssets(adapterDescription, newUrl);

            }
            return ok(result);
        } catch (AdapterException e) {
            logger.error("Not found adapter with id " + id, e);
            return fail();
        }
    }

    @GET
    @Path("/{id}/assets/icon")
    @Produces("image/png")
    public Response getAdapterIconAsset(@PathParam("id") String id, @PathParam("username") String userName) {
        try {
            AdapterDescription adapterDescription = descriptionManagement.getAdapter(id);

            String workerUrl = new Utils().getWorkerUrl(adapterDescription);
            String newUrl = Utils.addUserNameToApi(workerUrl, userName);

            byte[] result;
            if (adapterDescription instanceof GenericAdapterDescription) {
                result = descriptionManagement.getProtocolIconAsset(adapterDescription, newUrl);
            } else {
                result = descriptionManagement.getAdapterIconAsset(adapterDescription, newUrl);

            }
            return ok(result);
        } catch (AdapterException e) {
            logger.error("Not found adapter with id " + id, e);
            return fail();
        }
    }

    @GET
    @Path("/{id}/assets/documentation")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getAdapterDocumentationAsset(@PathParam("id") String id, @PathParam("username") String userName) {
        try {
            AdapterDescription adapterDescription = descriptionManagement.getAdapter(id);

            String workerUrl = new Utils().getWorkerUrl(adapterDescription);
            String newUrl = Utils.addUserNameToApi(workerUrl, userName);

            String result = "";
            if (adapterDescription instanceof GenericAdapterDescription) {
                result =  descriptionManagement.getProtocolDocumentationAsset(adapterDescription, newUrl);
            } else {
                result =  descriptionManagement.getAdapterDocumentationAsset(adapterDescription, newUrl);
            }
            return ok(result);
        } catch (AdapterException e) {
            logger.error("Not found adapter with id " + id, e);
            return fail();
        }
    }
}
