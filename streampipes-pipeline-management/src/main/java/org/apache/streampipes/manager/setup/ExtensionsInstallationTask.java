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

import org.apache.streampipes.manager.endpoint.EndpointFetcher;
import org.apache.streampipes.model.client.endpoint.ExtensionsServiceEndpoint;
import org.apache.streampipes.model.client.setup.InitialSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ExtensionsInstallationTask implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(ExtensionsInstallationTask.class);

  private static final int MAX_RETRIES = 6;
  private static final int SLEEP_TIME_SECONDS = 2;

  private final InitialSettings settings;
  private final BackgroundTaskNotifier callback;

  public ExtensionsInstallationTask(InitialSettings settings,
                                    BackgroundTaskNotifier callback) {
    this.settings = settings;
    this.callback = callback;
  }

  @Override
  public void run() {
    List<InstallationStep> steps = new ArrayList<>();
    if (settings.getInstallPipelineElements()) {
      List<ExtensionsServiceEndpoint> endpoints;
      int numberOfAttempts = 0;
      do {
        endpoints = new EndpointFetcher().getEndpoints();
        numberOfAttempts++;
        if (endpoints.isEmpty()) {
          LOG.info("Found 0 endpoints - waiting {} seconds to make sure all endpoints have properly started",
              SLEEP_TIME_SECONDS);
          try {
            TimeUnit.SECONDS.sleep(SLEEP_TIME_SECONDS);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      } while (endpoints.isEmpty() && numberOfAttempts < MAX_RETRIES);
      LOG.info("Found {} endpoints from which we will install extensions.", endpoints.size());
      LOG.info(
          "Further available extensions can always be installed by navigating to the 'Install pipeline elements' view");
      for (ExtensionsServiceEndpoint endpoint : endpoints) {
        steps.add(new PipelineElementInstallationStep(endpoint, settings.getInitialAdminUserSid()));
      }

      AtomicInteger errorCount = new AtomicInteger(0);
      steps.forEach(step -> {
        step.install();
        errorCount.addAndGet(step.getErrorCount());
      });
      callback.notifyFinished(errorCount.get());
    }
  }
}
