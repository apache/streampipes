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

package org.apache.streampipes.service.core;

import org.apache.streampipes.dataexplorer.management.DatasetQueryService;
import org.apache.streampipes.dataexplorer.management.DatasetServices;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.explorer.IChartStorage;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DatasetQueryConfigurationTest {
  @Test
  void contextShutdownClosesDatasetServices() {
    var queries = mock(DatasetQueryService.class);
    try (var context = new AnnotationConfigApplicationContext()) {
      context.registerBean(DatasetServices.class, () -> new DatasetServices(queries, null, null));
      context.refresh();
    }
    verify(queries).close();
  }

  @Test
  void explicitlyImportedConfigurationAssemblesServicesWithoutOpeningDatabaseConnections() {
    assertTrue(Arrays.asList(StreamPipesCoreApplication.class.getAnnotation(Import.class).value())
        .contains(DatasetQueryConfiguration.class));
    try (var context = new AnnotationConfigApplicationContext()) {
      context.registerBean(IChartStorage.class, () -> mock(IChartStorage.class));
      context.registerBean(SpResourceManager.class, () -> mock(SpResourceManager.class, RETURNS_DEEP_STUBS));
      context.register(DatasetQueryConfiguration.class);
      context.refresh();
      var services = context.getBean(DatasetServices.class);
      assertNotNull(services.queries());
      assertNotNull(services.administration());
      assertNotNull(services.exports());
    }
  }
}
