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
package org.apache.streampipes.node.management.operation.relay;

import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.node.management.operation.sync.SynchronizationFactory;
import org.apache.streampipes.node.management.operation.sync.SynchronizationType;
import org.apache.streampipes.node.management.utils.StorageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RelayHandler implements RelayOperation {
    private static final Logger LOG = LoggerFactory.getLogger(RelayHandler.class.getCanonicalName());

    private final NodeInfoDescription node;

    public RelayHandler(NodeInfoDescription node) {
        this.node = node;
    }

    @Override
    public Message restart() {
        String nodeControllerId = node.getNodeControllerId();
        List<SpDataStreamRelayContainer> runningRelays = StorageUtils.getAllRelaysFromNode(nodeControllerId);

        if (runningRelays.size() > 0) {
            runningRelays.forEach(relay -> {

                String relayName = relay.getName();
                String host = node.getHostname();
                int port = node.getPort();

                LOG.info("Sync active relays name={} to http://{}:{}", relayName, host, port);
                SynchronizationFactory.synchronize(relay, SynchronizationType.RESTART_RELAYS);
            });
        }
        return Notifications.success(NotificationType.NODE_JOIN_SUCCESS);
    }
}
