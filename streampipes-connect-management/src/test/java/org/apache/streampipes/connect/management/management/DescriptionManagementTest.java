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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.resource.management.AdapterResourceManager;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DescriptionManagementTest {

  private final IAdapterStorage descriptions = mock(IAdapterStorage.class);
  private final IAdapterStorage instances = mock(IAdapterStorage.class);
  private final AdapterResourceManager adapters = mock(AdapterResourceManager.class);
  private final DescriptionManagement management =
      new DescriptionManagement(mock(WorkerRestClient.class), adapters, descriptions);

  @Test
  void looksUpDescriptionByAppIdInInjectedStorage() {
    var description = description("app");
    when(descriptions.findAll()).thenReturn(List.of(description));

    assertEquals(description, management.getAdapter("app").orElseThrow());
  }

  @Test
  void deletesUnusedDescriptionFromDescriptionStorage() {
    when(descriptions.getElementById("description")).thenReturn(description("app"));
    when(adapters.getDb()).thenReturn(instances);
    when(instances.findAll()).thenReturn(List.of(description("other-app")));

    management.deleteAdapterDescription("description");

    verify(descriptions).deleteElementById("description");
    verify(instances, never()).deleteElementById("description");
  }

  @Test
  void preservesDescriptionUsedByAnAdapterInstance() {
    when(descriptions.getElementById("description")).thenReturn(description("app"));
    when(adapters.getDb()).thenReturn(instances);
    when(instances.findAll()).thenReturn(List.of(description("app")));

    assertThrows(SpRuntimeException.class, () -> management.deleteAdapterDescription("description"));

    verify(descriptions, never()).deleteElementById("description");
  }

  private AdapterDescription description(String appId) {
    var description = new AdapterDescription();
    description.setAppId(appId);
    return description;
  }
}
