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
import org.apache.streampipes.extensions.connectors.opcua.adapter.OpcUaAdapter;
import org.apache.streampipes.extensions.connectors.opcua.config.MiloOpcUaConfigurationProvider;
import org.apache.streampipes.extensions.connectors.opcua.config.OpcUaConfig;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscriptionManager;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

/***
 * Wrapper class for all OPC UA specific stuff.
 */
public class SpOpcUaClient<T extends OpcUaConfig> {

  private static final Logger LOG = LoggerFactory.getLogger(SpOpcUaClient.class);

  private OpcUaClient client;
  private final T spOpcConfig;

  private static final AtomicLong clientHandles = new AtomicLong(1L);

  public SpOpcUaClient(T config) {
    this.spOpcConfig = config;
  }

  /***
   *
   * @return current {@link org.eclipse.milo.opcua.sdk.client.OpcUaClient}
   */
  public OpcUaClient getClient() {
    return this.client;
  }

  /***
   * Establishes appropriate connection to OPC UA endpoint depending on the {@link SpOpcUaClient} instance
   *
   * @throws UaException An exception occurring during OPC connection
   */
  public void connect()
      throws UaException, ExecutionException, InterruptedException, SpConfigurationException, URISyntaxException {
    OpcUaClientConfig clientConfig = new MiloOpcUaConfigurationProvider().makeClientConfig(spOpcConfig);
    this.client = OpcUaClient.create(clientConfig);
    client.connect().get();
  }

  public void disconnect() {
    client.disconnect();
  }

  /***
   * Register subscriptions for given OPC UA nodes
   * @param nodes List of {@link org.eclipse.milo.opcua.stack.core.types.builtin.NodeId}
   * @param opcUaAdapter current instance of {@link OpcUaAdapter}
   * @throws Exception
   */
  public void createListSubscription(List<NodeId> nodes,
                                     OpcUaAdapter opcUaAdapter) throws Exception {
    client.getSubscriptionManager().addSubscriptionListener(new UaSubscriptionManager.SubscriptionListener() {
      @Override
      public void onSubscriptionTransferFailed(UaSubscription subscription, StatusCode statusCode) {
        LOG.warn("Transfer for subscriptionId={} failed: {}", subscription.getSubscriptionId(), statusCode);
        try {
          initSubscription(nodes, opcUaAdapter);
        } catch (Exception e) {
          LOG.error("Re-creating the subscription failed", e);
        }
      }
    });

    initSubscription(nodes, opcUaAdapter);
  }


  public void initSubscription(List<NodeId> nodes,
                               OpcUaAdapter opcUaAdapter) throws Exception {
    /*
     * create a subscription @ 1000ms
     */
    UaSubscription subscription = this.client.getSubscriptionManager().createSubscription(1000.0).get();

    List<CompletableFuture<DataValue>> values = new ArrayList<>();

    for (NodeId node : nodes) {
      values.add(this.client.readValue(0, TimestampsToReturn.Both, node));
    }

    for (CompletableFuture<DataValue> value : values) {
      if (value.get().getValue().toString().contains("null")) {
        LOG.error("Node has no value");
      }
    }


    List<ReadValueId> readValues = new ArrayList<>();
    // Read a specific value attribute
    for (NodeId node : nodes) {
      readValues.add(new ReadValueId(node, AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE));
    }

    List<MonitoredItemCreateRequest> requests = new ArrayList<>();

    for (ReadValueId readValue : readValues) {
      // important: client handle must be unique per item
      UInteger clientHandle = uint(clientHandles.getAndIncrement());

      MonitoringParameters parameters = new MonitoringParameters(
          clientHandle,
          1000.0,     // sampling interval
          null,      // filter, null means use default
          uint(10),   // queue size
          true         // discard oldest
      );

      requests.add(new MonitoredItemCreateRequest(readValue, MonitoringMode.Reporting, parameters));
    }

    UaSubscription.ItemCreationCallback onItemCreated =
        (item, i) -> item.setValueConsumer(opcUaAdapter::onSubscriptionValue);
    List<UaMonitoredItem> items = subscription.createMonitoredItems(
        TimestampsToReturn.Both,
        requests,
        onItemCreated
    ).get();

    for (UaMonitoredItem item : items) {
      NodeId tagId = item.getReadValueId().getNodeId();
      if (item.getStatusCode().isGood()) {
        LOG.info("item created for nodeId=" + tagId);
      } else {
        LOG.error("failed to create item for " + item.getReadValueId().getNodeId() + item.getStatusCode());
      }
    }
  }

  public T getSpOpcConfig() {
    return spOpcConfig;
  }
}
