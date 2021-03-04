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
package org.apache.streampipes.node.controller.container.management.relay;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelay;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.eventrelay.metrics.RelayMetrics;


import java.util.*;
import java.util.stream.Collectors;

public class DataStreamRelayManager {

    private static DataStreamRelayManager instance = null;

    private DataStreamRelayManager() {}

    public static DataStreamRelayManager getInstance() {
        if (instance == null) {
            synchronized (DataStreamRelayManager.class) {
                if (instance == null)
                    instance = new DataStreamRelayManager();
            }
        }
        return instance;
    }

    public Response start(InvocableStreamPipesEntity graph) {
        return start(convert(graph));
    }

    public Response start(SpDataStreamRelayContainer desc) {
        String strategy = desc.getEventRelayStrategy();
        String id = desc.getRunningStreamRelayInstanceId();
        TransportProtocol source = desc.getInputGrounding().getTransportProtocol();

        Map<String, EventRelay> eventRelayMap = new HashMap<>();

        // start data stream relay
        // 1:1 mapping -> remote forward
        // 1:n mamping -> remote fan-out
        desc.getOutputStreamRelays().forEach(relay -> {
            // add new relay if not running
            if(!runningRelayExists(id, relay)) {
                TransportProtocol target = relay.getEventGrounding().getTransportProtocol();
                EventRelay eventRelay = new EventRelay(source, target, strategy);
                eventRelay.start();
                eventRelayMap.put(relay.getElementId(), eventRelay);
            }
        });
        RunningRelayInstances.INSTANCE.add(desc.getRunningStreamRelayInstanceId(), eventRelayMap);
        return new Response(id,true,"");
    }

    public Response stop(String id) {
        Map<String, EventRelay> relays = RunningRelayInstances.INSTANCE.get(id);
        if (relays != null) {
            relays.values().forEach(EventRelay::stop);
        }
        RunningRelayInstances.INSTANCE.remove(id);
        return new Response(id, true, "");
    }

    public List<RelayMetrics> getAllRelaysMetrics() {
       return RunningRelayInstances.INSTANCE.getRunningInstances()
                    .stream()
                    .map(EventRelay::getRelayMetrics)
                    .collect(Collectors.toList());
    }

    private boolean runningRelayExists(String id, SpDataStreamRelay relay) {
        Map<String, EventRelay> relays = RunningRelayInstances.INSTANCE.get(id);
        if (relays != null) {
            return relays.keySet().stream()
                    .anyMatch(eventRelay -> eventRelay.equals(relay.getElementId()));
        } else {
            return false;
        }
    }

    // Helpers

    private SpDataStreamRelayContainer convert(InvocableStreamPipesEntity graph) {
        if (graph instanceof DataProcessorInvocation) {
            return new SpDataStreamRelayContainer((DataProcessorInvocation) graph);
        }
        throw new SpRuntimeException("Could not convert pipeline element description to data stream relay");
    }
}
