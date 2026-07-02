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

package org.apache.streampipes.extensions.connectors.opcua.adapter;

import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.connectors.opcua.client.ConnectedOpcUaClient;
import org.apache.streampipes.extensions.connectors.opcua.client.OpcUaClientProvider;
import org.apache.streampipes.extensions.connectors.opcua.config.OpcUaAdapterConfig;
import org.apache.streampipes.extensions.connectors.opcua.config.SharedUserConfiguration;
import org.apache.streampipes.extensions.connectors.opcua.model.node.BasicVariableNodeInfo;
import org.apache.streampipes.extensions.connectors.opcua.model.node.OpcUaNode;
import org.apache.streampipes.model.connect.guess.FieldStatusInfo;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.subscriptions.OpcUaMonitoredItem;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OpcUaAdapterSubscriptionTest {

  @Test
  @SuppressWarnings("unchecked")
  void shouldRouteSubscriptionValuesByNodeIdForDuplicateDisplayNames() throws Exception {
    var firstNodeId = new NodeId(2, "LineA.Temperature");
    var secondNodeId = new NodeId(2, "LineB.Temperature");
    var firstNode = new TestNode(firstNodeId, "Temperature", "lineA");
    var secondNode = new TestNode(secondNodeId, "Temperature", "lineB");
    var collector = mock(IEventCollector.class);
    var adapter = makeAdapter(collector, firstNode, secondNode);

    adapter.onSubscriptionValue(
        OpcUaMonitoredItem.newDataItem(firstNodeId),
        DataValue.valueOnly(new Variant(101))
    );
    adapter.onSubscriptionValue(
        OpcUaMonitoredItem.newDataItem(secondNodeId),
        DataValue.valueOnly(new Variant(202))
    );

    ArgumentCaptor<Map<String, Object>> eventCaptor = ArgumentCaptor.forClass(Map.class);
    verify(collector).collect(eventCaptor.capture());
    assertEquals(101, eventCaptor.getValue().get("lineA"));
    assertEquals(202, eventCaptor.getValue().get("lineB"));
  }

  @Test
  void shouldNotPublishBadStatusSubscriptionValuesWhenIncompleteEventsAreIgnored() throws Exception {
    var nodeId = new NodeId(2, "IntermittentValue");
    var node = new TestNode(nodeId, "IntermittentValue", "intermittent");
    var collector = mock(IEventCollector.class);
    var adapter = makeAdapter(collector, node);

    adapter.onSubscriptionValue(
        OpcUaMonitoredItem.newDataItem(nodeId),
        new DataValue(new Variant(1), new StatusCode(StatusCodes.Bad_NotReadable))
    );

    verify(collector, never()).collect(org.mockito.ArgumentMatchers.any());
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldRemoveStaleSubscriptionValueBeforeSendingIncompleteEvent() throws Exception {
    var firstNodeId = new NodeId(2, "StableCounter");
    var secondNodeId = new NodeId(2, "IntermittentValue");
    var firstNode = new TestNode(firstNodeId, "StableCounter", "stable");
    var secondNode = new TestNode(secondNodeId, "IntermittentValue", "intermittent");
    var collector = mock(IEventCollector.class);
    var adapter = makeAdapter(collector, SharedUserConfiguration.INCOMPLETE_OPTION_SEND, firstNode, secondNode);

    adapter.onSubscriptionValue(
        OpcUaMonitoredItem.newDataItem(firstNodeId),
        DataValue.valueOnly(new Variant(1))
    );
    adapter.onSubscriptionValue(
        OpcUaMonitoredItem.newDataItem(secondNodeId),
        DataValue.valueOnly(new Variant(2))
    );
    adapter.onSubscriptionValue(
        OpcUaMonitoredItem.newDataItem(secondNodeId),
        new DataValue(new Variant(3), new StatusCode(StatusCodes.Bad_NotReadable))
    );

    ArgumentCaptor<Map<String, Object>> eventCaptor = ArgumentCaptor.forClass(Map.class);
    verify(collector, org.mockito.Mockito.times(2)).collect(eventCaptor.capture());

    var incompleteEvent = eventCaptor.getAllValues().get(1);
    assertEquals(1, incompleteEvent.get("stable"));
    assertFalse(incompleteEvent.containsKey("intermittent"));
  }

  private OpcUaAdapter makeAdapter(IEventCollector collector,
                                   TestNode... nodes) throws Exception {
    return makeAdapter(collector, SharedUserConfiguration.INCOMPLETE_OPTION_IGNORE, nodes);
  }

  private OpcUaAdapter makeAdapter(IEventCollector collector,
                                   String incompleteEventStrategy,
                                   TestNode... nodes) throws Exception {
    var adapter = new OpcUaAdapter(mock(OpcUaClientProvider.class));
    var nodeIdToNodeMapping = getNodeIdToNodeMapping(adapter);
    for (var node : nodes) {
      nodeIdToNodeMapping.put(node.nodeInfo().getNodeId().toString(), node);
    }

    var config = new OpcUaAdapterConfig();
    config.setIncompleteEventStrategy(incompleteEventStrategy);

    setField(adapter, "collector", collector);
    setField(adapter, "connectedClient", new ConnectedOpcUaClient(mock(OpcUaClient.class)));
    setField(adapter, "numberOfEventProperties", nodes.length);
    setField(adapter, "opcUaAdapterConfig", config);
    return adapter;
  }

  @SuppressWarnings("unchecked")
  private Map<String, OpcUaNode> getNodeIdToNodeMapping(OpcUaAdapter adapter) throws Exception {
    Field field = adapter.getClass().getDeclaredField("nodeIdToNodeMapping");
    field.setAccessible(true);
    return (Map<String, OpcUaNode>) field.get(adapter);
  }

  private void setField(Object target,
                        String fieldName,
                        Object value) throws Exception {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }

  private static class TestNode implements OpcUaNode {

    private final BasicVariableNodeInfo nodeInfo;
    private final String eventPropertyName;

    TestNode(NodeId nodeId,
             String displayName,
             String eventPropertyName) {
      this.nodeInfo = mock(BasicVariableNodeInfo.class);
      this.eventPropertyName = eventPropertyName;

      when(this.nodeInfo.getNodeId()).thenReturn(nodeId);
      when(this.nodeInfo.getDisplayName()).thenReturn(displayName);
      when(this.nodeInfo.getDesiredName("")).thenReturn(eventPropertyName);
    }

    @Override
    public BasicVariableNodeInfo nodeInfo() {
      return nodeInfo;
    }

    @Override
    public int getNumberOfEventProperties(OpcUaClient client) {
      return 1;
    }

    @Override
    public void addToEvent(OpcUaClient client,
                           Map<String, Object> event,
                           Variant variant) {
      event.put(eventPropertyName, variant.getValue());
    }

    @Override
    public void addToEventPreview(OpcUaClient client,
                                  Map<String, Object> eventPreview,
                                  Map<String, FieldStatusInfo> fieldStatusInfos,
                                  Variant variant,
                                  FieldStatusInfo fieldStatusInfo) {
      eventPreview.put(eventPropertyName, variant.getValue());
    }
  }
}
