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

import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceStatus;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServiceRegistrationManagerTest {

  @Test
  void newRegistrationIsPersistedWithoutTouchingOtherRegistrations() {
    var storage = mock(IExtensionsServiceStorage.class);
    var sameAddress = registration("iiot-old", "iiot", "extensions", 8090);
    when(storage.findAll()).thenReturn(List.of(sameAddress));
    var fresh = registration("iiot-new", "iiot", "extensions", 8090);

    new ServiceRegistrationManager(storage).addService(fresh, SpServiceStatus.REGISTERED);

    // Stale predecessors are removed by StaleRegistrationCleanup after probing them, not here.
    verify(storage, never()).deleteElement(any());
    verify(storage).persist(fresh);
  }

  @Test
  void reRegistrationOfKnownServiceDoesNotRemoveAnything() {
    var storage = mock(IExtensionsServiceStorage.class);
    var existing = registration("iiot-1", "iiot", "extensions", 8090);
    existing.setRev("3-abc");
    when(storage.getElementById("iiot-1")).thenReturn(existing);
    var refreshed = registration("iiot-1", "iiot", "extensions", 8090);

    new ServiceRegistrationManager(storage).addService(refreshed, SpServiceStatus.REGISTERED);

    verify(storage, never()).deleteElement(any());
    verify(storage).updateElement(refreshed);
  }

  private SpServiceRegistration registration(String svcId, String group, String host, int port) {
    var registration = new SpServiceRegistration();
    registration.setSvcId(svcId);
    registration.setSvcGroup(group);
    registration.setHost(host);
    registration.setPort(port);
    registration.setStatus(SpServiceStatus.HEALTHY);
    return registration;
  }
}
