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

import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import java.util.List;

@Path("/v2/admin/permissions")
@Component
public class PermissionResource extends AbstractAuthGuardedRestResource {

  @GET
  @Path("objects/{objectInstanceId}")
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public List<Permission> getPermissionForObject(@PathParam("objectInstanceId") String objectInstanceId) {
    return getSpResourceManager().managePermissions().findForObjectId(objectInstanceId);
  }

  @PUT
  @Path("{permissionId}")
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public void updatePermission(@PathParam("permissionId") String permissionId,
                               Permission permission) {
    if (permissionId.equals(permission.getPermissionId())) {
      getSpResourceManager().managePermissions().update(permission);
    }
  }


}
