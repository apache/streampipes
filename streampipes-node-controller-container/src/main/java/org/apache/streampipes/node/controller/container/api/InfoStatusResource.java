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
package org.apache.streampipes.node.controller.container.api;

import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.node.controller.container.management.node.NodeManager;
import org.apache.streampipes.node.controller.container.management.relay.DataStreamRelayManager;;
import org.apache.streampipes.node.controller.container.management.resource.ResourceManager;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/v2/node/info")
public class InfoStatusResource extends AbstractResource {

    private static final String ACTIVATE = "activate";
    private static final String DEACTIVATE = "deactivate";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNodeInfo() {
        return ok(NodeManager.getInstance().retrieveNodeInfoDescription());
    }

    @PUT
    @JacksonSerialized
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateNodeInfo(NodeInfoDescription desc) {
        return ok(NodeManager.getInstance().updateNodeInfoDescription(desc));
    }

    @POST
    @Path("{action}")
    @JacksonSerialized
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response activateNode(@PathParam("action") String action) {
        if (action.equals(ACTIVATE)) {
            return ok(NodeManager.getInstance().activate());
        } else if (action.equals(DEACTIVATE)) {
            return ok(NodeManager.getInstance().deactivate());
        } else return fail();
    }

    @GET
    @Path("/resources")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() {
        return ok(ResourceManager.getInstance().retrieveNodeResources());
    }

    @GET
    @Path("/relays")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRelays() {
        return ok(DataStreamRelayManager.getInstance().getAllRelays());
    }
}
