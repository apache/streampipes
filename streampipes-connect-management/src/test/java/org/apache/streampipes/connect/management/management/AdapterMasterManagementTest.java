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

package org.apache.streampipes.connect.management.management;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.prometheus.adapter.AdapterMetricsManager;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.resource.management.AdapterResourceManager;
import org.apache.streampipes.storage.couchdb.impl.AdapterInstanceStorageImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdapterMasterManagementTest {

  @Test
  public void getAdapterFailNull() {
    AdapterInstanceStorageImpl adapterStorage = mock(AdapterInstanceStorageImpl.class);
    AdapterResourceManager resourceManager = mock(AdapterResourceManager.class);
    when(adapterStorage.getAllAdapters()).thenReturn(null);

    AdapterMasterManagement adapterMasterManagement =
        new AdapterMasterManagement(
            adapterStorage,
            resourceManager,
            null,
            AdapterMetricsManager.INSTANCE.getAdapterMetrics()
        );

    assertThrows(AdapterException.class, () -> adapterMasterManagement.getAdapter("id2"));
  }

  @Test
  public void getAdapterFail() {
    List<AdapterDescription> adapterDescriptions = List.of(new AdapterDescription());
    AdapterInstanceStorageImpl adapterStorage = mock(AdapterInstanceStorageImpl.class);
    AdapterResourceManager resourceManager = mock(AdapterResourceManager.class);
    when(adapterStorage.getAllAdapters()).thenReturn(adapterDescriptions);

    AdapterMasterManagement adapterMasterManagement =
        new AdapterMasterManagement(
            adapterStorage,
            resourceManager,
            null,
            AdapterMetricsManager.INSTANCE.getAdapterMetrics()
        );

    assertThrows(AdapterException.class, () -> adapterMasterManagement.getAdapter("id2"));
  }

  @Test
  public void getAllAdaptersSuccess() throws AdapterException {
    List<AdapterDescription> adapterDescriptions = List.of(new AdapterDescription());
    AdapterInstanceStorageImpl adapterStorage = mock(AdapterInstanceStorageImpl.class);
    AdapterResourceManager resourceManager = mock(AdapterResourceManager.class);
    when(adapterStorage.getAllAdapters()).thenReturn(adapterDescriptions);

    AdapterMasterManagement adapterMasterManagement =
        new AdapterMasterManagement(
            adapterStorage,
            resourceManager,
            null,
            AdapterMetricsManager.INSTANCE.getAdapterMetrics()
        );

    List<AdapterDescription> result = adapterMasterManagement.getAllAdapterInstances();

    Assertions.assertEquals(1, result.size());
  }

  @Test
  public void getAllAdaptersFail() {
    AdapterInstanceStorageImpl adapterStorage = mock(AdapterInstanceStorageImpl.class);
    AdapterResourceManager resourceManager = mock(AdapterResourceManager.class);
    when(adapterStorage.getAllAdapters()).thenReturn(null);

    AdapterMasterManagement adapterMasterManagement =
        new AdapterMasterManagement(
            adapterStorage,
            resourceManager,
            null,
            AdapterMetricsManager.INSTANCE.getAdapterMetrics()
        );

    assertThrows(AdapterException.class, adapterMasterManagement::getAllAdapterInstances);

  }
}
