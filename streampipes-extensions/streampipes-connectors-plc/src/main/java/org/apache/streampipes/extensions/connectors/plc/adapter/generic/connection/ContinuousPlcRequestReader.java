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
import org.apache.streampipes.extensions.api.connect.IPollingSettings;
import org.apache.streampipes.extensions.api.connect.IPullAdapter;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xConnectionSettings;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.utils.cache.CachedPlcConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ContinuousPlcRequestReader
    extends OneTimePlcRequestReader implements IPullAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(ContinuousPlcRequestReader.class);
  private static final int MAX_IDLE_PULLS = 300;

  private final IEventCollector collector;
  private int idlePullsBeforeNextAttempt = 0;
  private int currentIdlePulls = 0;

  /**
   *  Failure and recovery strategy:
   * - If a read fails, the number of idle pulls before the next attempt is doubled, up to a maximum of 300.
   * - If the read is successful, the idle pull counter is reset.
   */
  public ContinuousPlcRequestReader(
      PlcConnectionManager connectionManager,
      Plc4xConnectionSettings settings,
      PlcRequestProvider requestProvider,
      IEventCollector collector
  ) {
    super(connectionManager, settings, requestProvider);
    this.collector = collector;
  }

  @Override
  public void pullData() throws RuntimeException {
    if (currentIdlePulls < idlePullsBeforeNextAttempt) {
      idleRead();
    } else {
      connectAndReadPlcData();
    }
  }

  private void connectAndReadPlcData() {
    try (PlcConnection plcConnection = connectionManager.getConnection(settings.connectionString())) {
      if (plcConnection.isConnected()) {
        var readRequest = requestProvider.makeReadRequest(plcConnection, settings.nodes());
        var readResponse = readRequest.execute()
            .get(5000, TimeUnit.MILLISECONDS);
        processPlcReadResponse(readResponse);
      } else {
        handleFailingPlcRead("Not connected");
      }
    } catch (Exception e) {
      handleFailingPlcRead(e.getMessage());
    }
  }

  private void handleFailingPlcRead(String problem) {
    // ensure that the cached connection manager removes the broken connection
    if (connectionManager instanceof CachedPlcConnectionManager) {
      ((CachedPlcConnectionManager) connectionManager).removeCachedConnection(settings.connectionString());
    }

    // Increase backoff counter on failure
    if (idlePullsBeforeNextAttempt == 0) {
      idlePullsBeforeNextAttempt = 1;
    } else {
      idlePullsBeforeNextAttempt = Math.min(idlePullsBeforeNextAttempt * 2, MAX_IDLE_PULLS);
    }

    LOG.error(
        "Error while reading from PLC with connection string {}. Setting adapter to idle for {} attempts. {} ",
        settings.connectionString(), idlePullsBeforeNextAttempt, problem
    );

    currentIdlePulls = 0;
  }

  private void processPlcReadResponse(PlcReadResponse readResponse) {
    var event = eventGenerator.makeEvent(readResponse);
    collector.collect(event);
    this.resetIdlePulls();
  }

  private void idleRead() {
    LOG.debug("Skipping pullData call for {}. Idle pulls left: {}",
              settings.connectionString(), idlePullsBeforeNextAttempt - currentIdlePulls);
    currentIdlePulls++;
  }

  private void resetIdlePulls() {
    idlePullsBeforeNextAttempt = 0;
    currentIdlePulls = 0;
  }

  @Override
  public IPollingSettings getPollingInterval() {
    return PollingSettings.from(TimeUnit.MILLISECONDS, settings.pollingInterval());
  }
}
