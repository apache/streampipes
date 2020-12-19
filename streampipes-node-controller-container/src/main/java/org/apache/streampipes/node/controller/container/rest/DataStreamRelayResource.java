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

import org.apache.streampipes.container.transform.Transformer;
import org.apache.streampipes.container.util.Util;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.SpDataStreamRelayContainer;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.node.controller.container.management.relay.EventRelay;
import org.apache.streampipes.node.controller.container.management.relay.RunningRelayInstances;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/api/v2/node/stream/relay")
public class DataStreamRelayResource extends AbstractNodeContainerResource {
    private static final Logger LOG = LoggerFactory.getLogger(DataStreamRelayResource.class.getCanonicalName());

    @POST
    @Path("/invoke")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String invokeRelaySourceDataStream(SpDataStreamRelayContainer graph) {

        String strategy = graph.getEventRelayStrategy();
        String runningInstanceId = graph.getRunningStreamRelayInstanceId();
        TransportProtocol source = graph.getInputGrounding().getTransportProtocol();

        Map<String, EventRelay> eventRelayMap = new HashMap<>();

        graph.getOutputStreamRelays().forEach(r -> {
            TransportProtocol target = r.getEventGrounding().getTransportProtocol();

            EventRelay eventRelay = new EventRelay(source, target, strategy);
            eventRelay.start();
            eventRelayMap.put(r.getElementId(), eventRelay);
        });

        RunningRelayInstances.INSTANCE.add(graph.getRunningStreamRelayInstanceId(), eventRelayMap);

        return Util.toResponseString(new Response(runningInstanceId,true,""));
    }

    @DELETE
    @Path("/detach/{runningInstanceId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String detachRelaySourceDataStream(@PathParam("runningInstanceId") String runningInstanceId) {

        Map<String, EventRelay> relay = RunningRelayInstances.INSTANCE.get(runningInstanceId);
        if (relay != null) {
            relay.values().forEach(EventRelay::stop);
        }

        RunningRelayInstances.INSTANCE.remove(runningInstanceId);

        return Util.toResponseString(new Response(runningInstanceId, true, ""));
    }
}
