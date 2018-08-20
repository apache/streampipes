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
import org.streampipes.connect.management.master.DescriptionManagement;
import org.streampipes.connect.management.master.IDescriptionManagement;
import org.streampipes.connect.rest.AbstractContainerResource;
import org.streampipes.model.connect.adapter.AdapterDescriptionList;
import org.streampipes.model.connect.grounding.FormatDescriptionList;
import org.streampipes.model.connect.grounding.ProtocolDescriptionList;
import org.streampipes.rest.shared.annotation.JsonLdSerialized;
import org.streampipes.rest.shared.util.SpMediaType;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/api/v1/{username}/master/description")
public class DescriptionResource extends AbstractContainerResource {

    private Logger logger = LoggerFactory.getLogger(DescriptionResource.class);

    private IDescriptionManagement descriptionManagement;

    public DescriptionResource() {
        descriptionManagement = new DescriptionManagement();
    }

    public DescriptionResource(IDescriptionManagement descriptionManagement) {
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

    public void setDescriptionManagement(IDescriptionManagement descriptionManagement) {
        this.descriptionManagement = descriptionManagement;
    }
}
