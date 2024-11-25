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

package org.apache.streampipes.extensions.connectors.opcua.client;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.extensions.connectors.opcua.config.OpcUaConfig;

import org.eclipse.milo.opcua.stack.core.UaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class OpcUaClientProvider {

  private static final Logger LOG = LoggerFactory.getLogger(OpcUaClientProvider.class);

  private final Map<String, ConnectedOpcUaClient> clients = new ConcurrentHashMap<>();
  private final Map<String, Integer> consumers = new ConcurrentHashMap<>();

  public synchronized <T extends OpcUaConfig> ConnectedOpcUaClient getClient(T config)
      throws UaException, SpConfigurationException, URISyntaxException, ExecutionException, InterruptedException {
    var serverId = config.getUniqueServerId();
    if (clients.containsKey(serverId)) {
      LOG.debug("Adding new consumer to client {}", serverId);
      consumers.put(serverId, consumers.get(config.getUniqueServerId()) + 1);
      return clients.get(serverId);
    } else {
      LOG.debug("Creating new client {}", serverId);
      var connectedClient = new SpOpcUaClient<>(config).connect();
      clients.put(serverId, connectedClient);
      consumers.put(serverId, 1);
      return connectedClient;
    }
  }

  public <T extends OpcUaConfig> void releaseClient(T config) {
    String serverId = config.getUniqueServerId();
    LOG.debug("Releasing client {}", serverId);

    synchronized (this) {
      consumers.computeIfPresent(serverId, (key, count) -> {
        int updatedCount = count - 1;
        if (updatedCount <= 0) {
          LOG.debug("Disconnecting client {}", serverId);
          if (clients.containsKey(serverId)) {
            clients.get(serverId).disconnect();
            clients.remove(serverId);
          }
          return null;
        }
        return updatedCount;
      });
    }
  }
}
