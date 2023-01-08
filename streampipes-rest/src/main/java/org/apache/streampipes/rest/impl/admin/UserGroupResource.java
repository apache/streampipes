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
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.storage.api.IUserGroupStorage;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/v2/usergroups")
@Component
public class UserGroupResource extends AbstractAuthGuardedRestResource {

  @GET
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response getAllUserGroups() {
    return ok(getUserGroupStorage().getAll());
  }

  @POST
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response addUserGroup(Group group) {
    getUserGroupStorage().createElement(group);
    return ok();
  }

  @PUT
  @Path("{groupId}")
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response updateUserGroup(@PathParam("groupId") String groupId,
                                  Group group) {
    if (!groupId.equals(group.getGroupId())) {
      return badRequest();
    } else {
      return ok(getUserGroupStorage().updateElement(group));
    }
  }

  @DELETE
  @Path("{groupId}")
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response deleteUserGroup(@PathParam("groupId") String groupId) {
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

  private IUserGroupStorage getUserGroupStorage() {
    return getNoSqlStorage().getUserGroupStorage();
  }
}
