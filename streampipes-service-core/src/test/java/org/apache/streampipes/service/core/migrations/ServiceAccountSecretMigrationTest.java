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

package org.apache.streampipes.service.core.migrations;

import org.apache.streampipes.commons.constants.DefaultEnvValues;
import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.security.ServiceAccountSecret;
import org.apache.streampipes.model.client.user.DefaultRole;
import org.apache.streampipes.model.client.user.ServiceAccount;
import org.apache.streampipes.service.core.migrations.v099.ReplaceDefaultServiceSecretMigration;
import org.apache.streampipes.storage.api.user.IUserStorage;
import org.apache.streampipes.user.management.encryption.SecretEncryptionManager;
import org.apache.streampipes.user.management.service.ServiceAccountSecretManager;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServiceAccountSecretMigrationTest {

  @Test
  void replacesOnlyLegacyCredentialAndIsIdempotent() {
    for (boolean encrypted : new boolean[]{false, true}) {
      var users = mock(IUserStorage.class);
      var account = ServiceAccount.from("service", DefaultEnvValues.INITIAL_CLIENT_SECRET_DEFAULT,
          Set.of(DefaultRole.ROLE_ADMIN.name()));
      account.setSecretEncrypted(encrypted);
      if (encrypted) {
        account.setClientSecret(SecretEncryptionManager.encrypt(account.getClientSecret()));
      }
      when(users.getServiceAccount("service")).thenReturn(account);
      var manager = new ServiceAccountSecretManager(users);
      assertTrue(manager.usesLegacyDefault("service"));
      manager.replaceLegacyDefault("service", "n".repeat(64));
      assertFalse(manager.usesLegacyDefault("service"));
      assertTrue(account.isSecretEncrypted());
      assertEquals("n".repeat(64), ServiceAccountSecretManager.readSecret(account));
      assertEquals(Set.of(DefaultRole.ROLE_ADMIN.name()), account.getRoles());
      manager.replaceLegacyDefault("service", "different".repeat(8));
      assertEquals("n".repeat(64), ServiceAccountSecretManager.readSecret(account));
      verify(users, times(1)).updateUser(account);
    }
  }

  @Test
  void startupMigrationWaitsForReplacementAndPreservesCustomCredentials() {
    var users = mock(IUserStorage.class);
    var env = mock(Environment.class, RETURNS_DEEP_STUBS);
    when(env.getInitialServiceUser().getValueOrDefault()).thenReturn("bootstrap");
    var account = ServiceAccount.from("bootstrap", DefaultEnvValues.INITIAL_CLIENT_SECRET_DEFAULT, Set.of());
    when(users.getServiceAccount("bootstrap")).thenReturn(account);
    try (var environments = mockStatic(Environments.class)) {
      environments.when(Environments::getEnvironment).thenReturn(env);
      // Encryption requires the deployment's passcode when a replacement is configured.
      when(env.getEncryptionPasscode().getValueOrDefault()).thenReturn("test-encryption-passcode");
      var migration = new ReplaceDefaultServiceSecretMigration(users);
      assertTrue(migration.shouldExecute());
      migration.executeMigration();
      verify(users, never()).updateUser(account);
      assertTrue(migration.shouldExecute());
      when(env.getInitialServiceUserSecret().getValue()).thenReturn("replacement".repeat(8));
      migration.executeMigration();
      assertFalse(migration.shouldExecute());
      assertEquals("replacement".repeat(8), ServiceAccountSecretManager.readSecret(account));
      when(env.getInitialServiceUserSecret().getValue()).thenReturn("another".repeat(8));
      migration.executeMigration();
      assertEquals("replacement".repeat(8), ServiceAccountSecretManager.readSecret(account));
      verify(users, times(1)).updateUser(account);
    }
  }

  @Test
  void migrationPropagatesStorageFailure() {
    var users = mock(IUserStorage.class);
    var env = mock(Environment.class, RETURNS_DEEP_STUBS);
    when(env.getInitialServiceUser().getValueOrDefault()).thenReturn("bootstrap");
    when(env.getInitialServiceUserSecret().getValue()).thenReturn("replacement".repeat(8));
    when(env.getEncryptionPasscode().getValueOrDefault()).thenReturn("test-encryption-passcode");
    var account = ServiceAccount.from("bootstrap", DefaultEnvValues.INITIAL_CLIENT_SECRET_DEFAULT, Set.of());
    when(users.getServiceAccount("bootstrap")).thenReturn(account);
    doThrow(new IllegalStateException("storage unavailable")).when(users).updateUser(account);
    try (var environments = mockStatic(Environments.class)) {
      environments.when(Environments::getEnvironment).thenReturn(env);
      var migration = new ReplaceDefaultServiceSecretMigration(users);
      assertThrows(IllegalStateException.class, migration::executeMigration);
    }
  }

  @Test
  void rejectsMissingWeakAndDefaultReplacements() {
    assertThrows(IllegalArgumentException.class, () -> ServiceAccountSecret.requireValid(null, "test"));
    assertThrows(IllegalArgumentException.class, () -> ServiceAccountSecret.requireValid("", "test"));
    assertThrows(IllegalArgumentException.class, () -> ServiceAccountSecret.requireValid("short", "test"));
    assertThrows(IllegalArgumentException.class,
        () -> ServiceAccountSecret.requireValid(DefaultEnvValues.INITIAL_CLIENT_SECRET_DEFAULT, "test"));
    assertEquals("x".repeat(32), ServiceAccountSecret.requireValid("x".repeat(32), "test"));
  }
}
