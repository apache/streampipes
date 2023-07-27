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

package org.apache.streampipes.extensions.management.connect;

import org.apache.streampipes.extensions.api.connect.IPullAdapter;
import org.apache.streampipes.extensions.api.monitoring.SpMonitoringManager;
import org.apache.streampipes.model.monitoring.SpLogEntry;
import org.apache.streampipes.model.monitoring.SpLogMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

public class PullAdapterScheduler {

  protected static final Logger LOGGER = LoggerFactory.getLogger(PullAdapterScheduler.class);

  private ScheduledExecutorService scheduler;

  public void schedule(IPullAdapter pullAdapter,
                       String adapterElementId) {
    final Runnable task = () -> {
      try {
        pullAdapter.pullData();
      } catch (ExecutionException | InterruptedException e) {
        SpMonitoringManager.INSTANCE.addErrorMessage(
            adapterElementId,
            SpLogEntry.from(System.currentTimeMillis(), SpLogMessage.from(e)));
      } catch (TimeoutException e) {
        LOGGER.warn("Timeout occurred", e);
      }
    };

    scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(task, 1,
        pullAdapter.getPollingInterval().value(), pullAdapter.getPollingInterval().timeUnit());
  }

  public void shutdown() {
    scheduler.shutdownNow();
  }
}
