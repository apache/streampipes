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

import org.apache.streampipes.connect.management.management.WorkerRestClient;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.manager.pipeline.update.ChartSchemaUpdateCoordinator;
import org.apache.streampipes.model.client.user.PrincipalType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.message.SuccessMessage;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.rest.ResetManagement;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.storage.api.explorer.IChartStorage;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v2/reset")
@Conditional(ResetEndpointEnabledCondition.class)
@PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
public class ResetResource extends AbstractAuthGuardedRestResource {

  private final ResetManagement resetManagement;

  public ResetResource(WorkerRestClient workerRestClient,
                       ExtensionServiceRequestManager requestManager,
                       SpResourceManager resourceManager) {
    IExtensionsServiceStorage extensionsServiceStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getExtensionsServiceStorage();
    var pipelineManager = new PipelineManager(
        StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI(),
        resourceManager
    );
    this.resetManagement = new ResetManagement(
        workerRestClient,
        extensionsServiceStorage,
        requestManager,
        pipelineManager,
        resourceManager,
        new ChartSchemaUpdateCoordinator((IChartStorage) resourceManager.manageCharts().getDb())
    );
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Resets StreamPipes instance")
  public ResponseEntity<SuccessMessage> reset() {
    resetManagement.reset(getAuthenticatedUsername());
    var userStorage = getUserStorage();


    // Delete all users other than current user (admin) and their resources
    var allUsers = new ArrayList<>(userStorage.getAllUsers());
    for (var user : allUsers) {
      if (user.getPrincipalType() == PrincipalType.USER_ACCOUNT
          && !user.getPrincipalId().equals(getAuthenticatedUserSid())) {
        resetManagement.reset(user.getUsername());
        userStorage.deleteUser(user.getPrincipalId());
      }
    }

    // Delete all user Groups
    var allUserGroups = getNoSqlStorage().getUserGroupStorage().findAll();
    for (var group : allUserGroups) {
      getNoSqlStorage().getUserGroupStorage().deleteElementById(group.getElementId());
    }

    // Delete all connect script templates
    var allScriptTemplates = getNoSqlStorage().getTransformationScriptTemplateStorage().findAll();
    for (var template : allScriptTemplates) {
      getNoSqlStorage().getTransformationScriptTemplateStorage().deleteElementById(template.getElementId());
    }

    var message = Notifications.success("Reset of system successfully performed");
    return ok(message);
  }

}
