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

import org.apache.streampipes.manager.node.NodeClusterManager;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.rest.api.INode;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/users/{username}/nodes")
public class Node extends AbstractRestInterface implements INode {

    private static final String ACTIVATE = "activate";
    private static final String DEACTIVATE = "deactivate";

    @POST
    @JacksonSerialized
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response addNode(@PathParam("username") String username, NodeInfoDescription desc) {
        NodeClusterManager.addNode(desc);
        return statusMessage(Notifications.success(NotificationType.STORAGE_SUCCESS));
    }

    @PUT
    @JacksonSerialized
    @Path("/{nodeControllerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response updateNode(@PathParam("username") String username,
                               @PathParam("nodeControllerId") String nodeControllerId, NodeInfoDescription desc) {
        return statusMessage(NodeClusterManager.updateNode(desc));
    }

    @POST
    @JacksonSerialized
    @Path("/{action}/{nodeControllerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response changeNodeState(@PathParam("action") String action,
                                    @PathParam("username") String username,
                                    @PathParam("nodeControllerId") String nodeControllerId) {
        boolean success = false;
        if (action.equals(ACTIVATE)) {
            success = NodeClusterManager.activateNode(nodeControllerId);
        } else if (action.equals(DEACTIVATE)) {
            success = NodeClusterManager.deactivateNode(nodeControllerId);
        }
        if (success) {
            return statusMessage(Notifications.success(NotificationType.OPERATION_SUCCESS));
        } else {
            return statusMessage(Notifications.error(NotificationType.NODE_STATE_UPDATE_ERROR));
        }
    }

    @DELETE
    @Path("/{nodeControllerId}")
    @Override
    public Response deleteNode(@PathParam("username") String username,
                               @PathParam("nodeControllerId") String nodeControllerId) {
        NodeClusterManager.deleteNode(nodeControllerId);
        return statusMessage(Notifications.success(NotificationType.REMOVED_NODE));
    }

    @GET
    @Path("/available")
    @JacksonSerialized
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getAvailableNodes() {
        return ok(NodeClusterManager.getAvailableNodes());
    }

    @GET
    @JacksonSerialized
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getNodes() {
        return ok(NodeClusterManager.getAllNodes());
    }
}
