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

package org.apache.streampipes.resource.management;

import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdapterDescriptionResourceManagerTest {

  @Test
  void deletesInjectedDescriptionAndItsPermissions() {
    var storage = mock(IAdapterStorage.class);
    var permissions = mock(PermissionResourceManager.class);
    var description = new AdapterDescription();
    description.setElementId("description");
    var permission = new Permission();
    when(storage.getElementById("description")).thenReturn(description);
    when(permissions.findForObjectId("description")).thenReturn(List.of(permission));

    new AdapterDescriptionResourceManager(storage, permissions).delete("description");

    verify(permissions).delete(permission);
    verify(storage).deleteElement(description);
  }
}
