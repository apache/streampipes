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

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xConnectionSettings;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.utils.cache.CachedPlcConnectionManager;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OneTimePlcRequestReader {

  protected final Plc4xConnectionSettings settings;
  protected final PlcRequestProvider requestProvider;
  protected final PlcEventGenerator eventGenerator;

  protected final PlcConnectionManager connectionManager;

  public OneTimePlcRequestReader(PlcConnectionManager connectionManager,
                                 Plc4xConnectionSettings settings,
                                 PlcRequestProvider requestProvider) {
    this.connectionManager = connectionManager;
    this.settings = settings;
    this.requestProvider = requestProvider;
    this.eventGenerator = new PlcEventGenerator(settings.nodes());
  }

  public Map<String, Object> readPlcDataSynchronized() throws Exception {
    var connectionString = settings.connectionString();
    try (PlcConnection plcConnection = connectionManager.getConnection(connectionString)) {
      if (!plcConnection.getMetadata().isReadSupported()) {
        throw new AdapterException("This PLC does not support reading data");
      }
      var readRequest = requestProvider.makeReadRequest(plcConnection, settings.nodes());
      var readResponse = readRequest.execute().get(5000, TimeUnit.MILLISECONDS);
      return eventGenerator.makeEvent(readResponse);
    } finally {
      if (connectionManager instanceof CachedPlcConnectionManager) {
        ((CachedPlcConnectionManager) connectionManager).removeCachedConnection(settings.connectionString());
      }
    }
  }
}
