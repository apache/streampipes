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
package org.apache.streampipes.user.management.service;

import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.client.user.ServiceAccount;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.storage.api.user.IPermissionStorage;
import org.apache.streampipes.storage.api.user.IRoleStorage;
import org.apache.streampipes.storage.api.user.IUserGroupStorage;
import org.apache.streampipes.storage.api.user.IUserStorage;
import org.apache.streampipes.user.management.model.ServiceAccountDetails;
import org.apache.streampipes.user.management.model.UserAccountDetails;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class SpUserDetailsService implements UserDetailsService {

  private final IPermissionStorage permissionStorage;
  private final IRoleStorage roleStorage;
  private final IUserGroupStorage userGroupStorage;
  private final IUserStorage userStorage;

  public SpUserDetailsService(IUserStorage userStorage,
                              IPermissionStorage permissionStorage,
                              IRoleStorage roleStorage,
                              IUserGroupStorage userGroupStorage) {
    this.userStorage = userStorage;
    this.permissionStorage = permissionStorage;
    this.roleStorage = roleStorage;
    this.userGroupStorage = userGroupStorage;
  }

  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    Principal user = userStorage.getUser(s);
    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }
    return user instanceof UserAccount ? new UserAccountDetails(
        (UserAccount) user,
        permissionStorage,
        roleStorage,
        userGroupStorage
        ) :
        new ServiceAccountDetails((ServiceAccount) user, permissionStorage, roleStorage, userGroupStorage);
  }
}
