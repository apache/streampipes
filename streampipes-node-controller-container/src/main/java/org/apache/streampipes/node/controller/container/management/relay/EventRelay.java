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
import org.apache.streampipes.model.grounding.*;
import org.apache.streampipes.model.eventrelay.metrics.RelayMetrics;

public class EventRelay extends BaseEventRelay {

    private static final String DEFAULT_EVENT_RELAY_STRATEGY = "buffer";

    public EventRelay(TransportProtocol source, TransportProtocol target) {
        super(source, target, DEFAULT_EVENT_RELAY_STRATEGY);
    }

    public EventRelay(TransportProtocol source, TransportProtocol target, String relayStrategy)  {
        super(source, target, relayStrategy);
    }

    public void start() throws SpRuntimeException {
        this.multiBrokerBridge.start();
    }

    public void stop() throws SpRuntimeException {
        this.multiBrokerBridge.stop();
    }

    public RelayMetrics getRelayMetrics() {
        return this.multiBrokerBridge.getRelayMerics();
    }
}
