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

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.storage.api.pipeline.IDataStreamStorage;
import org.apache.streampipes.storage.api.system.IGenericStorage;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DataStreamResourceManagerTest {

  @Test
  void deletionCleansAssetLinksAndPermissions() throws IOException {
    var storage = mock(IDataStreamStorage.class);
    var permissions = mock(PermissionResourceManager.class);
    var genericStorage = mock(IGenericStorage.class);
    var stream = new SpDataStream();
    stream.setElementId("stream-1");
    when(storage.getElementById("stream-1")).thenReturn(stream);

    new DataStreamResourceManager(storage, permissions, new ResourceDeletionManager(genericStorage))
        .delete("stream-1");

    verify(genericStorage).deleteAssetLinkToResource("stream-1");
    verify(permissions).findForObjectId("stream-1");
    verify(storage).deleteElement(stream);
  }

  @Test
  public void update() {
    IDataStreamStorage storage = mock(IDataStreamStorage.class);
    PermissionResourceManager permissionResourceManager = mock(PermissionResourceManager.class);
    DataStreamResourceManager dataStreamResourceManager = new DataStreamResourceManager(
        storage, permissionResourceManager, mock(ResourceDeletionManager.class));
    dataStreamResourceManager.update(new SpDataStream());

    verify(storage, times(1)).updateElement(any());
  }
}
