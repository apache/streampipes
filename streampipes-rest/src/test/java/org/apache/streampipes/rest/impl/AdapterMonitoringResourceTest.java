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

package org.apache.streampipes.rest.impl;

import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.resource.management.AdapterResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AdapterMonitoringResourceTest {

  @Test
  void collectedMetricsDoesNotPollExtensions() {
    var requests = mock(ExtensionServiceRequestManager.class);
    var resources = mock(SpResourceManager.class);
    var adapters = mock(AdapterResourceManager.class);
    when(resources.manageAdapters()).thenReturn(adapters);
    var ids = List.of("missing-adapter");
    when(adapters.getReadableAdapterIds(isNull(), eq(ids))).thenReturn(List.of());

    var response = new AdapterMonitoringResource(requests, resources).getCollectedMetricsInfos(ids);

    assertEquals(200, response.getStatusCode().value());
    assertEquals(0, response.getBody().size());
    verify(adapters).getReadableAdapterIds(isNull(), eq(ids));
    verifyNoInteractions(requests);
  }
}
