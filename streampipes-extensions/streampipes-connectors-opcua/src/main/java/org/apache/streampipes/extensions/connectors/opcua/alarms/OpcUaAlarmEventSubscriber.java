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

package org.apache.streampipes.extensions.connectors.opcua.alarms;

import org.apache.streampipes.extensions.connectors.opcua.client.ConnectedOpcUaClient;

import org.eclipse.milo.opcua.sdk.client.ServiceFaultListener;
import org.eclipse.milo.opcua.sdk.client.SessionActivityListener;
import org.eclipse.milo.opcua.sdk.client.subscriptions.MonitoredItemServiceOperationResult;
import org.eclipse.milo.opcua.sdk.client.subscriptions.OpcUaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.subscriptions.OpcUaSubscription;
import org.eclipse.milo.opcua.stack.core.NodeIds;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseResultMask;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.CallMethodRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.CallMethodResult;
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.eclipse.milo.opcua.stack.core.StatusCodes.Bad_UnexpectedError;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

public class OpcUaAlarmEventSubscriber implements AutoCloseable {

  private static final Logger LOG = LoggerFactory.getLogger(OpcUaAlarmEventSubscriber.class);
  private static final double PUBLISHING_INTERVAL_MS = 1000.0;

  private final ConnectedOpcUaClient connectedClient;
  private final OpcUaAlarmAdapterConfig config;
  private final OpcUaAlarmEventMapper eventMapper;
  private final OpcUaAlarmEventFilter eventFilter;
  private final Consumer<Map<String, Object>> eventConsumer;
  private final SessionActivityListener sessionActivityListener;
  private final ServiceFaultListener serviceFaultListener;

  private volatile boolean closed;
  private volatile String lastSubscriptionOperation = "idle";
  private OpcUaSubscription subscription;

  OpcUaAlarmEventSubscriber(ConnectedOpcUaClient connectedClient,
                            OpcUaAlarmAdapterConfig config,
                            Consumer<Map<String, Object>> eventConsumer) {
    this.connectedClient = connectedClient;
    this.config = config;
    this.eventConsumer = eventConsumer;
    this.eventMapper = OpcUaAlarmEventMapper.create(connectedClient.getClient(), config);
    this.eventFilter = new OpcUaAlarmEventFilter(config);
    this.sessionActivityListener = new SessionActivityListener() {
      @Override
      public void onSessionActive(org.eclipse.milo.opcua.sdk.client.UaSession session) {
        LOG.debug(
            "OPC UA alarm subscriber client identity={} session became active: {}",
            System.identityHashCode(OpcUaAlarmEventSubscriber.this.connectedClient.getClient()),
            session.getSessionId()
        );
      }

      @Override
      public void onSessionInactive(org.eclipse.milo.opcua.sdk.client.UaSession session) {
        LOG.debug(
            "OPC UA alarm subscriber client identity={} session became inactive: {}",
            System.identityHashCode(OpcUaAlarmEventSubscriber.this.connectedClient.getClient()),
            session.getSessionId()
        );
      }
    };
    this.serviceFaultListener = serviceFault -> LOG.warn(
        "OPC UA alarm subscriber client identity={} received service fault: {} lastOperation={} localSubscriptionId={} managedSubscriptionCount={}",
        System.identityHashCode(OpcUaAlarmEventSubscriber.this.connectedClient.getClient()),
        serviceFault.getResponseHeader().getServiceResult(),
        lastSubscriptionOperation,
        subscription != null ? subscription.getSubscriptionId().orElse(null) : null,
        OpcUaAlarmEventSubscriber.this.connectedClient.getClient().getSubscriptions().size()
    );
  }

  synchronized void start() throws UaException {
    if (closed) {
      throw new IllegalStateException("Subscriber is already closed");
    }

    if (subscription == null) {
      connectedClient.getClient().addSessionActivityListener(sessionActivityListener);
      connectedClient.getClient().addFaultListener(serviceFaultListener);
      createSubscription();
    }
  }

