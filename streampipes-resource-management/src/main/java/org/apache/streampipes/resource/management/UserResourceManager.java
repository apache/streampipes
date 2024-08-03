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

package org.apache.streampipes.resource.management;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.exceptions.SpException;
import org.apache.streampipes.commons.exceptions.UserNotFoundException;
import org.apache.streampipes.commons.exceptions.UsernameAlreadyTakenException;
import org.apache.streampipes.mail.MailSender;
import org.apache.streampipes.model.client.user.PasswordRecoveryToken;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.client.user.Role;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.model.client.user.UserActivationToken;
import org.apache.streampipes.model.client.user.UserRegistrationData;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.api.IUserStorage;
import org.apache.streampipes.storage.couchdb.CouchDbStorageManager;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.util.PasswordUtil;
import org.apache.streampipes.user.management.util.TokenUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.List;

public class UserResourceManager extends AbstractResourceManager<IUserStorage> {

  private static final int RECOVERY_TOKEN_LENGTH = 40;
  private static final Logger LOG = LoggerFactory.getLogger(UserResourceManager.class);

  public UserResourceManager() {
    super(StorageDispatcher.INSTANCE.getNoSqlStore().getUserStorageAPI());
  }

  public static void setHideTutorial(String username, boolean hideTutorial) {
    IUserStorage userService = getUserStorage();
    UserAccount user = userService.getUserAccount(username);
    user.setHideTutorial(hideTutorial);
    userService.updateUser(user);
  }

  public static IUserStorage getUserStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getUserStorageAPI();
  }

  public Principal getPrincipalById(String principalId) {
    return db.getUserById(principalId);
  }

  public Principal getServiceAdmin() {
    var env = getEnvironment();
    return db.getServiceAccount(
        env.getInitialServiceUser().getValueOrDefault()
    );
  }

  public Principal getAdminUser() {
    return CouchDbStorageManager.INSTANCE
        .getUserStorageAPI()
        .getAllUserAccounts()
        .stream()
        .filter(u -> u.getRoles().contains(Role.ROLE_ADMIN))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

  public void registerUser(UserRegistrationData data) throws UsernameAlreadyTakenException {
    try {
      validateAndRegisterNewUser(data);
      createTokenAndSendActivationMail(data.getUsername());
    } catch (IOException e) {
      LOG.error("Registration of user could not be completed: {}", e.getMessage());
    }
  }

  private synchronized void validateAndRegisterNewUser(UserRegistrationData data) {
    if (db.checkUserExists(data.getUsername())) {
      throw new UsernameAlreadyTakenException("Username already taken");
    }
    String encryptedPassword;
    try {
      encryptedPassword = PasswordUtil.encryptPassword(data.getPassword());
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new SpException("Error during password encryption: %s".formatted(e.getMessage()));
    }

    createNewUser(data, encryptedPassword);
  }

  private synchronized void createNewUser(UserRegistrationData data, String encryptedPassword) {

    List<Role> roles = data.getRoles().stream().map(Role::valueOf).toList();
    UserAccount user = UserAccount.from(data.getUsername(), encryptedPassword, new HashSet<>(roles));
    user.setUsername(data.getUsername());
    user.setPassword(encryptedPassword);
    user.setAccountEnabled(false);
    db.storeUser(user);
  }

  public void activateAccount(String activationCode) throws UserNotFoundException {
    UserActivationToken token = getUserActivationTokenStorage().getElementById(activationCode);
    if (token != null) {
      Principal user = db.getUser(token.getUsername());
      if (user instanceof UserAccount) {
        user.setAccountEnabled(true);
        db.updateUser(user);
        getUserActivationTokenStorage().deleteElement(token);
      }
    } else {
      throw new UserNotFoundException("User or token not found");
    }
  }

  private void createTokenAndSendActivationMail(String username) throws IOException {
    String activationCode = TokenUtil.generateToken(RECOVERY_TOKEN_LENGTH);
    storeActivationCode(username, activationCode);
  }

  private void storeActivationCode(String username,
                                   String activationCode) throws IOException {
    UserActivationToken token = UserActivationToken.create(activationCode, username);
    getUserActivationTokenStorage().persist(token);
    new MailSender().sendAccountActivationMail(username, activationCode);
  }

  public void sendPasswordRecoveryLink(String username) throws UserNotFoundException, IOException {
    // send a password recovery link to the user
    if (db.checkUserExists(username)) {
      String recoveryCode = TokenUtil.generateToken(RECOVERY_TOKEN_LENGTH);
      storeRecoveryCode(username, recoveryCode);
      new MailSender().sendPasswordRecoveryMail(username, recoveryCode);
    }
  }

  public void checkPasswordRecoveryCode(String recoveryCode) {
    var tokenStorage = getPasswordRecoveryTokenStorage();
    PasswordRecoveryToken token = tokenStorage.getElementById(recoveryCode);
    if (token == null) {
      throw new IllegalArgumentException("Invalid recovery code");
    }
  }

  public void changePassword(String recoveryCode,
                             UserRegistrationData data) throws NoSuchAlgorithmException, InvalidKeySpecException {
    checkPasswordRecoveryCode(recoveryCode);
    PasswordRecoveryToken token = getPasswordRecoveryTokenStorage().getElementById(recoveryCode);
    Principal user = db.getUser(token.getUsername());
    if (user instanceof UserAccount) {
      String encryptedPassword = PasswordUtil.encryptPassword(data.getPassword());
      ((UserAccount) user).setPassword(encryptedPassword);
      db.updateUser(user);
      getPasswordRecoveryTokenStorage().deleteElement(token);
    }
  }

  private void storeRecoveryCode(String username,
                                 String recoveryCode) {
    getPasswordRecoveryTokenStorage().persist(PasswordRecoveryToken.create(recoveryCode, username));
  }

  private CRUDStorage<PasswordRecoveryToken> getPasswordRecoveryTokenStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getPasswordRecoveryTokenStorage();
  }

  private CRUDStorage<UserActivationToken> getUserActivationTokenStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getUserActivationTokenStorage();
  }

  private Environment getEnvironment() {
    return Environments.getEnvironment();
  }


  public void registerOauthUser(UserAccount userAccount) {
    db.storeUser(userAccount);
  }
}
