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
package org.apache.streampipes.service.extensions;

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.migration.ModelMigratorConfig;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreRequestSubmitter {

  private static final Logger LOG = LoggerFactory.getLogger(CoreRequestSubmitter.class);

  private static final int RETRY_INTERVAL_SECONDS = 3;

  public void submitRepeatedRequest(Supplier<Boolean> request, String successMessage, String failureMessage) {
    try {
      request.get();
      LOG.info(successMessage);
    } catch (SpRuntimeException e) {
      LOG.warn(failureMessage + " Trying again in {} seconds", RETRY_INTERVAL_SECONDS);
      try {
        TimeUnit.SECONDS.sleep(RETRY_INTERVAL_SECONDS);
        submitRepeatedRequest(request, successMessage, failureMessage);
      } catch (InterruptedException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  public void submitRegistrationRequest(IStreamPipesClient client, SpServiceRegistration serviceReg) {
    submitRepeatedRequest(() -> {
      client.adminApi().registerService(serviceReg);
      return true;
    }, "Successfully registered service at core.",
            String.format("Could not register service at core at url %s", client.getConnectionConfig().getBaseUrl()));
  }

  public void submitMigrationRequest(IStreamPipesClient client, List<ModelMigratorConfig> migrationConfigs,
          String serviceId, SpServiceRegistration serviceReg) {
    submitRepeatedRequest(() -> {
      try {
        client.adminApi().registerMigrations(migrationConfigs, serviceId);
        return true;
      } catch (RuntimeException e) {
        submitRegistrationRequest(client, serviceReg);
        submitMigrationRequest(client, migrationConfigs, serviceId, serviceReg);
        return true;
      }
    }, "Successfully sent migration request", "Core currently doesn't accept migration requests.");
  }
}
