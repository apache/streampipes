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
package org.apache.streampipes.rest.security;

import org.apache.streampipes.model.client.user.DefaultRole;
import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.pipeline.PipelineElementRecommendation;
import org.apache.streampipes.model.pipeline.PipelineElementRecommendationMessage;
import org.apache.streampipes.storage.api.IPermissionStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.model.PrincipalUserDetails;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Configuration
public class SpPermissionEvaluator implements PermissionEvaluator {

  private final IPermissionStorage permissionStorage;

  public SpPermissionEvaluator() {
    this.permissionStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getPermissionStorage();
  }

  /**
   * Evaluates whether the user has the necessary permissions for a given resource.
   *
   * @param authentication     The authentication object containing the user's credentials.
   * @param targetDomainObject The resource being accessed, which can be an instance of
   *                           PipelineElementRecommendationMessage or a String representing the resource ID.
   * @param permission         Is not used in this implementation.
   * @return true if the user has the necessary permissions, false otherwise.
   */
  @Override
  public boolean hasPermission(
      Authentication authentication,
      Object targetDomainObject,
      Object permission
  ) {
    if (targetDomainObject instanceof PipelineElementRecommendationMessage msg) {
      return handleRecommendationMessage(authentication, msg);
    }

    String objectId = String.valueOf(targetDomainObject);
    List<Permission> perms = getObjectPermission(objectId);

    if (isAnonymousAccess(perms)) {
      return true;
    }

    if (isAdmin(authentication)) {
      return true;
    }

    return hasPermissionForId(authentication, perms, objectId);
  }

  /**
   * Evaluates whether the user has the necessary permissions for a given resource.
   *
   * @param authentication The authentication object containing the user's credentials.
   * @param targetId       The ID of the resource being accessed.
   * @param targetType     Is not used in this implementation.
   * @param permission     Is not used in this implementation.
   * @return true if the user has the necessary permissions, false otherwise.
   */
  @Override
  public boolean hasPermission(
      Authentication authentication,
      Serializable targetId,
      String targetType,
      Object permission
  ) {
    // We do not use targetType in this implementation
    return hasPermission(authentication, targetId, permission);
  }

  private boolean handleRecommendationMessage(Authentication auth,
                                              PipelineElementRecommendationMessage message) {
    Predicate<PipelineElementRecommendation> isForbidden = rec -> {
      String elementId = rec.getElementId();
      List<Permission> perms = getObjectPermission(elementId);

      if (isAnonymousAccess(perms)) {
        return false;
      }
      if (isAdmin(auth)) {
        return false;
      }
      return !hasPermissionForId(auth, perms, elementId); // remove if not allowed
    };

    message.getPossibleElements().removeIf(isForbidden);
    return true;
  }

  private boolean hasPermissionForId(Authentication auth,
                                     List<Permission> permissions,
                                     String objectInstanceId) {
    PrincipalUserDetails<?> user = getUserDetailsOrNull(auth);
    if (user == null) {
      return false;
    }

    if (isPublicElement(permissions)) {
      return true;
    }

    return user.getAllObjectPermissions().contains(objectInstanceId);
  }

  private PrincipalUserDetails<?> getUserDetailsOrNull(Authentication authentication) {
    if (authentication == null
        || authentication instanceof AnonymousAuthenticationToken) {
      return null;
    }
    Object principal = authentication.getPrincipal();
    return (principal instanceof PrincipalUserDetails) ? (PrincipalUserDetails<?>) principal : null;
  }

  private boolean isAdmin(Authentication authentication) {
    PrincipalUserDetails<?> userDetails = getUserDetailsOrNull(authentication);
    if (userDetails == null) {
      return false;
    }

    return userDetails.getAuthorities().stream()
        .anyMatch(a ->
            Objects.equals(a.getAuthority(), DefaultRole.Constants.ROLE_ADMIN_VALUE)
        );
  }

  private boolean isPublicElement(List<Permission> permissions) {
    return !permissions.isEmpty()
        && (permissions.get(0).isPublicElement());
  }

  private boolean isAnonymousAccess(List<Permission> permissions) {
    return !permissions.isEmpty() && permissions.get(0).isReadAnonymous();
  }

  private List<Permission> getObjectPermission(String objectInstanceId) {
    return permissionStorage.getUserPermissionsForObject(objectInstanceId);
  }
}
