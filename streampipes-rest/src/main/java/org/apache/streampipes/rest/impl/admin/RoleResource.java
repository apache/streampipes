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

import org.apache.streampipes.model.client.user.Role;
import org.apache.streampipes.rest.core.base.impl.DefaultCRUDRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/admin/roles")
@PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
public class RoleResource extends DefaultCRUDRestResource<Role> {

  public RoleResource() {
    super(StorageDispatcher.INSTANCE.getNoSqlStore().getRoleStorage());
  }
}