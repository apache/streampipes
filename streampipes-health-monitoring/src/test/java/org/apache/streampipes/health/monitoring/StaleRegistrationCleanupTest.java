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

package org.apache.streampipes.health.monitoring;

import org.apache.streampipes.manager.api.extensions.ExtensionServiceOperationResult;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequest;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequests;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceStatus;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StaleRegistrationCleanupTest {

  private IExtensionsServiceStorage storage;
  private ExtensionServiceRequestManager requests;
  private MockedStatic<ExtensionServiceRequests> requestFactory;
  private StaleRegistrationCleanup cleanup;

  @BeforeEach
  void setUp() {
    storage = mock(IExtensionsServiceStorage.class);
    requests = mock(ExtensionServiceRequestManager.class);
    requestFactory = mockStatic(ExtensionServiceRequests.class);
    requestFactory.when(() -> ExtensionServiceRequests.serviceHealth(any(), any()))
        .thenReturn(mock(ExtensionServiceRequest.class));
    cleanup = new StaleRegistrationCleanup(storage, requests, mock(SpResourceManager.class));
  }

  @AfterEach
  void tearDown() {
    requestFactory.close();
  }

  @Test
  void removesPredecessorThatNobodyAnswersFor() throws IOException {
    var stale = registration("iiot-old", "iiot", "extensions", 8090);
    var fresh = registration("iiot-new", "iiot", "extensions", 8090);
    when(storage.findAll()).thenReturn(List.of(stale, fresh));
    when(requests.request(any())).thenThrow(new IOException("connection refused"));

    cleanup.removePredecessorsOf(fresh);

    verify(storage).deleteElement(stale);
  }

  @Test
  void removesPredecessorWhoseAddressIsNowServedByAnotherId() throws IOException {
    var stale = registration("iiot-old", "iiot", "extensions", 8090);
    var fresh = registration("iiot-new", "iiot", "extensions", 8090);
    when(storage.findAll()).thenReturn(List.of(stale, fresh));
    when(requests.request(any())).thenReturn(new ExtensionServiceOperationResult(404, null));

    cleanup.removePredecessorsOf(fresh);

    verify(storage).deleteElement(stale);
  }

  @Test
  void keepsLiveReplicaAtSameAddress() throws IOException {
    var replica = registration("iiot-a", "iiot", "extensions", 8090);
    var fresh = registration("iiot-b", "iiot", "extensions", 8090);
    when(storage.findAll()).thenReturn(List.of(replica, fresh));
    when(requests.request(any())).thenReturn(new ExtensionServiceOperationResult(200, null));

    cleanup.removePredecessorsOf(fresh);

    verify(storage, never()).deleteElement(any());
  }

  @Test
  void onlyProbesRegistrationsOfSameGroupAndAddress() throws IOException {
    var otherPort = registration("iiot-other", "iiot", "extensions", 8091);
    var otherGroup = registration("ai-old", "ai", "extensions", 8090);
    var otherHost = registration("iiot-remote", "iiot", "node-2", 8090);
    var fresh = registration("iiot-new", "iiot", "extensions", 8090);
    when(storage.findAll()).thenReturn(List.of(otherPort, otherGroup, otherHost, fresh));

    cleanup.removePredecessorsOf(fresh);

    verify(requests, times(0)).request(any());
    verify(storage, never()).deleteElement(any());
  }

  private SpServiceRegistration registration(String svcId, String group, String host, int port) {
    var registration = new SpServiceRegistration();
    registration.setSvcId(svcId);
    registration.setSvcGroup(group);
    registration.setHost(host);
    registration.setPort(port);
    registration.setHealthCheckPath("svchealth/" + svcId);
    registration.setStatus(SpServiceStatus.HEALTHY);
    return registration;
  }
}
