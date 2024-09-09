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
package org.apache.streampipes.user.management.util;

import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.authorization.RoleManager;

import java.util.HashSet;
import java.util.Set;

public class GrantedAuthoritiesBuilder {

  private final Set<String> allAuthorities;
  private final Principal principal;

  public GrantedAuthoritiesBuilder(Principal principal) {
    this.allAuthorities = new HashSet<>();
    this.principal = principal;
  }

  public Set<String> buildAllAuthorities() {
    allAuthorities.addAll(buildAllUserRoles());
    allAuthorities.addAll(buildAllGroupRoles());

    return allAuthorities;
  }

  private Set<String> buildAllUserRoles() {
    return buildAllRoles(principal.getRoles());
  }

  private Set<String> buildAllGroupRoles() {
    Set<String> allRoles = new HashSet<>();
    principal.getGroups().forEach(groupId -> {
      Set<String> groupRoles =
          StorageDispatcher.INSTANCE.getNoSqlStore().getUserGroupStorage().getElementById(groupId).getRoles();
      allRoles.addAll(buildAllRoles(groupRoles));
    });

    return allRoles;
  }

  private Set<String> buildAllRoles(Set<String> originalRoles) {
    Set<String> roles = new HashSet<>();
    originalRoles.forEach(role -> {
      roles.addAll(new RoleManager().getPrivileges(role));
    });
    return roles;
  }
}
