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
package org.apache.streampipes.rest.impl;

import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.rest.api.INode;
import org.apache.streampipes.rest.shared.annotation.GsonClientModel;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/users/{username}/nodes")
public class Node extends AbstractRestInterface implements INode {
    private static final Logger LOG = LoggerFactory.getLogger(Node.class.getCanonicalName());

    @POST
    @JacksonSerialized
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response addNode(@PathParam("username") String username, NodeInfoDescription desc) {
        Operations.addNode(desc);
        return statusMessage(Notifications.success("Node added"));
    }


    @PUT
    @JacksonSerialized
    @Path("/{nodeControllerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response updateNode(@PathParam("username") String username,
                               @PathParam("nodeControllerId") String nodeControllerId, NodeInfoDescription desc) {
        return statusMessage(Operations.updateNode(desc));
    }

    @DELETE
    @Path("/{nodeControllerId}")
    @Override
    public Response deleteNode(@PathParam("username") String username,
                               @PathParam("nodeControllerId") String nodeControllerId) {
        Operations.deleteNode(nodeControllerId);
        return statusMessage(Notifications.success("Node deleted"));
    }

    @GET
    @Path("/available")
    @JacksonSerialized
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getAvailableNodes() {
        // TODO: get from couchdb not from consul
        return ok(Operations.getAvailableNodes());
    }

    @GET
    @JacksonSerialized
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getNodes() {
        return ok(Operations.getAllNodes());
    }
}
