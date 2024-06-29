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
package org.apache.streampipes.rest.impl.admin;

import org.apache.streampipes.model.client.user.Group;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.storage.api.CRUDStorage;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/usergroups")
public class UserGroupResource extends AbstractAuthGuardedRestResource {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<List<Group>> getAllUserGroups() {
    return ok(getUserGroupStorage().findAll());
  }

  @PostMapping
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<Void> addUserGroup(@RequestBody Group group) {
    getUserGroupStorage().persist(group);
    return ok();
  }

  @PutMapping(path = "{groupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<Group> updateUserGroup(@PathVariable("groupId") String groupId,
                                           @RequestBody Group group) {
    if (!groupId.equals(group.getGroupId())) {
      throw new SpMessageException(
          HttpStatus.BAD_REQUEST,
          Notifications.error("Wrong group id provided"));
    } else {
      return ok(getUserGroupStorage().updateElement(group));
    }
  }

  @DeleteMapping(path = "{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<Void> deleteUserGroup(@PathVariable("groupId") String groupId) {
    Group group = getUserGroupStorage().getElementById(groupId);
    if (group != null) {
      getUserGroupStorage().deleteElement(group);

      // TODO remove group from all users
      getUserStorage().getAllUsers().forEach(user -> {
        if (user.getGroups().contains(groupId)) {
          user.getGroups().remove(groupId);
          getUserStorage().updateUser(user);
        }
      });
      return ok();
    } else {
      return badRequest();
    }
  }

  private CRUDStorage<Group> getUserGroupStorage() {
    return getNoSqlStorage().getUserGroupStorage();
  }
}
