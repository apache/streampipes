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

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.extensions.api.migration.IModelMigrator;
import org.apache.streampipes.extensions.management.client.StreamPipesClientResolver;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.ModelMigratorConfig;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StreamPipesExtensionsServiceBaseTest {

  @Test
  void reconnectCompletesReadinessHandshakeWithoutMigrators() {
    assertReconnectHandshake(List.of(), List.of());
  }

  @Test
  void reconnectResubmitsConfiguredMigrations() {
    var migrator = mock(IModelMigrator.class);
    var config = new ModelMigratorConfig("adapter", SpServiceTagPrefix.ADAPTER, 1, 2);
    when(migrator.config()).thenReturn(config);
    assertReconnectHandshake(List.of(migrator), List.of(config));
  }

  private void assertReconnectHandshake(List<IModelMigrator<?, ?>> migrators, List<ModelMigratorConfig> configs) {
    var service = mock(StreamPipesExtensionsServiceBase.class, CALLS_REAL_METHODS);
    var definition = mock(SpServiceDefinition.class);
    when(definition.getMigrators()).thenReturn(migrators);
    var registration = new SpServiceRegistration();
    registration.setSvcId("reconnected-service");
    var client = mock(StreamPipesClient.class, RETURNS_DEEP_STUBS);
    var admin = client.adminApi();

    try (var resolvers = mockConstruction(StreamPipesClientResolver.class,
        (resolver, context) -> when(resolver.makeStreamPipesClientInstance()).thenReturn(client))) {
      service.refreshServiceRegistration(definition, registration);

      var requests = inOrder(admin);
      requests.verify(admin).registerService(registration);
      requests.verify(admin).registerMigrations(configs, registration.getSvcId());
      requests.verifyNoMoreInteractions();
      verify(service, never()).afterServiceRegistered(definition, registration);
    }
  }
}
