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

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class ServiceRegistrationBrokerTest {
  @Test
  void rejectsMissingOrUnsupportedCapabilitiesBeforeWriting() {
    var storage = mock(IExtensionsServiceStorage.class);
    var manager = new ServiceRegistrationManager(storage);
    var registration = new SpServiceRegistration();
    assertThrows(IllegalArgumentException.class, () -> manager.registerService(registration));
    registration.setSupportedProtocols(Set.of("unknown"));
    assertThrows(IllegalArgumentException.class, () -> manager.registerService(registration));
    verifyNoInteractions(storage);
  }

  @Test
  void acceptsSupportedSupersetAndReturnsCoreSelection() {
    var storage = mock(IExtensionsServiceStorage.class);
    var registration = new SpServiceRegistration();
    registration.setSupportedProtocols(Set.of("nats", "kafka", "mqtt", "pulsar"));
    var response = new ServiceRegistrationManager(storage).registerService(registration);
    assertEquals(Environments.getEnvironment().getPrioritizedProtocol().getValueOrDefault(),
        response.getInternalBroker().getProtocolId());
    verify(storage).persist(registration);
  }
}
