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
import org.apache.streampipes.extensions.management.connect.adapter.model.specific.SpecificDataSetAdapter;
import org.apache.streampipes.extensions.management.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterSetDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataSetAdapterBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.Tuple2;
import org.apache.streampipes.sdk.utils.Assets;

import java.util.List;
import java.util.Map;

public class InfluxDbSetAdapter extends SpecificDataSetAdapter {

  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.influxdb.set";
  public static final int BATCH_SIZE = 8192;

  private InfluxDbClient influxDbClient;
  private Thread fetchDataThread;

  public static class FetchDataThread implements Runnable {

    InfluxDbSetAdapter influxDbSetAdapter;
    InfluxDbClient influxDbClient;

    public FetchDataThread(InfluxDbSetAdapter influxDbSetAdapter) throws AdapterException {
      this.influxDbSetAdapter = influxDbSetAdapter;
      this.influxDbClient = influxDbSetAdapter.getInfluxDbClient();

      influxDbClient.connect();
      influxDbClient.loadColumns();
    }

    @Override
    public void run() {
      if (!influxDbClient.isConnected()) {
        System.out.println("Cannot start PollingThread, when the client is not connected");
        return;
      }

      String oldestTimestamp = "0";
      while (!Thread.interrupted()) {
        // Get the next n elements, where the time is > than the last timestamp and send them (if there are some)
        List<List<Object>> queryResult = influxDbClient.
            query("SELECT " + influxDbClient.getColumnsString() + " FROM " + influxDbClient.getMeasurement()
                + " WHERE time > " + oldestTimestamp + " ORDER BY time ASC LIMIT " + BATCH_SIZE);

        for (List<Object> event : queryResult) {
          try {
            influxDbSetAdapter.send(influxDbClient.extractEvent(event));
          } catch (SpRuntimeException e) {
            System.out.println(e.getMessage());
          }
        }
        if (queryResult.size() < BATCH_SIZE) {
          // The last events or no event at all => Stop
          break;
        } else {
          // Get the new timestamp for the new round
          oldestTimestamp = InfluxDbClient.getTimestamp((String) queryResult.get(queryResult.size() - 1).get(0));
        }
      }
      influxDbClient.disconnect();
    }
  }

  public InfluxDbSetAdapter() {
  }

  public InfluxDbSetAdapter(SpecificAdapterSetDescription specificAdapterSetDescription) {
    super(specificAdapterSetDescription);

    getConfigurations(specificAdapterSetDescription);
  }

  @Override
  public SpecificAdapterSetDescription declareModel() {
    SpecificAdapterSetDescription description = SpecificDataSetAdapterBuilder.create(ID)
        .withAssets(Assets.ICON, Assets.DOCUMENTATION)
        .withLocales(Locales.EN)
        .requiredTextParameter(Labels.withId(InfluxDbClient.HOST))
        .requiredIntegerParameter(Labels.withId(InfluxDbClient.PORT))
        .requiredTextParameter(Labels.withId(InfluxDbClient.DATABASE))
        .requiredTextParameter(Labels.withId(InfluxDbClient.MEASUREMENT))
        .requiredTextParameter(Labels.withId(InfluxDbClient.USERNAME))
        .requiredSecret(Labels.withId(InfluxDbClient.PASSWORD))
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
    fetchDataThread = new Thread(new FetchDataThread(this));
    fetchDataThread.start();
  }

  @Override
  public void stopAdapter() throws AdapterException {
    fetchDataThread.interrupt();
    try {
      fetchDataThread.join();
    } catch (InterruptedException e) {
      throw new AdapterException("Unexpected Error while joining polling thread: " + e.getMessage());
    }
  }

  @Override
  public Adapter getInstance(SpecificAdapterSetDescription adapterDescription) {
    return new InfluxDbSetAdapter(adapterDescription);
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterSetDescription adapterDescription)
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

  private void getConfigurations(SpecificAdapterSetDescription adapterDescription) {
    ParameterExtractor extractor = new ParameterExtractor(adapterDescription.getConfig());

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

  public InfluxDbClient getInfluxDbClient() {
    return influxDbClient;
  }
}
