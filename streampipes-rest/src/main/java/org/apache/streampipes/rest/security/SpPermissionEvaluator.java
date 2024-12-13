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
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.model.PrincipalUserDetails;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;

@Configuration
public class SpPermissionEvaluator implements PermissionEvaluator {

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
    PrincipalUserDetails<?> userDetails = getUserDetails(authentication);
    if (targetDomainObject instanceof PipelineElementRecommendationMessage) {
      return isAdmin(userDetails) || filterRecommendation(
          authentication,
          (PipelineElementRecommendationMessage) targetDomainObject
      );
    } else {
      String objectInstanceId = (String) targetDomainObject;
      if (isAdmin(userDetails)) {
        return true;
      }
      return hasPermission(authentication, objectInstanceId);
    }
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
    PrincipalUserDetails<?> userDetails = getUserDetails(authentication);
    if (isAdmin(userDetails)) {
      return true;
    }
    return hasPermission(authentication, targetId.toString());
  }

  private boolean filterRecommendation(Authentication auth, PipelineElementRecommendationMessage message) {
    Predicate<PipelineElementRecommendation> isForbidden = r -> !hasPermission(auth, r.getElementId());
    message.getPossibleElements()
           .removeIf(isForbidden);

    return true;
  }

  private boolean hasPermission(Authentication auth, String objectInstanceId) {
    return isPublicElement(objectInstanceId)
        || getUserDetails(auth).getAllObjectPermissions()
                               .contains(objectInstanceId);
  }

  private PrincipalUserDetails<?> getUserDetails(Authentication authentication) {
    return (PrincipalUserDetails<?>) authentication.getPrincipal();
  }

  private boolean isPublicElement(String objectInstanceId) {
    List<Permission> permissions =
        StorageDispatcher.INSTANCE.getNoSqlStore()
                                  .getPermissionStorage()
                                  .getUserPermissionsForObject(objectInstanceId);
    return permissions.size() > 0 && permissions.get(0)
                                                .isPublicElement();
  }

  private boolean isAdmin(PrincipalUserDetails<?> userDetails) {
    return userDetails
        .getAuthorities()
        .stream()
        .anyMatch(a -> a.getAuthority()
                        .equals(DefaultRole.Constants.ROLE_ADMIN_VALUE));
  }
}
