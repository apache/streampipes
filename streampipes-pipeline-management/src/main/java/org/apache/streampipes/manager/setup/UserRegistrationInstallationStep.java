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

package org.apache.streampipes.manager.setup;

import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.client.user.Role;
import org.apache.streampipes.model.client.user.ServiceAccount;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.encryption.SecretEncryptionManager;
import org.apache.streampipes.user.management.util.PasswordUtil;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.Set;

public class UserRegistrationInstallationStep extends InstallationStep {

  private final String adminEmail;
  private final String adminPassword;
  private final String initialServiceAccountName;
  private final String initialServiceAccountSecret;
  private final String initialAdminUserSid;
  private final Set<Role> roles;

  public UserRegistrationInstallationStep(String adminEmail,
                                          String adminPassword,
                                          String initialServiceAccountName,
                                          String initialServiceAccountSecret,
                                          String initialAdminUserSid) {
    this.adminEmail = adminEmail;
    this.adminPassword = adminPassword;
    this.initialServiceAccountName = initialServiceAccountName;
    this.initialServiceAccountSecret = initialServiceAccountSecret;
    this.initialAdminUserSid = initialAdminUserSid;
    roles = new HashSet<>();
    roles.add(Role.ROLE_ADMIN);
  }

  @Override
  public void install() {

    try {
      addAdminUser();
      addServiceUser();

      logSuccess(getTitle());
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      logFailure("Could not encrypt password");
    }
  }

  private void addAdminUser() throws NoSuchAlgorithmException, InvalidKeySpecException {
    String encryptedPassword = PasswordUtil.encryptPassword(adminPassword);
    UserAccount user = UserAccount.from(adminEmail, encryptedPassword, roles);
    user.setPrincipalId(initialAdminUserSid);
    storePrincipal(user);
  }

  private void addServiceUser() {
    ServiceAccount serviceAccount = ServiceAccount.from(initialServiceAccountName, initialServiceAccountSecret, roles);
    serviceAccount.setClientSecret(SecretEncryptionManager.encrypt(initialServiceAccountSecret));
    serviceAccount.setSecretEncrypted(true);
    storePrincipal(serviceAccount);
  }

  private void storePrincipal(Principal principal) {
    StorageDispatcher.INSTANCE.getNoSqlStore().getUserStorageAPI().storeUser(principal);
  }

  @Override
  public String getTitle() {
    return "Creating admin user...";
  }

}
