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

import org.apache.streampipes.manager.extensions.AvailableExtensionsProvider;
import org.apache.streampipes.model.client.setup.InitialSettings;
import org.apache.streampipes.model.extensions.ExtensionItemDescription;
import org.apache.streampipes.storage.api.INoSqlStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionsInstallationTask implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(ExtensionsInstallationTask.class);

  private static final int MAX_RETRIES = 6;
  private static final int SLEEP_TIME_SECONDS = 5;

  private final InitialSettings settings;
  private final BackgroundTaskNotifier callback;
  private final INoSqlStorage storage;

  public ExtensionsInstallationTask(InitialSettings settings, INoSqlStorage storage, BackgroundTaskNotifier callback) {
    this.settings = settings;
    this.storage = storage;
    this.callback = callback;
  }

  @Override
  public void run() {
    List<InstallationStep> steps = new ArrayList<>();
    if (settings.getInstallPipelineElements()) {
      List<ExtensionItemDescription> availableExtensions;
      int numberOfAttempts = 0;
      do {

        availableExtensions = new AvailableExtensionsProvider(storage).getExtensionItemDescriptions();
        numberOfAttempts++;
        if (availableExtensions.isEmpty()) {
          LOG.info("Found 0 extensions - waiting {} seconds to make sure all extension services have properly started",
                  SLEEP_TIME_SECONDS);
          try {
            TimeUnit.SECONDS.sleep(SLEEP_TIME_SECONDS);
          } catch (InterruptedException e) {
            LOG.warn("Interrupted. ", e);
          }
        }
      } while (availableExtensions.isEmpty() && numberOfAttempts < MAX_RETRIES);
      LOG.info("Found {} extensions which we will install", availableExtensions.size());
      LOG.info("Further available extensions can be installed by navigating to the 'Install pipeline elements' view");
      for (ExtensionItemDescription extensionItem : availableExtensions) {
        steps.add(new PipelineElementInstallationStep(extensionItem, settings.getInitialAdminUserSid()));
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
