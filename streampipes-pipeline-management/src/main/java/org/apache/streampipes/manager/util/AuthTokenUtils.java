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

package org.apache.streampipes.manager.util;

import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.user.management.jwt.JwtTokenProvider;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthTokenUtils {

  public static String getAuthTokenForCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return makeBearerToken(new JwtTokenProvider().createToken(auth));
  }

  public static String getAuthToken(String resourceId) {
    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      return makeBearerToken(new JwtTokenProvider().createToken(auth));
    } else {
      if (resourceId != null) {
        String ownerSid = getOwnerSid(resourceId);
        return getAuthTokenForUser(ownerSid);
      } else {
        throw new IllegalArgumentException("No authenticated user found to associate with request");
      }
    }
  }

  public static String getAuthTokenForUser(String ownerSid) {
    Principal correspondingUser = new SpResourceManager().manageUsers().getPrincipalById(ownerSid);
    return getAuthTokenForUser(correspondingUser);
  }

  public static String getAuthTokenForUser(Principal principal) {
    return makeBearerToken(new JwtTokenProvider().createToken(principal));
  }

  private static String makeBearerToken(String token) {
    return "Bearer " + token;
  }

  private static String getOwnerSid(String resourceId) {
    return new SpResourceManager()
        .managePermissions()
        .findForObjectId(resourceId)
        .stream()
        .findFirst()
        .map(Permission::getOwnerSid)
        .orElseThrow(() -> new IllegalArgumentException("Could not find owner for resource " + resourceId));
  }
}
