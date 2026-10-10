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
import org.apache.streampipes.model.client.user.UserAccount;

public final class PrincipalStatus {

  private PrincipalStatus() {
  }

  public static boolean canAuthenticate(Principal principal) {
    // External providers own account status. Legacy OAuth accounts were stored as disabled.
    // This check only determines eligibility; callers must still validate the credentials.
    if (principal instanceof UserAccount user
        && user.getProvider() != null && !user.getProvider().isBlank()
        && !UserAccount.LOCAL.equals(user.getProvider())) {
      return true;
    }
    return principal != null && principal.isAccountEnabled()
        && !principal.isAccountLocked() && !principal.isAccountExpired();
  }
}
