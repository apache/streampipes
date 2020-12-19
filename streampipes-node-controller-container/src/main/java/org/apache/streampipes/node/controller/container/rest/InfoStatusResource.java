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
package org.apache.streampipes.node.controller.container.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.streampipes.node.controller.container.management.info.NodeInfoStorage;
import org.apache.streampipes.node.controller.container.management.relay.EventRelay;
import org.apache.streampipes.node.controller.container.management.relay.RunningRelayInstances;
import org.apache.streampipes.node.controller.container.management.relay.metrics.RelayMetrics;
import org.apache.streampipes.node.controller.container.management.resources.ResourceManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/api/v2/node")
public class InfoStatusResource extends AbstractNodeContainerResource{

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfo() {
        return ok(NodeInfoStorage.getInstance().retrieveNodeInfo());
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() {
        return ok(ResourceManager.getInstance().retrieveNodeResources());
    }

    @GET
    @Path("/metrics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMetrics() {

        List<RelayMetrics> metricsList = RunningRelayInstances.INSTANCE.getRunningInstances()
                .stream()
                .map(EventRelay::getRelayMetrics)
                .collect(Collectors.toList());

        try {
            return ok(JacksonSerializer
                    .getObjectMapper()
                    .writeValueAsString(metricsList));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return fail();
    }
}
