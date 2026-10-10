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
package org.apache.streampipes.user.management.util;

import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.client.user.ServiceAccount;
import org.apache.streampipes.model.client.user.UserAccount;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrincipalStatusTest {

  @Test
  void externalProviderOwnsStatusIncludingLegacyDisabledAccounts() {
    var user = new UserAccount();
    user.setProvider("keycloak");
    user.setAccountEnabled(false);
    user.setAccountLocked(true);
    user.setAccountExpired(true);

    assertTrue(PrincipalStatus.canAuthenticate(user));
  }

  @Test
  void localAndMissingProvidersEnforceLocalStatus() {
    var user = new UserAccount();
    assertLocalStatus(user);
    user.setProvider(null);
    assertLocalStatus(user);
    user.setProvider("");
    assertLocalStatus(user);
    user.setProvider(" ");
    assertLocalStatus(user);
  }

  @Test
  void serviceAccountsEnforceLocalStatus() {
    assertLocalStatus(new ServiceAccount());
  }

  @Test
  void missingPrincipalCannotAuthenticate() {
    assertFalse(PrincipalStatus.canAuthenticate(null));
  }

  private void assertLocalStatus(Principal principal) {
    principal.setAccountEnabled(true);
    principal.setAccountLocked(false);
    principal.setAccountExpired(false);
    assertTrue(PrincipalStatus.canAuthenticate(principal));

    principal.setAccountEnabled(false);
    assertFalse(PrincipalStatus.canAuthenticate(principal));
    principal.setAccountEnabled(true);
    principal.setAccountLocked(true);
    assertFalse(PrincipalStatus.canAuthenticate(principal));
    principal.setAccountLocked(false);
    principal.setAccountExpired(true);
    assertFalse(PrincipalStatus.canAuthenticate(principal));
  }
}
