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

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.subscriptions.OpcUaSubscription;
import org.eclipse.milo.opcua.stack.core.types.structured.EventFilter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OpcUaAlarmEventSubscriberTest {

  @Test
  void deletesSubscriptionWhenMonitoredItemCreationFails() throws Exception {
    var client = mock(OpcUaClient.class);
    var connectedClient = mock(ConnectedOpcUaClient.class);
    var config = new OpcUaAlarmAdapterConfig();
    var eventMapper = mock(OpcUaAlarmEventMapper.class);
    var eventFilter = mock(OpcUaAlarmEventFilter.class);
    var subscription = mock(OpcUaSubscription.class);

    when(connectedClient.getClient()).thenReturn(client);
    when(eventMapper.makeEventFilter(client.getStaticEncodingContext()))
        .thenReturn(mock(EventFilter.class));
    when(subscription.createMonitoredItems()).thenReturn(List.of());

    var subscriber = spy(new OpcUaAlarmEventSubscriber(
        connectedClient,
        config,
        event -> { },
        eventMapper,
        eventFilter
    ));
    doReturn(subscription).when(subscriber).createManagedSubscription();

    assertThrows(org.eclipse.milo.opcua.stack.core.UaException.class, subscriber::start);
    subscriber.close();

    verify(subscription, times(1)).delete();
  }
}
