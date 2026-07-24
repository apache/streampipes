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

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.OpcUaSession;
import org.eclipse.milo.opcua.stack.core.types.UaResponseMessageType;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.RequestHeader;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConnectedOpcUaClientTest {

  @Test
  void usesPullTimeoutAsRequestTimeoutHint() {
    var client = mock(OpcUaClient.class);
    var session = mock(OpcUaSession.class);
    var authenticationToken = new NodeId(1, "authentication-token");
    var requestHeader = mock(RequestHeader.class);
    var responseFuture = new CompletableFuture<UaResponseMessageType>();

    when(session.getAuthenticationToken()).thenReturn(authenticationToken);
    when(client.getSessionAsync()).thenReturn(CompletableFuture.completedFuture(session));
    when(client.newRequestHeader(authenticationToken, uint(250))).thenReturn(requestHeader);
    when(client.sendRequestAsync(any())).thenReturn(responseFuture);

    var connectedClient = new ConnectedOpcUaClient(client);
    connectedClient.readValuesAsync(List.of(new NodeId(2, "value")), 250);

    var requestCaptor = ArgumentCaptor.forClass(org.eclipse.milo.opcua.stack.core.types.UaRequestMessageType.class);
    verify(client).sendRequestAsync(requestCaptor.capture());

    var request = (ReadRequest) requestCaptor.getValue();
    assertSame(requestHeader, request.getRequestHeader());
    assertEquals(1, request.getNodesToRead().length);
  }
}