  private void createSubscription() throws UaException {
    setLastSubscriptionOperation("create-subscription");
    OpcUaSubscription newSubscription = createManagedSubscription();
    newSubscription.setSubscriptionListener(new OpcUaSubscription.SubscriptionListener() {
      @Override
      public void onKeepAliveReceived(OpcUaSubscription subscription) {
        LOG.debug(
            "Received OPC UA alarm keepalive for subscriptionId={}",
            subscription.getSubscriptionId().orElse(null)
        );
      }

      @Override
      public void onEventReceived(
          OpcUaSubscription subscription,
          List<OpcUaMonitoredItem> items,
          List<Variant[]> eventFields) {
        LOG.debug(
            "Subscription listener received {} OPC UA alarm event notification(s) for subscriptionId={}",
            eventFields.size(),
            subscription.getSubscriptionId().orElse(null)
        );
      }

      @Override
      public void onTransferFailed(OpcUaSubscription subscription, StatusCode statusCode) {
        if (closed) {
          return;
        }

        setLastSubscriptionOperation(
            "transfer-failed(subscriptionId=%s,status=%s)".formatted(
                subscription.getSubscriptionId().orElse(null),
                statusCode
            )
        );
        LOG.warn("Transfer for alarm subscriptionId={} failed: {}", subscription.getSubscriptionId(), statusCode);
        synchronized (OpcUaAlarmEventSubscriber.this) {
          deleteSubscriptionQuietly();
          try {
            createSubscription();
          } catch (UaException e) {
            LOG.error("Re-creating OPC UA alarm subscription failed", e);
          }
        }
      }
    });

    LOG.debug(
        "Created OPC UA alarm subscription with subscriptionId={} on client identity={} managerSubscriptionCount={}",
        newSubscription.getSubscriptionId().orElse(null),
        System.identityHashCode(connectedClient.getClient()),
        connectedClient.getClient().getSubscriptions().size()
    );

    var monitoredItem = OpcUaMonitoredItem.newEventItem(
        getSubscriptionTargetNodeId(),
        eventMapper.makeEventFilter(connectedClient.getClient().getStaticEncodingContext())
    );
    monitoredItem.setQueueSize(uint(100));
    monitoredItem.setDiscardOldest(true);
    monitoredItem.setEventValueListener(this::onEventReceived);

    newSubscription.addMonitoredItem(monitoredItem);
    setLastSubscriptionOperation(
        "create-monitored-items(subscriptionId=%s,targetNodeId=%s)".formatted(
            newSubscription.getSubscriptionId().orElse(null),
            getSubscriptionTargetNodeId()
        )
    );
    var results = newSubscription.createMonitoredItems();
    assertSingleGoodResult(results);
    LOG.debug(
        "Created OPC UA alarm monitored item on subscriptionId={} client identity={} resultCount={} status={}",
        newSubscription.getSubscriptionId().orElse(null),
        System.identityHashCode(connectedClient.getClient()),
        results.size(),
        results.get(0).operationResult().orElse(results.get(0).serviceResult())
    );

    this.subscription = newSubscription;
    requestConditionRefresh(newSubscription);
    setLastSubscriptionOperation(
        "subscription-ready(subscriptionId=%s)".formatted(newSubscription.getSubscriptionId().orElse(null))
    );
  }

  private OpcUaSubscription createManagedSubscription() throws UaException {
    var subscription = new OpcUaSubscription(connectedClient.getClient(), PUBLISHING_INTERVAL_MS);
    subscription.create();
    return subscription;
  }

  private void assertSingleGoodResult(List<MonitoredItemServiceOperationResult> results) throws UaException {
    if (results.isEmpty()) {
      throw new UaException(Bad_UnexpectedError, "No monitored item result returned for OPC UA alarm subscription");
    }

    var result = results.get(0);
    if (!result.isGood()) {
      throw new UaException(
          result.operationResult().orElse(result.serviceResult()),
          "Failed to create OPC UA alarm monitored item"
      );
    }
  }

