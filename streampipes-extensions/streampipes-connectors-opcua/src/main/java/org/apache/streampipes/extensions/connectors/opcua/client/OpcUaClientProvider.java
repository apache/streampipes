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
import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaCertificateUtils;

import org.eclipse.milo.opcua.stack.core.UaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class OpcUaClientProvider {

  private static final Logger LOG = LoggerFactory.getLogger(OpcUaClientProvider.class);

  private final Map<String, ConnectedOpcUaClient> clients = new ConcurrentHashMap<>();
  private final Map<String, Integer> consumers = new ConcurrentHashMap<>();
  private final Map<String, String> serverThumbprints = new HashMap<>();

  public synchronized <T extends OpcUaConfig> ConnectedOpcUaClient getClient(T config)
      throws UaException, SpConfigurationException, URISyntaxException, ExecutionException, InterruptedException {
    var serverId = config.getUniqueServerId();
    if (clients.containsKey(serverId)) {
      int newConsumerCount = consumers.get(config.getUniqueServerId()) + 1;
      consumers.put(serverId, newConsumerCount);
      var client = clients.get(serverId);
      LOG.debug(
          "Reusing OPC UA client {} (identity={}) with consumerCount={}",
          serverId,
          System.identityHashCode(client.getClient()),
          newConsumerCount
      );
      var associatedResourceId = config.getAssociatedResourceId();
      if (serverThumbprints.containsKey(serverId) && Objects.nonNull(associatedResourceId)) {
        OpcUaCertificateUtils.sendUsageToCore(
            serverThumbprints.get(serverId),
            associatedResourceId,
            config.getStreamPipesClient()
        );
      }
      return client;
    } else {
      LOG.debug("Creating new OPC UA client {}", serverId);
      var connectedClient = new SpOpcUaClient<>(config).connect();
      clients.put(serverId, connectedClient);
      consumers.put(serverId, 1);
      LOG.debug(
          "Created OPC UA client {} (identity={}) with consumerCount=1",
          serverId,
          System.identityHashCode(connectedClient.getClient())
      );
      if (config.getCertificateThumbprint() != null) {
        serverThumbprints.put(serverId, config.getCertificateThumbprint());
      }
      return connectedClient;
    }
  }

  public <T extends OpcUaConfig> void releaseClient(T config) {
    String serverId = config.getUniqueServerId();
    synchronized (this) {
      consumers.computeIfPresent(serverId, (key, count) -> {
        int updatedCount = count - 1;
        ConnectedOpcUaClient client = clients.get(serverId);
        LOG.debug(
            "Releasing OPC UA client {} (identity={}) from consumerCount={} to {}",
            serverId,
            client != null ? System.identityHashCode(client.getClient()) : null,
            count,
            Math.max(updatedCount, 0)
        );
        if (updatedCount <= 0) {
          LOG.debug("Disconnecting OPC UA client {}", serverId);
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
