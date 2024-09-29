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

import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.client.user.PrincipalType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.message.SuccessMessage;
import org.apache.streampipes.rest.ResetManagement;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;

import java.util.ArrayList;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/reset")
public class ResetResource extends AbstractAuthGuardedRestResource {

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Resets StreamPipes instance")
  public ResponseEntity<SuccessMessage> reset() {
    ResetManagement.reset(getAuthenticatedUsername());
    var userStorage = getUserStorage();
    // Delete all users other than current user (admin) and their resources
    var allUsers = new ArrayList<Principal>(userStorage.getAllUsers());
    for (var user : allUsers) {
      if (user.getPrincipalType() == PrincipalType.USER_ACCOUNT
              && !user.getPrincipalId().equals(getAuthenticatedUserSid())) {
        ResetManagement.reset(user.getUsername());
        userStorage.deleteUser(user.getPrincipalId());
      }
    }
    var message = Notifications.success("Reset of system successfully performed");
    return ok(message);
  }

}
