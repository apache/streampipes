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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.SpDataStreamRelay;
import org.apache.streampipes.model.SpDataStreamRelayContainer;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.node.controller.container.management.relay.metrics.RelayMetrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public Response startAdapterDataStreamRelay(SpDataStreamRelayContainer desc) {
        String strategy = desc.getEventRelayStrategy();
        String runningInstanceId = desc.getRunningStreamRelayInstanceId();
        TransportProtocol source = desc.getInputGrounding().getTransportProtocol();

        Map<String, EventRelay> eventRelayMap = new HashMap<>();

        desc.getOutputStreamRelays().forEach(r -> {
            TransportProtocol target = r.getEventGrounding().getTransportProtocol();
            EventRelay eventRelay = new EventRelay(source, target, strategy);
            eventRelay.start();
            eventRelayMap.put(r.getElementId(), eventRelay);
        });
        RunningRelayInstances.INSTANCE.add(desc.getRunningStreamRelayInstanceId(), eventRelayMap);
        return new Response(runningInstanceId,true,"");
    }

    public Response stopAdapterDataStreamRelay(String id) {
        Map<String, EventRelay> relay = RunningRelayInstances.INSTANCE.get(id);
        if (relay != null) {
            relay.values().forEach(EventRelay::stop);
        }
        RunningRelayInstances.INSTANCE.remove(id);
        return new Response(id, true, "");
    }

    public void startPipelineElementDataStreamRelay(DataProcessorInvocation graph) {
        TransportProtocol source = graph
                .getOutputStream()
                .getEventGrounding()
                .getTransportProtocol();

        String strategy = graph.getEventRelayStrategy();
        Map<String, EventRelay> eventRelayMap = new HashMap<>();

        List<SpDataStreamRelay> dataStreamRelays = graph.getOutputStreamRelays();
        dataStreamRelays.forEach(r -> {
            TransportProtocol target = r.getEventGrounding().getTransportProtocol();
            EventRelay eventRelay = new EventRelay(source, target, strategy);
            eventRelay.start();
            eventRelayMap.put(r.getElementId(), eventRelay);
        });
        RunningRelayInstances.INSTANCE.add(graph.getDeploymentRunningInstanceId(), eventRelayMap);
    }

    public void stopPipelineElementDataStreamRelay(String id) {
        // Stop relay for invocable if existing
        Map<String, EventRelay> relay = RunningRelayInstances.INSTANCE.get(id);
        if (relay != null) {
            relay.values().forEach(EventRelay::stop);
        }
        RunningRelayInstances.INSTANCE.remove(id);
    }

    public List<RelayMetrics> getAllRelays() {
       return RunningRelayInstances.INSTANCE.getRunningInstances()
                    .stream()
                    .map(EventRelay::getRelayMetrics)
                    .collect(Collectors.toList());
    }
}
