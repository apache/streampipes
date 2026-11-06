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

package org.apache.streampipes.extensions.connectors.plc.adapter.generic.connection;

import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.monitoring.IExtensionsLogger;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xConnectionSettings;

import org.apache.plc4x.java.api.PlcConnectionFactory;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.tools.eventpump.EventPump;
import org.apache.plc4x.java.tools.eventpump.TagBatch;
import org.apache.plc4x.java.tools.eventpump.triggers.TimerTrigger;
import org.apache.plc4x.java.tools.eventpump.triggers.Trigger;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ContinuousPlcRequestReader implements AutoCloseable, TagBatch.TagBatchListener {

  private final EventPump pump = new EventPump();
  private final String adapterId;
  private final IEventCollector collector;
  private final IExtensionsLogger logger;
  private final PlcEventGenerator eventGenerator;
  private final Map<String, String> nodes;
  private boolean failed;
  private boolean closed;

  public ContinuousPlcRequestReader(PlcConnectionFactory connectionFactory,
                                    Plc4xConnectionSettings settings,
                                    IEventCollector collector,
                                    String adapterId,
                                    IExtensionsLogger logger) {
    this(connectionFactory, settings, collector, adapterId, logger,
        new TimerTrigger(settings.pollingInterval(), 0, TimeUnit.MILLISECONDS));
  }

  ContinuousPlcRequestReader(PlcConnectionFactory connectionFactory,
                             Plc4xConnectionSettings settings,
                             IEventCollector collector,
                             String adapterId,
                             IExtensionsLogger logger,
                             Trigger trigger) {
    this.adapterId = adapterId;
    this.collector = collector;
    this.logger = logger;
    this.nodes = Map.copyOf(settings.nodes());
    this.eventGenerator = new PlcEventGenerator(nodes);
    try {
      var batch = TagBatch.builder()
          .withBatchId(adapterId)
          .withConnectionFactory(connectionFactory)
          .withConnectionString(settings.connectionString())
          .addTagAddresses(nodes)
          .withTrigger(trigger)
          // Preserve the previous reader's five-second bound, even if a driver never completes its read.
          .withFetchTimeout(5, TimeUnit.SECONDS)
          .withListener(this)
          .build();
      pump.addBatch(batch);
    } catch (RuntimeException e) {
      trigger.close();
      pump.close();
      throw e;
    }
  }

  public synchronized void start() {
    try {
      // Unlike startAll(), startBatch() propagates startup failures to the adapter lifecycle.
      pump.startBatch(adapterId);
    } catch (RuntimeException e) {
      close();
      throw e;
    }
  }

  @Override
  public synchronized void onTagsFetched(TagBatch batch, PlcReadResponse response) {
    if (closed) {
      return;
    }
    refreshRequest(batch);
    collector.collect(eventGenerator.makeEvent(response));
    if (failed) {
      failed = false;
      logger.info("PLC connection recovered", "Reading PLC data has resumed.");
    }
  }

  @Override
  public synchronized void onError(TagBatch batch, Throwable error) {
    if (closed) {
      return;
    }
    refreshRequest(batch);
    if (!failed) {
      failed = true;
      // Driver exception messages can contain connection URLs and credentials.
      logger.error(new IllegalStateException("PLC read failed (" + error.getClass().getSimpleName()
          + "). The event pump will retry with exponential backoff."));
    }
  }

  @Override
  public synchronized void onFetchSkipped(TagBatch batch, long lastFetchDurationMs, long consecutiveSkips) {
    if (!closed && consecutiveSkips == 1) {
      logger.warn("PLC poll skipped", "The previous read is still running. Consider increasing the polling interval.");
    }
  }

  private void refreshRequest(TagBatch batch) {
    // PLC4X 1.0 caches requests across fetches, but a request belongs to its original connection lease.
    // Reapplying the tags invalidates that cache so the next fetch builds against its newly acquired lease.
    batch.addTags(nodes);
  }

  @Override
  public synchronized void close() {
    if (!closed) {
      closed = true;
      pump.close();
    }
  }
}
