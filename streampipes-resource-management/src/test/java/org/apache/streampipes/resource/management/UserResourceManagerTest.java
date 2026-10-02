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

import org.apache.streampipes.commons.exceptions.UserNotFoundException;
import org.apache.streampipes.model.client.user.PasswordRecoveryToken;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.model.client.user.UserActivationToken;
import org.apache.streampipes.model.client.user.UserRegistrationData;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.api.user.IPasswordRecoveryTokenStorage;
import org.apache.streampipes.storage.api.user.IUserActivationTokenStorage;
import org.apache.streampipes.storage.api.user.IUserStorage;
import org.apache.streampipes.user.management.util.PasswordUtil;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class UserResourceManagerTest {

  private final IUserStorage users = mock(IUserStorage.class);
  private final IUserActivationTokenStorage activationTokens = mock(IUserActivationTokenStorage.class);
  private final IPasswordRecoveryTokenStorage recoveryTokens = mock(IPasswordRecoveryTokenStorage.class);
  private final UserResourceManager manager = new UserResourceManager(
      users, mock(ISpCoreConfigurationStorage.class), activationTokens, recoveryTokens);

  @Test
  void activatesAccountAndConsumesInjectedToken() {
    var token = UserActivationToken.create("activation", "user");
    var user = new UserAccount();
    user.setAccountEnabled(false);
    when(activationTokens.getElementById("activation")).thenReturn(token);
    when(users.getUser("user")).thenReturn(user);

    manager.activateAccount("activation");

    assertTrue(user.isAccountEnabled());
    verify(users).updateUser(user);
    verify(activationTokens).deleteElement(token);
    verifyNoInteractions(recoveryTokens);
  }

  @Test
  void rejectsUnknownActivationTokenWithoutUpdatingUsers() {
    assertThrows(UserNotFoundException.class, () -> manager.activateAccount("missing"));
    verifyNoInteractions(users, recoveryTokens);
  }

  @Test
  void changesPasswordAndConsumesInjectedRecoveryToken() throws Exception {
    var token = PasswordRecoveryToken.create("recovery", "user");
    var user = new UserAccount();
    var registration = mock(UserRegistrationData.class);
    when(registration.getPassword()).thenReturn("test-password");
    when(recoveryTokens.getElementById("recovery")).thenReturn(token);
    when(users.getUser("user")).thenReturn(user);
    try (var passwords = mockStatic(PasswordUtil.class)) {
      passwords.when(() -> PasswordUtil.encryptPassword("test-password")).thenReturn("encrypted");

      manager.changePassword("recovery", registration);

      assertEquals("encrypted", user.getPassword());
      verify(users).updateUser(user);
      verify(recoveryTokens).deleteElement(token);
      verifyNoInteractions(activationTokens);
    }
  }

  @Test
  void rejectsUnknownRecoveryTokenUsingRecoveryStorage() {
    assertThrows(IllegalArgumentException.class, () -> manager.checkPasswordRecoveryCode("missing"));
    verify(recoveryTokens).getElementById("missing");
    verifyNoInteractions(users, activationTokens);
  }
}
