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
import org.apache.streampipes.extensions.connectors.plc.adapter.s7.PlcReadResponseHandler;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ContinuousPlcRequestReader
    extends OneTimePlcRequestReader implements IPullAdapter, PlcReadResponseHandler {

  private static final Logger LOG = LoggerFactory.getLogger(ContinuousPlcRequestReader.class);

  private final IEventCollector collector;
  private PlcConnection plcConnection;

  public ContinuousPlcRequestReader(PlcConnectionManager connectionManager,
                                    Plc4xConnectionSettings settings,
                                    PlcRequestProvider requestProvider,
                                    IEventCollector collector) {
    super(connectionManager, settings, requestProvider);
    this.collector = collector;
  }

  @Override
  public void pullData() throws RuntimeException {
    try {
      var connection = getConnection();
      readPlcData(connection, this);
    } catch (Exception e) {
      LOG.error("Error while reading from PLC with connection string {} ", settings.connectionString(), e);
    }
  }

  private PlcConnection getConnection() throws PlcConnectionException {
    if (plcConnection == null || !plcConnection.isConnected()) {
      this.plcConnection = connectionManager.getConnection(settings.connectionString());
    }
    return this.plcConnection;
  }

  private void readPlcData(PlcConnection plcConnection, PlcReadResponseHandler handler) {
    var readRequest = requestProvider.makeReadRequest(plcConnection, settings.nodes());
    CompletableFuture<? extends PlcReadResponse> asyncResponse = readRequest.execute();
    asyncResponse.whenComplete(handler::onReadResult);
  }

  @Override
  public IPollingSettings getPollingInterval() {
    return PollingSettings.from(TimeUnit.MILLISECONDS, settings.pollingInterval());
  }

  @Override
  public void onReadResult(PlcReadResponse response, Throwable throwable) {
    if (throwable != null) {
      LOG.error(throwable.getMessage());
    } else {
      var event = eventGenerator.makeEvent(response);
      collector.collect(event);
    }
  }

  public void closeConnection() throws Exception {
    if (this.plcConnection != null && this.plcConnection.isConnected()) {
      this.plcConnection.close();
    }
  }
}
