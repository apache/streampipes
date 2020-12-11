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
package org.apache.streampipes.node.controller.container.management.relay.metrics;

import org.apache.streampipes.model.grounding.TransportProtocol;

public class RelayMetrics {

    private final long createdAt;
    private final String topic;
    private final TransportProtocol sourceTpProtocol;
    private final TransportProtocol targetTpProtocol;
    private final String strategy;
    private long numRelayedEvents = 0;
    private long numDroppedEvents = 0;

    public RelayMetrics(String topic, TransportProtocol sourceTpProtocol,
                        TransportProtocol targetTpProtocol, String strategy) {
        this.createdAt = System.currentTimeMillis();
        this.topic = topic;
        this.sourceTpProtocol = sourceTpProtocol;
        this.targetTpProtocol = targetTpProtocol;
        this.strategy = strategy;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public TransportProtocol getSourceTpProtocol() {
        return sourceTpProtocol;
    }

    public TransportProtocol getTargetTpProtocol() {
        return targetTpProtocol;
    }

    public String getStrategy() {
        return strategy;
    }

    public String getTopic() {
        return topic;
    }

    public long getNumRelayedEvents() {
        return numRelayedEvents;
    }

    public void increaseNumRelayedEvents() {
        this.numRelayedEvents++;
    }

    public long getNumDroppedEvents() {
        return numDroppedEvents;
    }

    public void increaseNumDroppedEvents() {
        this.numDroppedEvents++;
    }

    public void clearNumDroppedEvents() {
        this.numDroppedEvents = 0;
    }

}
