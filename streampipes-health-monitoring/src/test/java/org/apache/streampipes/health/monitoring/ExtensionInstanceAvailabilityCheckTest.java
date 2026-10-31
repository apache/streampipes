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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class ExtensionInstanceAvailabilityCheckTest {

  private static final String SERVICE_ID = "svc-1";

  private IExtensionsServiceStorage storage;
  private ExtensionServiceRequestManager requests;
  private MockedStatic<ExtensionServiceRequests> requestFactory;
  private SpServiceRegistration service;
  private ExtensionInstanceAvailabilityCheck check;

  @BeforeEach
  void setUp() {
    storage = mock(IExtensionsServiceStorage.class);
    requests = mock(ExtensionServiceRequestManager.class);
    service = new SpServiceRegistration();
    service.setSvcId(SERVICE_ID);
    service.setStatus(SpServiceStatus.HEALTHY);
    when(storage.findAll()).thenReturn(List.of(service));
    requestFactory = mockStatic(ExtensionServiceRequests.class);
    requestFactory.when(() -> ExtensionServiceRequests.extensionInstanceHealth(any(), any()))
        .thenReturn(mock(ExtensionServiceRequest.class));
    check = new ExtensionInstanceAvailabilityCheck(storage, SERVICE_ID, requests, mock(SpResourceManager.class));
  }

  @AfterEach
  void tearDown() {
    requestFactory.close();
  }

  @Test
  void reachableServiceReportsItsInstances() throws IOException {
    var body = "{\"adapterInstanceStates\":{},\"runningPipelineElementInstanceIds\":[\"instance-a\"]}";
    when(requests.request(any()))
        .thenReturn(new ExtensionServiceOperationResult(200, body.getBytes(StandardCharsets.UTF_8)));

    var health = check.checkReachableRunningInstances();

    assertTrue(health.isPresent());
    assertEquals(Set.of("instance-a"), health.get().runningPipelineElementInstanceIds());
  }

  @Test
  void healthyServiceThatDoesNotAnswerIsUnknown() throws IOException {
    when(requests.request(any())).thenThrow(new IOException("no responders"));

    assertTrue(check.checkReachableRunningInstances().isEmpty());
    assertTrue(check.checkRunningInstances().runningPipelineElementInstanceIds().isEmpty());
  }

  @Test
  void healthyServiceAnsweringWithErrorIsUnknown() throws IOException {
    when(requests.request(any())).thenReturn(new ExtensionServiceOperationResult(500, null));

    assertTrue(check.checkReachableRunningInstances().isEmpty());
  }

  @Test
  void unhealthyServiceThatDoesNotAnswerHasNoInstances() throws IOException {
    service.setStatus(SpServiceStatus.UNHEALTHY);
    when(requests.request(any())).thenThrow(new IOException("no responders"));

    var health = check.checkReachableRunningInstances();

    assertTrue(health.isPresent());
    assertTrue(health.get().runningPipelineElementInstanceIds().isEmpty());
  }

  @Test
  void unregisteredServiceHasNoInstances() throws IOException {
    when(storage.findAll()).thenReturn(List.of());

    var health = check.checkReachableRunningInstances();

    assertTrue(health.isPresent());
    assertTrue(health.get().runningPipelineElementInstanceIds().isEmpty());
  }
}
