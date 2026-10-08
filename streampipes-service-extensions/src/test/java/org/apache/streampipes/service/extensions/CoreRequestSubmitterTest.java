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

package org.apache.streampipes.service.extensions;

import org.apache.streampipes.client.api.IAdminApi;
import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.client.api.config.ClientConnectionUrlResolver;
import org.apache.streampipes.commons.exceptions.SpHttpErrorStatusCode;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.InternalBrokerProvider;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistrationResponse;
import org.apache.streampipes.model.grounding.BrokerConfiguration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CoreRequestSubmitterTest {
  @Test
  void permanentRegistrationRejectionDoesNotRetryForever() {
    var client = mock(IStreamPipesClient.class);
    var admin = mock(IAdminApi.class);
    when(client.adminApi()).thenReturn(admin);
    when(client.getConnectionConfig()).thenReturn(mock(ClientConnectionUrlResolver.class));
    var registration = new SpServiceRegistration();
    when(admin.registerService(registration)).thenThrow(new SpHttpErrorStatusCode("unsupported protocol", 400));
    assertThrows(IllegalStateException.class,
        () -> new CoreRequestSubmitter().submitRegistrationRequest(client, registration));
    verify(admin, times(1)).registerService(registration);
  }

  @Test
  @Timeout(2)
  void brokerFailureStopsStartupAfterAcceptedRegistration() {
    var client = mock(IStreamPipesClient.class);
    var admin = mock(IAdminApi.class);
    when(client.adminApi()).thenReturn(admin);
    when(client.getConnectionConfig()).thenReturn(mock(ClientConnectionUrlResolver.class));
    var registration = new SpServiceRegistration();
    var broker = new BrokerConfiguration();
    when(admin.registerService(registration)).thenReturn(new SpServiceRegistrationResponse(broker));
    try (var provider = mockStatic(InternalBrokerProvider.class)) {
      provider.when(() -> InternalBrokerProvider.configure(broker)).thenThrow(new SpRuntimeException("broker unavailable"));
      assertThrows(SpRuntimeException.class,
          () -> new CoreRequestSubmitter().submitRegistrationRequest(client, registration));
      verify(admin, times(1)).registerService(registration);
    }
  }
}