  private void requestConditionRefresh(OpcUaSubscription subscription) {
    var subscriptionId = subscription.getSubscriptionId();
    if (subscriptionId.isEmpty()) {
      LOG.warn("Skipping ConditionRefresh because the OPC UA subscription has no subscriptionId yet");
      return;
    }

    try {
      setLastSubscriptionOperation(
          "condition-refresh(subscriptionId=%s)".formatted(subscriptionId.get())
      );
      Optional<NodeId> conditionRefreshMethodId = findConditionRefreshMethodId();

      if (conditionRefreshMethodId.isEmpty()) {
        LOG.debug("Skipping ConditionRefresh because the server does not expose a ConditionRefresh method on the Server object");
        return;
      }

      var request = new CallMethodRequest(
          NodeIds.Server,
          conditionRefreshMethodId.get(),
          new Variant[] {new Variant(subscriptionId.get())}
      );

      var response = connectedClient.getClient().call(List.of(request));
      CallMethodResult result = response.getResults() != null && response.getResults().length > 0
          ? response.getResults()[0]
          : null;

      if (result == null) {
        LOG.warn("ConditionRefresh returned no result for subscriptionId={}", subscriptionId.get());
        return;
      }

      if (result.getStatusCode().isGood()) {
        LOG.debug("ConditionRefresh succeeded for subscriptionId={}", subscriptionId.get());
      } else {
        LOG.warn("ConditionRefresh failed for subscriptionId={} with status={}", subscriptionId.get(), result.getStatusCode());
      }
    } catch (UaException e) {
      LOG.warn("ConditionRefresh invocation failed for subscriptionId={}", subscriptionId.get(), e);
    }
  }

  private Optional<NodeId> findConditionRefreshMethodId() {
    try {
      var browseResult = connectedClient.getClient().browse(new BrowseDescription(
          NodeIds.Server,
          BrowseDirection.Forward,
          NodeIds.References,
          true,
          uint(NodeClass.Method.getValue()),
          uint(BrowseResultMask.All.getValue())
      ));

      Optional<NodeId> methodId = Arrays.stream(browseResult.getReferences())
          .filter(reference -> "ConditionRefresh".equals(reference.getBrowseName().getName()))
          .findFirst()
          .flatMap(this::toNodeId);

      if (methodId.isEmpty() && LOG.isDebugEnabled()) {
        String availableMethods = Arrays.stream(browseResult.getReferences())
            .map(reference -> reference.getBrowseName().getName())
            .collect(Collectors.joining(", "));

        LOG.debug("Server methods available for ConditionRefresh discovery: {}", availableMethods);
      }

      return methodId;
    } catch (UaException e) {
      LOG.debug("Browsing Server methods for ConditionRefresh failed", e);
      return Optional.empty();
    }
  }

  private Optional<NodeId> toNodeId(ReferenceDescription reference) {
    return reference.getNodeId().toNodeId(connectedClient.getClient().getNamespaceTable());
  }

  private void onEventReceived(OpcUaMonitoredItem item, Variant[] eventValues) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Received OPC UA event from nodeId={} values={}",
          item.getReadValueId().getNodeId(),
          Arrays.toString(eventValues));
    }

    var event = eventMapper.toEvent(eventValues);
    if (event.isEmpty()) {
      LOG.debug("Dropped OPC UA event after mapping because it was not recognized as an alarm/condition");
      return;
    }

    if (!eventFilter.matches(event)) {
      LOG.debug("Dropped OPC UA alarm event after applying configured filters: {}", event);
      return;
    }

    LOG.debug("Mapped OPC UA alarm event: {}", event);

    try {
      eventConsumer.accept(event);
    } catch (Exception e) {
      LOG.error("Processing OPC UA alarm event failed", e);
    }
  }

  private NodeId getSubscriptionTargetNodeId() {
    if (config.getNotifierNodeId() == null || config.getNotifierNodeId().isBlank()) {
      return NodeIds.Server;
    }

    return NodeId.parse(config.getNotifierNodeId());
  }

  @Override
  public synchronized void close() {
    closed = true;
    deleteSubscriptionQuietly();
    connectedClient.getClient().removeSessionActivityListener(sessionActivityListener);
    connectedClient.getClient().removeFaultListener(serviceFaultListener);
  }

  private void deleteSubscriptionQuietly() {
    if (subscription == null) {
      return;
    }

    try {
      setLastSubscriptionOperation(
          "delete-subscription(subscriptionId=%s)".formatted(subscription.getSubscriptionId().orElse(null))
      );
      subscription.delete();
    } catch (UaException e) {
      LOG.warn("Deleting OPC UA alarm subscription failed", e);
    } finally {
      subscription = null;
    }
  }

  private void setLastSubscriptionOperation(String lastSubscriptionOperation) {
    this.lastSubscriptionOperation = lastSubscriptionOperation;
  }
}
