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

package org.apache.streampipes.manager.execution.http;

import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.resource.management.PermissionResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InvokeExtensionRequestTest {

  @Test
  void toJsonUsesPipelineOwnerAsCorrespondingUserWithoutMutatingOriginal() throws Exception {
    var pipelineId = "pipeline-1";
    var ownerSid = "owner-sid";
    var storedUser = "stale-user";
    var resourceManager = mock(SpResourceManager.class);
    var permissionResourceManager = mock(PermissionResourceManager.class);
    var permission = new Permission();
    permission.setOwnerSid(ownerSid);
    when(resourceManager.managePermissions()).thenReturn(permissionResourceManager);
    when(permissionResourceManager.findForObjectId(pipelineId)).thenReturn(List.of(permission));

    var sinkInvocation = new DataSinkInvocation();
    sinkInvocation.setCorrespondingUser(storedUser);
    sinkInvocation.setSelectedServiceId("service-1");

    var request = new InvokeExtensionRequest(mock(ExtensionServiceRequestManager.class), resourceManager);

    var json = request.toJson(sinkInvocation, pipelineId);

    var payload = JacksonSerializer.getObjectMapper().readValue(json, DataSinkInvocation.class);
    assertEquals(ownerSid, payload.getCorrespondingUser());
    assertEquals("service-1", payload.getSelectedServiceId());
    assertEquals(storedUser, sinkInvocation.getCorrespondingUser());
  }
}
