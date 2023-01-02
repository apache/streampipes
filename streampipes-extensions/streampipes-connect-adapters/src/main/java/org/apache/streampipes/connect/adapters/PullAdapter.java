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

package org.apache.streampipes.connect.adapters;

import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.management.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public abstract class PullAdapter extends SpecificDataStreamAdapter {

  protected static Logger logger = LoggerFactory.getLogger(PullAdapter.class);
  private ScheduledExecutorService scheduler;
  private ScheduledExecutorService errorThreadscheduler;


  public PullAdapter() {
    super();
  }

  public PullAdapter(SpecificAdapterStreamDescription adapterDescription) {
    super(adapterDescription);
  }

  protected abstract void pullData();

  protected abstract PollingSettings getPollingInterval();

  @Override
  public void startAdapter() throws AdapterException {
    before();

    final Runnable errorThread = () -> {
      executeAdpaterLogic();
    };

    scheduler = Executors.newScheduledThreadPool(1);
    scheduler.schedule(errorThread, 0, TimeUnit.MILLISECONDS);

  }

  private void executeAdpaterLogic() {
    final Runnable task = () -> {

      pullData();

    };

    scheduler = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> handle = scheduler.scheduleAtFixedRate(task, 1,
        getPollingInterval().getValue(), getPollingInterval().getTimeUnit());

    try {
      handle.get();
    } catch (ExecutionException | InterruptedException e) {
      logger.error("Error", e);
    }
  }

  @Override
  public void stopAdapter() throws AdapterException {
    after();
    scheduler.shutdownNow();
  }

  /**
   * Called before adapter is started (e.g. initialize connections)
   */
  protected void before() throws AdapterException {

  }

  /**
   * Called before adapter is stopped (e.g. shutdown connections)
   */
  protected void after() throws AdapterException {

  }
}
