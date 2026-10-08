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
import org.apache.streampipes.commons.exceptions.SpHttpErrorStatusCode;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.InternalBrokerProvider;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistrationResponse;
import org.apache.streampipes.model.migration.ModelMigratorConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class CoreRequestSubmitter {

  private static final Logger LOG = LoggerFactory.getLogger(CoreRequestSubmitter.class);

  private static final int RETRY_INTERVAL_SECONDS = 3;

  public void submitRepeatedRequest(Supplier<Boolean> request,
                                    String successMessage,
                                    String failureMessage) {
    while (true) {
      try {
        request.get();
        LOG.info(successMessage);
        return;
      } catch (SpRuntimeException e) {
        LOG.warn(failureMessage + " Trying again in {} seconds", RETRY_INTERVAL_SECONDS);
        try {
          TimeUnit.SECONDS.sleep(RETRY_INTERVAL_SECONDS);
        } catch (InterruptedException interrupted) {
          Thread.currentThread().interrupt();
          throw new IllegalStateException("Interrupted while waiting for core", interrupted);
        }
      }
    }
  }

  public void submitRegistrationRequest(IStreamPipesClient client,
                                        SpServiceRegistration serviceReg) {
    var registrationResponse = new AtomicReference<SpServiceRegistrationResponse>();
    submitRepeatedRequest(
        () -> {
          try {
            registrationResponse.set(client.adminApi().registerService(serviceReg));
          } catch (SpHttpErrorStatusCode e) {
            if (e.getHttpStatusCode() == 400 || e.getHttpStatusCode() == 401 || e.getHttpStatusCode() == 403) {
              throw new IllegalStateException("Core rejected service registration; check supported broker protocols "
                  + "and service credentials (HTTP " + e.getHttpStatusCode() + ")", e);
            }
            throw e;
          }
          return true;
        },
        "Successfully registered service at core.",
        String.format(
            "Could not register service at core at url %s",
            client.getConnectionConfig().getBaseUrl()
        ));
    // Broker initialization failures must stop startup, not masquerade as failed HTTP registration.
    InternalBrokerProvider.configure(registrationResponse.get().getInternalBroker());
  }

  public void submitMigrationRequest(IStreamPipesClient client,
                                     List<ModelMigratorConfig> migrationConfigs,
                                     String serviceId,
                                     SpServiceRegistration serviceReg) {
    submitRepeatedRequest(
        () -> {
          try {
            client.adminApi().registerMigrations(migrationConfigs, serviceId);
            return true;
          } catch (RuntimeException e) {
            submitRegistrationRequest(client, serviceReg);
            submitMigrationRequest(client, migrationConfigs, serviceId, serviceReg);
            return true;
          }
        },
        "Successfully sent migration request",
        "Core currently doesn't accept migration requests.");
  }
}
