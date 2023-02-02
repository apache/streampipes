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

package org.apache.streampipes.extensions.connectors.influx.adapter;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.connectors.influx.shared.InfluxConfigs;
import org.apache.streampipes.extensions.connectors.influx.shared.InfluxKeys;
import org.apache.streampipes.extensions.management.connect.adapter.Adapter;
import org.apache.streampipes.extensions.management.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.Tuple2;
import org.apache.streampipes.sdk.utils.Assets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class InfluxDbStreamAdapter extends SpecificDataStreamAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(InfluxDbStreamAdapter.class);
  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.influxdb.stream";

  private static final String POLLING_INTERVAL = "pollingInterval";

  private InfluxDbClient influxDbClient;

  private Thread pollingThread;
  private int pollingInterval;

  public static class PollingThread implements Runnable {
    private int pollingInterval;

    private InfluxDbClient influxDbClient;
    private InfluxDbStreamAdapter influxDbStreamAdapter;

    PollingThread(InfluxDbStreamAdapter influxDbStreamAdapter, int pollingInterval) throws AdapterException {
      this.pollingInterval = pollingInterval;
      this.influxDbStreamAdapter = influxDbStreamAdapter;
      this.influxDbClient = influxDbStreamAdapter.getInfluxDbClient();

      influxDbClient.connect();
      influxDbClient.loadColumns();
    }

    @Override
    public void run() {
      if (!influxDbClient.isConnected()) {
        LOG.warn("Cannot start PollingThread, when the client is not connected");
        return;
      }
      // Checking the most recent timestamp
      // Timestamp is a string, because a long might not be big enough (it includes nano seconds)
      String lastTimestamp;
      try {
        lastTimestamp = getNewestTimestamp();
      } catch (SpRuntimeException e) {
        LOG.error(e.getMessage());
        return;
      }

      while (!Thread.interrupted()) {
        try {
          Thread.sleep(pollingInterval);
        } catch (InterruptedException e) {
          break;
        }
        List<List<Object>> queryResult = influxDbClient.query("SELECT " + influxDbClient.getColumnsString()
            + " FROM " + influxDbClient.getMeasurement() + " WHERE time > " + lastTimestamp + " ORDER BY time ASC ");
        if (queryResult.size() > 0) {
          // The last element has the highest timestamp (ordered asc) -> Set the new latest timestamp
          lastTimestamp = InfluxDbClient.getTimestamp((String) queryResult.get(queryResult.size() - 1).get(0));

          for (List<Object> value : queryResult) {
            try {
              Map<String, Object> out = influxDbClient.extractEvent(value);
              if (out != null) {
                influxDbStreamAdapter.send(out);
              }
            } catch (SpRuntimeException e) {
              LOG.error("Error: " + e.getMessage());
            }
          }
        }
      }
      influxDbClient.disconnect();
    }

    // Returns the newest timestamp in the measurement as unix timestamp in Nanoseconds.
    // If no entry is found, a SpRuntimeException is thrown
    String getNewestTimestamp() throws SpRuntimeException {
      List<List<Object>> queryResult = influxDbClient.query("SELECT * FROM " + influxDbClient.getMeasurement()
          + " ORDER BY time DESC LIMIT 1");
      if (queryResult.size() > 0) {
        return InfluxDbClient.getTimestamp((String) queryResult.get(0).get(0));
      } else {
        throw new SpRuntimeException("No entry found in query");
      }
    }
  }

  private InfluxDbClient getInfluxDbClient() {
    return influxDbClient;
  }

  public InfluxDbStreamAdapter() {
  }

  public InfluxDbStreamAdapter(SpecificAdapterStreamDescription specificAdapterStreamDescription) {
    super(specificAdapterStreamDescription);

    getConfigurations(specificAdapterStreamDescription);
  }

  @Override
  public SpecificAdapterStreamDescription declareModel() {
    var builder = SpecificDataStreamAdapterBuilder.create(ID)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN);

    InfluxConfigs.appendSharedInfluxConfig(builder);

    builder.requiredIntegerParameter(Labels.withId(POLLING_INTERVAL));
    builder.requiredSingleValueSelection(Labels.withId(InfluxDbClient.REPLACE_NULL_VALUES),
        Options.from(
            new Tuple2<>("Yes", InfluxDbClient.DO_REPLACE),
            new Tuple2<>("No", InfluxDbClient.DO_NOT_REPLACE)));

    return builder.build();
  }

  @Override
  public void startAdapter() throws AdapterException {
    pollingThread = new Thread(new PollingThread(this, pollingInterval));
    pollingThread.start();
  }

  @Override
  public void stopAdapter() throws AdapterException {
    // Signaling the thread to stop and then disconnect from the server
    pollingThread.interrupt();
    try {
      pollingThread.join();
    } catch (InterruptedException e) {
      throw new AdapterException("Unexpected Error while joining polling thread: " + e.getMessage());
    }
  }

  @Override
  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
    return new InfluxDbStreamAdapter(adapterDescription);
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription)
      throws AdapterException, ParseException {
    getConfigurations(adapterDescription);
    return influxDbClient.getSchema();
  }

  @Override
  public String getId() {
    return ID;
  }

  private void send(Map<String, Object> map) {
    adapterPipeline.process(map);
  }

  private void getConfigurations(SpecificAdapterStreamDescription adapterDescription) {
    var extractor = StaticPropertyExtractor.from(adapterDescription.getConfig());

    pollingInterval = extractor.singleValueParameter(POLLING_INTERVAL, Integer.class);
    String replace = extractor.selectedSingleValueInternalName(InfluxDbClient.REPLACE_NULL_VALUES, String.class);

    influxDbClient = new InfluxDbClient(
        InfluxConfigs.fromExtractor(extractor),
        extractor.singleValueParameter(InfluxKeys.DATABASE_MEASUREMENT_KEY, String.class),
        replace.equals(InfluxDbClient.DO_REPLACE));

  }
}
