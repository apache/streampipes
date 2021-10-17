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
package org.apache.streampipes.rest.impl.security;

import org.apache.streampipes.user.management.model.PrincipalUserDetails;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

@Configuration
public class SpPermissionEvaluator implements PermissionEvaluator {

  @Override
  public boolean hasPermission(Authentication authentication, Object o, Object permission) {
    String objectInstanceId = (String) o;

    return getUserDetails(authentication).getAllObjectPermissions().contains(objectInstanceId);
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object permission) {
    return getUserDetails(authentication).getAllObjectPermissions().contains(serializable.toString());
  }

  private PrincipalUserDetails<?> getUserDetails(Authentication authentication) {
    return (PrincipalUserDetails<?>) authentication.getPrincipal();
  }
}
