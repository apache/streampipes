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

package org.apache.streampipes.connect.iiot.adapters.influxdb;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.adapter.Adapter;
import org.apache.streampipes.extensions.management.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.apache.streampipes.extensions.management.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.Tuple2;
import org.apache.streampipes.sdk.utils.Assets;

import java.util.List;
import java.util.Map;

public class InfluxDbStreamAdapter extends SpecificDataStreamAdapter {

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
        System.out.println("Cannot start PollingThread, when the client is not connected");
        return;
      }
      // Checking the most recent timestamp
      // Timestamp is a string, because a long might not be big enough (it includes nano seconds)
      String lastTimestamp;
      try {
        lastTimestamp = getNewestTimestamp();
      } catch (SpRuntimeException e) {
        System.out.println(e.getMessage());
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
              System.out.println("Error: " + e.getMessage());
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
    SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredTextParameter(Labels.withId(InfluxDbClient.HOST))
        .requiredIntegerParameter(Labels.withId(InfluxDbClient.PORT))
        .requiredTextParameter(Labels.withId(InfluxDbClient.DATABASE))
        .requiredTextParameter(Labels.withId(InfluxDbClient.MEASUREMENT))
        .requiredTextParameter(Labels.withId(InfluxDbClient.USERNAME))
        .requiredSecret(Labels.withId(InfluxDbClient.PASSWORD))
        .requiredIntegerParameter(Labels.withId(POLLING_INTERVAL))
        .requiredSingleValueSelection(Labels.withId(InfluxDbClient.REPLACE_NULL_VALUES),
            Options.from(
                new Tuple2<>("Yes", InfluxDbClient.DO_REPLACE),
                new Tuple2<>("No", InfluxDbClient.DO_NOT_REPLACE)))
        .build();

    description.setAppId(ID);
    return description;
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
    ParameterExtractor extractor = new ParameterExtractor(adapterDescription.getConfig());

    pollingInterval = extractor.singleValue(POLLING_INTERVAL, Integer.class);
    String replace = extractor.selectedSingleValueInternalName(InfluxDbClient.REPLACE_NULL_VALUES);

    influxDbClient = new InfluxDbClient(
        extractor.singleValue(InfluxDbClient.HOST, String.class),
        extractor.singleValue(InfluxDbClient.PORT, Integer.class),
        extractor.singleValue(InfluxDbClient.DATABASE, String.class),
        extractor.singleValue(InfluxDbClient.MEASUREMENT, String.class),
        extractor.singleValue(InfluxDbClient.USERNAME, String.class),
        extractor.secretValue(InfluxDbClient.PASSWORD),
        replace.equals(InfluxDbClient.DO_REPLACE));

  }
}
