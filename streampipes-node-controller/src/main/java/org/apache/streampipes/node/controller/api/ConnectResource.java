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
package org.apache.streampipes.node.controller.api;

import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.apache.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.node.controller.management.connect.ConnectManager;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/api/v1/{username}/worker")
public class ConnectResource extends AbstractResource {

    // Registration
   @POST
   @JacksonSerialized
   @Path("/register")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response register(@PathParam("username") String username, ConnectWorkerContainer cw) {
       return ok(ConnectManager.getInstance().register(username, cw));
   }

    // AdapterResource
    @GET
    @Path("/adapters/{id}/assets")
    @Produces("application/zip")
    public Response getAdapterAssets(@PathParam("username") String username, @PathParam("id") String appId) {
        return ok(ConnectManager.getInstance().assets(username, appId, "adapter", "/"));
    }

    @GET
    @Path("/adapters/{id}/assets/icon")
    @Produces("image/png")
    public Response getAdapterIconAsset(@PathParam("username") String username, @PathParam("id") String appId) {
        return ok(ConnectManager.getInstance().assets(username, appId, "adapter", "/icon"));
    }

    @GET
    @Path("/adapters/{id}/assets/documentation")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAdapterDocumentationAsset(@PathParam("username") String username, @PathParam("id") String appId) {
        return ConnectManager.getInstance()
                .assets(username, appId, "adapter", "/documentation").toString();
    }

    // ProtocolResource
    @GET
    @Path("/protocols/{id}/assets")
    @Produces("application/zip")
    public Response getProtocolAssets(@PathParam("username") String username, @PathParam("id") String appId) {
        return ok(ConnectManager.getInstance().assets(username, appId, "protocol", "/"));
    }

    @GET
    @Path("/protocols/{id}/assets/icon")
    @Produces("image/png")
    public Response getProtocolIconAsset(@PathParam("username") String username, @PathParam("id") String appId) {
        return ok(ConnectManager.getInstance().assets(username, appId, "protocol", "/icon"));
    }

    @GET
    @Path("/protocols/{id}/assets/documentation")
    @Produces(MediaType.TEXT_PLAIN)
    public String getProtocolDocumentationAsset(@PathParam("username") String username,
                                             @PathParam("id") String appId) {
        return ConnectManager.getInstance()
                .assets(username, appId, "protocol", "documentation").toString();
    }

    // WorkerResource
    @POST
    @JacksonSerialized
    @Path("/stream/invoke")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response invokeStreamAdapter(@PathParam("username") String username, AdapterStreamDescription ad) {
        return ok(ConnectManager.getInstance().invoke(username, ad));
    }

    @POST
    @JacksonSerialized
    @Path("/stream/stop")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response stopStreamAdapter(@PathParam("username") String username, AdapterStreamDescription ad) {
        return ok(ConnectManager.getInstance().stop(username, ad));
    }

    @POST
    @JacksonSerialized
    @Path("/set/invoke")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response invokeSetAdapter(@PathParam("username") String username, AdapterSetDescription ad) {
        return ok(ConnectManager.getInstance().invoke(username, ad));
    }

    @POST
    @JacksonSerialized
    @Path("/set/stop")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response stopSetAdapter(@PathParam("username") String username, AdapterSetDescription ad){
        return ok(ConnectManager.getInstance().stop(username, ad));
    }

    @POST
    @JacksonSerialized
    @Path("/guess/schema")
    @Produces(MediaType.APPLICATION_JSON)
    public Response guessSchema(@PathParam("username") String username, AdapterDescription ad) {
       return ok(ConnectManager.getInstance().guess(username, ad));
    }

    // RuntimeResolvableResource
    @POST
    @Path("/resolvable/{id}/configurations")
    @JacksonSerialized
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response fetchConfigurations(@PathParam("username") String username, @PathParam("id") String appId,
                                        RuntimeOptionsRequest runtimeOptions) {
       return ok(ConnectManager.getInstance().fetchConfigurations(username, appId, runtimeOptions));
    }
}
