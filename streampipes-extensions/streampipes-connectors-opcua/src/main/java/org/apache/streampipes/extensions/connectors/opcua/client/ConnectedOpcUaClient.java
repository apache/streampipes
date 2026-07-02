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

import org.apache.streampipes.extensions.connectors.opcua.adapter.OpcUaAdapter;
import org.apache.streampipes.extensions.connectors.opcua.config.OpcUaAdapterConfig;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.subscriptions.MonitoredItemServiceOperationResult;
import org.eclipse.milo.opcua.sdk.client.subscriptions.OpcUaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.subscriptions.OpcUaSubscription;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

public class ConnectedOpcUaClient {

  private static final Logger LOG = LoggerFactory.getLogger(ConnectedOpcUaClient.class);
  private final OpcUaClient client;

  public ConnectedOpcUaClient(OpcUaClient client) {
    this.client = client;
  }

  /***
   * Register subscriptions for given OPC UA nodes
   * @param nodes List of {@link org.eclipse.milo.opcua.stack.core.types.builtin.NodeId}
   * @param config subscription configuration
   * @param opcUaAdapter current instance of {@link OpcUaAdapter}
   * @throws Exception
   */
  public void createListSubscription(List<NodeId> nodes,
                                     OpcUaAdapterConfig config,
                                     OpcUaAdapter opcUaAdapter) throws Exception {
    initSubscription(nodes, config, opcUaAdapter);
  }


  public void initSubscription(List<NodeId> nodes,
                               OpcUaAdapterConfig config,
                               OpcUaAdapter opcUaAdapter) throws Exception {
    var subscription = getOpcUaSubscription(nodes, config, opcUaAdapter);

    List<OpcUaMonitoredItem> items = new ArrayList<>();
    for (NodeId node : nodes) {
      var item = OpcUaMonitoredItem.newDataItem(node);
      item.setSamplingInterval((double) config.getSubscriptionSamplingIntervalMs());
      item.setQueueSize(uint(config.getSubscriptionQueueSize()));
      item.setDiscardOldest(config.isSubscriptionDiscardOldest());
      item.setDataValueListener(opcUaAdapter::onSubscriptionValue);
      items.add(item);
    }

    subscription.addMonitoredItems(items);
    List<MonitoredItemServiceOperationResult> results = subscription.createMonitoredItems();

    for (MonitoredItemServiceOperationResult result : results) {
      var monitoredItem = result.monitoredItem();
      NodeId tagId = monitoredItem.getReadValueId().getNodeId();
      if (result.isGood()) {
        LOG.info("item created for nodeId={}", tagId);
      } else {
        var statusCode = result.operationResult().orElse(result.serviceResult());
        LOG.error("failed to create item for {} {}", tagId, statusCode);
      }
    }
  }

  private @NonNull OpcUaSubscription getOpcUaSubscription(List<NodeId> nodes,
                                                          OpcUaAdapterConfig config,
                                                          OpcUaAdapter opcUaAdapter) throws UaException {
    OpcUaSubscription subscription = createManagedSubscription(config);
    subscription.setSubscriptionListener(new OpcUaSubscription.SubscriptionListener() {
      @Override
      public void onTransferFailed(OpcUaSubscription subscription, StatusCode statusCode) {
        LOG.warn("Transfer for subscriptionId={} failed: {}", subscription.getSubscriptionId(), statusCode);
        try {
          initSubscription(nodes, config, opcUaAdapter);
        } catch (Exception e) {
          LOG.error("Re-creating the subscription failed", e);
        }
      }
    });
    return subscription;
  }

  private OpcUaSubscription createManagedSubscription(OpcUaAdapterConfig config) throws UaException {
    var subscription = new OpcUaSubscription(this.client, (double) config.getSubscriptionPublishingIntervalMs());
    subscription.create();
    return subscription;
  }

  /***
   *
   * @return current {@link org.eclipse.milo.opcua.sdk.client.OpcUaClient}
   */
  public OpcUaClient getClient() {
    return this.client;
  }

  public void disconnect() {
    try {
      client.disconnect();
    } catch (UaException e) {
      LOG.warn("Disconnect failed", e);
    }
  }
}
