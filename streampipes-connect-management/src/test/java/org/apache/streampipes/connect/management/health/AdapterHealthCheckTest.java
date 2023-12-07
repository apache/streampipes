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

package org.apache.streampipes.connect.management.health;

import org.apache.streampipes.connect.management.management.AdapterMasterManagement;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.storage.api.IAdapterStorage;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdapterHealthCheckTest {

  private IAdapterStorage adapterInstanceStorageMock;

  @Before
  public void setUp() {
    adapterInstanceStorageMock = mock(IAdapterStorage.class);
  }

  @Test
  public void getAllRunningInstancesAdapterDescriptionsEmpty() {
    when(adapterInstanceStorageMock.getAllAdapters()).thenReturn(List.of());

    var healthCheck = new AdapterHealthCheck(adapterInstanceStorageMock, new AdapterMasterManagement());
    var result = healthCheck.getAllRunningInstancesAdapterDescriptions();

    assertTrue(result.isEmpty());
  }

  @Test
  public void getAllRunningInstancesAdapterDescriptionsMixed() {

    var nameRunningAdapter = "running-adapter";
    var nameStoppedAdapter = "stopped-adapter";

    var stoppedAdapter = new AdapterDescription();
    stoppedAdapter.setElementId(nameStoppedAdapter);
    stoppedAdapter.setRunning(false);

    var runningAdapter = new AdapterDescription();
    runningAdapter.setElementId(nameRunningAdapter);
    runningAdapter.setRunning(true);

    when(adapterInstanceStorageMock.getAllAdapters()).thenReturn(List.of(stoppedAdapter, runningAdapter));

    var healthCheck = new AdapterHealthCheck(adapterInstanceStorageMock, new AdapterMasterManagement());
    var result = healthCheck.getAllRunningInstancesAdapterDescriptions();

    assertEquals(1, result.size());
    assertTrue(result.containsKey(nameRunningAdapter));
    assertEquals(runningAdapter, result.get(nameRunningAdapter));

  }

}
