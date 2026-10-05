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

package org.apache.streampipes.service.core.extensions;

import org.apache.streampipes.manager.api.extensions.ExtensionServiceOperationResult;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequest;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTarget;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTag;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerTopics;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class TransportAwareExtensionServiceRequestManagerTest {

  private final ExtensionServiceRequestManager http = mock(ExtensionServiceRequestManager.class);
  private final NatsExtensionServiceRequestManager nats = mock(NatsExtensionServiceRequestManager.class);
  private final IExtensionsServiceStorage storage = mock(IExtensionsServiceStorage.class);
  private final ExtensionServiceRequest request = ExtensionServiceRequest.get(
      ExtensionServiceRequestTarget.of("http://extension", "service-1", "monitoring"), null);
  private final ExtensionServiceOperationResult response = new ExtensionServiceOperationResult(200, null);

  @Test
  void autoModeReflectsChangesInStoredTransportTags() throws IOException {
    var registration = new SpServiceRegistration();
    registration.setTags(Set.of(
        SpServiceTag.create(SpServiceTagPrefix.CUSTOM, ExtensionServiceBrokerTopics.TRANSPORT_TAG_NATS)));
    when(storage.getElementById("service-1")).thenReturn(registration);
    when(nats.request(request)).thenReturn(response);
    when(http.request(request)).thenReturn(response);
    var manager = manager(CoreExtensionTransportMode.AUTO);

    assertSame(response, manager.request(request));
    registration.setTags(Set.of());
    assertSame(response, manager.request(request));

    verify(nats).request(request);
    verify(http).request(request);
    verify(storage, times(2)).getElementById("service-1");
  }

  @Test
  void autoModeUsesHttpWhenServiceIsMissing() throws IOException {
    when(http.request(request)).thenReturn(response);

    assertSame(response, manager(CoreExtensionTransportMode.AUTO).request(request));

    verifyNoInteractions(nats);
  }

  @Test
  void httpModeDoesNotQueryStorage() throws IOException {
    when(http.request(request)).thenReturn(response);

    assertSame(response, manager(CoreExtensionTransportMode.HTTP).request(request));

    verifyNoInteractions(storage, nats);
  }

  @Test
  void natsModeDoesNotQueryStorage() throws IOException {
    when(nats.request(request)).thenReturn(response);

    assertSame(response, manager(CoreExtensionTransportMode.NATS).request(request));

    verifyNoInteractions(storage, http);
  }

  private TransportAwareExtensionServiceRequestManager manager(CoreExtensionTransportMode mode) {
    return new TransportAwareExtensionServiceRequestManager(http, nats, mode, storage);
  }
}
