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
package org.apache.streampipes.rest.core.base.impl;

import org.apache.streampipes.model.client.user.Role;
import org.apache.streampipes.user.management.model.PrincipalUserDetails;

import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbstractAuthGuardedRestResource extends AbstractRestResource {

  private static final List<String> adminRoles = Arrays.asList(
      Role.Constants.ROLE_ADMIN_VALUE,
      Role.Constants.ROLE_SERVICE_ADMIN_VALUE);
  @Context
  protected SecurityContext securityContext;

  protected boolean isAuthenticated() {
    return this.securityContext.getUserPrincipal() != null;
  }

  protected String getAuthenticatedUsername() {
    return this.securityContext.getUserPrincipal().getName();
  }

  protected PrincipalUserDetails<?> getPrincipal() {
    return (PrincipalUserDetails<?>) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  protected String getAuthenticatedUserSid() {
    return getPrincipal().getDetails().getPrincipalId();
  }

  protected boolean isAdminOrHasAnyAuthority(String... authorities) {
    List<String> allAuthorities = new ArrayList<>(Arrays.asList(authorities));
    allAuthorities.addAll(adminRoles);
    return hasAnyAuthority(allAuthorities);
  }

  protected boolean hasAnyAuthority(String... authorities) {
    return hasAnyAuthority(Arrays.asList(authorities));
  }

  protected boolean hasAnyAuthority(List<String> authorities) {
    return isAuthenticated()
        && SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getAuthorities()
            .stream()
            .anyMatch(a -> authorities.contains(a.getAuthority()));
  }

}
