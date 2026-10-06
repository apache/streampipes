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
package org.apache.streampipes.service.core.migrations.v099;

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.security.ServiceAccountSecret;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.user.IUserStorage;
import org.apache.streampipes.user.management.service.ServiceAccountSecretManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplaceDefaultServiceSecretMigration implements Migration {

  private static final Logger LOG = LoggerFactory.getLogger(ReplaceDefaultServiceSecretMigration.class);
  private final ServiceAccountSecretManager secrets;

  public ReplaceDefaultServiceSecretMigration(IUserStorage userStorage) {
    this.secrets = new ServiceAccountSecretManager(userStorage);
  }

  @Override
  public boolean shouldExecute() {
    return secrets.usesLegacyDefault(Environments.getEnvironment().getInitialServiceUser().getValueOrDefault());
  }

  @Override
  public void executeMigration() {
    var env = Environments.getEnvironment();
    String replacement = env.getInitialServiceUserSecret().getValue();
    if (!ServiceAccountSecret.isValid(replacement)) {
      LOG.error("The default service credential is blocked. Set SP_INITIAL_SERVICE_USER_SECRET and "
          + "SP_CLIENT_SECRET on extensions to the same random secret, then restart. "
          + "Human administrator login remains available to update service accounts.");
      return;
    }
    try {
      secrets.replaceLegacyDefault(env.getInitialServiceUser().getValueOrDefault(), replacement);
    } catch (RuntimeException e) {
      LOG.error("Could not replace default service credential; migration will be retried on restart");
      throw e;
    }
  }

  @Override
  public String getDescription() {
    return "Replace the public bootstrap service credential with the configured secret";
  }
}
