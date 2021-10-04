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

package org.apache.streampipes.manager.storage;

import org.apache.streampipes.model.client.user.RegistrationData;
import org.apache.streampipes.model.client.user.Role;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.storage.api.IUserStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.util.PasswordUtil;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Set;

public class UserManagementService {

  public Boolean registerUser(RegistrationData data, Set<Role> roles) {

    try {
      String encryptedPassword = PasswordUtil.encryptPassword(data.getPassword());
      UserAccount user = UserAccount.from(data.getEmail(), encryptedPassword, roles);
      user.setPassword(encryptedPassword);
      getUserStorage().storeUser(user);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      return false;
    }

    return true;
  }

  public static void setHideTutorial(String username, boolean hideTutorial) {
    IUserStorage userService = getUserStorage();
    UserAccount user = userService.getUserAccount(username);
    user.setHideTutorial(hideTutorial);
    userService.updateUser(user);
  }

  public static UserService getUserService() {
    return new UserService(getUserStorage());
  }

  public static IUserStorage getUserStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getUserStorageAPI();
  }

}
